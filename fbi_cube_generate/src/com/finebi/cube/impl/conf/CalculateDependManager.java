package com.finebi.cube.impl.conf;

import com.finebi.cube.conf.CalculateDepend;
import com.finebi.cube.conf.table.BIBusinessTable;
import com.finebi.cube.relation.*;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.exception.BITablePathEmptyException;
import com.fr.bi.stable.utils.code.BILogger;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by kary on 2016/6/13.
 */
public abstract class CalculateDependManager implements CalculateDepend {
    private Set<BIBusinessTable> biBusinessTableSet;
    public Set<CubeTableSource> analysisTableSources;

    @Override
    public Set<BITableSourceRelation4Incremental> calRelations(Set<BITableSourceRelation> biTableSourceRelationSet) {
        Set<BITableSourceRelation4Incremental> biTableSourceRelation4IncrementalSet = new HashSet<BITableSourceRelation4Incremental>();
        for (BITableSourceRelation biTableSourceRelation : biTableSourceRelationSet) {
            if (analysisTableSources.contains(biTableSourceRelation.getForeignTable())) {
                BITableSourceRelation4Incremental biTableSourceRelation4Incremental = new BITableSourceRelation4Incremental(biTableSourceRelation, biTableSourceRelation.getForeignTable());
            }
            if (analysisTableSources.contains(biTableSourceRelation.getPrimaryTable())) {
                BITableSourceRelation4Incremental biTableSourceRelation4Incremental = new BITableSourceRelation4Incremental(biTableSourceRelation, biTableSourceRelation.getPrimaryTable());
            }

        }
        return biTableSourceRelation4IncrementalSet;
    }

    @Override
    public Set<BITableSourceRelationPath4Incremetal> calRelationPath(Set<BITableSourceRelationPath> biTableSourceRelationPathSet, Set<BITableSourceRelation> tableRelationSet) {
        Set<BITableSourceRelationPath4Incremetal> biTableSourceRelationPath4IncremetalSet=new HashSet<BITableSourceRelationPath4Incremetal>();
        for (BITableSourceRelationPath biTableSourceRelationPath : biTableSourceRelationPathSet) {
            try {
                biTableSourceRelationPath.getFirstRelation();
                BITableSourceRelationPath frontPath = new BITableSourceRelationPath();
                frontPath.copyFrom(biTableSourceRelationPath);
                frontPath.removeLastRelation();
            } catch (BITablePathEmptyException e) {
                BILogger.getLogger().error(e.getMessage());
            }
        }
        return biTableSourceRelationPath4IncremetalSet;
    }

//    @Override
//    public void setOriginal(Set<BIBusinessTable> analysisTables) {
//        for (BIBusinessTable analysisTable : analysisTables) {
//            analysisTableSources.add(analysisTable.getTableSource());
//        }
//
//    }
}
