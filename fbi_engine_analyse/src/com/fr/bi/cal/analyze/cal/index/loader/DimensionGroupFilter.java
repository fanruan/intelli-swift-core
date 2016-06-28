package com.fr.bi.cal.analyze.cal.index.loader;

import com.finebi.cube.conf.table.BIBusinessTable;
import com.finebi.cube.conf.table.BusinessTable;
import com.finebi.cube.relation.BITableSourceRelation;
import com.fr.bi.cal.analyze.cal.index.loader.nodeiterator.IteratorManager;
import com.fr.bi.cal.analyze.cal.index.loader.nodeiterator.NormalIteratorManager;
import com.fr.bi.cal.analyze.cal.multithread.MergeSummaryCall;
import com.fr.bi.cal.analyze.cal.multithread.MultiThreadManagerImpl;
import com.fr.bi.cal.analyze.cal.result.*;
import com.fr.bi.cal.analyze.cal.sssecret.*;
import com.fr.bi.cal.analyze.cal.sssecret.sort.SortedTree;
import com.fr.bi.cal.analyze.cal.sssecret.sort.SortedTreeBuilder;
import com.fr.bi.cal.analyze.cal.store.GroupKey;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.conf.report.widget.field.dimension.BIDimension;
import com.fr.bi.conf.report.widget.field.dimension.filter.DimensionFilter;
import com.fr.bi.field.dimension.dimension.BIStringDimension;
import com.fr.bi.field.dimension.filter.general.GeneralANDDimensionFilter;
import com.fr.bi.field.target.key.cal.BICalculatorTargetKey;
import com.fr.bi.field.target.key.cal.configuration.BIConfiguratedCalculatorTargetKey;
import com.fr.bi.field.target.target.BISummaryTarget;
import com.fr.bi.manager.PlugManager;
import com.fr.bi.stable.data.BITable;
import com.fr.bi.stable.data.key.date.BIDay;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.report.key.TargetGettingKey;
import com.fr.bi.stable.report.result.*;
import com.fr.general.ComparatorUtils;
import com.fr.general.NameObject;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class created on 2016/3/9.
 *
 * @author Connery
 * @since 4.0
 */
public class DimensionGroupFilter {
    private static Map<FilterGroupValueIndexKey, GroupValueIndex[]> filterGroupValueIndexMap = new ConcurrentHashMap<FilterGroupValueIndexKey, GroupValueIndex[]>();
    private final BISession session;
    List<MergerInfo> mergerInfoList = new ArrayList<MergerInfo>();
    BIDimension[] rowDimension;
    BISummaryTarget[] usedTargets;
    Map<String, TargetCalculator> targetsMap;
    Map<String, DimensionFilter> targetFilterMap;
    List<BICalculatorTargetKey> calculatorTargets;
    NodeDimensionIterator[] iterators;
    SortedTreeBuilder[] sortedTreeBuilders;
    SortedTree[] sortedTrees;
    NameObject targetSort;
    Comparator[] dimensionComparator;
    private boolean shouldRecalculateIndex = false;
    private long startTime = System.currentTimeMillis();


    public DimensionGroupFilter(List<MergerInfo> mergerInfoList, Map<String, DimensionFilter> targetFilterMap, BIDimension[] rowDimension, BISummaryTarget[] usedTargets, Map<String, TargetCalculator> targetsMap, BISession session, NameObject targetSort) {
        this.mergerInfoList = mergerInfoList;
        this.rowDimension = rowDimension;
        this.dimensionComparator = new Comparator[rowDimension.length];
        this.usedTargets = usedTargets;
        this.targetsMap = targetsMap;
        this.targetFilterMap = targetFilterMap;
        this.session = session;
        this.targetSort = targetSort;
        calculatorTargets = LoaderUtils.getCalculatorTargets(usedTargets, session);
        iterators = getNodeIterators(mergerInfoList);
        sortedTrees = new SortedTree[mergerInfoList.size()];
        LoaderUtils.setAllExpander(mergerInfoList);

    }

    private NodeDimensionIterator[] getNodeIterators(List<MergerInfo> mergerInfoList) {
        NodeDimensionIterator[] iterators = new NodeDimensionIterator[mergerInfoList.size()];
        for (int i = 0; i < mergerInfoList.size(); i++) {
            iterators[i] = mergerInfoList.get(i).getRootDimensionGroup().moveToStart();
        }
        return iterators;
    }

