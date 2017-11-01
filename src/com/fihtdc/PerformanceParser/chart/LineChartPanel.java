package com.fihtdc.PerformanceParser.chart;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;
import java.util.Date;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.SymbolAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.StandardXYItemLabelGenerator;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.IntervalMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StackedXYAreaRenderer2;
import org.jfree.chart.renderer.xy.StackedXYBarRenderer;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYStepRenderer;
import org.jfree.data.gantt.Task;
import org.jfree.data.gantt.TaskSeries;
import org.jfree.data.gantt.TaskSeriesCollection;
import org.jfree.data.gantt.XYTaskDataset;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.Minute;
import org.jfree.data.time.SimpleTimePeriod;
import org.jfree.data.time.TimeTableXYDataset;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.TableXYDataset;
import org.jfree.ui.Layer;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;

import com.fihtdc.PerformanceParser.dataparser.AlogEventParser;
import com.fihtdc.PerformanceParser.utils.Const;


public class LineChartPanel {

    public static final int CATEGORY_MAIN = 0;
    public static final int FIRST_SERIES = 0;
    public static final int MAIN_PLOT_SCALE = 12;
    public static final int MODE_PLOT_SCALE = 14;
    public static final int MEM_PLOT_SCALE = 6;
    public static final int FRAME_DROP_SCALE = 6;
    
    private AlogEventParser mAlogEventParser;
    private List<XYDataSet> mXYMultiDataSet;
    private JPanel mJPanel;
    private JFreeChart mJFreeChart;
    private ChartPanel mChartPanel;
    private TimeTableXYDataset mTimeTableXYDataset;
    public IntervalMarker mIntervalMarker;
    private RectangleSelectedEvent mRectangleSelectedEvent = null;
    
    private boolean mDragStart = false;
    //public int topStartIndex = 0;
    //public int topEndIndex = 0;
    public int xStart = 0;
    public int xEnd = 0;
    public long startTime = 0l;
    public long endTime = 0l;

    public interface RectangleSelectedEvent {
        void onRectangleSelected(int startIndex, int endIndex);
        void onRectangleSelected(long startTime, long endTime);
    }

    public LineChartPanel(AlogEventParser alogEventParser, List<XYDataSet> mXYMultiDataSet) {
        this.mAlogEventParser = alogEventParser;
        this.mXYMultiDataSet = mXYMultiDataSet;
        initPanel();
    }

    
    
    public void initPanel() {
        mJFreeChart = createChart();
        mChartPanel = new ChartPanel(mJFreeChart);
        mChartPanel.setLayout(new BorderLayout());
        mChartPanel.setMouseWheelEnabled(true);
        mChartPanel.setPreferredSize(new Dimension(600, 450));
        mJPanel = mChartPanel;
    }


    public JFreeChart createChart() {
        // set X Axis time interval
        DateAxis mDateAxis = new DateAxis(Const.Panel.DATE_TIME);
        
        CombinedDomainXYPlot multiXYPlot = new CombinedDomainXYPlot();

        // CPU top line
        TableXYDataset topXYDataset = setTopXYDataset();
        // draw line chart
        XYPlot mainXYPlot = drawLineChart(topXYDataset);

        // status usage bar (Task Series)
        IntervalXYDataset intervalXYDataset = setTntervalXYDataset();
        // draw status usage chart
        XYPlot stackedXYPlot = drawStackedChart(intervalXYDataset);


        /* SCREEN_TOGGLED MinSMChien - Add Screen Toggled Plot */
        //  TODO: Draw Screen Toggled State
        //XYPlot screenToggledPlot = drawScreenToggled();
        /* END SCREEN_TOGGLED */
        /* Memory Information - MinSMChien - This will add a new char */
        XYPlot memInfoPlot = drawMemInfo();
        /* End Memory Information */
        
        XYPlot frameDropPlot = drawFrameDrop();


        multiXYPlot.setDomainAxis(mDateAxis);
        multiXYPlot.setDomainPannable(true);
        multiXYPlot.setRangePannable(false);
        multiXYPlot.add(mainXYPlot, MAIN_PLOT_SCALE);
        multiXYPlot.add(stackedXYPlot, MODE_PLOT_SCALE);
        /* SCREEN_TOGGLED MinSMChien - Add Screen Toggled Plot */
        //multiXYPlot.add(screenToggledPlot, MAIN_PLOT_SCALE);
        /* END SCREEN_TOGGLED */
        /* Memory Information - MinSMChien - This will add a new char */
        multiXYPlot.add(memInfoPlot, MEM_PLOT_SCALE);
        /* End Memory Information */
        /* Frame Drop Info - MinSMChien */
        multiXYPlot.add(frameDropPlot, FRAME_DROP_SCALE);
        /* End Frame Drop Info */
        
        multiXYPlot.setGap(1d);

        JFreeChart mJFreeChart = new JFreeChart(multiXYPlot);
        mJFreeChart.setBackgroundPaint(Color.WHITE);
        mJFreeChart.getLegend().setBackgroundPaint(Color.BLACK);
        mJFreeChart.getLegend().setItemPaint(Color.WHITE);
        mJFreeChart.getLegend().setItemFont(new Font("Arial", 0, 11));
        mJFreeChart.getLegend().setPosition(RectangleEdge.TOP);

        return mJFreeChart;
    }

