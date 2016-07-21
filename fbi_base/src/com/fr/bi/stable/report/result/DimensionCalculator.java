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

    List<BITableSourceRelation> getRelationList();

    List<BITableSourceRelation> getDirectToDimensionRelationList();

    BIKey createKey();

    Comparator getComparator();

    Object createEmptyValue();

    String getSortTarget();

    int getSortType();


    Iterator createValueMapIterator(BusinessTable table, ICubeDataLoader loader);

    Iterator createValueMapIterator(BusinessTable table, ICubeDataLoader loader, boolean useReallData, int groupLimit);

    int getOriginGroupSize(BusinessTable table, ICubeDataLoader loader);

    int getBaseTableValueCount(Object value, ICubeDataLoader loader);

    boolean hasSelfGroup();

    IGroup getGroup();


}