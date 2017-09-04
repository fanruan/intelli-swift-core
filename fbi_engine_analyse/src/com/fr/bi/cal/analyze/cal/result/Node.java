package com.fr.bi.cal.analyze.cal.result;

import com.fr.bi.cal.analyze.cal.utils.CubeReadingUtils;
import com.fr.bi.conf.report.widget.field.dimension.BIDimension;
import com.fr.bi.conf.report.widget.field.dimension.filter.DimensionFilter;
import com.fr.bi.report.key.TargetGettingKey;
import com.fr.bi.report.result.BINode;
import com.fr.bi.report.result.GviContainer;
import com.fr.bi.report.result.TargetCalculator;
import com.fr.bi.stable.constant.BIBaseConstant;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.structure.collection.map.ChildsMap;
import com.fr.bi.stable.utils.BICollectionUtils;
import com.fr.general.ComparatorUtils;
import com.fr.general.NameObject;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;
import com.fr.stable.StringUtils;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zhou
 */
public class Node implements BINode, GviContainer {

    /**
     * 小于1表示不分页
     */
    public static final int NONE_PAGE_LEVER = 0;

    /**
     *
     */
    private static final long serialVersionUID = -3230643843227594588L;

    /**
     * 子节点
     */
    volatile ChildsMap<Node> childs;

    /**
     *
     */
    private Object data;

    private Comparator comparator = BIBaseConstant.COMPARATOR.COMPARABLE.ASC;

    /**
     * 父亲节点
     */
    private Node parent;

    /**
     * 兄弟节点
     */
    private Node sibling;

    protected Number[] summaryValue;

    //ConcurrentHashMap需要支持高并发访问，不一定是该map高并发,当node过多时也需要高并发
    private GroupValueIndex[] indexValue;

    private String showValue;

    private transient Map<TargetGettingKey, Double> childAVG;

    //TODO 低效的算法， 放在result无所谓
    private transient Map<TopNKey, Double> topNLineMap;

    private int frameDeep;

    public Node(int sumLength) {

        childs = new ChildsMap<Node>();
        summaryValue = new Number[sumLength];
    }

    public Node(Object data, int sumLength) {

        this(sumLength);
        this.setData(data);
    }


    public Node(Comparator comparator, Object data, int sumLength) {

        this(data, sumLength);
        this.comparator = comparator;
    }

    /**
     * todo
     *
     * @param data
     */
    private void initShowValue(Object data) {
        // 先进行一次空值判断以及转换
        data = BICollectionUtils.cubeValueToWebDisplay(data);
        setShowValue(data == null ? StringUtils.EMPTY : data.toString());
    }

    @Override
    public Comparator getComparator() {

        return comparator;
    }

    @Override
    public Node getParent() {

        return parent;
    }

    @Override
    public void setParent(BINode parent) {

        this.parent = castNode(parent);
    }

    @Override
    public Object getData() {

        return data;
    }

    @Override
    public void setData(Object data) {

        this.data = data;
    }

    @Override
    public Node getSibling() {

        return sibling;
    }

    @Override
    public void setSibling(BINode sibling) {

        this.sibling = castNode(sibling);
    }

    @Override
    public String getShowValue() {

        if (showValue == null) {
            initShowValue(data);
        }
        return showValue;
    }

    @Override
    public void setShowValue(String showValue) {

        this.showValue = showValue;
    }

    /**
     * 添加子节点
     *
     * @param child 子节点
     */
    @Override
    public void addChild(BINode child) {

        Node node = castNode(child);
        node.setParent(this);
        if (!childs.isEmpty()) {
            Node lastNode = getLastChild();
            lastNode.setSibling(node);
        }
        childs.put(node.getData(), node);
    }

    private Node castNode(BINode lightNode) {

        return (Node) lightNode;
    }

    @Override
    public Node getLastChild() {

        return childs.getLastValue();
    }

    private GroupValueIndex[] getIndexValue() {

        if (indexValue == null) {
            synchronized (this) {
                if (indexValue == null) {
                    indexValue = new GroupValueIndex[summaryValue.length];
                }
            }
        }
        return indexValue;
    }


