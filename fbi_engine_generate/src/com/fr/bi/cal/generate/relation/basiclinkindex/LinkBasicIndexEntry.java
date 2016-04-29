package com.fr.bi.cal.generate.relation.basiclinkindex;

import com.fr.base.FRContext;
import com.fr.bi.base.BIUser;
import com.fr.bi.conf.log.BIRecord;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.stable.data.Table;
import com.fr.bi.stable.index.CubeGenerator;
import com.fr.bi.stable.utils.CubeBaseUtils;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.general.DateUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by GUY on 2015/3/25.
 */
public class LinkBasicIndexEntry implements CubeGenerator {

    /**
     *
     */
    private static final long serialVersionUID = -8595323414267783583L;
    protected BIUser biUser;
    public LinkBasicIndexEntry(long userId) {
        biUser = new BIUser(userId);
    }


    @Override
    public void generateCube() {
        BILogger.getLogger().info("Prepare Basic Relations");
        long start = System.currentTimeMillis();
        List<LinkBasicIndexManagerAndLoader> threadList = new ArrayList<LinkBasicIndexManagerAndLoader>();
        Set<Table> tableSet = BIConfigureManagerCenter.getCubeManager().getGeneratingObject(biUser.getUserId()).getPrimaryKeyMap().keySet();
        BIRecord log = BIConfigureManagerCenter.getLogManager().getBILog(biUser.getUserId());
        for (Table key : tableSet) {
            try {
                LinkBasicIndexManagerAndLoader loader = new LinkBasicIndexManagerAndLoader(key, biUser.getUserId());
                loader.setLog(log);
                threadList.add(loader);
            } catch (Exception e) {
                BILogger.getLogger().error(e.getMessage(), e);
            }
        }
        try {
            CubeBaseUtils.invokeCubeThreads(threadList);
        } catch (InterruptedException e) {
            FRContext.getLogger().error(e.getMessage(), e);
        }
        BILogger.getLogger().info("Basic Relations Completed! Cost:" + DateUtils.timeCostFrom(start));
    }
}