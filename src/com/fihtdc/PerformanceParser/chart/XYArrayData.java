package com.fihtdc.PerformanceParser.chart;

public class XYArrayData<X,Y> {
    private final X mX;
    private final Y mY;

    public XYArrayData(X x, Y y) {
        this.mX = x;
        this.mY = y;
    }

    public X getX() {
        return mX;
    }

    public Y getY() {
        return mY;
    }
}