    @Override
    public Node getFirstChild() {

        return childs.getFirstValue();
    }

    @Override
    public int getChildLength() {

        if (childs == null) {
            return 0;
        }
        return childs.size();
    }

    @Override
    public Node getChild(int index) {

        return childs.get(index);
    }

    @Override
    public Node getChild(Object data) {

        return childs.get(data);
    }

    @Override
    public List<Node> getChilds() {

        return childs.values();
    }

    public void clearChildren() {

        for (Node n : getChilds()) {
            n.sibling = null;
        }
        childs = new ChildsMap<Node>();
    }

    @Override
    public int getTotalLength() {

        if (childs.isEmpty()) {
            return 1;
        }
        int res = 0;
        for (int i = 0; i < childs.size(); i++) {
            Node node = childs.get(i);
            res += node.getTotalLength();
        }
        return res;
    }

    @Override
    public int getDeep() {

        if (childs == null || childs.isEmpty()) {
            return 1;
        }
        int max = 0;
        for (int i = 0; i < childs.size(); i++) {
            Node node = childs.get(i);
            int temp = node.getDeep();
            if (max < temp) {
                max = temp;
            }
        }
        return max + 1;
    }

    @Override
    public int getFrameDeep() {

        return frameDeep;
    }

    @Override
    public int setFrameDeep(int deep) {

        return frameDeep = deep;
    }

    public int getTotalLengthWithSummary() {

        int res = 1;
        for (int i = 0; i < childs.size(); i++) {
            Node node = childs.get(i);
            res += node.getTotalLengthWithSummary();
        }
        if (!needSummary()) {
            res--;
        }
        return Math.max(res, 1);
    }

    public int getTotalLengthWithSummary(NodeExpander expander) {

        if (expander == null) {
            return 1;
        }

        int res = 1;
        for (int i = 0; i < childs.size(); i++) {
            Node node = childs.get(i);
            res += node.getTotalLengthWithSummary(expander.getChildExpander(node.getShowValue()));
        }
        if (!needSummary()) {
            res--;
        }
        return Math.max(res, 1);
    }

    public int getTotalLength(NodeExpander expander) {

        if (expander == null) {
            return 1;
        }
        if (childs.isEmpty()) {
            return 1;
        }
        int res = 0;
        for (int i = 0; i < childs.size(); i++) {
            Node node = childs.get(i);
            res += node.getTotalLength(expander.getChildExpander(node.getShowValue()));
        }
        return res;
    }


    public void setTargetIndex(TargetGettingKey key, GroupValueIndex gvi) {

        if (summaryValue == null || summaryValue.length - 1 < key.getTargetIndex()) {
            return;
        }
        if (gvi != null) {
            getIndexValue()[key.getTargetIndex()] = gvi;
        }
    }

    public GroupValueIndex getTargetIndex(TargetGettingKey key) {

        if (summaryValue == null || summaryValue.length - 1 < key.getTargetIndex()) {
            return null;
        }
        return getIndexValue()[key.getTargetIndex()];
    }

    public boolean needSummary() {

        if (getChildLength() > 1) {
            return true;
        }
        if (getChildLength() == 0) {
            return false;
        }
        //        Node child = getChild(0);
        //        for (int i = 0; i < summaryValue.length; i++) {
        //            if (!ComparatorUtils.equals(summaryValue[i], child.summaryValue[i])) {
        //                return true;
        //            }
        //        }
        return false;

    }

    @Override
    public void setSummaryValue(TargetGettingKey key, Number value) {

        setSummaryValue(key.getTargetIndex(), value);
    }

    public void setSummaryValue(int index, Number value) {

        if (value != null) {
            if (BICollectionUtils.isCubeNullKey(value)) {
                value = null;
            }
        }
        if (index < summaryValue.length) {
            summaryValue[index] = value;
        }
    }

