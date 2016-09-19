package com.mz.nhoz.util;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mz.nhoz.util.exception.MoneyUtilsException;

/**
 * Herramientas utiles para trabajar valores numericos y precios a partir de
 * Strings,
 * 
 * @author martin
 *
 */
public class MoneyUtils {
	/**
	 * [-+]?[0-9]*[\\.,]?[0-9]+[\\.,]?[0-9]*
	 */
	public static final String NUMBER_PATTERN_REGEX = "[-+]?[0-9]*[\\.,]?[0-9]+[\\.,]?[0-9]*";
	private static final Pattern NUMBER_PATTERN = Pattern.compile(NUMBER_PATTERN_REGEX);

	/**
	 * Remueve el simbolo de precio '$' al principio del string.
	 * 
	 * @param toParse
	 *            String a parsear.
	 * @return String con precio removido.
	 */
	public static String removePriceSymbol(String toParse) {
		return removePriceSymbol(toParse, "$");
	}

	/**
	 * Remueve el simbolo de precio '$' al principio del string.
	 * 
	 * @param toParse
	 *            String a parsear.
	 * @return String con precio removido.
	 */
	public static String removePriceSymbol(String toParse, String symbol) {
		toParse = toParse.trim();
		return toParse.replaceAll(Pattern.quote(symbol) + " {0,}", "").trim();
	}

	public static String replaceCommaWithDot(String s) throws MoneyUtilsException {
		try {
			return s.replaceAll(",", "\\.");
		} catch (Exception e) {
			throw new MoneyUtilsException(e);
		}
	}

	/**
	 * Interpreta un valor numerico como un precio.
	 * 
	 * @param obj
	 *            Valor a interpretar [Number|String]
	 * @param locale
	 *            Locale a usar para interpretar el valor numerico en caso que
	 *            el mismo sea String.
	 * @return Valor numerico interpretado como Double.
	 * @throws MoneyUtilsException
	 *             En caso que el valor a interpretar no sea valor numerico ni
	 *             corresponda con {@link MoneyUtils#NUMBER_PATTERN_REGEX}
	 */
	public static Double parsePriceAsDouble(Object obj, Locale locale) throws MoneyUtilsException {
		if (obj instanceof Number) {
			Number number = (Number) obj;
			return number.doubleValue();
		}

		String s = obj.toString().trim();
		Matcher matcher = NUMBER_PATTERN.matcher(s);
		if (matcher.find()) {
			String match = matcher.group();

			NumberFormat format = NumberFormat.getInstance(locale);

			try {
				Number number = format.parse(match);
				return number.doubleValue();
			} catch (Exception e) {
				throw new MoneyUtilsException(e);
			}
		} else {
			throw new MoneyUtilsException("Error al obtener valor dinero a partir de " + s + " usando " + NUMBER_PATTERN_REGEX);
		}
	}

	public static Double parsePriceAsDouble(Object obj, DecimalSymbol... decimalSymbol) throws MoneyUtilsException {
		if (decimalSymbol == null || decimalSymbol.length == 0) {
			return parsePriceAsDouble(obj, Locale.US);
		}

		return parsePriceAsDouble(obj, decimalSymbol[0].locale);
	}

	public static void main(String[] args) {
		System.out.println(new Double(98.098) instanceof Number);
	}
}
