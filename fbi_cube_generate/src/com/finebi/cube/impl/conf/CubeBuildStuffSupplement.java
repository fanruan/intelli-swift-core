package com.finebi.cube.impl.conf;

import com.finebi.cube.ICubeConfiguration;
import com.finebi.cube.common.log.BILogger;
import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.conf.BICubeConfiguration;
import com.finebi.cube.exception.BICubeResourceAbsentException;
import com.finebi.cube.location.ICubeResourceLocation;
import com.finebi.cube.location.ICubeResourceRetrievalService;
import com.finebi.cube.relation.BITableSourceRelation;
import com.finebi.cube.relation.BITableSourceRelationPath;
import com.finebi.cube.structure.BITableKey;
import com.fr.bi.conf.manager.update.source.UpdateSettingSource;
import com.fr.bi.stable.constant.DBConstant;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.engine.CubeTaskType;
import com.fr.bi.stable.exception.BITablePathEmptyException;
import com.fr.bi.stable.utils.file.BIFileUtils;

import java.io.File;
import java.io.IOException;
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

    @Override
    public CubeTaskType getTaskType() {
        return CubeTaskType.PART;
    }

    /**
     * rename advanced to temp
     * rename tCube to advanced
     * delete temp
     *
     * @return
     */
    @Override
    public boolean replaceOldCubes() {

        ICubeConfiguration tempConf = BICubeConfiguration.getTempConf(String.valueOf(userId));
        ICubeConfiguration advancedConf = BICubeConfiguration.getConf(String.valueOf(userId));
        try {
            BIFileUtils.moveFile(tempConf.getRootURI().getPath().toString(), advancedConf.getRootURI().getPath().toString());
        } catch (Exception e) {
            BILoggerFactory.getLogger().error(e.getMessage(), e);
        }
        return true;
    }

    @Override
    protected void copyTableFile(ICubeResourceRetrievalService tempResourceRetrieval, ICubeResourceRetrievalService advancedResourceRetrieval, CubeTableSource source) throws BICubeResourceAbsentException, BITablePathEmptyException, IOException {
        ICubeResourceLocation from = advancedResourceRetrieval.retrieveResource(new BITableKey(source));
        ICubeResourceLocation to = tempResourceRetrieval.retrieveResource(new BITableKey(source));
        if (new File(from.getAbsolutePath()).exists()) {
            boolean result = false;
            if (!new File(to.getAbsolutePath()).exists()) {
                result = new File(to.getAbsolutePath()).mkdir();
            }
            if (result) {
                BIFileUtils.copyFile(new File(from.getAbsolutePath() + "/version"), new File(to.getAbsolutePath() + "/version"));
                BIFileUtils.copyFile(new File(from.getAbsolutePath() + "/count"), new File(to.getAbsolutePath() + "/count"));
                BIFileUtils.copyFile(new File(from.getAbsolutePath() + "/field"), new File(to.getAbsolutePath() + "/field"));
                BIFileUtils.copyFile(new File(from.getAbsolutePath() + "/field.fl"), new File(to.getAbsolutePath() + "/field.fl"));
                BIFileUtils.copyFile(new File(from.getAbsolutePath() + "/field.fp"), new File(to.getAbsolutePath() + "/field.fp"));
                BIFileUtils.copyFile(new File(from.getAbsolutePath() + "/removeList"), new File(to.getAbsolutePath() + "/removeList"));
            }
        }
    }

    @Override
    public boolean preConditionsCheck() {
        return true;
    }
}
