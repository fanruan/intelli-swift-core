package com.fr.bi.conf.report.widget.field.target.detailtarget;

import com.fr.bi.common.inter.Release;
import com.fr.bi.conf.report.widget.field.BITargetAndDimension;
import com.fr.bi.conf.report.widget.field.target.filter.TargetFilter;
import com.fr.bi.stable.data.Table;
import com.finebi.cube.api.ICubeDataLoader;
import com.fr.bi.stable.operation.sort.ISort;
import com.fr.bi.stable.relation.BISimpleRelation;
import com.finebi.cube.api.ICubeColumnIndexReader;

import java.util.List;
import java.util.Map;

public interface BIDetailTarget extends BITargetAndDimension, Release {

    /**
     * 创建索引map
     *
     * @param target 指标
     * @param loader loader对象
     * @return 索引map
     */
    ICubeColumnIndexReader createGroupValueMapGetter(Table target, ICubeDataLoader loader, long userId);

    /**
     * 计算值
     *
     * @param row
     * @param values
     * @param loader
     * @param userId
     * @return
     */
    Object createDetailValue(Long row, Map<String, Object> values, ICubeDataLoader loader, long userId);


    /**
     * 创建显示值
     *
     * @param value 值
     * @return object对象
     */
    public Object createShowValue(Object value);

    /**
     * 是不是计算指标
     *
     * @return true或false
     */
    boolean isCalculateTarget();

    /**
     * 准备计算
     *
     * @param values 值
     * @return true或fasle
     */
    boolean isReady4Calculate(Map<String, Object> values);

    List<BISimpleRelation> getRelationList(Table target, long userId);


    void setRelationList(List<BISimpleRelation> relationList);

    ISort getSort();

    TargetFilter getFilter();
}
