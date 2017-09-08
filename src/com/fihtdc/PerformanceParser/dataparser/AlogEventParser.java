package com.fihtdc.PerformanceParser.dataparser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;


/**
 * Parsing event logs and "top" info in main log files.
 * <br>
 * Event log:
 * <br>
 * <ul>
 *     <li><b>input_response</b>    (see {@link InputResponse})</li>
 *     <li><b>am_mem_factor</b>    (see {@link MemFactor})</li>
 *     <li><b>am_meminfo</b>    (see {@link MemInfo})</li>
 *     <li><b>am_pss</b>    (see {@link PSS})</li>
 *     <li><b>am_anr</b>    (see {@link ANR})</li>
 *     <li><b>am_crash</b>    (see {@link Crash})</li>
 *     <li><b>am_proc_died</b>    (see {@link ProcDied})</li>
 *     <li><b>am_kill</b>    (see {@link Kill})</li>
 *     <li><b>am_proc_start</b>    (see {@link ProcStart})</li>
 *     <li><b>am_proc_bound</b>    (see {@link ProcBound})</li>
 *     <li><b>am_activity_launch_time</b>    (see {@link ActivityLaunchTime})</li>
 *     <li><b>am_schedule_service_restart</b>    (see {@link ScheduleServiceRestart})</li>
 *     <li><b>am_focused_activity</b>    (see {@link ActivityFocused})</li>
 * </ul>
 * <br>
 * <br>
 * Main log:
 * <br>
 * <ul>
 *     <li><b>top</b>    (see {@link Top})</li>
 * </ul>
 * */
public class AlogEventParser {
    /**
     * The full path of the folder with the log files in there.*/
    private static String folder_path = "";

    /**
     * Set folder path with log files (main logs and event logs)
     * @param path the full path for log files
     * */
    public void setFolderPath(String path) {
        folder_path = path;
    }

    /**
     * Clear all data set
     */
    public void clearData() {
        // TODO: Clear All
        if(null != sInputResponse) sInputResponse.clear();
        if(null != sMemFactor) sMemFactor.clear();
        if(null != sMemInfo) sMemInfo.clear();
        if(null != sPSS) sPSS.clear();
        if(null != sANR) sANR.clear();
        if(null != sCrash) sCrash.clear();
        if(null != sProcDied) sProcDied.clear();
        if(null != sKill) sKill.clear();
        if(null != sProcStart) sProcStart.clear();
        if(null != sProcBound) sProcBound.clear();
        if(null != sActivityLaunchTime) sActivityLaunchTime.clear();
        if(null != sScheduleServiceRestart) sScheduleServiceRestart.clear();
        if(null != sActivityFocused) sActivityFocused.clear();
        if(null != sTop) sTop.clear();
    }


