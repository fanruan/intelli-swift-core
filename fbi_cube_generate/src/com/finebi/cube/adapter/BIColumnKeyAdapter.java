package com.finebi.cube.adapter;

import com.finebi.cube.structure.column.BIColumnKey;
import com.finebi.cube.structure.column.date.BIDateColumnTool;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.data.db.DBField;
import com.fr.bi.stable.utils.program.BINonValueUtils;

/**
 * Created by Young's on 2016/4/23.
 */
public class BIColumnKeyAdapter {
    public static final BIColumnKey covert(DBField field, int type) {
        BIColumnKey columnKey ;
        switch (type) {
            case BIReportConstant.GROUP.Y:
             columnKey=   BIDateColumnTool.generateYear(field);
                break;
            case BIReportConstant.GROUP.M:
             columnKey=   BIDateColumnTool.generateMonth(field);
                break;
            case BIReportConstant.GROUP.MD:
             columnKey=   BIDateColumnTool.generateDay(field);
                break;
            case BIReportConstant.GROUP.S:
             columnKey=   BIDateColumnTool.generateSeason(field);
                break;
            case BIReportConstant.GROUP.W:
              columnKey=  BIDateColumnTool.generateWeek(field);
                break;
            case BIReportConstant.GROUP.YMD:
              columnKey=  BIDateColumnTool.generateYearMonthDay(field);
                break;
            default:
                throw BINonValueUtils.beyondControl();
        }
        return columnKey;
    }


}
