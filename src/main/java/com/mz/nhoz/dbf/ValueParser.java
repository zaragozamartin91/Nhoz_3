package com.mz.nhoz.dbf;

import java.util.Date;

import nl.knaw.dans.common.dbflib.BooleanValue;
import nl.knaw.dans.common.dbflib.DateValue;
import nl.knaw.dans.common.dbflib.NumberValue;
import nl.knaw.dans.common.dbflib.StringValue;
import nl.knaw.dans.common.dbflib.Value;

import com.mz.nhoz.dbf.exception.ValueParserException;

/**
 * Parsea un tipo de dato Java a un Value Dbf.
 * 
 * @author martin.zaragoza
 *
 */
public class ValueParser {
	/**
	 * Envuelve un tipo de dato Java en un Value Dbf. Si el dato pasado es
	 * instancia de {@link nl.knaw.dans.common.dbflib.Value}, entonces el metodo
	 * devuelve la misma instancia.
	 * 
	 * @param rawVal
	 *            - dato a parsear.
	 * @return Objeto de tipo Value que envuelve a rawVal. Si rawVal es
	 *         instancia de {@link nl.knaw.dans.common.dbflib.Value}, entonces
	 *         el metodo devuelve la misma instancia.
	 * @throws ValueParserException
	 */
	public static Value parse(Object rawVal) throws ValueParserException {
		try {
			Class<? extends Object> clazz = rawVal.getClass();
			Class<?> superclass = clazz.getSuperclass();

			if (superclass.equals(Value.class)) {
				return (Value) rawVal;
			}

			if (superclass.equals(Number.class)) {
				return new NumberValue((Number) rawVal);
			}

			if (clazz.equals(String.class)) {
				return new StringValue((String) rawVal);
			}

			if (clazz.equals(Date.class)) {
				return new DateValue((Date) rawVal);
			}

			if (clazz.equals(Boolean.class)) {
				return new BooleanValue((Boolean) rawVal);
			}
		} catch (Exception e) {
			throw new ValueParserException(e);
		}

		throw new ValueParserException("Imposible parsear " + rawVal.toString());
	}
}// ValueParser
