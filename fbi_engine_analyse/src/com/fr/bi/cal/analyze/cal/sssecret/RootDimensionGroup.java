package com.fr.bi.cal.analyze.cal.sssecret;

import com.finebi.cube.api.BICubeManager;
import com.fr.bi.cal.analyze.cal.adapter.GroupCache;
import com.fr.bi.cal.analyze.cal.result.NodeExpander;
import com.fr.bi.cal.analyze.cal.store.GroupKey;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.conf.report.BIWidget;
import com.fr.bi.conf.report.widget.field.dimension.BIDimension;
import com.fr.bi.field.dimension.calculator.DateDimensionCalculator;
import com.fr.bi.field.dimension.calculator.StringDimensionCalculator;
import com.fr.bi.field.filtervalue.date.evenfilter.DateKeyTargetFilterValue;
import com.fr.bi.field.filtervalue.string.rangefilter.StringINFilterValue;
import com.fr.bi.manager.PerformancePlugManager;
import com.fr.bi.stable.constant.BIBaseConstant;
import com.fr.bi.stable.data.BITable;
import com.fr.bi.stable.data.Table;
import com.fr.bi.stable.data.key.date.BIDateValue;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.finebi.cube.relation.BITableSourceRelation;
import com.fr.bi.stable.report.result.DimensionCalculator;
import com.fr.bi.stable.report.result.TargetCalculator;
import com.fr.bi.stable.structure.collection.map.lru.FIFOHashMap;
import com.fr.bi.util.BIConfUtils;
import com.fr.cache.list.IntList;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 根，用于保存游标等其他信息，感觉可以优化成一个游标就完了
 *
 * @author Daniel
 */
public class RootDimensionGroup implements IRootDimensionGroup {

    protected NoneDimensionGroup root;
    private NoneDimensionGroup dereplicationRoot;
    protected DimensionCalculator[] cks;
    protected BISession session;
    protected int[][] parentColumnCksIndex;
    ISingleDimensionGroup singleDimensionGroupCache;
    FIFOHashMap<GroupKey, ISingleDimensionGroup> cacheMap = new FIFOHashMap<GroupKey, ISingleDimensionGroup>(64);
    private BIWidget widget;
    private BIDimension[] dimensions;
    private NodeExpander expander;
    private TreeIterator iter;
    private boolean useRealData;

    public RootDimensionGroup(NoneDimensionGroup root, DimensionCalculator[] cks, BIDimension[] dimensions, NodeExpander expander, BISession session, TargetCalculator calculator, BIWidget widget, boolean useRealData) {
        setRoot(root);
        this.cks = cks;
        this.dimensions = dimensions;
        this.expander = expander;
        this.session = session;
        this.iter = new TreeIterator(cks.length);
        this.widget = widget;
        this.useRealData = useRealData;
        initParentColumncks();
    }

    protected void setRoot(NoneDimensionGroup root) {
        this.root = root;
        if (root instanceof TreeNoneDimensionGroup) {
            this.dereplicationRoot = root;
        } else {
            if (!PerformancePlugManager.getInstance().useDereplication()) {
                this.dereplicationRoot = new DereplicationNoneDimenstionGroup(root);
            } else {
                this.dereplicationRoot = root;
            }
        }
    }

    public NoneDimensionGroup getRoot() {
        return dereplicationRoot;
    }

    public static int findPageIndexDichotomy(int[] shrinkPos, List<int[]> pageIndex, int start, int end) throws ArrayIndexOutOfBoundsException {
        //判断数组是否为空
        if (pageIndex == null) {
            throw new NullPointerException();
        }
        if (start < 0 || end < 0) {
            throw new ArrayIndexOutOfBoundsException();
        }
        if (end > start) {
            int middle = (start + end) / 2;
            int[] middleIndex = pageIndex.get(middle);
            //中间值小于当前值
            if (TreePageComparator.TC.compare(shrinkPos, middleIndex) >= 0) {
                //中间值小于当前值，同时下一个值大于等于当前值（end>=middle+1）,则middle为最小的大于值
                if (TreePageComparator.TC.compare(shrinkPos, pageIndex.get(middle + 1)) < 0) {
                    return middle + 1;
                } else {
                    //中间值小于当前值，但是下一个值仍然小于，则结果应该在（middle+1,end）中间
                    return findPageIndexDichotomy(shrinkPos, pageIndex, middle + 1, end);
                }
            } else {
                //中间值大于当前值
                return findPageIndexDichotomy(shrinkPos, pageIndex, start, middle);
            }
        } else if (start == end) {
            return start;
        } else {
            throw new ArrayIndexOutOfBoundsException();
        }
    }

