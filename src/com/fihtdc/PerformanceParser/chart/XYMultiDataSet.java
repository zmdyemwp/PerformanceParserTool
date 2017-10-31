package com.fihtdc.PerformanceParser.chart;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fihtdc.PerformanceParser.dataparser.AlogEventParser;
import com.fihtdc.PerformanceParser.dataparser.AlogEventParser.ActivityFocused;
import com.fihtdc.PerformanceParser.dataparser.AlogEventParser.CPUInfo;
import com.fihtdc.PerformanceParser.dataparser.AlogEventParser.Kill;
import com.fihtdc.PerformanceParser.dataparser.AlogEventParser.LMK;
import com.fihtdc.PerformanceParser.dataparser.AlogEventParser.MemInfo;
import com.fihtdc.PerformanceParser.dataparser.AlogEventParser.ProcDied;
import com.fihtdc.PerformanceParser.dataparser.AlogEventParser.ProcStart;
import com.fihtdc.PerformanceParser.dataparser.AlogEventParser.Resume;
import com.fihtdc.PerformanceParser.dataparser.AlogEventParser.ScreenToggled;
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
                    Const.LineStyle.SOLID, Const.LineSeat.SUB),

            /* Screen Toggled State */
            new DataSetElement(Const.LineTitles.SCREEN_TOGGLED, Const.PPColors.SCREEN_TOGGLED,
                    Const.LineStyle.SOLID, Const.LineSeat.MAIN),

            /* Memory Information */
            new DataSetElement(Const.LineTitles.MEMINFO, Const.PPColors.LINE_MEM_INFO,
                    Const.LineStyle.SOLID, Const.LineSeat.MAIN),
            
            
            /* LMK */
            new DataSetElement(Const.LineTitles.LMK, Const.PPColors.TASK_LMK,
                    Const.LineStyle.SOLID, Const.LineSeat.SUB),
            
            
        };
    }

    public void setXYMultiDataSet() {
        for (int i=0; i < mDataSetElement.length; i++) {
            XYDataSet xyDataSet = new XYDataSet(mDataSetElement[i].title, mDataSetElement[i].color,
                    mDataSetElement[i].style, mDataSetElement[i].seat);

            
            switch(mDataSetElement[i].getTitke()) {
                case Const.LineTitles.CPU_TOP:
                    ArrayList<Top> tops = mAlogEventParser.getTop(Long.MIN_VALUE, Long.MAX_VALUE);
                    ArrayList<CPUInfo> cpus = mAlogEventParser.getCPUInfo(Long.MIN_VALUE, Long.MAX_VALUE);
                    if(0 < cpus.size()) {
                        for(int j = 0; j < cpus.size(); j++) {
                            Integer[] arrayY = new Integer[5];
                            arrayY[0] = cpus.get(j).getIRQ();
                            arrayY[1] = cpus.get(j).getSoftIRQ();
                            arrayY[2] = cpus.get(j).getIOWait();
                            arrayY[3] = cpus.get(j).getSystem();
                            arrayY[4] = cpus.get(j).getUser();
                            xyDataSet.add(cpus.get(j).getTime(), arrayY);
                        }
                    } else {
                        for(int j = 0; j < tops.size(); j++) {
                            Integer[] arrayY = new Integer[4];
                            arrayY[0] = tops.get(j).getIRQ();
                            arrayY[1] = tops.get(j).getIOWait();
                            arrayY[2] = tops.get(j).getSystemUsage();
                            arrayY[3] = tops.get(j).getUserUsage();
                            xyDataSet.add(tops.get(j).getTime(), arrayY);
                        }
                    }

                    break;

                case Const.LineTitles.MEMINFO:
                    ArrayList<MemInfo> meminfo = mAlogEventParser.getMemInfo(Long.MIN_VALUE, Long.MAX_VALUE);
                    for(int j = 0; j < meminfo.size(); j++) {
                        Integer[] arrayY = new Integer[5];
                        int MB = 1024 * 1024;
                        arrayY[0] = meminfo.get(j).getZramSize() / MB;
                        arrayY[1] = meminfo.get(j).getKernelSize() / MB;
                        arrayY[2] = meminfo.get(j).getNativeSize() / MB;
                        arrayY[3] = meminfo.get(j).getCachedSize() / MB;
                        arrayY[4] = meminfo.get(j).getFreeSize() / MB;
                        xyDataSet.add(meminfo.get(j).getTime(), arrayY);
                    }
                    break;


                case Const.LineTitles.SCREEN_TOGGLED:
                    ArrayList<ScreenToggled> screens = mAlogEventParser.getScreenToggled(Long.MIN_VALUE, Long.MAX_VALUE);
                    for(int j = 0; j < screens.size(); j++) {
                        xyDataSet.add(screens.get(j).getTime(), new Integer[] {screens.get(j).getScreenOnOff()});
                    }
                    System.out.println("Screen Toggled #:" + screens.size() + ":" + xyDataSet.getArrayItemCount());
                    break;


                case Const.LineTitles.FOCUSED:
                    ArrayList<ActivityFocused> focuseds = 
                        mAlogEventParser.getActivityFocused(Long.MIN_VALUE, Long.MAX_VALUE);
                    ArrayList<Resume> resumes =
                        mAlogEventParser.getResume(Long.MIN_VALUE, Long.MAX_VALUE);

                    if(resumes.size() > 0) {
                        for(int j = 0; j < resumes.size(); j++) {
                            xyDataSet.add(resumes.get(j).getTime(), 1);
                        }
                    } else {
                        for(int j = 0; j < focuseds.size(); j++) {
                            xyDataSet.add(focuseds.get(j).getTime(), 1);
                        }
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
                    
                case Const.LineTitles.LMK:
                    ArrayList<LMK> lmk = mAlogEventParser.getLMK(Long.MIN_VALUE, Long.MAX_VALUE);
                    for(int j = 0; j < lmk.size(); j++) {
                        xyDataSet.add(lmk.get(j).getTime(), 1);
                    }
                    break;
                    
                    
                default:
                    System.out.println("DateSet not found");
                    break;
            }
            
            mXYMultiDataSet.add(xyDataSet);
            debugmsg(String.format("[%s](%d) => %d", xyDataSet.getTitle(), xyDataSet.getItemCount() ,mXYMultiDataSet.size()));
        }
    }

    static void debugmsg(String str) {
        System.out.println(str);
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
