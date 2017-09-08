package com.fihtdc.PerformanceParser.chart;

public class XYData<X extends Comparable<X>, Y extends Comparable<Y>> implements Comparable<XYData<X, Y>> {
    private final X mX;
    private final Y mY;

    public XYData(X x, Y y) {
        this.mX = x;
        this.mY = y;
    }

    public X getX() {
        return mX;
    }

    public Y getY() {
        return mY;
    }

    @Override
    public int compareTo(XYData<X, Y> o) {
        return getX().compareTo(o.getX());
    }
}
