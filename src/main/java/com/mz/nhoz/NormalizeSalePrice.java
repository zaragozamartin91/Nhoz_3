package com.mz.nhoz;

import com.mz.nhoz.config.Configuration;
import com.mz.nhoz.config.exception.GuiConfigurationAbortException;
import com.mz.nhoz.config.impl.GuiConfiguration;
import com.mz.nhoz.dbf.RecordUtils;
import com.mz.nhoz.dbf.exception.RecordUtilsException;
import com.mz.nhoz.util.DateUtils;
import com.mz.nhoz.util.NumberUtils;
import com.mz.nhoz.xls.ExcelReader;
import com.mz.nhoz.xls.ExcelRecord;
import com.mz.nhoz.xls.exception.ExcelReaderException;
import com.mz.nhoz.xls.util.ArticleFinder;
import com.mz.nhoz.xls.util.exception.ArticleFinderException;
import nl.knaw.dans.common.dbflib.IfNonExistent;
import nl.knaw.dans.common.dbflib.Record;
import nl.knaw.dans.common.dbflib.Table;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Iterator;
import java.util.regex.Pattern;

public class NormalizeSalePrice {
	private static final String SALE_VALUE_COLUMN = "RAZON";

	static Logger logger = Logger.getLogger(NormalizeSalePrice.class);
	static Pattern pathNoExtensionPattern = Pattern.compile(Pattern.quote(".") + "DBF", Pattern.CASE_INSENSITIVE);

	private final Configuration configuration;
    private String orgDbfFilePath;
	private File dbfFile;
	private File timestampDbfFile;
	private ArticleFinder articleFinder;
	private Table writeTable;
	private Table readTable;

    public NormalizeSalePrice() {
        HashSet<String> configToggles = new HashSet<>();
        configToggles.add("dbf");
        configToggles.add("xls");
        configuration = new GuiConfiguration(configToggles);
    }

    void run() {
		try {
			configuration.load();
		} catch (GuiConfigurationAbortException e) {
            logger.error(e.getMessage());
            return;
        } catch (Exception e) {
			logger.error("Error al realizar configuraciones", e);
			return;
		}

        String xlsFilePath = configuration.getXlsFilePath();
		orgDbfFilePath = configuration.getDbfFilePath();

		dbfFile = new File(orgDbfFilePath);
		timestampDbfFile = timestampFile();

        ExcelReader excelReader;
		try {
            excelReader = new ExcelReader(new File(xlsFilePath));
            articleFinder = new ArticleFinder(excelReader);
		} catch (ExcelReaderException e1) {
			logger.error("Error al abrir el archivo excel " + configuration.getXlsFilePath(), e1);
            return;
		}

        try {
			try {
				Files.copy(dbfFile.toPath(), timestampDbfFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                logger.info("Archivo de backup " + timestampDbfFile.toPath() + " creado");
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
                transformRecord(record);

				writeTable.addRecord(record);
			}

			writeTable.close();
			readTable.close();
		} catch (Exception e) {
			rollback();
		}
	}

	private void transformRecord(Record record) {
        String recordProviderId = RecordUtils.providerId(record);
        String recordArticleId = RecordUtils.articleId(record);
        Number originalPriceValue = RecordUtils.priceValue(record);
        Number originalSaleValue = RecordUtils.saleValue(record);
        Number utilityValue = RecordUtils.utility(record);

        String absArticleId = recordProviderId + "::" + recordArticleId;

		try {
			ExcelRecord excelRecord = articleFinder.findByProviderAndArticle(recordProviderId, recordArticleId);
			/* If there's no matching excel record, then avoid transforming the record */
            if (excelRecord.isNull()) {
				return;
			}

            /* If the utility value is null , zero or negative then avoid transforming the record */
            if (utilityValue == null || utilityValue.doubleValue() <= 0d) {
                return;
            }

            /* If the new sale value is lower than the current one, then avoid transforming the record */
            double newSaleValue = NumberUtils.augment(originalPriceValue.doubleValue(), utilityValue.doubleValue());
            if (originalSaleValue.doubleValue() > newSaleValue) {
                logger.info("ARTICULO " + absArticleId + " IGNORADO ; PRECIO ACTUAL DE VENTA SUPERIOR A PRECIO CON UTILIDAD");
                return;
            }

			RecordUtils.setValue(record, SALE_VALUE_COLUMN, newSaleValue);
			logger.info("ARTICULO " + absArticleId + " MODIFICADO ; " + "Razon " + originalSaleValue + " => " + newSaleValue);
		} catch (ArticleFinderException e) {
			logger.error("Error al buscar el articulo " + absArticleId + " en archivo excel", e);
		} catch (RecordUtilsException e) {
			logger.error("Error al modificar campo " + SALE_VALUE_COLUMN + " de articulo " + absArticleId + " en archivo excel", e);
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
			} catch (Exception ignored) {
			}

			Files.move(timestampDbfFile.toPath(), dbfFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			logger.error("ERROR AL REALIZAR ROLLBACK", e);
		}
	}

	private String timestampFileName() {
		String[] split = pathNoExtensionPattern.split(orgDbfFilePath);
        String dateTimeKey = DateUtils.dateTimeKey(LocalDateTime.now());
        return split[0] + "-" + dateTimeKey + ".DBF";
	}

	private File timestampFile() {
		return new File(timestampFileName());
	}

	public static void main(String[] args) {
		new NormalizeSalePrice().run();
		logger.info("FIN");
	}
}
