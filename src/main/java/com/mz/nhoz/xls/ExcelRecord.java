package com.mz.nhoz.xls;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import com.mz.nhoz.xls.util.CellParser;
import com.mz.nhoz.xls.util.exception.CellParserException;

/**
 * Registro/fila de tabla excel.
 * 
 * @author martin.zaragoza
 *
 */
public class ExcelRecord {
	private static final CellParser CELL_PARSER = new CellParser();
	private Row row;

	public ExcelRecord(Row row) {
		this.row = row;
	}

	/**
	 * Obtiene la cantidad de columnas del registro/fila.
	 * 
	 * @return cantidad de columnas del registro/fila.
	 */
	public int getColumnCount() {
		return row.getLastCellNum();
	}

	/**
	 * Obtiene un valor de una celda.
	 * 
	 * @param columnIndex
	 *            Indice de columna de celda (inicia en el 0).
	 * @return Valor de celda. Null en caso que la celda no exista o no tenga valor.
	 * @throws CellParserException
	 *             En caso que el contenido de la celda no sea parseable.
	 */
	public Object getCellValue(int columnIndex) throws CellParserException {
		Cell cell = row.getCell(columnIndex, Row.RETURN_BLANK_AS_NULL);
		return (cell == null) ? null : CELL_PARSER.parseValue(cell);
	}
}
