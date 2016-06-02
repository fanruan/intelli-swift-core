package com.fr.bi.cal.analyze.cal.sssecret;


import com.finebi.cube.api.ICubeColumnIndexReader;
import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.conf.table.BusinessTable;
import com.finebi.cube.relation.BITableSourceRelation;
import com.fr.bi.cal.analyze.cal.Executor.Executor;
import com.fr.bi.cal.analyze.cal.Executor.ILazyExecutorOperation;
import com.fr.bi.cal.analyze.cal.index.loader.IndexIterator;
import com.fr.bi.cal.analyze.cal.result.*;
import com.fr.bi.cal.analyze.cal.sssecret.sort.SortedNode;
import com.fr.bi.cal.analyze.cal.store.GroupKey;
import com.fr.bi.cal.analyze.cal.store.UserRightColumnKey;
import com.fr.bi.cal.analyze.cal.utils.CubeReadingUtils;
import com.fr.bi.cal.analyze.exception.TerminateExecutorException;
import com.fr.bi.conf.utils.BIModuleUtils;
import com.fr.bi.stable.connection.ConnectionRowGetter;
import com.fr.bi.stable.constant.BIBaseConstant;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.data.BITable;
import com.fr.bi.stable.gvi.GVIFactory;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.report.result.DimensionCalculator;
import com.fr.bi.stable.report.result.TargetCalculator;
import com.fr.bi.stable.structure.collection.map.CubeTreeMap;
import com.fr.bi.util.BIConfUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.TreeSet;


/**
 * TODO 需要改成可以半路计算，提升即时计算性能
 * 即可以用之前已经计算好的结果继续计算
 *
 * @author Daniel
 *         分页机制，使用另外一个线程来判断计算当前已经计算了多少结果了 并取数
 */
public class SingleDimensionGroup extends NoneDimensionGroup implements ILazyExecutorOperation<Entry, NewRootNodeChild>, ISingleDimensionGroup {

    protected TargetCalculator calculator;
    protected volatile NewDiskBaseRootNode root;

    protected DimensionCalculator column;
    protected transient DimensionCalculator[] pcolumns;

    protected transient int[] pckindex;

    protected transient Object[] data;

    protected transient int ckIndex;

    private transient boolean useRealData = true;

    private transient int demoGroupLimit = BIBaseConstant.PART_DATA_GROUP_LIMIT;

    /**
     * Group计算的构造函数
     *
     * @param column 维度
     * @param gvi    获取实际过滤条件的对象
     */
    protected SingleDimensionGroup(BusinessTable tableKey, DimensionCalculator[] pcolumns, int[] pckindex, DimensionCalculator column, Object[] data, int ckIndex, GroupValueIndex gvi, ICubeDataLoader loader, boolean useRealData, int demoGroupLimit) {
        this.loader = loader;
        this.tableKey = tableKey;
        this.pcolumns = pcolumns;
        this.column = column;
        this.pckindex = pckindex;
        this.ckIndex = ckIndex;
        this.data = data;
        this.useRealData = useRealData;
        this.initRoot(gvi);
        this.lazyExecutor = new Executor();
        this.demoGroupLimit = demoGroupLimit;
        if (isTurnOnWhenInit()) {
            turnOnExecutor();
        }
    }

    public static SingleDimensionGroup createDimensionGroup(final BusinessTable tableKey, final DimensionCalculator[] pcolumns, final int[] pckindex, final DimensionCalculator column, final Object[] data, final int ckIndex, final GroupValueIndex gvi, final ICubeDataLoader loader, boolean useRealData) {
        long rowCount = loader.getTableIndex(BIModuleUtils.getBusinessTableById(tableKey.getID()).getTableSource()).getRowCount();
        int groupLimit = 10;
        if (rowCount < BIBaseConstant.PART_DATA_COUNT_LIMIT) {
            useRealData = true;
        } else {
            long groupCount = loader.getTableIndex(column.getField().getTableBelongTo().getTableSource()).loadGroup(column.createKey(), new ArrayList<BITableSourceRelation>()).nonPrecisionSize();
            groupLimit = (int) (groupCount * BIBaseConstant.PART_DATA_COUNT_LIMIT / rowCount);
        }
        final boolean urd = useRealData;
        final int count = Math.min(Math.max(BIBaseConstant.PART_DATA_GROUP_LIMIT, groupLimit), BIBaseConstant.PART_DATA_GROUP_MAX_LIMIT);
        return new SingleDimensionGroup(tableKey, pcolumns, pckindex, column, data, ckIndex, gvi, loader, urd, count);
    }

