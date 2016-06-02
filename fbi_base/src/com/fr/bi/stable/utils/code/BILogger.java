package com.fr.bi.stable.utils.code;


/**
 * BI日志输出
 */
public class BILogger {
    boolean verbose = true;
    public static BILogger logger = null;
//    public LogInfo topicLog;
//    public LogInfo fragmentLog;
//    public LogInfo status;
    public LogInfo logInfo=new LogInfo(0,"","");

    public static BILogger getLogger() {
        if (logger != null) {
            return logger;
        }
        synchronized (BILogger.class) {
            if (logger == null) {
                logger = new BILogger();
                logger.logInfo=new LogInfo(0,"","");
            }
        }
        return logger;
    }
    public void error(String message) {
        System.err.println(message);
        addLog(0,"",message);
    }

    public void error(String message, Throwable e) {
        System.err.println(message);
        e.printStackTrace();
        addLog(0,"",message);
    }

    public void info(String message) {
        System.out.println(message);
        addLog(0,message,"");
    }

    public void debug(String message) {
        if (verbose) {
            System.out.println(message);
            addLog(0,message,"");
        }
    }
    
    public void addLog(long costTime,String message,String errMsg){
        this.logInfo.setCostTime(this.logInfo.getCostTime()+costTime);
        this.logInfo.setMessage(this.logInfo.getMessage()+message);
        this.logInfo.setErrorMsg(this.logInfo.getErrorMsg()+errMsg);
//        this.logInfo.logInfo.add(log);
    };
    public LogInfo getLogInfo(){
        return logInfo;
    }
}
