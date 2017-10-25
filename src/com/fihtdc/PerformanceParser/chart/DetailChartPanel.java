package com.fihtdc.PerformanceParser.chart;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import com.fihtdc.PerformanceParser.dataparser.AlogEventParser;
import com.fihtdc.PerformanceParser.dataparser.AlogEventParser.ANR;
import com.fihtdc.PerformanceParser.dataparser.AlogEventParser.ActivityFocused;
import com.fihtdc.PerformanceParser.dataparser.AlogEventParser.ActivityLaunchTime;
import com.fihtdc.PerformanceParser.dataparser.AlogEventParser.BinderSample;
import com.fihtdc.PerformanceParser.dataparser.AlogEventParser.Crash;
import com.fihtdc.PerformanceParser.dataparser.AlogEventParser.FrameDrop;
import com.fihtdc.PerformanceParser.dataparser.AlogEventParser.Kill;
import com.fihtdc.PerformanceParser.dataparser.AlogEventParser.PSS;
import com.fihtdc.PerformanceParser.dataparser.AlogEventParser.ProcDied;
import com.fihtdc.PerformanceParser.dataparser.AlogEventParser.ProcStart;
import com.fihtdc.PerformanceParser.dataparser.AlogEventParser.ScreenToggled;
import com.fihtdc.PerformanceParser.dataparser.AlogEventParser.Top;
import com.fihtdc.PerformanceParser.utils.Const;

public class DetailChartPanel {

    private JPanel mJPanel;
    private JPanel mCPUTopPanel;
    private JPanel mProcStartPanel;
    private JPanel mFocusedPanel;
    private JPanel mProcDiedPanel;
    private JPanel mKillPanel;
    private JPanel mPSSPanel;
    private JPanel mLaunchTimePanel;

    private JPanel mScreenToggledPanel;
    private JPanel mBinderSamplePanel;
    
    private JPanel mFrameDropPanel;

    private JTable mCPUTopTable;
    private JTable mProcStartTable;
    private JTable mFocusedTable;
    private JTable mProcDiedTable;
    private JTable mKillTable;
    private JTable mPSSTable;
    private JTable mLaunchTimeTable;

    private JTable mScreenToggledTable;
    private JTable mBinderSampleTable;
    
    private JTable mFrameDropTable;

    private JTextArea mDetailInfo;
    private JTabbedPane mTabPane;

    SimpleDateFormat format = new SimpleDateFormat(
            Const.DateSet.DATE_FORMAT_STANDARD);
    private StringBuilder result = new StringBuilder();
    private AlogEventParser mAlogEventParser;

    private long mStartTime = Long.MIN_VALUE;
    private long mEndTime = Long.MIN_VALUE;

    public DetailChartPanel() {
    }

    public DetailChartPanel(AlogEventParser alogEventParser) {
        this.mAlogEventParser = alogEventParser;
        initPanel();
    }

