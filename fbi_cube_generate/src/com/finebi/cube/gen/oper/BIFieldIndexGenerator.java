package com.finebi.cube.gen.oper;

import com.finebi.cube.common.log.BILogger;
import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.conf.utils.BILogHelper;
import com.finebi.cube.map.map2.ExternalIntArrayMapFactory;
import com.finebi.cube.map.map2.IntArrayListExternalMap;
import com.finebi.cube.message.IMessage;
import com.finebi.cube.structure.BITableKey;
import com.finebi.cube.structure.Cube;
import com.finebi.cube.structure.CubeTableEntityGetterService;
import com.finebi.cube.structure.column.BIColumnKey;
import com.finebi.cube.structure.column.ICubeColumnEntityService;
import com.finebi.cube.structure.column.date.BICubeDateSubColumn;
import com.fr.bi.manager.PerformancePlugManager;
import com.fr.bi.stable.constant.BILogConstant;
import com.fr.bi.stable.constant.CubeConstant;
import com.fr.bi.stable.data.db.ICubeFieldSource;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.gvi.GVIFactory;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.gvi.traversal.SingleRowTraversalAction;
import com.fr.bi.stable.io.newio.NIOConstant;
import com.fr.bi.stable.structure.array.IntArray;
import com.fr.bi.stable.structure.array.IntList;
import com.fr.bi.stable.structure.array.IntListFactory;
import com.fr.bi.stable.utils.algorithem.BIMD5Utils;
import com.fr.bi.stable.utils.program.BINonValueUtils;
import com.fr.bi.stable.utils.program.BIStringUtils;
import com.fr.bi.util.BIConfigurePathUtils;
import com.fr.fs.control.UserControl;
import com.google.common.base.Stopwatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

/**
 * This class created on 2016/4/7.
 *
 * @author Connery
 * @since 4.0
 */
public class BIFieldIndexGenerator<T> extends AbstractFieldIndexGenerator<T> {
    private static final BILogger LOGGER = BILoggerFactory.getLogger(BIFieldIndexGenerator.class);

    protected ICubeColumnEntityService<T> columnEntityService;
    protected long rowCount;
    private static final String CACHE = "caches";
    private static final String BASEPATH = File.separator + CACHE;

    public BIFieldIndexGenerator(Cube cube, CubeTableSource tableSource, ICubeFieldSource hostBICubeFieldSource, BIColumnKey targetColumnKey) {
        super(cube, tableSource, hostBICubeFieldSource, targetColumnKey);
    }

    private void initial() {
        try {
            CubeTableEntityGetterService tableEntityService = cube.getCubeTable(new BITableKey(tableSource.getSourceID()));
            columnEntityService = (ICubeColumnEntityService<T>) tableEntityService.getColumnDataGetter(targetColumnKey);
            rowCount = tableEntityService.getRowCount();
        } catch (Exception e) {
            throw BINonValueUtils.beyondControl(e.getMessage(), e);
        }
    }

