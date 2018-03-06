package com.fr.swift.adaptor.widget.group;

import com.finebi.conf.constant.BIReportConstant.GROUP;
import com.fr.swift.query.group.GroupType;

/**
 * @author anchore
 * @date 2018/2/28
 */
public class GroupTypeAdaptor {
    public static GroupType adaptGroupType(int groupType) {
        switch (groupType) {
            case GROUP.AUTO_GROUP:
                return GroupType.AUTO;
            case GROUP.CUSTOM_GROUP:
                return GroupType.CUSTOM;
            case GROUP.CUSTOM_NUMBER_GROUP:
                return GroupType.CUSTOM_NUMBER;
            case GROUP.NO_GROUP:
                return GroupType.NONE;
            case GROUP.Y:
                return GroupType.YEAR;
            case GROUP.S:
                return GroupType.QUARTER;
            case GROUP.M:
                return GroupType.MONTH;
            case GROUP.D:
                return GroupType.DAY;
            case GROUP.W:
                return GroupType.WEEK;
            case GROUP.HOUR:
                return GroupType.HOUR;
            case GROUP.MINUTE:
                return GroupType.MINUTE;
            case GROUP.SECOND:
                return GroupType.SECOND;
            case GROUP.WEEK_COUNT:
                return GroupType.WEEK_OF_YEAR;
            case GROUP.YMDHMS:
                return GroupType.Y_M_D_H_M_S;
            case GROUP.YMDHM:
                return GroupType.Y_M_D_H_M;
            case GROUP.YMDH:
                return GroupType.Y_M_D_H;
            case GROUP.YMD:
                return GroupType.Y_M_D;
            case GROUP.YS:
                return GroupType.Y_Q;
            case GROUP.YM:
                return GroupType.Y_M;
            case GROUP.YD:
                return GroupType.Y_D;
            case GROUP.YW:
                return GroupType.Y_W;
            case GROUP.MD:
                return GroupType.M_D;
            default:
                return null;
        }
    }
}