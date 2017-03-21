package com.finebi.cube.gen.oper;

import com.finebi.cube.adapter.BICubeTableAdapter;
import com.finebi.cube.common.log.BILogExceptionInfo;
import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.conf.utils.BILogHelper;
import com.finebi.cube.exception.BICubeIndexException;
import com.finebi.cube.impl.pubsub.BIProcessor;
import com.finebi.cube.impl.pubsub.BIProcessorThreadManager;
import com.finebi.cube.message.IMessage;
import com.finebi.cube.relation.BITableSourceRelation;
import com.finebi.cube.structure.*;
import com.finebi.cube.structure.column.BIColumnKey;
import com.finebi.cube.structure.column.ICubeColumnEntityService;
import com.finebi.cube.utils.BIRelationHelper;
import com.fr.bi.conf.log.BILogManager;
import com.fr.bi.conf.provider.BILogManagerProvider;
import com.fr.bi.conf.report.widget.RelationColumnKey;
import com.fr.bi.stable.constant.BILogConstant;
import com.fr.bi.stable.constant.CubeConstant;
import com.fr.bi.stable.constant.DBConstant;
import com.fr.bi.stable.data.db.ICubeFieldSource;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.gvi.GVIFactory;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.gvi.traversal.BrokenTraversalAction;
import com.fr.bi.stable.gvi.traversal.SingleRowTraversalAction;
import com.fr.bi.stable.gvi.traversal.TraversalAction;
import com.fr.bi.stable.io.newio.NIOConstant;
import com.fr.bi.stable.operation.sort.comp.ASCComparator;
import com.fr.bi.stable.operation.sort.comp.CastDoubleASCComparator;
import com.fr.bi.stable.operation.sort.comp.CastFloatASCComparator;
import com.fr.bi.stable.operation.sort.comp.CastLongASCComparator;
import com.fr.bi.stable.utils.program.BINonValueUtils;
import com.fr.bi.stable.utils.program.BIStringUtils;
import com.fr.fs.control.UserControl;
import com.fr.stable.StringUtils;
import com.fr.stable.bridge.StableFactory;
import com.fr.stable.collections.array.IntArray;
import com.google.common.base.Stopwatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * This class created on 2016/4/7.
 *
 * @author Connery
 * @since 4.0
 */
public class BIRelationIndexGenerator extends BIProcessor {
    protected Cube cube;
    protected CubeChooser cubeChooser;
    protected BICubeRelation relation;
    private static final Logger logger = LoggerFactory.getLogger(BIRelationIndexGenerator.class);

    public BIRelationIndexGenerator(Cube cube, Cube integrityCube, BICubeRelation relation, Map<String, CubeTableSource> tablesNeed2GenerateMap) {
        this.cube = cube;
        this.relation = relation;
        this.cubeChooser = new CubeChooser(cube, integrityCube, tablesNeed2GenerateMap);
        initThreadPool();
    }

//    public void setCubeChooser(CubeChooser cubeChooser) {
//        this.cubeChooser=cubeChooser;
//    }

    @Override
    protected void initThreadPool() {
        executorService = BIProcessorThreadManager.getInstance().getExecutorService();
    }

