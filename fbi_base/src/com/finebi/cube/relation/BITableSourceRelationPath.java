package com.finebi.cube.relation;

import com.finebi.cube.location.ICubeSource;
import com.fr.bi.stable.data.db.ICubeFieldSource;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.exception.BITablePathConfusionException;
import com.fr.bi.stable.exception.BITablePathEmptyException;
import com.fr.bi.stable.utils.algorithem.BIMD5Utils;
import com.fr.bi.stable.utils.program.BINonValueUtils;

import java.util.Iterator;
import java.util.List;

/**
 * This class created on 2016/3/9.
 *
 * @author Connery
 * @since 4.0
 */
public class BITableSourceRelationPath extends BIBasicRelationPath<CubeTableSource, ICubeFieldSource, BITableSourceRelation> implements ICubeSource {
    private static final long serialVersionUID = -7079918444114325493L;

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
        for (BITableSourceRelation relation : relations) {
            addRelationAtTail(relation);
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

    @Override
    public boolean equals(Object o) {
        if (o instanceof BITableSourceRelationPath) {
            List<BITableSourceRelation> oRelations = ((BITableSourceRelationPath) o).getAllRelations();
            if (oRelations.size() == this.getAllRelations().size()) {
                for (BITableSourceRelation oRelation : oRelations) {
                    if (!this.getAllRelations().contains(oRelation)) {
                        return false;
                    }
                }
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
}