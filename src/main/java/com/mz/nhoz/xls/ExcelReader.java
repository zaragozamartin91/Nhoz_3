package com.mz.nhoz.xls;

import java.io.File;
import java.io.FileInputStream;
import java.util.Iterator;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.mz.nhoz.xls.exception.ExcelReaderException;

/**
 * Lector de hojas de Archivos Excel (xls o xlsx).
 * 
 * @author martin.zaragoza
 *
 */
public class ExcelReader implements Iterable<ExcelRecord> {
	private Workbook workbook;
	private Sheet sheet;

	/**
	 * Abre un documento de excel a partir de un archivo en la hoja 0.
	 * 
	 * @param excFile
	 *            - archivo xls o xlsX.
	 * @throws ExcelReaderException
	 */
	public ExcelReader(File excFile) throws ExcelReaderException {
		reset(excFile, 0);
	}

	/**
	 * Abre un documento de excel a partir de un archivo y un indice de hoja.
	 * 
	 * @param excFile
	 *            - archivo xls o xlsX.
	 * @param sheetIndex
	 *            - indice de hoja.
	 * @throws ExcelReaderException
	 */
	public ExcelReader(File excFile, int sheetIndex) throws ExcelReaderException {
		reset(excFile, sheetIndex);
	}

	@Override
	public Iterator<ExcelRecord> iterator() {
		return new Iterator<ExcelRecord>() {
			Iterator<Row> rowIterator = sheet.rowIterator();

			public boolean hasNext() {
				return rowIterator.hasNext();
			}

			public ExcelRecord next() {
				Row nextRow = rowIterator.next();
				return new ExcelRecord(nextRow);
			}

			public void remove() {
				throw new UnsupportedOperationException("Operación REMOVER no soportada!");
			}
		};
	}

	private void reset(File excFile, int sheetIndex) throws ExcelReaderException {
		try {
			ExcelDocType excelDocType = new ExcelDocType(excFile);
			FileInputStream excelFileInputStream = new FileInputStream(excFile);

			if (excelDocType.isXls()) {
				workbook = new HSSFWorkbook(excelFileInputStream);
			} else if (excelDocType.isXlsx()) {
				workbook = new XSSFWorkbook(excelFileInputStream);
			} else {
				excelFileInputStream.close();
				throw new ExcelReaderException("Tipo de archivo de entrada no es Xls ni Xlsx!");
			}

			try {
				sheet = workbook.getSheetAt(sheetIndex);
			} catch (Exception e) {
				throw new ExcelReaderException("Error al obtener la hoja de indice " + sheetIndex, e);
			}
		} catch (Exception e) {
			throw new ExcelReaderException(e);
		}
	}
}// ExcelReader
