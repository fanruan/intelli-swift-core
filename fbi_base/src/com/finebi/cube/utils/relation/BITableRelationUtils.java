package com.finebi.cube.utils.relation;

import com.finebi.cube.relation.BITableRelation;

/**
 * Created by kary on 2016/9/21.
 */
public class BITableRelationUtils {
    public static boolean isRelationValid(BITableRelation biTableRelation) {
        boolean isTypeCorrect = biTableRelation.getForeignField().getFieldType() == biTableRelation.getPrimaryField().getFieldType();
        return isTypeCorrect;
    }

}
