package com.finebi.cube.relation;

import com.finebi.cube.location.ICubeSource;
import com.fr.bi.stable.data.db.ICubeFieldSource;
import com.fr.bi.stable.data.source.ICubeTableSource;
import com.fr.bi.stable.exception.BITablePathConfusionException;
import com.fr.bi.stable.exception.BITablePathEmptyException;
import com.fr.bi.stable.utils.algorithem.BIMD5Utils;
import com.fr.bi.stable.utils.program.BINonValueUtils;

import java.util.Iterator;

/**
 * This class created on 2016/3/9.
 *
 * @author Connery
 * @since 4.0
 */
public class BITableSourceRelationPath extends BIBasicRelationPath<ICubeTableSource, ICubeFieldSource, BITableSourceRelation> implements ICubeSource {
    public BITableSourceRelationPath() {
        super();
    }

    public BITableSourceRelationPath(BITableSourceRelation relation) {
        super();
        try {
            addRelationAtHead(relation);
        } catch (BITablePathConfusionException e) {
            throw BINonValueUtils.beyondControl(e);
        }
    }

    /**
     *
     */
    private boolean isTaggedFieldPath = true;

    public BITableSourceRelationPath(BITableSourceRelation[] relations) throws BITablePathConfusionException {
        super();
        BINonValueUtils.checkNull(relations);
        for (int i = 0; i < relations.length; i++) {
            addRelationAtTail(relations[i]);
        }
    }

    public String getSourceID() {
        StringBuffer sb = new StringBuffer();
        Iterator<BITableSourceRelation> relationIterator = getAllRelations().iterator();
        while (relationIterator.hasNext()) {
            BITableSourceRelation relation = relationIterator.next();
            sb.append(relation.getPrimaryTable().getSourceID())
                    .append(relation.getPrimaryField().getFieldName())
                    .append(relation.getForeignTable().getSourceID())
                    .append(relation.getForeignField().getFieldName());
        }
        return BIMD5Utils.getMD5String(new String[]{sb.toString()});
    }


    public void markFieldPath() {
        isTaggedFieldPath = true;
    }

    public void markTablePath() {
        isTaggedFieldPath = false;
    }

    public ICubeFieldSource getPrimaryField() throws BITablePathEmptyException {
        return getFirstRelation().primaryField;
    }


}