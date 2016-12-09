package com.fr.bi.field.dimension.calculator;

import com.finebi.cube.api.ICubeColumnIndexReader;
import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.conf.field.BusinessField;
import com.finebi.cube.conf.table.BusinessTable;
import com.finebi.cube.relation.BITableSourceRelation;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.conf.report.widget.field.dimension.BIDimension;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.data.db.ICubeFieldSource;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.engine.index.key.IndexKey;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.operation.sort.comp.ComparatorFacotry;
import com.fr.bi.stable.structure.collection.map.CubeLinkedHashMap;
import com.fr.bi.stable.structure.collection.map.CubeTreeMap;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by 小灰灰 on 2015/6/30.
 */
public class NumberDimensionCalculator extends AbstractDimensionCalculator {
    private transient ICubeColumnIndexReader customMap;
    private static final long serialVersionUID = -1047271246017361490L;

    public NumberDimensionCalculator(BIDimension dimension, BusinessField column, List<BITableSourceRelation> relations) {
        super(dimension, column, relations);
    }

    public NumberDimensionCalculator(BIDimension dimension, BusinessField field, List<BITableSourceRelation> relations, List<BITableSourceRelation> directToDimensionRelations) {
        super(dimension, field, relations, directToDimensionRelations);
    }

    @Override
    public Iterator createValueMapIterator(BusinessTable table, ICubeDataLoader loader, boolean useRealData, int groupLimit) {
        if (isNoGroup() && !isCustomSort()) {
            //默认设置field本身为关联主键
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
            //数值类型计算空值索引start
            GroupValueIndex nullGroupValueIndex = loader.getTableIndex(usedTableSource).getNullGroupValueIndex(usedColumnKey);
            CubeLinkedHashMap newGetter = new CubeLinkedHashMap();
            newGetter.put("", nullGroupValueIndex);
            Iterator iter = getter.iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                Object key = entry.getKey();
                if (key == null) {
                    continue;
                }
                newGetter.put(key, entry.getValue());
            }
            //数值类型计算空值索引end
            getter = dimension.getGroup().createGroupedMap(newGetter);
            if (useRealData && isNoGroup() && getSortType() != BIReportConstant.SORT.CUSTOM) {
                return getSortType() != BIReportConstant.SORT.DESC ? getter.iterator() : getter.previousIterator();
            }
            return dimension.getSort().createGroupedMap(getter).iterator();
        }
        if (customMap == null) {
            initCustomMap(loader, useRealData, groupLimit);
        }
        if (isCustomSort()) {
            return customMap.iterator();
        }
        return getSortType() != BIReportConstant.SORT.NUMBER_DESC ? customMap.iterator() : customMap.previousIterator();

    }

    private void initCustomMap(ICubeDataLoader loader, boolean useRealData, int groupLimit) {
        ICubeColumnIndexReader getter = loader.getTableIndex(field.getTableBelongTo().getTableSource()).loadGroup(dimension.createKey(field), getRelationList(), useRealData, groupLimit);
        GroupValueIndex nullGroupValueIndex = loader.getTableIndex(field.getTableBelongTo().getTableSource()).getNullGroupValueIndex(dimension.createKey(field));
        CubeLinkedHashMap newGetter = new CubeLinkedHashMap();
        newGetter.put("", nullGroupValueIndex);
        Iterator iter = getter.iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            Object key = entry.getKey();
            if (key == null) {
                continue;
            }
            newGetter.put(key, entry.getValue());
        }
        getter = dimension.getGroup().createGroupedMap(newGetter);
        if (isCustomSort()) {
            customMap = dimension.getSort().createGroupedMap(getter);
        } else {
            Comparator comparator;
            if (getGroup().getType() == BIReportConstant.GROUP.ID_GROUP) {
                comparator = ComparatorFacotry.getComparator(BIReportConstant.SORT.NUMBER_ASC);
            } else {
                comparator = ComparatorFacotry.getComparator(BIReportConstant.SORT.ASC);
            }
            CubeTreeMap treeMap = new CubeTreeMap(comparator);
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