package com.fr.bi.field.dimension.calculator;

import com.finebi.cube.api.ICubeColumnIndexReader;
import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.conf.field.BusinessField;
import com.finebi.cube.conf.table.BusinessTable;
import com.finebi.cube.relation.BITableSourceRelation;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.stable.engine.index.key.IndexKey;
import com.fr.bi.stable.operation.group.IGroup;
import com.fr.bi.stable.report.result.DimensionCalculator;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * Created by 小灰灰 on 2015/7/6.
 */
public class NoneDimensionCalculator implements DimensionCalculator {
    protected List<BITableSourceRelation> relations;
    private BusinessField field;

    public NoneDimensionCalculator(BusinessField column, List<BITableSourceRelation> relations) {
        this.field = column;
        this.relations = relations;
    }

    @Override
    public List<BITableSourceRelation> getDirectToDimensionRelationList() {
        return relations;
    }

    @Override
    public BusinessField getField() {
        return field;
    }

    @Override
    public ICubeColumnIndexReader createNoneSortNoneGroupValueMapGetter(BusinessTable target, ICubeDataLoader loader) {
        return loader.getTableIndex(field.getTableBelongTo().getTableSource()).loadGroup(createKey(), relations);
    }

    /**
     * 获取维度到维度/指标的分组索引
     *
     * @param target
     * @param loader
     * @return
     */
    @Override
    public ICubeColumnIndexReader createNoneSortGroupValueMapGetter(BusinessTable target, ICubeDataLoader loader) {
        return loader.getTableIndex(field.getTableBelongTo().getTableSource()).loadGroup(createKey(), relations);
    }

    /**
     * 是否为超级大分组
     *
     * @param targetTable 指标表
     * @param loader      注释
     * @return 是否为超级大分组
     */
    @Override
    public boolean isSupperLargeGroup(BusinessTable targetTable, ICubeDataLoader loader) {
        return false;
    }

    ;


    /**
     * 是否为超级大分组
     *
     * @param loader 注释
     * @return 是否为超级大分组
     */
    @Override
    public boolean isSupperLargeGroup(ICubeDataLoader loader) {
        return false;
    }

    ;

    @Override
    public List<BITableSourceRelation> getRelationList() {
        return relations;
    }

    @Override
    public BIKey createKey() {
        return new IndexKey(field.getFieldName());
    }


    @Override
    public Comparator getComparator() {
        return null;
    }


    @Override
    public Object createEmptyValue() {
        return null;
    }

    @Override
    public String getSortTarget() {
        return null;
    }

    @Override
    public int getSortType() {
        return 0;
    }

    @Override
    public Iterator createValueMapIterator(BusinessTable table, ICubeDataLoader loader) {
        return createNoneSortGroupValueMapGetter(table, loader).iterator();
    }

    @Override
    public Iterator createValueMapIterator(BusinessTable table, ICubeDataLoader loader, boolean useReallData, int groupLimit) {
        return null;
    }

    @Override
    public ICubeColumnIndexReader createValueMap(BusinessTable table, ICubeDataLoader loader) {
        return createNoneSortGroupValueMapGetter(table, loader);
    }

    @Override
    public ICubeColumnIndexReader createValueMap(BusinessTable table, ICubeDataLoader loader, boolean useReallData, int groupLimit) {
        return createNoneSortGroupValueMapGetter(table, loader);
    }

    @Override
    public int getBaseTableValueCount(Object value, ICubeDataLoader loader) {
        return 0;
    }

    @Override
    public boolean hasSelfGroup() {
        return false;
    }

    @Override
    public IGroup getGroup() {
        return null;
    }

    @Override
    public DimensionCalculator clone() {
        return null;
    }
}