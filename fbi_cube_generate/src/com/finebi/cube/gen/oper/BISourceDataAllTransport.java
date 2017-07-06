package com.finebi.cube.gen.oper;

import com.finebi.cube.adapter.BIUserCubeManager;
import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.conf.utils.BICubeLogExceptionInfo;
import com.finebi.cube.conf.utils.BILogHelper;
import com.finebi.cube.exception.BICubeColumnAbsentException;
import com.finebi.cube.message.IMessage;
import com.finebi.cube.structure.Cube;
import com.fr.bi.common.inter.Traversal;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.conf.provider.BILogManagerProvider;
import com.fr.bi.stable.constant.BIBaseConstant;
import com.fr.bi.stable.constant.BILogConstant;
import com.fr.bi.stable.data.db.BIDataValue;
import com.fr.bi.stable.data.db.ICubeFieldSource;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.utils.program.BINonValueUtils;
import com.fr.bi.stable.utils.program.BIStringUtils;
import com.fr.fs.control.UserControl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by kary on 16/7/13.
 */
public class BISourceDataAllTransport extends BISourceDataTransport {
    private static final Logger LOGGER = LoggerFactory.getLogger(BISourceDataAllTransport.class);

    public BISourceDataAllTransport(Cube cube, Cube integrityCube, CubeTableSource tableSource, Set<CubeTableSource> allSources, Set<CubeTableSource> parentTableSource, long version, Map<String, CubeTableSource> tablesNeed2GenerateMap) {
        super(cube, integrityCube, tableSource, allSources, parentTableSource, version, tablesNeed2GenerateMap);
    }

    @Override
    public Object mainTask(IMessage lastReceiveMessage) {
        BILogManagerProvider biLogManager = BIConfigureManagerCenter.getLogManager();
        LOGGER.info(BIStringUtils.append("The table:", fetchTableInfo(), " start transport task",
                BILogHelper.logCubeLogTableSourceInfo(tableSource.getSourceID())));
        tableEntityService.recordCurrentExecuteTime();
        BILogHelper.cacheCubeLogTableNormalInfo(tableSource.getSourceID(), BILogConstant.LOG_CACHE_TIME_TYPE.TRANSPORT_EXECUTE_START, System.currentTimeMillis());
        long t = System.currentTimeMillis();
        try {
            LOGGER.info(BIStringUtils.append("The table:", fetchTableInfo(), " record table structure info"));
            recordTableInfo();
            LOGGER.info(BIStringUtils.append("The table:", fetchTableInfo(), " process transportation operation"));
            buildTableBasicStructure();
            long count = transport();
            LOGGER.info(BIStringUtils.append("The table:", fetchTableInfo(), " finish transportation operation and record ",
                    String.valueOf(count), " records"));
            if (count >= 0) {
                /*清除remove的过滤条件*/
                TreeSet<Integer> sortRemovedList = new TreeSet<Integer>(BIBaseConstant.COMPARATOR.COMPARABLE.ASC);
                tableEntityService.recordRemovedLine(sortRemovedList);
                tableEntityService.recordRowCount(count);
            }
            tableEntityService.addVersion(version);
            tableEntityService.forceReleaseWriter();
            long tableCostTime = System.currentTimeMillis() - t;
            LOGGER.info("transport cost time: " + tableCostTime + "ms" + BILogHelper.logCubeLogTableSourceInfo(tableSource.getSourceID()));
            BILogHelper.cacheCubeLogTableNormalInfo(tableSource.getSourceID(), BILogConstant.LOG_CACHE_TIME_TYPE.TRANSPORT_EXECUTE_END, System.currentTimeMillis());
            try {
                biLogManager.infoTable(tableSource.getPersistentTable(), tableCostTime, UserControl.getInstance().getSuperManagerID());
            } catch (Exception e) {
                BILoggerFactory.getLogger().error(tableSource.getTableName() + e.getMessage(), e);
            }
            return null;
        } catch (Throwable e) {
            try {
                biLogManager.errorTable(tableSource.getPersistentTable(), e.getMessage(), UserControl.getInstance().getSuperManagerID());
            } catch (Exception e1) {
                BILoggerFactory.getLogger().error(e1.getMessage(), e1);
            }
            BILogHelper.cacheCubeLogTableNormalInfo(tableSource.getSourceID(), BILogConstant.LOG_CACHE_TIME_TYPE.TRANSPORT_EXECUTE_END, System.currentTimeMillis());
            BICubeLogExceptionInfo exceptionInfo = new BICubeLogExceptionInfo(System.currentTimeMillis(), "Transport Exception", e.getMessage(), e, tableSource.getSourceID());
            BILogHelper.cacheCubeLogTableException(tableSource.getSourceID(), exceptionInfo);
            BILoggerFactory.getLogger(BISourceDataAllTransport.class).error(e.getMessage(), e);
            throw BINonValueUtils.beyondControl(e.getMessage(), e);
        }
    }


    private long transport() {
        List<ICubeFieldSource> fieldList = tableEntityService.getFieldInfo();
        ICubeFieldSource[] cubeFieldSources = new ICubeFieldSource[fieldList.size()];
        for (int i = 0; i < fieldList.size(); i++) {
            fieldList.get(i).setTableBelongTo(tableSource);
            cubeFieldSources[i] = fieldList.get(i);
        }
        return this.tableSource.read(new Traversal<BIDataValue>() {
            @Override
            public void actionPerformed(BIDataValue v) {
                try {
                    tableEntityService.addDataValue(v);
                } catch (BICubeColumnAbsentException e) {
                    BILoggerFactory.getLogger().error(e.getMessage(), e);
                }
            }
        }, cubeFieldSources, new BIUserCubeManager(UserControl.getInstance().getSuperManagerID(), cubeChooser));
    }


}
