package com.image.applet.ui;

import java.io.File;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import com.image.applet.util.AppletParam;
import com.image.applet.util.FileCounter;

public class FileInfoTableModel extends AbstractTableModel {

	private static final long serialVersionUID = -5907430720433588853L;

	/**
	 * 是否开启数据输出
	 */
	private boolean DEBUG = true;

	/**
	 * 表格列字段名称
	 */
	private String[] columnNames = { "操作", "文件名", "类型", "图片数量", "视频数量" };

	/**
	 * 是否可修改
	 */
	private boolean[] editable = { true, false, false, false, false };

	private Object[][] NULLARRAY = new Object[0][];

	/**
	 * 表格中的数据
	 */
	private Object[][] data = NULLARRAY;
	
	public FileInfoTableModel() {
		super();
	}

	public FileInfoTableModel(String[] columnNames) {
		super();
		this.columnNames = columnNames;
	}

	/**
	 * 增加一条文件信息，成功返回true，失败返回false
	 * 
	 * @param file
	 * @return 
	 */
	public boolean addFile(File file) {
		if (file == null || fileExists(file) || !acceptFile(file)) {
			return false;
		}
		
		Object[][] temp = new Object[data.length + 1][];

		System.arraycopy(data, 0, temp, 0, data.length);

		// 构造一条上传文件的信息
		int index = 0; // 用索引，方便调整顺序
		Object[] info = new Object[6];
		info[index++] = false; // 状态，用于将列表中的状态为false的删除掉
		info[index++] = file.getName(); // 文件名
		info[index++] = file.isFile() ? "文件" : "目录";
		
		long[] count = FileCounter.countFile(file);
		info[index++] = count[0];
		info[index++] = count[1];

		info[index++] = file.getAbsolutePath(); // 本地绝对路径

		temp[data.length] = info;

		data = temp;
		fireTableDataChanged();
		return true;
	}
	
	/**
	 * 判断文件是否已存在
	 * @param file
	 * @return
	 */
	private boolean fileExists(File file) {
		boolean exists = false;
		for (Object[] d : data) {
			if (file.getAbsolutePath().equalsIgnoreCase((String)d[5])) {
				exists = true;
				break;
			}
		}
		return exists;
	}

	/**
	 * 判断是否接受此文件
	 * 
	 * @param file
	 * @return
	 */
	protected boolean acceptFile(File file) {
		
		boolean accept = false;

		AppletParam param = AppletParam.getInstance();

		if (file.isDirectory() && param.isDirectorySelectionEnabled) {
			accept = true;
		} else if (file.isFile() && param.isFileSelectionEnabled) {
			if (param.useFileExtension()) {
				String name = file.getName();
				int index = name.lastIndexOf(".");
				String suffix = "";
				if (index > 0) {
					suffix = name.substring(index + 1).toLowerCase();
					accept = param.getFileNameExtension().contains(suffix);
					accept = accept && file.length() < param.maxFileSize; // 判断文件是否超过最大限制
				}
			} else {
				accept = true;
			}
		}

		return accept;
	}

	/**
	 * 添加多个文件，返回实际满足要求的文件数量
	 * 
	 * @param files
	 * @return
	 */
	public int addFiles(List<File> files) {
		int count = 0;
		if (files != null && files.size() > 0) {
			for (File file : files) {
				if (addFile(file)) {
					count++;
				}
			}
		}
		return count;
	}

	/**
	 * 删除文件信息
	 * 
	 * @param index
	 */
	public void removeFile(int index) {
		if (index >= 0 && index < data.length) {
			Object[][] temp = new Object[data.length - 1][];
			System.arraycopy(data, 0, temp, 0, index);

			System.arraycopy(data, index + 1, temp, index, data.length - index
					- 1);

			data = temp;
			fireTableDataChanged();
		}
	}

	/**
	 * 若数组中的“操作”列对应的值是true时，则删除。
	 */
	public void removeFiles() {

		for (int i = data.length - 1; i >= 0; i--) {
			if ((Boolean) data[i][0]) {
				removeFile(i);
			}
		}

	}

	/**
	 * 删除给定索引的文件信息
	 * 
	 * @param indexes
	 */
	public void removeFiles(int[] indexes) {
		// 先从小到大排序
		for (int i = 0; i < indexes.length; i++) {
			for (int j = indexes.length - 1; j > i; j--) {
				if (indexes[j] < indexes[j - 1]) {
					swap(indexes, j, j - 1);
				}
			}
		}

		// 然后从大到小删除
		for (int i = indexes.length - 1; i >= 0; i--) {
			removeFile(i);
		}
	}

	/**
	 * 交换数组元素，排序使用
	 * 
	 * @param data
	 * @param i
	 * @param j
	 */
	public void swap(int[] data, int i, int j) {
		int temp = data[i];
		data[i] = data[j];
		data[j] = temp;
	}

	/**
	 * 是否有选中的数据
	 * 
	 * @return
	 */
	public boolean hasSelectedRow() {
		boolean state = false;
		for (int i = 0; i < data.length; i++) {
			if ((Boolean) data[i][0]) {
				state = true;
				break;
			}
		}
		return state;
	}

	public int getColumnCount() {
		return columnNames.length;
	}

	public int getRowCount() {
		return data.length;
	}

	public String getColumnName(int col) {
		return columnNames[col];
	}

	public Object getValueAt(int row, int col) {
		if (row != -1 && col != -1) {
			return data[row][col];
		}
		return null;
	}

	/*
	 * JTable uses this method to determine the default renderer/ editor for
	 * each cell. If we didn't implement this method, then the last column would
	 * contain text ("true"/"false"), rather than a check box.
	 */
	public Class<?> getColumnClass(int c) {
		return getValueAt(0, c).getClass();
	}

	/*
	 * Don't need to implement this method unless your table's editable.
	 */
	public boolean isCellEditable(int row, int col) {
		// Note that the data/cell address is constant,
		// no matter where the cell appears onscreen.
		return editable[col];
	}

	/*
	 * Don't need to implement this method unless your table's data can change.
	 */
	public void setValueAt(Object value, int row, int col) {
		if (row < data.length) {
			if (DEBUG) {
				System.out.println("Setting value at " + row + "," + col
						+ " to " + value + " (an instance of "
						+ value.getClass() + ")");
			}

			data[row][col] = value;
			fireTableCellUpdated(row, col);

			if (DEBUG) {
				System.out.println("New value of data:");
				printDebugData();
			}
		} else {
			fireTableDataChanged();
		}
	}

	private void printDebugData() {
		int numRows = getRowCount();
		int numCols = getColumnCount();

		for (int i = 0; i < numRows; i++) {
			System.out.print("    row " + i + ":");
			for (int j = 0; j < numCols; j++) {
				System.out.print("  " + data[i][j]);
			}
			System.out.println();
		}
		System.out.println("--------------------------");
	}

	/**
	 * @return the columnNames
	 */
	public String[] getColumnNames() {
		return columnNames;
	}

	/**
	 * @param columnNames
	 *            the columnNames to set
	 */
	public void setColumnNames(String[] columnNames) {
		this.columnNames = columnNames;
	}

	/**
	 * @return the data
	 */
	public Object[][] getData() {
		return data;
	}

}
