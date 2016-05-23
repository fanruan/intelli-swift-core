package com.finebi.cube.conf.relation.relation;

import com.fr.bi.common.container.BICollectionContainer;
import com.fr.bi.common.container.BISetContainer;
import com.finebi.cube.relation.BITableRelation;

import java.util.Collection;
import java.util.Set;

/**
 * 表关联的容器，方法设为保护。具体操作通过Service的manager来。
 * 防止获得对象后对对象的修改
 * Created by Connery on 2016/1/12.
 */
public class BIRelationContainer extends BISetContainer<BITableRelation> implements IRelationContainer {
    @Override
    public boolean isEmpty() {
        return super.isEmpty();
    }

    @Override
    protected void add(BITableRelation element) {
        super.add(element);
    }

    @Override
    public Boolean contain(BITableRelation element) {
        return super.contain(element);
    }

    @Override
    protected void setContainer(Collection<BITableRelation> container) {
        super.setContainer(container);
    }

    @Override
    protected void useContent(BICollectionContainer targetContainer) {
        super.useContent(targetContainer);
    }

    @Override
    protected void remove(BITableRelation element) {
        super.remove(element);
    }

    @Override
    protected void clear() {
        super.clear();
    }

    @Override
    public Set<BITableRelation> getContainer() {
        return super.getContainer();
    }

    @Override
    protected int size() {
        return super.size();
    }
}