package com.fr.bi.field.target.detailtarget.field;

import com.fr.bi.field.target.detailtarget.BIAbstractDetailTarget;
import com.fr.bi.stable.data.BITable;
import com.fr.bi.stable.data.Table;
import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.api.ICubeColumnIndexReader;

import java.util.Map;

public class BIEmptyDetailTarget extends BIAbstractDetailTarget {

    public BIEmptyDetailTarget(String name) {
        this.setValue(name);
    }

    /**
     * 创建明细值
     *
     * @param row    行
     * @param values 值
     * @param loader loader对象
     * @return object对象
     */
    @Override
    public Object createDetailValue(Long row, Map<String, Object> values, ICubeDataLoader loader, long userId) {
        return null;
    }

    @Override
    public BITable createTableKey() {
        return null;
    }

    /**
     * 是否是计算指标
     *
     * @return true或false
     */
    @Override
    public boolean isCalculateTarget() {
        return false;
    }

    /**
     * 准备计算
     *
     * @param values 值
     * @return true或false
     */
    @Override
    public boolean isReady4Calculate(Map<String, Object> values) {
        return true;
    }

    /**
     * 创建显示值
     *
     * @param value 值
     * @return 显示值
     */
    @Override
    public Object createShowValue(Object value) {
        return null;
    }

    /**
     * 索引map
     *
     * @param target 指标
     * @param loader loader对象
     * @return 索引map
     */
    @Override
    public ICubeColumnIndexReader createGroupValueMapGetter(Table target, ICubeDataLoader loader, long userId) {
        return null;
    }

    ;

}