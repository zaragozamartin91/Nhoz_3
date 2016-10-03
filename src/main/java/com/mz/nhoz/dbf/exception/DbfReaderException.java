package com.mz.nhoz.dbf.exception;

public class DbfReaderException extends Exception {
	private static final long serialVersionUID = -5709318637857407358L;

	public DbfReaderException() {
	}

	public DbfReaderException(String message) {
		super(message);
	}

	public DbfReaderException(Throwable cause) {
		super(cause);
	}

	public DbfReaderException(String message, Throwable cause) {
		super(message, cause);
	}
}
