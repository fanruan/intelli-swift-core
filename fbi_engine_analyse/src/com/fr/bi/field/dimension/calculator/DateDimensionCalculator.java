package com.fr.bi.field.dimension.calculator;

import com.fr.bi.conf.report.widget.BIDataColumn;
import com.fr.bi.conf.report.widget.field.dimension.BIDimension;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.data.Table;
import com.finebi.cube.api.ICubeDataLoader;
import com.fr.bi.stable.relation.BITableSourceRelation;
import com.finebi.cube.api.ICubeColumnIndexReader;
import com.fr.bi.stable.structure.collection.map.CubeTreeMap;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by 小灰灰 on 2015/6/30.
 */
public class DateDimensionCalculator extends AbstractDimensionCalculator {
    public DateDimensionCalculator(BIDimension dimension, BIDataColumn column, List<BITableSourceRelation> relations) {
        super(dimension, column, relations);
    }

    @Override
    public Iterator createValueMapIterator(Table table, ICubeDataLoader loader, boolean useRealData, int groupLimit) {
        ICubeColumnIndexReader getter = loader.getTableIndex(field).loadGroup(dimension.createKey(field), getRelationList(), useRealData, groupLimit);
        CubeTreeMap treeMap = new CubeTreeMap(getComparator());
        Iterator it = getter.iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            treeMap.put(getGroupDate() == BIReportConstant.GROUP.M ? String.valueOf((Integer)entry.getKey() + 1) : entry.getKey().toString(), entry.getValue());
        }
        return getSortType() != BIReportConstant.SORT.DESC ? treeMap.iterator() : treeMap.previousIterator();
    }

    /**
     * 是否为超级大分组
     *
     * @param targetTable 指标表
     * @param loader      注释
     * @return 是否为超级大分组
     */
    @Override
    public boolean isSupperLargeGroup(Table targetTable, ICubeDataLoader loader) {
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