    /**
     * Read log files and parse the content to create data set for further use
     * */
    public void initData() {
        File folder = new File(folder_path);
        //  TODO: Parse ALogs for Top Information
        File[] files = folder.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                String name  = file.getName();
                return name.contains("alog") && !name.contains("_");
            }
        });
        sortFilesInReverseOrder(files);
        for(File f: files) {
            debugmsg(f.getName());
            getCurrentYear(f);              // TODO: try to get year in alog
            parseFile(f);
        }

        files = folder.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.getName().contains("alog_events");
            }
        });
        sortFilesInReverseOrder(files);
        for(File f: files) {
            debugmsg(f.getName());
            parseFile(f);
        }
    }


    public long getStartTime() {
        long start = Integer.MAX_VALUE;
        if(0 < sInputResponse.size() && start > sInputResponse.get(0).getTime()) start = sInputResponse.get(0).getTime();
        if(0 < sMemFactor.size() && start > sMemFactor.get(0).getTime()) start = sMemFactor.get(0).getTime();
        if(0 < sMemInfo.size() && start > sMemInfo.get(0).getTime()) start = sMemInfo.get(0).getTime();
        if(0 < sPSS.size() && start > sPSS.get(0).getTime()) start = sPSS.get(0).getTime();
        if(0 < sANR.size() && start > sANR.get(0).getTime()) start = sANR.get(0).getTime();
        if(0 < sCrash.size() && start > sCrash.get(0).getTime()) start = sCrash.get(0).getTime();
        if(0 < sProcDied.size() && start > sProcDied.get(0).getTime()) start = sProcDied.get(0).getTime();
        if(0 < sKill.size() && start > sKill.get(0).getTime()) start = sKill.get(0).getTime();
        if(0 < sProcStart.size() && start > sProcStart.get(0).getTime()) start = sProcStart.get(0).getTime();
        if(0 < sProcBound.size() && start > sProcBound.get(0).getTime()) start = sProcBound.get(0).getTime();
        if(0 < sActivityLaunchTime.size() && start > sActivityLaunchTime.get(0).getTime()) start = sActivityLaunchTime.get(0).getTime();
        if(0 < sScheduleServiceRestart.size() && start > sScheduleServiceRestart.get(0).getTime()) start = sScheduleServiceRestart.get(0).getTime();
        if(0 < sActivityFocused.size() && start > sActivityFocused.get(0).getTime()) start = sActivityFocused.get(0).getTime();
        if(0 < sTop.size() && start > sTop.get(0).getTime()) start = sTop.get(0).getTime();

        return start;
    }

    public long getEndTime() {
        long end = -1;
        int theSize = sInputResponse.size();
        if(0 < theSize && end < sInputResponse.get(theSize - 1).getTime()) end = sInputResponse.get(theSize - 1).getTime();

        theSize = sMemFactor.size();
        if(0 < theSize && end < sMemFactor.get(theSize - 1).getTime()) end = sMemFactor.get(theSize - 1).getTime();
        
        theSize = sMemInfo.size();
        if(0 < theSize && end < sMemInfo.get(theSize - 1).getTime()) end = sMemInfo.get(theSize - 1).getTime();
        
        theSize = sPSS.size();
        if(0 < theSize && end < sPSS.get(theSize - 1).getTime()) end = sPSS.get(theSize - 1).getTime();
        
        theSize = sANR.size();
        if(0 < theSize && end < sANR.get(theSize - 1).getTime()) end = sANR.get(theSize - 1).getTime();
        
        theSize = sCrash.size();
        if(0 < theSize && end < sCrash.get(theSize - 1).getTime()) end = sCrash.get(theSize - 1).getTime();
        
        theSize = sProcDied.size();
        if(0 < theSize && end < sProcDied.get(theSize - 1).getTime()) end = sProcDied.get(theSize - 1).getTime();
        
        theSize = sKill.size();
        if(0 < theSize && end < sKill.get(theSize - 1).getTime()) end = sKill.get(theSize - 1).getTime();
        
        theSize = sProcStart.size();
        if(0 < theSize && end < sProcStart.get(theSize - 1).getTime()) end = sProcStart.get(theSize - 1).getTime();
        
        theSize = sProcBound.size();
        if(0 < theSize && end < sProcBound.get(theSize - 1).getTime()) end = sProcBound.get(theSize - 1).getTime();
        
        theSize = sActivityLaunchTime.size();
        if(0 < theSize && end < sActivityLaunchTime.get(theSize - 1).getTime()) end = sActivityLaunchTime.get(theSize - 1).getTime();
        
        theSize = sScheduleServiceRestart.size();
        if(0 < theSize && end < sScheduleServiceRestart.get(theSize - 1).getTime()) end = sScheduleServiceRestart.get(theSize - 1).getTime();
        
        theSize = sActivityFocused.size();
        if(0 < theSize && end < sActivityFocused.get(theSize - 1).getTime()) end = sActivityFocused.get(theSize - 1).getTime();
        
        theSize = sTop.size();
        if(0 < theSize && end < sTop.get(theSize - 1).getTime()) end = sTop.get(theSize - 1).getTime();

        return end;
    }



    /**************************************************************************/
    /**
     * Private method read a log file line by line and parse it. */
    void parseFile(File f) {
        try {
            BufferedReader bfr = new BufferedReader(new FileReader(f));
            String line;
            while(null != (line = bfr.readLine())) {
                try {
                    parseLine(line);
                } catch(Exception ex) {
                    debugmsg(ex.getLocalizedMessage());
                    debugmsg("SOMETHING WRONG!!");
                    ex.printStackTrace();
                    continue;
                }
            }
            bfr.close();
        } catch (FileNotFoundException fnfex) {
            debugmsg(fnfex.getLocalizedMessage());
        } catch (IOException ioex) {
            debugmsg(ioex.getLocalizedMessage());
        } finally {
        }
    }

    void parseLine(String line) {
        String tag = getTagFromLine(line);
        switch(tag) {
        case ProcStart.TAG:
            sProcStart.add(new ProcStart(line));
            break;
        case InputResponse.TAG:
            sInputResponse.add(new InputResponse(line));
            break;
        case MemFactor.TAG:
            sMemFactor.add(new MemFactor(line));
            break;
        case MemInfo.TAG:
            sMemInfo.add(new MemInfo(line));
            break;
        case PSS.TAG:
            sPSS.add(new PSS(line));
            break;
        case ANR.TAG:
            sANR.add(new ANR(line));
            break;
        case Crash.TAG:
            sCrash.add(new Crash(line));
            break;
        case ProcDied.TAG:
            sProcDied.add(new ProcDied(line));
            break;
        case Kill.TAG:
            sKill.add(new Kill(line));
            break;
        case ProcBound.TAG:
            sProcBound.add(new ProcBound(line));
            break;
        case ActivityLaunchTime.TAG:
            sActivityLaunchTime.add(new ActivityLaunchTime(line));
            break;
        case ScheduleServiceRestart.TAG:
            sScheduleServiceRestart.add(new ScheduleServiceRestart(line));
            break;
        case ActivityFocused.TAG:
            sActivityFocused.add(new ActivityFocused(line));
            break;
        case Top.TAG:
            {
                int count = 0;
                int index = 0;
                while(-1 != (index = line.indexOf('%', index))) {
                    index++;
                    count++;
                    if(10 < count) {
                        break;
                    }
                }
                if(4 == count) {
                    // debugmsg(line);
                    sTop.add(new Top(line));
                }
            }
            break;
        default:
            //debugmsg("TAG NOT FOUND: " + tag);
            break;
        }
    }



    /**************************************************************************/
    private void debugmsg(String str) {
        System.out.println(str);
    }

    private void sortFilesInReverseOrder(File[] files) {
        for(int i = 0; i < files.length; i++) {
            for(int j = i+i; j < files.length; j++) {
                if(0 > files[i].getName().compareTo(files[j].getName())) {
                    File temp = files[j];
                    files[j] = files[i];
                    files[i] = temp;
                }
            }
        }
    }

    String getTagFromLine(String line) {
        String result = "";
        try {
            String[] strs = line.split(" +");
            String tag = strs[5];
            if(0 > tag.indexOf(':')) {
                result = tag;
            } else {
                result = tag.substring(0, tag.indexOf(':'));
            }
        } catch (Exception ex) {
            debugmsg(ex.getLocalizedMessage());
        }
        return result;
    }

    private boolean mIsCurrentYearSet = false;
    void getCurrentYear(File f) {
        if(mIsCurrentYearSet) {
            debugmsg(String.format("Current Year Is Set: %d", sYear));
            return;
        }
        BufferedReader bfr = null;
        try {
            bfr = new BufferedReader(new FileReader(f));
            String line;
            while(null != (line = bfr.readLine())) {
                if(5 > line.length() || !line.contains("UTC")) {
                    continue;
                } else {
                    int index = line.indexOf("-", 6);
                    if(4 < index) {
                        sYear = Integer.parseInt(line.substring(index - 4, index));
                        mIsCurrentYearSet = true;
                    }
                }
            }
            bfr.close();
        } catch (Exception ex) {
            debugmsg(ex.getLocalizedMessage());
        }
    }
    /**************************************************************************/
    private static int sYear = 2017;
    /**************************************************************************/
    /**
     * Basic class of all event class
     * parse the input line for the time stamp
     * */
    public class BaseEvent {
        protected long time;
        /**
         * Constructor of the basic class of event classes
         * parse input line for time stamp
         * */
        BaseEvent(String line) {
            if(18 > line.length()) {
                time = 0;
            } else {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy MM-dd HH:mm:ss.SSS");
                    String str_time = "" + sYear + " " + line.substring(0, 18);
                    //debugmsg(str_time);
                    time = sdf.parse(str_time).getTime();
                } catch(ParseException pex) {
                    debugmsg(pex.toString());
                }
            }
        }
        BaseEvent() {
            time = 0;
        }
        /**
         * Return the time stamp of the event
         * @return time stamp of the event
         */
        public long getTime() {
            return time;
        }
        protected String getEventValues(String line) {
            String result;
            int index_start = line.indexOf('[');
            int index_end = line.indexOf(']');
            if(-1 == index_start || -1 == index_end) {
                return "";
            }
            index_start += 1;
            result = line.substring(index_start, index_end);
            return result;
        }
        /**
         * Return string for this basic class
         * @return UTC time stamp
         */
        public String toString() {
            //String result = String.format("[%s]", new Date(time).toString());
            String result = "" + time;
            return result;
        }
    }
    static <T> ArrayList<T> getBaseEvent(ArrayList<T> list, long start, long end) {
        if(start > end) {
            return null;
        }
        ArrayList<T> result = new ArrayList<T>();
        for(T item:list) {
            if(((BaseEvent)item).getTime() > start &&
                    ((BaseEvent)item).getTime() < end) {
                result.add(item);
            }
        }
        return result;
    }

    /**
     * Input response time
     * */
    public class InputResponse extends BaseEvent {
        /**
         * The logcat tag in event logs
         */
        public final static String TAG = "input_response";
        InputResponse(String line) {
            super(line);
            String values = getEventValues(line);
            String[] params = values.split(",");
            window = params[0];
            input_response_time = Double.parseDouble(params[1]);
        }
        String window;
        Double input_response_time;
        /**
         * The method to get the window info
         * @return The information of the window
         */
        public String getWindow() {
            return window;
        }
        /**
         * The method to get the response time
         * @return The response time in millisecond.
         */
        public Double getInputResponseTime() {
            return input_response_time;
        }
        /**
         * Return a string represent an InputResponse object
         * @return "time,window,input response time"
         */
        public String toString() {
            return super.toString() + String.format(",%s,%f", window, input_response_time);
        }
    }
    private static ArrayList<InputResponse> sInputResponse = new ArrayList<InputResponse>();
    /**
     * The method to get the input response event within the given time period
     * @param starttime The begging time point of the given time period
     * @param endtime The end time of the given time period
     * @return An ArrayList contains the input response event within the given time period
     */
    public ArrayList<InputResponse> getInputResponse(long starttime, long endtime) {
        return getBaseEvent(sInputResponse, starttime, endtime);
    }

    /**
     * Memory condition change factor
     * */
    public class MemFactor extends BaseEvent {
        public final static String TAG = "am_mem_factor";
        MemFactor(String line) {
            super(line);
            String values = getEventValues(line);
            String[] params = values.split(",");
            newMemCondition = Integer.parseInt(params[0]);
            oldMemCondition = Integer.parseInt(params[1]);
        }
        Integer newMemCondition;
        Integer oldMemCondition;
        /**
         * The method to get the new memory condition
         * @return The new memory condition
         */
        public Integer getNewMemCondition() {
            return newMemCondition;
        }
        /**
         * The method to get the old memory condition
         * @return The old memory condition
         */
        public Integer getOldMemCondition() {
            return oldMemCondition;
        }
        /**
         * Return a string represent a memory factor object
         * @return "time,old memory condition,new memory condition"
         */
        public String toString() {
            return super.toString() + String.format(",%d,%d", oldMemCondition, newMemCondition);
        }

    }
    private static ArrayList<MemFactor> sMemFactor = new ArrayList<MemFactor>();
    /**
     * The method to get the required memory factor event within the given time period
     * @param starttime The start time of the period
     * @param endtime The end time of the period
     * @return An ArrayList of MemFactor within the time period between startime and endtime
     */
    public ArrayList<MemFactor> getMemFactor(long starttime, long endtime) {
        return getBaseEvent(sMemFactor, starttime, endtime);
    }

    /**
     * Memory Info
     * */
    public class MemInfo extends BaseEvent {
        public final static String TAG = "am_meminfo";
        MemInfo(String line) {
            super(line);
            String values = getEventValues(line);
            String[] params = values.split(",");
            Cached = Integer.parseInt(params[0]);
            Free = Integer.parseInt(params[1]);
            Zram = Integer.parseInt(params[2]);
            Kernel = Integer.parseInt(params[3]);
            Native = Integer.parseInt(params[4]);
        }
        Integer Cached;
        Integer Free;
        Integer Zram;
        Integer Kernel;
        Integer Native;
        /**
         * The method to get the cached size of global memory usage
         * @return Cached size of memory
         */
        public Integer getCachedSize() {
            return Cached;
        }
        /**
         * The method to get the free size of global memory usage
         * @return Free size of memory
         */
        public Integer getFreeSize() {
            return Free;
        }
        /**
         * The method to get the zram size of global memory usage
         * @return ZRam size
         */
        public Integer getZramSize() {
            return Zram;
        }
        /**
         * The method to get the kernel size of global memory usage
         * @return Kernel size
         */
        public Integer getKernelSize() {
            return Kernel;
        }
        /**
         * The method to get the native size of global memory usage
         * @return Native size
         * */
        public Integer getNativeSize() {
            return Native;
        }
        public String toString() {
            return super.toString() + String.format(",%d,%d,%d,%d,%d",
                    Cached, Free, Zram, Kernel, Native);
        }
    }
    private static ArrayList<MemInfo> sMemInfo = new ArrayList<MemInfo>();
    public ArrayList<MemInfo> getMemInfo(long starttime, long endtime) {
        return getBaseEvent(sMemInfo, starttime, endtime);
    }

    public class PSS extends BaseEvent {
        public final static String TAG = "am_pss";
        PSS(String line) {
            super(line);
            String values = getEventValues(line);
            String[] params = values.split(",");
            pid = Integer.parseInt(params[0]);
            uid = Integer.parseInt(params[1]);
            package_name = params[2];
            pss = Integer.parseInt(params[3]);
            uss = Integer.parseInt(params[4]);
            swappss = Integer.parseInt(params[5]);
        }
        Integer pid;
        Integer uid;
        String package_name;
        Integer pss;
        Integer uss;
        Integer swappss;
        /**
         * The method to get PID
         * @return PID
         */
        public Integer getPID() {
            return pid;
        }
        /**
         * The method to get UID
         * @return UID
         */
        public Integer getUID() {
            return uid;
        }
        /**
         * The method to get package name
         * @return Package name
         */
        public String getPackageName() {
            return package_name;
        }
        /**
         * The method to get PSS
         * @return PSS
         */
        public Integer getPSS() {
            return pss;
        }
        /**
         * The method to get USS
         * @return USS
         */
        public Integer getUSS() {
            return uss;
        }
        /**
         * The method to get Swap PSS
         * @return Swap PSS
         */
        public Integer getSwapPSS() {
            return swappss;
        }
        public String toString() {
            return super.toString() + String.format(",%d,%d,%s,%d,%d,%d",
                    pid, uid, package_name, pss, uss, swappss);
        }
    }
    private static ArrayList<PSS> sPSS = new ArrayList<PSS>();
    public ArrayList<PSS> getPSS(long starttime, long endtime) {
        return getBaseEvent(sPSS, starttime, endtime);
    }

    public class ANR extends BaseEvent {
        public final static String TAG = "am_anr";
        ANR(String line) {
            super(line);
            String values = getEventValues(line);
            String[] params = values.split(",");
            user = Integer.parseInt(params[0]);
            pid = Integer.parseInt(params[1]);
            package_name = params[2];
            flag = Integer.parseInt(params[3]);
            reason = params[4];
        }
        Integer user;
        Integer pid;
        String package_name;
        Integer flag;
        String reason;
        /**
         * The method to get User
         * @return User
         */
        public Integer getUser() {
            return user;
        }
        /**
         * The method to get PID
         * @return PID
         */
        public Integer getPID() {
            return pid;
        }
        /**
         * The method to get package name
         * @return Package name
         */
        public String getPackageName() {
            return package_name;
        }
        /**
         * The method to get flags
         * @return Flags
         */
        public Integer getFlag() {
            return flag;
        }
        /**
         * The method to get the ANR reason
         * @return ANR reason
         */
        public String getReason() {
            return reason;
        }

        public String toString() {
            return super.toString() + String.format(",%d,%d,%s,%d,%s",
                    user, pid, package_name, flag, reason);
        }
    }
    private static ArrayList<ANR> sANR = new ArrayList<ANR>();
    ArrayList<ANR> getANR(long starttime, long endtime) {
        return getBaseEvent(sANR, starttime, endtime);
    }

    public class Crash extends BaseEvent {
        public final static String TAG = "am_crash";
        Crash(String line) {
            super(line);
            String values = getEventValues(line);
            String[] params = values.split(",");
            pid = Integer.parseInt(params[0]);
            user = Integer.parseInt(params[1]);
            process_name= params[2];
            flag = Integer.parseInt(params[3]);
            exception = params[4];
            msg = params[5];
            file = params[6];
            line_number = Integer.parseInt(params[7]);
        }
        Integer user;
        Integer pid;
        String process_name;
        Integer flag;
        String exception;
        String msg;
        String file;
        Integer line_number;
        /**
         * The method to get User
         * @return User
         */
        public Integer getUser() {
            return user;
        }
        /**
         * The method to get PID
         * @return PID
         */
        public Integer getPID() {
            return pid;
        }
        /**
         * The method to get process name
         * @return Process name
         */
        public String getProcessName() {
            return process_name;
        }
        /**
         * The method to get flags
         * @return Flags
         */
        public Integer getFlag() {
            return flag;
        }
        /**
         * The method to get exception that caused the process crash
         * @return Exception
         */
        public String getException() {
            return exception;
        }
        /**
         * The method to get detail message about the exception that caused the process crash
         * @return Detail message
         */
        public String getExceptionMessage() {
            return msg;
        }
        /**
         * The method to get the file name in which the exception happened
         * @return File name with exception
         */
        public String getFileName() {
            return file;
        }
        /**
         * The method to get the line number in which the exception happened
         * @return Line number with exception
         */
        public Integer getLineNumber() {
            return line_number;
        }

        public String toString() {
            return super.toString() + String.format(",%d,%d,%s,%d,%s,%s,%s,%d",
                    user, pid, process_name, flag, exception, msg, file, line_number);
        }
    }
    private static ArrayList<Crash> sCrash = new ArrayList<Crash>();
    ArrayList<Crash> getCrash(long starttime, long endtime) {
        return getBaseEvent(sCrash, starttime, endtime);
    }

    public class ProcDied extends BaseEvent {
        public final static String TAG = "am_proc_died";
        ProcDied(String line) {
            super(line);
            String values = getEventValues(line);
            String[] params = values.split(",");
            user = Integer.parseInt(params[0]);
            pid = Integer.parseInt(params[1]);
            process_name = params[2];
        }
        Integer user;
        Integer pid;
        String process_name;
        /**
         * The method to get User
         * @return User
         */
        public Integer getUser() {
            return user;
        }
        /**
         * The method to get PID
         * @return PID
         */
        public Integer getPID() {
            return pid;
        }
        /**
         * The method to get process name
         * @return Process name
         */
        public String getProcessName() {
            return process_name;
        }
        public String toString() {
            return super.toString() + String.format(",%d,%d,%s", user, pid, process_name);
        }
    }
    private static ArrayList<ProcDied> sProcDied = new ArrayList<ProcDied>();
    public ArrayList<ProcDied> getProcDied(long starttime, long endtime) {
        return getBaseEvent(sProcDied, starttime, endtime);
    }
    
    public class Kill extends BaseEvent {
        public final static String TAG = "am_kill";
        Kill(String line) {
            super(line);
            String values = getEventValues(line);
            String[] params = values.split(",");
            user = Integer.parseInt(params[0]);
            pid = Integer.parseInt(params[1]);
            process_name = params[2];
            oom_adj = Integer.parseInt(params[3]);
            reason = params[4];
        }
        Integer user;
        Integer pid;
        String process_name;
        Integer oom_adj;
        String reason;
        /**
         * The method to get user
         * @return User
         */
        public Integer getUser() {
            return user;
        }
        /**
         * The method to get PID
         * @return PID
         */
        public Integer getPID() {
            return pid;
        }
        /**
         * The method to get process name
         * @return Process name
         */
        public String getProcessName() {
            return process_name;
        }
        /**
         * The method to get OOM adj
         * @return OOM adj
         */
        public Integer getOOMAdj() {
            return oom_adj;
        }
        /**
         * The method to get the reason to kill this process
         * @return The reason to kill this process
         */
        public String getReason() {
            return reason;
        }
        public String toString() {
            return super.toString() + String.format(",%d,%d,%s,%d,%s", user, pid, process_name, oom_adj, reason);
        }
    }
    private static ArrayList<Kill> sKill = new ArrayList<Kill>();
    public ArrayList<Kill> getKill(long starttime, long endtime) {
        return getBaseEvent(sKill, starttime, endtime);
    }

    public class ProcStart extends BaseEvent {
        public final static String TAG = "am_proc_start";
        ProcStart(String line) {
            super(line);
            String subline = line.substring(line.indexOf('[')+1, line.indexOf(']'));
            String[] params = subline.split(",");
            user = Integer.parseInt(params[0]);
            pid = Integer.parseInt(params[1]);
            uid = Integer.parseInt(params[2]);
            package_name = params[3];
            module_type = params[4];
            module_name = params[5];
        }
        Integer user;
        Integer pid;
        Integer uid;
        String package_name;
        String module_type;
        String module_name;
        /**
         * The method to get user
         * @return User
         */
        public Integer getUser() {
            return user;
        }
        /**
         * The method to get PID
         * @return PID
         */
        public Integer getPid() {
            return pid;
        }
        /**
         * The method to get UID
         * @return UID
         */
        public Integer getUid() {
            return uid;
        }
        /**
         * The method to get package name
         * @return Package name
         */
        public String getPackage() {
            return package_name;
        }
        /**
         * The method to get the module type (activity, service, receiver, provider...)
         * @return Module type
         */
        public String getModuleType() {
            return module_type;
        }
        /**
         * The method to get the module name
         * @return Module name
         */
        public String getModuleName() {
            return module_name;
        }
        public String toString() {
            String result = String.format("%d,%d,%d,%d,%s,%s,%s",
                    /*new Date(time).toString(),*/ time,
                    user, pid, uid, package_name, module_type, module_name);
            return result;
        }
    }
    private static ArrayList<ProcStart> sProcStart = new ArrayList<ProcStart>();
    /**
     * Return an ArrayList of ProcStart, with the time stamp within the time period specified with the starttime and endtime.
     * @param starttime a long number as the beginning of the time period, in which the am_proc_start events are required
     * @param endtime a long number as the ending of the time period, in whitch the am_proc_start events are required
     * @return an ArrayList with the ProcStart objects which are am_proc_start events within the time period from starttime to endtime.
     * */
    public ArrayList<ProcStart> getAmProcStart(final long starttime, final long endtime) {
        if(starttime > endtime) {
            return null;
        }
        ArrayList<ProcStart> result = new ArrayList<ProcStart>();
        for(ProcStart event:sProcStart) {
            if(starttime < event.getTime() && endtime > event.getTime()) {
                result.add(event);
            }
        }
        return result;
    }

    public class ProcBound extends BaseEvent {
        public final static String TAG = "am_proc_bound";
        ProcBound(String line) {
            super(line);
            String values = getEventValues(line);
            String[] params = values.split(",");
            user = Integer.parseInt(params[0]);
            pid = Integer.parseInt(params[1]);
            process_name = params[2];
        }
        Integer user;
        Integer pid;
        String process_name;
        /**
         * The method to get user
         * @return User
         */
        public Integer getUser() {
            return user;
        }
        /**
         * The method to get PID
         * @return PID
         */
        public Integer getPID() {
            return pid;
        }
        /**
         * The method to get process name
         * @return Process name
         */
        public String getProcessName() {
            return process_name;
        }

        public String toString() {
            return super.toString() + String.format(",%d,%d,%s", user, pid, process_name); 
        }
    }
    private static ArrayList<ProcBound> sProcBound = new ArrayList<ProcBound>();
    public ArrayList<ProcBound> getProcBound(long starttime, long endtime) {
        return getBaseEvent(sProcBound, starttime, endtime);
    }

    public class ActivityLaunchTime extends BaseEvent {
        public final static String TAG = "am_activity_launch_time";
        ActivityLaunchTime(String line) {
            super(line);
            String values = getEventValues(line);
            String[] params = values.split(",");
            user = Integer.parseInt(params[0]);
            token = Integer.parseInt(params[1]);
            component_name = params[2];
            this_time = Integer.parseInt(params[3]);
            total_time = Integer.parseInt(params[4]);
        }
        Integer user;
        Integer token;
        String component_name;
        Integer this_time;
        Integer total_time;
        /**
         * The method to get user
         * @return User
         */
        public Integer getUser() {
            return user;
        }
        /**
         * The method to get token
         * @return Token
         */
        public Integer getToken() {
            return token;
        }
        /**
         * The method to get component name
         * @return Component name
         */
        public String getComponentName() {
            return component_name;
        }
        /**
         * The method to get current launch time
         * @return Current launch time
         */
        public Integer getThisTime() {
            return this_time;
        }
        /**
         * The method to get total launch time
         * @return Total launch time
         */
        public Integer getTotalTime() {
            return total_time;
        }
        public String toString() {
            return super.toString() + String.format(",%d,%d,%s,%d,%d", user, token, component_name, this_time, total_time); 
        }
    }
    private static ArrayList<ActivityLaunchTime> sActivityLaunchTime = new ArrayList<ActivityLaunchTime>();
    public ArrayList<ActivityLaunchTime>getActivityLaunchTime(long starttime, long endtime) {
        return getBaseEvent(sActivityLaunchTime, starttime, endtime);
    }

    public class ScheduleServiceRestart extends BaseEvent {
        public final static String TAG = "am_schedule_service_restart";
        ScheduleServiceRestart(String line) {
            super(line);
            String values = getEventValues(line);
            String[] params = values.split(",");
            user = Integer.parseInt(params[0]);
            component_name = params[1];
            delay = Integer.parseInt(params[2]);
        }
        Integer user;
        String component_name;
        Integer delay;
        /**
         * The method to get user
         * @return User
         */
        public Integer getUser() {
            return user;
        }
        /**
         * The method to get component name
         * @return Component name
         */
        public String getComponentName() {
            return component_name;
        }
        /**
         * The method to get the delay time for the next restart of the service
         * @return Delay time
         */
        public Integer getDelay() {
            return delay;
        }
        
        public String toString() {
            return super.toString() + String.format("%d,%s,%d", user, component_name, delay);
        }
    }
    private static ArrayList<ScheduleServiceRestart> sScheduleServiceRestart = new ArrayList<ScheduleServiceRestart>();
    public ArrayList<ScheduleServiceRestart> getScheduleServiceRestart(long starttime, long endtime) {
        return getBaseEvent(sScheduleServiceRestart, starttime, endtime);
    }

    public class ActivityFocused extends BaseEvent {
        public final static String TAG = "am_focused_activity";
        ActivityFocused(String line) {
            super(line);
            String values = getEventValues(line);
            String[] params = values.split(",");
            user = Integer.parseInt(params[0]);
            component_name = params[1];
            reason = params[2];
        }
        Integer user;
        String component_name;
        String reason;
        /**
         * The method to get user
         * @return User
         */
        public Integer getUser() {
            return user;
        }
        /**
         * The method to get component name
         * @return Component name
         */
        public String getComponentName() {
            return component_name;
        }
        /**
         * The method to get the reason of focused
         * @return Reason
         */
        public String getReason() {
            return reason;
        }
        public String toString() {
            return super.toString() + String.format("%d,%s,%s", user, component_name, reason);
        }
    }
    private static ArrayList<ActivityFocused> sActivityFocused = new ArrayList<ActivityFocused>();
    public ArrayList<ActivityFocused> getActivityFocused(long starttime, long endtime) {
        return getBaseEvent(sActivityFocused, starttime, endtime);
    }


    public class Top extends BaseEvent {
        public static final String TAG = "top";
        Top(String line) {
            super(line);
            int index = line.indexOf(USER);
            user_usage = Integer.parseInt(line.substring(index + USER.length(), line.indexOf('%', index)).trim());
            index = line.indexOf(SYSTEM);
            system_usage = Integer.parseInt(line.substring(index + SYSTEM.length(), line.indexOf('%', index)).trim());
            index = line.indexOf(IOW);
            io_wait = Integer.parseInt(line.substring(index + IOW.length(), line.indexOf('%', index)).trim());
            index = line.indexOf(IRQ);
            irq = Integer.parseInt(line.substring(index + IRQ.length(), line.indexOf('%', index)).trim());
        }
        private static final String USER = "User";
        private static final String SYSTEM = "System";
        private static final String IOW = "IOW";
        private static final String IRQ = "IRQ";
        Integer user_usage;
        Integer system_usage;
        Integer io_wait;
        Integer irq;
        /**
         * The method to get the CPU loading of user usage
         * @return User usage
         */
        public Integer getUserUsage() {
            return user_usage;
        }
        /**
         * The method to get the CPU loading of system usage
         * @return System usage
         */
        public Integer getSystemUsage() {
            return system_usage;
        }
        /**
         * The method to get the CPU loading of IO waiting
         * @return IO waiting
         */
        public Integer getIOWait() {
            return io_wait;
        }
        /**
         * The method to get the CPU loading of IRQ
         * @return IRQ
         */
        public Integer getIRQ() {
            return irq;
        }
        public String toString() {
//            return super.toString() + String.format(" %s[%d]; %s[%d]; %s[%d]; %s[%d]",
//                    USER, user_usage, SYSTEM, system_usage, IOW, io_wait, IRQ, irq);
            return super.toString() + String.format("%d,%d,%d,%d", user_usage, system_usage, io_wait, irq);
        }
    }
    private static ArrayList<Top> sTop = new ArrayList<Top>();
    public ArrayList<Top> getTop(long starttime, long endtime) {
        return getBaseEvent(sTop, starttime, endtime);
    }




    /**
     * Print the first, the middle, and the last am_proc_start event for debugging
     * @param obj BaseEvent object, 
     *  */
    //  TODO: Use this method to show all events for debugging
    String getFields(Object obj) {
        String result = "";
        Field[] fields = obj.getClass().getDeclaredFields();
        for(Field f:fields) {
            result += ",";
            try {
                result += f.get(obj);
            } catch (Exception ex) {
                //debugmsg(ex.getLocalizedMessage());
                continue;
            }
        }
        return ((BaseEvent)obj).getTime() + result;
    }

    <T> void showBaseEventArrayContent(ArrayList<T> list) {
        if(null == list || 0 == list.size()) {
            return;
        }

        if(1 == list.size()) {
            debugmsg(getFields(list.get(0)));
        } else if(2 == list.size()) {
            debugmsg(getFields(list.get(0)));
            debugmsg(getFields(list.get(1)));
        } else {
            int index = 0;
            // index = 0
            debugmsg(getFields(list.get(index)));
            // (index = list.size()-1)/2
            index = (list.size()-1)/2;
            debugmsg(getFields(list.get(index)));
            // index = list.size()-1
            index = list.size()-1;
            debugmsg(getFields(list.get(index)));
        }
    }
    /**
     * The method to print the first, middle and the last events of each tag
     * This method is used for debugging
     */
    public void getEventLogStatistics() {
        debugmsg("[sInputResponse]");
        showBaseEventArrayContent(sInputResponse);
        debugmsg("[sMemFactor]");
        showBaseEventArrayContent(sMemFactor);
        debugmsg("[sMemInfo]");
        showBaseEventArrayContent(sMemInfo);
        debugmsg("[sPSS]");
        showBaseEventArrayContent(sPSS);
        debugmsg("[sANR]");
        showBaseEventArrayContent(sANR);
        debugmsg("[sCrash]");
        showBaseEventArrayContent(sCrash);
        debugmsg("[sProcDied]");
        showBaseEventArrayContent(sProcDied);
        debugmsg("[sKill]");
        showBaseEventArrayContent(sKill);
        debugmsg("[sProcStart]");
        showBaseEventArrayContent(sProcStart);
        debugmsg("[sProcBound]");
        showBaseEventArrayContent(sProcBound);
        debugmsg("[sActivityLaunchTime]");
        showBaseEventArrayContent(sActivityLaunchTime);
        debugmsg("[sScheduleServiceRestart]");
        showBaseEventArrayContent(sScheduleServiceRestart);
        debugmsg("[sActivityFocused]");
        showBaseEventArrayContent(sActivityFocused);
        debugmsg("[sTop]");
        showBaseEventArrayContent(sTop);
    }




}





