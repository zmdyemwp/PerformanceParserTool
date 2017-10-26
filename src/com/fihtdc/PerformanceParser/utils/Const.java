package com.fihtdc.PerformanceParser.utils;

import java.awt.Color;


public final class Const {
    
    public static final class TAG {
        public static final String LOG_PARSER = "LogParser";
        public static final String PERFORMANCE_PARSER = "PerformanceParserTool";
    }

    
    public static final class Version {
        public static final String NUMBER = "v1.0.2";
    }
    
    public static final class PathName {
        public static final String DEFAULT_PATH = "\\C:\\";
    }

    public static final class Panel {
        public static final String TITLE_ALOG_EVENTLOG_INFO = "手機 alog & eventlog 資訊";
        public static final String FILE = "File";
        public static final String SELECT_FOLDER = "Select Folder";
        public static final String EXIT = "Exit";
        public static final String ABOUT = "About";
        public static final String ABOUT_MESSAGE = "<html> <table width=250> "
                + "<tr> <td align=center valign=center> Performance Parser Version : "
                + Version.NUMBER + "</td> </tr> </table> </html>";
        public static final String ZOOMABLE = "Zoomable";
        public static final String SELECTABLE = "Selectable";
        public static final String LEFT = "  Left:  ";
        public static final String RIGHT = "  Right: ";
        public static final String SHIFT_TIPS = " Hold \"Ctrl\" key to shift the area. ";
        public static final String DATE_TIME = "Date/Time";
        public static final String TOP_AXIS_TITLE = "Top Level(%)";
        
        public static final String CPU_TOP_INFO = "CPU TOP Info";
        public static final String PROC_START_INFO = "Proc Start Info";
        public static final String PROC_DIED_INFO = "Proc Died Info";
        public static final String KILL_INFO = "Kill Info";
        public static final String FOCUSED_INFO = "Focused Info";
        public static final String PSS_INFO = "PSS Info";
        public static final String LAUNCHTIME_INFO = "LaunchTime Info";
        
        public static final String TIME = "時間";
        public static final String MODULE_NAME = "module name";
        public static final String MODULE_TYPE = "module type";
        public static final String PACKAGE_NAME = "Package name";
        public static final String PID = "PID";
        public static final String UID = "UID";
        public static final String USER = "User";
        public static final String PSS = "PSS";
        public static final String USS = "USS";
        public static final String SWAP_PSS = "Swap PSS";
        public static final String TOKEN = "Token";
        public static final String CURRENT_LAUNCH_TIME = "Current Launch Time";
        public static final String TOTAL_LAUNCH_TIME = "Total Launch Time";
        
        public static final String SCREEN_TOGGLED_INFO = "Screen Toggled Info";
        public static final String BINDER_SAMPLE_INFO = "Binder Sample Info";
        public static final String DESCRIPTOR = "Descriptor";
        public static final String METHOD_NUM = "Method Number";
        public static final String BLCK_PACKAGE = "Blocked Package";
        public static final String DURATION = "Duration";
        public static final String SAMPLE_PERCENT = "Sample Percent";
        public static final String SCREEN_STATE = "Screen State";
        
        public static final String OOM_ADJ = "OOM Adj";
        public static final String PROC_STATE = "Proc State";
        
        public static final String FRAME_DROP = "Frame Drop";
        
        public static final String COMPONENT_NAME = "Component name";
        public static final String REASON = "Reason";
        
        public static final String OOM = "OOM";
        public static final String LMK = "LMK";
        
        public static final String DESCRIPTION = "Description";
    }
    
    public static final class ItemCMD {
        public static final String LEFT_OF_FRAME_TO_LEFT = "LeftOfFrameToLeft";
        public static final String LEFT_OF_FRAME_TO_RIGHT = "LeftOfFrameToRight";
        public static final String RIGHT_OF_FRAME_TO_LEFT = "RightOfFrameToLeft";
        public static final String RIGHT_OF_FRAME_TO_RIGHT = "RightOfFrameToRight";
        public static final String LEFT_OF_FRAME = "leftOfFrame";
        public static final String RIGHT_OF_FRAME = "rightOfFrame";
    }

