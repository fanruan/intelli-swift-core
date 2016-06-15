package com.fr.bi.stable.report.result;

import com.finebi.cube.conf.field.BusinessField;
import com.finebi.cube.conf.table.BusinessTable;
import com.fr.bi.base.key.BIKey;
import com.finebi.cube.api.ICubeDataLoader;
import com.fr.bi.stable.operation.group.IGroup;
import com.finebi.cube.relation.BITableSourceRelation;
import com.finebi.cube.api.ICubeColumnIndexReader;
import com.fr.stable.FCloneable;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * Created by 小灰灰 on 2015/6/30.
 */
public interface DimensionCalculator extends FCloneable {

     BusinessField getField();


    ICubeColumnIndexReader createNoneSortNoneGroupValueMapGetter(BusinessTable target, ICubeDataLoader loader);
    /**
     * 获取维度到维度/指标的分组索引
     *
     * @param target
     * @param loader
     * @return
     */
    ICubeColumnIndexReader createNoneSortGroupValueMapGetter(BusinessTable target, ICubeDataLoader loader);

    /**
     * 是否为超级大分组
     *
     * @param targetTable 指标表
     * @param loader      注释
     * @return 是否为超级大分组
     */
    boolean isSupperLargeGroup(BusinessTable targetTable, ICubeDataLoader loader);

    /**
     * 是否为超级大分组
     *
     * @param loader 注释
     * @return 是否为超级大分组
     */
    boolean isSupperLargeGroup(ICubeDataLoader loader);

    List<BITableSourceRelation> getRelationList();

    List<BITableSourceRelation> getDirectToDimensionRelationList();

    BIKey createKey();

    Comparator getComparator();

    Object createEmptyValue();

    String getSortTarget();

    int getSortType();


    Iterator createValueMapIterator(BusinessTable table, ICubeDataLoader loader);

    Iterator createValueMapIterator(BusinessTable table, ICubeDataLoader loader, boolean useReallData, int groupLimit);

    ICubeColumnIndexReader createValueMap(BusinessTable table, ICubeDataLoader loader);

    ICubeColumnIndexReader createValueMap(BusinessTable table, ICubeDataLoader loader, boolean useReallData, int groupLimit);

    int getBaseTableValueCount(Object value, ICubeDataLoader loader);

    boolean hasSelfGroup();

    IGroup getGroup();


}