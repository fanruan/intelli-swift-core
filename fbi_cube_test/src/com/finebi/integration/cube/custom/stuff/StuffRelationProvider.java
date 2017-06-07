package com.finebi.integration.cube.custom.stuff;

import com.finebi.cube.conf.relation.BISystemTableRelationManager;
import com.finebi.cube.relation.BITableRelation;
import com.finebi.cube.relation.BITableRelationPath;
import com.finebi.integration.cube.custom.stuff.creater.TablePathCreater;
import com.finebi.integration.cube.custom.stuff.creater.TableRelationsCreater;
import com.fr.bi.stable.exception.BITablePathConfusionException;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Lucifer on 2017-6-1.
 *
 * @author Lucifer
 * @since Advanced FineBI Analysis 1.0
 */
public class StuffRelationProvider extends BISystemTableRelationManager {

    Set<BITableRelation> biTableRelationSet = new HashSet<BITableRelation>();
    Set<BITableRelationPath> biTableRelationPathSet = new HashSet<BITableRelationPath>();

    public StuffRelationProvider() throws BITablePathConfusionException {
        biTableRelationSet.add(TableRelationsCreater.getTableRelationAB());
        biTableRelationSet.add(TableRelationsCreater.getTableRelationBC());
        biTableRelationSet.add(TableRelationsCreater.getTableRelationCD());

        biTableRelationPathSet.add(TablePathCreater.getPathABC());
        biTableRelationPathSet.add(TablePathCreater.getPathBCD());
        biTableRelationPathSet.add(TablePathCreater.getPathABCD());
    }

    @Override
    public Set<BITableRelation> getAllTableRelation(long userId) {
        return biTableRelationSet;
    }

    @Override
    public Set<BITableRelationPath> getAllTablePath(long userId) {
        return biTableRelationPathSet;
    }

}
