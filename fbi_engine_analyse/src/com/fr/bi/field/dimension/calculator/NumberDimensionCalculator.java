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
public class NumberDimensionCalculator extends AbstractDimensionCalculator {
    public NumberDimensionCalculator(BIDimension dimension, BusinessField column, List<BITableSourceRelation> relations) {
        super(dimension, column, relations);
    }

    public NumberDimensionCalculator(BIDimension dimension, BusinessField field, List<BITableSourceRelation> relations, List<BITableSourceRelation> directToDimensionRelations) {
        super(dimension, field, relations, directToDimensionRelations);
    }

    @Override
    public Iterator createValueMapIterator(BusinessTable table, ICubeDataLoader loader, boolean useRealData, int groupLimit) {
        ICubeColumnIndexReader getter = loader.getTableIndex(field.getTableBelongTo().getTableSource()).loadGroup(dimension.createKey(field), getRelationList(), useRealData, groupLimit);
        getter = dimension.getGroup().createGroupedMap(getter);
        Comparator comparator;
        if(getSortType() == BIReportConstant.SORT.NONE || getSortType() == BIReportConstant.SORT.CUSTOM){
            return dimension.getSort().createGroupedMap(getter).iterator();
        }
        if(getGroup().getType() == BIReportConstant.GROUP.ID_GROUP){
            comparator = ComparatorFacotry.getComparator(BIReportConstant.SORT.NUMBER_ASC);
        }else{
            comparator = ComparatorFacotry.getComparator(BIReportConstant.SORT.ASC);
        }
        CubeTreeMap treeMap = new CubeTreeMap(comparator);
        Iterator it = getter.iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            treeMap.put(entry.getKey(), entry.getValue());
        }
        return getSortType() != BIReportConstant.SORT.NUMBER_DESC ? treeMap.iterator() : treeMap.previousIterator();
    }

}