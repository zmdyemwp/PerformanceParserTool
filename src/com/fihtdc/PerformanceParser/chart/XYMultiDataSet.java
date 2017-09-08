package com.fihtdc.PerformanceParser.chart;

import java.awt.Color;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jfree.chart.title.Title;
import org.jfree.util.Log;

import com.fihtdc.PerformanceParser.dataparser.AlogEventParser;
import com.fihtdc.PerformanceParser.dataparser.AlogEventParser.ActivityFocused;
import com.fihtdc.PerformanceParser.dataparser.AlogEventParser.Kill;
import com.fihtdc.PerformanceParser.dataparser.AlogEventParser.ProcDied;
import com.fihtdc.PerformanceParser.dataparser.AlogEventParser.ProcStart;
import com.fihtdc.PerformanceParser.dataparser.AlogEventParser.Top;
import com.fihtdc.PerformanceParser.utils.Const;

public class XYMultiDataSet {


    List<XYDataSet> mXYMultiDataSet = new ArrayList<XYDataSet>();
    /* data set elements */
    private DataSetElement[] mDataSetElement;
    private AlogEventParser mAlogEventParser;

    public XYMultiDataSet(AlogEventParser alogEventParser) {
        this.mAlogEventParser = alogEventParser;
        initDataSet();
        setXYMultiDataSet();
    }

    private void initDataSet(){
        mDataSetElement = new DataSetElement[] {
            /* cpu top line element */
            new DataSetElement(Const.LineTitles.CPU_TOP, Const.PPColors.LINE_CPU_TOP,
                    Const.LineStyle.SOLID, Const.LineSeat.MAIN),

            /* focused line element */
            new DataSetElement(Const.LineTitles.FOCUSED, Const.PPColors.TASK_FOCUSED,
                    Const.LineStyle.SOLID, Const.LineSeat.SUB),
            /* proc start locating line element */
            new DataSetElement(Const.LineTitles.PROC_START, Const.PPColors.TASK_PROC_START,
                    Const.LineStyle.SOLID, Const.LineSeat.SUB),
            /* proc died on line element */
            new DataSetElement(Const.LineTitles.PROC_DIED, Const.PPColors.TASK_PROC_DIED,
                    Const.LineStyle.SOLID, Const.LineSeat.SUB)
        };
    }

    public void setXYMultiDataSet() {
        for (int i=0; i < mDataSetElement.length; i++) {
            XYDataSet xyDataSet = new XYDataSet(mDataSetElement[i].title, mDataSetElement[i].color,
                    mDataSetElement[i].style, mDataSetElement[i].seat);

            
            switch(mDataSetElement[i].getTitke()) {
                case Const.LineTitles.CPU_TOP:
                    ArrayList<Top> tops = mAlogEventParser.getTop(Long.MIN_VALUE, Long.MAX_VALUE);
                    
                    for(int j = 0; j < tops.size(); j++) {
                        Integer[] arrayY = new Integer[4];
                        arrayY[0] = tops.get(j).getIRQ();
                        arrayY[1] = tops.get(j).getIOWait();
                        arrayY[2] = tops.get(j).getSystemUsage();
                        arrayY[3] = tops.get(j).getUserUsage();
                        xyDataSet.add(tops.get(j).getTime(), arrayY);
                    }

                    break;
                case Const.LineTitles.FOCUSED:
                    ArrayList<ActivityFocused> focuseds = 
                        mAlogEventParser.getActivityFocused(Long.MIN_VALUE, Long.MAX_VALUE);

                    for(int j = 0; j < focuseds.size(); j++) {
                        xyDataSet.add(focuseds.get(j).getTime(), 1);
                    }
                    
                    break;
                case Const.LineTitles.PROC_START:
                    ArrayList<ProcStart> procStarts = mAlogEventParser.getAmProcStart(Long.MIN_VALUE, Long.MAX_VALUE);
                    
                    for(int j = 0; j < procStarts.size(); j++) {
                        xyDataSet.add(procStarts.get(j).getTime(), 1);
                    }
                    
                    break;
                case Const.LineTitles.PROC_DIED:
                    ArrayList<ProcDied> procDieds = mAlogEventParser.getProcDied(Long.MIN_VALUE, Long.MAX_VALUE);
                    ArrayList<Kill> kills = mAlogEventParser.getKill(Long.MIN_VALUE, Long.MAX_VALUE);
                    
                    long[] tmes = new long[procDieds.size() + kills.size()];
                    
                    for(int j = 0; j < procDieds.size(); j++) {
                        tmes[j] = procDieds.get(j).getTime();
                    }

                    for(int j = procDieds.size(); j < kills.size() + procDieds.size(); j++) {
                        tmes[j] = kills.get(j - procDieds.size()).getTime();
                    }                
                    
                    Arrays.sort(tmes);

                    for(int j = 0; j < tmes.length; j++) {
                        xyDataSet.add(tmes[j], 1);
                    }
                    break;
                default:
                    System.out.println("DateSet not found");
                    break;
            }
            
            mXYMultiDataSet.add(xyDataSet);
        }
    }

    public List<XYDataSet> getXYMultiDataSet() {
        return this.mXYMultiDataSet;
    }
    
    public static class DataSetElement{
        public String title;
        public Color color;
        public String style;
        public String seat;

        public DataSetElement(String title, Color color, String style, String seat){
            this.title = title;
            this.color = color;
            this.style = style;
            this.seat = seat;
        }
        
        private String getTitke() {
            return title;
        }
    }
}
