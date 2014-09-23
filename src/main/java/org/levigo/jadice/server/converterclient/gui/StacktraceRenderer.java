package org.levigo.jadice.server.converterclient.gui;

import javax.swing.table.DefaultTableCellRenderer;

public class StacktraceRenderer extends DefaultTableCellRenderer {
	private static final long serialVersionUID = 542257145110345008L;
	private String[] value;
	
	@Override
	public String getText() {
		if (value == null)
			return null;
		
		StringBuilder sb = new StringBuilder(); 
//		sb.append("<html>");
		for (int i = 0; i < value.length; i++) {
		  if (i != 0) {
		    sb.append("\n");
		  }
		  sb.append(value[i]);
		}
//		sb.append("</html>");
		return sb.toString();
	}
	
	@Override
	protected void setValue(Object value) {
	  
		if (! (value instanceof String[] || value == null))
			throw new IllegalArgumentException("value is not a string[]");
		this.value = (String[]) value;
		super.setValue(value);
	}
	
}
