package com.mz.nhoz.xls.exception;

public class ExcelReaderException extends Exception {
	private static final long serialVersionUID = -6576129768230787604L;

	public ExcelReaderException(String message) {
		super(message);
	}

	public ExcelReaderException(Throwable cause) {
		super(cause);
	}

	public ExcelReaderException(String message, Throwable cause) {
		super(message, cause);
	}

}
