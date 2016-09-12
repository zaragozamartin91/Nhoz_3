package com.mz.nhoz.dans;

import java.util.Date;

import nl.knaw.dans.common.dbflib.BooleanValue;
import nl.knaw.dans.common.dbflib.ByteArrayValue;
import nl.knaw.dans.common.dbflib.DateValue;
import nl.knaw.dans.common.dbflib.DbfLibException;
import nl.knaw.dans.common.dbflib.Field;
import nl.knaw.dans.common.dbflib.NumberValue;
import nl.knaw.dans.common.dbflib.Record;
import nl.knaw.dans.common.dbflib.StringValue;

public class DbfRecordValueParser {
	public DbfRecordValueData parseRecordFieldValue(Record record, Field field) throws DbfLibException {
		String fieldName = field.getName();
		switch (field.getType()) {

		// Numeric value, mapped to a {@link Number} subclass.
		case NUMBER:
			Number numberValue = record.getNumberValue(fieldName);
			return new DbfRecordValueData(new NumberValue(numberValue), numberValue, field);

			// Float value, mapped to a {@link Number} subclass.
		case FLOAT:
			Number numberValue2 = record.getNumberValue(fieldName);
			return new DbfRecordValueData(new NumberValue(numberValue2), numberValue2, field);

			// String values, mapped to {@link String}.
		case CHARACTER:
			String stringValue = record.getStringValue(fieldName);
			return new DbfRecordValueData(new StringValue(stringValue), stringValue, field);

			// Logical, or boolean value, mapped to {@link Boolean}
		case LOGICAL:
			Boolean booleanValue = record.getBooleanValue(fieldName);
			return new DbfRecordValueData(new BooleanValue(booleanValue), booleanValue, field);

			// Date value, mapped to <code>java.util.Date</code>. Note that in
			// xBase the date does <em>not</em> have a time component. The time
			// related fields of <code>java.util.Date</code> are therefore set
			// to 0.
		case DATE:
			Date dateValue = record.getDateValue(fieldName);
			return new DbfRecordValueData(new DateValue(dateValue), dateValue, field);

			// A String value (without length limitations), mapped to
			// {@link String}
		case MEMO:
			String stringValue2 = record.getStringValue(fieldName);
			return new DbfRecordValueData(new StringValue(stringValue2), stringValue2, field);

			// A binary value (without length limitations), mapped to
			// <code>byte[]</code>.
		case GENERAL:
			byte[] rawValue = record.getRawValue(field);
			return new DbfRecordValueData(new ByteArrayValue(rawValue), rawValue, field);
			// A binary value (without length limitations), mapped to
			// <code>byte[]</code>.
		case PICTURE:
			byte[] rawValue2 = record.getRawValue(field);
			return new DbfRecordValueData(new ByteArrayValue(rawValue2), rawValue2, field);
			// A binary value (without length limitations), mapped to
			// <code>byte[]</code>.
		case BINARY:
			byte[] rawValue3 = record.getRawValue(field);
			return new DbfRecordValueData(new ByteArrayValue(rawValue3), rawValue3, field);

		default:
			throw new IllegalArgumentException("El campo " + field.getName() + " no tiene ningun tipo conocido");
		}
	}
	

	public DbfRecordValueData parseJavaValue(Object value, Field field) {
		switch (field.getType()) {

		// Numeric value, mapped to a {@link Number} subclass.
		case NUMBER:
			Number numberValue = (Number) value;
			return new DbfRecordValueData(new NumberValue(numberValue), numberValue, field);

			// Float value, mapped to a {@link Number} subclass.
		case FLOAT:
			Number numberValue2 = (Number) value;
			return new DbfRecordValueData(new NumberValue(numberValue2), numberValue2, field);

			// String values, mapped to {@link String}.
		case CHARACTER:
			String stringValue = value.toString();
			return new DbfRecordValueData(new StringValue(stringValue), stringValue, field);

			// Logical, or boolean value, mapped to {@link Boolean}
		case LOGICAL:
			Boolean booleanValue = (Boolean) value;
			return new DbfRecordValueData(new BooleanValue(booleanValue), booleanValue, field);

			// Date value, mapped to <code>java.util.Date</code>. Note that in
			// xBase the date does <em>not</em> have a time component. The time
			// related fields of <code>java.util.Date</code> are therefore set
			// to 0.
		case DATE:
			Date dateValue = (Date) value;
			return new DbfRecordValueData(new DateValue(dateValue), dateValue, field);

			// A String value (without length limitations), mapped to
			// {@link String}
		case MEMO:
			String stringValue2 = value.toString();
			return new DbfRecordValueData(new StringValue(stringValue2), stringValue2, field);

			// A binary value (without length limitations), mapped to
			// <code>byte[]</code>.
		case GENERAL:
			byte[] rawValue = (byte[]) value;
			return new DbfRecordValueData(new ByteArrayValue(rawValue), rawValue, field);
			// A binary value (without length limitations), mapped to
			// <code>byte[]</code>.
		case PICTURE:
			byte[] rawValue2 = (byte[]) value;
			return new DbfRecordValueData(new ByteArrayValue(rawValue2), rawValue2, field);
			// A binary value (without length limitations), mapped to
			// <code>byte[]</code>.
		case BINARY:
			byte[] rawValue3 = (byte[]) value;
			return new DbfRecordValueData(new ByteArrayValue(rawValue3), rawValue3, field);

		default:
			throw new IllegalArgumentException("El campo " + field.getName() + " no tiene ningun tipo conocido");
		}
	}

}
