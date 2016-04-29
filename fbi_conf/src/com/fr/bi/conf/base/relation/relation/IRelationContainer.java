package com.fr.bi.conf.base.relation.relation;

import com.fr.bi.stable.relation.BITableRelation;

import java.util.Set;

/**
 * This class created on 2016/4/22.
 *
 * @author Connery
 * @since 4.0
 */
public interface IRelationContainer {
    boolean isEmpty();

    Boolean contain(BITableRelation element);

    Set<BITableRelation> getContainer();
}
