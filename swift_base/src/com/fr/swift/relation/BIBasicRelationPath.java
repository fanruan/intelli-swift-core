package com.fr.swift.relation;

import com.fr.bi.common.container.BIListContainer;
import com.fr.bi.stable.exception.BITablePathConfusionException;
import com.fr.bi.stable.exception.BITablePathEmptyException;
import com.fr.bi.stable.utils.program.BINonValueUtils;
import com.fr.general.ComparatorUtils;
import com.fr.swift.util.Crasher;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * @author yee
 * @date 2018/1/17
 */
public class BIBasicRelationPath<T, F, R extends BIBasicRelation<T, F>> extends BIListContainer<R> {
    private static final long serialVersionUID = -8774217185899006329L;

    public BIBasicRelationPath() {
        super();
    }

    public BIBasicRelationPath(R[] relations) throws BITablePathConfusionException {
        super();
        BINonValueUtils.checkNull(relations);
        for (R relation : relations) {
            addRelationAtTail(relation);
        }
    }

    public BIBasicRelationPath addRelationAtTail(R relation) throws BITablePathConfusionException {
        synchronized (container) {
            try {
                R lastRelation = getLastRelation();
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

    public BIBasicRelationPath addRelationAtHead(R relation) {
        synchronized (container) {
            try {
                R firstRelation = getFirstRelation();
                if (canRelationsBuildPath(relation, firstRelation)) {
                    Collection<R> collection = initCollection();
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

    public boolean canRelationsBuildPath(R part_head, R part_tail) {
        return ComparatorUtils.equals(part_head.getForeignTable(), part_tail.getPrimaryTable());
    }

    public Boolean isEmptyPath() {
        return isEmpty();
    }

    public R getLastRelation() throws BITablePathEmptyException {
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

    public R getFirstRelation() {
        synchronized (container) {
            if (!isEmptyPath()) {
                return getFirstOne();
            } else {
                return Crasher.crash(new BITablePathEmptyException());
            }
        }
    }

    public void copyFrom(BIBasicRelationPath<T, F, R> path) {
        container.addAll(path.container);
    }

    public List<R> getAllRelations() {
        return (List<R>) super.getContainer();
    }

    @Override
    public int hashCode() {
        int result = 0;
        for (R relation : container) {
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

        BIBasicRelationPath<T, F, R> that = (BIBasicRelationPath<T, F, R>) o;
        Iterator<R> it = container.iterator();
        Iterator<R> thatIt = that.container.iterator();

        if (container.size() == that.container.size()) {
            while (it.hasNext()) {
                R relation = it.next();
                R thatRelation = thatIt.next();
                if (!ComparatorUtils.equals(relation, thatRelation)) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    public T getStartTable() {
        return getFirstRelation().primaryTable;
    }

    public T getEndTable() throws BITablePathEmptyException {
        return getLastRelation().getForeignTable();
    }
}