    @Override
    public Number getSummaryValue(TargetGettingKey key) {

        return getSummaryValue(key.getTargetIndex());
    }

    public Number getSummaryValue(int index) {

        if (summaryValue == null || summaryValue.length - 1 < index) {
            return null;
        }
        return summaryValue[index];
    }

    protected Node createNewNode() {

        Node newNode = new Node(comparator, this.getData(), summaryValue.length);
        newNode.showValue = this.getShowValue();
        return newNode;
    }

    /**
     * 转成字符串显示
     *
     * @return 注释
     */
    @Override
    public String toString() {

        if (data != null) {
            return "BINode:[" + data.toString() + "]";
        }
        return null;
    }

    /**
     * 创建包含前count个的node
     *
     * @param count 个数
     * @return 新的node
     */
    public Node createBeforeCountNode(int count) {

        int end = Math.min(count, this.getChildLength());
        Node newnode = createNewNode();
        newnode.summaryValue = this.getSummaryValue();
        ChildsMap<Node> childs = this.childs;
        Node tempNode = null;
        for (int i = 0; i < end && i < childs.size(); i++) {
            Node temp_node = childs.get(i);
            Node child = temp_node.createCloneNodeWithValue();
            if (tempNode != null) {
                CubeReadingUtils.setSibling(tempNode, child);
            }
            newnode.addChild(child);
            tempNode = child;
        }
        return newnode;
    }

    /**
     * 创建包含后count个的node
     *
     * @param count 个数
     * @return 新的node
     */
    public Node createAfterCountNode(int count) {

        int end = this.getChildLength();
        int start = Math.max(end - count, 0);
        Node newnode = createNewNode();
        newnode.summaryValue = this.getSummaryValue();
        ChildsMap<Node> childs = this.childs;
        Node tempNode = null;
        for (int i = start; i < end && i < childs.size(); i++) {
            Node temp_node = childs.get(i);
            Node child = temp_node.createCloneNodeWithValue();
            if (tempNode != null) {
                CubeReadingUtils.setSibling(tempNode, child);
            }
            newnode.addChild(child);
            tempNode = child;
        }
        return newnode;
    }

    /**
     * 克隆node，按最终child的个数取，不是第一层child
     *
     * @param start 开始
     * @param end   结束
     * @return
     */
    public Node createCloneNodeWithValue(int start, int end) {

        Node newnode = copyNode();
        ChildsMap<Node> childs = this.childs;
        Node tempNode = null;
        int position = 0;
        for (int i = 0; i < childs.size(); i++) {
            Node c = childs.get(i);
            int clen = c.getTotalLength();
            if (position + clen > start && position < end) {
                int s = position > start ? 0 : start - position;
                int e = position + clen < end ? clen : end - position;
                Node child = c.createCloneNodeWithValue(s, e);
                if (tempNode != null) {
                    CubeReadingUtils.setSibling(tempNode, child);
                }
                newnode.addChild(child);
                tempNode = child;
            }
            if (position >= end) {
                break;
            }
            position += clen;
        }
        return newnode;
    }

    /**
     * 克隆node
     *
     * @return 新node
     */
    public Node createCloneNodeWithValue() {

        Node newnode = copyNode();
        ChildsMap<Node> childs = this.childs;
        Node tempNode = null;
        for (int i = 0; i < childs.size(); i++) {
            Node temp_node = childs.get(i);
            Node child = temp_node.createCloneNodeWithValue();
            if (tempNode != null) {
                CubeReadingUtils.setSibling(tempNode, child);
            }
            newnode.addChild(child);
            tempNode = child;
        }
        return newnode;
    }


    /**
     * 获取指标key子节点的平均值
     *
     * @param key 指标的key
     * @return 平均值
     */
    @Override
    public double getChildAVGValue(TargetGettingKey key) {

        if (getChildAVG().get(key) == null) {
            int count = this.getChildLength();
            if (count == 0) {
                getChildAVG().put(key, this.getSummaryValue(key) == null ? 0 : this.getSummaryValue(key).doubleValue());
                return getChildAVG().get(key);
            }
            double sum = 0;
            for (int i = 0; i < count; i++) {
                Node child = this.getChild(i);
                Number v = child.getSummaryValue(key);
                sum += v == null ? 0 : v.doubleValue();
            }
            getChildAVG().put(key, sum / count);
        }
        return getChildAVG().get(key);
    }

