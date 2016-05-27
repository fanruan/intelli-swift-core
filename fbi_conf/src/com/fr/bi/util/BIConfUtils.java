package com.fr.bi.util;

import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.conf.BICubeConfigureCenter;
import com.finebi.cube.conf.field.BusinessField;
import com.finebi.cube.conf.table.BusinessTable;
import com.finebi.cube.relation.BITableRelation;
import com.finebi.cube.relation.BITableSourceRelation;
import com.fr.bi.base.BIUser;
import com.fr.bi.exception.BIKeyAbsentException;
import com.fr.bi.stable.connection.DirectTableConnection;
import com.fr.bi.stable.data.db.BICubeFieldSource;
import com.fr.bi.stable.data.db.CubeFieldSource;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.engine.index.key.IndexKey;
import com.fr.bi.stable.report.result.DimensionCalculator;
import com.fr.bi.stable.utils.program.BINonValueUtils;
import com.fr.general.ComparatorUtils;

import java.util.ArrayList;
import java.util.List;

public class BIConfUtils {
    public static DirectTableConnection createDirectTableConnection(List<BITableSourceRelation> relation, ICubeDataLoader loader) {
        DirectTableConnection temp = null;
        for (int i = relation.size(); i > 0; i--) {
            DirectTableConnection connection = createConnection(relation.get(i - 1), loader);
            if (temp != null) {
                temp.setNext(connection);
            }
            temp = connection;
        }
        while (temp != null && temp.getLast() != null) {
            temp = temp.getLast();
        }
        return temp;
    }

    public static DirectTableConnection createDirectTableConnection(BITableSourceRelation[] relationList, ICubeDataLoader loader) {
        DirectTableConnection temp = null;
        for (int i = relationList.length; i > 0; i--) {
            DirectTableConnection connection = createConnection(relationList[i - 1], loader);
            if (temp != null) {
                temp.setNext(connection);
            }
            temp = connection;
        }
        while (temp != null && temp.getLast() != null) {
            temp = temp.getLast();
        }
        return temp;
    }

    public static boolean isSameRelation(BITableSourceRelation one, BITableSourceRelation other, long userId) {
        if (one == null || other == null) {
            return false;
        }
        if (one.hashCode() == other.hashCode()) {
            return true;
        }
        return (ComparatorUtils.equals(one.getForeignKey().getFieldName(), other.getForeignKey().getFieldName()) && ComparatorUtils.equals(one.getPrimaryKey().getFieldName(), other.getPrimaryKey().getFieldName()));

    }

    public static DirectTableConnection createDirectTableConnection(DimensionCalculator ck, BusinessTable targetKey, ICubeDataLoader loader) {
        DirectTableConnection temp = null;
        List<BITableSourceRelation> relationList = ck.getRelationList();
        for (int i = relationList.size(); i > 0; i--) {
            DirectTableConnection connection;
            if (i == 1) {
                connection = createConnection(ck, relationList.get(i - 1), loader);
            } else {
                connection = createConnection(relationList.get(i - 1), loader);
            }
            if (temp != null) {
                temp.setNext(connection);
            }
            temp = connection;
        }
        while (temp != null && temp.getLast() != null) {
            temp = temp.getLast();
        }
        return temp;
    }

    private static DirectTableConnection createConnection(BITableSourceRelation relation, ICubeDataLoader loader) {
        CubeFieldSource primaryKey = relation.getPrimaryKey();
        CubeFieldSource foreignKey = relation.getForeignKey();
        return new DirectTableConnection(foreignKey.getTableBelongTo(), new IndexKey(foreignKey.getFieldName()), loader.getTableIndex(foreignKey.getTableBelongTo()),
                primaryKey.getTableBelongTo(), new IndexKey(primaryKey.getFieldName()), loader.getTableIndex(primaryKey.getTableBelongTo()));
    }

    private static DirectTableConnection createConnection(DimensionCalculator ck, BITableSourceRelation relation, ICubeDataLoader loader) {
        CubeFieldSource primaryKey = relation.getPrimaryKey();
        CubeFieldSource foreignKey = relation.getForeignKey();
        return new DirectTableConnection(foreignKey.getTableBelongTo(), new IndexKey(foreignKey.getFieldName()), loader.getTableIndex(foreignKey.getTableBelongTo()),
                ck.getField().getTableBelongTo().getTableSource(), loader.getTableIndex(ck.getField().getTableBelongTo().getTableSource()).getColumnIndex(primaryKey.getFieldName()), loader.getTableIndex(ck.getField().getTableBelongTo().getTableSource()));
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
            foreignTableSource = BICubeConfigureCenter.getDataSourceManager().getTableSource(primaryField.getTableBelongTo());
        } catch (BIKeyAbsentException e) {
            throw BINonValueUtils.beyondControl(e);
        }
        CubeFieldSource primaryFieldSource = new BICubeFieldSource(primaryTableSource, primaryField.getFieldName(), primaryField.getClassType(), primaryField.getFieldSize());
        CubeFieldSource foreignFieldSource = new BICubeFieldSource(foreignTableSource, foreignField.getFieldName(), foreignField.getClassType(), foreignField.getFieldSize());
        return new BITableSourceRelation(
                primaryFieldSource,
                foreignFieldSource,
                primaryTableSource,
                foreignTableSource
        );
    }

    public static List<BITableSourceRelation> convertToMD5RelationFromSimpleRelation(List<BITableRelation> relations, BIUser user) {

        return BIConfUtils.convert2TableSourceRelation(relations);
    }
}