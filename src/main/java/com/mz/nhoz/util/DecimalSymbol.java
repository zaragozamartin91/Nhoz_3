package com.mz.nhoz.util;

public enum DecimalSymbol {
	DOT("."), COMMA(",");

	public final String symbol;

	private DecimalSymbol(String symbol) {
		this.symbol = symbol;
	}
}
