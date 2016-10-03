package com.mz.nhoz.dbf.exception;

public class DbfWriterException extends Exception {
	private static final long serialVersionUID = -4399932681284888071L;

	public DbfWriterException() {
	}

	public DbfWriterException(String message) {
		super(message);
	}

	public DbfWriterException(Throwable cause) {
		super(cause);
	}

	public DbfWriterException(String message, Throwable cause) {
		super(message, cause);
	}

	public DbfWriterException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
