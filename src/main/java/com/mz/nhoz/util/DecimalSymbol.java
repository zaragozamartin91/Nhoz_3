package com.mz.nhoz.util;

import java.util.Locale;

public enum DecimalSymbol {
	DOT(Locale.US), COMMA(Locale.ITALY);

	public final Locale locale;

	private DecimalSymbol(Locale symbol) {
		this.locale = symbol;
	}
}
