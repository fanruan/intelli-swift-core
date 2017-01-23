package com.finebi.cube.common.log;

import com.fr.stable.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by roy on 2017/1/22.
 */
public class BILogExceptionInfo {
    private long occurTime;
    private String exceptionMessage;
    private String operation;

    public BILogExceptionInfo(long occurTime, String operation, String exceptionMessage) {
        this.occurTime = occurTime;
        this.exceptionMessage = exceptionMessage;
        this.operation = operation;
    }

    @Override
    public String toString() {
        String exceptionLogString = StringUtils.EMPTY;
        try {
            Date date = new Date(occurTime);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            StringBuffer sb = new StringBuffer();
            sb.append("\n" + "The Exception Occur Time: " + simpleDateFormat.format(date));
            sb.append("\n" + "The Exception Operation: " + operation);
            sb.append("\n" + "The Exception Message: " + exceptionMessage);
            exceptionLogString = sb.toString();
        } catch (Exception e) {
            BILoggerFactory.getLogger(this.getClass()).error(e.getMessage(), e);
        }
        return exceptionLogString;
    }
}