    public TimeTableXYDataset setTopXYDataset() {
        mTimeTableXYDataset = new TimeTableXYDataset();
        try {
            for (XYDataSet mXYDataSet : mXYMultiDataSet) {
                if ( mXYDataSet.getSeat().equals(Const.LineSeat.MAIN)
                        && mXYDataSet.getTitle().equals(Const.LineTitles.CPU_TOP)
                        && 0 < mXYDataSet.getArrayItemCount()) {
                    boolean isCpuInfo = 5 == mXYDataSet.getArrayY(0).size();

                    for (int i = 0; i < mXYDataSet.getArrayItemCount(); i++) {
                        Date dataTime = new Date(((long) mXYDataSet.getArrayX(i) - 60000));    // move one minute backward
                        if(isCpuInfo) {
                            mTimeTableXYDataset.add(new Minute(dataTime),
                                    mXYDataSet.getArrayY(i).get(0), Const.LineTitles.TOP_SUB_IRQ);
                            mTimeTableXYDataset.add(new Minute(dataTime),
                                    mXYDataSet.getArrayY(i).get(1), Const.LineTitles.TOP_SUB_SOFT_IRQ);
                            mTimeTableXYDataset.add(new Minute(dataTime),
                                    mXYDataSet.getArrayY(i).get(2), Const.LineTitles.TOP_SUB_IOWAIT);
                            mTimeTableXYDataset.add(new Minute(dataTime),
                                    mXYDataSet.getArrayY(i).get(3), Const.LineTitles.TOP_SUB_SYSTEM_USAGE);
                            mTimeTableXYDataset.add(new Minute(dataTime),
                                    mXYDataSet.getArrayY(i).get(4), Const.LineTitles.TOP_SUB_USER_USAGE);
                        } else {
                            mTimeTableXYDataset.add(new Minute(dataTime),
                                    mXYDataSet.getArrayY(i).get(0), Const.LineTitles.TOP_SUB_IRQ);
                            mTimeTableXYDataset.add(new Minute(dataTime),
                                    mXYDataSet.getArrayY(i).get(1), Const.LineTitles.TOP_SUB_IOWAIT);
                            mTimeTableXYDataset.add(new Minute(dataTime),
                                    mXYDataSet.getArrayY(i).get(2), Const.LineTitles.TOP_SUB_SYSTEM_USAGE);
                            mTimeTableXYDataset.add(new Minute(dataTime),
                                    mXYDataSet.getArrayY(i).get(3), Const.LineTitles.TOP_SUB_USER_USAGE);
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(mJPanel, Const.Dialog.CPU_TOP
                    + Const.Dialog.ERROR, Const.Dialog.ERROR, JOptionPane.ERROR_MESSAGE);
        }
        return mTimeTableXYDataset;
    }


    private void debugmsg(String str) {
        System.out.println(str);
    }
    /* SCREEN_TOGGLED MinSMChien - Add Screen Toggled Plot */
    public TimeTableXYDataset setScreenXYDataset() {
        TimeTableXYDataset screenXYDataset = new TimeTableXYDataset();
        try {
            for (XYDataSet mXYDataSet:mXYMultiDataSet) {
                if (mXYDataSet.getSeat().equals(Const.LineSeat.MAIN) && mXYDataSet.getTitle().equals(Const.LineTitles.SCREEN_TOGGLED)) {
                    for( int i = 0; i < mXYDataSet.getArrayItemCount(); i++) {
                        Date dataTime = new Date(((long) mXYDataSet.getArrayX(i)));
                        screenXYDataset.add(new Millisecond(dataTime),
                                mXYDataSet.getArrayY(i).get(0), Const.LineTitles.SCREEN_TOGGLED);
                    }
                    debugmsg("setScreenXYDataset()::Screen Toggled: " + screenXYDataset.getItemCount());
                } else {
                    debugmsg("The Title: " + mXYDataSet.getTitle());
                }
            }
        } catch(Exception ex) {
            ex.printStackTrace();
        }
        return screenXYDataset;
    }
    /*
    public XYPlot drawScreenToggled() {
        XYPlot mXYPlot = new XYPlot();
        int YAxisCount = 0;
        
        TimeTableXYDataset screenXYDataset = new TimeTableXYDataset();
        try {
            for (XYDataSet mXYDataSet:mXYMultiDataSet) {
                if (mXYDataSet.getSeat().equals(Const.LineSeat.MAIN) && mXYDataSet.getTitle().equals(Const.LineTitles.SCREEN_TOGGLED)) {
                    for( int i = 0; i < mXYDataSet.getArrayItemCount(); i++) {
                        Date dataTime = new Date(((long) mXYDataSet.getArrayX(i)));
                        screenXYDataset.add(new Millisecond(dataTime),
                                mXYDataSet.getArrayY(i).get(0), Const.LineTitles.SCREEN_TOGGLED);
                    }
                }
            }
        } catch(Exception ex) {
            ex.printStackTrace();
        }
        
        XYStepRenderer localXYStepRenderer = new XYStepRenderer();
        localXYStepRenderer.setSeriesStroke(0, new BasicStroke(2.0F));
        localXYStepRenderer.setSeriesStroke(1, new BasicStroke(2.0F));
        localXYStepRenderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator());
        localXYStepRenderer.setDefaultEntityRadius(6);
        localXYStepRenderer.setBaseItemLabelGenerator(new StandardXYItemLabelGenerator());
        localXYStepRenderer.setBaseItemLabelsVisible(true);
        localXYStepRenderer.setBaseItemLabelFont(new Font("Dialog", 1, 14));

        NumberAxis screenAxis = new NumberAxis();
        screenAxis.setAutoRange(true);
        screenAxis.setAutoRangeIncludesZero(true);

        mXYPlot.setDataset(YAxisCount, screenXYDataset);
        mXYPlot.setRenderer(YAxisCount, localXYStepRenderer);
        mXYPlot.setRangeAxis(YAxisCount, screenAxis);
        mXYPlot.mapDatasetToRangeAxis(YAxisCount, YAxisCount);
        
        return mXYPlot;
    }
    */
    /* END SCREEN_TOGGLED */




    public XYPlot drawFrameDrop() {
        XYPlot mXYPlot = new XYPlot();
        int YAxisCount = 0;

        TimeTableXYDataset frameDropXYDataset = new TimeTableXYDataset();
        try {
            for (XYDataSet mXYDataSet:mXYMultiDataSet) {
                if (mXYDataSet.getSeat().equals(Const.LineSeat.MAIN) && mXYDataSet.getTitle().equals(Const.LineTitles.FRAME_DROP)) {
                    for( int i = 0; i < mXYDataSet.getArrayItemCount(); i++) {
                        Date dataTime = new Date(((long) mXYDataSet.getArrayX(i)));
                        frameDropXYDataset.add(new Millisecond(dataTime),
                                mXYDataSet.getArrayY(i).get(0), Const.LineTitles.FRAME_DROP);
                    }
                }
            }
        } catch(Exception ex) {
            ex.printStackTrace();
        }
        
        
        //NumberAxis topAxis = new JNumberAxis(Const.Panel.TOP_AXIS_TITLE, Const.Axis.MIN_RANGE_Y, Const.Axis.MAX_RANGE_Y);
        NumberAxis frameDropAxis = new NumberAxis();
        frameDropAxis.setAutoRange(true);
        frameDropAxis.setAutoRangeIncludesZero(true);
        frameDropAxis.setTickUnit(new NumberTickUnit(50));

        StackedXYBarRenderer frameDropRenderer = new StackedXYBarRenderer();
        frameDropRenderer.setDrawBarOutline(false);
        frameDropRenderer.setBaseItemLabelsVisible(true);
        frameDropRenderer.setBaseItemLabelGenerator(new StandardXYItemLabelGenerator());
        frameDropRenderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator());
        frameDropRenderer.setShadowVisible(false);
        frameDropRenderer.setBarPainter(new StandardXYBarPainter());

        mXYPlot.setDataset(YAxisCount, frameDropXYDataset);
        mXYPlot.setRenderer(YAxisCount, frameDropRenderer);
        mXYPlot.setRangeAxis(YAxisCount, frameDropAxis);
        mXYPlot.mapDatasetToRangeAxis(YAxisCount, YAxisCount);
        mXYPlot.setDomainGridlinesVisible(true);
        mXYPlot.setBackgroundPaint(Color.BLACK);
        mXYPlot.setRangeGridlinePaint(Color.lightGray);
        mXYPlot.setDomainGridlinePaint(Color.lightGray);
        
        return mXYPlot;
    }






    /* Memory Information - MinSMChien */
    public XYPlot drawMemInfo() {
        XYPlot mXYPlot = new XYPlot();
        int YAxisCount = 0;

        TimeTableXYDataset memInfoXYDataset = new TimeTableXYDataset();
        try {
            for (XYDataSet mXYDataSet:mXYMultiDataSet) {
                if (mXYDataSet.getSeat().equals(Const.LineSeat.MAIN) && mXYDataSet.getTitle().equals(Const.LineTitles.MEMINFO)) {
                    for( int i = 0; i < mXYDataSet.getArrayItemCount(); i++) {
                        Date dataTime = new Date(((long) mXYDataSet.getArrayX(i)));
                        memInfoXYDataset.add(new Minute(dataTime),
                                mXYDataSet.getArrayY(i).get(0), Const.LineTitles.MEMINFO_ZRAM);
                        memInfoXYDataset.add(new Minute(dataTime),
                                mXYDataSet.getArrayY(i).get(1), Const.LineTitles.MEMINFO_KERNEL);
                        memInfoXYDataset.add(new Minute(dataTime),
                                mXYDataSet.getArrayY(i).get(2), Const.LineTitles.MEMINFO_NATIVE);
                        memInfoXYDataset.add(new Minute(dataTime),
                                mXYDataSet.getArrayY(i).get(3), Const.LineTitles.MEMINFO_CACHED);
                        memInfoXYDataset.add(new Minute(dataTime),
                                mXYDataSet.getArrayY(i).get(4), Const.LineTitles.MEMINFO_FREE);
                    }
                }
            }
        } catch(Exception ex) {
            ex.printStackTrace();
        }
        
        /*      Stacked Area
        StackedXYAreaRenderer2 memInfoRenderer = new StackedXYAreaRenderer2(new StandardXYToolTipGenerator(), null);
        memInfoRenderer.setOutline(true);
        memInfoRenderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator());
        memInfoRenderer.setBaseItemLabelsVisible(true);
        memInfoRenderer.setBaseItemLabelGenerator(new StandardXYItemLabelGenerator());
        */

        /*
        TimeTableXYDataset localTimeTableXYDataset = new TimeTableXYDataset();
        try {
            for (XYDataSet mXYDataSet:mXYMultiDataSet) {
                if (mXYDataSet.getSeat().equals(Const.LineSeat.MAIN) && mXYDataSet.getTitle().equals(Const.LineTitles.MEMINFO)) {
                    for( int i = 0; i < mXYDataSet.getArrayItemCount(); i++) {
                        Date dataTime = new Date(((long) mXYDataSet.getArrayX(i)));
                        
                        localTimeTableXYDataset.add(new Minute(dataTime), mXYDataSet.getArrayY(i).get(0), "ZRAM");
                        localTimeTableXYDataset.add(new Minute(dataTime), mXYDataSet.getArrayY(i).get(1), "KERNEL");
                        localTimeTableXYDataset.add(new Minute(dataTime), mXYDataSet.getArrayY(i).get(2), "NATIVE");
                        localTimeTableXYDataset.add(new Minute(dataTime), mXYDataSet.getArrayY(i).get(3), "CACHED");
                        localTimeTableXYDataset.add(new Minute(dataTime), mXYDataSet.getArrayY(i).get(4), "FREE");

                    }
                }
            }
        } catch(Exception ex) {
            ex.printStackTrace();
        }
        */
        
        /* Stacked Bar */
        StackedXYBarRenderer memInfoRenderer = new StackedXYBarRenderer();
        memInfoRenderer.setDrawBarOutline(false);
        memInfoRenderer.setBaseItemLabelsVisible(true);
        memInfoRenderer.setBaseItemLabelGenerator(new StandardXYItemLabelGenerator());
        memInfoRenderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator());
        memInfoRenderer.setBarPainter(new StandardXYBarPainter());
        memInfoRenderer.setShadowVisible(false);
        

        NumberAxis memInfoAxis = new NumberAxis();
        memInfoAxis.setAutoRange(true);
        memInfoAxis.setAutoRangeIncludesZero(true);

        mXYPlot.setDataset(YAxisCount, memInfoXYDataset);
        mXYPlot.setRenderer(YAxisCount, memInfoRenderer);
        mXYPlot.setRangeAxis(YAxisCount, memInfoAxis);
        mXYPlot.setDomainGridlinesVisible(true);
        //mXYPlot.setDomainMinorGridlinesVisible(true);
        mXYPlot.setBackgroundPaint(Color.BLACK);
        mXYPlot.setRangeGridlinePaint(Color.lightGray);
        mXYPlot.setDomainGridlinePaint(Color.lightGray);
        mXYPlot.mapDatasetToRangeAxis(YAxisCount, YAxisCount);
        
        return mXYPlot;
    }



    public XYPlot drawLineChart(TableXYDataset topXYDataset) {
        XYPlot mXYPlot = new XYPlot();
        int YAxisCount = 0;


        /* Screen Toggled Line Chart */
            TableXYDataset screenDataset = setScreenXYDataset();
            XYStepRenderer screenXYStepRenderer = new XYStepRenderer();
            screenXYStepRenderer.setSeriesStroke(0, new BasicStroke(2.0F));
            //screenXYStepRenderer.setSeriesStroke(1, new BasicStroke(2.0F));
            screenXYStepRenderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator());
            screenXYStepRenderer.setDefaultEntityRadius(6);
            screenXYStepRenderer.setBaseItemLabelGenerator(new StandardXYItemLabelGenerator());
            screenXYStepRenderer.setBaseItemLabelsVisible(true);
            screenXYStepRenderer.setBaseItemLabelFont(new Font("Dialog", 1, 14));
            //screenXYStepRenderer.setBaseItemLabelPaint(paint);
            NumberAxis screenAxis = new NumberAxis();
            screenAxis.setAutoRange(true);
            screenAxis.setAutoRangeIncludesZero(true);
            screenAxis.setTickUnit(new NumberTickUnit(1));
            //screenAxis.setRange(-0.1, 5.0);

            mXYPlot.setDataset(YAxisCount, screenDataset);
            mXYPlot.setRenderer(YAxisCount, screenXYStepRenderer);
            mXYPlot.setRangeAxis(YAxisCount, screenAxis);
            mXYPlot.mapDatasetToRangeAxis(YAxisCount, YAxisCount);
            mXYPlot.setDomainGridlinesVisible(true);
            mXYPlot.setDomainMinorGridlinesVisible(true);
            mXYPlot.setBackgroundPaint(Color.BLACK);
            mXYPlot.setRangeGridlinePaint(Color.lightGray);
            mXYPlot.setDomainGridlinePaint(Color.lightGray);
            mXYPlot.setRangeAxisLocation(AxisLocation.BOTTOM_OR_LEFT);
            mXYPlot.setAxisOffset(new RectangleInsets(0, 5d, 0, 5d));
            mXYPlot.setDomainCrosshairVisible(true);
        /* End Screen Toggled */


        YAxisCount++;


        //NumberAxis topAxis = new JNumberAxis(Const.Panel.TOP_AXIS_TITLE, Const.Axis.MIN_RANGE_Y, Const.Axis.MAX_RANGE_Y);
        NumberAxis topAxis = new NumberAxis();
        topAxis.setAutoRange(true);
        topAxis.setAutoRangeIncludesZero(true);
        //topAxis.setRange(Const.Axis.MIN_RANGE_Y, Const.Axis.MAX_RANGE_Y);
        topAxis.setTickUnit(new NumberTickUnit(10));

        /*
        StackedXYAreaRenderer2 topRenderer = new StackedXYAreaRenderer2(new StandardXYToolTipGenerator(), null);
        topRenderer.setOutline(true);
        topRenderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator());
        topRenderer.setBaseItemLabelsVisible(true);
        topRenderer.setBaseItemLabelGenerator(new StandardXYItemLabelGenerator());
        */
        StackedXYBarRenderer topRenderer = new StackedXYBarRenderer();
        topRenderer.setDrawBarOutline(false);
        topRenderer.setBaseItemLabelsVisible(true);
        topRenderer.setBaseItemLabelGenerator(new StandardXYItemLabelGenerator());
        topRenderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator());
        topRenderer.setShadowVisible(false);
        topRenderer.setBarPainter(new StandardXYBarPainter());
        //StackedXYBarRenderer topRenderer = new StackedXYBarRenderer();
        //topRenderer.setDrawBarOutline(false);
        //topRenderer.setBasePositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12, TextAnchor.BOTTOM_CENTER));
        //topRenderer.setBarPainter(new StandardXYBarPainter());
        //topRenderer.setShadowVisible(false);
        //topRenderer.setSeriesPaint(0, Const.PPColors.LINE_CPU_TOP);

        mXYPlot.setDataset(YAxisCount, topXYDataset);
        mXYPlot.setRenderer(YAxisCount, topRenderer);
        mXYPlot.setRangeAxis(YAxisCount, topAxis);
        mXYPlot.mapDatasetToRangeAxis(YAxisCount, YAxisCount);
        

        return mXYPlot;
    }


