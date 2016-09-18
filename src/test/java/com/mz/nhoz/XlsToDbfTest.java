package com.mz.nhoz;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;

import org.junit.Test;

import com.mz.nhoz.dbf.DbfAction;
import com.mz.nhoz.dbf.DbfReader;
import com.mz.nhoz.dbf.DbfRecord;
import com.mz.nhoz.dbf.DbfWriter;
import com.mz.nhoz.dbf.exception.DbfReaderException;
import com.mz.nhoz.dbf.exception.DbfWriterException;
import com.mz.nhoz.util.DecimalSymbol;
import com.mz.nhoz.util.MoneyUtils;
import com.mz.nhoz.util.exception.MoneyUtilsException;
import com.mz.nhoz.xls.ExcelReader;
import com.mz.nhoz.xls.ExcelRecord;
import com.mz.nhoz.xls.exception.ExcelReaderException;
import com.mz.nhoz.xls.util.ArticleFinder;
import com.mz.nhoz.xls.util.exception.ArticleFinderException;
import com.mz.nhoz.xls.util.exception.CellParserException;

public class XlsToDbfTest {

	@Test
	public void testAddRecord() throws ExcelReaderException, DbfReaderException, IOException, DbfWriterException {
		final String providerId = "14";
		final String priceSymbol = "$";
		final DecimalSymbol decimalSymbol = DecimalSymbol.DOT;

		final String xlsFilePath = "testFiles/prov14.xlsx";
		final String orgDbfFilePath = "testFiles/LISTAPRE.DBF";
		final String destDbfFilePath = "testFiles/LISTAPRE_COPY.DBF";

		newDbfFromXls(providerId, priceSymbol, decimalSymbol, xlsFilePath, orgDbfFilePath, destDbfFilePath);

		assertTrue(true);

	}

	private void newDbfFromXls(final String providerId, final String priceSymbol, final DecimalSymbol decimalSymbol, final String xlsFilePath,
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

				System.out.println("Leyendo articulo: " + absoluteArticleId);

				if (providerId.equals(dbfRecordProviderId)) {
					try {
						ExcelRecord excelArticleRecord = articleFinder.find(dbfRecordArticleId.toString());
						if (excelArticleRecord.hasData()) {
							String rawPriceValue = excelArticleRecord.getCellValue(1).toString();
							Double priceValue = MoneyUtils.parsePriceAsDouble(MoneyUtils.removePriceSymbol(rawPriceValue, priceSymbol),
									decimalSymbol);
							dbfRecord.setValue("PRECIOUNI", priceValue);
						}

						System.out.println("Articulo " + absoluteArticleId + " modificado");
					} catch (ArticleFinderException e) {
						System.err.println("Error al intentar buscar el articulo " + dbfRecordArticleId + " en archivo excel");
					} catch (CellParserException e) {
						System.err.println("Error al parsear el contenido del articulo " + dbfRecordArticleId + " en archivo excel");
					} catch (MoneyUtilsException e) {
						System.err.println("Error al Interpretar el contenido del articulo " + dbfRecordArticleId + " en archivo excel");
					}
				}

				try {
					System.out.println("Guardando: " + Arrays.asList(dbfRecord.getValues()));
					dbfWriter.addRecord(dbfRecord);
				} catch (DbfWriterException e) {
					System.err.println("Error al agregar el registro " + absoluteArticleId + " al dbf destino");
				}
			}
		});

		System.out.println("Escribiendo archivo dbf...");
		dbfWriter.close();
		System.out.println("Fin de escritura de archivo dbf");
	}

}