    public boolean isUseRealData() {
        return useRealData;
    }

    public void setWidgetDateMap(int i, Object data) {
        if (widget == null) {
            return;
        }
        session.setWidgetDateMap(widget.getWidgetName(), dimensions[i].getValue(), dimensions[i].toString(data), data);
    }

    private void initParentColumncks() {
        parentColumnCksIndex = new int[this.cks.length][this.cks.length];
        for (int i = 0; i < parentColumnCksIndex.length; i++) {
            Arrays.fill(parentColumnCksIndex[i], -1);
        }
        for (int i = 1; i < this.cks.length; i++) {
            BITableSourceRelation[] relation = cks[i].getRelationList().toArray(new BITableSourceRelation[cks[i].getRelationList().size()]);
            for (int j = 0; j < i; j++) {
                BITableSourceRelation[] relationBase = cks[j].getRelationList().toArray(new BITableSourceRelation[cks[j].getRelationList().size()]);
                if (relationLarge(relationBase, relation) && cks[j] instanceof StringDimensionCalculator && (!cks[j].hasSelfGroup())) {
                    parentColumnCksIndex[i][j] = j;
                }
            }
        }
    }

    private Table getRealTableKey4Calculate(DimensionCalculator column, Table tableKey) {
        return tableKey == BITable.BI_EMPTY_TABLE() ? column.getField().getTableBelongTo() : tableKey;
    }

