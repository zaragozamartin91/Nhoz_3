package com.mz.nhoz;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

import org.junit.Test;

import com.linuxense.javadbf.DBFField;
import com.linuxense.javadbf.DBFReader;
import com.linuxense.javadbf.DBFWriter;

public class JavadbfTest {
	@Test
	public void testRead() throws IOException {
		FileInputStream dbfInputStream = new FileInputStream(new File("testFiles/LISTAPRE.DBF"));
		DBFReader reader = new DBFReader(dbfInputStream);

		// get the field count if you want for some reasons like the following
		//
		int numberOfFields = reader.getFieldCount();

		// use this count to fetch all field information
		// if required
		//
		for (int i = 0; i < numberOfFields; i++) {
			DBFField field = reader.getField(i);

			// do something with it if you want
			// refer the JavaDoc API reference for more details
			//
			System.out.println(field.getName());
		}

		// Now, lets us start reading the rows
		//
		Object[] rowObjects;

		while ((rowObjects = reader.nextRecord()) != null) {
			System.out.println(Arrays.asList(rowObjects));
		}

		// By now, we have itereated through all of the rows

		dbfInputStream.close();
	}

	@Test
	public void testDuplicate() throws IOException {
		File outFile = new File("testFiles/LISTAPRE_COPY.DBF");
		if (outFile.exists()) {
			outFile.delete();
		}

		FileInputStream dbfInputStream = new FileInputStream(new File("testFiles/LISTAPRE.DBF"));
		DBFReader reader = new DBFReader(dbfInputStream);

		// get the field count if you want for some reasons like the following
		//
		int numberOfFields = reader.getFieldCount();
		DBFField[] fields = new DBFField[numberOfFields];

		// use this count to fetch all field information
		// if required
		//
		for (int i = 0; i < numberOfFields; i++) {
			DBFField field = reader.getField(i);
			fields[i] = field;

			// do something with it if you want
			// refer the JavaDoc API reference for more details
			//
			System.out.println("field " + i + ": " + field.getName());
		}

		DBFWriter writer = new DBFWriter();
		writer.setFields(fields); // fields is a non-empty array of DBFField
									// objects

		Object[] rowData;
		while ((rowData = reader.nextRecord()) != null) {
			rowData[9] = new Double("999.99");
			writer.addRecord(rowData);
		}

		FileOutputStream dbfOutputStream = new FileOutputStream(outFile.getAbsolutePath());
		writer.write(dbfOutputStream);
		dbfOutputStream.close();

		dbfInputStream.close();
	}
}
