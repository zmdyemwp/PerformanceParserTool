package com.fihtdc.PerformanceParser.chart;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class XYDataSet {

    private final List<XYData<Double, Double>> mXY = new ArrayList<XYData<Double, Double>>() {
        public boolean add(XYData<Double, Double> mt) {
            int index = Collections.binarySearch(this, mt);
            if (index < 0)
                index = ~index;
            super.add(index, mt);
            return true;
        }
    };

    private final List<XYArrayData<Double, List<Integer>>> mXArrayY = new ArrayList<XYArrayData<Double, List<Integer>>>();

    private String mTitle;
    private String mStyle;
    private String mSeat;
    private Color mColor;

    public XYDataSet(String title, Color color, String style, String seat) {
        mTitle = title;
        mColor = color;
        mStyle = style;
        mSeat = seat;
    }

    public synchronized void add(double x, double y) {
        XYData<Double, Double> xyData = new XYData<Double, Double>(x, y);
        mXY.add(xyData);
    }

    public synchronized void add(double x, Integer[] y) {
        XYArrayData<Double, List<Integer>> xyData = new XYArrayData<Double, List<Integer>>(x, Arrays.asList(y));
        mXArrayY.add(xyData);
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public Color getColor() {
        return mColor;
    }

    public void setColor(Color color) {
        this.mColor = color;
    }

    public String getStyle() {
        return mStyle;
    }

    public void setStyle(String style) {
        this.mStyle = style;
    }

    public String getSeat() {
        return mSeat;
    }

    public void setSeat(String seat) {
        this.mSeat = seat;
    }

    public synchronized double getX(int index) {
        return mXY.get(index).getX();
    }

    public synchronized double getY(int index) {
        return mXY.get(index).getY();
    }

    public synchronized int getItemCount() {
        return mXY.size();
    }

    public synchronized double getArrayX(int index) {
        return mXArrayY.get(index).getX();
    }

    public synchronized List<Integer> getArrayY(int index) {
        return mXArrayY.get(index).getY();
    }
    public synchronized int getArrayItemCount() {
        return mXArrayY.size();
    }
}
