package com.mz.nhoz.xls;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import com.google.gson.Gson;
import com.mz.nhoz.xls.exception.RowRecordException;
import com.mz.nhoz.xls.util.CellParser;
import com.mz.nhoz.xls.util.exception.CellDeserializerException;

/**
 * Representa un registro de fila de Excel.
 * 
 * @author martin.zaragoza
 *
 */
public class RowRecord {
	public static final CellParser CELL_DESERIALIZER = new CellParser();

	private Map<String, Object> valueMap = new HashMap<String, Object>();

	public RowRecord(Row row, TableHeader header) throws RowRecordException {
		super();

		try {
			build(row, header);
		} catch (Exception e) {
			throw new RowRecordException(e);
		}
	}// cons

	private void build(Row row, TableHeader header) throws CellDeserializerException {
		Iterator<Cell> cellIterator = row.cellIterator();

		int index = 0;

		while (cellIterator.hasNext()) {
			Cell cell = (Cell) cellIterator.next();

			if (!CELL_DESERIALIZER.isBlank(cell) && !CELL_DESERIALIZER.isEmpty(cell)) {
				Object deserializedCellValue = CELL_DESERIALIZER.parseValue(cell);
				String cellLabel = header.getLabel(index);

				valueMap.put(cellLabel, deserializedCellValue);
			}
			++index;
		}
	}// __build

	public boolean isEmpty() {
		return valueMap.isEmpty();
	}

	public Set<String> keys() {
		return this.valueMap.keySet();
	}

	public Collection<Object> values() {
		return this.valueMap.values();
	}

	/**
	 * Retorna un objeto de la fila.
	 * 
	 * @param cellLabel - Id de columna.
	 * @return objeto de la fila.
	 */
	public Object get(String cellLabel) {
		return this.valueMap.get(cellLabel);
	}// get
	

	public String toString() {
		return new Gson().toJson(this.valueMap);
	}

	public boolean equalsMap(Map<String, Object> map) {
		return this.valueMap.equals(map);
	}

	public RowRecord put(String key, Object value) {
		this.valueMap.put(key, value);
		return this;
	}
}// RowRecord
