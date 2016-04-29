package com.fr.bi.conf.base.relation.path;

import com.fr.bi.conf.base.relation.BITableRelationAnalysisService;
import com.fr.bi.stable.exception.BITablePathConfusionException;
import com.fr.bi.stable.exception.BITableAbsentException;
import com.fr.bi.stable.exception.BITableRelationConfusionException;
import com.fr.bi.stable.relation.BITablePair;
import com.fr.bi.stable.relation.BITableRelation;
import com.fr.bi.stable.relation.BITableRelationPath;

import java.util.Set;

/**
 * Created by Connery on 2016/1/14.
 */
public class BITableRelationshipManager implements BITableRelationshipService {
    BICommonSeniorTableAnalysisService commonSeniorTableContainer;
    BITablePathAnalysersService tablePathAnalysersService;

    public BITableRelationshipManager(BITableRelationAnalysisService biTableRelationAnalyser) {
        commonSeniorTableContainer = new BICommonSeniorTableAnalyser();
        tablePathAnalysersService = new BITablePathAnalysersManager(biTableRelationAnalyser);
    }

    @Override
    public void addBITableRelation(BITableRelation relation) {
        commonSeniorTableContainer.registerOneTableRelation(relation);
        tablePathAnalysersService.registerBITableRelation(relation);
    }

    @Override
    public Set<BITableRelationPath> getAllPath(BITablePair tablePair)
            throws BITableAbsentException, BITableRelationConfusionException, BITablePathConfusionException {
        return tablePathAnalysersService.analysisAllPath(tablePair);
    }

    @Override
    public BITableContainer getCommonSeniorTables(BITablePair tablePair) throws BITableAbsentException {
        return commonSeniorTableContainer.analysisCommonRelation(tablePair);
    }

    @Override
    public void removeTableRelation(BITableRelation relation) {
        commonSeniorTableContainer.removeTableRelation(relation);
        tablePathAnalysersService.removeBITableRelation(relation);
    }

    @Override
    public void clear() {
        tablePathAnalysersService.clear();
        commonSeniorTableContainer.clear();
    }

    @Override
    public boolean contain(BITableRelation tableRelation) {
        return commonSeniorTableContainer.containTableRelation(tableRelation)
                && tablePathAnalysersService.contain(tableRelation);
    }
}