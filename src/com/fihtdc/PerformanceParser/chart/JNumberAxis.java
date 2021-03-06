package com.fihtdc.PerformanceParser.chart;

import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.ValueAxisPlot;
import org.jfree.data.Range;
import org.jfree.data.RangeType;

public class JNumberAxis extends NumberAxis {

    double maxAxis;
    double minAxis;

    private RangeType rangeType;

    public JNumberAxis(String title) {
       super(title);
    }

    public JNumberAxis(String title, double minAxis, double maxAxis) {
        super(title);
        this.minAxis = minAxis;
        this.maxAxis = maxAxis;
     }

    @Override
    protected void autoAdjustRange() {

        Plot plot = getPlot();
        if (plot == null) {
            return;  // no plot, no data
        }

        if (plot instanceof ValueAxisPlot) {
            ValueAxisPlot vap = (ValueAxisPlot) plot;

            Range r = vap.getDataRange(this);
            if (r == null) {
                r = getDefaultAutoRange();
            }

            //double upper = maxAxis;
            //double lower = minAxis;
            double upper = r.getUpperBound();
            double lower = r.getLowerBound();
            if (this.rangeType == RangeType.POSITIVE) {
                lower = Math.max(0.0, lower);
                upper = Math.max(0.0, upper);
            }
            else if (this.rangeType == RangeType.NEGATIVE) {
                lower = Math.min(0.0, lower);
                upper = Math.min(0.0, upper);
            }

            if (getAutoRangeIncludesZero()) {
                lower = Math.min(lower, 0.0);
                upper = Math.max(upper, 0.0);
            }
            double range = upper - lower;

            // if fixed auto range, then derive lower bound...
            double fixedAutoRange = getFixedAutoRange();
            if (fixedAutoRange > 0.0) {
                lower = upper - fixedAutoRange;
            }
            else {
                // ensure the autorange is at least <minRange> in size...
                double minRange = getAutoRangeMinimumSize();
                if (range < minRange) {
                    double expand = (minRange - range) / 2;
                    upper = upper + expand;
                    lower = lower - expand;
                    if (lower == upper) { // see bug report 1549218
                        double adjust = Math.abs(lower) / 10.0;
                        lower = lower - adjust;
                        upper = upper + adjust;
                    }
                    if (this.rangeType == RangeType.POSITIVE) {
                        if (lower < 0.0) {
                        upper = upper - lower;
                        lower = 0.0;
                        }
                    }
                    else if (this.rangeType == RangeType.NEGATIVE) {
                        if (upper > 0.0) {
                            lower = lower - upper;
                            upper = 0.0;
                        }
                    }
                }

                if (getAutoRangeStickyZero()) {
                    if (upper <= 0.0) {
                        upper = Math.min(0.0, upper + getUpperMargin() * range);
                    }
                    else {
                        upper = upper + getUpperMargin() * range;
                    }
                    if (lower >= 0.0) {
                        lower = Math.max(0.0, lower - getLowerMargin() * range);
                    }
                    else {
                        lower = lower - getLowerMargin() * range;
                    }
                }
                else {
                    upper = upper + getUpperMargin() * range;
                    lower = lower - getLowerMargin() * range;
                }
            }
            setRange(new Range(minAxis, maxAxis), false, false);
        }
    }
}
