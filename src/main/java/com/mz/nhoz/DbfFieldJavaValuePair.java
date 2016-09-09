package com.mz.nhoz;

import nl.knaw.dans.common.dbflib.Field;

public class DbfFieldJavaValuePair {
	public final Field field;
	public final Object value;

	public DbfFieldJavaValuePair(Field field, Object value) {
		super();
		this.field = field;
		this.value = value;
	}
}
