package com.mz.nhoz.dbf;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

import com.linuxense.javadbf.DBFException;
import com.linuxense.javadbf.DBFField;
import com.linuxense.javadbf.DBFWriter;
import com.mz.nhoz.dbf.exception.DbfWriterException;

/**
 * Escritor de archivos Dbf.
 * 
 * @author martin
 *
 */
public class DbfWriter {
	private FileOutputStream outputStream;
	private DBFWriter writer = new DBFWriter();
	private AtomicBoolean fieldsSet = new AtomicBoolean(false);
	private AtomicBoolean closed = new AtomicBoolean(false);

	/**
	 * Crea un escritor de archivos dbf.
	 * 
	 * @param dbfFile
	 *            Archivo a escribir.
	 * @param overwrite
	 *            [OPCIONAL] true si se desea que el archivo destino se
	 *            sobreescriba. False en caso contrario. FALSE por defecto.
	 * @throws FileNotFoundException
	 *             Si el archivo destino es un directorio o si el archivo
	 *             destino NO existe y no puede ser creado.
	 */
	public DbfWriter(File dbfFile, boolean... overwrite) throws FileNotFoundException {
		boolean doOverwrite = overwrite != null && overwrite[0];
		if (doOverwrite && dbfFile.exists()) {
			dbfFile.delete();
		}

		outputStream = new FileOutputStream(dbfFile);
	}

	/**
	 * Cierra y vuelca los contenidos almacenados al archivo destino.
	 * 
	 * @throws IOException
	 *             Si ocurrio un error al cerrarl el stream del archivo destino.
	 * @throws DbfWriterException
	 *             Si ocurrio un error al volcar los contenidos al archivo
	 *             destino o si el archivo ya fue cerrado previamente.
	 */
	public void close() throws IOException, DbfWriterException {
		if (closed.compareAndSet(false, true)) {
			try {
				writer.write(outputStream);
			} catch (Exception e) {
				throw new DbfWriterException("Error al escribir los registros en el archivo dbf", e);
			}
			outputStream.close();
			return;
		}

		throw new DbfWriterException("Archivo ya fue cerrado!");
	}

	/**
	 * Agrega un registro.
	 * 
	 * @param record
	 *            Registro a agregar.
	 * @return this.
	 * @throws DbfWriterException
	 *             En caso que no se puedan establecer los campos del escritor
	 *             dbf o que ocurra un error durante el agregado del registro.
	 */
	public DbfWriter addRecord(DbfRecord record) throws DbfWriterException {
		if (fieldsSet.compareAndSet(false, true)) {
			try {
				writer.setFields(record.getFields());
			} catch (DBFException e) {
				throw new DbfWriterException("Ocurrio un error al establecer los campos del archivo dbf destino", e);
			}
		}

		Object[] recordValues = record.getValues();
		try {
			writer.addRecord(recordValues);
		} catch (DBFException e) {
			throw new DbfWriterException("Ocurrio un error al agregar el registro " + Arrays.asList(recordValues), e);
		}

		return this;
	}
}
