package com.finebi.cube.gen.oper;

import com.finebi.cube.impl.pubsub.BIProcessor;
import com.finebi.cube.message.IMessage;
import com.finebi.cube.structure.BITableKey;
import com.finebi.cube.structure.Cube;
import com.finebi.cube.structure.CubeTableEntityGetterService;
import com.finebi.cube.structure.column.BIColumnKey;
import com.finebi.cube.structure.column.ICubeColumnEntityService;
import com.fr.bi.conf.log.BILogManager;
import com.fr.bi.conf.provider.BILogManagerProvider;
import com.fr.bi.stable.data.db.ICubeFieldSource;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.gvi.GVIFactory;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.gvi.traversal.SingleRowTraversalAction;
import com.fr.bi.stable.structure.collection.list.IntList;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.bi.stable.utils.program.BINonValueUtils;
import com.fr.fs.control.UserControl;
import com.fr.stable.bridge.StableFactory;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * This class created on 2016/4/7.
 *
 * @author Connery
 * @since 4.0
 */
public class BIFieldIndexGenerator<T> extends BIProcessor {
    protected CubeTableSource tableSource;
    protected ICubeFieldSource hostBICubeFieldSource;
    /**
     * 当前需要生产的ColumnKey，不能通过hostDBField转换。
     * 因为子类型是无法通过DBFiled转换得到的
     */
    protected BIColumnKey targetColumnKey;
    protected ICubeColumnEntityService<T> columnEntityService;
    protected Cube cube;
    protected long rowCount;

    public BIFieldIndexGenerator(Cube cube, CubeTableSource tableSource, ICubeFieldSource hostBICubeFieldSource, BIColumnKey targetColumnKey) {
        this.tableSource = tableSource;
        this.hostBICubeFieldSource = hostBICubeFieldSource;
        this.cube = cube;
        this.targetColumnKey = targetColumnKey;
    }

    private void initial() {
        try {
            CubeTableEntityGetterService tableEntityService = cube.getCubeTable(new BITableKey(tableSource.getSourceID()));
            columnEntityService = (ICubeColumnEntityService<T>) tableEntityService.getColumnDataGetter(targetColumnKey);
            rowCount = tableEntityService.getRowCount();
            tableEntityService.clear();
        } catch (Exception e) {
            throw BINonValueUtils.beyondControl(e.getMessage(), e);
        }
    }

    @Override
    public Object mainTask(IMessage lastReceiveMessage) {
        BILogManager biLogManager = StableFactory.getMarkedObject(BILogManagerProvider.XML_TAG, BILogManager.class);
        long t=System.currentTimeMillis();
        biLogManager.logIndexStart(UserControl.getInstance().getSuperManagerID());
        try {
            initial();
            buildTableIndex();
            long costTime=System.currentTimeMillis()-t;
            if (null!=tableSource.getPersistentTable()) {
                biLogManager.infoColumn(tableSource.getPersistentTable(),hostBICubeFieldSource.getFieldName(),costTime,Long.valueOf(UserControl.getInstance().getSuperManagerID()));
            }
        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
            if (null!=tableSource.getPersistentTable()) {
                biLogManager.errorTable(tableSource.getPersistentTable(), e.getMessage(), UserControl.getInstance().getSuperManagerID());
            }
        } finally {
            return null;
        }
    }

    @Override
    public void release() {
        columnEntityService.clear();
    }

    public void buildTableIndex() {
        IntList nullRowNumbers = new IntList();
        Map<T, IntList> group2rowNumber = createTreeMap(nullRowNumbers);
        Iterator<Map.Entry<T, IntList>> group2rowNumberIt = group2rowNumber.entrySet().iterator();
        int groupPosition = 0;
        columnEntityService.recordSizeOfGroup(group2rowNumber.size());
        while (group2rowNumberIt.hasNext()) {
            Map.Entry<T, IntList> entry = group2rowNumberIt.next();
            T groupValue = entry.getKey();
            IntList groupRowNumbers = entry.getValue();
            columnEntityService.addGroupValue(groupPosition, groupValue);
            GroupValueIndex groupValueIndex = buildGroupValueIndex(groupRowNumbers);
            columnEntityService.addGroupIndex(groupPosition, groupValueIndex);
            buildPositionOfGroup(groupPosition, groupValueIndex);
            groupPosition++;
        }
        GroupValueIndex nullIndex = buildGroupValueIndex(nullRowNumbers);
        buildPositionOfGroup(null, nullIndex);
        columnEntityService.addNULLIndex(0, nullIndex);
    }

    private void buildPositionOfGroup(final Integer groupPosition, GroupValueIndex groupValueIndex) {
        groupValueIndex.Traversal(new SingleRowTraversalAction() {
            @Override
            public void actionPerformed(int row) {
                columnEntityService.addPositionOfGroup(row, groupPosition);
            }
        });
    }

    private GroupValueIndex buildGroupValueIndex(IntList groupRowNumbers) {
        return GVIFactory.createGroupVauleIndexBySimpleIndex(groupRowNumbers);
    }

    private Map<T, IntList> createTreeMap(IntList nullRowNumbers) {
        Map<T, IntList> group2rowNumber = new TreeMap<T, IntList>(columnEntityService.getGroupComparator());
        for (int i = 0; i < rowCount; i++) {
            T originalValue = columnEntityService.getOriginalValueByRow(i);
            if (originalValue != null) {
                IntList list = group2rowNumber.get(originalValue);
                if (list == null) {
                    list = new IntList();
                    group2rowNumber.put(originalValue, list);
                }
                list.add(i);
            } else {
                nullRowNumbers.add(i);
            }
        }
        return group2rowNumber;
    }

}
