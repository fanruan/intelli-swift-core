package com.fr.bi.field.dimension.calculator;

import com.finebi.cube.api.ICubeColumnIndexReader;
import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.conf.field.BusinessField;
import com.finebi.cube.conf.table.BusinessTable;
import com.finebi.cube.relation.BITableSourceRelation;
import com.fr.bi.base.FinalBoolean;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.conf.report.widget.field.dimension.BIDimension;
import com.fr.bi.stable.constant.BIBaseConstant;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.constant.DBConstant;
import com.fr.bi.stable.data.db.ICubeFieldSource;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.engine.index.key.IndexKey;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.operation.group.BIGroupUtils;
import com.fr.bi.stable.operation.sort.comp.ComparatorFacotry;
import com.fr.bi.stable.operation.sort.comp.CustomComparator;
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
    public Iterator createValueMapIterator(BusinessTable table, ICubeDataLoader loader, boolean useRealData, int groupLimit, GroupValueIndex filterGvi) {

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

            if (!useRealData) {
                applyFilterForNotRealData(getter, filterGvi);
            }

            //数值类型计算空值索引start
            final GroupValueIndex nullGroupValueIndex = loader.getTableIndex(usedTableSource).getNullGroupValueIndex(usedColumnKey);
            getter = dimension.getGroup().createGroupedMap(getter);
            if (useRealData && isNoGroup() && getSortType() != BIReportConstant.SORT.CUSTOM) {
                return getIterator(getter, nullGroupValueIndex);
            }
            return dimension.getSort().createGroupedMap(getter).iterator();
        }
        if (customMap == null || !useRealData) {
            initCustomMap(loader, useRealData, groupLimit, filterGvi);
        }
        if (isCustomSort()) {
            return customMap.iterator();
        }
        return getSortType() != BIReportConstant.SORT.NUMBER_DESC ? customMap.iterator() : customMap.previousIterator();

    }

    private Iterator getIterator(ICubeColumnIndexReader getter, final GroupValueIndex nullGroupValueIndex) {

        final Iterator iterator = (getSortType() != BIReportConstant.SORT.DESC
                && getSortType() != BIReportConstant.SORT.NUMBER_DESC) ? getter.iterator() : getter.previousIterator();
        final FinalBoolean usedNullIndex = new FinalBoolean();
        usedNullIndex.flag = false;
        return new Iterator() {

            @Override
            public boolean hasNext() {

                if (iterator.hasNext()) {
                    return true;
                }
                if (!usedNullIndex.flag) {
                    usedNullIndex.flag = true;
                    return true;
                }
                return false;
            }

            @Override
            public Object next() {

                if (usedNullIndex.flag) {
                    return new Map.Entry() {

                        @Override
                        public Object getKey() {

                            return null;
                        }

                        @Override
                        public Object getValue() {

                            return nullGroupValueIndex;
                        }

                        @Override
                        public Object setValue(Object value) {

                            return null;
                        }

                        @Override
                        public boolean equals(Object o) {

                            return false;
                        }

                        @Override
                        public int hashCode() {

                            return 0;
                        }
                    };
                }
                return iterator.next();
            }

            @Override
            public void remove() {

            }
        };
    }

    private synchronized void initCustomMap(ICubeDataLoader loader, boolean useRealData, int groupLimit, GroupValueIndex filterGvi) {

        ICubeColumnIndexReader getter = loader.getTableIndex(field.getTableBelongTo().getTableSource()).loadGroup(dimension.createKey(field), getRelationList(), useRealData, groupLimit);
        //BI-5055 这边空分组取得不对，应该取得是主表空分组对应字表的gvi，而不是取出主表自己的gvi
        //        GroupValueIndex nullGroupValueIndex = loader.getTableIndex(field.getTableBelongTo().getTableSource()).getNullGroupValueIndex(dimension.createKey(field));
        //        if (!nullGroupValueIndex.isAllEmpty()) {
        //            getter = new CubeIndexGetterWithNullValue(getter, null, nullGroupValueIndex);
        //        }
        if (!useRealData) {
            applyFilterForNotRealData(getter, filterGvi);
        }
        getter = dimension.getGroup().createGroupedMap(getter);

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

    @Override
    public Comparator getComparator() {

        if (getSortType() == BIReportConstant.SORT.ASC || getSortType() == BIReportConstant.SORT.NUMBER_ASC || getSortType() == BIReportConstant.SORT.NONE) {
            return getGroup().getType() == BIReportConstant.GROUP.ID_GROUP ? BIBaseConstant.COMPARATOR.NUMBER.ASC : BIBaseConstant.COMPARATOR.STRING.ASC_STRING_CC;
        } else if (getSortType() == BIReportConstant.SORT.DESC || getSortType() == BIReportConstant.SORT.NUMBER_DESC) {
            return getGroup().getType() == BIReportConstant.GROUP.ID_GROUP ? BIBaseConstant.COMPARATOR.NUMBER.DESC : BIBaseConstant.COMPARATOR.STRING.DESC_STRING_CC;
        } else {
            return new CustomComparator();
        }
    }

    public boolean isCustomSort() {

        return getSortType() == BIReportConstant.SORT.CUSTOM;
    }

    @Override
    public Object convertToOriginValue(String stringValue) {

        try {
            if (BIGroupUtils.isCustomGroup(getGroup())) {
                return super.convertToOriginValue(stringValue);
            }
            return convertNumber(stringValue);
        } catch (Exception e) {
            BILoggerFactory.getLogger().error(e.getMessage());
        }
        return null;
    }

    protected Object convertNumber(String value) {

        switch (field.getClassType()) {
            case DBConstant.CLASS.LONG:
                return Long.parseLong(value);
            case DBConstant.CLASS.DOUBLE:
                return Double.parseDouble(value);
            //BI-4741 long和int类型数据都转为long
            default:
                return Long.parseLong(value);
        }
    }
}