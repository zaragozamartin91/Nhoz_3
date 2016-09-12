package com.mz.nhoz;

import static org.junit.Assert.*;

import java.util.Locale;

import org.junit.Test;

import com.mz.nhoz.util.DecimalSymbol;
import com.mz.nhoz.util.MoneyUtils;
import com.mz.nhoz.util.exception.MoneyUtilsException;

public class MoneyUtilsTest {

	@Test
	public void testParsePriceAsDouble() throws MoneyUtilsException {
		Double doubleValue = 123.77;
		assertEquals(doubleValue, MoneyUtils.parsePriceAsDouble(doubleValue.toString(), Locale.US));

		assertEquals(new Double(3456.89), MoneyUtils.parsePriceAsDouble("3,456.89", Locale.US));
		assertEquals(new Double(3456.89), MoneyUtils.parsePriceAsDouble("3456.89", Locale.US));
		
		assertEquals(new Double(3456.89), MoneyUtils.parsePriceAsDouble("3.456,89", Locale.ITALY));
		assertEquals(new Double(3456.89), MoneyUtils.parsePriceAsDouble("3456,89", DecimalSymbol.COMMA));
	}

}