    public static ISingleDimensionGroup createSortDimensionGroup(final BusinessTable tableKey, final DimensionCalculator[] pcolumns, final int[] pckindex, final DimensionCalculator column, final Object[] data, final int ckIndex, final GroupValueIndex gvi, final ICubeDataLoader loader, SortedNode sortedNode, boolean useRealData) {
        SingleDimensionGroup singleDimensionGroup = createDimensionGroup(tableKey, pcolumns, pckindex, column, data, ckIndex, gvi, loader, useRealData);
        return new SortedSingleDimensionGroup(singleDimensionGroup, sortedNode);
    }

    public static GroupKey createGroupKey(BusinessTable tableKey, DimensionCalculator column, GroupValueIndex gvi, boolean useRealData) {
        DimensionCalculator[] columnKey = new DimensionCalculator[2];
        columnKey[0] = new UserRightColumnKey(gvi, tableKey);
        columnKey[1] = column;
        return new GroupKey(tableKey, columnKey, useRealData);
    }

    protected boolean isTurnOnWhenInit() {
        return true;
    }

    @Override
    protected void initRoot(GroupValueIndex gvi) {
        root = new NewDiskBaseRootNode(column);
        root.setGroupValueIndex(gvi);

    }

    public void turnOnExecutor() {
        Iterator iterator = getIterator();
        if (iterator != null) {
            this.lazyExecutor.initial(this, iterator);
        } else {
            NewDiskBaseRootNodeChild child = new NewDiskBaseRootNodeChild(column, column.createEmptyValue());
            GroupValueIndex gvi = null;
            gvi = GVIFactory.createAllEmptyIndexGVI();
            child.setGroupValueIndex(gvi);
            addRootChild(child);
        }
    }

    protected Iterator getIterator() {

        return getNormalIterator();

    }

    private Iterator getSortIterator() {
        return new IndexIterator(getNormalIterator(), column, getRealTableKey4Calculate(), getLoader());
    }

    private Iterator getNormalIterator() {
        if (!useRealData) {
            return column.createValueMapIterator(getRealTableKey4Calculate(), getLoader(), useRealData, demoGroupLimit);
        }
        if (hasParentRelation() && column.getGroup().getType() == BIReportConstant.GROUP.NO_GROUP) {
            return getIterByChildValue();
        } else if (shouldGetIterByAllValue()) {
            return getIterByAllValue();
        } else {
            return column.createValueMapIterator(getRealTableKey4Calculate(), getLoader(), useRealData, demoGroupLimit);
        }
    }

    private boolean shouldGetIterByAllValue() {
        return (column.isSupperLargeGroup(getRealTableKey4Calculate(), getLoader()) && root.getGroupValueIndex().getRowsCountWithData() < 1024) && (!column.hasSelfGroup());
    }

    private boolean hasParentRelation() {
        for (int i = 0; i < ckIndex; i++) {
            if (hasParentRelation(i)) {
                return true;
            }
        }
        return false;
    }

    private Object[] getValuesByParents() {
        HashMap<Object, Integer> values = new HashMap<Object, Integer>();
        int useableParentCount = 0;
        for (int i = 0; i < ckIndex; i++) {
            if (hasParentRelation(i)) {
                Object[] res = CubeReadingUtils.getChildValuesAsParentOrSameTable(getRealTableKey4Calculate(), data[i], pcolumns[i], column, getLoader());
                for (int j = 0; j < res.length; j++) {
                    if (values.containsKey(res[j])) {
                        values.put(res[j], values.get(res[j]) + 1);
                    } else {
                        values.put(res[j], 1);
                    }
                }
                useableParentCount++;
            }
        }
        ArrayList list = new ArrayList();
        Iterator<Entry<Object, Integer>> it = values.entrySet().iterator();
        while (it.hasNext()) {
            Entry<Object, Integer> entry = it.next();
            if (entry.getValue() == useableParentCount) {
                list.add(entry.getKey());
            }
        }
        return list.toArray(new Object[list.size()]);
    }

