package com.fr.bi.conf.base.relation.path;

import com.fr.bi.stable.exception.BITableAbsentException;
import com.fr.bi.stable.exception.BITablePathConfusionException;
import com.fr.bi.stable.exception.BITableRelationConfusionException;
import com.fr.bi.stable.relation.BITablePair;
import com.fr.bi.stable.relation.BITableRelation;
import com.fr.bi.stable.relation.BITableRelationPath;

import java.util.Set;

/**
 * Created by Connery on 2016/1/14.
 */
public interface BITableRelationshipService {
    void addBITableRelation(BITableRelation relation);

    Set<BITableRelationPath> getAllPath(BITablePair tablePair)
            throws BITableAbsentException, BITableRelationConfusionException, BITablePathConfusionException;

    BITableContainer getCommonSeniorTables(BITablePair tablePair) throws BITableAbsentException;

    void removeTableRelation(BITableRelation relation);

    void clear();

    boolean contain(BITableRelation tableRelation);
}