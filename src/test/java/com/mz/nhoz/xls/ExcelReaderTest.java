package com.mz.nhoz.xls;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

import com.mz.nhoz.xls.exception.ExcelReaderException;
import com.mz.nhoz.xls.util.exception.CellParserException;

public class ExcelReaderTest {

	@Test(expected = ExcelReaderException.class)
	public void testExcelReaderFileNonexistentSheet() throws ExcelReaderException {
		new ExcelReader(new File("testFiles/newXlsx.xlsx"), 4);
	}

	@Test
	public void testExcelReaderFileInt() throws ExcelReaderException {
		new ExcelReader(new File("testFiles/newXlsx.xlsx"), 0);
	}

	@Test
	public void testIteratorWithXlsxFile() throws ExcelReaderException, CellParserException {
		ExcelReader excelReader = new ExcelReader(new File("testFiles/newXlsx.xlsx"), 0);
		int rowCount = 0;

		for (ExcelRecord excelRecord : excelReader) {
			int columnCount = excelRecord.getColumnCount();
			assertEquals(columnCount, 4);
			assertNull(excelRecord.getCellValue(columnCount));

			for (int i = 0; i < columnCount; i++) {
				assertNotNull(excelRecord.getCellValue(i));
			}

			++rowCount;
		}

		assertEquals(4, rowCount);
	}

}