    private boolean hasParentRelation(int i) {
        return pckindex[i] != -1 && (!column.hasSelfGroup()) && (!pcolumns[i].hasSelfGroup() && pcolumns[i].getBaseTableValueCount(data[i], getLoader()) < 256);
    }


    private BusinessTable getRealTableKey4Calculate() {
        return tableKey == BITable.BI_EMPTY_TABLE() ? column.getField().getTableBelongTo() : tableKey;
    }

    private Iterator getIterByChildValue() {
        ICubeColumnIndexReader valueMap = column.createValueMap(getRealTableKey4Calculate(), getLoader(), useRealData, demoGroupLimit);
        Object[] res = getValuesByParents();
        int tlen = res.length;
        Object[] showKeys = valueMap.createKey(tlen);
        for (int j = 0; j < tlen; j++) {
            showKeys[j] = res[j];
        }
        Object[] gvis = valueMap.getGroupIndex(showKeys);
        CubeTreeMap map = new CubeTreeMap(root.getComparator());
        for (int i = 0; i < tlen; i++) {
            map.put(showKeys[i], gvis[i]);
        }
        return map.entrySet().iterator();
    }

    private Iterator getIterByAllValue() {
        final TreeSet treeSet = new TreeSet(root.getComparator());
        final ConnectionRowGetter getter = new ConnectionRowGetter(BIConfUtils.createDirectTableConnection(column, getRealTableKey4Calculate(), getLoader()));
        Object[] res = getter.getConnectedRows(column, root.getGroupValueIndex(), column.createKey(), getLoader());
        for (int k = 0; k < res.length; k++) {
            if (res[k] != null) {
                treeSet.add(res[k]);
            }
        }
        Iterator iter = treeSet.iterator();
        int tlen = treeSet.size();
        ICubeColumnIndexReader valueMap = column.createValueMap(getRealTableKey4Calculate(), getLoader(), useRealData, demoGroupLimit);
        Object[] showKeys = valueMap.createKey(tlen);
        int j = 0;
        while (iter.hasNext()) {
            showKeys[j++] = iter.next();
        }
        Object[] gvis = valueMap.getGroupIndex(showKeys);
        CubeTreeMap map = new CubeTreeMap(root.getComparator());
        for (int i = 0; i < tlen; i++) {
            map.put(showKeys[i], gvis[i]);
        }
        return map.entrySet().iterator();
    }

    private NewRootNodeChild getCurrentNodeChild(Entry entry) {
        Object keyValue = entry.getKey();
        GroupValueIndex gvi = (GroupValueIndex) entry.getValue();
        NewDiskBaseRootNodeChild childNode = new NewDiskBaseRootNodeChild(column, keyValue);
        childNode.setGroupValueIndex(root.getGroupValueIndex().AND(gvi));
        return childNode;
    }

    private boolean isNodeChildVisible(GroupValueIndex parentIndex, NewRootNodeChild childNode) {
        return showNode(parentIndex, childNode);
    }

    protected void addNodeChild(NewRootNodeChild childNode) {
        root.addChild((NewDiskBaseRootNodeChild) childNode);
    }

    private boolean showNode(GroupValueIndex parentIndex, Node node) {
        return indexIsAllEmpty(parentIndex, node);
    }


    private boolean indexIsAllEmpty(GroupValueIndex parentIndex, Node node) {
        return (parentIndex == null || parentIndex.isAllEmpty()) && (node.getIndex4Cal() == null || node.getIndex4Cal().isAllEmpty());
    }

