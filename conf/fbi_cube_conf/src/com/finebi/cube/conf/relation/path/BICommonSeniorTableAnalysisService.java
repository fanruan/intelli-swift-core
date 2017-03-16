package com.finebi.cube.conf.relation.path;

import com.finebi.cube.relation.BITablePair;
import com.finebi.cube.relation.BITableRelation;
import com.fr.bi.stable.exception.BITableAbsentException;


/**
 * Created by Connery on 2016/1/14.
 */
public interface BICommonSeniorTableAnalysisService {
    void registerOneTableRelation(BITableRelation biTableRelation);

    BITableContainer analysisCommonRelation(BITablePair tablePair)
            throws BITableAbsentException;

    void removeTableRelation(BITableRelation biTableRelation);

    boolean containTableRelation(BITableRelation biTableRelation);

    void clear();
}