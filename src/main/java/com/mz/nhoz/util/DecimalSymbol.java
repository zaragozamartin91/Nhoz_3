package com.mz.nhoz.util;

import java.util.Locale;

public enum DecimalSymbol {
	DOT(".", Locale.US), COMMA(",", Locale.ITALY);

	public final String symbol;
	public final Locale locale;

	private DecimalSymbol(String symbol, Locale locale) {
		this.symbol = symbol;
		this.locale = locale;
	}
}
