package com.mz.nhoz;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import nl.knaw.dans.common.dbflib.CorruptedTableException;
import nl.knaw.dans.common.dbflib.DbfLibException;
import nl.knaw.dans.common.dbflib.Field;
import nl.knaw.dans.common.dbflib.IfNonExistent;
import nl.knaw.dans.common.dbflib.Record;
import nl.knaw.dans.common.dbflib.Table;
import nl.knaw.dans.common.dbflib.Value;
import nl.knaw.dans.common.dbflib.Version;

import org.junit.Test;

public class ReadDbfTest {
	private DbfRecordValueParser recordParser = new DbfRecordValueParser();
	private DbfRecordBuilder recordBuilder = new DbfRecordBuilder();

	@Test
	public void readListapre() throws IOException, DbfLibException {
		Table original = new Table(new File("testFiles/LISTAPRE.DBF"));
		original.open();
		System.out.println("original record count: "
				+ original.getRecordCount());
		Version originalVersion = original.getVersion();

		List<Field> fields = original.getFields();
		Field priceField = fields.get(9);

		for (final Field field : fields) {
			System.out.println("Name:         " + field.getName());
			System.out.println("Type:         " + field.getType());
			System.out.println("Length:       " + field.getLength());
			System.out.println("DecimalCount: " + field.getDecimalCount());
			System.out.println();
		}

		File duplicateFile = new File("testFiles/LISTAPRE_COPY.DBF");
		deleteFile(duplicateFile);
		Table duplicate = new Table(duplicateFile, originalVersion, fields);
		duplicate.open(IfNonExistent.CREATE);

		int i = 0;
		Iterator<Record> recordIterator = original.recordIterator(false);
		while (recordIterator.hasNext()) {
			System.out.println("Editando registro: " + i);
			Record record = (Record) recordIterator.next();
			Record newRecord = recordBuilder.buildFrom(record, fields,
					new DbfFieldJavaValuePair(priceField, 999.99f));
			try {
				duplicate.addRecord(newRecord);
			} catch (Exception e) {
				System.err.println("Error editando el registro: " + i);
				System.err.println(e.getMessage());
			}
			++i;
		}

		duplicate.close();
		original.close();
	}

	@Test
	public void testBrokenRecord() throws CorruptedTableException, IOException {
		Table original = new Table(new File("testFiles/LISTAPRE.DBF"));
		original.open();
		System.out.println("original record count: "
				+ original.getRecordCount());
		Version originalVersion = original.getVersion();

		List<Field> fields = original.getFields();
		Field priceField = fields.get(9);

		for (final Field field : fields) {
			System.out.println("Name:         " + field.getName());
			System.out.println("Type:         " + field.getType());
			System.out.println("Length:       " + field.getLength());
			System.out.println("DecimalCount: " + field.getDecimalCount());
			System.out.println();
		}

		{
			Record record = original.getRecordAt(446);
			System.out.println(record.getTypedValue(priceField.getName()));
		}
		{
			Record record = original.getRecordAt(447);
			System.out.println(record.getTypedValue(priceField.getName()));
		}
		{
			Record record = original.getRecordAt(445);
			System.out.println(record.getTypedValue(priceField.getName()));
		}

		original.close();
	}

	private void deleteFile(File file) {
		try {
			file.delete();
		} catch (Exception e) {
		}
	}
}
