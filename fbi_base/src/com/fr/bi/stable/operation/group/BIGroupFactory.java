package com.fr.bi.stable.operation.group;

import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.operation.group.group.*;
import com.fr.bi.stable.operation.group.group.date.*;
import com.fr.json.JSONObject;

/**
 * Created by GUY on 2015/4/9.
 */
public class BIGroupFactory {

    public static IGroup parseGroup(JSONObject jo) throws Exception {
        IGroup group = null;
        int type = jo.optInt("type", BIReportConstant.GROUP.NO_GROUP);
        switch (type) {
            case BIReportConstant.GROUP.NO_GROUP:
                group = new NoGroup();
                break;
            case BIReportConstant.GROUP.AUTO_GROUP:
                group = new AutoGroup();
                break;
            case BIReportConstant.GROUP.CUSTOM_GROUP:
                group = new CustomGroup();
                break;
            case BIReportConstant.GROUP.CUSTOM_NUMBER_GROUP:
                group = new CustomNumberGroup();
                break;
            case BIReportConstant.GROUP.ID_GROUP:
                group = new IdGroup();
                break;
            case BIReportConstant.GROUP.YMD:
                group = new YMDGroup();
                break;
            case BIReportConstant.GROUP.Y:
                group = new YearGroup();
                break;
            case BIReportConstant.GROUP.S:
                group = new SeasonGroup();
                break;
            case BIReportConstant.GROUP.M:
                group = new MonthGroup();
                break;
            case BIReportConstant.GROUP.W:
                group = new WeekGroup();
                break;
            case BIReportConstant.GROUP.YMDHMS:
                group = new YMDHMSGroup();
                break;
            default:
                group = new NoGroup();
                break;
        }
        if (group != null) {
            group.parseJSON(jo);
        }
        return group;
    }

    public static IGroup parseNumberGroup(JSONObject jo) throws Exception {
        IGroup group = null;
        int type = jo.optInt("type", BIReportConstant.GROUP.AUTO_GROUP);
        switch (type) {
            case BIReportConstant.GROUP.NO_GROUP:
                group = new NoGroup();
                break;
            case BIReportConstant.GROUP.AUTO_GROUP:
                group = new AutoGroup();
                break;
            case BIReportConstant.GROUP.CUSTOM_NUMBER_GROUP:
                group = new CustomNumberGroup();
                break;
            default:
                group = new NoGroup();
                break;
        }
        if (group != null) {
            group.parseJSON(jo);
        }
        return group;
    }

    public static IGroup parseStringGroup(JSONObject jo) throws Exception {
        IGroup group = null;
        int type = jo.optInt("type", BIReportConstant.GROUP.NO_GROUP);
        switch (type) {
            case BIReportConstant.GROUP.NO_GROUP:
                group = new NoGroup();
                break;
            case BIReportConstant.GROUP.CUSTOM_GROUP:
                group = new CustomGroup();
                break;
            default:
                group = new NoGroup();
                break;
        }
        if (group != null) {
            group.parseJSON(jo);
        }
        return group;
    }

    public static IGroup parseDateGroup(JSONObject jo) throws Exception {
        IGroup group = null;
        int type = jo.optInt("type", BIReportConstant.GROUP.NO_GROUP);
        switch (type) {
            case BIReportConstant.GROUP.NO_GROUP:
                group = new NoGroup();
                break;
            case BIReportConstant.GROUP.YMD:
                group = new YMDGroup();
                break;
            case BIReportConstant.GROUP.Y:
                group = new YearGroup();
                break;
            case BIReportConstant.GROUP.S:
                group = new SeasonGroup();
                break;
            case BIReportConstant.GROUP.M:
                group = new MonthGroup();
                break;
            case BIReportConstant.GROUP.W:
                group = new WeekGroup();
                break;
            case BIReportConstant.GROUP.YMDHMS:
                group = new YMDHMSGroup();
                break;
            default:
                group = new NoGroup();
                break;
        }
        if (group != null) {
            group.parseJSON(jo);
        }
        return group;
    }
}