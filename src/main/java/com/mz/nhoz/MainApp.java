package com.mz.nhoz;

import static com.mz.nhoz.util.MoneyUtils.parsePriceAsDouble;
import static com.mz.nhoz.util.MoneyUtils.removePriceSymbol;
import static java.util.Calendar.DATE;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.mz.nhoz.config.Configuration;
import com.mz.nhoz.config.exception.ConfigurationException;
import com.mz.nhoz.config.impl.GuiConfiguration;
import com.mz.nhoz.dbf.DbfAction;
import com.mz.nhoz.dbf.DbfReader;
import com.mz.nhoz.dbf.DbfRecord;
import com.mz.nhoz.dbf.DbfWriter;
import com.mz.nhoz.dbf.exception.DbfReaderException;
import com.mz.nhoz.dbf.exception.DbfWriterException;
import com.mz.nhoz.util.DecimalSymbol;
import com.mz.nhoz.util.exception.MoneyUtilsException;
import com.mz.nhoz.xls.ExcelReader;
import com.mz.nhoz.xls.ExcelRecord;
import com.mz.nhoz.xls.exception.ExcelReaderException;
import com.mz.nhoz.xls.util.ArticleFinder;
import com.mz.nhoz.xls.util.exception.ArticleFinderException;
import com.mz.nhoz.xls.util.exception.CellParserException;

public class MainApp {
	private static final String PRICE_COLUMN = "PRECIOUNI";
	private static final int MAX_SWAP_TRIES = 5;

	static Logger logger = Logger.getLogger(MainApp.class);
	static Pattern pathNoExtensionPattern = Pattern.compile(Pattern.quote(".") + "DBF", Pattern.CASE_INSENSITIVE);

	private Configuration configuration = new GuiConfiguration();

	private String providerId;
	private String priceSymbol;
	private DecimalSymbol decimalSymbol;
	private String xlsFilePath;

	private String orgDbfFilePath;
	private File orgDbfFile;
	private File dstDbfFile;
	private File timestampDbfFile;

	void run() throws ConfigurationException, FileNotFoundException, ExcelReaderException, DbfReaderException, IOException, DbfWriterException {
		configuration.load();

		providerId = configuration.getProviderId();
		priceSymbol = "$";
		decimalSymbol = configuration.getDecimalSymbol();
		xlsFilePath = configuration.getXlsFilePath();

		orgDbfFilePath = configuration.getDbfFilePath();

		orgDbfFile = new File(orgDbfFilePath);
		dstDbfFile = new File(orgDbfFilePath);
		timestampDbfFile = new File(timestampFileName());

		boolean swapFilesSuccess = swapFiles();
		if (swapFilesSuccess) {
			writeDbfFromXls();
		} else {
			logger.error("IMPOSIBLE INTERCAMBIAR ARCHIVOS. ABORTANDO PROGRAMA...");
		}
	}

	private String timestampFileName() {
		String[] split = pathNoExtensionPattern.split(orgDbfFilePath);

		GregorianCalendar today = new GregorianCalendar();
		int year = today.get(YEAR);
		int month = today.get(MONTH) + 1;
		int day = today.get(DATE);
		int hour = today.get(Calendar.HOUR_OF_DAY);
		int minute = today.get(Calendar.MINUTE);

		return split[0] + "-" + year + "-" + month + "-" + day + "-" + hour + "-" + minute + ".DBF";
	}