    public void initPanel() {
        mJPanel = new JPanel();
        GridBagLayout gBL = new GridBagLayout();
        GridBagConstraints gBCs = new GridBagConstraints();
        mJPanel.setLayout(gBL);

        Border titleBorder = BorderFactory.createTitledBorder(null,
                Const.Panel.TITLE_ALOG_EVENTLOG_INFO, TitledBorder.LEFT, TitledBorder.TOP);
        mJPanel.setBorder(titleBorder);
        
        mDetailInfo = new JTextArea();
        mDetailInfo.setLineWrap(true);
        mDetailInfo.setEditable(false);
        mDetailInfo.setBackground(null);
        mJPanel.add(mDetailInfo);

        mTabPane = new JTabbedPane();
        mCPUTopPanel = new JPanel(new BorderLayout());
        mProcStartPanel = new JPanel(new BorderLayout());
        mFocusedPanel = new JPanel(new BorderLayout());
        mProcDiedPanel = new JPanel(new BorderLayout());
        mKillPanel = new JPanel(new BorderLayout());
        mPSSPanel = new JPanel(new BorderLayout());
        mLaunchTimePanel = new JPanel(new BorderLayout());

        mScreenToggledPanel = new JPanel(new BorderLayout());
        mBinderSamplePanel = new JPanel(new BorderLayout());

        mFrameDropPanel = new JPanel(new BorderLayout());




        mCPUTopTable = new JTable();
        mProcStartTable = new JTable();
        mFocusedTable = new JTable();
        mProcDiedTable = new JTable();
        mKillTable = new JTable();
        mPSSTable = new JTable();
        mLaunchTimeTable = new JTable();

        mScreenToggledTable = new JTable();
        mBinderSampleTable = new JTable();

        mFrameDropTable = new JTable();



        mCPUTopPanel.add(new JScrollPane(mCPUTopTable, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS));
        mProcStartPanel.add(new JScrollPane(mProcStartTable, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS));
        mFocusedPanel.add(new JScrollPane(mFocusedTable, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS));
        mProcDiedPanel.add(new JScrollPane(mProcDiedTable, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS));
        mKillPanel.add(new JScrollPane(mKillTable, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS));
        mPSSPanel.add(new JScrollPane(mPSSTable, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS));
        mLaunchTimePanel.add(new JScrollPane(mLaunchTimeTable, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS));

        mScreenToggledPanel.add(new JScrollPane(mScreenToggledTable, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS));
        mBinderSamplePanel.add(new JScrollPane(mBinderSampleTable, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS));

        mFrameDropPanel.add(new JScrollPane(mFrameDropTable, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS));


        mTabPane.addTab(Const.Panel.CPU_TOP_INFO, mCPUTopPanel);
        mTabPane.addTab(Const.Panel.PROC_START_INFO, mProcStartPanel);
        mTabPane.addTab(Const.Panel.FOCUSED_INFO, mFocusedPanel);
        mTabPane.addTab(Const.Panel.PROC_DIED_INFO, mProcDiedPanel);
        mTabPane.addTab(Const.Panel.KILL_INFO, mKillPanel);
        mTabPane.addTab(Const.Panel.PSS_INFO, mPSSPanel);
        mTabPane.addTab(Const.Panel.LAUNCHTIME_INFO, mLaunchTimePanel);

        mTabPane.addTab(Const.Panel.SCREEN_TOGGLED_INFO, mScreenToggledPanel);
        mTabPane.addTab(Const.Panel.BINDER_SAMPLE_INFO, mBinderSamplePanel);

        mTabPane.addTab(Const.Panel.FRAME_DROP, mFrameDropPanel);


        mJPanel.add(mTabPane);

        resetJPanel();

        gBCs.fill = GridBagConstraints.BOTH;
        gBCs.anchor = GridBagConstraints.NORTHWEST;
        gBCs.gridx = 0;
        gBCs.gridy = 0;
        gBCs.insets = new Insets(3, 5, 3, 5);
        gBL.setConstraints(mDetailInfo, gBCs);
        gBCs.gridx = 0;
        gBCs.gridy = 1;
        gBCs.weightx = 1;
        gBCs.weighty = 1;
        gBL.setConstraints(mTabPane, gBCs);
    }

    public void resetJPanel() {
        mStartTime = Long.MIN_VALUE;
        mEndTime = Long.MIN_VALUE;
        mDetailInfo.setText("");

        mDetailInfo.append(Const.Panel.TIME + ": ");
        
        mCPUTopTable.setModel(new AppinfoTableModel(new String[0], new String[0][0]));
        mProcStartTable.setModel(new AppinfoTableModel(new String[0], new String[0][0]));
        mFocusedTable.setModel(new AppinfoTableModel(new String[0], new String[0][0]));
        mProcDiedTable.setModel(new AppinfoTableModel(new String[0], new String[0][0]));
        mKillTable.setModel(new AppinfoTableModel(new String[0], new String[0][0]));
        mPSSTable.setModel(new AppinfoTableModel(new String[0], new String[0][0]));
        mLaunchTimeTable.setModel(new AppinfoTableModel(new String[0], new String[0][0]));
        mScreenToggledTable.setModel(new AppinfoTableModel(new String[0], new String[0][0]));
        mBinderSampleTable.setModel(new AppinfoTableModel(new String[0], new String[0][0]));
        
        mFrameDropTable.setModel(new AppinfoTableModel(new String[0], new String[0][0]));
    }

    public JPanel getJPanel() {
        return mJPanel;
    }

