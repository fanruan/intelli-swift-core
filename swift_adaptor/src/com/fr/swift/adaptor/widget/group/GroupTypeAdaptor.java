package com.fr.swift.adaptor.widget.group;

import com.finebi.conf.constant.BICommonConstants.GROUP;
import com.fr.swift.query.group.GroupType;

import static com.finebi.conf.constant.BIConfConstants.CONF.GROUP.DATE;

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
//            case GROUP.YD:
//                return GroupType.Y_D;
            case GROUP.YW:
                return GroupType.Y_W;
//            case GROUP.MD:
//                return GroupType.M_D;
            default:
                return null;
        }
    }

    static GroupType adaptSingleValueGroupType(int type) {
        switch (type) {
            // 相同值为一组
            case 1:
                return GroupType.NONE;
            case DATE.YEAR:
                return GroupType.YEAR;
            case DATE.QUARTER:
                return GroupType.QUARTER;
            case DATE.MONTH:
                return GroupType.MONTH;
            case DATE.WEEK_COUNT:
                return GroupType.WEEK_OF_YEAR;
            case DATE.WEEKDAY:
                return GroupType.WEEK;
            case DATE.DAY:
                return GroupType.DAY;
            case DATE.HOUR:
                return GroupType.HOUR;
            case DATE.MINUTE:
                return GroupType.MINUTE;
            case DATE.SECOND:
                return GroupType.SECOND;
            case DATE.YMDHMS:
                return GroupType.Y_M_D_H_M_S;
            case DATE.YMDHM:
                return GroupType.Y_M_D_H_M;
            case DATE.YMDH:
                return GroupType.Y_M_D_H;
            case DATE.DATE:
                return GroupType.Y_M_D;
            case DATE.YQ:
                return GroupType.Y_Q;
            case DATE.YM:
                return GroupType.Y_M;
            case DATE.YW:
                return GroupType.Y_W;
            default:
                return null;
        }
    }
}