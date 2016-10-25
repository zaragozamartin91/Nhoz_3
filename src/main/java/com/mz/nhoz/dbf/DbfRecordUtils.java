package com.mz.nhoz.dbf;

import java.lang.reflect.Field;
import java.util.Map;

import nl.knaw.dans.common.dbflib.Record;
import nl.knaw.dans.common.dbflib.Value;

public class DbfRecordUtils {
	@SuppressWarnings("unchecked")
	public static Map<String, Value> valueMap(Record record) {
		try {
			Field valueMapField = record.getClass().getDeclaredField("valueMap");
			valueMapField.setAccessible(true);
			return (Map<String, Value>) valueMapField.get(record);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
