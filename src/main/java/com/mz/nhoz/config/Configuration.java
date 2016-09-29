package com.mz.nhoz.config;

import com.mz.nhoz.config.exception.ConfigurationException;
import com.mz.nhoz.util.DecimalSymbol;

/**
 * Configuracion de la aplicacion.
 * 
 * @author martin
 *
 */
public interface Configuration {

	/**
	 * Inicia la carga de la configuracion.
	 * 
	 * @throws ConfigurationException
	 *             En caso que ocurra un error durante la carga.
	 */
	void load() throws ConfigurationException;

	/**
	 * Obtiene path de archivo dbf.
	 * 
	 * @return path de archivo dbf.
	 */
	String getDbfFilePath();

	/**
	 * Obtiene path de archivo excel.
	 * 
	 * @return path de archivo excel.
	 */
	String getXlsFilePath();

	/**
	 * Obtiene id de proveedor.
	 * 
	 * @return id de proveedor.
	 */
	String getProviderId();

	/**
	 * Obtiene el simbolo decimal de los precios.
	 * 
	 * @return simbolo decimal de los precios.
	 */
	DecimalSymbol getDecimalSymbol();
}