    private FilterGroupValueIndexKey createFilterGroupKey(List<DimensionFilter> filterList, SummaryDimensionGroup[] summaryDimensionGroups) {
        Set<GroupKey> groupKeySet = new HashSet<GroupKey>();
        for (SummaryDimensionGroup group : summaryDimensionGroups) {
            groupKeySet.add(group.createSingleDimensionGroupKey());
        }
        return new FilterGroupValueIndexKey(new HashSet<DimensionFilter>(filterList), groupKeySet);
    }

    private TargetGettingKey[] getNoCalculatorTargets() {
        TargetGettingKey[] noCalculatorTargets = new TargetGettingKey[mergerInfoList.size()];
        for (int i = 0; i < noCalculatorTargets.length; i++) {
            noCalculatorTargets[i] = mergerInfoList.get(i).getTargetGettingKey();
        }
        return noCalculatorTargets;
    }

    private GroupValueIndex[] createAllShowIndex(int size) {
        GroupValueIndex[] retIndexes = new GroupValueIndex[size];
        Arrays.fill(retIndexes, MergerInfo.ALL_SHOW);
        return retIndexes;
    }

    private GroupValueIndex[] createAllEmptyIndex(int size) {
        GroupValueIndex[] retIndexes = new GroupValueIndex[size];
        Arrays.fill(retIndexes, MergerInfo.ALL_EMPTY);
        return retIndexes;
    }

    private void calCalculateTarget(LightNode mergeNode) {
        List targetKey = new ArrayList(targetsMap.values());
        List calculatorTarget = new ArrayList();
        for (BICalculatorTargetKey key : calculatorTargets) {
            calculatorTarget.add(targetsMap.get(key.getTargetName()));
        }
        targetKey.removeAll(calculatorTarget);
        CubeIndexLoader.calculateTargets(targetKey, calculatorTarget, mergeNode, true);

    }

    private boolean allShowNode(List<DimensionFilter> filterList, IMergerNode node, Map<String, TargetCalculator> targetsMap, int deep) {
        Object oldData = null;
        if (node.getData() instanceof BIDay) {
            oldData = node.getData();
            node.setShowValue(rowDimension[deep].toString(oldData));
        } else {
            node.setShowValue(node.getData().toString());
        }
        boolean ret = true;
        for (DimensionFilter filter : filterList) {
            if (!filter.showNode(node, targetsMap, session.getLoader())) {
                ret = false;
                break;
            }
        }
        if (oldData != null) {
            node.setData(oldData);
        }
        return ret;
    }

    private List<DimensionFilter> removeNullFilter(List<DimensionFilter> filterList) {
        List<DimensionFilter> ret = new ArrayList<DimensionFilter>();
        for (DimensionFilter filter : filterList) {
            if (filter != null) {
                ret.add(filter);
            }
        }
        return ret;
    }

    private List<DimensionFilter> getDimensionTraverseResultFilters(int deep) {
        List<DimensionFilter> filterList = new ArrayList<DimensionFilter>();
        DimensionFilter filter = rowDimension[deep].getFilter();
        if (isNormalResultFilter(filter)) {
            filterList.add(filter);
        }
        return AddTargetFilters(filterList, deep);
    }

    private boolean isNormalResultFilter(DimensionFilter filter) {
        return !isDirectFilter(filter);
    }

    private List<DimensionFilter> getAllResultFilters() {
        List<DimensionFilter> filterList = getAllDimensionFilter();
        return AddTargetFilters(filterList, rowDimension.length - 1);
    }

    private List<DimensionFilter> AddTargetFilters(List<DimensionFilter> filterList, int deep) {
        if (targetFilterMap != null && deep == rowDimension.length - 1) {
            filterList.addAll(targetFilterMap.values());
        }
        return removeNullFilter(filterList);
    }

    private List<DimensionFilter> getAllDimensionFilter() {
        List<DimensionFilter> filterList = new ArrayList<DimensionFilter>();
        for (int deep = 0; deep < rowDimension.length; deep++) {
            DimensionFilter filter = rowDimension[deep].getFilter();
            filterList.add(filter);
        }
        return filterList;
    }

    private List<DimensionFilter> getAllNotDateDimensionFilter() {
        List<DimensionFilter> filterList = new ArrayList<DimensionFilter>();
        for (int deep = 0; deep < rowDimension.length; deep++) {
            if (!isStringDimension(rowDimension[deep])) {
                DimensionFilter filter = rowDimension[deep].getFilter();
                filterList.add(filter);
            }
        }
        return filterList;
    }

