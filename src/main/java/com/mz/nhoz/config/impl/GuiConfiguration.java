package com.mz.nhoz.config.impl;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import com.mz.nhoz.config.exception.ConfigurationException;
import com.mz.nhoz.util.DecimalSymbol;
import com.mz.nhoz.util.StringUtils;

public class GuiConfiguration extends AbstractConfiguration {
	Logger logger = Logger.getLogger(GuiConfiguration.class);
	private boolean cancel = false;

	@Override
	public void load() throws ConfigurationException {
		setDbfFilePath(chooseFile("SELECCIONE ARCHIVO DBF").getAbsolutePath());
		if (cancel) {
			throw new ConfigurationException("PROCESO DE CONFIGURAICON CANCELADO");
		}

		setXlsFilePath(chooseFile("SELECCIONE ARCHIVO XLS").getAbsolutePath());
		if (cancel) {
			throw new ConfigurationException("PROCESO DE CONFIGURAICON CANCELADO");
		}

		setProviderId(getProviderId("INGRESE EL CODIGO DE PROVEEDOR"));

		setDecimalSymbol(getDecimalSymbol("INGRESE EL SEPARADOR DECIMAL"));
	}

	private DecimalSymbol getDecimalSymbol(String message) {
		String decimalDelim = JOptionPane.showInputDialog(message, ".");
		if (StringUtils.nullOrEmpty(decimalDelim)) {
			return getDecimalSymbol(message);
		}

		DecimalSymbol decimalSymbol = null;
		DecimalSymbol[] decimalSymbols = DecimalSymbol.values();
		for (DecimalSymbol decSym : decimalSymbols) {
			if (decSym.symbol.equals(decimalDelim)) {
				decimalSymbol = decSym;
				break;
			}
		}

		if (decimalSymbol == null) {
			return getDecimalSymbol(message);
		}

		return decimalSymbol;
	}

	private File chooseFile(String dialogTitle) {
		JFileChooser fc = new JFileChooser(new File("."));
		fc.setDialogTitle(dialogTitle);
		int returnVal = fc.showOpenDialog(null);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			return fc.getSelectedFile();
		} else if (returnVal == JFileChooser.CANCEL_OPTION) {
			cancel = true;
			// return new File(".");
			System.exit(1);
			return null;
		} else {
			return chooseFile(dialogTitle);
		}
	}

	private String getProviderId(String message) {
		String provider = JOptionPane.showInputDialog(message);

		if (StringUtils.nullOrEmpty(provider)) {
			return getProviderId(message);
		}

		return provider;
	}

	public static void main(String[] args) throws ConfigurationException {
		GuiConfiguration guiConfiguration = new GuiConfiguration();
		guiConfiguration.load();

		System.out.println(guiConfiguration);
	}
}
