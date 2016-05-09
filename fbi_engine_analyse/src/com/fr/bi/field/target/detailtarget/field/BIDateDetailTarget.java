package com.fr.bi.field.target.detailtarget.field;

import com.fr.bi.base.key.BIKey;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.constant.DateConstant;
import com.fr.bi.stable.data.BIField;
import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.api.ICubeTableService;
import com.fr.bi.stable.engine.index.key.IndexKey;
import com.fr.bi.stable.engine.index.key.IndexTypeKey;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

public class BIDateDetailTarget extends BIStringDetailTarget {

    /**
     * 取值
     *
     * @param row    行
     * @param values 相关的值map
     * @return 实际值
     */
    @Override
    public Object createDetailValue(Long row, Map<String, Object> values, ICubeDataLoader loader, long userId) {
        if (row != null) {
            int r = row.intValue();
            if (r > -1) {
                ICubeTableService ti = loader.getTableIndex(this.createTableKey());
                return ti.getRowValue(this.createKey(getStatisticElement()), r);
            }
        }
        return null;
    }

    @Override
    public BIKey createKey(BIField column) {
        if (group.getType() == BIReportConstant.GROUP.NO_GROUP) {
            return new IndexKey(column.getFieldName());
        }
        return new IndexTypeKey(column.getFieldName(), group.getType());
    }

    /**
     * 获取显示值
     *
     * @param value 明细表一行值
     * @return 显示值
     */
    @Override
    public Object createShowValue(Object value) {
        if (value == null) {
            return null;
        }
        if (group.getType() == BIReportConstant.GROUP.YMD) {
            Date date = new Date((Long) value);
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            return c.get(Calendar.YEAR) + "/" + insertZero(c.get(Calendar.MONTH) + 1) + "/" + insertZero(c.get(Calendar.DAY_OF_MONTH));
        }
        return value;
    }

    private Object insertZero(int time) {
        if (time < 10) {
            return "0" + time;
        }
        return "" + time;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BIDateDetailTarget)) {
            return false;
        }

        BIDateDetailTarget that = (BIDateDetailTarget) o;

        if (group != that.group) {
            return false;
        }

        return true;
    }

}