    public IntervalXYDataset setTntervalXYDataset() {
        TaskSeriesCollection dataset = new TaskSeriesCollection();
        for (XYDataSet mXYDataSet : mXYMultiDataSet) {
            if (mXYDataSet.getSeat().equals(Const.LineSeat.SUB)) {
                TaskSeries mTaskSeries = new TaskSeries(mXYDataSet.getTitle());
                long start = mAlogEventParser.getStartTime();
                long end = mAlogEventParser.getEndTime();
                
                if(mXYDataSet.getTitle().equals(Const.LineTitles.FOCUSED)) {
                    for (int i = 0; i <= mXYDataSet.getItemCount(); i++) {
                        if(i == mXYDataSet.getItemCount()) {
                            end = mAlogEventParser.getEndTime();
                            Task task = new Task("", new SimpleTimePeriod(start, end));
                            mTaskSeries.add(task);
                        } else {
                            long itemTime = (long) mXYDataSet.getX(i);
                            if(i == 0) {
                                /*
                                if(start < itemTime) {
                                    end = itemTime - 100;
                                    Task task = new Task("", new SimpleTimePeriod(start, end));
                                    mTaskSeries.add(task);
                                }
                                */
                                start = itemTime;
                            } else {
                                end = itemTime - 100;
                                if(start >= end) {
                                    start = itemTime;
                                    continue;
                                }
                                Task task = new Task("", new SimpleTimePeriod(start, end));
                                mTaskSeries.add(task);
                                start = itemTime;
                            }
                        }
                    }
                } else {
                    for (int i = 0; i < mXYDataSet.getItemCount(); i++) {
                        Task task = new Task("", new SimpleTimePeriod(
                                (long) mXYDataSet.getX(i), (long) mXYDataSet.getX(i) + 100));
                        
                        mTaskSeries.add(task);
                    }
                }
                dataset.add(mTaskSeries);
            }
        }

        XYTaskDataset mXYTaskDataset = new XYTaskDataset(dataset);
        mXYTaskDataset.setTransposed(true);
        mXYTaskDataset.setSeriesWidth(0.5d);
        return mXYTaskDataset;
    }