    public synchronized void getDetailInfo(long startTime, long endTime) {
        result.setLength(0);
        if (startTime > endTime)
            return;

        result.append(Const.Panel.TIME + ": " + format.format(startTime) + " > "
                + format.format(endTime) + Const.Symbols.LINE);
        // mDetailInfo.setText(result.toString());

        performParseCPUTopList(startTime, endTime);
        performParseProcStartList(startTime, endTime);
        performParseFocusedList(startTime, endTime);
        performParseProcDiedList(startTime, endTime);
        performParseKillList(startTime, endTime);
        performParsePSSList(startTime, endTime);
        performParseLaunchTimeList(startTime, endTime);
        performParseANR(startTime, endTime);
        performParseCrash(startTime, endTime);

        performParseScreenToggled(startTime, endTime);
        performParseBinderSample(startTime, endTime);

        performParseFrameDrop(startTime, endTime);
    
        mDetailInfo.setText(result.toString());
    }

    private class CPUStatistics {
        CPUStatistics() {
            user_max = user_avg = sys_max = sys_avg = io_max = io_avg = 0;
        }
        
        public double user_max;
        public double user_avg;
        
        public double sys_max;
        public double sys_avg;
        
        public double io_max;
        public double io_avg;
    }

    private CPUStatistics getCPUStatistics(ArrayList<Top> list) {
        CPUStatistics result = new CPUStatistics();
        if(0 == list.size()) {
            return result;
        }

        for (Top top:list) {
            result.user_avg += top.getUserUsage();
            if(top.getUserUsage() > result.user_max) result.user_max = top.getUserUsage();
            
            result.sys_avg += top.getSystemUsage();
            if(top.getUserUsage() > result.sys_max) result.sys_max = top.getSystemUsage();
            
            result.io_avg += top.getIOWait();
            if(top.getUserUsage() > result.io_max) result.io_max = top.getIOWait();
        }
        result.user_avg /= list.size();
        result.sys_avg /= list.size();
        result.io_avg /= list.size();
        
        //debugmsg("getCPUStatistics()::" + result.toString());
        return result;
    }

    private void debugmsg(String str) {
        System.out.println(str);
    }

    private void performParseCPUTopList(long startTime, long endTime) {

        ArrayList<Top> tops = mAlogEventParser.getTop(startTime, endTime);

        if (tops == null || tops.size() == 0) {
            mCPUTopTable.setModel(new AppinfoTableModel(new String[0], new String[0][0]));
            return;
        }

        CPUStatistics cpuStat = getCPUStatistics(tops);
        result.append(String.format(
                "CPU Statistics:\n"
                + "\tUser:\n"
                + "\t\tmax: %f\n"
                + "\t\tavg: %f\n"
                + "\tSystem:\n"
                + "\t\tmax: %f\n"
                + "\t\tavg: %f\n"
                + "\tIOWait:\n"
                + "\t\tmax: %f\n"
                + "\t\tavg: %f\n\n",
                cpuStat.user_max, cpuStat.user_avg,
                cpuStat.sys_max, cpuStat.sys_avg,
                cpuStat.io_max, cpuStat.io_avg));


        String[] title = { Const.Panel.TIME, Const.LineTitles.TOP_SUB_IRQ, Const.LineTitles.TOP_SUB_IOWAIT,
                Const.LineTitles.TOP_SUB_SYSTEM_USAGE, Const.LineTitles.TOP_SUB_USER_USAGE };

        String[][] items = new String[tops.size()][title.length];

        for (int i = 0; i < tops.size(); i++) {
            items[i][0] = format.format(tops.get(i).getTime());
            items[i][1] = tops.get(i).getIRQ().toString();
            items[i][2] = tops.get(i).getIOWait().toString();
            items[i][3] = tops.get(i).getSystemUsage().toString();
            items[i][4] = tops.get(i).getUserUsage().toString();
        }

        AppinfoTableModel appTM = new AppinfoTableModel(title, items);
        mCPUTopTable.setModel(appTM);
    }

    
    private void performParseProcStartList(long startTime, long endTime) {
        
        ArrayList<ProcStart> procStarts = mAlogEventParser.getAmProcStart(startTime, endTime);
        
        if (procStarts == null || procStarts.size() == 0) {
            mProcStartTable.setModel(new AppinfoTableModel(new String[0], new String[0][0]));
            return;
        }

        result.append("Proc Start Count: " + procStarts.size() + "\n\n");
        
        String[] title = { Const.Panel.TIME, Const.Panel.MODULE_NAME, Const.Panel.MODULE_TYPE,
                Const.Panel.PACKAGE_NAME, Const.Panel.PID, Const.Panel.UID, Const.Panel.USER};

        String[][] items = new String[procStarts.size()][title.length];

        for (int i = 0; i < procStarts.size(); i++) {
            items[i][0] = format.format(procStarts.get(i).getTime());
            items[i][1] = procStarts.get(i).getModuleName();
            items[i][2] = procStarts.get(i).getModuleType();
            items[i][3] = procStarts.get(i).getPackage();
            items[i][4] = procStarts.get(i).getPid().toString();
            items[i][5] = procStarts.get(i).getUid().toString();
            items[i][6] = procStarts.get(i).getUser().toString();
        }

        AppinfoTableModel appTM = new AppinfoTableModel(title, items);
        mProcStartTable.setModel(appTM);
    }
    
