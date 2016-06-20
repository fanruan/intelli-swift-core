package com.finebi.cube.structure;

import com.finebi.cube.relation.BIBasicRelationPath;
import com.finebi.cube.structure.column.BIColumnKey;
import com.fr.bi.stable.exception.BITablePathEmptyException;
import com.fr.bi.stable.utils.algorithem.BIMD5Utils;

import java.util.Iterator;

/**
 * This class created on 2016/4/10.
 *
 * @author Connery
 * @since 4.0
 */
public class BICubeTablePath extends BIBasicRelationPath<ITableKey, BIColumnKey, BICubeRelation> {


    public BIColumnKey getPrimaryField() throws BITablePathEmptyException {
        return getFirstRelation().getPrimaryField();
    }

    public String getSourceID() {
        StringBuffer sb = new StringBuffer();
        Iterator<BICubeRelation> relationIterator = getAllRelations().iterator();
        while (relationIterator.hasNext()) {
            BICubeRelation relation = relationIterator.next();
            sb.append(relation.getPrimaryTable().getSourceID()).append(relation.getForeignTable().getSourceID());
        }
        return BIMD5Utils.getMD5String(new String[]{sb.toString()});
    }
}