    public XYPlot drawStackedChart(IntervalXYDataset mXYDataset) {
        XYPlot mXYPlot = new XYPlot();

        String[] modeInfoTitle = new String[] {Const.LineTitles.FOCUSED, Const.LineTitles.PROC_START,
                Const.LineTitles.PROC_DIED, Const.LineTitles.LMK, Const.LineTitles.CRASH, Const.LineTitles.ANR};

        SymbolAxis yAxis = new SymbolAxis("", modeInfoTitle);
        yAxis.setGridBandsVisible(false);
        yAxis.setAxisLineVisible(false);
        yAxis.setTickLabelFont(new Font(Font.DIALOG, Font.BOLD, 10));
        XYBarRenderer renderer = new XYBarRenderer();

        renderer.setShadowVisible(false);
        renderer.setUseYInterval(true);
        renderer.setBaseSeriesVisibleInLegend(false);
        
        int index = 0;
        for (XYDataSet mXYDataSet : mXYMultiDataSet) {
            if(mXYDataSet.getSeat().equals(Const.LineSeat.SUB)) {
                debugmsg("[drawStackedChart]" + mXYDataSet.getTitle());
                renderer.setSeriesPaint(index, mXYDataSet.getColor());
                index++;
            }
        }

        renderer.setBaseItemLabelFont(new Font(Font.DIALOG, Font.BOLD, 30));
        renderer.setBarPainter(new StandardXYBarPainter());

        mXYPlot.setDataset(mXYDataset);
        mXYPlot.setRangeAxis(yAxis);
        mXYPlot.setRenderer(renderer);

        mXYPlot.setDomainGridlinesVisible(true);
        mXYPlot.setDomainMinorGridlinesVisible(true);

        mXYPlot.setBackgroundPaint(Color.BLACK);
        mXYPlot.setRangeGridlinePaint(Color.lightGray);
        mXYPlot.setDomainGridlinePaint(Color.lightGray);
        mXYPlot.setAxisOffset(new RectangleInsets(0, 5d, 0, 5d));

        return mXYPlot;
    }