    @Override
    public Object mainTask(IMessage lastReceiveMessage) {

        BILogManager biLogManager = StableFactory.getMarkedObject(BILogManagerProvider.XML_TAG, BILogManager.class);
        biLogManager.logRelationStart(UserControl.getInstance().getSuperManagerID());
        Stopwatch stopwatch = Stopwatch.createStarted();
        RelationColumnKey relationColumnKeyInfo = null;
        logger.info(BIStringUtils.append("\n start building relation index", logRelation()));
        String relationID = StringUtils.EMPTY;
        try {
            relationID = BuildLogHelper.calculateRelationID(relation);
        } catch (Exception e) {
            BILoggerFactory.getLogger(BIRelationIndexGenerator.class).error("Calculate Relation ID Error, the Relation info is: " + logRelation());
        }
        BILogHelper.cacheCubeLogRelationNormalInfo(relationID, BILogConstant.LOG_CACHE_TIME_TYPE.RELATION_INDEX_EXECUTE_START, System.currentTimeMillis());
        try {
            relationColumnKeyInfo = getRelationColumnKeyInfo();
        } catch (Exception e) {
            BILoggerFactory.getLogger(BIRelationIndexGenerator.class).error(e.getMessage(), e);
        }
        try {
            buildRelationIndex();
            biLogManager.infoRelation(relationColumnKeyInfo, stopwatch.elapsed(TimeUnit.SECONDS), UserControl.getInstance().getSuperManagerID());
            logger.info(BIStringUtils.append("\n finish building relation index ,elapse {} second", logRelation()), stopwatch.elapsed(TimeUnit.SECONDS));
            BILogHelper.cacheCubeLogRelationNormalInfo(relationID, BILogConstant.LOG_CACHE_TIME_TYPE.RELATION_INDEX_EXECUTE_END, System.currentTimeMillis());
            return null;
        } catch (Exception e) {
            try {
                biLogManager.errorRelation(relationColumnKeyInfo, e.getMessage(), UserControl.getInstance().getSuperManagerID());
            } catch (Exception e1) {
                BILoggerFactory.getLogger().error(e1.getMessage(), e1);
            }
            BILogExceptionInfo exceptionInfo = new BILogExceptionInfo(System.currentTimeMillis(), "BuildRelationIndex The error relation info is: " + logRelation(), e.getMessage(), e);
            BILogHelper.cacheCubeLogRelationException(relationID, exceptionInfo);
            BILoggerFactory.getLogger(BIRelationIndexGenerator.class).error("BuildRelationIndex The error relation info is: " + logRelation(), e);
            throw BINonValueUtils.beyondControl(e.getMessage(), e);
        }
    }

