package com.finebi.cube.conf.relation.path;

import com.fr.bi.stable.exception.BITableAbsentException;
import com.fr.bi.stable.exception.BITablePathConfusionException;
import com.fr.bi.stable.exception.BITableRelationConfusionException;
import com.finebi.cube.relation.BITablePair;
import com.finebi.cube.relation.BITableRelation;
import com.finebi.cube.relation.BITableRelationPath;

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