    public MouseListener mMouseListener = new MouseListener() {
        @Override
        public void mousePressed(MouseEvent arg0) {
            //topStartIndex = dateTimeToIndex(mTimeTableXYDataset, xPosToDateTime(arg0.getX()));
            startTime = xPosToDateTime(arg0.getX());
            mDragStart = true;
            if (mIntervalMarker != null) {
                CombinedDomainXYPlot cmplot = (CombinedDomainXYPlot) mJFreeChart.getPlot();
                List<XYPlot> plot = cmplot.getSubplots();
                plot.get(CATEGORY_MAIN).removeDomainMarker(CATEGORY_MAIN, mIntervalMarker, Layer.FOREGROUND);
                mIntervalMarker = null;
            }
        }

        @Override
        public void mouseReleased(MouseEvent arg0) {
            mDragStart = false;
            //topEndIndex = dateTimeToIndex(mTimeTableXYDataset, xPosToDateTime(arg0.getX()));
            endTime = xPosToDateTime(arg0.getX());
            if (mRectangleSelectedEvent != null)
                mRectangleSelectedEvent.onRectangleSelected(startTime, endTime);
        }

        @Override
        public void mouseExited(MouseEvent arg0) {
        }

        @Override
        public void mouseEntered(MouseEvent arg0) {
        }

        @Override
        public void mouseClicked(MouseEvent arg0) {
        }
    };

