package com.fr.bi.field.dimension.calculator;

import com.finebi.cube.api.ICubeColumnIndexReader;
import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.conf.field.BusinessField;
import com.finebi.cube.conf.table.BusinessTable;
import com.finebi.cube.relation.BITableSourceRelation;
import com.fr.bi.conf.report.widget.field.dimension.BIDimension;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.operation.sort.comp.ComparatorFacotry;
import com.fr.bi.stable.structure.collection.map.CubeTreeMap;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by 小灰灰 on 2015/6/30.
 */
public class DateDimensionCalculator extends AbstractDimensionCalculator {
    public DateDimensionCalculator(BIDimension dimension, BusinessField column, List<BITableSourceRelation> relations) {
        super(dimension, column, relations);
    }

    public DateDimensionCalculator(BIDimension dimension, BusinessField field, List<BITableSourceRelation> relations, List<BITableSourceRelation> directToDimensionRelations) {
        super(dimension, field, relations, directToDimensionRelations);
    }

    @Override
    public Iterator createValueMapIterator(BusinessTable table, ICubeDataLoader loader, boolean useRealData, int groupLimit) {
        ICubeColumnIndexReader getter = loader.getTableIndex(field.getTableBelongTo().getTableSource()).loadGroup(dimension.createKey(field), getRelationList(), useRealData, groupLimit);
        Comparator comparator;
//        if(getGroupDate() == BIReportConstant.GROUP.M){
//            comparator = ComparatorFacotry.getComparator(BIReportConstant.SORT.NUMBER_ASC);
//        }else{
//            comparator = getComparator();
//        }
        comparator = ComparatorFacotry.getComparator(BIReportConstant.SORT.NUMBER_ASC);
        CubeTreeMap treeMap = new CubeTreeMap(comparator);
        Iterator it = getter.iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            treeMap.put(getGroupDate() == BIReportConstant.GROUP.M ? (Integer) entry.getKey() + 1 : entry.getKey(), entry.getValue());
        }
        return getSortType() != BIReportConstant.SORT.NUMBER_DESC ? treeMap.iterator() : treeMap.previousIterator();
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

    public int getGroupDate() {
        return getGroup().getType();
    }

}