    /**
     * 获取A->B->指标  B->指标 A与B之间的关系
     *
     * @param relationBase B->指标
     * @param relation     A->指标
     * @return 是否是这样的关系
     */
    private boolean relationLarge(BITableSourceRelation[] relationBase,
                                  BITableSourceRelation[] relation) {
        if (relationBase == null || relation == null) {
            return false;
        }
        if (relationBase.length > relation.length) {
            return false;
        }
        int baseLen = relationBase.length;
        int len = relation.length;
        for (int i = 1; i < baseLen + 1; i++) {
            BITableSourceRelation table1 = relation[len - i];
            BITableSourceRelation table2 = relationBase[baseLen - i];
            if (!BIConfUtils.isSameRelation(table1, table2, session.getLoader().getUserId())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void setExpander(NodeExpander expander) {
        this.expander = expander;
    }

    /**
     * TODO 这里可以改成可以前后移动的游标提高性能先这样
     */
    @Override
    public TreeIterator moveToShrinkStartValue(Object[] value) {
        if (value != null) {
            int[] shrinkPos = getValueStartRow(value);
            iter.travelToPositionPage(shrinkPos);
        } else {
            iter.moveCurrentStart();
        }
        return iter;
    }

    @Override
    public TreeIterator moveLast() {
        iter.moveLast();
        return iter;
    }

    @Override
    public TreeIterator moveNext() {
        return iter;
    }

    @Override
    public TreeIterator moveToStart() {
        iter.reset();
        return iter;
    }

    @Override
    public void clearCache() {
        singleDimensionGroupCache = null;
    }

    private int[] getValueStartRow(Object[] value) {
        IntList result = new IntList();
        getValueStartRow(root, value, 0, result);
        for (int i = value.length; i < cks.length; i++) {
            result.add(-1);
        }
        return result.toArray();
    }

    private void getValueStartRow(NoneDimensionGroup ng, Object[] value, int deep, IntList list) {
        if (deep >= value.length) {
            return;
        }
        ISingleDimensionGroup sg = (root.getTableKey() == BITable.BI_EMPTY_TABLE() && deep > 0) ? ng.createNoneTargetSingleDimensionGroup(deep == 0 ? null : cks, parentColumnCksIndex[deep], cks[deep], value, deep, getCKGvigetter(value, deep), useRealData) : getSingleDimensionGroupCache(value, ng, deep);
        int i = sg.getChildIndexByValue(value[deep]);
        if (i == -1) {
            return;
        }
        list.add(i);
        try {
            NoneDimensionGroup currentNg = sg.getChildDimensionGroup(i);
            if (currentNg.node == null) {
                return;
            }
            getValueStartRow(currentNg, value, deep + 1, list);
        } catch (GroupOutOfBoundsException e) {
            return;
        }
    }

    private ISingleDimensionGroup getSingleDimensionGroupCache(Object[] value, NoneDimensionGroup ng, int deep) {
        if (deep == 0) {
            if (singleDimensionGroupCache != null) {
                return singleDimensionGroupCache;
            }
        }
        if (ng instanceof TreeNoneDimensionGroup) {
            return ((TreeNoneDimensionGroup) ng).getSingleDimensionGroup();
        }
        return getLightCacheFromSession(value, ng, deep);
    }

    /**
     * 获得下一个值，可能是下一个child，也可能下一个brother。
     * 如果是结点展开，同时深度没有到底。那么就是下一个child。（深度是指维度间的上下级关系）
     * 否则是当前结点下一个brother。
     *
     * @param gv
     * @param ng
     * @param index
     * @param deep
     * @param expander
     * @param list
     */
    private ReturnStatus getNext(GroupConnectionValue gv, NoneDimensionGroup ng, int[] index, int deep, NodeExpander expander, IntList list) {
        return getNext(gv, ng, index, deep, expander, list, null);
    }

    /**
     * 加了一位nextBrother。
     * 当从nextParent进入到GetNext里面时，直接进入nextBrother方法。
     *
     * @param gv
     * @param ng
     * @param index
     * @param deep
     * @param expander
     * @param list
     * @param nextBrother
     */
    private ReturnStatus getNext(GroupConnectionValue gv,
                                 NoneDimensionGroup ng,
                                 int[] index,
                                 int deep,
                                 NodeExpander expander,
                                 IntList list,
                                 Object nextBrother) {
        if (expander == null) {
            return groupEnd();
        }
        if (cks.length == 0) {
            return ReturnStatus.NULL;
        }
        ISingleDimensionGroup sg;
        sg = getSingleDimensionGroup(gv, ng, deep);
        String currentValueShowName = null;
        //如果不是-1说明是从child搞上来的有可能是没有的
        if (index[deep] != -1) {
            try {
                currentValueShowName = sg.getChildShowName(index[deep]);
            } catch (GroupOutOfBoundsException e) {
                if (ReturnStatus.GroupEnd == gotoNextParent(gv, index, deep, expander, list)) {
                    return groupEnd();
                }
            }
        }
        if (notNextChild(index, deep, expander, nextBrother, currentValueShowName)) {
            try {
                ReturnStatus returnStatus = gotoNextBrother(sg, gv, index, deep, expander, list);
                if (returnStatus == ReturnStatus.GroupOutOfBounds) {
                    if (ReturnStatus.GroupEnd == gotoNextParent(gv, index, deep, expander, list)) {
                        return groupEnd();
                    }
                } else if (returnStatus == ReturnStatus.GroupOutOfBounds) {
                    return catchGroupOutOfBounds(gv, index, deep, expander, list);
                } else if (returnStatus == ReturnStatus.GroupEnd) {
                    return groupEnd();
                }
            } catch (GroupOutOfBoundsException e) {
                return catchGroupOutOfBounds(gv, index, deep, expander, list);
            }
        } else {
            if (ReturnStatus.GroupEnd == gotoNextChildValue(sg, gv, index, deep, expander, list)) {
                return groupEnd();
            }
        }
        return ReturnStatus.Success;
    }

    private ReturnStatus catchGroupOutOfBounds(GroupConnectionValue gv, int[] index, int deep, NodeExpander expander, IntList list) {
        ReturnStatus gotoNextParentStatus = gotoNextParent(gv, index, deep, expander, list);
        if (ReturnStatus.GroupEnd == gotoNextParentStatus) {
            return groupEnd();
        }
        return ReturnStatus.Success;
    }

    private boolean notNextChild(int[] index, int deep, NodeExpander expander, Object nextBrother, String currentValueShowName) {
        return index.length == deep + 1 || (currentValueShowName != null && expander.getChildExpander(currentValueShowName) == null || nextBrother != null);
    }

    private ReturnStatus groupEnd() {
//		throw new GroupEndException();
        return ReturnStatus.GroupEnd;
    }

    protected ISingleDimensionGroup getSingleDimensionGroup(GroupConnectionValue gv, NoneDimensionGroup ng, int deep) {
        ISingleDimensionGroup sg = null;
        if (deep == 0) {
            sg = getFirstCacheDimensionGroup(gv, ng, deep);
        } else {
            sg = getLightCacheFromSession(gv, ng, deep);
        }
        return sg;
    }

    private ISingleDimensionGroup getFirstCacheDimensionGroup(GroupConnectionValue gv, NoneDimensionGroup ng, int deep) {
        if (singleDimensionGroupCache == null) {
            singleDimensionGroupCache = createSingleDimensionGroup(gv, ng, deep);
        }
        return singleDimensionGroupCache;
    }

    private ISingleDimensionGroup getCacheDimensionGroup(GroupConnectionValue gv, NoneDimensionGroup ng, int deep) {
        GroupKey groupKey = createGroupKey(ng, deep);
        ISingleDimensionGroup singleDimensionGroup = cacheMap.get(groupKey);
        if (singleDimensionGroup == null) {
            singleDimensionGroup = createSingleDimensionGroup(gv, ng, deep);
            cacheMap.put(groupKey, singleDimensionGroup);
        }
        return singleDimensionGroup;
    }

    private GroupKey createGroupKey(NoneDimensionGroup ng, int deep) {
        return SingleDimensionGroup.createGroupKey(ng.tableKey, cks[deep], ng.node.getGroupValueIndex(), useRealData);
    }

    private ISingleDimensionGroup getLightCacheFromSession(GroupConnectionValue gv, NoneDimensionGroup ng, int deep) {
        ISingleDimensionGroup sg;
        GroupKey groupKey = SingleDimensionGroup.createGroupKey(ng.tableKey, cks[deep], ng.tableKey == BITable.BI_EMPTY_TABLE() ? getCKGvigetter(gv, deep) : ng.node.getGroupValueIndex(), useRealData);
        ISingleDimensionGroup singleDimensionGroup = GroupCache.getLightDimensionGroupCache(session, groupKey);
        if (singleDimensionGroup == null) {
            singleDimensionGroup = createSingleDimensionGroup(gv, ng, deep);
            GroupCache.putLightSingleDimensionGroupCache(session, groupKey, singleDimensionGroup);
        }
        sg = singleDimensionGroup;
        return sg;
    }

    private ISingleDimensionGroup getLightCacheFromSession(Object[] data, NoneDimensionGroup ng, int deep) {
        ISingleDimensionGroup sg;
        GroupKey groupKey = SingleDimensionGroup.createGroupKey(ng.tableKey, cks[deep], ng.tableKey == BITable.BI_EMPTY_TABLE() ? getCKGvigetter(data, deep) : ng.node.getGroupValueIndex(), useRealData);
        ISingleDimensionGroup singleDimensionGroup = GroupCache.getLightDimensionGroupCache(session, groupKey);
        if (singleDimensionGroup == null) {
            singleDimensionGroup = createSingleDimensionGroup(data, ng, deep);
            GroupCache.putLightSingleDimensionGroupCache(session, groupKey, singleDimensionGroup);
        }
        sg = singleDimensionGroup;
        return sg;
    }

    protected ISingleDimensionGroup createSingleDimensionGroup(GroupConnectionValue gv, NoneDimensionGroup ng, int deep) {
        ISingleDimensionGroup sg;
        if ((root.getTableKey() == BITable.BI_EMPTY_TABLE() && deep > 0)) {
            sg = ng.createNoneTargetSingleDimensionGroup(deep == 0 ? null : cks, parentColumnCksIndex[deep], cks[deep], getParentsValuesByGv(gv, deep), deep, getCKGvigetter(gv, deep), useRealData);
        } else {
            sg = ng.createSingleDimensionGroup(deep == 0 ? null : cks, parentColumnCksIndex[deep], cks[deep], getParentsValuesByGv(gv, deep), deep, useRealData);
        }
        return sg;
    }

    protected ISingleDimensionGroup createSingleDimensionGroup(Object[] data, NoneDimensionGroup ng, int deep) {
        ISingleDimensionGroup sg;
        if ((root.getTableKey() == BITable.BI_EMPTY_TABLE() && deep > 0)) {
            sg = ng.createNoneTargetSingleDimensionGroup(deep == 0 ? null : cks, parentColumnCksIndex[deep], cks[deep], data, deep, getCKGvigetter(data, deep), useRealData);
        } else {
            sg = ng.createSingleDimensionGroup(deep == 0 ? null : cks, parentColumnCksIndex[deep], cks[deep], data, deep, useRealData);
        }
        return sg;
    }

    protected GroupValueIndex getCKGvigetter(GroupConnectionValue gv, int deep) {
        DimensionCalculator ck = cks[deep];
        GroupValueIndex gvi = iter.createFilterGvi(ck);
        int i = deep;
        GroupConnectionValue v = gv;
        while (i != 0) {
            i--;
            DimensionCalculator ckp = cks[i];
            Object value = v.getData();
            if (value == null || ckp.getRelationList() == null) {
                v = v.getParent();
                continue;
            }
            if (ckp instanceof DateDimensionCalculator) {
                Set<BIDateValue> currentSet = new HashSet<BIDateValue>();
                currentSet.add((BIDateValue) value);
                DateKeyTargetFilterValue dktf = new DateKeyTargetFilterValue(((DateDimensionCalculator) ckp).getGroupDate(), currentSet);
                GroupValueIndex pgvi = dktf.createFilterIndex(ckp, ck.getField().getTableBelongTo(), BICubeManager.getInstance().fetchCubeLoader(session.getUserId()), session.getUserId());
                gvi = gvi.AND(pgvi);
            } else if (ckp instanceof StringDimensionCalculator) {
                if (value == BIBaseConstant.EMPTY_NODE_DATA) {
                    v = v.getParent();
                    continue;
                }
                Set currentSet = ((StringDimensionCalculator) ckp).createFilterValueSet((String) value, session.getLoader());
                StringINFilterValue stf = new StringINFilterValue(currentSet);
                GroupValueIndex pgvi = stf.createFilterIndex(ckp, ck.getField().getTableBelongTo(), BICubeManager.getInstance().fetchCubeLoader(session.getUserId()), session.getUserId());
                gvi = gvi.AND(pgvi);
            }
            v = v.getParent();
        }
        return gvi;
    }

    protected Object[] getParentsValuesByGv(GroupConnectionValue groupConnectionValue, int deep) {
        ArrayList al = new ArrayList();
        GroupConnectionValue gv = groupConnectionValue;
        while (deep-- > 0) {
            al.add(gv.getData());
            gv = gv.getParent();
        }
        int len = al.size();
        Object[] obs = new Object[len];
        for (int i = 0; i < len; i++) {
            obs[i] = al.get(len - 1 - i);
        }
        return obs;
    }

    private GroupValueIndex getCKGvigetter(Object[] values, int deep) {
        DimensionCalculator ck = cks[deep];
        GroupValueIndex gvi = iter.createFilterGvi(ck);
        int i = deep;
        while (i != 0) {
            i--;
            DimensionCalculator ckp = cks[i];
            Object value = values[i];
            if (value == null || ckp.getRelationList() == null) {
                continue;
            }
            if (ckp instanceof DateDimensionCalculator) {
                Set<BIDateValue> currentSet = new HashSet<BIDateValue>();
                currentSet.add((BIDateValue) value);
                DateKeyTargetFilterValue dktf = new DateKeyTargetFilterValue(((DateDimensionCalculator) ckp).getGroupDate(), currentSet);

                GroupValueIndex pgvi = dktf.createFilterIndex(ckp, ck.getField().getTableBelongTo(), BICubeManager.getInstance().fetchCubeLoader(session.getUserId()), session.getUserId());
                gvi = gvi.AND(pgvi);
            } else if (ckp instanceof StringDimensionCalculator) {
                if (value == BIBaseConstant.EMPTY_NODE_DATA) {
                    continue;
                }
                Set currentSet = ((StringDimensionCalculator) ckp).createFilterValueSet((String) value, session.getLoader());
                StringINFilterValue stf = new StringINFilterValue(currentSet);
                GroupValueIndex pgvi = stf.createFilterIndex(ckp, ck.getField().getTableBelongTo(), BICubeManager.getInstance().fetchCubeLoader(session.getUserId()), session.getUserId());
                gvi = gvi.AND(pgvi);
            }
        }
        return gvi;
    }

    private ReturnStatus gotoNextChildValue(ISingleDimensionGroup sg, GroupConnectionValue gv, int[] index, int deep, NodeExpander expander, IntList list) {
        if (index[deep] == -1) {
            index[deep] += 1;
        }
        return findCurrentValue(sg, gv, index, deep, expander, list, index[deep]);
    }

    private ReturnStatus findCurrentValue(ISingleDimensionGroup sg, GroupConnectionValue gv, int[] index, int deep, NodeExpander expander, IntList list, int row) {
        NoneDimensionGroup nds;
        if (row == 0) {
            try {
                nds = sg.getChildDimensionGroup(row);
                if (NoneDimensionGroup.NULL == nds) {
                    if (deep == 0) {
                        return ReturnStatus.GroupEnd;
                    }
                    index[deep - 1] = index[deep - 1] + 1;
                    list.set(deep - 1, index[deep - 1] + 1);
                    return ReturnStatus.GroupOutOfBounds;
                }
            } catch (GroupOutOfBoundsException e) {
                if (deep == 0) {
                    return ReturnStatus.GroupEnd;
                }
                index[deep - 1] = index[deep - 1] + 1;
                throw e;
            }
        } else {
            nds = sg.getChildDimensionGroup(row);
            if (nds == NoneDimensionGroup.NULL) {
                return ReturnStatus.GroupOutOfBounds;
            }
        }
        GroupConnectionValue ngv = createGroupConnectionValue(sg, deep, row, nds);
        NodeExpander ex = expander.getChildExpander(sg.getChildShowName(row));
        list.add(row);
        ngv.setParent(gv);
        if (ex != null && deep + 1 < index.length) {
            ReturnStatus returnStatus = getNext(ngv, nds, index, deep + 1, ex, list);
            if (ReturnStatus.GroupEnd == returnStatus) {
                return groupEnd();
            }
        }
        return ReturnStatus.Success;
    }

    protected GroupConnectionValue createGroupConnectionValue(ISingleDimensionGroup sg, int deep, int row, NoneDimensionGroup nds) {
        GroupConnectionValue ngv = new GroupConnectionValue(cks[deep], sg.getChildData(row), sg.getRoot().getComparator(), nds);
        ngv.setGroupRow(row);
        if (sg instanceof ReverseSingleDimensionGroup) {
            ngv.setComparator(BIBaseConstant.COMPARATOR.COMPARABLE.ASC);
        }
        return ngv;
    }

    private ReturnStatus gotoNextBrother(ISingleDimensionGroup sg, GroupConnectionValue gv, int[] index, int deep, NodeExpander expander, IntList list) {
        return findCurrentValue(sg, gv, index, deep, expander, list, index[deep] + 1);
    }

    private ReturnStatus gotoNextParent(GroupConnectionValue gv, int[] index, int deep, NodeExpander expander, IntList list) {
        if (deep == 0) {
            return groupEnd();
        }
        int[] newIndex = index.clone();
        //Connery:这里移动下一位置，但是在getNext方法里面，调用了nextBrother，又在parent的向下移动了一个位置。
        newIndex[deep - 1] = list.get(deep - 1);
        Arrays.fill(newIndex, deep, newIndex.length, -1);
        list.remove(list.size() - 1);
        if (ReturnStatus.GroupEnd == getNext(gv.getParent(), gv.getParent().getCurrentValue(), newIndex, deep - 1, expander.getParent(), list, new Object())) {
            return groupEnd();
        }
        return ReturnStatus.Success;
    }

    private static class TreePageComparator implements Comparator<int[]> {

        private static TreePageComparator TC = new TreePageComparator();

        @Override
        public int compare(int[] p1, int[] p2) {
            if (p1 == p2) {
                return 0;
            }
            if (p1 == null) {
                return -1;
            }
            if (p2 == null) {
                return 1;
            }
            int len1 = p1.length;
            int len2 = p2.length;
            int lim = Math.min(len1, len2);
            int k = 0;
            while (k < lim) {
                int c1 = p1[k];
                int c2 = p2[k];
                if (c1 != c2) {
                    return c1 - c2;
                }
                k++;
            }
            return len1 - len2;
        }

    }

    private class TreeIterator implements NodeDimensionIterator {
        private int[] index;
        private int[] tempIndex;
        private Map<Table, GroupValueIndex> controlFilters = new ConcurrentHashMap<Table, GroupValueIndex>();

        /**
         * TODO 先放内存看看再说
         */
        private List<int[]> pageIndex = new ArrayList<int[]>();

        private TreeIterator(int len) {
            this.index = new int[len];
            Arrays.fill(this.index, -1);
            PageEnd();
        }

        private void moveLast() {
            int pos = pageIndex.size() - 3;
            if (pos < 0) {
                throw new GroupEndException();
            }
            this.index = pageIndex.get(pos);
            this.pageIndex = this.pageIndex.subList(0, pos + 1);
        }

        private GroupValueIndex createFilterGvi(DimensionCalculator ck) {
            GroupValueIndex gvi = controlFilters.get(ck.getField().getTableBelongTo());
            if (gvi == null) {
                gvi = session.createFilterGvi(ck.getField().getTableBelongTo());
                controlFilters.put(ck.getField().getTableBelongTo(), gvi);
            }
            return gvi;
        }

        private void moveCurrentStart() {
            int pos = pageIndex.size() - 2;
            if (pos < 0) {
                throw new GroupEndException();
            }
            this.index = pageIndex.get(pos);
            this.pageIndex = this.pageIndex.subList(0, pos + 1);
        }


        private void travelToPositionPage(int[] shrinkPos) {
            int position = findPageIndexDichotomy(shrinkPos, pageIndex, 0, pageIndex.size() - 1);
            if (position - 1 >= 0) {
                this.index = pageIndex.get(position - 1);
            }
            if (position < pageIndex.size()) {
                pageIndex = pageIndex.subList(0, position);
            }
        }

        @Override
        public GroupConnectionValue next() {
            return seek(index);
        }

        private GroupConnectionValue seek(int[] index) {
            try {
                GroupConnectionValue gv = new GroupConnectionValue(null, null, null, root);
                IntList list = new IntList();
                if (ReturnStatus.GroupEnd == getNext(gv, root, index.clone(), 0, expander, list)) {
                    this.tempIndex = null;
                    return null;
                }
                for (int i = list.size(); i < cks.length; i++) {
                    list.add(-1);
                }
                this.tempIndex = list.toArray();
                return gv;
            } catch (GroupEndException e) {
                this.tempIndex = null;
                return null;
            }
        }

        @Override
        public boolean hasPrevious() {
            return pageIndex.size() > 2;
        }

        @Override
        public boolean hasNext() {
            return next() != null && index != null && index.length > 0;
        }

        @Override
        public void moveNext() {
            if (this.tempIndex != null) {
                this.index = tempIndex.clone();
            }
        }

        @Override
        public int getPageIndex() {
            return pageIndex.size() - 1;
        }

        @Override
        public void PageEnd() {
            pageIndex.add(this.index.clone());
        }

        private void fillEndEmpty(int deep) {
            Arrays.fill(this.index, deep, this.index.length, -1);
        }

        private void reset() {
            fillEndEmpty(0);
            this.pageIndex = this.pageIndex.subList(0, 0);
        }

    }
}