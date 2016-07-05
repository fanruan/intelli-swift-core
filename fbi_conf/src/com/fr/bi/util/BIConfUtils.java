package com.fr.bi.util;

import com.finebi.cube.conf.BICubeConfigureCenter;
import com.finebi.cube.conf.field.BusinessField;
import com.finebi.cube.relation.BITableRelation;
import com.finebi.cube.relation.BITableSourceRelation;
import com.fr.bi.exception.BIKeyAbsentException;
import com.fr.bi.stable.data.db.BICubeFieldSource;
import com.fr.bi.stable.data.db.ICubeFieldSource;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.utils.program.BINonValueUtils;
import com.fr.general.ComparatorUtils;

import java.util.ArrayList;
import java.util.List;


public class BIConfUtils {
    public static boolean isSameRelation(BITableSourceRelation one, BITableSourceRelation other, long userId) {
        if (one == null || other == null) {
            return false;
        }
        if (one.hashCode() == other.hashCode()) {
            return true;
        }
        return (ComparatorUtils.equals(one.getForeignKey().getFieldName(), other.getForeignKey().getFieldName()) && ComparatorUtils.equals(one.getPrimaryKey().getFieldName(), other.getPrimaryKey().getFieldName()));

    }

    public static List<BITableSourceRelation> convert2TableSourceRelation(List<BITableRelation> relations) {
        List<BITableSourceRelation> list = new ArrayList<BITableSourceRelation>();
        for (BITableRelation relation : relations) {
            list.add(convert2TableSourceRelation(relation));
        }
        return list;
    }

    public static BITableSourceRelation convert2TableSourceRelation(BITableRelation relation) {
        BusinessField primaryField = relation.getPrimaryField();
        BusinessField foreignField = relation.getForeignField();
        CubeTableSource primaryTableSource = null;
        CubeTableSource foreignTableSource = null;
        try {
            primaryTableSource = BICubeConfigureCenter.getDataSourceManager().getTableSource(primaryField.getTableBelongTo());
            foreignTableSource = BICubeConfigureCenter.getDataSourceManager().getTableSource(foreignField.getTableBelongTo());
        } catch (BIKeyAbsentException e) {
            throw BINonValueUtils.beyondControl(e);
        }
        ICubeFieldSource primaryFieldSource = new BICubeFieldSource(primaryTableSource, primaryField.getFieldName(), primaryField.getClassType(), primaryField.getFieldSize());
        ICubeFieldSource foreignFieldSource = new BICubeFieldSource(foreignTableSource, foreignField.getFieldName(), foreignField.getClassType(), foreignField.getFieldSize());
        return new BITableSourceRelation(
                primaryFieldSource,
                foreignFieldSource,
                primaryTableSource,
                foreignTableSource
        );
    }

}