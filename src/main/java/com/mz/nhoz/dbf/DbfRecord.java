package com.mz.nhoz.dbf;

import com.linuxense.javadbf.DBFField;

/**
 * Registro de archivo dbf.
 * 
 * @author martin
 *
 */
public class DbfRecord {
	private DBFField[] fields;
	private Object[] values;

	/**
	 * Construye nuevo registro.
	 * 
	 * @param fields
	 *            Campos del registro.
	 * @param values
	 *            Valores.
	 */
	public DbfRecord(DBFField[] fields, Object[] values) {
		super();
		this.fields = fields;
		this.values = values;
	}

	/**
	 * Obtiene valor de campo del registro.
	 * 
	 * @param fieldName
	 *            Nombre del campo.
	 * @return valor de campo del registro
	 */
	public Object getValue(String fieldName) {
		int fieldIndex = getFieldIndex(fieldName);
		return values[fieldIndex];
	}

	/**
	 * Obtiene valor de campo del registro.
	 * 
	 * @param fieldIndex
	 *            Indice del campo.
	 * @return valor de campo del registro
	 */
	public Object getValue(int fieldIndex) {
		return values[fieldIndex];
	}

	/**
	 * Establece el valor de un campo.
	 * 
	 * @param fieldIndex
	 *            Indice de campo a modificar.
	 * @param newValue
	 *            valor nuevo.
	 */
	public void setValue(int fieldIndex, Object newValue) {
		values[fieldIndex] = newValue;
	}

	/**
	 * Establece el valor de un campo
	 * 
	 * @param fieldName
	 *            Nombre de campo a modificar.
	 * @param newValue
	 *            valor nuevo.
	 * @return this.
	 */
	public DbfRecord setValue(String fieldName, Object newValue) {
		int fieldIndex = getFieldIndex(fieldName);
		values[fieldIndex] = newValue;
		return this;
	}

	/**
	 * Obtiene los campos del registro.
	 * 
	 * @return campos del registro.
	 */
	public DBFField[] getFields() {
		return fields;
	}

	/**
	 * Obtiene los valores del registro.
	 * 
	 * @return valores del registro.
	 */
	public Object[] getValues() {
		return values;
	}

	private int getFieldIndex(String fieldName) {
		for (int i = 0; i < fields.length; i++) {
			if (fieldName.equals(fields[i].getName())) {
				return i;
			}
		}
		throw new IllegalArgumentException("El campo " + fieldName + " no existe!");
	}
}
