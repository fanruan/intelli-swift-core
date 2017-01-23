package com.finebi.cube.gen.oper;

import com.finebi.cube.common.log.BILogExceptionInfo;
import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.conf.utils.BILogCacheTagHelper;
import com.finebi.cube.conf.utils.BILogHelper;
import com.finebi.cube.message.IMessage;
import com.finebi.cube.structure.Cube;
import com.fr.bi.stable.constant.BILogConstant;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.utils.program.BINonValueUtils;
import com.fr.bi.stable.utils.program.BIStringUtils;

import java.util.Set;
import java.util.Vector;

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
        try {
            BILoggerFactory.getLogger(BISourceDataNeverTransport.class).info(BIStringUtils.append("The table:", fetchTableInfo(), " start transport task",
                    BILogHelper.logCubeLogTableSourceInfo(tableSource.getSourceID())));
            BILoggerFactory.cacheLoggerInfo(BILogConstant.LOG_CACHE_TAG.CUBE_GENERATE_INFO, BILogCacheTagHelper.getCubeLogTransportTimeSubTag(tableSource.getSourceID(), BILogConstant.LOG_CACHE_TIME_TYPE.START), System.currentTimeMillis());
            copyFromOldCubes();
            recordTableInfo();
            tableEntityService.addVersion(version);
            tableEntityService.clear();
            BILoggerFactory.cacheLoggerInfo(BILogConstant.LOG_CACHE_TAG.CUBE_GENERATE_INFO, BILogCacheTagHelper.getCubeLogTransportTimeSubTag(tableSource.getSourceID(), BILogConstant.LOG_CACHE_TIME_TYPE.END), System.currentTimeMillis());
            return null;
        } catch (Exception e) {
            BILoggerFactory.cacheLoggerInfo(BILogConstant.LOG_CACHE_TAG.CUBE_GENERATE_INFO, BILogCacheTagHelper.getCubeLogTransportTimeSubTag(tableSource.getSourceID(), BILogConstant.LOG_CACHE_TIME_TYPE.END), System.currentTimeMillis());
            BILogExceptionInfo exceptionInfo = new BILogExceptionInfo(System.currentTimeMillis(), "Transport Exception", e.getMessage());
            Vector<BILogExceptionInfo> exceptionList = BILogHelper.getCubeLogExceptionList(tableSource.getSourceID());
            exceptionList.add(exceptionInfo);
            BILoggerFactory.cacheLoggerInfo(BILogConstant.LOG_CACHE_TAG.CUBE_GENERATE_EXCEPTION_INFO, BILogCacheTagHelper.getCubeLogExceptionSubTag(tableSource.getSourceID()), exceptionList);
            BILoggerFactory.getLogger(BISourceDataNeverTransport.class).error(e.getMessage(), e);
            throw BINonValueUtils.beyondControl(e.getMessage(), e);
        }
    }


}
