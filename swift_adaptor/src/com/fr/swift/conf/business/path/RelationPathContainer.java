package com.fr.swift.conf.business.path;

import com.finebi.conf.structure.path.FineBusinessTableRelationPath;
import com.fr.swift.conf.business.container.AbstractResourceContainer;

import java.util.ArrayList;

/**
 * @author yee
 * @date 2018/3/19
 */
public class RelationPathContainer extends AbstractResourceContainer<FineBusinessTableRelationPath> {

    private RelationPathContainer() {
        super.resourceList = new ArrayList<FineBusinessTableRelationPath>();
    }

    private static class SingletonHolder {
        private static final RelationPathContainer INSTANCE = new RelationPathContainer();
    }

    public static final RelationPathContainer getContainer() {
        return RelationPathContainer.SingletonHolder.INSTANCE;
    }
}