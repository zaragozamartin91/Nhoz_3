package com.mz.nhoz.dbf;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;

import org.junit.Test;

import com.linuxense.javadbf.DBFException;
import com.mz.nhoz.dbf.exception.DbfReaderException;

public class DbfReaderTest {

	@Test
	public void testForEach() throws FileNotFoundException, DbfReaderException, DBFException {
		DbfReader reader = new DbfReader(new File("testFiles/LISTAPRE.DBF"));

		reader.forEach(new DbfAction() {
			public void run(DbfRecord record) {
				System.out.println(Arrays.asList(record.getValues()));
			}
		});

		assertTrue(reader.isClosed());
	}

	@Test(expected = IllegalStateException.class)
	public void testForEachTwiceAndFail() throws FileNotFoundException, DbfReaderException, DBFException {
		DbfReader reader = new DbfReader(new File("testFiles/LISTAPRE.DBF"));

		reader.forEach(new DbfAction() {
			public void run(DbfRecord record) {
			}
		});

		reader.forEach(new DbfAction() {
			public void run(DbfRecord record) {
			}
		});
	}

}
