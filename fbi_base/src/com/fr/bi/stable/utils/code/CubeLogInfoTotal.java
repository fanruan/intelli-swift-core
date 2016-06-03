package com.fr.bi.stable.utils.code;


import java.util.LinkedList;
import java.util.List;

/**
 * Created by wuk on 16/6/2.
 */
public class CubeLogInfoTotal {
    private long costTime;
    private String message;
    private String errorMsg;
    private List cubeGenerateLogList;

    public CubeLogInfoTotal(long costTime, String errorInfo, String errorMsg) {
        this.costTime = costTime;
        this.message = errorInfo;
        this.errorMsg = errorMsg;
        this.cubeGenerateLogList = new LinkedList();
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

    

}

