package com.fr.bi.web.service.utils;

import com.finebi.cube.common.log.BILogger;
import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.conf.BICubeConfiguration;
import com.finebi.cube.conf.BICubeConfigureCenter;
import com.finebi.cube.conf.pack.data.IBusinessPackageGetterService;
import com.finebi.cube.conf.table.BusinessTable;
import com.fr.bi.base.BIUser;
import com.fr.bi.conf.report.BIWidget;
import com.fr.bi.conf.report.widget.field.BITargetAndDimension;
import com.fr.bi.conf.struct.BIPackageManager;
import com.fr.bi.etl.analysis.data.AnalysisCubeTableSource;
import com.fr.bi.etl.analysis.manager.BIAnalysisETLManagerCenter;
import com.fr.bi.stable.data.BITableID;
import com.fr.bi.stable.exception.BITableAbsentException;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by kary on 17-1-11.
 */
public class BIAnalysisTableHelper {
    private static BILogger LOGGER = BILoggerFactory.getLogger(BIAnalysisTableHelper.class);

    public static double getTableGeneratingProcessById(String tableId, long userId) {
        double percent;
        BusinessTable table = null;
        try {
            table = BIAnalysisETLManagerCenter.getBusiPackManager().getTable(tableId, userId);
        } catch (BITableAbsentException e) {
            LOGGER.error("Get Analysis ETL Table error.The table id is: " + tableId);
        }
        try {
            percent = getPercent((AnalysisCubeTableSource) table.getTableSource(), userId);
        } catch (Exception e) {
            LOGGER.error("Generate Analysis ETL Table Cube error. The table is: " + BIAnalysisETLManagerCenter.getAliasManagerProvider().getAliasName(tableId, userId));
            percent = -1;
        }
        return percent;
    }

    public static double getPercent(AnalysisCubeTableSource source, long userId) {
        double percent;
        Set<AnalysisCubeTableSource> sources = new HashSet<AnalysisCubeTableSource>();
        // 判断Version只需判断自身,如果是AnalysisETLTableSource，则需要同时check自己的parents即AnalysisBaseTableSource
        source.getSourceNeedCheckSource(sources);
        // BI-9341 螺旋分析删掉用掉的表更新没有失败  添加对应的判断，从而判断对应的业务包表是否存在
        if (!isBusinessTableExist(source, userId)) {
            return -1;
        }
        int generated = 0;
        for (AnalysisCubeTableSource s : sources) {
            if (BIAnalysisETLManagerCenter.getUserETLCubeManagerProvider().isError(s, new BIUser(userId))) {
                return -1;
            }
            //BILoggerFactory.getLogger(BIAnalysisETLGetGeneratingStatusAction.class).info(" check Version Of " + s.createUserTableSource(userId).fetchObjectCore().getIDValue());
            if (BIAnalysisETLManagerCenter.getUserETLCubeManagerProvider().checkVersion(s, new BIUser(userId))) {
                generated++;
            } else {
                BIAnalysisETLManagerCenter.getUserETLCubeManagerProvider().addTask(s, new BIUser(userId));
            }
        }
        percent = generated == sources.size() ? 1 : (0.1 + 0.9 * generated / sources.size());
        return percent;
    }

    public static boolean isError(AnalysisCubeTableSource source, long userId) {
        Set<AnalysisCubeTableSource> sources = new HashSet<AnalysisCubeTableSource>();
        // 判断Version只需判断自身,如果是AnalysisETLTableSource，则需要同时check自己的parents即AnalysisBaseTableSource
        source.getSourceNeedCheckSource(sources);
        for (AnalysisCubeTableSource s : sources) {
            if (BIAnalysisETLManagerCenter.getUserETLCubeManagerProvider().isError(s, new BIUser(userId))) {
                return true;
            }
        }
        return false;
    }


    public static boolean getTableHealthById(String tableId, long userId) {
        BusinessTable table = null;
        try {
            table = BIAnalysisETLManagerCenter.getBusiPackManager().getTable(tableId, userId);
            return BIAnalysisETLManagerCenter.getUserETLCubeManagerProvider().isAvailable((AnalysisCubeTableSource) table.getTableSource(), new BIUser(userId));
        } catch (Exception e) {
        }
        return false;
    }

    public static int getTableCubeCount(String tableId, long userId) {
        BusinessTable table = null;
        try {
            table = BIAnalysisETLManagerCenter.getBusiPackManager().getTable(tableId, userId);
            if (table == null) {
                return 0;
            }
            return BIAnalysisETLManagerCenter.getUserETLCubeManagerProvider().getThreadPoolCubeCount((AnalysisCubeTableSource) table.getTableSource(), new BIUser(userId));
        } catch (Exception e) {
        }
        return 0;
    }

    private static boolean isBusinessTableExist(AnalysisCubeTableSource source, long userId) {
        boolean exist = true;
        try {
            Set<IBusinessPackageGetterService> allPackages = BICubeConfigureCenter.getPackageManager().getAllPackages(userId);
            Iterator<BIWidget> sourceIterator = source.getWidgets().iterator();
            while (sourceIterator.hasNext()) {
                BIWidget widget = sourceIterator.next();
                BITargetAndDimension[] dimensions = widget.getDimensions();
                for (BITargetAndDimension dimension : dimensions) {
                    BITableID tableId = dimension.createColumnKey().getTableBelongTo().getID();
                    Iterator<IBusinessPackageGetterService> packageIterator = allPackages.iterator();
                    while (packageIterator.hasNext()) {
                        IBusinessPackageGetterService businessPackage = packageIterator.next();
                        BusinessTable specificTable = businessPackage.getSpecificTable(tableId);
                        if (specificTable == null) {
                            exist = false;
                        }
                    }
                }
            }
        } catch (Exception e) {
            exist = false;
            BILoggerFactory.getLogger().error(e.getMessage(), e);
        }
        return exist;
    }
}