    /**
     * 获取topN的位置临界值
     *
     * @param key 指标
     * @param N   Top几
     * @return 值
     */
    @Override
    public double getChildTOPNValueLine(TargetGettingKey key, int N) {

        if (N < 1) {
            return Double.POSITIVE_INFINITY;
        }
        TopNKey topNKey = new TopNKey(N, key);
        Double nLine = getTopNLineMap().get(topNKey);
        if (nLine == null) {
            nLine = NodeUtils.getTopN(this, key, N);
            getTopNLineMap().put(topNKey, nLine);
        }
        return nLine;
    }

    /**
     * 获取维度的TopN临界值
     *
     * @param N top N位置
     * @return 临界值
     */
    @Override
    public Comparable getChildTOPNValueLine(int N) {

        int count = this.getChildLength();
        if (N < 1 || count == 0) {
            return null;
        }
        return (Comparable) getChild(Math.min(N, count) - 1).getData();
    }

    /**
     * 获取维度的BottomN临界值
     *
     * @param N BottomN位置
     * @return 临界值
     */
    @Override
    public Comparable getChildBottomNValueLine(int N) {

        return getChildTOPNValueLine(this.getChildLength() + 1 - N);
    }

    /**
     * 创建值过滤的新node
     *
     * @param rowDimension 维 度
     * @param targetsMap   所有指标
     * @return 新节点
     */
    public Node createResultFilterNode(BIDimension[] rowDimension,
                                       Map<String, TargetCalculator> targetsMap) {

        Node node = createResultFilterNode(0, rowDimension, targetsMap);
        if (node == null) {
            return createNewNode();
        }
        return node;
    }

    private Node createResultFilterNode(int index, BIDimension[] rowDimension,
                                        Map<String, TargetCalculator> targetsMap) {

        Node newnode = copyNode();
        ChildsMap<Node> childs = this.childs;
        Node tempNode = null;
        for (int i = 0; i < childs.size(); i++) {
            Node temp_node = childs.get(i);
            if (rowDimension[index].showNode(temp_node, targetsMap)) {
                Node child = temp_node.createResultFilterNode(index + 1, rowDimension, targetsMap);
                if (child == null) {
                    continue;
                }
                if (tempNode != null) {
                    CubeReadingUtils.setSibling(tempNode, child);
                }
                newnode.addChild(child);
                tempNode = child;
            }
        }
        if (tempNode == null && childs.size() > 0) {
            return null;
        }
        return newnode;
    }

    /**
     * 创建指标过滤的值
     *
     * @param targetFilterMap 指标过滤
     * @param targetsMap      所有指标
     * @return 新节点
     */
    public Node createResultFilterNode(Map<String, DimensionFilter> targetFilterMap,
                                       Map<String, TargetCalculator> targetsMap) {

        return createTargetFilterNode(targetFilterMap, targetsMap);
    }

    private Node createTargetFilterNode(Map<String, DimensionFilter> targetFilterMap,
                                        Map<String, TargetCalculator> targetsMap) {

        Node newnode = copyNode();
        ChildsMap<Node> childs = this.childs;
        Node tempNode = null;
        for (int i = 0; i < childs.size(); i++) {
            Node temp_node = childs.get(i);
            boolean showNode = true;
            if (targetFilterMap != null) {
                Iterator<DimensionFilter> it = targetFilterMap.values().iterator();
                while (it.hasNext()) {
                    if (!it.next().showNode(temp_node, targetsMap, null)) {
                        showNode = false;
                        break;
                    }
                }
            }
            if (showNode) {
                Node child = temp_node.createTargetFilterNode(targetFilterMap, targetsMap);
                if (child == null) {
                    continue;
                }
                if (tempNode != null) {
                    CubeReadingUtils.setSibling(tempNode, child);
                }
                newnode.addChild(child);
                tempNode = child;
            }
        }
        if (tempNode == null && childs.size() > 0) {
            return null;
        }
        return newnode;
    }

