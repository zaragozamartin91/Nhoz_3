package com.mz.nhoz.xls.util.exception;

public class ArticleFinderException extends Exception {
	private static final long serialVersionUID = -3663287438463707495L;

	public ArticleFinderException() {
	}

	public ArticleFinderException(String message) {
		super(message);
	}

	public ArticleFinderException(Throwable cause) {
		super(cause);
	}

	public ArticleFinderException(String message, Throwable cause) {
		super(message, cause);
	}

	public ArticleFinderException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
