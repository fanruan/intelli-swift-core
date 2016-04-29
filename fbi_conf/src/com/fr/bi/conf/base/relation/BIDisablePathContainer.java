package com.fr.bi.conf.base.relation;

import com.fr.bi.common.container.BICollectionContainer;
import com.fr.bi.common.container.BISetContainer;
import com.fr.bi.stable.relation.BITableRelationPath;

import java.util.Collection;
import java.util.Set;

/**
 * Created by Connery on 2016/1/13.
 */
public class BIDisablePathContainer extends BISetContainer<BITableRelationPath> {

    @Override
    protected Set<BITableRelationPath> getContainer() {
        return super.getContainer();
    }

    @Override
    protected void add(BITableRelationPath element) {
        super.add(element);
    }

    @Override
    protected Boolean contain(BITableRelationPath element) {
        return super.contain(element);
    }

    @Override
    protected void setContainer(Collection<BITableRelationPath> container) {
        super.setContainer(container);
    }

    @Override
    protected void useContent(BICollectionContainer targetContainer) {
        super.useContent(targetContainer);
    }

    @Override
    protected void remove(BITableRelationPath element) {
        super.remove(element);
    }

    @Override
    protected void clear() {
        super.clear();
    }

    @Override
    protected boolean isEmpty() {
        return super.isEmpty();
    }

    @Override
    protected int size() {
        return super.size();
    }
}