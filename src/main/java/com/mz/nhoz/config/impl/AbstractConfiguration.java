package com.mz.nhoz.config.impl;

import com.mz.nhoz.config.Configuration;
import com.mz.nhoz.util.DecimalSymbol;

public abstract class AbstractConfiguration implements Configuration {
	protected String dbfFilePath = "C:\\Martin\\LISTAPRE.DBF";
	protected String xlsFilePath = "C:\\Xls\\PROVEEDOR.xls";
	protected String providerId = null;
	protected DecimalSymbol decimalSymbol;

	public String getDbfFilePath() {
		return dbfFilePath;
	}

	public void setDbfFilePath(String dbfFilePath) {
		this.dbfFilePath = dbfFilePath;
	}

	public String getXlsFilePath() {
		return xlsFilePath;
	}

	public void setXlsFilePath(String xlsFilePath) {
		this.xlsFilePath = xlsFilePath;
	}

	public String getProviderId() {
		return providerId;
	}

	public void setProviderId(String providerId) {
		this.providerId = providerId;
	}

	public DecimalSymbol getDecimalSymbol() {
		return decimalSymbol;
	}

	public void setDecimalSymbol(DecimalSymbol decimalSymbol) {
		this.decimalSymbol = decimalSymbol;
	}

	@Override
	public String toString() {
		return "AbstractConfiguration [dbfFilePath=" + dbfFilePath + ", xlsFilePath=" + xlsFilePath + ", providerId=" + providerId + ", " + "]";
	}
}
