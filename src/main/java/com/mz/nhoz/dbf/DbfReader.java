package com.mz.nhoz.dbf;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import com.linuxense.javadbf.DBFException;
import com.linuxense.javadbf.DBFField;
import com.linuxense.javadbf.DBFReader;
import com.mz.nhoz.dbf.exception.DbfReaderException;

/**
 * Lector de archivos dbf.
 * 
 * @author martin
 *
 */
public class DbfReader {
	private AtomicBoolean closed = new AtomicBoolean(false);

	private DBFReader reader;
	private FileInputStream fileInputStream;

	/**
	 * Crea un nuevo lector de archivos Dbf.
	 * 
	 * @param file
	 *            Archivo a leer.
	 * @throws FileNotFoundException
	 *             Si el archivo no existe.
	 * @throws DbfReaderException
	 *             Si ocurre un error al leer el archivo.
	 */
	public DbfReader(File file) throws FileNotFoundException, DbfReaderException {
		super();
		try {
			fileInputStream = new FileInputStream(file);
			reader = new DBFReader(fileInputStream);
		} catch (DBFException e) {
			throw new DbfReaderException("Error al abrir el archivo dbf", e);
		}
	}

	/**
	 * Cierra el archivo dbf para liberar recursos.
	 * 
	 * @throws DbfReaderException
	 *             Si ocurre un error al cerrar el archivo.
	 * @throws IllegalStateException
	 *             Si el archivo fue cerrado anteriormente.
	 */
	public void close() throws DbfReaderException {
		if (closed.compareAndSet(false, true)) {
			doClose();
		} else {
			throw new IllegalStateException("El archivo ya fue cerrado anteriormente!");
		}
	}

	/**
	 * Obtiene las definiciones de los campos del archivo.
	 * 
	 * @return definiciones de los campos del archivo.
	 * @throws DbfReaderException
	 *             En caso que los campos no puedan ser leidos.
	 */
	public DBFField[] getFields() throws DbfReaderException {
		try {
			List<DBFField> fields = new ArrayList<DBFField>();
			int fieldCount = reader.getFieldCount();
			for (int i = 0; i < fieldCount; i++) {
				fields.add(reader.getField(i));
			}

			return fields.toArray(new DBFField[0]);
		} catch (DBFException e) {
			throw new DbfReaderException("Error al obtener", e);
		}
	}

	/**
	 * Realiza una accion con cada registro del archivo.
	 * 
	 * @param dbfAction
	 *            Accion a realizar.
	 * @throws DBFException
	 *             Error al obtener un registro.
	 * @throws DbfReaderException
	 *             Error al obtener los campos del archivo.
	 * @throws IllegalStateException
	 *             Si el archivo ya fue leido o si fue cerrado.
	 */
	public void forEach(DbfAction dbfAction) throws DbfReaderException {
		if (closed.compareAndSet(false, true)) {
			DBFField[] fields = getFields();
			Object[] rowObjects;

			int recordIndex = 0;
			while (true) {
				try {
					rowObjects = reader.nextRecord();
					if (rowObjects == null) {
						break;
					}
					DbfRecord dbfRecord = new DbfRecord(fields, rowObjects);
					dbfAction.run(dbfRecord);
				} catch (DBFException e) {
					throw new DbfReaderException("Error al leer el registro " + recordIndex, e);
				}
				++recordIndex;
			}

			doClose();
		} else {
			throw new IllegalStateException("El archivo ya fue leido.");
		}
	}

	/**
	 * Retorna true si el archivo fue cerrado para su manipulacion.
	 * 
	 * @return true si el archivo fue cerrado para su manipulacion, false en
	 *         caso contrario.
	 */
	public boolean isClosed() {
		return closed.get();
	}

	private void doClose() throws DbfReaderException {
		try {
			fileInputStream.close();
		} catch (IOException e) {
			throw new DbfReaderException("Error al cerrar el archivo dbf", e);
		}
	}
}
