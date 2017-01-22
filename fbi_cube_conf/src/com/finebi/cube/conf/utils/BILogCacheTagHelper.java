package com.finebi.cube.conf.utils;

import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.bi.stable.constant.BILogConstant;
import com.fr.bi.stable.utils.program.BIStringUtils;

/**
 * Created by roy on 2017/1/22.
 */
public class BILogCacheTagHelper {
    public static String getCubeLogTransportTimeSubTag(String tableSourceID, String startOrEnd) {
        String subTag = BIStringUtils.emptyString();
        if (!BIStringUtils.isEmptyString(tableSourceID)) {
            subTag = tableSourceID + "_" + BILogConstant.LOG_CACHE_SUB_TAG.TRANSPORT_EXECUTE_TIME + startOrEnd;
        } else {
            BILoggerFactory.getLogger(BILogHelper.class).error("The TableSourceID is empty");
        }
        return subTag;
    }

    public static String getCubeLogExceptionSubTag(String tableSourceID) {
        String subTag = BIStringUtils.emptyString();
        if (!BIStringUtils.isEmptyString(tableSourceID)) {
            subTag = tableSourceID;
        } else {
            BILoggerFactory.getLogger(BILogHelper.class).error("The TableSourceID is empty");
        }
        return subTag;
    }


}
