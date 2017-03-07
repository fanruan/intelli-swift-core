package com.fr.bi.field.dimension.calculator;

import com.finebi.cube.api.ICubeColumnIndexReader;
import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.conf.field.BusinessField;
import com.finebi.cube.conf.table.BusinessTable;
import com.finebi.cube.relation.BITableSourceRelation;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.conf.report.widget.field.dimension.BIDimension;
import com.fr.bi.stable.constant.BIBaseConstant;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.constant.DBConstant;
import com.fr.bi.stable.data.db.ICubeFieldSource;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.engine.index.key.IndexKey;
import com.fr.bi.stable.operation.sort.comp.ComparatorFacotry;
import com.fr.bi.stable.operation.sort.comp.CustomComparator;
import com.fr.bi.stable.structure.collection.map.CubeTreeMap;

import java.util.*;

/**
 * Created by 小灰灰 on 2015/6/30.
 */
public class StringDimensionCalculator extends AbstractDimensionCalculator {
    private transient ICubeColumnIndexReader customMap;
    private static final long serialVersionUID = 7475835494164777419L;

    public StringDimensionCalculator(BIDimension dimension, BusinessField column, List<BITableSourceRelation> relations) {
        super(dimension, column, relations);
    }

    public StringDimensionCalculator(BIDimension dimension, BusinessField column, List<BITableSourceRelation> relations, List<BITableSourceRelation> directToDimensionRelations) {
        super(dimension, column, relations, directToDimensionRelations);
    }

    @Override
    public Comparator getComparator() {
        if (getSortType() == BIReportConstant.SORT.ASC || getSortType() == BIReportConstant.SORT.NONE) {
            return getField().getFieldType() == DBConstant.COLUMN.NUMBER && getGroup().getType() == BIReportConstant.GROUP.NO_GROUP ? BIBaseConstant.COMPARATOR.COMPARABLE.ASC : BIBaseConstant.COMPARATOR.STRING.ASC_STRING_CC;
        } else if (getSortType() == BIReportConstant.SORT.DESC) {
            return getField().getFieldType() == DBConstant.COLUMN.NUMBER && getGroup().getType() == BIReportConstant.GROUP.NO_GROUP ? BIBaseConstant.COMPARATOR.COMPARABLE.DESC : BIBaseConstant.COMPARATOR.STRING.DESC_STRING_CC;
        } else {
            return new CustomComparator();
        }
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        StringDimensionCalculator sdc = (StringDimensionCalculator) super.clone();
        sdc.field = (BusinessField) this.field.clone();
        sdc.dimension = this.dimension;
        sdc.relations = this.relations;
        return sdc;

    }

    public Set createFilterValueSet(String value, ICubeDataLoader loader) {
        Set set = new HashSet();
        set.add(value);
        return set;
    }

    @Override
    public Iterator createValueMapIterator(BusinessTable table, ICubeDataLoader loader, boolean useRealData, int groupLimit) {
        if (isNoGroup() && !isCustomSort()) {
            return super.createValueMapIterator(table, loader, useRealData, groupLimit);
        }
        if (customMap == null) {
            initCustomMap(loader, useRealData, groupLimit);
        }
        if (isCustomSort()) {
            return customMap.iterator();
        }
        return getSortType() != BIReportConstant.SORT.DESC ? customMap.iterator() : customMap.previousIterator();

    }

    private void initCustomMap(ICubeDataLoader loader, boolean useRealData, int groupLimit) {
        CubeTableSource usedTableSource = getTableSourceFromField();
        BIKey usedColumnKey = dimension.createKey(field);
        //多对多处理,这里默认relationList的第一个关联是公共主表关联
        if (getDirectToDimensionRelationList().size() > 0) {
            ICubeFieldSource primaryField = getDirectToDimensionRelationList().get(0).getPrimaryField();
            CubeTableSource primaryTableSource = primaryField.getTableBelongTo();
            usedTableSource = primaryTableSource;
            usedColumnKey = new IndexKey(primaryField.getFieldName());
        }
        ICubeColumnIndexReader getter = loader.getTableIndex(usedTableSource).loadGroup(usedColumnKey, getRelationList(), useRealData, groupLimit);
        getter = dimension.getGroup().createGroupedMap(getter);
        if (isCustomSort()) {
            customMap = dimension.getSort().createGroupedMap(getter);
        } else {
            CubeTreeMap treeMap = new CubeTreeMap(ComparatorFacotry.getComparator(BIReportConstant.SORT.ASC));
            Iterator it = getter.iterator();
            while (it.hasNext()) {
                Map.Entry entry = (Map.Entry) it.next();
                treeMap.put(entry.getKey(), entry.getValue());
            }
            customMap = treeMap;
        }
    }

    public boolean isCustomSort() {
        return getSortType() == BIReportConstant.SORT.CUSTOM;
    }
}