    private void performParseFocusedList(long startTime, long endTime) {
        ArrayList<ActivityFocused> focuseds = mAlogEventParser.getActivityFocused(startTime, endTime);
        
        if (focuseds == null || focuseds.size() == 0) {
            mFocusedTable.setModel(new AppinfoTableModel(new String[0], new String[0][0]));
            return;
        }
        
        String[] title = { Const.Panel.TIME, Const.Panel.COMPONENT_NAME, Const.Panel.REASON, Const.Panel.USER};

        String[][] items = new String[focuseds.size()][title.length];

        for (int i = 0; i < focuseds.size(); i++) {
            items[i][0] = format.format(focuseds.get(i).getTime());
            items[i][1] = focuseds.get(i).getComponentName();
            items[i][2] = focuseds.get(i).getReason();
            items[i][3] = focuseds.get(i).getUser().toString();
        }

        AppinfoTableModel appTM = new AppinfoTableModel(title, items);
        mFocusedTable.setModel(appTM);
    }
    
    private void performParseProcDiedList(long startTime, long endTime) {
        
        ArrayList<ProcDied> procDieds = mAlogEventParser.getProcDied(startTime, endTime);
        
        if (procDieds == null || procDieds.size() == 0) {
            mProcDiedTable.setModel(new AppinfoTableModel(new String[0], new String[0][0]));
            return;
        }
        
        result.append("Proc Died Count: " + procDieds.size() + "\n\n");
        
        String[] title = { Const.Panel.TIME, Const.Panel.PACKAGE_NAME, Const.Panel.PID, Const.Panel.USER, Const.Panel.OOM_ADJ, Const.Panel.PROC_STATE};

        String[][] items = new String[procDieds.size()][title.length];

        for (int i = 0; i < procDieds.size(); i++) {
            items[i][0] = format.format(procDieds.get(i).getTime());
            items[i][1] = procDieds.get(i).getProcessName();
            items[i][2] = procDieds.get(i).getPID().toString();
            items[i][3] = procDieds.get(i).getUser().toString();
            items[i][4] = procDieds.get(i).getOomAdj().toString();
            items[i][5] = procDieds.get(i).getProcState().toString();
        }

        AppinfoTableModel appTM = new AppinfoTableModel(title, items);
        mProcDiedTable.setModel(appTM);
    }
    
    private void performParseKillList(long startTime, long endTime) {
        
        ArrayList<Kill> kills = mAlogEventParser.getKill(startTime, endTime);
        
        if (kills == null || kills.size() == 0) {
            mKillTable.setModel(new AppinfoTableModel(new String[0], new String[0][0]));
            return;
        }
        
        result.append("Proc Killed Count: " + kills.size() + "\n\n");
        
        String[] title = { Const.Panel.TIME, Const.Panel.PACKAGE_NAME, Const.Panel.OOM,
                Const.Panel.PID, Const.Panel.REASON, Const.Panel.USER};

        String[][] items = new String[kills.size()][title.length];

        for (int i = 0; i < kills.size(); i++) {
            items[i][0] = format.format(kills.get(i).getTime());
            items[i][1] = kills.get(i).getProcessName();
            items[i][2] = kills.get(i).getOOMAdj().toString();
            items[i][3] = kills.get(i).getPID().toString();
            items[i][4] = kills.get(i).getReason();
            items[i][5] = kills.get(i).getUser().toString();
        }

        AppinfoTableModel appTM = new AppinfoTableModel(title, items);
        mKillTable.setModel(appTM);
    }
    
