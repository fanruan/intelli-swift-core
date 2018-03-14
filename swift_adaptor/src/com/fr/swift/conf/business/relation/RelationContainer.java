package com.fr.swift.conf.business.relation;

import com.finebi.conf.structure.relation.FineBusinessTableRelation;
import com.fr.swift.conf.business.container.AbstractResourceContainer;

import java.util.ArrayList;

/**
 * @author yee
 * @date 2018/3/14
 */
public class RelationContainer extends AbstractResourceContainer<FineBusinessTableRelation> {

    private RelationContainer() {
        super.resourceList = new ArrayList<FineBusinessTableRelation>();
    }

    private static class SingletonHolder {
        private static final RelationContainer INSTANCE = new RelationContainer();
    }

    public static final RelationContainer getContainer() {
        return RelationContainer.SingletonHolder.INSTANCE;
    }
}