    public static final class Dialog {
        public static final String ERROR = "錯誤";
        public static final String WARNING = "警告";
        public static final String SELECT_FOLDER_ERROR = "event log不存在,請重新選取檔案資料夾!";
        public static final String CPU_TOP = "CPU Top";
    }
    
    public static class FileName {
        public static final String EVENT_STATUS = "event";
    }
    
    public static class DateSet {
        public static final String DATE_FORMAT_STANDARD = "yyyy-MM-dd HH:mm:ss.SSS";
        public static final String DATE_FORMAT_STANDARD_YMD = "yyyy-MM-dd 00:00:00";
        public static final String DATE_FORMAT_XXXL = "yyyy/MM/dd HH:mm";
        public static final String DATE_FORMAT_XXL = "yyyyMMddHHmmss";
        public static final String DATE_FORMAT_XL = "yyyy-MM-dd";
        public static final String DATE_FORMAT_L = "MM/dd HH:mm";
        public static final String DATE_FORMAT_M = "M/d";
        public static final String DATE_FORMAT = "HH:mm";
        public static final String DATE_FORMAT_S = "HH:mm.ss";
        public static final String DATE_FORMAT_SS = "mm.ss-SSS";
        public static final String DATE_FORMAT_YMD = "yyyyMMdd";
        public static final long MILLI_SECOND = 1000;
        public static final long MILLI_MIN = 60 * MILLI_SECOND;
        public static final long MILLI_HOUR = 60 * MILLI_MIN;
        public static final long MILLI_DAY = 24 * MILLI_HOUR;
        public static final long MILLI_MONTH = 30 * MILLI_DAY;
    }

    public static final class LineStyle {
        public static final String SOLID = "solid";
        public static final String DASHED = "dashed";
        public static final String NONE = "none";
    }

    public static final class LineSeat {
        public static final String MAIN = "main";
        public static final String SUB = "sub";
        public static final String POLATION = "polation";
    }

    public static final class PPColors {
        public static final Color RECTANGLE = new Color(0, 191, 255);
        public static final Color LINE_CPU_TOP = new Color(200, 200, 100);
        public static final Color TASK_PROC_START = new Color(147, 122, 219);
        public static final Color TASK_FOCUSED = new Color(140, 196, 82);
        public static final Color TASK_PROC_DIED = new Color(252, 180, 65);
        public static final Color SCREEN_TOGGLED = new Color(255, 255, 255);
        public static final Color LINE_MEM_INFO = new Color(200, 200, 100);
        public static final Color TASK_LMK = new Color(255,255,255);
    }
    
    public static final class LineTitles {
        public static final String CPU_TOP = "CPU TOP";
        public static final String TOP_SUB_IOWAIT = "IOWait";
        public static final String TOP_SUB_IRQ = "IRQ";
        public static final String TOP_SUB_SYSTEM_USAGE = "Syetem Usage";
        public static final String TOP_SUB_USER_USAGE = "User Usage";
        public static final String PROC_START = "proc_start";
        public static final String FOCUSED = "focused";
        public static final String PROC_DIED = "proc_died & am_kied";
        public static final String AM_KILL = "am_kied";
        public static final String SCREEN_TOGGLED = "screen_toggled";
        public static final String MEMINFO = "memory_info";
        public static final String MEMINFO_ZRAM = "zram";
        public static final String MEMINFO_KERNEL = "kernel";
        public static final String MEMINFO_NATIVE = "native";
        public static final String MEMINFO_CACHED = "cached";
        public static final String MEMINFO_FREE = "free";
        public static final String LMK = "LMK";
    }

    public static final class Symbols {
        public static final char NUMBER_SIGN = '#';
        public static final char ASTERISK = '*';
        public static final String LINE = "\n";
        public static final String SEMICOLON = ";";
        public static final String COMMA = ",";
        public static final String VERTICAL_BAR = "\\|";
        public static final String PARENTHESES_L = "(";
        public static final String PARENTHESES_R = ")";
    }

    public static final class StatusIndex {
        public static final int PROC_START = 0;
        public static final int FOCUSED = 1;
        public static final int WIFI_ON = 2;
    }

    public static final class Axis {
        public static final long MIN_RANGE_Y = 0;
        public static final long MAX_RANGE_Y = 105;
    }
}