    /**
     * 创建排序后的新值
     *
     * @param rowDimension 维度
     * @param targetsMap   所有指标
     * @return 新节点
     */
    public Node createSortedNode(BIDimension[] rowDimension,
                                 Map<String, TargetGettingKey> targetsMap) {

        return createSortedNode(0, rowDimension, targetsMap);
    }

    private Node createSortedNode(int index, BIDimension[] rowDimension,
                                  Map<String, TargetGettingKey> targetsMap) {

        if (rowDimension.length == index) {
            return this;
        }

        Node newnode = copyNode();
        ChildsMap childs = this.childs;
        Node tempNode = null;
        String sortTarget = rowDimension[index].getSortTarget();
        List<Node> childNodes = childs.getNodeList();
        final TargetGettingKey target_key = sortTarget != null ? targetsMap.get(sortTarget) : null;
        final int sortType = rowDimension[index].getSortType();
        if (target_key != null) {
            Collections.sort(childNodes, new Comparator<Node>() {

                @Override
                public int compare(Node o1, Node o2) {

                    Number v1 = o1.getSummaryValue(target_key);
                    Number v2 = o2.getSummaryValue(target_key);
                    if (v1 == v2) {
                        return 0;
                    }
                    if (v1 == null) {
                        return 1;
                    }
                    if (v2 == null) {
                        return -1;
                    }
                    if (v1.doubleValue() == v2.doubleValue()) {
                        return 0;
                    }
                    boolean v = v1.doubleValue() < v2.doubleValue();
                    return (sortType == BIReportConstant.SORT.ASC) == v ? -1 : 1;
                }
            });
        }
        for (int i = 0; i < childNodes.size(); i++) {
            Node temp_node = childNodes.get(i);
            Node child = temp_node.createSortedNode(index + 1, rowDimension, targetsMap);
            //清除兄弟关系
            temp_node.setSibling(null);
            if (tempNode != null) {
                CubeReadingUtils.setSibling(tempNode, child);
            }
            newnode.addChild(child);
            tempNode = child;
        }
        return newnode;
    }

    /**
     * 创建排序后的新值
     *
     * @param targetSort 排序的指标
     * @param targetsMap 所有指标
     * @return 新节点
     */
    @Override
    public Node createSortedNode(NameObject targetSort,
                                 Map<String, TargetGettingKey> targetsMap) {

        return createTargetSortedNode(targetSort, targetsMap);
    }

    private Node createTargetSortedNode(NameObject targetSort,
                                        Map<String, TargetGettingKey> targetsMap) {

        if (targetSort == null) {
            return this;
        }
        Node newnode = copyNode();
        ChildsMap childs = this.childs;
        Node tempNode = null;
        String sortTarget = targetSort.getName();
        List<Node> childNodes = childs.getNodeList();
        final TargetGettingKey target_key = sortTarget != null ? targetsMap.get(sortTarget) : null;
        final int sortType = (Integer) (targetSort.getObject());
        if (target_key != null) {
            Collections.sort(childNodes, new Comparator<Node>() {

                @Override
                public int compare(Node o1, Node o2) {

                    Number v1 = o1.getSummaryValue(target_key);
                    Number v2 = o2.getSummaryValue(target_key);
                    if (v1 == v2) {
                        return 0;
                    }
                    if (v1 == null) {
                        return 1;
                    }
                    if (v2 == null) {
                        return -1;
                    }
                    if (v1.doubleValue() == v2.doubleValue()) {
                        return 0;
                    }
                    boolean v = v1.doubleValue() < v2.doubleValue();
                    return (sortType == BIReportConstant.SORT.ASC) == v ? -1 : 1;
                }
            });
        }
        for (int i = 0; i < childNodes.size(); i++) {
            Node temp_node = childNodes.get(i);
            Node child = temp_node.createTargetSortedNode(targetSort, targetsMap);
            //清除兄弟关系
            temp_node.setSibling(null);
            if (tempNode != null) {
                CubeReadingUtils.setSibling(tempNode, child);
            }
            newnode.addChild(child);
            tempNode = child;
        }
        return newnode;
    }

