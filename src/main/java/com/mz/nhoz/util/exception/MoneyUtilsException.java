package com.mz.nhoz.util.exception;

public class MoneyUtilsException extends Exception {
	private static final long serialVersionUID = 2997208030897452661L;

	public MoneyUtilsException(String string) {
		super(string);
	}

	public MoneyUtilsException(Exception e) {
		super(e);
	}

}
