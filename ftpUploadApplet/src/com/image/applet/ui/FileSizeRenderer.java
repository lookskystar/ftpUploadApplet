package com.image.applet.ui;

import javax.swing.table.DefaultTableCellRenderer;

public class FileSizeRenderer extends DefaultTableCellRenderer {

	private static final long serialVersionUID = -8988206802370901229L;

	public FileSizeRenderer() {
		super();
		setHorizontalAlignment(RIGHT);
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.DefaultTableCellRenderer#setValue(java.lang.Object)
	 */
	@Override
	protected void setValue(Object value) {

		if (value instanceof Number) {
			long size = 0;
			size = (Long) value;
			
			double d = 0.0;
			
			if (size > 1024 * 1024 * 1024) { // > 1G
				d = size * 100 / 1024 / 1024 / 1024;
				d = d * 1.0 / 100;
				super.setValue(d + "G");
			} else if (size > 1024 * 1024) {
				d = size * 100 / 1024 / 1024;
				d = d * 1.0 / 100;
				super.setValue(d + "M");
			} else if (size > 1024) {
				d = size * 100 / 1024;
				d = d * 1.0 / 100;
				super.setValue(d + "K");
			} else {
				super.setValue(value + "B");
			}
		} else {
			super.setValue(value);
		}
	}
	
	public static void main(String[] args) {
		Object value = 123;
		System.out.println(value instanceof Number);
	}

}
