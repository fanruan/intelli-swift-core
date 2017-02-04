package com.finebi.cube.conf.utils;

import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.bi.stable.utils.program.BIStringUtils;

/**
 * Created by roy on 2017/1/22.
 */
public class BILogCacheTagHelper {
    public static String getCubeLogSubTag(String tableSourceID) {
        String subTag = BIStringUtils.emptyString();
        if (!BIStringUtils.isEmptyString(tableSourceID)) {
            subTag = tableSourceID;
        } else {
            BILoggerFactory.getLogger(BILogHelper.class).error("The TableSourceID is empty");
        }
        return subTag;
    }


}
