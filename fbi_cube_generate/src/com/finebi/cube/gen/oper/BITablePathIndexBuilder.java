package com.finebi.cube.gen.oper;

import com.finebi.cube.common.log.BILogExceptionInfo;
import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.conf.utils.BILogHelper;
import com.finebi.cube.exception.BICubeColumnAbsentException;
import com.finebi.cube.exception.BICubeIndexException;
import com.finebi.cube.exception.BICubeRelationAbsentException;
import com.finebi.cube.exception.IllegalRelationPathException;
import com.finebi.cube.impl.pubsub.BIProcessor;
import com.finebi.cube.impl.pubsub.BIProcessorThreadManager;
import com.finebi.cube.message.IMessage;
import com.finebi.cube.message.IMessageBody;
import com.finebi.cube.relation.BITableSourceRelation;
import com.finebi.cube.structure.BICubeRelation;
import com.finebi.cube.structure.BICubeTablePath;
import com.finebi.cube.structure.Cube;
import com.finebi.cube.structure.CubeRelationEntityGetterService;
import com.finebi.cube.structure.CubeTableEntityGetterService;
import com.finebi.cube.structure.ICubeIndexDataGetterService;
import com.finebi.cube.structure.ICubeRelationEntityService;
import com.finebi.cube.structure.ITableKey;
import com.finebi.cube.utils.BIRelationHelper;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.conf.provider.BILogManagerProvider;
import com.fr.bi.conf.report.widget.RelationColumnKey;
import com.fr.bi.stable.constant.BILogConstant;
import com.fr.bi.stable.constant.CubeConstant;
import com.fr.bi.stable.data.db.ICubeFieldSource;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.exception.BITablePathEmptyException;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.gvi.GroupValueIndexOrHelper;
import com.fr.bi.stable.gvi.RoaringGroupValueIndex;
import com.fr.bi.stable.gvi.traversal.SingleRowTraversalAction;
import com.fr.bi.stable.io.newio.NIOConstant;
import com.fr.bi.stable.utils.program.BINonValueUtils;
import com.fr.bi.stable.utils.program.BIStringUtils;
import com.fr.fs.control.UserControl;
import com.fr.general.ComparatorUtils;
import com.google.common.base.Stopwatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * This class created on 2016/4/8.
 *
 * @author Connery
 * @since 4.0
 */
public class BITablePathIndexBuilder extends BIProcessor {
    protected Cube cube;
    protected CubeChooser cubeChooser;
    protected BICubeTablePath relationPath;
    private boolean isAllDependRelationValid = true;
    private static final Logger LOGGER = LoggerFactory.getLogger(BITablePathIndexBuilder.class);

    public BITablePathIndexBuilder(Cube cube, Cube integrityCube, BICubeTablePath relationPath, Map<String, CubeTableSource> tablesNeed2GenerateMap) {
        this.cube = cube;
        this.cubeChooser = new CubeChooser(cube, integrityCube, tablesNeed2GenerateMap);
        this.relationPath = relationPath;
        initThreadPool();
    }

    public BITablePathIndexBuilder(Cube cube, Cube integrityCube, BICubeTablePath relationPath) {
        this.cube = cube;
        this.cubeChooser = new CubeChooser(cube, integrityCube);
        this.relationPath = relationPath;
        initThreadPool();
    }


    @Override
    protected void initThreadPool() {
        executorService = BIProcessorThreadManager.getInstance().getExecutorService();
    }

