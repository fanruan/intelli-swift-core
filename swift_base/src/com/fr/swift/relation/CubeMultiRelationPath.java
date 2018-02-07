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
public class CubeMultiRelationPath {
    private List<CubeMultiRelation> container;

    public CubeLogicColumnKey getPrimaryField() {
        return getFirstRelation().getPrimaryField();
    }

    public String getSourceID() {
        StringBuffer sb = new StringBuffer();
        for (CubeMultiRelation relation : getAllRelations()) {
            sb.append(relation.getPrimaryTable().getId())
                    .append(relation.getPrimaryField().getKey())
                    .append(relation.getForeignTable().getId())
                    .append(relation.getForeignField().getKey());
        }
        return MD5Utils.getMD5String(new String[]{sb.toString()});
    }

    public CubeMultiRelationPath() {
        container = new ArrayList<CubeMultiRelation>();
    }

    public CubeMultiRelationPath(CubeMultiRelation[] relations) {
        for (CubeMultiRelation relation : relations) {
            addRelationAtTail(relation);
        }
    }

    public CubeMultiRelationPath addRelationAtTail(CubeMultiRelation relation) {
        synchronized (container) {
            CubeMultiRelation lastRelation = null;
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

    public void add(CubeMultiRelation relation) {
        synchronized (container) {
            container.add(relation);
        }
    }

    protected void clear() {
        synchronized (container) {
            container.clear();
        }
    }

    public CubeMultiRelationPath addRelationAtHead(CubeMultiRelation relation) {
        synchronized (container) {
            try {
                CubeMultiRelation firstRelation = getFirstRelation();
                if (canRelationsBuildPath(relation, firstRelation)) {
                    Collection<CubeMultiRelation> collection = new ArrayList<CubeMultiRelation>();
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

    public boolean canRelationsBuildPath(CubeMultiRelation part_head, CubeMultiRelation part_tail) {
        return ComparatorUtils.equals(part_head.getForeignTable(), part_tail.getPrimaryTable());
    }

    public Boolean isEmptyPath() {
        synchronized (container) {
            return container.isEmpty();
        }
    }

    public CubeMultiRelation getLastRelation() {
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

    public CubeMultiRelation getFirstRelation() {
        synchronized (container) {
            if (!isEmptyPath()) {
                return getFirstOne();
            } else {
                return Crasher.crash("Path empty");
            }
        }
    }

    public void copyFrom(CubeMultiRelationPath path) {
        container.addAll(path.container);
    }

    public List<CubeMultiRelation> getAllRelations() {
        return Collections.unmodifiableList(container);
    }

    @Override
    public int hashCode() {
        int result = 0;
        for (CubeMultiRelation relation : container) {
            result = 31 * result + (relation != null ? relation.hashCode() : 0);
        }
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CubeMultiRelation)) {
            return false;
        }

        CubeMultiRelationPath that = (CubeMultiRelationPath) o;
        Iterator<CubeMultiRelation> it = container.iterator();
        Iterator<CubeMultiRelation> thatIt = that.container.iterator();

        if (container.size() == that.container.size()) {
            while (it.hasNext()) {
                CubeMultiRelation relation = it.next();
                CubeMultiRelation thatRelation = thatIt.next();
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

    protected CubeMultiRelation getLastOne() throws IndexOutOfBoundsException {
        return container.get(container.size() - 1);
    }

    protected CubeMultiRelation getFirstOne() throws IndexOutOfBoundsException {
        return container.get(0);
    }
}
