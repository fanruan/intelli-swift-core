package com.fr.bi.stable.connection;

import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.api.ICubeTableService;
import com.finebi.cube.relation.BITableSourceRelation;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.stable.data.db.CubeFieldSource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Created by GUY on 2015/3/26.
 */
public class DirectTableConnectionFactory {

    public static ConnectionRowGetter createConnectionRow(List<BITableSourceRelation> relationList, ICubeDataLoader loader) {
        DirectTableConnection connection = createDirectTableConnection(relationList, loader);
        return new ConnectionRowGetter(connection);
    }

    /**
     * 创建直接表链接
     *
     * @param loader CubeTILoader对象
     * @return DirectTableConnection对象
     */
    private static DirectTableConnection createDirectTableConnection(List<BITableSourceRelation> relationList, ICubeDataLoader loader) {
        DirectTableConnection temp = null;
        //计算时关联反转
        List<BITableSourceRelation> relationListRev = new ArrayList<BITableSourceRelation>();
        relationListRev.addAll(relationList);
        Collections.reverse(relationListRev);


        Iterator<BITableSourceRelation> it = relationListRev.iterator();
        while (it.hasNext()) {
            DirectTableConnection c = createConnection(it.next(), loader);
            if (temp != null) {
                temp.setNext(c);
            }
            temp = c;
        }
        while (temp != null && temp.getLast() != null) {
            temp = temp.getLast();
        }
        return temp;
    }


    private static DirectTableConnection createConnection(BITableSourceRelation relation, ICubeDataLoader loader) {
        CubeFieldSource primaryKey = relation.getPrimaryKey();
        CubeFieldSource foreignKey = relation.getForeignKey();
        BIKey primaryIndex = getFieldIndex(loader, primaryKey);
        BIKey foreignIndex = getFieldIndex(loader, foreignKey);
        if (primaryIndex != null && foreignIndex != null) {
            return new DirectTableConnection(
                    foreignKey.getTableBelongTo(), foreignIndex,
                    loader.getTableIndex(foreignKey.getTableBelongTo()),
                    primaryKey.getTableBelongTo(), primaryIndex,
                    loader.getTableIndex(primaryKey.getTableBelongTo())
            );
        } else {
            return null;
        }
    }

    private static BIKey getFieldIndex(ICubeDataLoader loader, CubeFieldSource foreignKey) {
        ICubeTableService ti = loader.getTableIndex(foreignKey.getTableBelongTo());
        if (ti != null) {
            return ti.getColumnIndex(foreignKey.getFieldName());
        } else {
            return null;
        }
    }
}