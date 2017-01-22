package com.fr.bi.cal.analyze.cal.sssecret;

import com.finebi.cube.api.ICubeTableService;
import com.finebi.cube.api.ICubeValueEntryGetter;
import com.finebi.cube.conf.table.BusinessTable;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.cal.analyze.cal.index.loader.MetricGroupInfo;
import com.fr.bi.cal.analyze.cal.index.loader.TargetAndKey;
import com.fr.bi.cal.analyze.cal.result.NodeExpander;
import com.fr.bi.cal.analyze.cal.sssecret.diminfo.MergeIteratorCreator;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.stable.data.db.ICubeFieldSource;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.engine.index.key.IndexKey;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.report.result.DimensionCalculator;
import com.fr.bi.stable.utils.program.BINonValueUtils;
import com.fr.cache.list.IntList;
import com.fr.general.ComparatorUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * 根，用于保存游标等其他信息，感觉可以优化成一个游标就完了
 *
 * @author Daniel
 */
public class RootDimensionGroup implements IRootDimensionGroup {

    protected List<MetricGroupInfo> metricGroupInfoList;
    protected MergeIteratorCreator[] mergeIteratorCreators;
    protected BISession session;
    protected boolean useRealData;

    private TreeIterator iter;
    protected ICubeValueEntryGetter[][] getters;
    protected DimensionCalculator[][] columns;
    protected ICubeTableService[] tis;
    private ISingleDimensionGroup[] singleDimensionGroupCache;
    protected BusinessTable[] metrics;
    protected List<TargetAndKey>[] summaryLists;
    protected NoneDimensionGroup root;
    protected int rowSize;
    private NodeExpander expander;

    public RootDimensionGroup(List<MetricGroupInfo> metricGroupInfoList, MergeIteratorCreator[] mergeIteratorCreators, BISession session, boolean useRealData) {
        this.metricGroupInfoList = metricGroupInfoList;
        this.mergeIteratorCreators = mergeIteratorCreators;
        this.session = session;
        this.useRealData = useRealData;
        init();
    }

    protected void init() {
        initIterator();
        initGetterAndRows();
        initRoot();
    }

    private void initIterator() {
        if (metricGroupInfoList == null || metricGroupInfoList.isEmpty()) {
            BINonValueUtils.beyondControl("invalid parameters");
        }
        rowSize = metricGroupInfoList.get(0).getRows().length;
        for (MetricGroupInfo info : metricGroupInfoList) {
            if (info.getRows().length != rowSize) {
                throw new RuntimeException("invalid parameters");
            }
        }
        this.singleDimensionGroupCache = new ISingleDimensionGroup[rowSize];
        this.iter = new TreeIterator(rowSize);
    }

    protected void initGetterAndRows() {
        getters = new ICubeValueEntryGetter[rowSize][metricGroupInfoList.size()];
        columns = new DimensionCalculator[rowSize][metricGroupInfoList.size()];
        tis = new ICubeTableService[metricGroupInfoList.size()];
        for (int i = 0; i < metricGroupInfoList.size(); i++) {
            DimensionCalculator[] rs = metricGroupInfoList.get(i).getRows();
            for (int j = 0; j < rs.length; j++) {
                ICubeTableService ti = session.getLoader().getTableIndex(getSource(rs[j]));
                columns[j][i] = rs[j];
                getters[j][i] = ti.getValueEntryGetter(createKey(rs[j]), rs[j].getRelationList());
                tis[i] = ti;
            }
        }
    }

    protected void initRoot() {
        metrics = new BusinessTable[metricGroupInfoList.size()];
        summaryLists = new ArrayList[metricGroupInfoList.size()];
        GroupValueIndex[] gvis = new GroupValueIndex[metricGroupInfoList.size()];
        for (int i = 0; i < metricGroupInfoList.size(); i++) {
            metrics[i] = metricGroupInfoList.get(i).getMetric();
            summaryLists[i] = metricGroupInfoList.get(i).getSummaryList();
            gvis[i] = metricGroupInfoList.get(i).getFilterIndex();
        }
        root = NoneDimensionGroup.createDimensionGroup(metrics, summaryLists, tis, gvis, session.getLoader());
    }

