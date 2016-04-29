package com.fr.bi.cal.generate.relation.firstlinkindex;

import com.fr.bi.base.BIUser;
import com.fr.bi.cal.loader.CubeGeneratingTableIndexLoader;
import com.fr.bi.cal.stable.cube.file.TableCubeFile;
import com.fr.bi.cal.stable.index.utils.BIVersionUtils;
import com.fr.bi.conf.log.BIRecord;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.finebi.cube.api.ICubeDataLoader;
import com.fr.bi.stable.relation.BITableSourceRelation;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.bi.stable.utils.file.BIPathUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 小灰灰 on 14-2-12.
 */
public class LinkFirstIndexLoaderManager {
    /**
     *
     */
    private static final long serialVersionUID = 2277851691281613200L;
    private BITableSourceRelation relation;
    private TableCubeFile oldCube;
    private TableCubeFile currentCube;
    protected BIUser biUser;

    public LinkFirstIndexLoaderManager(BITableSourceRelation relation, long userId) {
        biUser = new BIUser(userId);
        this.relation = relation;
        String tableDirectory = relation.getPrimaryTable().fetchObjectCore().getIDValue();
        oldCube = new TableCubeFile(BIPathUtils.createTablePath(tableDirectory, userId));
        currentCube = new TableCubeFile(BIPathUtils.createTableTempPath(tableDirectory, userId));
    }

    /**
     * 创建loader
     *
     * @return 创建的loader
     */
    public LinkFirstIndexLoader createLoader() {
        try {
            ICubeDataLoader loader = getLoader();
            List<BITableSourceRelation> relations = new ArrayList<BITableSourceRelation>();
            relations.add(relation);
            int relation_version = BIVersionUtils.createRelationVersionValue(loader, relations);

            BIRecord log = BIConfigureManagerCenter.getLogManager().getBILog(biUser.getUserId()
            );
            if (!checkVersion(relation_version)) {
                LinkFirstIndexLoader lfi = new LinkFirstIndexLoader(relation, currentCube.getLinkIndexFile(relations), getLoader());
                lfi.setLog(log);
                return lfi;
            }
            return null;
        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
            return null;
        }
    }

    protected ICubeDataLoader getLoader() {
        return CubeGeneratingTableIndexLoader.getInstance(biUser.getUserId()
        );
    }

    private boolean checkVersion(int relation_version) {
        try {
            List<BITableSourceRelation> relations = new ArrayList<BITableSourceRelation>();
            relations.add(relation);
            return oldCube.checkRelationVersion(relations, relation_version);
        } catch (Exception e) {
            return false;
        }
    }
}