    private Node copyNode() {

        Node newnode = createNewNode();
        newnode.summaryValue = this.getSummaryValue();
        return newnode;
    }

    public Number[] getSummaryValue() {

        return summaryValue;
    }

    public void setSummaryValue(Number[] summaryValue) {

        this.summaryValue = summaryValue;
    }

    /**
     * 注释
     *
     * @param dimensions 注释求
     * @param keys       注释回
     * @param index      当前操注释类型
     * @return 注释
     */
    public JSONObject toJSONObject(BIDimension[] dimensions, TargetGettingKey[] keys, int index) throws JSONException {

        JSONObject jo = new JSONObject();
        if (index > -1) {
            jo.put("n", dimensions[index].toString(data));
        }
        JSONArray summary = new JSONArray();
        for (int i = 0; i < keys.length; i++) {
            //            summary.put(GeneralUtils.objectToNumber(GeneralUtils.objectToString(this.getSummaryValue(keys[i]))));
            // 需要对汇总值进行转换，如果会汇总值为空值的表示则不进行显示
            summary.put(BICollectionUtils.cubeValueToWebDisplay(this.getSummaryValue(keys[i])));
        }
        jo.put("s", summary);
        jo.put("x", getTotalLengthWithSummary());
        JSONArray children = new JSONArray();
        int childsSize = childs.size();
        if (childsSize > 0) {
            for (int i = 0; i < childsSize; i++) {
                children.put(childs.get(i).toJSONObject(dimensions, keys, index + 1));
            }
            jo.put("c", children);
        }
        return jo;
    }

    public JSONObject toTopJSONObject(BIDimension[] dimensions, TargetGettingKey[] keys, int index) throws JSONException {

        JSONObject jo = JSONObject.create();
        if (index > -1) {
            jo.put("n", dimensions[index].toString(getData()));
        }
        int childSize = childs.size();
        if (childSize > 0) {
            JSONArray children = JSONArray.create();
            for (int i = 0; i < childSize; i++) {
                children.put(childs.get(i).toTopJSONObject(dimensions, keys, index + 1));
            }
            jo.put("c", children);
        } else {
            JSONArray children = JSONArray.create();
            for (int i = 0; i < keys.length; i++) {
                JSONObject target = JSONObject.create();
                target.put("n", keys[i].getTargetName());
                children.put(target);
            }
            if (children.length() > 0) {
                jo.put("c", children);
            }
        }
        return jo;
    }


    private Map<TargetGettingKey, Double> getChildAVG() {

        if (childAVG == null) {
            childAVG = new ConcurrentHashMap<TargetGettingKey, Double>();
        }
        return childAVG;
    }


    private Map<TopNKey, Double> getTopNLineMap() {

        if (topNLineMap == null) {
            topNLineMap = new ConcurrentHashMap<TopNKey, Double>(1);
        }
        return topNLineMap;
    }

    @Override
    public ResultType getResultType() {

        return ResultType.BIGROUP;
    }

    private class TopNKey {

        private int N;

        private TargetGettingKey key;

        public TopNKey(int n, TargetGettingKey key) {

            N = n;
            this.key = key;
        }

        @Override
        public boolean equals(Object o) {

            if (this == o) {
                return true;
            }
            ;
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            TopNKey topNKey = (TopNKey) o;

            if (N != topNKey.N) {
                return false;
            }
            return key != null ? ComparatorUtils.equals(key, topNKey.key) : topNKey.key == null;

        }

        @Override
        public int hashCode() {

            int result = N;
            result = 31 * result + (key != null ? key.hashCode() : 0);
            return result;
        }
    }
}