    public MouseMotionListener mMouseMotionListener = new MouseMotionListener() {
        @Override
        public void mouseDragged(MouseEvent arg0) {
            if (mDragStart) {
                //topEndIndex = dateTimeToIndex(mTimeTableXYDataset, xPosToDateTime(arg0.getX()));
                endTime = xPosToDateTime(arg0.getX());
                if (mIntervalMarker == null) {
                    if (Math.abs(endTime - startTime) > 0) {
                        //double start = mTimeTableXYDataset.getXValue(FIRST_SERIES, topStartIndex);
                        //double end = mTimeTableXYDataset.getXValue(FIRST_SERIES, topEndIndex);
                        mIntervalMarker = new IntervalMarker(startTime, endTime, Const.PPColors.RECTANGLE);
                        mIntervalMarker.setAlpha(0.5f);
                        CombinedDomainXYPlot cmplot = (CombinedDomainXYPlot) mJFreeChart.getPlot();
                        List<XYPlot> plot = cmplot.getSubplots();
                        plot.get(CATEGORY_MAIN).addDomainMarker(CATEGORY_MAIN, mIntervalMarker, Layer.FOREGROUND);
                    }
                } else {
                    mIntervalMarker.setEndValue(endTime);
                }
            }
        }

        @Override
        public void mouseMoved(MouseEvent arg0) {
        }
    };


