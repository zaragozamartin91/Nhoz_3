package com.mz.nhoz.util;

import com.mz.nhoz.util.exception.NumberUtilsException;

public class NumberUtils {
	public static final String DOUBLE_REGEX = "[-+]?[0-9]*[\\.,]?[0-9]+[\\.,]?[0-9]*";

	@SuppressWarnings("unchecked")
	public static <E extends Number> E parseAsNumber(String s, Class<E> type) throws NumberUtilsException {
		try {
			if (s.matches(DOUBLE_REGEX)) {
				Double doubleValue = Double.valueOf(s);
				if (type.equals(Integer.class)) {
					return (E) Integer.valueOf(doubleValue.intValue());
				}
				if (type.equals(Float.class)) {
					return (E) Float.valueOf(doubleValue.floatValue());
				}
				return (E) doubleValue;
			} else {
				throw new NumberFormatException("Error durante parseo de " + s + " como numero a partir de " + DOUBLE_REGEX);
			}
		} catch (Exception e) {
			throw new NumberUtilsException(e);
		}
	}

	/**
	 * Obtiene el valor numerico con formato americano de un String como un
	 * Integer. Ejemplo: parseUsLocaleNumberStringAsInteger("123.89") = new
	 * Integer(123).
	 * 
	 * @param s
	 *            - String a parsear como numero.
	 * @return Integer con valor representativo.
	 * @throws NumberUtilsException
	 */
	public static Integer parseStringAsInteger(String s) throws NumberUtilsException {
		return parseAsNumber(s, Integer.class);
	}

	/**
	 * Obtiene el valor numerico con formato americano de un String como un
	 * Double. Ejemplo: parseUsLocaleNumberStringAsInteger("123.89") = new
	 * Double(123.89).
	 * 
	 * @param s
	 *            - String a parsear como numero.
	 * @return Integer con valor representativo.
	 * @throws NumberUtilsException
	 */
	public static Double parseStringAsDouble(String s) throws NumberUtilsException {
		return parseAsNumber(s, Double.class);
	}

	public static String parseIntegerAsString(Integer integer, int digitCount) {
		String strInt = integer.toString();

		if (strInt.length() >= digitCount) {
			return strInt;
		}

		int diffDigits = digitCount - strInt.length();
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < diffDigits; i++) {
			stringBuilder.append("0");
		}
		stringBuilder.append(strInt);

		return stringBuilder.toString();
	}

	/**
	 * Intenta comparar dos objetos como numeros enteros.
	 * 
	 * @param first
	 *            - Primer objeto.
	 * @param second
	 *            - Segundo objeto.
	 * @return True en caso que los objetos puedan representarse como numeros
	 *         enteros y coincidan en valor, false en caso contrario.
	 */
	public static boolean equalAsIntegers(Object first, Object second) {
		try {
			final Integer n_first = parseStringAsInteger(first.toString().trim());
			final Integer n_second = parseStringAsInteger(second.toString().trim());
			return n_first.equals(n_second);
		} catch (Exception e) {
			return false;
		}
	}// tryCompareObjectsAsIntegers

	/**
	 * Intenta comparar dos objetos como numeros de doble precision.
	 * 
	 * @param first
	 *            - Primer objeto.
	 * @param second
	 *            - Segundo objeto.
	 * @return True en caso que los objetos puedan representarse como numeros de
	 *         doble precision y coincidan en valor, false en caso contrario.
	 */
	public static boolean equalAsDoubles(Object first, Object second) {
		try {
			final String s_first = first.toString().trim();
			final String s_second = second.toString().trim();
			final Double n_first = parseStringAsDouble(s_first);
			final Double n_second = parseStringAsDouble(s_second);
			return n_first.equals(n_second);
		} catch (Exception e) {
			return false;
		}
	}// tryCompareObjectsAsIntegers

}
