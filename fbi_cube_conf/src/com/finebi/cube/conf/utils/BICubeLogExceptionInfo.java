package com.finebi.cube.conf.utils;

import com.finebi.cube.common.log.BILogExceptionInfo;
import com.fr.stable.StringUtils;

/**
 * Created by roy on 2017/1/23.
 */
public class BICubeLogExceptionInfo extends BILogExceptionInfo {
    private String tableSourceID;
    private String fieldName = StringUtils.EMPTY;


    public BICubeLogExceptionInfo(long occurTime, String operation, String exceptionMessage, Exception exception, String tableSourceID) {
        super(occurTime, operation, exceptionMessage, exception);
        this.tableSourceID = tableSourceID;
    }

    public BICubeLogExceptionInfo(long occurTime, String operation, String exceptionMessage, Exception exception, String tableSourceID, String fieldName) {
        super(occurTime, operation, exceptionMessage, exception);
        this.tableSourceID = tableSourceID;
        this.fieldName = fieldName;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("\n" + "The Error TableInfo: " + BILogHelper.logCubeLogTableSourceInfo(this.tableSourceID));
        if (!StringUtils.isEmpty(fieldName)) {
            sb.append("\n" + "The Error Field is: " + fieldName);
        }
        sb.append(super.toString());
        return sb.toString();
    }
}
