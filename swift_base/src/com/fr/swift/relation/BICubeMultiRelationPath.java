package com.fr.swift.relation;

import com.fr.bi.common.container.BIListContainer;
import com.fr.bi.stable.exception.BITablePathConfusionException;
import com.fr.bi.stable.exception.BITablePathEmptyException;
import com.fr.bi.stable.utils.algorithem.BIMD5Utils;
import com.fr.bi.stable.utils.program.BINonValueUtils;
import com.fr.general.ComparatorUtils;
import com.fr.swift.source.SourceKey;
import com.fr.swift.util.Crasher;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * @author yee
 * @date 2018/1/17
 */
public class BICubeMultiRelationPath extends BIListContainer<BICubeMultiRelation> {
    public BICubeLogicColumnKey getPrimaryField() {
        return getFirstRelation().getPrimaryField();
    }

    public String getSourceID() {
        StringBuffer sb = new StringBuffer();
        for (BICubeMultiRelation relation : getAllRelations()) {
            sb.append(relation.getPrimaryTable().getId())
                    .append(relation.getPrimaryField().getKey())
                    .append(relation.getForeignTable().getId())
                    .append(relation.getForeignField().getKey());
        }
        return BIMD5Utils.getMD5String(new String[]{sb.toString()});
    }

    public BICubeMultiRelationPath() {
        super();
    }

    public BICubeMultiRelationPath(BICubeMultiRelation[] relations) {
        super();
        try {
            BINonValueUtils.checkNull(relations);
            for (BICubeMultiRelation relation : relations) {
                addRelationAtTail(relation);
            }
        } catch (Exception e) {
            Crasher.crash(e);
        }
    }

    public BICubeMultiRelationPath addRelationAtTail(BICubeMultiRelation relation) throws BITablePathConfusionException {
        synchronized (container) {
            try {
                BICubeMultiRelation lastRelation = getLastRelation();
                if (canRelationsBuildPath(lastRelation, relation)) {
                    add(relation);
                } else {
                    throw new BITablePathConfusionException("The primary of relation needed to be added is: " +
                            relation.getPrimaryTable() +
                            ", but current path last relation's foreign table is: " +
                            relation.getForeignField() + ".They should be equal");
                }
            } catch (BITablePathEmptyException ignore) {
                add(relation);
            }
            return this;
        }
    }

    public BICubeMultiRelationPath addRelationAtHead(BICubeMultiRelation relation) {
        synchronized (container) {
            try {
                BICubeMultiRelation firstRelation = getFirstRelation();
                if (canRelationsBuildPath(relation, firstRelation)) {
                    Collection<BICubeMultiRelation> collection = initCollection();
                    collection.addAll(container);
                    clear();
                    add(relation);
                    container.addAll(collection);
                } else {
                    throw new BITablePathConfusionException("The foreign of relation needed to be added is: " +
                            relation.getForeignField() +
                            ", but current path first relation's primary table is: " +
                            relation.getPrimaryTable() + ". They should be equal");
                }

            } catch (Exception ignore) {
                add(relation);
            }
            return this;
        }
    }

    public void removeLastRelation() throws BITablePathEmptyException {
        synchronized (container) {
            container.remove(getLastRelation());
        }
    }

    public void removeFirstRelation() {
        synchronized (container) {
            container.remove(getFirstRelation());
        }
    }

    public boolean canRelationsBuildPath(BICubeMultiRelation part_head, BICubeMultiRelation part_tail) {
        return ComparatorUtils.equals(part_head.getForeignTable(), part_tail.getPrimaryTable());
    }

    public Boolean isEmptyPath() {
        return isEmpty();
    }

    public BICubeMultiRelation getLastRelation() throws BITablePathEmptyException {
        synchronized (container) {

            if (!isEmptyPath()) {
                return getLastOne();
            } else {
                throw new BITablePathEmptyException();
            }
        }
    }

    @Override
    public int size() {
        return super.size();
    }

    public BICubeMultiRelation getFirstRelation() {
        synchronized (container) {
            if (!isEmptyPath()) {
                return getFirstOne();
            } else {
                return Crasher.crash(new BITablePathEmptyException());
            }
        }
    }

    public void copyFrom(BICubeMultiRelationPath path) {
        container.addAll(path.container);
    }

    public List<BICubeMultiRelation> getAllRelations() {
        return (List<BICubeMultiRelation>) super.getContainer();
    }

    @Override
    public int hashCode() {
        int result = 0;
        for (BICubeMultiRelation relation : container) {
            result = 31 * result + (relation != null ? relation.hashCode() : 0);
        }
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof com.finebi.cube.relation.BITableRelationPath)) {
            return false;
        }

        BICubeMultiRelationPath that = (BICubeMultiRelationPath) o;
        Iterator<BICubeMultiRelation> it = container.iterator();
        Iterator<BICubeMultiRelation> thatIt = that.container.iterator();

        if (container.size() == that.container.size()) {
            while (it.hasNext()) {
                BICubeMultiRelation relation = it.next();
                BICubeMultiRelation thatRelation = thatIt.next();
                if (!ComparatorUtils.equals(relation, thatRelation)) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    public SourceKey getStartTable() {
        return getFirstRelation().primaryTable;
    }

    public SourceKey getEndTable() throws BITablePathEmptyException {
        return getLastRelation().getForeignTable();
    }
}
