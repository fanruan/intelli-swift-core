package com.fr.bi.field.target.detailtarget.field;

import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.conf.field.BusinessField;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.engine.index.key.IndexKey;
import com.fr.bi.stable.engine.index.key.IndexTypeKey;

import java.util.Calendar;
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
                initialTableSource(loader);
                Object ob =  columnDetailGetter.getValue(r);
                if (ob == null){
                    return ob;
                }
                return group.getType() == BIReportConstant.GROUP.M ? ((Number)ob).longValue() + 1 : ((Number)ob).longValue();
            }
        }
        return null;
    }

    @Override
    public BIKey createKey(BusinessField column) {
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
        Calendar c = Calendar.getInstance();
        switch (group.getType()) {
            case BIReportConstant.GROUP.YMD:
                c.setTimeInMillis((Long) value);
                return c.get(Calendar.YEAR) + "/" + insertZero(c.get(Calendar.MONTH) + 1) + "/" + insertZero(c.get(Calendar.DAY_OF_MONTH));
            case BIReportConstant.GROUP.YMDHMS:
                c.setTimeInMillis((Long) value);
                return c.get(Calendar.YEAR) + "/"
                        + insertZero(c.get(Calendar.MONTH) + 1) + "/"
                        + insertZero(c.get(Calendar.DAY_OF_MONTH)) + "/ "
                        + insertZero(c.get(Calendar.HOUR_OF_DAY)) + ":"
                        + insertZero(c.get(Calendar.MINUTE)) + ":"
                        + insertZero(c.get(Calendar.SECOND));
        }
        return value;
    }

    private Object insertZero(int time) {
        if (time < 10) {
            return "0" + time;
        }
        return "" + time;
    }

}