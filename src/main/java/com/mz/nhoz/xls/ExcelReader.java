package com.mz.nhoz.xls;

import java.io.File;
import java.io.FileInputStream;
import java.util.Iterator;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.mz.nhoz.xls.exception.ExcelReaderException;
import com.mz.nhoz.xls.util.CellParser;

public class ExcelReader {
	private Workbook workbook;
	private Sheet sheet;
	private TableHeader tableHeader;

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

	/**
	 * Obtiene el encabezado de la tabla.
	 * 
	 * @return encabezado de la tabla.
	 * @throws ExcelReaderException
	 */
	public TableHeader getTableHeader() throws ExcelReaderException {
		if (tableHeader == null) {
			buildTableHeader();
		}

		return tableHeader;
	}// getTableHeader

	/**
	 * Retorna un iterador de registros de filas.
	 * 
	 * @return iterador de registros de filas.
	 * @throws ExcelReaderException
	 */
	public RowRecordIterator rowRecordIterator() throws ExcelReaderException {
		try {
			Iterator<Row> rowIterator = sheet.rowIterator();
			rowIterator.next();
			return new RowRecordIterator(rowIterator, getTableHeader());
		} catch (Exception e) {
			throw new ExcelReaderException(e);
		}
	}// rowRecordIterator

	private void reset(File excFile, int sheetIndex) throws ExcelReaderException {
		try {
			ExcelDocType excelDocType = new ExcelDocType(excFile);
			FileInputStream fis = new FileInputStream(excFile);

			if (excelDocType.isXls()) {
				workbook = new HSSFWorkbook(fis);
			} else if (excelDocType.isXlsx()) {
				workbook = new XSSFWorkbook(fis);
			} else {
				fis.close();
				throw new ExcelReaderException("Tipo de archivo de entrada no es Xls ni Xlsx!");
			}

			sheet = workbook.getSheetAt(sheetIndex);
		} catch (Exception e) {
			throw new ExcelReaderException(e);
		}
	}// __reset

	private void buildTableHeader() throws ExcelReaderException {
		try {
			CellParser cellDeserializer = new CellParser();
			tableHeader = new TableHeader();

			Iterator<Row> rowIterator = sheet.iterator();
			Row firstRow = rowIterator.next();
			Iterator<Cell> cellIterator = firstRow.cellIterator();

			int index = 0;
			while (cellIterator.hasNext()) {
				Cell cell = (Cell) cellIterator.next();

				if (cellDeserializer.isBlank(cell) == false) {
					String label = (String) cellDeserializer.parseValue(cell);
					tableHeader.add(label.trim(), index);
				}

				index++;
			}
		} catch (Exception e) {
			throw new ExcelReaderException(e);
		}
	}// __buildTableHeader

	public String toString() {
		try {
			StringBuilder stringBuilder = new StringBuilder();
			CellParser cellDeserializer = new CellParser();

			Iterator<Row> __rowIterator = this.sheet.iterator();
			while (__rowIterator.hasNext()) {
				Row row = (Row) __rowIterator.next();

				Iterator<Cell> __cellIterator = row.cellIterator();
				while (__cellIterator.hasNext()) {
					Cell cell = (Cell) __cellIterator.next();
					if (cellDeserializer.isEmpty(cell)) {
						// System.out.println("Empty cell found!");
					} else {
						stringBuilder.append(cellDeserializer.parseValue(cell) + "\t");
					}
				}// while (__cellIterator.hasNext())

				stringBuilder.append("\n");
			}

			return stringBuilder.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}// ExcelReader