    private void addRootChild(NewDiskBaseRootNodeChild child) {
        root.addChild(child);
        notifyMainThread();
    }

    private void notifyMainThread() {
        synchronized (SingleDimensionGroup.this) {
            SingleDimensionGroup.this.notifyAll();
        }
    }

    @Override
    public int getChildIndexByValue(Object value) {
        return root.getIndexByValue(value);
    }

    @Override
    public NoneDimensionGroup getChildDimensionGroup(int row) {
        MemNode node = getMemNodeByWait(row);
        if (isNull(node)) {
            return NULL;
        }
        return createDimensionGroup(tableKey, node.getGroupValueIndex(), getLoader());
    }

    private boolean isNull(MemNode node) {
        return node == MemNode.NULL;
    }

    @Override
    public Object getChildData(int row) {
        MemNode node = getMemNodeByWait(row);
        checkNotNull(node);
        return node.getData();
    }

    private void checkNotNull(MemNode memNode) {
        if (memNode == MemNode.NULL) {
            throw GroupOutOfBoundsException.create(-1);
        }
    }

    @Override
    public String getChildShowName(int row) {
        MemNode node = getMemNodeByWait(row);
        checkNotNull(node);
        return node.getShowValue();
    }

    protected NewDiskBaseRootNodeChild getChildByWait(int row) {
        waitExecutor(row);
        NewDiskBaseRootNodeChild child = getChild(row);
        if (child == null) {
            if (row == 0) {
                child = createEmptyChild();
                addRootChild(child);
            } else {
                throw GroupOutOfBoundsException.create(row);
            }
        }
        return child;
    }

    protected MemNode getMemNodeByWait(int row) {
        waitExecutor(row);
        MemNode child = getMemChild(row);
        if (child == null) {
            if (row == 0) {
                addRootChild(createEmptyChild());
                child = getMemChild(0);
            } else {
                return MemNode.NULL;
            }
        }
        return child;
    }

    @Override
    public Node getChildNode(int row) {
        try {
            return getChildByWait(row);
        } catch (GroupOutOfBoundsException e) {
            return null;
        }
    }


    @Override
    public void initPrecondition() throws TerminateExecutorException {
    }

    @Override
    public NewRootNodeChild mainTaskConditions(Entry obj) {
        return getCurrentNodeChild(obj);
    }

    @Override
    public boolean jumpCurrentOne(NewRootNodeChild para) throws TerminateExecutorException {
        GroupValueIndex parentIndex = para.getGroupValueIndex();
        return isNodeChildVisible(parentIndex, para);
    }

    @Override
    public void mainTask(Entry obj, NewRootNodeChild nodeChild) throws TerminateExecutorException {
        addNodeChild(nodeChild);
    }

    @Override
    public void endCheck() throws TerminateExecutorException {
        if (root.getChildLength() == 0) {
            throw new TerminateExecutorException();
        }
    }

    @Override
    public void executorTerminated() {
        NewDiskBaseRootNodeChild child = createEmptyChild();
        addRootChild(child);
    }

    protected NewDiskBaseRootNodeChild createEmptyChild() {
        NewDiskBaseRootNodeChild child = new NewDiskBaseRootNodeChild(column, column.createEmptyValue());
        GroupValueIndex gvi = root.getGroupValueIndex();
        child.setGroupValueIndex(gvi);
        return child;
    }

    private NewDiskBaseRootNodeChild getChild(int row) {
        if (row < getCurrentTotalRow()) {
            return root.getChild(row);
        }
        return null;
    }

    private MemNode getMemChild(int row) {
        if (row < getCurrentTotalRow()) {
            return root.getMemChild(row);
        }
        return null;
    }

    /**
     * 计算根节点 第一个维度 用于分页
     *
     * @return 分页的node
     */
    @Override
    public Node getRoot() {
        return root;
    }


    /**
     * 释放资源，之前需要释放的，现在暂时没有什么需要释放的
     */
    @Override
    public void release() {
    }

    @Override
    public int getCurrentTotalRow() {
        return root.getChildLength();
    }

}