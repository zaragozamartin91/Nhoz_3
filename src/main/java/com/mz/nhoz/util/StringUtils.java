package com.mz.nhoz.util;

import java.util.StringTokenizer;

public class StringUtils {
	private StringUtils() {
	}

	public static boolean nullOrEmpty(String s) {
		return s == null || s.trim().isEmpty();
	}

	public static boolean notNullNorEmpty(String s) {
		return !nullOrEmpty(s);
	}

	public static StringPair parsePair(String line, String delim) {
		StringTokenizer stringTokenizer = new StringTokenizer(line, delim);
		String first = stringTokenizer.nextToken().trim();
		String second = stringTokenizer.nextToken().trim();

		return new StringPair(first, second);
	}

	/**
	 * Intenta comparar dos objetos como strings aplicando trim antes de
	 * comparar.
	 * 
	 * @param first
	 *            Primer objeto.
	 * @param second
	 *            Segundo objeto.
	 * @return Si el contenido de <code>first.toString().trim()</code> coincide
	 *         con el de <code>second.toString().trim()</code> , false en caso
	 *         contrario.
	 */
	public static boolean equalAsStrings(Object first, Object second) {
		try {
			final String strRecordValue = first.toString().trim();
			final String strCompareValue = second.toString().trim();

			return strCompareValue.contentEquals(strRecordValue);
		} catch (Exception e) {
			return false;
		}
	}// tryCompareObjectsAsStrings

	/**
	 * Retorna un string nuevo con todos los ceros que lo encabezaban removidos.
	 * Si el string original es "0" -> se devuelve "0".
	 * 
	 * @param s
	 *            String a quitar ceros.
	 * @return Nuevo string sin ceros que lo encabecen.
	 */
	public static String removeLeadingZeroes(String s) {
//		return s.replaceFirst("^0+", "");
		return s.replaceFirst("^0+(?!$)", "");
	}

}