    /**
     * Sort PSS List by PSS
     * (Implementing #1 - Quick Sort -> java.lang.StackOverflowError)
     * (Implementing #2 - Quick Sort + in-place)
     * (Implementing #3 - using Collections.sort)
     * */
    private void sortPSS(ArrayList<PSS> list) {
        Collections.sort(list);
    }
    private void sortPSSUsingArrayListSorting(ArrayList<PSS> list) {
        sortArrayList(list, 0, list.size()-1);
    }
    private <T extends Comparable<T>> void sortArrayList(ArrayList<T> list,  int start, int end) {
        T temp;
        if(0 <= start && start < end && end < list.size()) {    //  check parameters valid
            /*
             * 1. select the last item as the pivot in quick sorting
             * 2. adjust the origin list except the last item
             * 3. switch the pivot to the new position
             */
            int left = start;
            int right = end -1;
            while(left < right) {
                while(left < right && list.get(left).compareTo(list.get(end)) < 0) {
                    left++;
                }
                while(left < right && list.get(right).compareTo(list.get(end)) >= 0) {
                    right--;
                }
                if(left < right) {
                    temp = list.get(left);
                    list.set(left, list.get(right));
                    list.set(right, temp);
                }
            }
            if(list.get(right).compareTo(list.get(end)) >= 0) {
                temp = list.get(right);
                list.set(right, list.get(end));
                list.set(end, temp);
                sortArrayList(list, start, right-1);
                sortArrayList(list, right+1, end);
            } else {
                sortArrayList(list, start, end-1);
                /*
                 * This will be the worst case of Quick Sorting
                 * if every time of sorting is end here
                 * */
            }
        }
    }
    /*
    private ArrayList<PSS> sortPSS(ArrayList<PSS> list) {
        ArrayList<PSS> less = new ArrayList<PSS>();
        ArrayList<PSS> greater = new ArrayList<PSS>();
        for(int i = 1; i < list.size(); i++) {
            if(list.get(0).getPSS() < list.get(i).getPSS()) {
                greater.add(list.get(i));
            } else {
                less.add(list.get(i));
            }
        }
        ArrayList<PSS> result = new ArrayList<PSS>();
        try {
            result.addAll(sortPSS(less));
            result.add(list.get(0));
            result.addAll(sortPSS(greater));
        } catch(Exception e) {
            debugmsg(e.toString());
        }
        return result;
    }
    */

    private void performParsePSSList(long startTime, long endTime) {
        ArrayList<PSS> psses = mAlogEventParser.getPSS(startTime, endTime);
        if(null == psses || 0 == psses.size()) {
            mPSSTable.setModel(new AppinfoTableModel(new String[0], new String[0][0]));
            return;
        }
        // TODO: Statistics Information?
        //sortPSS(psses, 0, psses.size()-1);
        sortPSS(psses);
        String[] title = {Const.Panel.TIME, Const.Panel.PACKAGE_NAME, Const.Panel.PID, Const.Panel.UID, Const.Panel.PSS, Const.Panel.USS, Const.Panel.SWAP_PSS};
        String[][] items = new String[psses.size()][title.length];
        for (int i = 0; i < psses.size(); i++) {
            items[i][0] = format.format(psses.get(i).getTime());
            items[i][1] = psses.get(i).getPackageName();
            items[i][2] = psses.get(i).getPID().toString();
            items[i][3] = psses.get(i).getUID().toString();
            items[i][4] = psses.get(i).getPSS().toString();
            items[i][5] = psses.get(i).getUSS().toString();
            items[i][6] = psses.get(i).getSwapPSS().toString();
        }
        AppinfoTableModel appTM = new AppinfoTableModel(title, items);
        mPSSTable.setModel(appTM);
    }

