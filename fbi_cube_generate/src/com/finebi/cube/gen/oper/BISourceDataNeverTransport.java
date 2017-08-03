package com.finebi.cube.gen.oper;

import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.conf.utils.BICubeLogExceptionInfo;
import com.finebi.cube.conf.utils.BILogHelper;
import com.finebi.cube.message.IMessage;
import com.finebi.cube.structure.Cube;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.conf.provider.BILogManagerProvider;
import com.fr.bi.stable.constant.BILogConstant;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.utils.program.BINonValueUtils;
import com.fr.bi.stable.utils.program.BIStringUtils;
import com.fr.fs.control.UserControl;

import java.util.Set;

/**
 * Created by kary on 16/7/13.
 */
public class BISourceDataNeverTransport extends BISourceDataTransport {
    public BISourceDataNeverTransport(Cube cube, CubeTableSource tableSource, Set<CubeTableSource> allSources, Set<CubeTableSource> parentTableSource, long version) {
        super(cube, tableSource, allSources, parentTableSource, version);
    }

    /**
     * TODO 捕获异常，发送终止消息，停止其他监听对象的等待。
     *
     * @param lastReceiveMessage
     * @return
     */
    @Override
    public Object mainTask(IMessage lastReceiveMessage) {
        BILogManagerProvider biLogManager = BIConfigureManagerCenter.getLogManager();
        try {
            BILoggerFactory.getLogger(BISourceDataNeverTransport.class).info(BIStringUtils.append("The table:", fetchTableInfo(), " start transport task",
                    BILogHelper.logCubeLogTableSourceInfo(tableSource.getSourceID())));
            BILogHelper.cacheCubeLogTableNormalInfo(tableSource.getSourceID(), BILogConstant.LOG_CACHE_TIME_TYPE.TRANSPORT_EXECUTE_START, System.currentTimeMillis());
            copyFromOldCubes();
            recordTableInfo();
            tableEntityService.addVersion(version);
            tableEntityService.clear();
            try {
                biLogManager.infoTable(tableSource.getPersistentTable(), 0, UserControl.getInstance().getSuperManagerID());
            } catch (Exception e) {
                BILoggerFactory.getLogger().error(tableSource.getTableName() + e.getMessage(), e);
            }
            BILogHelper.cacheCubeLogTableNormalInfo(tableSource.getSourceID(), BILogConstant.LOG_CACHE_TIME_TYPE.TRANSPORT_EXECUTE_END, System.currentTimeMillis());
            return null;
        } catch (Throwable e) {
            BILogHelper.cacheCubeLogTableNormalInfo(tableSource.getSourceID(), BILogConstant.LOG_CACHE_TIME_TYPE.TRANSPORT_EXECUTE_END, System.currentTimeMillis());
            BICubeLogExceptionInfo exceptionInfo = new BICubeLogExceptionInfo(System.currentTimeMillis(), "Transport Exception", e.getMessage(), e, tableSource.getSourceID());
            BILogHelper.cacheCubeLogTableException(tableSource.getSourceID(), exceptionInfo);
            BILoggerFactory.getLogger(BISourceDataNeverTransport.class).error(e.getMessage(), e);
            throw BINonValueUtils.beyondControl(e.getMessage(), e);
        }
    }


}
