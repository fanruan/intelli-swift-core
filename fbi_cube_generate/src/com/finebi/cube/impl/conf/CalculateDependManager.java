package com.finebi.cube.impl.conf;

import com.finebi.cube.conf.CalculateDepend;
import com.finebi.cube.relation.BITableSourceRelation;
import com.finebi.cube.relation.BITableSourceRelation4Incremental;
import com.finebi.cube.relation.BITableSourceRelationPath;
import com.finebi.cube.relation.BITableSourceRelationPath4Incremetal;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.exception.BITablePathEmptyException;
import com.fr.bi.stable.utils.code.BILogger;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by kary on 2016/6/13.
 */
public abstract class CalculateDependManager implements CalculateDepend {
    public Set<CubeTableSource> analysisTableSources;

    @Override
    public Set<BITableSourceRelation4Incremental> calRelations(Set<BITableSourceRelation> biTableSourceRelationSet) {
        Set<BITableSourceRelation4Incremental> biTableSourceRelation4IncrementalSet = new HashSet<BITableSourceRelation4Incremental>();
        for (BITableSourceRelation biTableSourceRelation : biTableSourceRelationSet) {
            if (analysisTableSources.contains(biTableSourceRelation.getForeignTable())) {
                biTableSourceRelation4IncrementalSet.add(new BITableSourceRelation4Incremental(biTableSourceRelation, biTableSourceRelation.getForeignTable()));
            } else 
            if (analysisTableSources.contains(biTableSourceRelation.getPrimaryTable())) {
                biTableSourceRelation4IncrementalSet.add(new BITableSourceRelation4Incremental(biTableSourceRelation, biTableSourceRelation.getPrimaryTable()));
            } else {
                biTableSourceRelation4IncrementalSet.add(new BITableSourceRelation4Incremental(biTableSourceRelation, null));
            }
        }
        return biTableSourceRelation4IncrementalSet;
    }

    @Override
    public Set<BITableSourceRelationPath4Incremetal> calRelationPath(Set<BITableSourceRelationPath> biTableSourceRelationPathSet, Set<BITableSourceRelation> tableRelationSet) {
        Set<BITableSourceRelationPath4Incremetal> biTableSourceRelationPath4IncremetalSet=new HashSet<BITableSourceRelationPath4Incremetal>();
        for (BITableSourceRelationPath biTableSourceRelationPath : biTableSourceRelationPathSet) {
            Set<BITableSourceRelation> biTableSourceRelationSet = new HashSet<BITableSourceRelation>();
            try {
                while (tableRelationSet.contains(biTableSourceRelationPath.getFirstRelation())) {
                    biTableSourceRelationSet.add(biTableSourceRelationPath.getFirstRelation());
                    biTableSourceRelationPath.removeFirstRelation();
                }
                while (tableRelationSet.contains(biTableSourceRelationPath.getLastRelation())) {
                    biTableSourceRelationSet.add(biTableSourceRelationPath.getLastRelation());
                    biTableSourceRelationPath.removeLastRelation();
                }
                BITableSourceRelationPath4Incremetal biTableSourceRelationPath4Incremetal = new BITableSourceRelationPath4Incremetal(biTableSourceRelationPath, biTableSourceRelationSet);
                biTableSourceRelationPath4IncremetalSet.add(biTableSourceRelationPath4Incremetal);
            } catch (BITablePathEmptyException e) {
                BILogger.getLogger().error(e.getMessage());
            }
        }

        return biTableSourceRelationPath4IncremetalSet;
    }
}
