package com.fihtdc.PerformanceParser.chart;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

public class AppinfoTableModel extends AbstractTableModel {
    private String[] columnNames;
    private String[][] data;

    public AppinfoTableModel() {

    }

    public AppinfoTableModel(String names[], String[][] data) {
        this.columnNames=names;
        this.data = data;
    }
    public AppinfoTableModel(String names[], ArrayList<String[]> data) {
        this.columnNames=names;
        this.data = (String [][])data.toArray(new String[0][0]);
    }
    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public int getRowCount() {
        return data.length;
    }

    @Override
    public Object getValueAt(int row, int col) {
        return data[row][col];
    }

    @Override
    public String getColumnName(int col) {
        return columnNames[col];
    }

}