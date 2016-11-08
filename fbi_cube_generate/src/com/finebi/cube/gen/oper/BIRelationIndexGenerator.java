package com.finebi.cube.gen.oper;

import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.conf.BICubeConfigureCenter;
import com.finebi.cube.conf.pack.data.IBusinessPackageGetterService;
import com.finebi.cube.conf.table.BIBusinessTable;
import com.finebi.cube.impl.pubsub.BIProcessor;
import com.finebi.cube.message.IMessage;
import com.finebi.cube.relation.BITableSourceRelation;
import com.finebi.cube.structure.*;
import com.finebi.cube.structure.column.BIColumnKey;
import com.finebi.cube.structure.column.ICubeColumnEntityService;
import com.fr.bi.conf.log.BILogManager;
import com.fr.bi.conf.provider.BILogManagerProvider;
import com.fr.bi.conf.report.widget.RelationColumnKey;
import com.fr.bi.stable.constant.CubeConstant;
import com.fr.bi.stable.constant.DBConstant;
import com.fr.bi.stable.data.db.ICubeFieldSource;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.gvi.GVIFactory;
import com.fr.bi.stable.gvi.GroupValueIndex;
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
import com.fr.general.ComparatorUtils;
import com.fr.stable.bridge.StableFactory;
import com.fr.stable.collections.array.IntArray;
import com.google.common.base.Stopwatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    protected BICubeRelation relation;
    private static final Logger logger = LoggerFactory.getLogger(BIRelationIndexGenerator.class);

    public BIRelationIndexGenerator(Cube cube, BICubeRelation relation) {
        this.cube = cube;
        this.relation = relation;
    }

    @Override
    public Object mainTask(IMessage lastReceiveMessage) {

        BILogManager biLogManager = StableFactory.getMarkedObject(BILogManagerProvider.XML_TAG, BILogManager.class);
        biLogManager.logRelationStart(UserControl.getInstance().getSuperManagerID());
        Stopwatch stopwatch = Stopwatch.createStarted();
        RelationColumnKey relationColumnKeyInfo = null;
        logger.info(BIStringUtils.append("\n    ", logRelation(), "start building relation index"));
        try {
            relationColumnKeyInfo = getRelationColumnKeyInfo();
        } catch (Exception e) {
            BILoggerFactory.getLogger().error(e.getMessage(), e);
        }
        try {
            buildRelationIndex();
            biLogManager.infoRelation(relationColumnKeyInfo, stopwatch.elapsed(TimeUnit.SECONDS), UserControl.getInstance().getSuperManagerID());
            logger.info(BIStringUtils.append("\n    ", logRelation(), "finish building relation index ,elapse {} second"), stopwatch.elapsed(TimeUnit.SECONDS));

            return null;
        } catch (Exception e) {
            try {
                biLogManager.errorRelation(relationColumnKeyInfo, e.getMessage(), UserControl.getInstance().getSuperManagerID());
            } catch (Exception e1) {
                BILoggerFactory.getLogger().error(e1.getMessage(), e1);
            }
            BILoggerFactory.getLogger().error(e.getMessage(), e);
            throw BINonValueUtils.beyondControl(e.getMessage(), e);
        }
    }

    private String logRelation() {
        try {
            return BIStringUtils.append(
                    " Relation ID:" + BuildLogHelper.calculateRelationID(relation),
                    " Primary table:", relation.getPrimaryTable().getSourceID(),
                    " Primary field:", relation.getPrimaryField().getColumnName(),
                    " Foreign table:", relation.getForeignTable().getSourceID(),
                    " Foreign field:", relation.getForeignField().getColumnName()
            );
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return "error relation";
    }


    public RelationColumnKey getRelationColumnKeyInfo() {
        BITableSourceRelation tableRelation = getTableRelation(this.relation);
        ICubeFieldSource field = tableRelation.getPrimaryField();
        List<BITableSourceRelation> relations = new ArrayList<BITableSourceRelation>();
        relations.add(tableRelation);
        return new RelationColumnKey(field, relations);
    }

    private BITableSourceRelation getTableRelation(BICubeRelation relation) {
        ICubeFieldSource primaryField = null;
        ICubeFieldSource foreignField = null;
        CubeTableSource primaryTable = null;
        CubeTableSource foreignTable = null;
        Set<CubeTableSource> allTableSource = getAllTableSource();
        for (CubeTableSource cubeTableSource : allTableSource) {
            if (ComparatorUtils.equals(relation.getPrimaryTable().getSourceID(), cubeTableSource.getSourceID())) {
                primaryTable = cubeTableSource;
                Set<CubeTableSource> primarySources = new HashSet<CubeTableSource>();
                primarySources.add(cubeTableSource);
                for (ICubeFieldSource iCubeFieldSource : primaryTable.getFacetFields(primarySources)) {
                    if (ComparatorUtils.equals(iCubeFieldSource.getFieldName(), relation.getPrimaryField().getColumnName())) {
                        primaryField = iCubeFieldSource;
                    }
                }
                break;
            }
        }
        for (CubeTableSource cubeTableSource : allTableSource) {
            if (ComparatorUtils.equals(relation.getForeignTable().getSourceID(), cubeTableSource.getSourceID())) {
                foreignTable = cubeTableSource;
                Set<CubeTableSource> foreignSource = new HashSet<CubeTableSource>();
                foreignSource.add(cubeTableSource);
                for (ICubeFieldSource iCubeFieldSource : foreignTable.getFacetFields(foreignSource)) {
                    if (ComparatorUtils.equals(iCubeFieldSource.getFieldName(), relation.getForeignField().getColumnName())) {
                        foreignField = iCubeFieldSource;
                    }
                }
                break;
            }
        }
        BITableSourceRelation biTableSourceRelation = new BITableSourceRelation(primaryField, foreignField, primaryTable, foreignTable);
        return biTableSourceRelation;
    }

    private Set<CubeTableSource> getAllTableSource() {
        Set<CubeTableSource> cubeTableSourceSet = new HashSet<CubeTableSource>();
        Set<IBusinessPackageGetterService> packs = BICubeConfigureCenter.getPackageManager().getAllPackages(UserControl.getInstance().getSuperManagerID());
        for (IBusinessPackageGetterService pack : packs) {
            Iterator<BIBusinessTable> tIt = pack.getBusinessTables().iterator();
            while (tIt.hasNext()) {
                BIBusinessTable table = tIt.next();
                cubeTableSourceSet.add(table.getTableSource());
            }
        }
        return cubeTableSourceSet;
    }


    @Override
    public void release() {
        cube.clear();
    }

    private void buildRelationIndex() {
        CubeTableEntityGetterService primaryTable = null;
        CubeTableEntityGetterService foreignTable = null;
        ICubeColumnEntityService primaryColumn = null;
        ICubeColumnEntityService foreignColumn = null;
        BICubeRelationEntity tableRelation = null;
        try {
            BIColumnKey primaryKey = relation.getPrimaryField();
            BIColumnKey foreignKey = relation.getForeignField();
            ITableKey primaryTableKey = relation.getPrimaryTable();
            ITableKey foreignTableKey = relation.getForeignTable();
            primaryTable = cube.getCubeTable(primaryTableKey);
            foreignTable = cube.getCubeTable(foreignTableKey);
            /**
             * 关联的主字段对象
             */
            primaryColumn = (ICubeColumnEntityService) cube.getCubeColumn(primaryTableKey, primaryKey);
            /**
             * 关联的子字段对象
             */
            foreignColumn = (ICubeColumnEntityService) cube.getCubeColumn(foreignTableKey, foreignKey);
            /**
             * 表间关联对象
             */
            tableRelation = (BICubeRelationEntity) cube.getCubeRelation(primaryTableKey, relation);
            final BICubeRelationEntity finalTableRelation = tableRelation;
            GroupValueIndex nullIndex = GVIFactory.createAllEmptyIndexGVI();
            /**
             * 主表的行数
             */
            int primaryGroupSize = primaryColumn.sizeOfGroup();
            int foreignGroupSize = foreignColumn.sizeOfGroup();
            if (foreignGroupSize == 0) {
                return;
            }
            int foreignIndex = 0;
            Object foreignColumnValue = foreignColumn.getGroupObjectValue(foreignIndex);
            GroupValueIndex foreignGroupValueIndex = foreignColumn.getBitmapIndex(foreignIndex);
            Comparator c = primaryColumn.getGroupComparator();
            if (isNumberColumn(primaryColumn.getClassType()) && isNumberColumn(foreignColumn.getClassType())) {
                c = generateComparatorByType(primaryColumn.getClassType(), foreignColumn.getClassType());
            }
            int[] reverse = new int[foreignTable.getRowCount()];
            Arrays.fill(reverse, NIOConstant.INTEGER.NULL_VALUE);
            Stopwatch stopwatch = Stopwatch.createStarted();
            for (int index = 0; index < primaryGroupSize; index++) {
                /**
                 * 关联主字段的value值
                 */
                Object primaryColumnValue = primaryColumn.getGroupObjectValue(index);
                /**
                 * value值在主表的索引
                 */
                GroupValueIndex pGroupValueIndex = primaryColumn.getBitmapIndex(index);
                int result = c.compare(primaryColumnValue, foreignColumnValue);
                /**
                 * 小于0说明主表的id在子表找不到，大于0说明子表的id在主表找不到
                 */
                if (result < 0) {
                    pGroupValueIndex.Traversal(new SingleRowTraversalAction() {
                        @Override
                        public void actionPerformed(int row) {
                            finalTableRelation.addRelationIndex(row, GVIFactory.createAllEmptyIndexGVI());
                        }
                    });
                } else if (result == 0) {
                    matchRelation(tableRelation, foreignGroupValueIndex, reverse, pGroupValueIndex);
                    foreignIndex++;
                    foreignColumnValue = foreignColumn.getGroupObjectValue(foreignIndex);
                    foreignGroupValueIndex = foreignColumn.getBitmapIndex(foreignIndex);
                } else {
                    while (foreignIndex < foreignGroupSize && c.compare(primaryColumnValue, foreignColumnValue) > 0) {
                        nullIndex.or(foreignGroupValueIndex);
                        foreignIndex++;
                        if (foreignIndex == foreignGroupSize) {
                            foreignColumnValue = null;
                            foreignGroupValueIndex = null;
                        } else {
                            foreignColumnValue = foreignColumn.getGroupObjectValue(foreignIndex);
                            foreignGroupValueIndex = foreignColumn.getBitmapIndex(foreignIndex);
                        }
                    }
                    if (c.compare(primaryColumnValue, foreignColumnValue) == 0) {
                        matchRelation(tableRelation, foreignGroupValueIndex, reverse, pGroupValueIndex);
                        foreignIndex++;
                        foreignColumnValue = foreignColumn.getGroupObjectValue(foreignIndex);
                        foreignGroupValueIndex = foreignColumn.getBitmapIndex(foreignIndex);
                    } else {
                        pGroupValueIndex.Traversal(new SingleRowTraversalAction() {
                            @Override
                            public void actionPerformed(int row) {
                                finalTableRelation.addRelationIndex(row, GVIFactory.createAllEmptyIndexGVI());
                            }
                        });
                    }
                }

                if (CubeConstant.LOG_SEPERATOR_ROW != 0 && index % CubeConstant.LOG_SEPERATOR_ROW == 0) {
                    logger.info(BIStringUtils.append("\n    ", logRelation(), "read ", String.valueOf(index), " rows field value and time elapse:", String.valueOf(stopwatch.elapsed(TimeUnit.SECONDS)), " second"));
                }
            }
            while (foreignIndex < foreignGroupSize - 1) {
                nullIndex.or(foreignGroupValueIndex);
                foreignIndex++;
                foreignGroupValueIndex = foreignColumn.getBitmapIndex(foreignIndex);
            }
            nullIndex.or(foreignGroupValueIndex);
            buildReverseIndex(tableRelation, reverse);
            tableRelation.addRelationNULLIndex(0, nullIndex);
        } catch (Exception e) {
            try {
                logger.error("error relation :" + relation.toString() + " the exception is:" + "relation information used as listed:" + relation.getPrimaryTable().getSourceID() + "." + relation.getPrimaryField().getColumnName() + " to " + relation.getForeignTable().getSourceID() + "." + relation.getForeignField().getColumnName());
            } catch (Exception e1) {
                BILoggerFactory.getLogger().error(e1.getMessage(), e1);
            }
            throw BINonValueUtils.beyondControl(e.getMessage(), e);
        } finally {
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

        }

    }

    private void matchRelation(BICubeRelationEntity tableRelation, GroupValueIndex foreignGroupValueIndex, int[] reverse, GroupValueIndex pGroupValueIndex) {
        final IntArray array = new IntArray();
        pGroupValueIndex.Traversal(new TraversalAction() {
            @Override
            public void actionPerformed(int[] data) {
                array.addAll(data);
            }
        });
        for (int i = 0; i < array.size; i++) {
            tableRelation.addRelationIndex(array.get(i), foreignGroupValueIndex);

            try {
                initReverseIndex(reverse, array.get(i), foreignGroupValueIndex);
            } catch (ArrayIndexOutOfBoundsException e) {
                logger.error("GVI:" + foreignGroupValueIndex.toString());

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
                index[rowIndex] = row;
            }
        });
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

}
