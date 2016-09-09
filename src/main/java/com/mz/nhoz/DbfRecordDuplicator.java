package com.mz.nhoz;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.knaw.dans.common.dbflib.DbfLibException;
import nl.knaw.dans.common.dbflib.Field;
import nl.knaw.dans.common.dbflib.Record;
import nl.knaw.dans.common.dbflib.Value;

public class DbfRecordDuplicator {
	DbfRecordValueParser recordValueParser = new DbfRecordValueParser();

	public Record duplicate(Record record, List<Field> fields) throws DbfLibException {
		Map<String, Value> valueMap = new HashMap<String, Value>();

		for (Field field : fields) {
			valueMap.put(field.getName(), recordValueParser.parseRecordFieldValue(record, field).dbfValue);
		}

		return new Record(valueMap);
	}
}
