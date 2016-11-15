package com.finebi.cube.utils;

import com.finebi.cube.ICubeConfiguration;
import com.finebi.cube.relation.BITableRelation;

/**
 * Created by kary on 2016/9/21.
 */
public class BITableRelationUtils {
    public static boolean isRelationValid(BITableRelation relation) {
        boolean checkNull = null != relation.getPrimaryTable() && null != relation.getForeignTable() && null != relation.getPrimaryField() && null != relation.getForeignField();
        boolean isStructureCorrect = relation.getForeignField().getTableBelongTo().getID().getIdentity().equals(relation.getForeignTable().getID().getIdentity()) && relation.getPrimaryField().getTableBelongTo().getID().getIdentity().equals(relation.getPrimaryTable().getID().getIdentity());
        boolean isTypeCorrect = relation.getForeignField().getFieldType() == relation.getPrimaryField().getFieldType();
        return checkNull && isStructureCorrect && isTypeCorrect;
    }

    /***
     * 检测relation依赖的表是否可用，防止客户因为物理删除cube导致关系混乱
     * @param relation
     * @param cubeConfiguration
     * @return
     */
    public static boolean isRelationAvailable(BITableRelation relation, ICubeConfiguration cubeConfiguration) {
        boolean checkNull = null != relation.getPrimaryTable() && null != relation.getForeignTable() && null != relation.getPrimaryField() && null != relation.getForeignField();
        if (!checkNull) {
            return false;
        }
        boolean isAvailable = BITableKeyUtils.isTableExisted(relation.getForeignTable().getTableSource(), cubeConfiguration) &&BITableKeyUtils.isTableExisted(relation.getPrimaryTable().getTableSource(), cubeConfiguration);
        boolean isRelationValid = isRelationValid(relation);
        return isRelationValid && isAvailable;
    }
}
