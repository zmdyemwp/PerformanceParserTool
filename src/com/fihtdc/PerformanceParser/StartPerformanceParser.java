package com.fihtdc.PerformanceParser;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JTextPane;
import javax.swing.JToolBar;

import org.jfree.ui.RefineryUtilities;

import com.fihtdc.PerformanceParser.chart.DetailChartPanel;
import com.fihtdc.PerformanceParser.chart.LineChartPanel;
import com.fihtdc.PerformanceParser.chart.LineChartPanel.RectangleSelectedEvent;
import com.fihtdc.PerformanceParser.dataparser.AlogEventParser;
import com.fihtdc.PerformanceParser.utils.Const;
import com.fihtdc.PerformanceParser.utils.Const.FileName;
import com.fihtdc.PerformanceParser.chart.XYMultiDataSet;

public class StartPerformanceParser extends JFrame implements ActionListener {
    
    private static StartPerformanceParser mStartJFreeChart;
    private Container mContainer = null;
    private LineChartPanel mLineChartPanel = null;
    private DetailChartPanel mDetailChartPanel = null;
    private XYMultiDataSet mXYMultiDataSet;
    
    private JRadioButtonMenuItem zoomItem;
    private JRadioButtonMenuItem selectItem;
    
    private JToolBar mJToolBar;
    private JLabel mLabelLeft;
    private JLabel mLabelRight;
    private JLabel mZoomTips;
    private JTextPane mToolTips;
    private JButton mButtonLS;
    private JButton mButtonLB;
    private JButton mButtonRS;
    private JButton mButtonRB;
    
    
    
    
    private static String folderPath = Const.PathName.DEFAULT_PATH;
    
    private static int WINDOW_X_SCALE = 1200;
    private static int WINDOW_Y_SCALE = 660;
    
    private boolean zoomEnabled = false;
    private boolean dialogTips = true;
    
    private String message = Const.Dialog.SELECT_FOLDER_ERROR;
    
    public static void main(String s[]) {
        String title = Const.TAG.PERFORMANCE_PARSER + " " + Const.Version.NUMBER;
        System.out.println("Start " + title);
        System.out.println(s.length);


        mStartJFreeChart = new StartPerformanceParser(title);
        mStartJFreeChart.setDefaultCloseOperation(EXIT_ON_CLOSE);
        mStartJFreeChart.setVisible(true);
        mStartJFreeChart.setSize(new Dimension(WINDOW_X_SCALE, WINDOW_Y_SCALE));
        mStartJFreeChart.setIconImage(new ImageIcon(mStartJFreeChart.getClass()
                .getResource("/images/icon.png")).getImage());
        RefineryUtilities.centerFrameOnScreen(mStartJFreeChart);

    }

    public StartPerformanceParser(String title) {
        super(title);
        mContainer = this.getContentPane();
        createMenu();
        init();
        zoomEnabled = false;
        setItemVisible(zoomEnabled);
    }

    public void createMenu() {
        JMenuBar mMenuBar = new JMenuBar();
        mMenuBar.setLayout(new FlowLayout(FlowLayout.LEFT));
        mMenuBar.setPreferredSize(new Dimension(200, 73));
        this.setJMenuBar(mMenuBar);

        JMenu mFile = new JMenu(Const.Panel.FILE);

        JMenuItem mSelectFolder = new JMenuItem(Const.Panel.SELECT_FOLDER);
        mSelectFolder.addActionListener(mSelectFolderListener);
        mFile.add(mSelectFolder);
        mFile.addSeparator();

        JMenuItem mExit = new JMenuItem(Const.Panel.EXIT);
        mExit.addActionListener(mExitListener);
        mFile.add(mExit);

        JMenu mAbout = new JMenu(Const.Panel.ABOUT);
        JLabel mVersion = new JLabel(Const.Panel.ABOUT_MESSAGE);
        mAbout.add(mVersion);

        mMenuBar.add(mFile, BorderLayout.WEST);
        mMenuBar.add(mAbout, BorderLayout.WEST);
        mMenuBar.add(Box.createHorizontalStrut(10000));
        createToolBar();
        mMenuBar.add(mJToolBar, BorderLayout.SOUTH);
    }
    

