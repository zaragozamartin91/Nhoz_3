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
	static Logger logger = Logger.getLogger(MainApp.class);
	static Pattern pathNoExtensionPattern = Pattern.compile(Pattern.quote(".") + "DBF", Pattern.CASE_INSENSITIVE);

	Configuration configuration = new GuiConfiguration();

	void run() throws ConfigurationException, FileNotFoundException, ExcelReaderException, DbfReaderException, IOException, DbfWriterException {
		configuration.load();

		String providerId = configuration.getProviderId();
		String priceSymbol = "$";
		DecimalSymbol decimalSymbol = configuration.getDecimalSymbol();
		String xlsFilePath = configuration.getXlsFilePath();

		final String orgDbfFilePath = configuration.getDbfFilePath();
		String destDbfFilePath = tempFileName(orgDbfFilePath);

		writeDbfFromXls(providerId, priceSymbol, decimalSymbol, xlsFilePath, orgDbfFilePath, destDbfFilePath);

		// Thread swapFilesThread = new Thread(new Runnable() {
		// public void run() {
		// try {
		// Thread.sleep(2000);
		swapFiles(orgDbfFilePath);
		// } catch (InterruptedException e) {
		// }
		// }
		// });
		// swapFilesThread.setDaemon(false);
		// swapFilesThread.start();
	}

	private String tempFileName(String orgDbfFilePath) {
		String timestampFileName = timestampFileName(orgDbfFilePath);
		String[] split = pathNoExtensionPattern.split(timestampFileName);

		return split[0] + "TEMP" + ".DBF";
	}

	private String timestampFileName(String orgDbfFilePath) {
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
	private void writeDbfFromXls(final String providerId, final String priceSymbol, final DecimalSymbol decimalSymbol, final String xlsFilePath,
			final String orgDbfFilePath, final String destDbfFilePath)
			throws ExcelReaderException, FileNotFoundException, DbfReaderException, IOException, DbfWriterException {

		final ExcelReader excelReader = new ExcelReader(new File(xlsFilePath));
		final DbfReader dbfReader = new DbfReader(new File(orgDbfFilePath));
		final ArticleFinder articleFinder = new ArticleFinder(excelReader);
		final DbfWriter dbfWriter = new DbfWriter(new File(destDbfFilePath), true);

		dbfReader.forEach(new DbfAction() {
			public void run(DbfRecord dbfRecord) {
				Object dbfRecordProviderId = dbfRecord.getValue("CODIGOPROV");
				Object dbfRecordArticleId = dbfRecord.getValue("ARTICULO");
				String absoluteArticleId = dbfRecordProviderId + ":" + dbfRecordArticleId;

				logger.info("Leyendo articulo: " + absoluteArticleId);

				if (providerId.equals(dbfRecordProviderId)) {
					try {
						ExcelRecord excelArticleRecord = articleFinder.find(dbfRecordArticleId.toString());
						if (excelArticleRecord.hasData()) {
							Object rawPriceValue = excelArticleRecord.getCellValue(1);

							if (rawPriceValue != null) {
								String stringRawPriceValue = rawPriceValue.toString();
								String noSymbolStringPriceValue = removePriceSymbol(stringRawPriceValue, priceSymbol);
								Double priceValue = parsePriceAsDouble(noSymbolStringPriceValue, decimalSymbol);
								dbfRecord.setValue("PRECIOUNI", priceValue);
								logger.debug("Articulo " + absoluteArticleId + " modificado");
							}
						}

					} catch (ArticleFinderException e) {
						logger.debug("Error al intentar buscar el articulo " + dbfRecordArticleId + " en archivo excel");
					} catch (CellParserException e) {
						logger.debug("Error al parsear el contenido del articulo " + dbfRecordArticleId + " en archivo excel");
					} catch (MoneyUtilsException e) {
						logger.debug("Error al Interpretar el contenido del articulo " + dbfRecordArticleId + " en archivo excel");
					}
				}

				try {
					logger.info("Guardando: " + Arrays.asList(dbfRecord.getValues()));
					dbfWriter.addRecord(dbfRecord);
				} catch (DbfWriterException e) {
					logger.error("Error al agregar el registro " + absoluteArticleId + " al dbf destino");
				}
			}
		});

		logger.info("Escribiendo archivo dbf...");
		dbfWriter.close();
		logger.info("Fin de escritura de archivo dbf");

		// try {
		// dbfReader.close();
		// } catch (Exception e) {
		// }
		//
		// try {
		// dbfWriter.close();
		// } catch (Exception e) {
		// }
	}

	/**
	 * Intercambia los nombres de los archivos Dbf original y el Nuevo escrito.
	 * 
	 * @param orgDbfFilePath
	 *            Path del archivo dbf original.
	 * @throws InterruptedException
	 */
	private void swapFiles(
			final String orgDbfFilePath) /* throws InterruptedException */ {
		logger.info("INTERCAMBIANDO ARCHIVOS...");

		trySwapFiles(orgDbfFilePath, 1);

		logger.info("FIN INTERCAMBIO DE ARCHIVOS...");
	}

	private void trySwapFiles(final String orgDbfFilePath, int tryCount) {
		if (tryCount > 5) {
			logger.error("LIMITE DE INTENTO DE RENOMBRAR ARCHIVOS SUPERADO.");
			return;
		}
		logger.info("INTENTO " + tryCount + " DE INTERCAMBIO DE ARCHIVOS...");
		int nextTry = tryCount + 1;

		File orgFile = new File(orgDbfFilePath);
		File timestampFile = new File(timestampFileName(orgDbfFilePath));
		File tempFile = new File(tempFileName(orgDbfFilePath));

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}

		boolean orgFileRenameSuccess = orgFile.renameTo(timestampFile);
		if (orgFileRenameSuccess) {
			logger.info("ARCHIVO " + orgDbfFilePath + " RENOMBRADO EXITOSAMENTE A " + timestampFileName(orgDbfFilePath));

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}

			boolean tempFileRenameSuccess = tempFile.renameTo(orgFile);
			if (tempFileRenameSuccess) {
				logger.info("ARCHIVO " + tempFile.getAbsolutePath() + " RENOMBRADO EXITOSAMENTE A " + orgDbfFilePath);
			} else {
				// SI FALLE EN RENOMBRAR EL ARCHIVO NUEVO DE LISTAPRE AL NOMBRE
				// ORIGINAL "LISTAPRE-DBF" ENTONCES HAGO UN ROLLBACK RENOMBRANDO
				// EL VIEJO LISTAPRE AL NOMBRE "LISTAPRE.DBF".
				logger.info("RESTAURANDO NOMBRE DE ARCHIVO VIEJO...");
				timestampFile.renameTo(orgFile);
				trySwapFiles(orgDbfFilePath, nextTry);
			}
		} else {
			trySwapFiles(orgDbfFilePath, nextTry);
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