    private boolean hasTraverseResultFilter() {
        return !getTraverseResultFilter().isEmpty();
    }

    private List<DimensionFilter> getTraverseResultFilter() {
        List<DimensionFilter> list = getAllResultFilters();
        List<DimensionFilter> retList = new ArrayList<DimensionFilter>();
        for (DimensionFilter filter : list) {
            if (!isDirectFilter(filter)) {
                retList.add(filter);
            }
        }
        return retList;
    }

    private boolean isNotStringDimensionFilter(DimensionFilter filter) {
        for (int i = 0; i < rowDimension.length; i++) {
            if (rowDimension[i].getFilter() == filter) {
                if (!isStringDimension(rowDimension[i])) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isDirectFilter(DimensionFilter filter) {
        if (isNotStringDimensionFilter(filter)) {
            return false;
        }
        if (filter instanceof GeneralANDDimensionFilter) {
            GeneralANDDimensionFilter resultFilter = (GeneralANDDimensionFilter) filter;
            if (resultFilter.canCreateDirectFilter()) {
                return true;
            }
        }
        return false;

    }

    private Node[] nextNodes(SummaryDimensionGroup[] summaryDimensionGroups) {
        Node[] nodes = getCurrentNodes(summaryDimensionGroups);

        Node[] minNodes = getMinNodesNodes(summaryDimensionGroups, nodes);

        moveNext(summaryDimensionGroups, minNodes);
        return minNodes;
    }

    private Node[] getCurrentNodes(SummaryDimensionGroup[] summaryDimensionGroups) {
        Node[] nodes = new Node[summaryDimensionGroups.length];
        for (int i = 0; i < summaryDimensionGroups.length; i++) {
            nodes[i] = (Node) summaryDimensionGroups[i].getCurrentNode();
        }
        return nodes;
    }

    public List<MergerInfo> calculateAllDimensionFilter() {
        GroupValueIndex[] allDirectFilterIndex = createDirectFilterIndex();
        applyFilterIndexes(allDirectFilterIndex);
        GroupValueIndex[] allDimensionFilterIndex = createTraverseFilterIndex();
        for (int i = 0; i < allDimensionFilterIndex.length; i++) {
            mergerInfoList.get(i).setFilterIndex(allDimensionFilterIndex[i]);
            if (sortedTreeBuilders != null && hasTargetSortByTargetKey()) {
                mergerInfoList.get(i).setSortedTree(sortedTreeBuilders[i].build());
            }
        }
        return mergerInfoList;
    }

    private void applyFilterIndexes(GroupValueIndex[] groupValueIndexes) {
        for (int i = 0; i < groupValueIndexes.length; i++) {
            if (groupValueIndexes[i] != null) {
                mergerInfoList.get(i).applyFilterIndex(groupValueIndexes[i]);
            }
        }
        this.iterators = getNodeIterators(mergerInfoList);
        LoaderUtils.setAllExpander(mergerInfoList);
    }

    private boolean isStringDimension(BIDimension dimension) {
        return dimension instanceof BIStringDimension;
    }

    private GroupValueIndex[] createDirectFilterIndex() {
        GroupValueIndex[] ret = createAllShowIndex(mergerInfoList.size());
        for (int i = 0; i < mergerInfoList.size(); i++) {
            for (int deep = 0; deep < rowDimension.length; deep++) {
                if (!isStringDimension(rowDimension[deep])) {
                    continue;
                }
                if (rowDimension[deep].getFilter() instanceof GeneralANDDimensionFilter) {
                    GeneralANDDimensionFilter resultFilter = (GeneralANDDimensionFilter) rowDimension[deep].getFilter();
                    if (resultFilter.canCreateDirectFilter()) {
                        DimensionCalculator c = mergerInfoList.get(i).createColumnKey()[deep];
                        BusinessTable t = (ComparatorUtils.equals(mergerInfoList.get(i).getRoot().getTableKey(), BITable.BI_EMPTY_TABLE())) ? c.getField().getTableBelongTo() : mergerInfoList.get(i).getRoot().getTableKey();
                        GroupValueIndex filterIndex = resultFilter.createFilterIndex(c, t, session.getLoader(), session.getUserId());
                        ret[i] = and(ret[i], filterIndex);
                    }
                }
            }
        }
        return ret;
    }

    private GroupValueIndex and(GroupValueIndex gvi1, GroupValueIndex gvi2) {
        if (gvi1 == MergerInfo.ALL_SHOW) {
            return gvi2;
        }
        return gvi1.AND(gvi2);
    }

    private GroupValueIndex[] createTraverseFilterIndex() {
        if (shouldNotTraverse()) {
            return createAllShowIndex(mergerInfoList.size());
        }
        GroupValueIndex[] groupValueIndexes = new GroupValueIndex[mergerInfoList.size()];
        groupValueIndexes = createAllShowIndex(mergerInfoList.size());
        GroupValueIndex[][] groupValueIndexe2D = createAllEmptyIndex2D();
        GroupConnectionValue[] roots = next();
        GroupConnectionValue[] lastRoots = null;
        initSortedTreeBuilders();
        RowCounter counter = new RowCounter(rowDimension.length);
        TreeBuilder nodeBuilder = new TreeBuilder();
        setRootIndexMap(nodeBuilder);
        int count = 0;
        while (!GroupUtils.isAllEmpty(roots)) {
            moveNext(roots);
            int firstChangeDeep = getFirstChangeDeep(roots, lastRoots);
            for (int deep = firstChangeDeep; deep < rowDimension.length; deep++) {
                count ++;
                fillValueIndex(groupValueIndexe2D, roots, counter, nodeBuilder, deep, count);
            }
            lastRoots = roots;
            roots = next();
        }
        MultiThreadManagerImpl.getInstance().awaitExecutor();
        if (shouldBuildTree()) {
            buildTree(groupValueIndexe2D, counter, nodeBuilder);
        }
        createFinalIndexes(groupValueIndexes, groupValueIndexe2D);
        return groupValueIndexes;
    }

    private void buildTree(GroupValueIndex[][] groupValueIndexe2D, RowCounter counter, TreeBuilder nodeBuilder) {
        LightNode sortedNode = buildTreeNode(groupValueIndexe2D, counter, nodeBuilder);
        for (int i = 0; i < mergerInfoList.size(); i++) {
            if (isShouldRecalculateIndex()) {
                NodeUtils.reCalculateIndex(sortedNode, mergerInfoList.get(i).getTargetGettingKey());
            }
            mergerInfoList.get(i).setTreeNode(sortedNode);
        }
    }

    private void createFinalIndexes(GroupValueIndex[] groupValueIndexes, GroupValueIndex[][] groupValueIndexe2D) {
        for (int i = 0; i < groupValueIndexe2D.length; i++) {
            if (!ComparatorUtils.equals(mergerInfoList.get(i).getRoot().getTableKey(), BIBusinessTable.createEmptyTable())) {
                for (int j = 0; j < groupValueIndexe2D[i].length; j++) {
                    if (groupValueIndexes[i] == MergerInfo.ALL_SHOW) {
                        groupValueIndexes[i] = groupValueIndexe2D[i][j];
                    } else {
                        groupValueIndexes[i] = groupValueIndexes[i].AND(groupValueIndexe2D[i][j]);
                    }
                }
            }
        }
    }

    private void fillValueIndex(GroupValueIndex[][] groupValueIndexe2D, GroupConnectionValue[] roots, RowCounter counter, TreeBuilder nodeBuilder, int deep, int count) {
        GroupConnectionValue[] groupConnectionValueChildren = getDeepChildren(roots, deep + 1);

        IMergerNode mergeNode = new MergerNode();
        GroupValueIndex[] groupValueIndexArray = new GroupValueIndex[mergerInfoList.size()];
        Map<TargetGettingKey, GroupValueIndex> gviMap = new HashMap<TargetGettingKey, GroupValueIndex>();
        for (int j = 0; j < groupConnectionValueChildren.length; j++) {
            if (groupConnectionValueChildren[j] != null) {
                NoneDimensionGroup currentValue = groupConnectionValueChildren[j].getCurrentValue();
                mergeNode.setData(groupConnectionValueChildren[j].getData());
                mergeNode.setComparator(getComparator(deep));
                mergeNode.setCk(groupConnectionValueChildren[j].getCk());
                groupValueIndexArray[j] = currentValue.getRoot().getGroupValueIndex();
                gviMap.put(mergerInfoList.get(j).getTargetGettingKey(), currentValue.getRoot().getGroupValueIndex());
                if(MultiThreadManagerImpl.getInstance().isMultiCall() && count < 100) {
                    MultiThreadManagerImpl.getInstance().getExecutorService().submit(new MergeSummaryCall(mergeNode, currentValue, mergerInfoList.get(j)));
                }else {
                    Number summaryValue = currentValue.getSummaryValue(mergerInfoList.get(j).getSummary());
                    if (summaryValue != null) {
                        mergeNode.setSummaryValue(mergerInfoList.get(j).getTargetGettingKey(), summaryValue);
                    }
                }
            }
        }
        Map<TargetGettingKey, GroupValueIndex> targetIndexValueMap = new HashMap<TargetGettingKey, GroupValueIndex>();
        for (int i = 0; i < groupValueIndexArray.length; i++) {
            targetIndexValueMap.put(mergerInfoList.get(i).getTargetGettingKey(), groupValueIndexArray[i]);
        }
        mergeNode.setGroupValueIndexArray(groupValueIndexArray);
        if (shouldSetIndex()) {
            mergeNode.setTargetIndexValueMap(targetIndexValueMap);
            mergeNode.setGroupValueIndexMap(gviMap);
        }
        if (shouldBuildTree()) {
            nodeBuilder.addLastNode(deep, mergeNode);
        } else {
            calCalculateTarget(mergeNode);
            filter(groupValueIndexe2D, counter, deep, groupConnectionValueChildren, mergeNode);
        }
    }

    private boolean shouldSetIndex() {
        return true;
    }

    private void setRootIndexMap(TreeBuilder nodeBuilder) {
        Map<TargetGettingKey, GroupValueIndex> gviMap = new HashMap<TargetGettingKey, GroupValueIndex>();
        Map<TargetGettingKey, GroupValueIndex> targetIndexValueMap = new HashMap<TargetGettingKey, GroupValueIndex>();
        for (int i = 0; i < mergerInfoList.size(); i++) {
            gviMap.put(mergerInfoList.get(i).getTargetGettingKey(), mergerInfoList.get(i).getGroupValueIndex());
            targetIndexValueMap.put(mergerInfoList.get(i).getTargetGettingKey(), mergerInfoList.get(i).getGroupValueIndex());
        }
        nodeBuilder.setRootGroupValueIndexMap(gviMap);
        nodeBuilder.setRootTargetIndexValueMap(targetIndexValueMap);
    }

    private void moveNext(GroupConnectionValue[] roots) {
        GroupUtils.moveNext(roots, iterators);
    }

    private boolean shouldNotTraverse() {
        return !hasTraverseResultFilter() && !hasTargetSortByTargetKey() && !shouldBuildTree();
    }

    private LightNode buildTreeNode(GroupValueIndex[][] groupValueIndexe2D, RowCounter counter, TreeBuilder nodeBuilder) {
        TreeIterator iterator = new TreeIterator(nodeBuilder.build());
        IMergerNode node;
        calCalculateTarget(nodeBuilder.getTree());

        TreeBuilder filterBuilder = new TreeBuilder();
        if (shouldSetIndex()) {
            NodeUtils.copyIndexMap(filterBuilder.getRoot(), nodeBuilder.getRoot());
        }
        while ((node = (IMergerNode) iterator.next()) != null) {
            filterTree(filterBuilder, groupValueIndexe2D, counter, iterator.getCurrentDeep(), node);
        }
        LightNode retNode = filterBuilder.build();


        if (hasTargetSort()) {
            retNode = sortNode(retNode);
        }

        retNode = clearNull(retNode, 0);
        NodeUtils.buildParentAndSiblingRelation(retNode);
        retNode = sum(retNode);
        return retNode;
    }

    private LightNode clearNull(LightNode node, int deep) {
        LightNode newNode = MergerNode.copyNode(node);
        for (int i = 0; i < node.getChildLength(); i++) {
            LightNode childNode = clearNull(node.getChild(i), deep + 1);
            if (isLastNode(deep + 1) || hasChild(childNode)) {
                newNode.addChild(childNode);
            }
        }
        return newNode;
    }

    private boolean isLastNode(int deep) {
        return rowDimension.length == deep;
    }

    private boolean hasChild(LightNode node) {
        return node.getChildLength() != 0;
    }

    private LightNode sum(LightNode node) {
        LightNode retNode = sumNode(node);
        //没child就没计算了
        if (retNode.getChildLength() == 0) {
            for (int i = 0; i < mergerInfoList.size(); i++) {
                Number summaryValue = mergerInfoList.get(i).getRoot().getSummaryValue(mergerInfoList.get(i).getSummary());
                if (summaryValue != null && retNode.getSummaryValue(mergerInfoList.get(i).getTargetGettingKey().getTargetKey()) == null) {
                    retNode.setSummaryValue(mergerInfoList.get(i).getTargetGettingKey(), summaryValue);
                }
            }
        }
        calCalculateTarget(retNode);
        return retNode;
    }

    private LightNode sortNode(LightNode retNode) {
        ISortInfoList sortInfoList = createSortInfoList();

        LightNode sortedNode = retNode.createSortedNode(targetSort, targetsMap, sortInfoList, 0);
        NodeUtils.buildParentAndSiblingRelation(sortedNode);

        return sortedNode;
    }

    private ISortInfoList createSortInfoList() {
        SortInfo[] sortInfoArray = new SortInfo[rowDimension.length];

        ISortInfoList sortInfoList = new SortInfoList();
        if (targetSort != null) {
            sortInfoList.setPrioritySortInfo(new SortInfo(targetSort.getName(), (Integer) targetSort.getObject()));

        }

        for (int i = 0; i < rowDimension.length; i++) {
            if (rowDimension[i].getSortTarget() != null) {
                SortInfo sortInfo = new SortInfo(rowDimension[i].getSortTarget(), rowDimension[i].getSortType());
                sortInfoArray[i] = sortInfo;
            }
        }

        sortInfoList.setSortInfoArray(sortInfoArray);
        return sortInfoList;
    }

    private LightNode sumNode(LightNode sortedNode) {
        NodeSummarizing summarizing = new NodeSummarizing();
        summarizing.setNode(sortedNode);
        summarizing.setTargetGettingKeys(getNoCalculatorTargets());
        return summarizing.sum();
    }

    private IMergerNode copyValue(IMergerNode node) {
        return MergerNode.copyNode(node);
    }

    private void filterTree(TreeBuilder filterBuilder, GroupValueIndex[][] groupValueIndexe2D, RowCounter counter, int deep, IMergerNode mergeNode) {
        if (allShowNode(getDimensionTraverseResultFilters(deep), mergeNode, targetsMap, deep)) {
            counter.count(deep);
            if (!counter.isFilteredCurrent(deep)) {
                filterBuilder.addLastNode(deep, copyValue(mergeNode));
            }
            GroupValueIndex[] groupValueIndexArray = mergeNode.getGroupValueIndexArray();
            for (int j = 0; j < groupValueIndexArray.length; j++) {
                if (groupValueIndexArray[j] != null) {
                    GroupValueIndex index = mergeNode.getGroupValueIndexArray()[j];
                    groupValueIndexe2D[j][deep] = or(groupValueIndexe2D[j][deep], index);
                }
            }
        } else {
            counter.markFiltered(deep);
        }
    }

    private GroupValueIndex or(GroupValueIndex groupValueIndex, GroupValueIndex index) {
        if (groupValueIndex == MergerInfo.ALL_EMPTY) {
            groupValueIndex = tryToSmall(index);
        } else {
            groupValueIndex = groupValueIndex.OR(index);
        }
        return groupValueIndex;
    }

    private GroupValueIndex tryToSmall(GroupValueIndex index) {
        return index;
    }

    private void filter(GroupValueIndex[][] groupValueIndexe2D, RowCounter counter, int deep, GroupConnectionValue[] groupConnectionValueChildren, IMergerNode mergeNode) {
        if (allShowNode(getDimensionTraverseResultFilters(deep), mergeNode, targetsMap, deep)) {
            counter.count(deep);
            for (int j = 0; j < groupConnectionValueChildren.length; j++) {
                if (groupConnectionValueChildren[j] != null) {
                    NoneDimensionGroup currentValue = groupConnectionValueChildren[j].getCurrentValue();
                    GroupValueIndex index = currentValue.getRoot().getGroupValueIndex();
                    groupValueIndexe2D[j][deep] = or(groupValueIndexe2D[j][deep], index);
                }
                if (hasTargetSortByTargetKey()) {
                    buildSortedTree(counter, deep, groupConnectionValueChildren, mergeNode, j);
                }

            }
        } else {
            counter.markFiltered(deep);
        }
    }

    private void buildSortedTree(RowCounter counter, int deep, Object[] groupConnectionValueChildren, IMergerNode mergeNode, int j) {
        if (1 == 1) {
            return;
        }
        Number sortValue = mergeNode.getSummaryValue(targetsMap.get(targetSort.getName()));
        double v;
        if (sortValue == null) {
            v = Double.MIN_VALUE;
        } else {
            v = sortValue.doubleValue();
        }
        if ((Integer) targetSort.getObject() == 1) {
            v = -v;
        }
        if (!counter.isFilteredCurrent(deep)) {
            if (groupConnectionValueChildren[j] != null) {
                sortedTreeBuilders[j].add(deep, counter.getDeepRow(deep), v);
            } else {
                sortedTreeBuilders[j].add(deep, -1, v);
            }
        }
    }

    private boolean hasTargetSortByTargetKey() {
        return targetSort != null;
    }

    private boolean hasTargetSortByDimension() {
        for (int i = 0; i < rowDimension.length; i++) {
            if (rowDimension[i].useTargetSort()) {
                return true;
            }
        }
        return false;
    }

    private boolean hasTargetSort() {
        return hasTargetSortByTargetKey() || hasTargetSortByDimension();
    }

    private void initSortedTreeBuilders() {
        sortedTreeBuilders = new SortedTreeBuilder[mergerInfoList.size()];
        for (int i = 0; i < sortedTreeBuilders.length; i++) {
            sortedTreeBuilders[i] = new SortedTreeBuilder();
        }
    }

    private int getFirstChangeDeep(GroupConnectionValue[] roots, GroupConnectionValue[] lastRoots) {
        if (lastRoots == null) {
            return 0;
        }
        GroupConnectionValue lastRoot = getNotNullGroupConnectionValue(lastRoots);
        GroupConnectionValue root = getNotNullGroupConnectionValue(roots);
        int deep = 0;
        GroupConnectionValue chain = root.getChild();
        GroupConnectionValue lastChain = lastRoot.getChild();

        while (chain != null && lastChain != null) {
            int c = chain.getComparator().compare(chain.getData(), lastChain.getData());
            if (c != 0) {
                break;
            }
            chain = chain.getChild();
            lastChain = lastChain.getChild();
            deep++;
        }
        return deep;
    }

    private GroupConnectionValue getNotNullGroupConnectionValue(GroupConnectionValue[] roots) {
        if (roots == null) {
            return null;
        }
        for (int i = 0; i < roots.length; i++) {
            if (roots[i] != null) {
                return roots[i];
            }
        }
        return null;
    }

    private GroupValueIndex[][] createAllEmptyIndex2D() {
        GroupValueIndex[][] groupValueIndex2D = new GroupValueIndex[mergerInfoList.size()][rowDimension.length];
        for (int i = 0; i < groupValueIndex2D.length; i++) {
            groupValueIndex2D[i] = createAllEmptyIndex(rowDimension.length);
        }
        return groupValueIndex2D;
    }

    private GroupConnectionValue[] getDeepChildren(GroupConnectionValue[] groupConnectionValues, int deep) {
        GroupConnectionValue[] groupConnectionValueChildren = new GroupConnectionValue[groupConnectionValues.length];
        for (int i = 0; i < groupConnectionValues.length; i++) {
            if (groupConnectionValues[i] != null) {
                groupConnectionValueChildren[i] = getDeepChild(groupConnectionValues[i], deep);
            }
        }
        return groupConnectionValueChildren;
    }

    private GroupConnectionValue getDeepChild(GroupConnectionValue root, int deep) {
        GroupConnectionValue child = root;
        for (int i = 0; i < deep; i++) {
            child = child.getChild();
            if (child == null) {
                return null;
            }
        }
        return child;
    }

    private RootDimensionGroup[] mergerRootDimensionGroup = null;

    private GroupConnectionValue[] next() {
        checkInRuntime();
        getIteratorManager().moveNext();
        GroupConnectionValue[] groupConnectionValues = getIteratorManager().getNextGroupConnectionValues();
        return getAllMinChildGroups(groupConnectionValues);
    }

    private IteratorManager iteratorManager = null;

    private IteratorManager getIteratorManager() {
        if (iteratorManager == null) {
            iteratorManager = new NormalIteratorManager(getRootDimensionGroups());
        }
        return iteratorManager;
    }

    private RootDimensionGroup[] getRootDimensionGroups() {
        if (mergerRootDimensionGroup == null) {
            mergerRootDimensionGroup = getRootDimensionGroups0();
        }
        return mergerRootDimensionGroup;
    }

    private RootDimensionGroup[] getRootDimensionGroups0() {
        RootDimensionGroup[] rootDimensionGroups = new RootDimensionGroup[mergerInfoList.size()];
        for (int i = 0; i < rootDimensionGroups.length; i++) {
            rootDimensionGroups[i] = mergerInfoList.get(i).getRootDimensionGroup();
        }
        return rootDimensionGroups;
    }


    private void checkInRuntime() {
        checkTimeout();
        checkInterrupt();
    }

    private void checkTimeout() {
        if (PlugManager.getPerformancePlugManager().controlTimeout()) {
            if (PlugManager.getPerformancePlugManager().isTimeout(startTime)) {
                throw new RuntimeException(PlugManager.getPerformancePlugManager().getTimeoutMessage());
            }
        }
    }

    private void checkInterrupt() {
        if (PlugManager.getPerformancePlugManager().controlUniqueThread()) {
            PlugManager.getPerformancePlugManager().checkExit();
        }
    }

    private GroupConnectionValue[] getAllMinChildGroups(GroupConnectionValue[] roots) {
        GroupConnectionValue[] parents = GroupUtils.getGroupConnectionValueChildren(roots);
        GroupConnectionValue[] gcvs = GroupUtils.getMinChildGroups(parents);
        while (!GroupUtils.isAllEmpty(gcvs)) {
            parents = gcvs;
            gcvs = GroupUtils.getGroupConnectionValueChildren(gcvs);
            gcvs = GroupUtils.getMinChildGroups(gcvs);
        }
        GroupConnectionValue[] allMinChildGroups = new GroupConnectionValue[roots.length];
        for (int i = 0; i < roots.length; i++) {
            if (parents[i] != null) {
                allMinChildGroups[i] = roots[i];
            }
        }
        return allMinChildGroups;
    }

    private Node[] getMinNodesNodes(SummaryDimensionGroup[] summaryDimensionGroups, Node[] nodes) {
        Node[] minNodes = new Node[summaryDimensionGroups.length];
        Object minValue = null;
        int firstMinValue = 0;
        for (int i = 0; i < nodes.length; i++) {
            Node node = nodes[i];
            if (node == null) {
                continue;
            }
            if (minValue == null && node != null) {
                minValue = node.getData();
            }
            int c = node.getComparator().compare(minValue, node.getData());
            if (c > 0) {
                minValue = node.getData();
                firstMinValue = i;
                minNodes[i] = nodes[i];
            } else if (c == 0) {
                minNodes[i] = nodes[i];
            }
        }

        for (int i = 0; i < firstMinValue; i++) {
            minNodes[i] = null;
        }
        return minNodes;
    }

    private Node mergeNodes(Node[] minNodes) {
        Node mergeNode = new Node(null, null);
        for (int i = 0; i < minNodes.length; i++) {
            if (minNodes[i] != null) {
                if (mergeNode.getData() == null) {
                    mergeNode.setData(minNodes[i].getData());
                }
                mergeNode.setSummaryValue(minNodes[i].getSummaryValue());
            }
        }
        return mergeNode;
    }

    private void moveNext(SummaryDimensionGroup[] summaryDimensionGroups, Node[] minNodes) {
        for (int i = 0; i < minNodes.length; i++) {
            if (minNodes[i] != null) {
                summaryDimensionGroups[i].next();
            }
        }
    }

    public boolean shouldBuildTree() {
        if (hasTargetSort()) {
            return true;
        }
        for (BICalculatorTargetKey key : calculatorTargets) {
            if (key instanceof BIConfiguratedCalculatorTargetKey) {
                return true;
            }
        }

//		if (hasDateDimension()) {
//			return true;
//		}

        for (DimensionFilter filter : getAllResultFilters()) {
            if (filter.needParentRelation()) {
                return true;
            }
        }

        return false;
    }

    private boolean hasDimensionTargetSort() {
        for (int i = 0; i < rowDimension.length; i++) {
            if (rowDimension[i].getSortTarget() != null) {
                return true;
            }
        }
        return false;
    }

    private Comparator getComparator(int deep) {
        if (dimensionComparator[deep] == null) {
            dimensionComparator[deep] = getComparator(rowDimension[deep]);
        }
        return dimensionComparator[deep];
    }

    private Comparator getComparator(BIDimension dimension) {
        return dimension.createCalculator(dimension.getStatisticElement(), new ArrayList<BITableSourceRelation>()).getComparator();
//		return dimension.createColumnKey(dimension.getColumn()).getComparator();
    }

    public boolean isShouldRecalculateIndex() {
        return shouldRecalculateIndex;
    }

    public void setShouldRecalculateIndex(boolean shouldRecalculateIndex) {
        this.shouldRecalculateIndex = shouldRecalculateIndex;
    }

    private List<TargetGettingKey> getNoCalculatorTargetKeys() {
        List<BISummaryTarget> noCalculatorTargetKeys = Arrays.asList(usedTargets);
        return null;
    }
}