    private CubeTableSource getSource(DimensionCalculator column) {
        //多对多
        if (column.getDirectToDimensionRelationList().size() > 0) {
            ICubeFieldSource primaryField = column.getDirectToDimensionRelationList().get(0).getPrimaryField();
            return primaryField.getTableBelongTo();
        }
        return column.getField().getTableBelongTo().getTableSource();
    }

    private BIKey createKey(DimensionCalculator column) {
        //多对多
        if (column.getDirectToDimensionRelationList().size() > 0) {
            ICubeFieldSource primaryField = column.getDirectToDimensionRelationList().get(0).getPrimaryField();
            return new IndexKey(primaryField.getFieldName());
        }
        return column.createKey();
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
    public void setExpander(NodeExpander expander) {
        this.expander = expander;
    }

    private int[] getValueStartRow(Object[] value) {
        IntList result = new IntList();
        getValueStartRow(root, value, 0, result);
        for (int i = value.length; i < rowSize; i++) {
            result.add(-1);
        }
        return result.toArray();
    }

    private void getValueStartRow(NoneDimensionGroup ng, Object[] value, int deep, IntList list) {
        if (deep >= value.length) {
            return;
        }
        ISingleDimensionGroup sg = getSingleDimensionGroupCache(value, ng, deep);
        int i = sg.getChildIndexByValue(value[deep]);
        if (i == -1) {
            return;
        }
        list.add(i);
        NoneDimensionGroup currentNg = sg.getChildDimensionGroup(i);
        if (currentNg == NoneDimensionGroup.NULL) {
            return;
        }
        getValueStartRow(currentNg, value, deep + 1, list);
    }

    private ISingleDimensionGroup getSingleDimensionGroupCache(Object[] value, NoneDimensionGroup ng, int deep) {
        return createSingleDimensionGroup(value, ng, deep);
    }

    /**
     * @param gv       表示一行的值
     * @param index    上一次游标的位置
     * @param deep     维度的层级
     * @param expander 展开信息
     * @param list     当前的游标
     */
    private ReturnStatus getNext(GroupConnectionValue gv,
                                 int[] index,
                                 int deep,
                                 NodeExpander expander,
                                 IntList list) {
        if (expander == null) {
            return ReturnStatus.GroupEnd;
        }
        if (rowSize == 0) {
            return ReturnStatus.Success;
        }
        ISingleDimensionGroup sg = getCacheDimensionGroup(gv, deep);
        //如果往下移动，行数就加1
        if (notNextChild(index, deep, expander, sg)) {
            index[deep]++;
        }
        ReturnStatus returnStatus = findCurrentValue(sg, gv, list, index[deep]);
        //如果越界，就一直往上移，直到不越界或者结束。
        while (returnStatus == ReturnStatus.GroupOutOfBounds) {
            //如果找到根节点还是越界，说明结束了。
            if (deep == 0) {
                return ReturnStatus.GroupEnd;
            }
            //把index数组deep位置后面的都清了，下移一位开始找，每次seek的时候这个while循环至多执行一次。
            Arrays.fill(index, deep, index.length, -1);
            list.remove(list.size() - 1);
            deep -= 1;
            gv = gv.getParent();
            sg = getCacheDimensionGroup(gv, deep);
            expander = expander.getParent();
            returnStatus = findCurrentValue(sg, gv, list, ++index[deep]);
        }
        //如果往下展开，就继续往下
        NodeExpander ex = expander.getChildExpander(sg.getChildShowName(index[deep]));
        if (ex != null && deep + 1 < index.length) {
            if (ReturnStatus.GroupEnd == getNext(gv.getChild(), index, deep + 1, ex, list)){
                return ReturnStatus.GroupEnd;
            }
        }
        return ReturnStatus.Success;
    }

    //最后一个维度或者初始化的情况或者没有展开的情况必定是往下的
    private boolean notNextChild(int[] index, int deep, NodeExpander expander, ISingleDimensionGroup sg) {
        if (index.length == deep + 1 || index[deep] == -1) {
            return true;
        }
        String showValue = sg.getChildShowName(index[deep]);
        return showValue != null && expander.getChildExpander(showValue) == null;
    }

    private ISingleDimensionGroup getCacheDimensionGroup(GroupConnectionValue gv, int deep) {
        if (singleDimensionGroupCache[deep] == null || !ComparatorUtils.equals(singleDimensionGroupCache[deep].getData(), getParentsValuesByGv(gv, deep))) {
            singleDimensionGroupCache[deep] = createSingleDimensionGroup(gv, deep);
        }
        return singleDimensionGroupCache[deep];
    }

    protected ISingleDimensionGroup createSingleDimensionGroup(GroupConnectionValue gv, int deep) {
        return createSingleDimensionGroup(getParentsValuesByGv(gv, deep), gv.getCurrentValue(), deep);
    }

    protected ISingleDimensionGroup createSingleDimensionGroup(Object[] data, NoneDimensionGroup ng, int deep) {
        return ng.createSingleDimensionGroup(columns[deep], getters[deep], data, mergeIteratorCreators[deep], useRealData);
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

    private ReturnStatus findCurrentValue(ISingleDimensionGroup sg, GroupConnectionValue gv, IntList list, int row) {
        NoneDimensionGroup nds = sg.getChildDimensionGroup(row);
        if (nds == NoneDimensionGroup.NULL) {
            return ReturnStatus.GroupOutOfBounds;
        }
        GroupConnectionValue ngv = createGroupConnectionValue(sg, row, nds);
        list.add(row);
        ngv.setParent(gv);
        return ReturnStatus.Success;
    }

    protected GroupConnectionValue createGroupConnectionValue(ISingleDimensionGroup sg, int row, NoneDimensionGroup nds) {
        GroupConnectionValue ngv = new GroupConnectionValue(sg.getChildData(row), nds);
        return ngv;
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

    /**
     * 类似n位进制不定的整数的加法，每次next就加1。
     * 比如有三个维度，数组初始位置是{-1, -1, -1}, 从末尾依次往上加1，一旦越界，比如加到了{0, 0, 6}越界了，就进位加1，变成{0, 1, 0}再继续。
     * 初始化为{-1, -1, -1}而不是{0, 0, -1}是因为可能没有展开倒最后一个维度，要是没有展开的情况会直接略过分组的第一个值。
     */
    private class TreeIterator implements NodeDimensionIterator {
        private int[] index;
        private int[] tempIndex;

        /**
         * TODO 先放内存看看再说
         */
        private List<int[]> pageIndex = new ArrayList<int[]>();

        private TreeIterator(int len) {
            this.index = new int[len];
            Arrays.fill(this.index, -1);
            pageEnd();
        }

        private void moveLast() {
            int pos = pageIndex.size() - 3;
            this.index = pageIndex.get(pos);
            this.pageIndex = this.pageIndex.subList(0, pos + 1);
        }

        private void moveCurrentStart() {
            int pos = pageIndex.size() - 2;
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
            GroupConnectionValue gv = new GroupConnectionValue(null, root);
            IntList list = new IntList();
            int indexCopy[] = Arrays.copyOf(index, index.length);
            if (ReturnStatus.GroupEnd == getNext(gv, indexCopy, 0, expander, list)) {
                this.tempIndex = null;
                return null;
            }
            //没有展开的情况list的size会小于维度的数量，要补齐。主要是怕越界。。。
            for (int i = list.size(); i < rowSize; i++) {
                list.add(-1);
            }
            this.tempIndex = list.toArray();
            return gv;
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
        public void pageEnd() {
            pageIndex.add(this.index.clone());
        }
    }
}