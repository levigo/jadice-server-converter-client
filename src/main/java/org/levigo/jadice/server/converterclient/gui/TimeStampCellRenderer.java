package org.levigo.jadice.server.converterclient.gui;

import java.text.DateFormat;
import java.util.Date;

import javax.swing.table.DefaultTableCellRenderer;

public class TimeStampCellRenderer extends DefaultTableCellRenderer {
	private static final long serialVersionUID = 542257145110345008L;
	private Date date;
	
	@Override
	public String getText() {
		if (date == null)
			return null;
		
		return DateFormat.getDateTimeInstance().format(date);
	}
	
	@Override
	protected void setValue(Object value) {
		if (! (value instanceof Date || value == null))
			throw new IllegalArgumentException("value is not a date");
		this.date = (Date) value;
		super.setValue(value);
	}
	
}