    public void createToolBar() {
        mJToolBar = new JToolBar();
        //mJToolBar.setPreferredSize(new Dimension(1110, 27));

        mJToolBar.setFloatable(false);

        zoomItem = new JRadioButtonMenuItem(Const.Panel.ZOOMABLE);
        zoomItem.addActionListener(mZoomItemListener);
        mJToolBar.add(zoomItem);

        selectItem = new JRadioButtonMenuItem(Const.Panel.SELECTABLE);
        selectItem.addActionListener(mSelectListener);
        mJToolBar.add(selectItem);

        mZoomTips = new JLabel(Const.Panel.SHIFT_TIPS);
        mJToolBar.add(mZoomTips);

        mLabelLeft = new JLabel();
        mLabelLeft.setText(Const.Panel.LEFT);
        mJToolBar.add(mLabelLeft);

        mButtonLS = new JButton();
        mButtonLS.setActionCommand(Const.ItemCMD.LEFT_OF_FRAME_TO_LEFT);
        mButtonLS.addActionListener(this);
        mButtonLS.setText("<");
        mJToolBar.add(mButtonLS);

        mButtonLB = new JButton();
        mButtonLB.setActionCommand(Const.ItemCMD.LEFT_OF_FRAME_TO_RIGHT);
        mButtonLB.addActionListener(this);
        mButtonLB.setText(">");
        mJToolBar.add(mButtonLB);

        mLabelRight = new JLabel();
        mLabelRight.setText(Const.Panel.RIGHT);
        mJToolBar.add(mLabelRight);

        mButtonRS = new JButton();
        mButtonRS.setActionCommand(Const.ItemCMD.RIGHT_OF_FRAME_TO_LEFT);
        mButtonRS.addActionListener(this);
        mButtonRS.setText("<");
        mJToolBar.add(mButtonRS);

        mButtonRB = new JButton();
        mButtonRB.setActionCommand(Const.ItemCMD.RIGHT_OF_FRAME_TO_RIGHT);
        mButtonRB.addActionListener(this);
        mButtonRB.setText(">");
        mJToolBar.add(mButtonRB);

        mToolTips = new JTextPane();
        mToolTips.setEditable(false);
        mToolTips.setBackground(null);

        mJToolBar.add(mToolTips);
    }


    ActionListener mSelectFolderListener = new ActionListener() {
        public void actionPerformed(ActionEvent ae) {
            final JFileChooser filechooser = new JFileChooser(folderPath);
            filechooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int yesornot = filechooser.showOpenDialog(StartPerformanceParser.this);
            if (yesornot == JFileChooser.APPROVE_OPTION) {
                File f = filechooser.getSelectedFile();
                folderPath = f.getAbsolutePath();

                init();
                dialogTips = true;
                message = Const.Dialog.SELECT_FOLDER_ERROR;
                File folder = new File(folderPath);
                if (folder.exists()) {
                    for (final File fileEntry : folder.listFiles()) {
                        if (!fileEntry.isDirectory()) {
                            String name = fileEntry.getName();
                            if (name.contains(FileName.EVENT_STATUS)) {
                                    loadData(folderPath);
                                    dialogTips = false;
                                    break;
 
                            }
                        }
                    }
                }
                
                updateUI();
                if (dialogTips) {
                    JOptionPane.showMessageDialog(StartPerformanceParser.this,
                            message, Const.Dialog.WARNING, JOptionPane.WARNING_MESSAGE);
                }
                mStartJFreeChart.setTitle(Const.TAG.PERFORMANCE_PARSER + " "
                        + Const.Version.NUMBER + " - " + folderPath);
            }
        }
    };


    ActionListener mExitListener = new ActionListener() {
        public void actionPerformed(ActionEvent ae) {
            System.exit(0);
        }
    };

