package com.fr.bi.field.target.detailtarget.field;

import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.conf.field.BusinessField;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.engine.index.key.IndexKey;
import com.fr.bi.stable.engine.index.key.IndexTypeKey;
import com.fr.bi.stable.utils.BICollectionUtils;

import java.util.Map;

public class BIDateDetailTarget extends BIStringDetailTarget {

    private static final long serialVersionUID = -3300500860997448514L;

    /**
     * 第一个前面不插入"0"的月份如4-->"04" PMD...
     */
    private static int FIRST_NOT_INSERT_ZERO_MONTH = 10;

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
                // 直接返回原始值
                return columnDetailGetter.getValue(r);
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
        // 不是空值的时候才进行下面的处理
        if (BICollectionUtils.isCubeNullKey(value)) {
            return null;
        }
        return value;
    }

    private Object insertZero(int time) {
        if (time < FIRST_NOT_INSERT_ZERO_MONTH) {
            return "0" + time;
        }
        return "" + time;
    }

}