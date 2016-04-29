package com.fr.bi.conf.base.relation.path;

import com.fr.bi.conf.base.relation.BITableRelationAnalysisService;
import com.fr.bi.stable.data.Table;
import com.fr.bi.stable.exception.BITableAbsentException;
import com.fr.bi.stable.exception.BITablePathConfusionException;
import com.fr.bi.stable.exception.BITableRelationConfusionException;
import com.fr.bi.exception.BIKeyAbsentException;
import com.fr.bi.exception.BIKeyDuplicateException;
import com.fr.bi.stable.relation.BITablePair;
import com.fr.bi.stable.relation.BITableRelation;
import com.fr.bi.stable.relation.BITableRelationPath;
import com.fr.bi.stable.utils.program.BINonValueUtils;

import java.util.Set;

/**
 * Created by Connery on 2016/1/13.
 */
public class BITablePathAnalysersManager implements BITablePathAnalysersService {
    private BITablePathAnalyserContainer pathContainer;
    private BITableRelationAnalysisService biTableRelationAnalyser;

    public BITablePathAnalysersManager(BITableRelationAnalysisService biTableRelationAnalyser) {
        pathContainer = new BITablePathAnalyserContainer();
        this.biTableRelationAnalyser = biTableRelationAnalyser;
    }

    private BITablePathAnalyser getTablePathAnalyser(Table table) {
        synchronized (pathContainer) {
            try {
                return pathContainer.getValue(table);
            } catch (BIKeyAbsentException ignore) {
                BITablePathAnalyser pathAnalyser = new BITablePathAnalyser(table, biTableRelationAnalyser);
                try {
                    pathContainer.putKeyValue(table, pathAnalyser);
                } catch (BIKeyDuplicateException ignore_) {
                    throw BINonValueUtils.beyondControl();
                }
                return pathAnalyser;
            }
        }
    }

    @Override
    public Set<BITableRelationPath> analysisAllPath(BITablePair biTablePair)
            throws BITableAbsentException, BITableRelationConfusionException, BITablePathConfusionException {
        BITablePathAnalyser pathAnalyser = getTablePathAnalyser(biTablePair.getFrom());
        return pathAnalyser.getAllRelationPath(biTablePair.getTo());
    }

    @Override
    public void registerBITableRelation(BITableRelation biTableRelation) {
        Table primaryTable = biTableRelation.getPrimaryTable();
        Table foreignTable = biTableRelation.getForeignTable();
        BITablePathAnalyser primaryTablePathAnalyser;
        BITablePathAnalyser foreignTablePathAnalyser;
        primaryTablePathAnalyser = getTablePathAnalyser(primaryTable);
        foreignTablePathAnalyser = getTablePathAnalyser(foreignTable);
        if (!primaryTablePathAnalyser.containPathRelation(foreignTablePathAnalyser)) {
            primaryTablePathAnalyser.buildPathRelation(foreignTablePathAnalyser);
        }
    }

    @Override
    public boolean contain(BITableRelation biTableRelation) {
        Table primaryTable = biTableRelation.getPrimaryTable();
        Table foreignTable = biTableRelation.getForeignTable();
        BITablePathAnalyser primaryTablePathAnalyser;
        BITablePathAnalyser foreignTablePathAnalyser;
        primaryTablePathAnalyser = getTablePathAnalyser(primaryTable);
        foreignTablePathAnalyser = getTablePathAnalyser(foreignTable);
        return  primaryTablePathAnalyser.containPathRelation(foreignTablePathAnalyser);
    }

    @Override
    public void removeBITableRelation(BITableRelation biTableRelation) {
        Table primaryTable = biTableRelation.getPrimaryTable();
        Table foreignTable = biTableRelation.getForeignTable();
        BITablePathAnalyser primaryTablePathAnalyser;
        BITablePathAnalyser foreignTablePathAnalyser;
        primaryTablePathAnalyser = getTablePathAnalyser(primaryTable);
        foreignTablePathAnalyser = getTablePathAnalyser(foreignTable);
        if (primaryTablePathAnalyser.containPathRelation(foreignTablePathAnalyser)) {
            primaryTablePathAnalyser.destoryPathRelation(foreignTablePathAnalyser);
        }
    }

    @Override
    public void clear() {
        pathContainer.clear();
        biTableRelationAnalyser.clear();
    }
}