    public long xPosToDateTime(int valueX) {
        XYPlot plot = (XYPlot) mJFreeChart.getPlot();
        ChartRenderingInfo info = mChartPanel.getChartRenderingInfo();
        Rectangle2D dataArea = info.getPlotInfo().getDataArea();
        ValueAxis domainAxis = plot.getDomainAxis();
        RectangleEdge domainAxisEdge = plot.getDomainAxisEdge();

        double chartX = domainAxis.java2DToValue(valueX, dataArea, domainAxisEdge);
        Date dataTime = new Date(((long) chartX));
        return dataTime.getTime();
    }

    public int dateTimeToIndex(TimeTableXYDataset collection, double timeX) {
        int index = 0;
        int[] surround = getSurroundingItems(FIRST_SERIES, (long) timeX);
        if (surround[0] == -1) {
            index = surround[1];
        } else if (surround[1] == -1) {
            index = surround[0];
        } else {
            index = (Math.abs(timeX - collection.getXValue(FIRST_SERIES, surround[0]))) < Math
                    .abs((timeX - collection.getXValue(FIRST_SERIES, surround[1]))) ? surround[0] : surround[1];
        }

        return index;
    }

    public void moveMarker(String bar, int offset) {
//        if (mIntervalMarker != null) {
//            if (bar.equals(Const.ItemCMD.LEFT_OF_FRAME)) {
//                if ((topStartIndex + offset) < topEndIndex && (topStartIndex + offset) > -1) {
//                    topStartIndex += offset;
//
//                    mIntervalMarker.setStartValue(mTimeTableXYDataset.getXValue(FIRST_SERIES, topStartIndex));
//                }
//            } else if (bar.equals(Const.ItemCMD.RIGHT_OF_FRAME)) {
//                if ((topEndIndex + offset) > topStartIndex && (topEndIndex + offset) < mTimeTableXYDataset.getItemCount(0)) {
//                    topEndIndex += offset;
//
//                    mIntervalMarker.setEndValue(mTimeTableXYDataset.getXValue(FIRST_SERIES, topEndIndex));
//                }
//            }
//            
//            if (mRectangleSelectedEvent != null) {
//                mRectangleSelectedEvent.onRectangleSelected(topStartIndex, topEndIndex);
//            }
//        }
    }