    @Override
    public Object mainTask(IMessage lastReceiveMessage) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        if (isAllDependRelationValid) {
            LOGGER.info(BIStringUtils.append("\n    ", logPath(), "start building path index main task"));
            BILogHelper.cacheCubeLogRelationNormalInfo(relationPath.getSourceID(), BILogConstant.LOG_CACHE_TIME_TYPE.RELATION_INDEX_EXECUTE_START, System.currentTimeMillis());
            buildRelationPathIndex();
            BILogHelper.cacheCubeLogRelationNormalInfo(relationPath.getSourceID(), BILogConstant.LOG_CACHE_TIME_TYPE.RELATION_INDEX_EXECUTE_END, System.currentTimeMillis());
            LOGGER.info(BIStringUtils.append("\n    ", logPath(), "finish building path index main task,{} second "), stopwatch.elapsed(TimeUnit.SECONDS));
        } else {
            BILogExceptionInfo exceptionInfo = new BILogExceptionInfo(System.currentTimeMillis(), BIStringUtils.append("TablePathIndex Error", getTablePathInfo()), "the path depend relations is not all valid, the path will not be generate", new Exception());
            BILogHelper.cacheCubeLogRelationException(relationPath.getSourceID(), exceptionInfo);
            LOGGER.warn(BIStringUtils.append("\n    ", logPath(), "the path depend relations is not all valid, the path will not be generate"));
        }
        return null;
    }

    @Override
    public void release() {
        cube.clear();
    }

    private String logPath() {
        return logPath(relationPath);
    }

    private String logPath(BICubeTablePath relationPath) {
        Integer count = 0;
        StringBuffer sb = new StringBuffer();
        try {
            sb.append("The path ID:").append(BuildLogHelper.calculatePathID(relationPath));
            for (BICubeRelation relation : relationPath.getAllRelations()) {
                sb.append(BIStringUtils.append(
                        "\nRelation " + count++,
                        "\n       Primary table:", relation.getPrimaryTable().getSourceID(),
                        "\n       Primary field:", relation.getPrimaryField().getColumnName(),
                        "\n       Foreign table:", relation.getForeignTable().getSourceID(),
                        "\n       Foreign field:", relation.getForeignField().getColumnName(),
                        "\n"
                ));

            }
            return sb.toString();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return "error path";
    }

    private void buildRelationPathIndex() {
        if (relationPath.size() >= 2) {
            BILogManagerProvider biLogManager = BIConfigureManagerCenter.getLogManager();
            biLogManager.logRelationStart(UserControl.getInstance().getSuperManagerID());
            long t = System.currentTimeMillis();
            CubeRelationEntityGetterService lastRelationEntity = null;
            CubeRelationEntityGetterService frontRelationPathReader = null;
            ICubeRelationEntityService targetPathEntity = null;
            RelationColumnKey columnKeyInfo = null;
            try {
                Stopwatch stopwatch = Stopwatch.createStarted();
                columnKeyInfo = getRelationColumnKeyInfo();
                int primaryRowCount = getPrimaryTableRowCount();
                lastRelationEntity = buildLastRelationReader();
                frontRelationPathReader = buildFrontRelationPathReader();
                targetPathEntity = buildTargetRelationPathWriter();
                GroupValueIndexOrHelper helper = new GroupValueIndexOrHelper();
                int[] reverse = new int[getJuniorTableRowCount()];
                Arrays.fill(reverse, NIOConstant.INTEGER.NULL_VALUE);
                for (int row = 0; row < primaryRowCount; row++) {
                    GroupValueIndex frontGroupValueIndex = frontRelationPathReader.getBitmapIndex(row);
                    GroupValueIndex resultGroupValueIndex = getTableLinkedOrGVI(frontGroupValueIndex, lastRelationEntity);
                    helper.add(resultGroupValueIndex);
                    targetPathEntity.addRelationIndex(row, resultGroupValueIndex);
                    initReverseIndex(reverse, row, resultGroupValueIndex);
                    if (CubeConstant.LOG_SEPERATOR_ROW != 0 && row % CubeConstant.LOG_SEPERATOR_ROW == 0) {
                        LOGGER.info(BIStringUtils.append("\n    ", logPath(), "read ", String.valueOf(row), " rows field value and time elapse:", String.valueOf(stopwatch.elapsed(TimeUnit.SECONDS)), " second"));
                    }
                }
                GroupValueIndex nullIndex = helper.compute().NOT(reverse.length);
                buildReverseIndex(targetPathEntity, reverse);
                targetPathEntity.addRelationNULLIndex(0, nullIndex);
                targetPathEntity.addVersion(System.currentTimeMillis());
                long costTime = System.currentTimeMillis() - t;
                try {
                    biLogManager.infoRelation(columnKeyInfo, costTime, UserControl.getInstance().getSuperManagerID());
                } catch (Exception e) {
                    BILoggerFactory.getLogger().error(e.getMessage(), e);
                }
            } catch (Throwable e) {
                try {
                    BILoggerFactory.getLogger(BITablePathIndexBuilder.class).error(BIStringUtils.append("TablePathIndex Error", getTablePathInfo()), e);
                    biLogManager.errorRelation(columnKeyInfo, e.getMessage(), UserControl.getInstance().getSuperManagerID());
                } catch (Exception e1) {
                    BILoggerFactory.getLogger(BITablePathIndexBuilder.class).error(e1.getMessage());
                }
                BILogExceptionInfo exceptionInfo = new BILogExceptionInfo(System.currentTimeMillis(), BIStringUtils.append("TablePathIndex Error", getTablePathInfo()), e.getMessage(), e);
                BILogHelper.cacheCubeLogRelationException(relationPath.getSourceID(), exceptionInfo);
                throw BINonValueUtils.beyondControl(e.getMessage(), e);
            } finally {

                if (lastRelationEntity != null) {
                    lastRelationEntity.clear();
                }
                if (frontRelationPathReader != null) {
                    frontRelationPathReader.clear();
                }
                if (targetPathEntity != null) {
                    targetPathEntity.forceReleaseWriter();
                    targetPathEntity.clear();
                }
            }
        }
    }

    private void initReverseIndex(final int[] index, final int row, GroupValueIndex gvi) {
        gvi.Traversal(new SingleRowTraversalAction() {
            @Override
            public void actionPerformed(int rowIndex) {
                index[rowIndex] = row;
            }
        });
    }

    private void buildReverseIndex(ICubeRelationEntityService tableRelation, int[] index) {
        for (int i = 0; i < index.length; i++) {
            tableRelation.addReverseIndex(i, index[i]);
        }
    }

    public static GroupValueIndex getTableLinkedOrGVI(GroupValueIndex currentIndex, final ICubeIndexDataGetterService reader) {
        if (currentIndex != null) {

            final GroupValueIndex result = new RoaringGroupValueIndex();
            currentIndex.Traversal(new SingleRowTraversalAction() {
                @Override
                public void actionPerformed(int rowIndices) {
                    try {
                        result.or(reader.getBitmapIndex(rowIndices));
                    } catch (BICubeIndexException e) {
                        BILoggerFactory.getLogger().error(e.getMessage(), e);
                    }
                }
            });
            return result;
        }
        return null;
    }

    protected int getPrimaryTableRowCount() throws BITablePathEmptyException {
        ITableKey primaryTableKey = relationPath.getFirstRelation().getPrimaryTable();
        CubeTableEntityGetterService primaryTable = cubeChooser.getCubeTable(primaryTableKey);
        int rowCount = primaryTable.getRowCount();
        primaryTable.clear();
        return rowCount;
    }

    protected int getJuniorTableRowCount() throws BITablePathEmptyException {
        ITableKey foreignTableKey = relationPath.getLastRelation().getForeignTable();
        CubeTableEntityGetterService foreignTable = cubeChooser.getCubeTable(foreignTableKey);
        int rowCount = foreignTable.getRowCount();
        foreignTable.clear();
        return rowCount;
    }

    private CubeRelationEntityGetterService buildLastRelationReader() throws
            BICubeRelationAbsentException, BICubeColumnAbsentException, IllegalRelationPathException, BITablePathEmptyException {
        BICubeRelation lastRelation = relationPath.getLastRelation();
        ITableKey lastPrimaryKey = lastRelation.getPrimaryTable();
        try {
            BICubeTablePath relationPath = new BICubeTablePath();
            relationPath.addRelationAtHead(lastRelation);
            LOGGER.info("get the path tail reader,the tail :" + logPath(relationPath));
        } catch (Exception e) {
            LOGGER.info(e.getMessage(), e);
        }
        return cubeChooser.getCubeRelation(lastPrimaryKey, lastRelation);
    }

    private ICubeRelationEntityService buildTargetRelationPathWriter() throws
            BICubeRelationAbsentException, BICubeColumnAbsentException, IllegalRelationPathException, BITablePathEmptyException {
        BICubeTablePath frontRelation = new BICubeTablePath();
        frontRelation.copyFrom(relationPath);
        ITableKey firstPrimaryKey = relationPath.getFirstRelation().getPrimaryTable();
        return cube.getCubeRelationWriter(firstPrimaryKey, frontRelation);
    }

    private CubeRelationEntityGetterService buildFrontRelationPathReader() throws
            BICubeRelationAbsentException, BICubeColumnAbsentException, IllegalRelationPathException, BITablePathEmptyException {
        ITableKey firstPrimaryKey = relationPath.getFirstRelation().getPrimaryTable();
        BICubeTablePath frontRelation = buildFrontRelation();
        try {
            LOGGER.info("get the path tail reader,the tail :" + logPath(frontRelation));
        } catch (Exception e) {
            LOGGER.info(e.getMessage(), e);
        }
        return cubeChooser.getCubeRelation(firstPrimaryKey, frontRelation);
    }

    private BICubeTablePath buildFrontRelation() throws BITablePathEmptyException {
        BICubeTablePath frontRelation = new BICubeTablePath();
        frontRelation.copyFrom(relationPath);
        frontRelation.removeLastRelation();
        return frontRelation;
    }

    public RelationColumnKey getRelationColumnKeyInfo() {
        List<BITableSourceRelation> relations = new ArrayList<BITableSourceRelation>();
        ICubeFieldSource field = null;
        for (BICubeRelation relation : this.relationPath.getAllRelations()) {
            BITableSourceRelation tableRelation = null;
            try {
                tableRelation = BIRelationHelper.getTableRelation(relation);
            } catch (Exception e) {
                BILoggerFactory.getLogger().error("get relationColumnKey failed! relation information used as listed:" + relation.getPrimaryTable().getSourceID() + "." + relation.getPrimaryField().getColumnName() + " to " + relation.getForeignTable().getSourceID() + "." + relation.getForeignField().getColumnName());
            }
            field = tableRelation.getPrimaryKey();
            relations.add(tableRelation);
        }
        return new RelationColumnKey(field, relations);
    }

    private String getTablePathInfo() {
        StringBuffer sb = new StringBuffer();
        sb.append("\n The path info is:");
        int i = 0;
        for (BICubeRelation relation : this.relationPath.getAllRelations()) {
            i++;
            sb.append("\n*******************path contain relation " + i + "*******************");
            sb.append(logRelation(relation));
        }
        return sb.toString();
    }

    private String logRelation(BICubeRelation relation) {
        try {
            return BIStringUtils.append(
                    "\n Relation ID:" + BuildLogHelper.calculateRelationID(relation),
                    "\n Primary table info:", BILogHelper.logCubeLogTableSourceInfo(relation.getPrimaryTable().getSourceID()),
                    "\n Primary field:", relation.getPrimaryField().getColumnName(),
                    "\n Foreign table info:", BILogHelper.logCubeLogTableSourceInfo(relation.getForeignTable().getSourceID()),
                    "\n Foreign field:", relation.getForeignField().getColumnName()
            );
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return "Get relation info error.";
        }
    }

    @Override
    public void handleMessage(IMessage receiveMessage) {
        if (receiveMessage.getBody() != null) {
            isAllDependRelationValid = isAllDependRelationValid && ComparatorUtils.equals(receiveMessage.getBody().getMessageBody(), CubeConstant.RELATION_VALIDATION.VALID);
        }
    }

    @Override
    protected IMessageBody getFinishMess() {
        return new IMessageBody() {
            @Override
            public String getMessageBody() {
                if (isAllDependRelationValid) {
                    return CubeConstant.RELATION_VALIDATION.VALID;
                } else {
                    return CubeConstant.RELATION_VALIDATION.INVALID;
                }
            }
        };
    }

//    public void setCubeChooser(CubeCalculatorChooser cubeChooser) {
//        this.cubeChooser = cubeChooser;
//    }
}
