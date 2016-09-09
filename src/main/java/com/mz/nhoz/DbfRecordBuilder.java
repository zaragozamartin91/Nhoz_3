package com.mz.nhoz;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.knaw.dans.common.dbflib.DbfLibException;
import nl.knaw.dans.common.dbflib.Field;
import nl.knaw.dans.common.dbflib.Record;
import nl.knaw.dans.common.dbflib.Value;

public class DbfRecordBuilder {
	private static DbfRecordValueParser recordValueParser = new DbfRecordValueParser();

	public Record buildFrom(Record original, List<Field> recordFields, DbfFieldJavaValuePair... updateFields)
			throws DbfLibException {
		if (updateFields == null) {
			throw new IllegalArgumentException("Los campos a actualizar no pueden ser nulos!");
		}

		Map<Field, DbfFieldJavaValuePair> updateFieldsMap = new HashMap<Field, DbfFieldJavaValuePair>();
		for (DbfFieldJavaValuePair updateField : updateFields) {
			updateFieldsMap.put(updateField.field, updateField);
		}

		Map<String, Value> recordValuesMap = new HashMap<String, Value>();

		for (Field field : recordFields) {
			if (updateFieldsMap.containsKey(field)) {
				DbfFieldJavaValuePair updateField = updateFieldsMap.get(field);
				DbfRecordValueData recordValueData = recordValueParser.parseJavaValue(updateField.value, updateField.field);
				recordValuesMap.put(field.getName(), recordValueData.dbfValue);
			} else {
				DbfRecordValueData recordValueData = recordValueParser.parseRecordFieldValue(original, field);
				recordValuesMap.put(field.getName(), recordValueData.dbfValue);
			}
		}

		return new Record(recordValuesMap);
	}
}
