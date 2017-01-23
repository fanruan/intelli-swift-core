package com.fr.bi.web.service.utils;

import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.conf.table.BusinessTable;
import com.fr.bi.base.BIUser;
import com.fr.bi.etl.analysis.data.AnalysisCubeTableSource;
import com.fr.bi.etl.analysis.manager.BIAnalysisETLManagerCenter;
import com.fr.bi.stable.exception.BITableAbsentException;
import com.fr.bi.web.service.action.BIAnalysisETLGetGeneratingStatusAction;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by kary on 17-1-11.
 */
public class BIAnalysisTableHelper {

    public static double getTableGeneratingProcessById(String tableId, long userId){
        double percent;
        try {
            BusinessTable table = BIAnalysisETLManagerCenter.getBusiPackManager().getTable(tableId, userId);
            Set<AnalysisCubeTableSource> sources = new HashSet<AnalysisCubeTableSource>();
            // 判断Version只需判断自身,如果是AnalysisETLTableSource，则需要同时check自己的parents即AnalysisBaseTableSource
            ((AnalysisCubeTableSource) table.getTableSource()).getSourceNeedCheckSource(sources);
            int generated = 0;
            for (AnalysisCubeTableSource s : sources) {
                BILoggerFactory.getLogger(BIAnalysisETLGetGeneratingStatusAction.class).info(" check Version Of " + s.createUserTableSource(userId).fetchObjectCore().getIDValue());
                if (BIAnalysisETLManagerCenter.getUserETLCubeManagerProvider().checkVersion(s, new BIUser(userId))) {
                    generated++;
                } else {
                    BIAnalysisETLManagerCenter.getUserETLCubeManagerProvider().addTask(s, new BIUser(userId));
                }
            }
            percent = generated == sources.size() ? 1 : (0.1 + 0.9 * generated / sources.size());
        } catch (BITableAbsentException e) {
            percent = 0.1;
        }
        return percent;
    }


    public static boolean getTableHealthById(String tableId, long userId){
        BusinessTable table = null;
        try {
            table = BIAnalysisETLManagerCenter.getBusiPackManager().getTable(tableId, userId);
            return BIAnalysisETLManagerCenter.getUserETLCubeManagerProvider().isAvailable((AnalysisCubeTableSource) table.getTableSource(), new BIUser(userId));
        } catch (BITableAbsentException e) {
        }
        return  false;
    }

    public static int getTableCubeCount(String tableId, long userId){
        BusinessTable table = null;
        try {
            table = BIAnalysisETLManagerCenter.getBusiPackManager().getTable(tableId, userId);
            return BIAnalysisETLManagerCenter.getUserETLCubeManagerProvider().getThreadPoolCubeCount((AnalysisCubeTableSource) table.getTableSource(), new BIUser(userId));
        } catch (BITableAbsentException e) {
        }
        return  0;
    }
}
