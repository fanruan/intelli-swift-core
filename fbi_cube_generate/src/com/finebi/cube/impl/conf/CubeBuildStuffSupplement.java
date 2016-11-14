package com.finebi.cube.impl.conf;

import com.finebi.cube.common.log.BILogger;
import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.relation.BITableSourceRelation;
import com.finebi.cube.relation.BITableSourceRelationPath;
import com.fr.bi.conf.manager.update.source.UpdateSettingSource;
import com.fr.bi.stable.constant.DBConstant;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.exception.BITablePathEmptyException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * This class created on 2016/11/5.
 *
 * @author Connery
 * @since Advanced FineBI Analysis 1.0
 */
public class CubeBuildStuffSupplement extends CubeBuildSpecific {
    private static BILogger logger = BILoggerFactory.getLogger(CubeBuildStuffSupplement.class);

    private long userId;

    public CubeBuildStuffSupplement(long userId, Set<CubeTableSource> absentTables, Set<BITableSourceRelation> absentRelations, Set<BITableSourceRelationPath> absentPaths) {
        super(userId);
        this.userId = userId;
        initialStuffInConstruction(absentTables, absentRelations, absentPaths);
        filter();
        calculateDepends(this.tableInConstruction, this.relationInConstruction, this.pathInConstruction);
    }

    private void initialStuffInConstruction(Set<CubeTableSource> absentTables, Set<BITableSourceRelation> absentRelations, Set<BITableSourceRelationPath> absentPaths) {
        this.tableInConstruction = new HashSet<CubeTableSource>(absentTables);
        this.relationInConstruction = new HashSet<BITableSourceRelation>(absentRelations);
        this.pathInConstruction = new HashSet<BITableSourceRelationPath>(absentPaths);
        filter();
        calculateRelevantStuff(this.tableInConstruction);
    }


    @Override
    protected Set<CubeTableSource> choseTables() {
        Set<CubeTableSource> tables = new HashSet<CubeTableSource>();
        tables.addAll(tableInConstruction);
        for (BITableSourceRelation relation : relationInConstruction) {
            tables.add(relation.getPrimaryTable());
        }
        for (BITableSourceRelationPath path : pathInConstruction) {
            try {
                tables.add(path.getFirstRelation().getPrimaryTable());
            } catch (BITablePathEmptyException e) {
                logger.error(e.getMessage(), e);
                continue;
            }
        }
        tables = set2Set(calculateTableSource(tables));
        return filterDuplicateTable(tables);
    }

    @Override
    public Map<CubeTableSource, UpdateSettingSource> getUpdateSettingSources() {
        Map<CubeTableSource, UpdateSettingSource> updateSettingSourceMap = new HashMap<CubeTableSource, UpdateSettingSource>();
        for (CubeTableSource source : tableLayers) {
            updateSettingSourceMap.put(source, setUpdateTypes(source));
        }
        return updateSettingSourceMap;
    }

    @Override
    public String getCubeTaskId() {
        return DBConstant.GLOBAL_UPDATE_TYPE.PART_UPDATE;
    }
}
