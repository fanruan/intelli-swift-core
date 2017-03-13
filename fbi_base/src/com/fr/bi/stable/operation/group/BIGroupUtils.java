package com.fr.bi.stable.operation.group;

import com.fr.bi.stable.constant.BIReportConstant;

/**
 * Created by 小灰灰 on 2017/3/9.
 */
public class BIGroupUtils {
    public static boolean isCustomGroup(IGroup group) {
        if (group == null){
            return false;
        }
        int groupType = group.getType();
        return groupType == BIReportConstant.GROUP.CUSTOM_GROUP
                || groupType == BIReportConstant.GROUP.CUSTOM_NUMBER_GROUP
                || groupType == BIReportConstant.GROUP.AUTO_GROUP;
    }
}
