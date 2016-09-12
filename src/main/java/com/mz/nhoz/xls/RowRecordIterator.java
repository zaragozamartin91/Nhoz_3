package com.mz.nhoz.xls;

import java.util.Iterator;

import org.apache.poi.ss.usermodel.Row;

import com.mz.nhoz.xls.exception.RowRecordException;

public class RowRecordIterator implements Iterator<RowRecord> {
	private Iterator<Row> rowIterator;
	private TableHeader TableHeader;

	public RowRecordIterator(Iterator<Row> rowIterator, com.mz.nhoz.xls.TableHeader tableHeader) {
		super();
		this.rowIterator = rowIterator;
		TableHeader = tableHeader;
	}

	@Override
	public boolean hasNext() {
		return rowIterator.hasNext();
	}//hasNext

	@Override
	public RowRecord next() {
		Row nextRow = rowIterator.next();
		try {
			RowRecord rowRecord = new RowRecord(nextRow, TableHeader);
			return rowRecord;
		} catch (RowRecordException e) {
			return null;
		}
	}//next

	@Override
	public void remove() {
		rowIterator.remove();
	}//remove
}// RowRecordIterator