    private void performParseLaunchTimeList(long startTime, long endTime) {
        ArrayList<ActivityLaunchTime> launches = mAlogEventParser.getActivityLaunchTime(startTime, endTime);
        if(null == launches || 0 == launches.size()) {
            mLaunchTimeTable.setModel(new AppinfoTableModel(new String[0], new String[0][0]));
            return;
        }
        String[] title = {Const.Panel.TIME, Const.Panel.COMPONENT_NAME, Const.Panel.TOKEN, Const.Panel.CURRENT_LAUNCH_TIME, Const.Panel.TOTAL_LAUNCH_TIME, Const.Panel.USER};
        String[][] items = new String[launches.size()][title.length];
        for(int i = 0; i < launches.size(); i++) {
            items[i][0] = format.format(launches.get(i).getTime());
            items[i][1] = launches.get(i).getComponentName().toString();
            items[i][2] = launches.get(i).getToken().toString();
            items[i][3] = launches.get(i).getThisTime().toString();
            items[i][4] = launches.get(i).getTotalTime().toString();
            items[i][5] = launches.get(i).getUser().toString();
        }
        AppinfoTableModel appTM = new AppinfoTableModel(title, items);
        mLaunchTimeTable.setModel(appTM);
    }

    private void performParseANR(long startTime, long endTime) {
        ArrayList<ANR> anrs = mAlogEventParser.getANR(startTime, endTime);
        if(null == anrs || 0 == anrs.size()) {
            return;
        }
        result.append("ANR Count: " + anrs.size() + "\n\n");
    }

    private void performParseCrash(long startTime, long endTime) {
        ArrayList<Crash> crashes = mAlogEventParser.getCrash(startTime, endTime);
        if(null == crashes || 0 == crashes.size()) {
            return;
        }
        result.append("Crash Count: " + crashes.size() + "\n\n");
    }

    private void performParseScreenToggled(long startTime, long endTime) {
        ArrayList<ScreenToggled> screen = mAlogEventParser.getScreenToggled(startTime, endTime);
        if(null == screen || 0 == screen.size()) {
            mScreenToggledTable.setModel(new AppinfoTableModel(new String[0], new String[0][0]));
            return;
        }
        String[] title = {Const.Panel.TIME, Const.Panel.SCREEN_STATE};
        String[][] items = new String[screen.size()][title.length];
        for(int i = 0; i < screen.size(); i++) {
            items[i][0] = format.format(screen.get(i).getTime());
            items[i][1] = screen.get(i).getScreenOnOff().toString();
        }
        AppinfoTableModel appTM = new AppinfoTableModel(title, items);
        mScreenToggledTable.setModel(appTM);
    }

    private void performParseBinderSample(long startTime, long endTime) {
        ArrayList<BinderSample> bind = mAlogEventParser.getBinderSample(startTime, endTime);
        if(null == bind || 0 == bind.size()) {
            mBinderSampleTable.setModel(new AppinfoTableModel(new String[0], new String[0][0]));
            return;
        }
        String[] title = {Const.Panel.TIME, Const.Panel.DESCRIPTOR, Const.Panel.METHOD_NUM, Const.Panel.BLCK_PACKAGE, Const.Panel.DURATION, Const.Panel.SAMPLE_PERCENT};
        String[][] items = new String[bind.size()][title.length];
        for(int i = 0; i < bind.size(); i++) {
            items[i][0] = format.format(bind.get(i).getTime());
            items[i][1] = bind.get(i).getDescriptor();
            items[i][2] = bind.get(i).getMethodNumber().toString();
            items[i][3] = bind.get(i).getBlockPackage();
            items[i][4] = bind.get(i).getDuration().toString();
            items[i][5] = bind.get(i).getSamplePercent().toString();
        }
        AppinfoTableModel appTM = new AppinfoTableModel(title, items);
        mBinderSampleTable.setModel(appTM);
    }

    private void performParseFrameDrop(long startTime, long endTime) {
        ArrayList<FrameDrop> framedrop = mAlogEventParser.getFrameDrop(startTime, endTime);
        if(null == framedrop || 0 == framedrop.size()) {
            mFrameDropTable.setModel(new AppinfoTableModel(new String[0], new String[0][0]));
            return;
        }
        String[] title = {Const.Panel.TIME, Const.Panel.FRAME_DROP};
        String[][] items = new String[framedrop.size()][title.length];
        for(int i = 0; i < framedrop.size(); i++) {
            items[i][0] = format.format(framedrop.get(i).getTime());
            items[i][1] = framedrop.get(i).getFrameDrop().toString();
        }
        AppinfoTableModel appTM = new AppinfoTableModel(title, items);
        mFrameDropTable.setModel(appTM);
    }


}


