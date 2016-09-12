package com.mz.nhoz.xls.util;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;

import com.mz.nhoz.xls.util.exception.CellDeserializerException;

public class CellParser {
	private FormulaEvaluator formulaEvaluator;
	public static final String BLANK = "";

	public CellParser() {
		super();
	}

	public CellParser(FormulaEvaluator formulaEvaluator) {
		super();
		this.formulaEvaluator = formulaEvaluator;
	}

	/**
	 * Interpreta el valor de una celda retornando el valor de la misma.
	 * 
	 * @param cell
	 *            Celda a analizar.
	 * @return valor de celda.
	 * @throws CellDeserializerException
	 *             En caso que sea imposible obtener el valor de la celda o que
	 *             el tipo de valor no corresponda con ninguno de los
	 *             convencionales.
	 */
	public Object parseValue(Cell cell) throws CellDeserializerException {
		try {
			int cellType = cell.getCellType();
			String cellPos = cell.getRowIndex() + ":" + cell.getColumnIndex();

			switch (cellType) {
			case Cell.CELL_TYPE_NUMERIC:
				return cell.getNumericCellValue();
			case Cell.CELL_TYPE_STRING:
				return cell.getStringCellValue();
			case Cell.CELL_TYPE_BOOLEAN:
				return cell.getBooleanCellValue();
			case Cell.CELL_TYPE_BLANK:
				return BLANK;
			case Cell.CELL_TYPE_FORMULA:
				if (formulaEvaluator == null) {
					throw new CellDeserializerException("No se dispone de un evaluador de formulas para deseralizar la celda " + cellPos);
				}
				return formulaEvaluator.evaluate(cell).getNumberValue();
			}

			throw new CellDeserializerException("Imposible deserealizar celda " + cellPos);
		} catch (Exception e) {
			throw new CellDeserializerException(e);
		}
	}// deserialize

	public boolean isBlank(Cell cell) throws CellDeserializerException {
		try {
			return cell.getCellType() == Cell.CELL_TYPE_BLANK;
		} catch (Exception e) {
			throw new CellDeserializerException(e);
		}
	}// isBlank

	public boolean isEmpty(Cell cell) throws CellDeserializerException {
		try {
			String stringValue = this.parseValue(cell).toString();
			return stringValue == null || stringValue.contentEquals("");
		} catch (Exception e) {
			throw new CellDeserializerException(e);
		}
	}
}// CellDeserializer
