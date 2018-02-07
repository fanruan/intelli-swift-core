package com.fr.swift.relation;

import com.fr.general.ComparatorUtils;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.core.MD5Utils;
import com.fr.swift.util.Crasher;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * @author yee
 * @date 2018/1/17
 */
public class BICubeMultiRelationPath {
    private List<BICubeMultiRelation> container;
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
        return MD5Utils.getMD5String(new String[]{sb.toString()});
    }

    public BICubeMultiRelationPath() {
        container = new ArrayList<BICubeMultiRelation>();
    }

    public BICubeMultiRelationPath(BICubeMultiRelation[] relations) {
        for (BICubeMultiRelation relation : relations) {
            addRelationAtTail(relation);
        }
    }

    public BICubeMultiRelationPath addRelationAtTail(BICubeMultiRelation relation) {
        synchronized (container) {
            BICubeMultiRelation lastRelation = null;
            try {
                lastRelation = getLastRelation();
            } catch (Exception ignore) {
                add(relation);
                return this;
            }
            if (null != lastRelation && canRelationsBuildPath(lastRelation, relation)) {
                add(relation);
            } else {
                Crasher.crash("The primary of relation needed to be added is: " +
                        relation.getPrimaryTable() +
                        ", but current path last relation's foreign table is: " +
                        relation.getForeignField() + ".They should be equal");
            }
            return this;
        }
    }

    public void add(BICubeMultiRelation relation) {
        synchronized (container) {
            container.add(relation);
        }
    }

    protected void clear() {
        synchronized (container) {
            container.clear();
        }
    }

    public BICubeMultiRelationPath addRelationAtHead(BICubeMultiRelation relation) {
        synchronized (container) {
            try {
                BICubeMultiRelation firstRelation = getFirstRelation();
                if (canRelationsBuildPath(relation, firstRelation)) {
                    Collection<BICubeMultiRelation> collection = new ArrayList<BICubeMultiRelation>();
                    collection.addAll(container);
                    clear();
                    add(relation);
                    container.addAll(collection);
                } else {
                    Crasher.crash("The foreign of relation needed to be added is: " +
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

    public void removeLastRelation() {
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
        synchronized (container) {
            return container.isEmpty();
        }
    }

    public BICubeMultiRelation getLastRelation() {
        synchronized (container) {

            if (!isEmptyPath()) {
                return getLastOne();
            } else {
                return Crasher.crash("Path empty");
            }
        }
    }

    public int size() {
        synchronized (container) {
            return container.size();
        }
    }

    public BICubeMultiRelation getFirstRelation() {
        synchronized (container) {
            if (!isEmptyPath()) {
                return getFirstOne();
            } else {
                return Crasher.crash("Path empty");
            }
        }
    }

    public void copyFrom(BICubeMultiRelationPath path) {
        container.addAll(path.container);
    }

    public List<BICubeMultiRelation> getAllRelations() {
        return Collections.unmodifiableList(container);
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
        if (!(o instanceof BICubeMultiRelation)) {
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

    public SourceKey getEndTable() {
        return getLastRelation().getForeignTable();
    }

    protected BICubeMultiRelation getLastOne() throws IndexOutOfBoundsException {
        return container.get(container.size() - 1);
    }

    protected BICubeMultiRelation getFirstOne() throws IndexOutOfBoundsException {
        return container.get(0);
    }
}
