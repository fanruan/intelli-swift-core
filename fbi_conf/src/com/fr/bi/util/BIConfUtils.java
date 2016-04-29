package com.fr.bi.util;

import com.fr.bi.base.BIUser;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.stable.connection.DirectTableConnection;
import com.fr.bi.stable.data.BIField;
import com.fr.bi.stable.data.Table;
import com.finebi.cube.api.ICubeDataLoader;
import com.fr.bi.stable.relation.BISimpleRelation;
import com.fr.bi.stable.relation.BITableRelation;
import com.fr.bi.stable.relation.BITableSourceRelation;
import com.fr.bi.stable.report.result.DimensionCalculator;
import com.fr.general.ComparatorUtils;

import java.util.ArrayList;
import java.util.List;

public class BIConfUtils {
    public static DirectTableConnection creatDirectTableConnection(List<BITableSourceRelation> relation, ICubeDataLoader loader) {
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

    public static DirectTableConnection creatDirectTableConnection(BITableSourceRelation[] relationList, ICubeDataLoader loader) {
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

    public static DirectTableConnection createDirectTableConnection(DimensionCalculator ck, Table targetKey, ICubeDataLoader loader) {
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
        BIField primaryKey = relation.getPrimaryKey();
        BIField foreignKey = relation.getForeignKey();
        return new DirectTableConnection(foreignKey.getTableBelongTo(), loader.getFieldIndex(foreignKey), loader.getTableIndex(foreignKey),
                primaryKey.getTableBelongTo(), loader.getFieldIndex(primaryKey), loader.getTableIndex(primaryKey));
    }

    private static DirectTableConnection createConnection(DimensionCalculator ck, BITableSourceRelation relation, ICubeDataLoader loader) {
        BIField primaryKey = relation.getPrimaryKey();
        BIField foreignKey = relation.getForeignKey();
        return new DirectTableConnection(foreignKey.getTableBelongTo(), loader.getFieldIndex(foreignKey), loader.getTableIndex(foreignKey),
                ck.getField().getTableBelongTo(), loader.getTableIndex(ck.getField()).getColumnIndex(primaryKey.getFieldName()), loader.getTableIndex(ck.getField()));
    }

    public static List<BITableSourceRelation> convert2TableSourceRelation(List<BITableRelation> relations, BIUser user) {
        List<BITableSourceRelation> list = new ArrayList<BITableSourceRelation>();
        for (BITableRelation relation : relations) {
            BIField pField = relation.getPrimaryField();
            BIField fField = relation.getForeignField();
            list.add(new BITableSourceRelation(
                    pField,
                    fField,
                    BIConfigureManagerCenter.getDataSourceManager().getTableSourceByID(pField.getTableID(), user),
                    BIConfigureManagerCenter.getDataSourceManager().getTableSourceByID(fField.getTableID(), user)
            ));
        }
        return list;
    }

    public static List<BITableSourceRelation> convertToMD5RelationFromSimpleRelation(List<BISimpleRelation> relations, BIUser user) {
        List<BITableRelation> list = new ArrayList<BITableRelation>();
        for (BISimpleRelation relation : relations) {
            list.add(relation.getTableRelation());
        }
        return BIConfUtils.convert2TableSourceRelation(list, user);
    }
}