    private String logRelation() {
        try {
            return BIStringUtils.append(
                    "\n Relation ID:" + BuildLogHelper.calculateRelationID(relation),
                    "\n Primary table info:", BILogHelper.logCubeLogTableSourceInfo(relation.getPrimaryTable().getSourceID()),
                    "\n Primary field:", relation.getPrimaryField().getColumnName(),
                    "\n Foreign table info:", BILogHelper.logCubeLogTableSourceInfo(relation.getForeignTable().getSourceID()),
                    "\n Foreign field:", relation.getForeignField().getColumnName()
            );
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return "error relation";
    }


    public RelationColumnKey getRelationColumnKeyInfo() {
        BITableSourceRelation tableRelation = BIRelationHelper.getTableRelation(this.relation);
        ICubeFieldSource field = tableRelation.getPrimaryField();
        List<BITableSourceRelation> relations = new ArrayList<BITableSourceRelation>();
        relations.add(tableRelation);
        return new RelationColumnKey(field, relations);
    }

    @Override
    public void release() {
        cube.clear();
    }


    private GroupValueIndex getTableShowIndex(CubeTableEntityGetterService table) {
        if (null != table.getRemovedList() && table.getRemovedList().size != 0) {
            return GVIFactory.createGroupValueIndexBySimpleIndex(table.getRemovedList()).NOT(table.getRowCount());
        } else {
            return GVIFactory.createAllShowIndexGVI(table.getRowCount());
        }
    }

    private void buildRelationIndex() {
        CubeTableEntityGetterService primaryTable = null;
        CubeTableEntityGetterService foreignTable = null;
        ICubeColumnEntityService primaryColumn = null;
        ICubeColumnEntityService foreignColumn = null;
        BICubeRelationEntity tableRelation = null;
        BICubeTableAdapter pTableAdapter = null;
        try {
            BIColumnKey primaryKey = relation.getPrimaryField();
            BIColumnKey foreignKey = relation.getForeignField();
            ITableKey primaryTableKey = relation.getPrimaryTable();
            ITableKey foreignTableKey = relation.getForeignTable();
            primaryTable = cubeChooser.getCubeTable(primaryTableKey);
            foreignTable = cubeChooser.getCubeTable(foreignTableKey);
            //关联的主字段对象
            primaryColumn = (ICubeColumnEntityService) primaryTable.getColumnDataGetter(primaryKey);
            //关联的子字段对象
            foreignColumn = (ICubeColumnEntityService) foreignTable.getColumnDataGetter(foreignKey);
            //表间关联对象
            tableRelation = (BICubeRelationEntity) cube.getCubeRelation(primaryTableKey, relation);
            Comparator c = primaryColumn.getGroupComparator();
            if (isNumberColumn(primaryColumn.getClassType()) && isNumberColumn(foreignColumn.getClassType())) {
                c = generateComparatorByType(primaryColumn.getClassType(), foreignColumn.getClassType());
            }
            buildIndex(primaryTable, foreignTable, primaryColumn, foreignColumn, tableRelation, c);
        } catch (Exception e) {
            throw BINonValueUtils.beyondControl("The relation:" + logRelation() + ",error message" + e.getMessage(), e);
        } finally {
            release(primaryTable, foreignTable, primaryColumn, foreignColumn, tableRelation, pTableAdapter);
        }
    }

    private void release(CubeTableEntityGetterService primaryTable, CubeTableEntityGetterService foreignTable, ICubeColumnEntityService primaryColumn, ICubeColumnEntityService foreignColumn, BICubeRelationEntity tableRelation, BICubeTableAdapter pTableAdapter) {
        if (primaryTable != null) {
            primaryTable.forceReleaseWriter();
            primaryTable.clear();
        }
        if (foreignTable != null) {
            foreignTable.forceReleaseWriter();
            foreignTable.clear();
        }
        if (primaryColumn != null) {
            primaryColumn.forceReleaseWriter();
            primaryColumn.clear();
        }
        if (foreignColumn != null) {
            foreignColumn.forceReleaseWriter();
            foreignColumn.clear();
        }
        if (tableRelation != null) {
            tableRelation.forceReleaseWriter();
            tableRelation.clear();
        }
        if (cube != null) {
            cube.clear();
        }
        if (null != pTableAdapter) {
            pTableAdapter.clear();
        }
    }

    //pony要连续写，只能先全部存内存，索引太大了，所以先转化成压缩的byte[]存内存。
    //即使建了笛卡儿积的索引，因为重复的主键都指向同一个对象，内存也不会膨胀太多。
    protected void buildIndex(CubeTableEntityGetterService primaryTable, CubeTableEntityGetterService foreignTable, ICubeColumnEntityService primaryColumn, ICubeColumnEntityService foreignColumn, BICubeRelationEntity tableRelation, Comparator c) throws BICubeIndexException {
        //主表的分组数
        int primaryGroupSize = primaryColumn.sizeOfGroup();
        int foreignGroupSize = foreignColumn.sizeOfGroup();
        //关联外表字段的value值
        Object foreignColumnValue;
        //value值在外表的索引
        GroupValueIndex foreignGroupValueIndex;
        if (foreignGroupSize == 0) {
            foreignColumnValue = null;
            foreignGroupValueIndex = GVIFactory.createAllEmptyIndexGVI();
        } else {
            foreignColumnValue = getForeignGroupObjectValue(foreignColumn, 0);
            foreignGroupValueIndex = getForeignBitmapIndex(foreignColumn, 0);
        }
        int[] reverse = new int[foreignTable.getRowCount()];
        final byte[][] relationIndexBytes = new byte[primaryTable.getRowCount()][];
        Arrays.fill(reverse, NIOConstant.INTEGER.NULL_VALUE);
        GroupValueIndex allShowIndex = getTableShowIndex(primaryTable);
        GroupValueIndex nullIndex = buildIndex(primaryColumn, foreignColumn, c, primaryGroupSize, foreignGroupSize, foreignColumnValue, foreignGroupValueIndex, reverse, relationIndexBytes, allShowIndex);
        buildIndex(tableRelation, relationIndexBytes);
        buildReverseIndex(tableRelation, reverse);
        buildNullIndex(tableRelation, nullIndex);
        tableRelation.addVersion(System.currentTimeMillis());
    }

    protected void buildNullIndex(BICubeRelationEntity tableRelation, GroupValueIndex nullIndex) {
        tableRelation.addRelationNULLIndex(0, nullIndex);
    }

    protected GroupValueIndex getForeignBitmapIndex(ICubeColumnEntityService foreignColumn, int position) throws BICubeIndexException {
        return foreignColumn.getBitmapIndex(position);
    }

    protected Object getForeignGroupObjectValue(ICubeColumnEntityService foreignColumn, int position) {
        return foreignColumn.getGroupObjectValue(position);
    }

    private GroupValueIndex buildIndex(ICubeColumnEntityService primaryColumn, ICubeColumnEntityService foreignColumn, Comparator c,
                                       int primaryGroupSize, int foreignGroupSize, Object foreignColumnValue, GroupValueIndex foreignGroupValueIndex,
                                       int[] reverse, final byte[][] relationIndexBytes, GroupValueIndex allShowIndex) throws BICubeIndexException {
        Stopwatch stopwatch = Stopwatch.createStarted();
        GroupValueIndex nullIndex = GVIFactory.createAllEmptyIndexGVI();
        Object primaryColumnValue;
        GroupValueIndex pGroupValueIndex;
        int foreignIndex = 0;
        for (int index = 0; index < primaryGroupSize; index++) {
            primaryColumnValue = primaryColumn.getGroupObjectValue(index);
            pGroupValueIndex = primaryColumn.getBitmapIndex(index);
            pGroupValueIndex = pGroupValueIndex.AND(allShowIndex);
            int result = c.compare(primaryColumnValue, foreignColumnValue);
            /**
             * 小于0说明主表的id在子表找不到，大于0说明子表的id在主表找不到
             */
            if (result < 0) {
                notMatch(relationIndexBytes, pGroupValueIndex);
            } else if (result == 0) {
                matchRelation(relationIndexBytes, foreignGroupValueIndex, reverse, pGroupValueIndex);
                foreignIndex++;
                foreignColumnValue = getForeignColumnValue(foreignIndex, foreignGroupSize, foreignColumn);
                foreignGroupValueIndex = getForeignColumnIndex(foreignIndex, foreignGroupSize, foreignColumn);
            } else {
                while (foreignIndex < foreignGroupSize && c.compare(primaryColumnValue, foreignColumnValue) > 0) {
                    nullIndex.or(foreignGroupValueIndex);
                    foreignIndex++;
                    foreignColumnValue = getForeignColumnValue(foreignIndex, foreignGroupSize, foreignColumn);
                    foreignGroupValueIndex = getForeignColumnIndex(foreignIndex, foreignGroupSize, foreignColumn);
                }
                if (c.compare(primaryColumnValue, foreignColumnValue) == 0) {
                    matchRelation(relationIndexBytes, foreignGroupValueIndex, reverse, pGroupValueIndex);
                    foreignIndex++;
                    foreignColumnValue = getForeignColumnValue(foreignIndex, foreignGroupSize, foreignColumn);
                    foreignGroupValueIndex = getForeignColumnIndex(foreignIndex, foreignGroupSize, foreignColumn);
                } else {
                    notMatch(relationIndexBytes, pGroupValueIndex);
                }
            }
            if (CubeConstant.LOG_SEPERATOR_ROW != 0 && index % CubeConstant.LOG_SEPERATOR_ROW == 0) {
                logger.info(BIStringUtils.append(logRelation(), "read ", String.valueOf(index), " rows field value and time elapse:", String.valueOf(stopwatch.elapsed(TimeUnit.SECONDS)), " second"));
            }
        }
        while (foreignIndex < foreignGroupSize - 1) {
            nullIndex.or(foreignGroupValueIndex);
            foreignIndex++;
            foreignGroupValueIndex = foreignColumn.getBitmapIndex(foreignIndex);
        }
        return nullIndex.or(foreignGroupValueIndex);
    }

    private void notMatch(final byte[][] relationIndexBytes, GroupValueIndex pGroupValueIndex) {
        pGroupValueIndex.Traversal(new SingleRowTraversalAction() {
            @Override
            public void actionPerformed(int row) {
                relationIndexBytes[row] = GVIFactory.createAllEmptyIndexGVI().getBytes();
            }
        });
    }

    private Object getForeignColumnValue(int foreignIndex, int foreignGroupSize, ICubeColumnEntityService foreignColumn) {
        if (foreignIndex == foreignGroupSize) {
            return null;
        } else {
            return foreignColumn.getGroupObjectValue(foreignIndex);
        }
    }

    private GroupValueIndex getForeignColumnIndex(int foreignIndex, int foreignGroupSize, ICubeColumnEntityService foreignColumn) {
        if (foreignIndex == foreignGroupSize) {
            return GVIFactory.createAllEmptyIndexGVI();
        } else {
            try {
//                return foreignColumn.getBitmapIndex(foreignIndex);
                return getForeignBitmapIndex(foreignColumn, foreignIndex);
            } catch (BICubeIndexException e) {
                throw BINonValueUtils.beyondControl(e);
            }
        }
    }

    private void matchRelation(byte[][] relationIndexBytes, GroupValueIndex foreignGroupValueIndex, int[] reverse, GroupValueIndex pGroupValueIndex) {
        final IntArray array = new IntArray();
        pGroupValueIndex.Traversal(new TraversalAction() {
            @Override
            public void actionPerformed(int[] data) {
                array.addAll(data);
            }
        });
        byte[] bytes = foreignGroupValueIndex.getBytes();
        for (int i = 0; i < array.size; i++) {
            relationIndexBytes[array.get(i)] = bytes;

            try {
                initReverseIndex(reverse, array.get(i), foreignGroupValueIndex);
            } catch (ArrayIndexOutOfBoundsException e) {
                foreignGroupValueIndex.Traversal(new SingleRowTraversalAction() {
                    @Override
                    public void actionPerformed(int rowIndex) {
                        logger.error("GVI value:" + rowIndex);
                    }
                });
                logger.error(e.getMessage(), e);
            }
        }
    }

    private void initReverseIndex(final int[] index, final int row, GroupValueIndex gvi) {
        gvi.Traversal(new SingleRowTraversalAction() {
            @Override
            public void actionPerformed(int rowIndex) {
                try {
                    index[rowIndex] = row;
                } catch (ArrayIndexOutOfBoundsException e) {
                    logger.error("Array size:" + index.length + " row index:" + rowIndex);
                    throw e;
                }
            }
        });
    }

    private void buildIndex(BICubeRelationEntity tableRelation, byte[][] relationIndex) {
        //pony为了不破坏结构，先用ByteGroupValueIndex来传byte[]
        for (int i = 0; i < relationIndex.length; i++) {
            tableRelation.addRelationIndex(i, new ByteGroupValueIndex(relationIndex[i]));
        }
    }

    private void buildReverseIndex(ICubeRelationEntityService tableRelation, int[] index) {
        for (int i = 0; i < index.length; i++) {
            tableRelation.addReverseIndex(i, index[i]);
        }
    }

    private Comparator generateComparatorByType(int primaryColumnType, int foreignColumnType) {
        /**
         * 效率考虑,根据数值的类型来选择比较时转换的数值对象类型
         * 如果有Double型,优先Double类型比较,
         * 没有Double的基础上,如果有Float就选择转成Float比较
         * 依次类推
         */
        if (primaryColumnType == DBConstant.CLASS.DOUBLE || foreignColumnType == DBConstant.CLASS.DOUBLE) {
            return new CastDoubleASCComparator();
        }

        if (primaryColumnType == DBConstant.CLASS.FLOAT || foreignColumnType == DBConstant.CLASS.FLOAT) {
            return new CastFloatASCComparator();
        }

        if (primaryColumnType == DBConstant.CLASS.LONG || foreignColumnType == DBConstant.CLASS.LONG) {
            return new CastLongASCComparator();
        }

        return new ASCComparator();
    }

    private boolean isNumberColumn(int columnType) {
        return columnType == DBConstant.CLASS.LONG || columnType == DBConstant.CLASS.INTEGER || columnType == DBConstant.CLASS.DOUBLE;
    }

    private class ByteGroupValueIndex implements GroupValueIndex {
        private byte[] bytes;

        public ByteGroupValueIndex(byte[] bytes) {
            this.bytes = bytes;
        }

        @Override
        public byte[] getBytes() {
            return bytes;
        }

        @Override
        public GroupValueIndex AND(GroupValueIndex valueIndex) {
            throw new UnsupportedOperationException();
        }

        @Override
        public GroupValueIndex OR(GroupValueIndex valueIndex) {
            throw new UnsupportedOperationException();
        }

        @Override
        public GroupValueIndex ANDNOT(GroupValueIndex index) {
            throw new UnsupportedOperationException();
        }

        @Override
        public GroupValueIndex NOT(int rowCount) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void addValueByIndex(int index) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isAllEmpty() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void Traversal(TraversalAction action) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void Traversal(SingleRowTraversalAction action) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean BrokenableTraversal(BrokenTraversalAction action) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isOneAt(int rowIndex) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getRowsCountWithData() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean hasSameValue(GroupValueIndex parentIndex) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void write(DataOutput out) throws IOException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void readFields(DataInput in) throws IOException {
            throw new UnsupportedOperationException();
        }

        @Override
        public GroupValueIndex or(GroupValueIndex index) {
            throw new UnsupportedOperationException();
        }

        @Override
        public GroupValueIndex and(GroupValueIndex index) {
            throw new UnsupportedOperationException();
        }

        @Override
        public GroupValueIndex andnot(GroupValueIndex index) {
            throw new UnsupportedOperationException();
        }

        @Override
        public GroupValueIndex clone() {
            throw new UnsupportedOperationException();
        }
    }

}
