package com.fr.bi.field.dimension.calculator;

import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.conf.field.BusinessField;
import com.finebi.cube.conf.table.BusinessTable;
import com.finebi.cube.relation.BITableSourceRelation;
import com.fr.bi.conf.report.widget.field.dimension.BIDimension;
import com.fr.bi.stable.constant.BIBaseConstant;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.constant.DBConstant;
import com.fr.bi.stable.operation.sort.comp.CustomComparator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

/**
 * Created by 小灰灰 on 2015/6/30.
 */
public class StringDimensionCalculator extends AbstractDimensionCalculator {
    public StringDimensionCalculator(BIDimension dimension, BusinessField column, List<BITableSourceRelation> relations) {
        super(dimension, column, relations);
    }

    public StringDimensionCalculator(BIDimension dimension, BusinessField column, List<BITableSourceRelation> relations, List<BITableSourceRelation> directToDimensionRelations) {
        super(dimension, column, relations, directToDimensionRelations);
    }


    @Override
    public boolean isSupperLargeGroup(BusinessTable targetTable, ICubeDataLoader loader) {
        try {
            return createNoneSortGroupValueMapGetter(targetTable, loader).nonPrecisionSize() > BIBaseConstant.LARGE_GROUP_LINE;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean isSupperLargeGroup(ICubeDataLoader loader) {
        try {
            return loader.getTableIndex(dimension.createTableKey().getTableSource()).loadGroup(dimension.createKey(field), new ArrayList<BITableSourceRelation>()).nonPrecisionSize() > BIBaseConstant.LARGE_GROUP_LINE;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Comparator getComparator() {
        if (getSortType() == BIReportConstant.SORT.ASC) {
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
        return null;
    }
}