package com.mz.nhoz.xls;

import java.io.File;

public class ExcelDocType {
	public static final String XLS_EXT = ".xls";
	public static final String XLSX_EXT = ".xlsx";
	private File file;

	public ExcelDocType(File file) {
		super();
		this.file = file;
	}

	public boolean isXls() {
		return file.getName().toLowerCase().endsWith(XLS_EXT);
	}
	
	public boolean isXlsx() {
		return file.getName().toLowerCase().endsWith(XLSX_EXT);
	}
}//ExcelDocType
