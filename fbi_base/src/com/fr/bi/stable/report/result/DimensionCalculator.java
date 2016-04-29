package com.fr.bi.stable.report.result;

import com.fr.bi.base.key.BIKey;
import com.fr.bi.stable.data.BIField;
import com.fr.bi.stable.data.Table;
import com.finebi.cube.api.ICubeDataLoader;
import com.fr.bi.stable.operation.group.IGroup;
import com.fr.bi.stable.relation.BITableSourceRelation;
import com.finebi.cube.api.ICubeColumnIndexReader;
import com.fr.stable.FCloneable;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * Created by 小灰灰 on 2015/6/30.
 */
public interface DimensionCalculator extends FCloneable {

    public BIField getField();

    /**
     * 获取维度到维度/指标的分组索引
     *
     * @param target
     * @param loader
     * @return
     */
    ICubeColumnIndexReader createNoneSortGroupValueMapGetter(Table target, ICubeDataLoader loader);

    /**
     * 是否为超级大分组
     *
     * @param targetTable 指标表
     * @param loader      注释
     * @return 是否为超级大分组
     */
    boolean isSupperLargeGroup(Table targetTable, ICubeDataLoader loader);

    /**
     * 是否为超级大分组
     *
     * @param loader 注释
     * @return 是否为超级大分组
     */
    boolean isSupperLargeGroup(ICubeDataLoader loader);

    List<BITableSourceRelation> getRelationList();

    BIKey createKey();

    Comparator getComparator();

    Object createEmptyValue();

    String getSortTarget();

    int getSortType();


    Iterator createValueMapIterator(Table table, ICubeDataLoader loader);

    Iterator createValueMapIterator(Table table, ICubeDataLoader loader, boolean useReallData, int groupLimit);

    ICubeColumnIndexReader createValueMap(Table table, ICubeDataLoader loader);

    ICubeColumnIndexReader createValueMap(Table table, ICubeDataLoader loader, boolean useReallData, int groupLimit);

    int getBaseTableValueCount(Object value, ICubeDataLoader loader);

    boolean hasSelfGroup();

    IGroup getGroup();


}