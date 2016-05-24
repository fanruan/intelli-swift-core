package com.finebi.cube.relation;

import com.fr.bi.common.container.BIListContainer;
import com.fr.bi.stable.exception.BITablePathConfusionException;
import com.fr.bi.stable.exception.BITablePathEmptyException;
import com.fr.bi.stable.utils.program.BINonValueUtils;
import com.fr.general.ComparatorUtils;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * This class created on 2016/3/9.
 *
 * @author Connery
 * @since 4.0
 */
public class BIBasicRelationPath<T, F, R extends BIBasicRelation<T, F>> extends BIListContainer<R> {
    public BIBasicRelationPath() {
        super();
    }

    public BIBasicRelationPath(R[] relations) throws BITablePathConfusionException {
        super();
        BINonValueUtils.checkNull(relations);
        for (int i = 0; i < relations.length; i++) {
            addRelationAtTail(relations[i]);
        }
    }

    public BIBasicRelationPath addRelationAtTail(R relation) throws BITablePathConfusionException {
        synchronized (container) {
            try {
                R lastRelation = getLastRelation();
                if (canRelationsBuildPath(lastRelation, relation)) {
                    add(relation);
                } else {
                    throw new BITablePathConfusionException("The primary of relation needed to be added is:" +
                            "" + relation.getPrimaryTable().toString() + "" +
                            ",but current path last relation's foreign table is:" + relation.getForeignField().toString() + ".They should be equal");
                }
            } catch (BITablePathEmptyException ignore) {
                add(relation);
            }
            return this;
        }
    }

    public BIBasicRelationPath addRelationAtHead(R relation) throws BITablePathConfusionException {
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
                    throw new BITablePathConfusionException("The foreign of relation needed to be added is:" +
                            "" + relation.getForeignField().toString() + "" +
                            ",but current path first relation's primary table is:" + relation.getPrimaryTable().toString() + ".They should be equal");
                }

            } catch (BITablePathEmptyException ignore) {
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

    public R getFirstRelation() throws BITablePathEmptyException {
        synchronized (container) {
            if (!isEmptyPath()) {
                return getFirstOne();
            } else {
                throw new BITablePathEmptyException();
            }
        }
    }

    public void copyFrom(BIBasicRelationPath path) {
        container.addAll(path.container);
    }

    public List<R> getAllRelations() {
        return (List<R>) super.getContainer();
    }

    @Override
    public int hashCode() {
        int result = 0;
        Iterator<R> it = container.iterator();
        while (it.hasNext()) {
            R relation = it.next();
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

        BIBasicRelationPath that = (BIBasicRelationPath) o;
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

    public T getStartTable() throws BITablePathEmptyException {
        return getFirstRelation().primaryTable;
    }

    public T getEndTable() throws BITablePathEmptyException {
        return getLastRelation().getForeignTable();
    }
}
