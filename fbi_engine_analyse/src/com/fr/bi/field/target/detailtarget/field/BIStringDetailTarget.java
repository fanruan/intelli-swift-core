package com.fr.bi.field.target.detailtarget.field;

import com.finebi.cube.conf.table.BIBusinessTable;
import com.finebi.cube.conf.table.BusinessTable;
import com.fr.bi.field.target.detailtarget.BIAbstractDetailTarget;

import java.util.Map;

public class BIStringDetailTarget extends BIAbstractDetailTarget {

    /**
     * 获取显示值
     *
     * @param value 明细表一行值
     * @return 显示值
     */
    @Override
    public Object createShowValue(Object value) {
        return value;
    }

    @Override
    public BusinessTable createTableKey() {
        return new BIBusinessTable(column.getTableBelongTo().getID());
    }

    /**
     * 是否为计算指标
     *
     * @return 是否为计算指标
     */
    @Override
    public boolean isCalculateTarget() {
        return false;
    }

    /**
     * 可否计算，看他调用到的指标有没有算完，处理计算指标嵌套情况
     *
     * @param values 指标名和值的map
     * @return 可否计算
     */

    @Override
    public boolean isReady4Calculate(Map<String, Object> values) {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BIStringDetailTarget)) {
            return false;
        }

        return true;
    }


}