package com.fr.bi.conf.base.relation.path;

import com.fr.bi.stable.exception.BITableAbsentException;
import com.fr.bi.stable.relation.BITablePair;
import com.fr.bi.stable.relation.BITableRelation;

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