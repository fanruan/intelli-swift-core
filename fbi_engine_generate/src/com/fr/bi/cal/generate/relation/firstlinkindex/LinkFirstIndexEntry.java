package com.fr.bi.cal.generate.relation.firstlinkindex;

import com.fr.bi.stable.utils.code.BILogger;
import com.fr.bi.base.BIUser;
import com.fr.bi.stable.index.CubeGenerator;
import com.fr.bi.stable.relation.BITableSourceRelation;
import com.fr.bi.stable.utils.CubeBaseUtils;
import com.fr.general.DateUtils;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by GUY on 2015/3/24.
 */
public class LinkFirstIndexEntry implements CubeGenerator {

    /**
     *
     */
    private static final long serialVersionUID = -2414931884899020295L;
    private Set<BITableSourceRelation> relations;
    protected BIUser biUser;
    public LinkFirstIndexEntry(Set<BITableSourceRelation> relations, long userId) {
        biUser = new BIUser(userId);
        this.relations = relations;
    }

    @Override
    public void generateCube() {
        BILogger.getLogger().info("Prepare First Relations");
        long t = System.currentTimeMillis();
        generateBasicRowIndex(relations);
        BILogger.getLogger().info("First Relations Completed, Cost:" + DateUtils.timeCostFrom(t));
    }


    private void generateBasicRowIndex(Set<BITableSourceRelation> relations) {

        Iterator<BITableSourceRelation> it = relations.iterator();
        HashSet<LinkFirstIndexLoader> calList = new HashSet<LinkFirstIndexLoader>();
        while (it.hasNext()) {
            BITableSourceRelation relation = it.next();
            LinkFirstIndexLoader loader = new LinkFirstIndexLoaderManager(relation, biUser.getUserId()
            ).createLoader();
            if (loader != null) {
                calList.add(loader);
            }
        }
        try {
            CubeBaseUtils.invokeCubeThreads(calList);
        } catch (InterruptedException e) {
            BILogger.getLogger().error(e.getMessage(), e);
        }
    }
}