    @Override
    public T mainTask(IMessage lastReceiveMessage) {
        LOGGER.info(BIStringUtils.append(logFileInfo(), " start building field index main task") +
                BILogHelper.logCubeLogTableSourceInfo(tableSource.getSourceID()));
        BILogHelper.cacheCubeLogFieldNormalInfo(tableSource.getSourceID(), hostBICubeFieldSource.getFieldName(), BILogConstant.LOG_CACHE_TIME_TYPE.FIELD_INDEX_EXECUTE_START, System.currentTimeMillis());
        Stopwatch stopwatch = Stopwatch.createStarted();
        biLogManager.logIndexStart(UserControl.getInstance().getSuperManagerID());
        try {
            initial();
            if (PerformancePlugManager.getInstance().isDiskSort()) {
                buildTableIndexExternal();
            } else {
                buildTableIndex();
            }
            LOGGER.info(BIStringUtils.append(logFileInfo(), " finish building field index main task,elapse:", String.valueOf(stopwatch.elapsed(TimeUnit.SECONDS)), " second"));
            BILogHelper.cacheCubeLogFieldNormalInfo(tableSource.getSourceID(), hostBICubeFieldSource.getFieldName(), BILogConstant.LOG_CACHE_TIME_TYPE.FIELD_INDEX_EXECUTE_END, System.currentTimeMillis());
            try {
                biLogManager.infoColumn(tableSource.getPersistentTable(), hostBICubeFieldSource.getFieldName(), stopwatch.elapsed(TimeUnit.SECONDS), Long.valueOf(UserControl.getInstance().getSuperManagerID()));
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
            return null;
        } catch (Throwable e) {
            handleBuildIndexFailed(e);
            throw BINonValueUtils.beyondControl(e.getMessage(), e);
        } finally {
            columnEntityService.forceReleaseWriter();
        }
    }

    @Override
    public void release() {
        columnEntityService.forceReleaseWriter();
        columnEntityService.clear();
    }

    public void buildTableIndex() {
        IntList nullRowNumbers = IntListFactory.createIntList();
        Map<T, IntList> group2rowNumber = createTreeMap(nullRowNumbers);
        Iterator<Map.Entry<T, IntList>> group2rowNumberIt = group2rowNumber.entrySet().iterator();
        IntArray positionOfGroup = doBuildTableIndex(group2rowNumberIt);
        group2rowNumber.clear();
        GroupValueIndex nullIndex = buildGroupValueIndex(nullRowNumbers);
        buildPositionOfGroup(positionOfGroup);
        columnEntityService.addNULLIndex(0, nullIndex);
//        group2rowNumber.clear();
    }

    public void buildTableIndexExternal() {
        IntList nullRowNumbers = IntListFactory.createIntList();
        IntArrayListExternalMap group2rowNumber = createExternalMap(nullRowNumbers);
        Iterator<Map.Entry<T, IntList>> group2rowNumberIt = group2rowNumber.getIterator();
        IntArray positionOfGroup = doBuildTableIndex(group2rowNumberIt);
        group2rowNumber.clear();
        GroupValueIndex nullIndex = buildGroupValueIndex(nullRowNumbers);
        buildPositionOfGroup(positionOfGroup);
        columnEntityService.addNULLIndex(0, nullIndex);
//        group2rowNumber.release();
    }

    private IntArray doBuildTableIndex(Iterator<Map.Entry<T, IntList>> group2rowNumberIt) {
        int groupPosition = 0;
        LOGGER.info(BIStringUtils.append(logFileInfo(), " start building field index"));
        IntArray positionOfGroup = IntListFactory.createIntArray((int) rowCount, NIOConstant.INTEGER.NULL_VALUE);
        Stopwatch stopwatch = Stopwatch.createStarted();
        GroupTraversalAction groupTraversalAction = new GroupTraversalAction(positionOfGroup);
        while (group2rowNumberIt.hasNext()) {
            Map.Entry<T, IntList> entry = group2rowNumberIt.next();
            T groupValue = entry.getKey();
            IntList groupRowNumbers = entry.getValue();
            columnEntityService.addGroupValue(groupPosition, groupValue);
            GroupValueIndex groupValueIndex = buildGroupValueIndex(groupRowNumbers);
            columnEntityService.addGroupIndex(groupPosition, groupValueIndex);
            initPositionOfGroup(groupPosition, groupValueIndex, groupTraversalAction);
            groupPosition++;
        }
        columnEntityService.recordSizeOfGroup(groupPosition);
        LOGGER.info(BIStringUtils.append(logFileInfo(), " finish building field index,elapse:", String.valueOf(stopwatch.elapsed(TimeUnit.SECONDS))));
        return positionOfGroup;
    }

    private void initPositionOfGroup(int groupPosition, GroupValueIndex groupValueIndex, GroupTraversalAction action) {
        action.groupPosition = groupPosition;
        groupValueIndex.Traversal(action);
    }

    private void buildPositionOfGroup(IntArray position) {
        for (int i = 0; i < position.size(); i++) {
            columnEntityService.addPositionOfGroup(i, position.get(i));
        }
        position.release();
    }

    private GroupValueIndex buildGroupValueIndex(IntList groupRowNumbers) {
        GroupValueIndex groupValueIndex = GVIFactory.createGroupValueIndexBySimpleIndex(groupRowNumbers);
        groupRowNumbers.clear();
        return groupValueIndex;
    }

    private void constructMap(Map<T, IntList> map, IntList nullRowNumbers) {
        LOGGER.info(BIStringUtils.append(logFileInfo(), " read detail data ,the row count:", String.valueOf(rowCount)));
        Stopwatch stopwatch = Stopwatch.createStarted();
        OriginValueGetter<T> getter;
        if (columnEntityService instanceof BICubeDateSubColumn) {
            getter = new OriginValueGetter<T>() {
                Calendar calendar = Calendar.getInstance();
                BICubeDateSubColumn cubeDateSubColumn = (BICubeDateSubColumn) columnEntityService;

                @Override
                public T getOriginalObjectValueByRow(int row) {
                    return (T) cubeDateSubColumn.getOriginalValueByRow(row, calendar);
                }
            };
        } else {
            getter = new OriginValueGetter<T>() {
                @Override
                public T getOriginalObjectValueByRow(int row) {
                    return columnEntityService.getOriginalObjectValueByRow(row);
                }
            };
        }
        for (int i = 0; i < rowCount; i++) {
            T originalValue = getter.getOriginalObjectValueByRow(i);
            if (originalValue != null) {
                IntList list = map.get(originalValue);
                if (list == null) {
                    list = IntListFactory.createIntList();
                    map.put(originalValue, list);
                }
                list.add(i);
            } else {
                nullRowNumbers.add(i);
            }
            if (CubeConstant.LOG_SEPERATOR_ROW != 0 && i % CubeConstant.LOG_SEPERATOR_ROW == 0) {
                LOGGER.info(BIStringUtils.append(logFileInfo(), " read ", String.valueOf(i), " rows field value and time elapse:", String.valueOf(stopwatch.elapsed(TimeUnit.SECONDS)), " second"));
            }
        }
        stopwatch.stop();
    }

    private Map<T, IntList> createTreeMap(IntList nullRowNumbers) {
        Map<T, IntList> group2rowNumber = new TreeMap<T, IntList>(columnEntityService.getGroupComparator());
        constructMap(group2rowNumber, nullRowNumbers);
        return group2rowNumber;
    }

    private IntArrayListExternalMap<T> createExternalMap(IntList nullRowNumbers) {
        String tableSourceID = tableSource.getSourceID();
        String dataFloder = BIConfigurePathUtils.createBasePath() + BASEPATH + File.separator + tableSourceID + File.separator + BIMD5Utils.getMD5String(new String[]{tableSourceID, targetColumnKey.getKey()});
        IntArrayListExternalMap<T> group2rowNumber = ExternalIntArrayMapFactory.getIntListExternalMap(columnEntityService.getClassType(), columnEntityService.getGroupComparator(), dataFloder);
        constructMap(group2rowNumber, nullRowNumbers);
        return group2rowNumber;
    }

    private interface OriginValueGetter<T> {
        T getOriginalObjectValueByRow(int row);
    }

    private class GroupTraversalAction implements SingleRowTraversalAction {
        private int groupPosition;
        private com.fr.bi.stable.structure.array.IntArray position;

        public GroupTraversalAction(com.fr.bi.stable.structure.array.IntArray positionOfGroup) {
            this.position = positionOfGroup;
        }

        @Override
        public void actionPerformed(int row) {
            position.put(row, groupPosition);
        }
    }
}