    ActionListener mZoomItemListener = new ActionListener() {
        public void actionPerformed(ActionEvent ae) {
            if (!zoomEnabled) {
                zoomEnabled = true;
                if (mLineChartPanel != null || mDetailChartPanel != null) {
                    mLineChartPanel.setChartZoomable(zoomEnabled);
                    mDetailChartPanel.resetJPanel();
                }
            }
            setItemVisible(zoomEnabled);
        }
    };

    ActionListener mSelectListener = new ActionListener() {
        public void actionPerformed(ActionEvent ae) {
            if (zoomEnabled) {
                zoomEnabled = false;
                if (mLineChartPanel != null || mDetailChartPanel != null) {
                    mLineChartPanel.setChartZoomable(zoomEnabled);
                    mDetailChartPanel.resetJPanel();
                }
            }
            setItemVisible(zoomEnabled);
        }
    };

    public void setItemVisible(Boolean enable) {
        zoomItem.setSelected(zoomEnabled);
        mZoomTips.setVisible(zoomEnabled);
        selectItem.setSelected(!zoomEnabled);
        mLabelLeft.setVisible(!zoomEnabled);
        mButtonLS.setVisible(!zoomEnabled);
        mButtonLB.setVisible(!zoomEnabled);
        mLabelRight.setVisible(!zoomEnabled);
        mButtonRS.setVisible(!zoomEnabled);
        mButtonRB.setVisible(!zoomEnabled);
        mToolTips.setVisible(!zoomEnabled);
    }
    
    public void init() {
        if (mLineChartPanel != null) {
            mLineChartPanel.clear();
            mLineChartPanel = null;
        }
        mDetailChartPanel = null;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        System.out.println(e.getActionCommand());
        if (mLineChartPanel != null || mDetailChartPanel != null) {
            if (cmd.equals(Const.ItemCMD.LEFT_OF_FRAME_TO_LEFT)) {
                mLineChartPanel.moveMarker(Const.ItemCMD.LEFT_OF_FRAME, -1);
            } else if (cmd.equals(Const.ItemCMD.LEFT_OF_FRAME_TO_RIGHT)) {
                mLineChartPanel.moveMarker(Const.ItemCMD.LEFT_OF_FRAME, +1);
            } else if (cmd.equals(Const.ItemCMD.RIGHT_OF_FRAME_TO_LEFT)) {
                mLineChartPanel.moveMarker(Const.ItemCMD.RIGHT_OF_FRAME, -1);
            } else if (cmd.equals(Const.ItemCMD.RIGHT_OF_FRAME_TO_RIGHT)) {
                mLineChartPanel.moveMarker(Const.ItemCMD.RIGHT_OF_FRAME, +1);
            }
        }
    }

    public void loadData(String folderPath) {
        AlogEventParser avp = new AlogEventParser();
        avp.setFolderPath(folderPath);
        avp.clearData();
        avp.initData();

        mXYMultiDataSet = new XYMultiDataSet(avp);

        mDetailChartPanel = new DetailChartPanel(avp);
        mLineChartPanel = new LineChartPanel(avp, mXYMultiDataSet.getXYMultiDataSet());
        mLineChartPanel.setChartZoomable(zoomEnabled);
        
        if (mLineChartPanel != null)
            mLineChartPanel.setRectangleSelectedEvent(mRectangleSelectedEvent);
    }


    public void updateUI() {
        mContainer.removeAll();
        mContainer.setLayout(new GridLayout(1, 2));
        if (mLineChartPanel != null || mDetailChartPanel != null) {
            mContainer.add(mLineChartPanel.getJPanel());
            mContainer.add(mDetailChartPanel.getJPanel());
        }
        mContainer.validate();
        mContainer.repaint();
    }

    RectangleSelectedEvent mRectangleSelectedEvent = new RectangleSelectedEvent() {
        @Override
        public void onRectangleSelected(int startIndex, int endIndex) {
            //mDetailChartPanel.getDetailInfo(levelStartIndex, levelEndIndex);
        }

        @Override
        public void onRectangleSelected(long startTime, long endTime) {
            mDetailChartPanel.getDetailInfo(startTime, endTime);
        }
    };

}
