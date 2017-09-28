package com.finebi.cube.gen.oper;

import com.finebi.cube.common.log.BILogger;
import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.conf.utils.BICubeLogExceptionInfo;
import com.finebi.cube.conf.utils.BILogHelper;
import com.finebi.cube.impl.pubsub.BIProcessor;
import com.finebi.cube.impl.pubsub.BIProcessorThreadManager;
import com.finebi.cube.message.IMessage;
import com.finebi.cube.structure.Cube;
import com.finebi.cube.structure.column.BIColumnKey;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.conf.provider.BILogManagerProvider;
import com.fr.bi.stable.constant.BILogConstant;
import com.fr.bi.stable.data.db.ICubeFieldSource;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.utils.program.BIStringUtils;
import com.fr.fs.control.UserControl;
import com.fr.stable.StringUtils;

/**
 * Created by neil on 2017/8/4.
 */
public abstract class AbstractFieldIndexGenerator<T> extends BIProcessor<T> {
    private static final BILogger LOGGER = BILoggerFactory.getLogger(AbstractFieldIndexGenerator.class);

    protected CubeTableSource tableSource;
    protected ICubeFieldSource hostBICubeFieldSource;
    /**
     * 当前需要生产的ColumnKey，不能通过hostDBField转换。
     * 因为子类型是无法通过DBFiled转换得到的
     */
    protected BIColumnKey targetColumnKey;
    protected Cube cube;
    protected BILogManagerProvider biLogManager;

    public AbstractFieldIndexGenerator(Cube cube, CubeTableSource tableSource, ICubeFieldSource hostBICubeFieldSource, BIColumnKey targetColumnKey) {
        this.tableSource = tableSource;
        this.hostBICubeFieldSource = hostBICubeFieldSource;
        this.cube = cube;
        this.targetColumnKey = targetColumnKey;
        init();
    }

    @Override
    public void handleMessage(IMessage receiveMessage) {
        //Do nothing...
    }

    private void init() {
        biLogManager = BIConfigureManagerCenter.getLogManager();
        initThreadPool();
    }

    @Override
    protected void initThreadPool() {
        executorService = BIProcessorThreadManager.getInstance().getExecutorService();
    }

    protected String logFileInfo() {
        try {
            return BIStringUtils.append("The table:" + tableSource.getTableName(), " ", tableSource.getSourceID(), " the field:" + hostBICubeFieldSource.getFieldName());
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return StringUtils.EMPTY;
    }

    protected void handleBuildIndexFailed(Throwable e) {
        try {
            biLogManager.errorTable(tableSource.getPersistentTable(), e.getMessage(), UserControl.getInstance().getSuperManagerID());
        } catch (Exception e1) {
            LOGGER.error(e.getMessage(), e);
        }
        BILogHelper.cacheCubeLogFieldNormalInfo(tableSource.getSourceID(), hostBICubeFieldSource.getFieldName(), BILogConstant.LOG_CACHE_TIME_TYPE.FIELD_INDEX_EXECUTE_END, System.currentTimeMillis());
        BICubeLogExceptionInfo exceptionInfo = new BICubeLogExceptionInfo(System.currentTimeMillis(), "Field Index Build", e.getMessage(), e, tableSource.getSourceID(), hostBICubeFieldSource.getFieldName());
        BILogHelper.cacheCubeLogTableException(tableSource.getSourceID(), exceptionInfo);
        LOGGER.error(e.getMessage(), e);
    }
}