    public JPanel getJPanel() {
        return mJPanel;
    }

    public void setChartZoomable(boolean enable) {
        mChartPanel.setMouseZoomable(enable);
        mChartPanel.setMouseWheelEnabled(enable);
        if (enable) {
            mChartPanel.removeMouseListener(mMouseListener);
            mChartPanel.removeMouseMotionListener(mMouseMotionListener);
            mChartPanel.setRangeZoomable(false);
            if (mIntervalMarker != null) {
                CombinedDomainXYPlot cmplot = (CombinedDomainXYPlot) mJFreeChart.getPlot();
                List<XYPlot> plot = cmplot.getSubplots();
                plot.get(CATEGORY_MAIN).removeDomainMarker(CATEGORY_MAIN, mIntervalMarker, Layer.FOREGROUND);
                mIntervalMarker = null;
            }
        } else {
            mChartPanel.addMouseListener(mMouseListener);
            mChartPanel.addMouseMotionListener(mMouseMotionListener);
        }
    }

    public void clear() {
        mXYMultiDataSet.clear();
    }

    public void setRectangleSelectedEvent(RectangleSelectedEvent event) {
        mRectangleSelectedEvent = event;
    }
    
    public int[] getSurroundingItems(int series, long milliseconds) {
        int[] result = new int[] {-1, -1};
        if(mTimeTableXYDataset != null) {
            for (int i = 0; i < mTimeTableXYDataset.getItemCount(); i++) {
                Number x = mTimeTableXYDataset.getX(series, i);
                long m = x.longValue();
                if (m <= milliseconds) {
                    result[0] = i;
                }
                if (m >= milliseconds) {
                    result[1] = i;
                    break;
                }
            }
        }
        return result;
    }
}