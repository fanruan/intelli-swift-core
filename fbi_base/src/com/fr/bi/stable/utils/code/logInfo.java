package com.fr.bi.stable.utils.code;

/**
 * Created by wuk on 16/6/2.
 */
public class LogInfo {
    private long costTime;
    private String message;
    private String errorMsg;

    public LogInfo(long costTime, String errorInfo, String errorMsg) {
        this.costTime = costTime;
        this.message = errorInfo;
        this.errorMsg = errorMsg;
    }

    public void setCostTime(long costTime) {
        this.costTime = costTime;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public long getCostTime() {
        return costTime;
    }

    public String getMessage() {
        return message;
    }

    public String getErrorMsg() {
        return errorMsg;
    }
//    public LogInfo add(LogInfo logInfo){
//        this.setCostTime(this.getCostTime()+logInfo.getCostTime());
//        this.setMessage(this.getMessage()+logInfo.getMessage());
//        this.setErrorMsg(this.errorMsg+logInfo.getErrorMsg());
//       return this; 
//    }
}