	/**
	 * Escribe un Dbf nuevo a partir de un Dbf original y una lista de precios
	 * de Excel.
	 */
	private void writeDbfFromXls() throws ExcelReaderException, FileNotFoundException, DbfReaderException, IOException, DbfWriterException {
		final ExcelReader excelReader = new ExcelReader(new File(xlsFilePath));
		final DbfReader dbfReader = new DbfReader(timestampDbfFile);
		final ArticleFinder articleFinder = new ArticleFinder(excelReader);
		final DbfWriter dbfWriter = new DbfWriter(dstDbfFile, true);

		dbfReader.forEach(new DbfAction() {
			public void run(DbfRecord dbfRecord) {
				Object dbfRecordProviderId = dbfRecord.getValue("CODIGOPROV");
				Object dbfRecordArticleId = dbfRecord.getValue("ARTICULO");
				String absoluteArticleId = dbfRecordProviderId + ":" + dbfRecordArticleId;

				logger.info("Leyendo articulo: " + absoluteArticleId);

				Object oldPriceValue = dbfRecord.getValue(PRICE_COLUMN);
				if (providerId.equals(dbfRecordProviderId)) {
					try {
						ExcelRecord excelArticleRecord = articleFinder.find(dbfRecordArticleId.toString());
						if (excelArticleRecord.hasData()) {
							Object rawPriceValue = excelArticleRecord.getCellValue(1);

							if (rawPriceValue != null) {
								String stringRawPriceValue = rawPriceValue.toString();
								String noSymbolStringPriceValue = removePriceSymbol(stringRawPriceValue, priceSymbol);
								Double priceValue = parsePriceAsDouble(noSymbolStringPriceValue, decimalSymbol);

								dbfRecord.setValue(PRICE_COLUMN, priceValue);

								logger.info("ARTICULO " + absoluteArticleId + " MODIFICADO");
							}
						}

					} catch (ArticleFinderException e) {
						logger.info("ERROR AL BUSCAR ARTICULO " + dbfRecordArticleId + " EN ARCHIVO EXCEL");
					} catch (CellParserException e) {
						logger.info("ERROR AL PARSEAR CONTENIDO DE ARTICULO " + dbfRecordArticleId + " EN ARCHIVO EXCEL");
					} catch (MoneyUtilsException e) {
						logger.info("ERROR AL INTERPRETAR EL PRECIO DEL ARTICULO " + dbfRecordArticleId + " EN ARCHIVO EXCEL");
					}
				}

				try {
					logger.info("GUARDANDO: " + Arrays.asList(dbfRecord.getValues()));
					dbfWriter.addRecord(dbfRecord);
				} catch (DbfWriterException e) {
					logger.error("ERROR AL AGREGAR REGISTRO " + absoluteArticleId + " EN DBF DESTINO");
					dbfRecord.setValue(PRICE_COLUMN, oldPriceValue);
					try {
						dbfWriter.addRecord(dbfRecord);
					} catch (DbfWriterException e1) {
						logger.error("ERROR AL RESTAURAR REGISTRO " + absoluteArticleId + " EN DFB");
					}
				}
			}
		});

		logger.info("Escribiendo archivo dbf...");
		dbfWriter.close();
		logger.info("Fin de escritura de archivo dbf");
	}

	/**
	 * Intercambia los nombres de los archivos Dbf original y el Nuevo escrito.
	 * 
	 * @param orgDbfFilePath
	 *            Path del archivo dbf original.
	 * @throws InterruptedException
	 */
	private boolean swapFiles() {
		logger.info("INTERCAMBIANDO ARCHIVOS...");

		return trySwapFiles(1);
	}

	private boolean trySwapFiles(int tryCount) {
		if (tryCount > MAX_SWAP_TRIES) {
			logger.error("IMPOSIBLE RENOMBRAR ARCHIVO " + orgDbfFilePath + " A " + timestampDbfFile.getAbsolutePath());
			return false;
		}
		logger.info("INTENTO " + tryCount + " DE INTERCAMBIO DE ARCHIVOS...");
		int nextTry = tryCount + 1;

		boolean orgDbfFileRenameSuccess = orgDbfFile.renameTo(timestampDbfFile);
		if (orgDbfFileRenameSuccess) {
			logger.info("ARCHIVO " + orgDbfFilePath + " RENOMBRADO A " + timestampDbfFile.getAbsolutePath() + "  EXITOSAMENTE!");
			return true;
		} else {
			return trySwapFiles(nextTry);
		}
	}

	public static void main(String[] args) {
		try {
			new MainApp().run();
		} catch (ConfigurationException e) {
			logger.error("OCURRIO ERROR DURANTE CONFIGURACION DE LA APLICACION", e);
		} catch (FileNotFoundException e) {
			logger.error("NO SE ENCONTRARON LOS ARCHIVOS", e);
		} catch (ExcelReaderException e) {
			logger.error("OCURRIO ERROR AL LEER EL ARCHIVO EXCEL", e);
		} catch (DbfReaderException e) {
			logger.error("OCURRIO ERROR AL LEER EL ARCHIVO DBF", e);
		} catch (IOException e) {
			logger.error("OCURRIO ERROR AL MANEJAR LOS ARCHIVOS", e);
		} catch (DbfWriterException e) {
			logger.error("ERROR AL ESCRIBIR EL ARCHIVO DBF", e);
		}
	}
}
