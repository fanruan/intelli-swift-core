package com.fr.bi.cal.generate.relation;

import com.finebi.cube.conf.BICubeConfigureCenter;
import com.finebi.cube.conf.pack.data.IBusinessPackageGetterService;
import com.finebi.cube.conf.table.BusinessTable;
import com.finebi.cube.relation.BITableSourceRelation;
import com.fr.bi.base.BIUser;
import com.fr.bi.cal.generate.relation.basiclinkindex.LinkBasicIndexEntry;
import com.fr.bi.cal.generate.relation.firstlinkindex.LinkFirstIndexEntry;
import com.fr.bi.cal.generate.relation.inuserelation.LinkInUseIndexEntry;
import com.fr.bi.conf.utils.BIPackUtils;
import com.fr.bi.stable.index.CubeGenerator;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.general.DateUtils;

import java.util.Set;

/**
 * This class created on 2016/3/9.
 *
 * @author Connery
 * @since 4.0
 */
public class RelationGenerator implements CubeGenerator {
    public RelationGenerator(long userId) {
        biUser = new BIUser(userId);
    }

    protected BIUser biUser;

    @Override
    public void generateCube() {
        BILogger.getLogger().info("Relation Build Start");
        long start = System.currentTimeMillis();
        try {
            Set<BITableSourceRelation> relations = new RelationsGetter(biUser.getUserId()).getGenerateRelations();
            //直接表关联
            LinkFirstIndexEntry linkFirstIndexEntry = new LinkFirstIndexEntry(relations, biUser.getUserId());

            linkFirstIndexEntry.generateCube();
            //基础表关联
            LinkBasicIndexEntry linkBasicIndexEntry = new LinkBasicIndexEntry(biUser.getUserId());

            linkBasicIndexEntry.generateCube();

            LinkInUseIndexEntry linkInUseIndexEntry = new LinkInUseIndexEntry(biUser);
            //使用中关联
            linkInUseIndexEntry.generateCube();

            BILogger.getLogger().info("Relation Build Complete! Cost :" + DateUtils.timeCostFrom(start));
        } catch (Throwable e) {
            BILogger.getLogger().error(e.getMessage(), e);
        }
    }

    public Set<BusinessTable> fetchTableKeys() {
        Set<IBusinessPackageGetterService> packs = BICubeConfigureCenter.getCubeManager().getGeneratingObject(biUser.getUserId()
        ).getPacks();
        return BIPackUtils.getAllBusiTableKeys(packs);
    }
}
