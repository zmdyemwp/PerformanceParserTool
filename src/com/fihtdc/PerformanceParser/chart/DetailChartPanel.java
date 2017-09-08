package com.fihtdc.PerformanceParser.chart;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import com.fihtdc.PerformanceParser.dataparser.AlogEventParser;
import com.fihtdc.PerformanceParser.dataparser.AlogEventParser.ActivityFocused;
import com.fihtdc.PerformanceParser.dataparser.AlogEventParser.Kill;
import com.fihtdc.PerformanceParser.dataparser.AlogEventParser.ProcDied;
import com.fihtdc.PerformanceParser.dataparser.AlogEventParser.ProcStart;
import com.fihtdc.PerformanceParser.dataparser.AlogEventParser.Top;
import com.fihtdc.PerformanceParser.utils.Const;

public class DetailChartPanel {

    private JPanel mJPanel;
    private JPanel mCPUTopPanel;
    private JPanel mProcStartPanel;
    private JPanel mFocusedPanel;
    private JPanel mProcDiedPanel;
    private JPanel mKillPanel;

    private JTable mCPUTopTable;
    private JTable mProcStartTable;
    private JTable mFocusedTable;
    private JTable mProcDiedTable;
    private JTable mKillTable;

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

        mCPUTopTable = new JTable();
        mProcStartTable = new JTable();
        mFocusedTable = new JTable();
        mProcDiedTable = new JTable();
        mKillTable = new JTable();

        mCPUTopPanel.add(new JScrollPane(mCPUTopTable, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS));
        mProcStartPanel.add(new JScrollPane(mProcStartTable, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS));
        mFocusedPanel.add(new JScrollPane(mFocusedTable, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS));
        mProcDiedPanel.add(new JScrollPane(mProcDiedTable, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS));
        mKillPanel.add(new JScrollPane(mKillTable, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS));

        mTabPane.addTab(Const.Panel.CPU_TOP_INFO, mCPUTopPanel);
        mTabPane.addTab(Const.Panel.PROC_START_INFO, mProcStartPanel);
        mTabPane.addTab(Const.Panel.FOCUSED_INFO, mFocusedPanel);
        mTabPane.addTab(Const.Panel.PROC_DIED_INFO, mProcDiedPanel);
        mTabPane.addTab(Const.Panel.KILL_INFO, mKillPanel);

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
        
        mDetailInfo.setText(result.toString());

        performParseCPUTopList(startTime, endTime);
        performParseProcStartList(startTime, endTime);
        performParseFocusedList(startTime, endTime);
        performParseProcDiedList(startTime, endTime);
        performParseKillList(startTime, endTime);

    }

    private void performParseCPUTopList(long startTime, long endTime) {

        ArrayList<Top> tops = mAlogEventParser.getTop(startTime, endTime);

        if (tops == null || tops.size() == 0) {
            mCPUTopTable.setModel(new AppinfoTableModel(new String[0], new String[0][0]));
            return;
        }

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
        
        String[] title = { Const.Panel.TIME, Const.Panel.PACKAGE_NAME, Const.Panel.PID, Const.Panel.USER};

        String[][] items = new String[procDieds.size()][title.length];

        for (int i = 0; i < procDieds.size(); i++) {
            items[i][0] = format.format(procDieds.get(i).getTime());
            items[i][1] = procDieds.get(i).getProcessName();
            items[i][2] = procDieds.get(i).getPID().toString();
            items[i][3] = procDieds.get(i).getUser().toString();
        }

        AppinfoTableModel appTM = new AppinfoTableModel(title, items);
        mFocusedTable.setModel(appTM);
    }
    
    private void performParseKillList(long startTime, long endTime) {
        
        ArrayList<Kill> kills = mAlogEventParser.getKill(startTime, endTime);
        
        if (kills == null || kills.size() == 0) {
            mKillTable.setModel(new AppinfoTableModel(new String[0], new String[0][0]));
            return;
        }
        
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
}