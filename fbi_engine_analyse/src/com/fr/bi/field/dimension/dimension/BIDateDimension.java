package com.fr.bi.field.dimension.dimension;

import com.fr.bi.base.key.BIKey;
import com.fr.bi.conf.report.widget.BIDataColumn;
import com.fr.bi.field.dimension.calculator.DateDimensionCalculator;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.data.BIField;
import com.fr.bi.stable.engine.index.key.IndexTypeKey;
import com.fr.bi.stable.relation.BITableSourceRelation;
import com.fr.bi.stable.report.result.DimensionCalculator;
import com.fr.stable.StringUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class BIDateDimension extends BIAbstractDimension {

    /**
     * 转化string
     *
     * @param v 值
     * @return 转化的string
     */
    @Override
    public String toString(Object v) {
        if (group.getType() == BIReportConstant.GROUP.YMD) {
            Date date = new Date(Long.parseLong((String) v));
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            return c.get(Calendar.YEAR) + "/" + insertZero(c.get(Calendar.MONTH) + 1) + "/" + insertZero(c.get(Calendar.DAY_OF_MONTH)).toString();

        }


        if (v != null) {
            return v.toString();
        }
        return "";
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BIDateDimension)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        return true;
    }


    @Override
    public BIKey createKey(BIField column) {
        return new IndexTypeKey(column.getFieldName(), group.getType());
    }

    @Override
    public DimensionCalculator createCalculator(BIDataColumn column, List<BITableSourceRelation> relations) {
        return new DateDimensionCalculator(this, column, relations);
    }

    private Object insertZero(int time) {
        if (time < 10) {
            return "0" + time;
        }
        return "" + time;
    }

    @Override
    public Object getValueByType(Object data) {
        if (group.getType() == BIReportConstant.GROUP.YMD){
            return data == null ? null : Long.parseLong(data.toString());
        }
        return data == null ? StringUtils.EMPTY : Integer.parseInt(data.toString());
    }
}