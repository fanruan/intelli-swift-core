package com.fr.bi.stable.connection;

import com.fr.bi.base.BIBasicCore;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.stable.data.BIField;
import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.api.ICubeTableService;
import com.fr.bi.stable.relation.BITableSourceRelation;

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

        Iterator<BITableSourceRelation> it = relationList.iterator();
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
        BIField primaryKey = relation.getPrimaryKey();
        BIField foreignKey = relation.getForeignKey();
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

    private static BIKey getFieldIndex(ICubeDataLoader loader, BIField foreignKey) {
        ICubeTableService ti = loader.getTableIndex(BIBasicCore.generateValueCore(foreignKey.getTableBelongTo().getID().getIdentityValue()));
        if (ti != null) {
            return ti.getColumnIndex(foreignKey.getFieldName());
        } else {
            return null;
        }
    }
}