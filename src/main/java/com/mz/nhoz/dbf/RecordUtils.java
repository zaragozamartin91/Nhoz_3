package com.mz.nhoz.dbf;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import nl.knaw.dans.common.dbflib.Record;
import nl.knaw.dans.common.dbflib.Value;

import org.apache.log4j.Logger;

import com.mz.nhoz.dbf.exception.RecordUtilsException;
import com.mz.nhoz.dbf.exception.ValueParserException;

/**
 * Utilidades para manejo de registros dbf.
 * 
 * @author martin.zaragoza
 *
 */
public class RecordUtils {
	static Logger logger = Logger.getLogger(RecordUtils.class);

	/**
	 * Obtiene el mapa de valores que guarda un {@link Record}.
	 * 
	 * @param record
	 *            - registro a obtener mapa de valores.
	 * @return Mapa de valores del registro.
	 * @throws RecordUtilsException
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Value> valueMap(Record record) {
		try {
			Field valueMapField = record.getClass().getDeclaredField("valueMap");
			valueMapField.setAccessible(true);
			return (Map<String, Value>) valueMapField.get(record);
		} catch (Exception e) {
			logger.error(e);
			return null;
		}
	}// getValueMap

	/**
	 * Establece el valor de un campo del registro.
	 * 
	 * @param record
	 *            Registro a modificar.
	 * @param key
	 *            Nombre del campo a modificar.
	 * @param value
	 *            Valor a establecer.
	 * @throws RecordUtilsException
	 *             En caso que el registro no pueda leerse/escribirse.
	 */
	public static void setValue(Record record, String key, Object value) throws RecordUtilsException {
		try {
			Map<String, Value> valueMap = valueMap(record);
			Value parsedValue = ValueParser.parse(value);
			valueMap.put(key, parsedValue);
		} catch (ValueParserException e) {
			throw new RecordUtilsException(e);
		}
	}

    public static Number utility(Record record) {
        return record.getNumberValue("UTILIDAD");
    }

    public static Number saleValue(Record record) {
        return record.getNumberValue("RAZON");
    }

    public static Number priceValue(Record record) {
        return record.getNumberValue("PRECIOUNI");
    }

	public static String articleId(Record record) {
		return record.getStringValue("ARTICULO");
	}

	public static String providerId(Record record) {
		return record.getStringValue("CODIGOPROV");
	}

	/**
	 * Obtiene los datos del registro como un mapa clave->valor. Las claves son los encabezados del registro, los
	 * valores son objetos java.
	 * 
	 * @param record
	 *            - Registro a deserealizar.
	 * @return mapa clave->valor. Las claves son los encabezados del registro, los valores son objetos java.
	 * @throws RecordUtilsException
	 */
	public static Map<String, Object> deserialize(Record record) throws RecordUtilsException {
		Map<String, Value> valueMap = valueMap(record);
		Set<String> keySet = valueMap.keySet();
		Map<String, Object> deserializedValues = new HashMap<String, Object>();

		for (String key : keySet) {
			Value value = valueMap.get(key);
			if (value == null) {
				deserializedValues.put(key, null);
				continue;
			}

			deserializedValues.put(key, record.getTypedValue(key));
		}

		return deserializedValues;
	}// deserializeRecord

}
