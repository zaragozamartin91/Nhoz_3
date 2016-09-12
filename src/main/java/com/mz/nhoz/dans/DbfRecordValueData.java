package com.mz.nhoz.dans;

import nl.knaw.dans.common.dbflib.Field;
import nl.knaw.dans.common.dbflib.Value;

@SuppressWarnings("unchecked")
public class DbfRecordValueData {
	public final Value dbfValue;
	public final Object javaValue;
	public final Field field;

	public DbfRecordValueData(Value dbfValue, Object javaValue, Field field) {
		super();
		this.dbfValue = dbfValue;
		this.javaValue = javaValue;
		this.field = field;
	}

	public <T extends Value> T getDbfValueAs(Class<T> type) {
		return (T) dbfValue;
	}

	public <T> T getJavaValueAs(Class<T> type) {
		return (T) javaValue;
	}

	@Override
	public String toString() {
		return "RecordValueData [dbfValue=" + dbfValue + ", javaValue=" + javaValue + ", field=" + field + "]";
	}
}
