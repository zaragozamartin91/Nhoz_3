package com.mz.nhoz;

import static com.mz.nhoz.util.MoneyUtils.parsePriceAsDouble;
import static com.mz.nhoz.util.MoneyUtils.removePriceSymbol;
import static java.util.Calendar.DATE;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.regex.Pattern;

import nl.knaw.dans.common.dbflib.IfNonExistent;
import nl.knaw.dans.common.dbflib.Record;
import nl.knaw.dans.common.dbflib.Table;

import org.apache.log4j.Logger;

import com.mz.nhoz.config.Configuration;
import com.mz.nhoz.config.impl.GuiConfiguration;
import com.mz.nhoz.dbf.RecordUtils;
import com.mz.nhoz.dbf.exception.RecordUtilsException;
import com.mz.nhoz.util.DecimalSymbol;
import com.mz.nhoz.util.exception.MoneyUtilsException;
import com.mz.nhoz.xls.ExcelReader;
import com.mz.nhoz.xls.ExcelRecord;
import com.mz.nhoz.xls.exception.ExcelReaderException;
import com.mz.nhoz.xls.util.ArticleFinder;
import com.mz.nhoz.xls.util.exception.ArticleFinderException;
import com.mz.nhoz.xls.util.exception.CellParserException;

public class MainAppV2 {
	private static final String PRICE_COLUMN = "PRECIOUNI";

	static Logger logger = Logger.getLogger(MainAppV2.class);
	static Pattern pathNoExtensionPattern = Pattern.compile(Pattern.quote(".") + "DBF", Pattern.CASE_INSENSITIVE);

	private Configuration configuration = new GuiConfiguration();

	private String providerId;
	private String priceSymbol;
	private DecimalSymbol decimalSymbol;
	private String xlsFilePath;

	private String orgDbfFilePath;
	private File dbfFile;
	private File timestampDbfFile;

	private ArticleFinder articleFinder;

	private Table writeTable;

	private Table readTable;

	void run() {
		try {
			configuration.load();
		} catch (Exception e) {
			logger.error("Error al realizar configuraciones", e);
			return;
		}

		providerId = configuration.getProviderId();
		priceSymbol = "$";
		decimalSymbol = configuration.getDecimalSymbol();
		xlsFilePath = configuration.getXlsFilePath();

		orgDbfFilePath = configuration.getDbfFilePath();

		dbfFile = new File(orgDbfFilePath);
		timestampDbfFile = timestampFile();

		try {
			articleFinder = new ArticleFinder(new ExcelReader(new File(xlsFilePath)));
		} catch (ExcelReaderException e1) {
			logger.error("Error al abrir el archivo excel " + configuration.getXlsFilePath(), e1);
			return;
		}

		try {
			try {
				Files.copy(dbfFile.toPath(), timestampDbfFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				logger.error("Error al copiar el archivo dbf origen " + dbfFile.getAbsolutePath(), e);
				throw e;
			}

			writeTable = new Table(dbfFile);
			readTable = new Table(timestampDbfFile);

			openTables();

			deleteRecords();

			Iterator<Record> recordIterator = readTable.recordIterator();
			while (recordIterator.hasNext()) {
				Record record = (Record) recordIterator.next();
				String recordProvider = RecordUtils.providerId(record);

				if (providerId.equals(recordProvider)) {
					parseRecord(record);
				}

				writeTable.addRecord(record);
			}

			writeTable.close();
			readTable.close();
		} catch (Exception e) {
			rollback();
		}
	}

	private void parseRecord(Record record) {
		String articleId = RecordUtils.articleId(record);
		String absArticleId = providerId + "::" + articleId;

		try {
			ExcelRecord excelRecord = articleFinder.find(articleId);
			if (excelRecord.isNull()) {
				return;
			}

			Object rawPriceValue = excelRecord.getCellValue(1);

			if (rawPriceValue == null) {
				return;
			}

			String stringRawPriceValue = rawPriceValue.toString();
			String noSymbolStringPriceValue = removePriceSymbol(stringRawPriceValue, priceSymbol);
			Double priceValue = parsePriceAsDouble(noSymbolStringPriceValue, decimalSymbol);

			RecordUtils.setValue(record, PRICE_COLUMN, priceValue);

			logger.info("ARTICULO " + absArticleId + " MODIFICADO");

		} catch (ArticleFinderException e) {
			logger.error("Error al buscar el articulo " + absArticleId + " en archivo excel", e);
		} catch (CellParserException e) {
			logger.error("Error al parsear celda de precio de articulo " + absArticleId + " en archivo excel", e);
		} catch (MoneyUtilsException e) {
			logger.error("Error al interpretar precio de articulo " + absArticleId + " en archivo excel", e);
		} catch (RecordUtilsException e) {
			logger.error("Error al modificar campo " + PRICE_COLUMN + " de articulo " + absArticleId + " en archivo excel", e);
		}
	}

	private void openTables() throws Exception {
		try {
			writeTable.open(IfNonExistent.ERROR);
			readTable.open(IfNonExistent.ERROR);
		} catch (Exception e) {
			logger.error("Error al abrir la tabla dbf", e);
			throw e;
		}
	}

	private void deleteRecords() throws Exception {
		int recordCount = writeTable.getRecordCount();
		for (int i = 0; i < recordCount; i++) {
			try {
				writeTable.deleteRecordAt(i);
			} catch (IOException e) {
				logger.error("Error al eliminar registro " + i + " de archivo dbf", e);
			}
		}

		try {
			writeTable.pack();
		} catch (Exception e) {
			logger.error("Error al comprimir dbf", e);
			throw e;
		}
	}

	private void rollback() {
		try {
			logger.error("OCURRIO UN ERROR GRAVE... REALIZANDO ROLLBACK...");

			try {
				writeTable.close();
				readTable.close();
			} catch (Exception e) {
			}

			Files.move(timestampDbfFile.toPath(), dbfFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			logger.error("ERROR AL REALIZAR ROLLBACK", e);
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

	private File timestampFile() {
		return new File(timestampFileName());
	}

	public static void main(String[] args) {
		new MainAppV2().run();
		logger.info("FIN");
	}
}
