package com.mz.nhoz.config.impl;

import java.awt.*;
import java.io.File;
import java.util.HashSet;
import java.util.Set;

import javax.swing.*;

import com.mz.nhoz.config.exception.GuiConfigurationAbortException;

import com.mz.nhoz.config.exception.ConfigurationException;
import com.mz.nhoz.util.DecimalSymbol;
import com.mz.nhoz.util.StringUtils;

public class GuiConfiguration extends AbstractConfiguration {
    private final Set<String> configToggles;

    public GuiConfiguration(Set<String> configToggles) {
        this.configToggles = configToggles;
    }

    public GuiConfiguration() {
        HashSet<String> toggles = new HashSet<>();
        toggles.add("dbf");
        toggles.add("xls");
        toggles.add("provider");
        toggles.add("decimalSymbol");
        this.configToggles = toggles;
    }

    @Override
	public void load() throws GuiConfigurationAbortException {
        if (configToggles.contains("dbf")) setDbfFilePath(chooseFile("SELECCIONE ARCHIVO DBF").getAbsolutePath());

        if (configToggles.contains("xls"))  setXlsFilePath(chooseFile("SELECCIONE ARCHIVO XLS").getAbsolutePath());

        if (configToggles.contains("provider")) setProviderId(readProviderId("INGRESE EL CODIGO DEL PROVEEDOR A ACTUALIZAR"));

        if (configToggles.contains("decimalSymbol")) setDecimalSymbol(readDecimalSymbol("INGRESE EL SEPARADOR DECIMAL DE LOS PRECIOS DEL ARCHIVO EXCEL"));
	}

    private DecimalSymbol readDecimalSymbol(String message) {
        // Create radio buttons
        JRadioButton dot = new JRadioButton("PUNTO ( . )");
        JRadioButton comma = new JRadioButton("COMA ( , )");
        dot.setSelected(true); // Default selection

        // Group radio buttons so only one can be selected
        ButtonGroup group = new ButtonGroup();
        group.add(dot);
        group.add(comma);

        // Create panel and add components
        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel(message));
        panel.add(dot);
        panel.add(comma);

        // Show dialog
        int result = JOptionPane.showConfirmDialog(
                null,
                panel,
                "Input",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (result == JOptionPane.CANCEL_OPTION) {
            throw new GuiConfigurationAbortException("Lectura de simbolo decimal abortada");
        }

        if (dot.isSelected()) return DecimalSymbol.DOT;

        if (comma.isSelected()) return DecimalSymbol.COMMA;

        return this.readDecimalSymbol(message);
    }


	private File chooseFile(String dialogTitle) {
		JFileChooser fc = new JFileChooser(new File("."));
		fc.setDialogTitle(dialogTitle);
		int returnVal = fc.showOpenDialog(null);

		if (returnVal == JFileChooser.APPROVE_OPTION) return fc.getSelectedFile();

        if (returnVal == JFileChooser.CANCEL_OPTION) {
            throw new GuiConfigurationAbortException("Seleccion de archivo abortada");
		}

        return chooseFile(dialogTitle);
	}

	private String readProviderId(String message) {
		String provider = JOptionPane.showInputDialog(message);

		if (StringUtils.nullOrEmpty(provider)) {
			return readProviderId(message);
		}

		return provider;
	}

	public static void main(String[] args) throws ConfigurationException {
		GuiConfiguration guiConfiguration = new GuiConfiguration(new HashSet());
		guiConfiguration.load();

		System.out.println(guiConfiguration);
	}
}
