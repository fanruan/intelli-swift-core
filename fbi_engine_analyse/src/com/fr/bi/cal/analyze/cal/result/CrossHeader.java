/**
 *
 */
package com.fr.bi.cal.analyze.cal.result;

import com.fr.bi.cal.analyze.cal.utils.CubeReadingUtils;
import com.fr.bi.conf.report.widget.field.dimension.BIDimension;
import com.fr.bi.conf.report.widget.field.dimension.filter.ResultFilter;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.report.key.TargetGettingKey;
import com.fr.bi.stable.report.result.DimensionCalculator;
import com.fr.bi.stable.report.result.TargetCalculator;
import com.fr.bi.stable.structure.collection.map.ChildsMap;
import com.fr.general.NameObject;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

import java.io.Serializable;
import java.util.*;

/**
 * @author Daniel
 *         TODO 代码质量
 */
public class CrossHeader extends Node implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -7347868497832306939L;


    private CrossNode value;

    /**
     * 构造函数
     *
     * @param key    字段信息
     * @param data   值
     * @param getter 索引
     */
    public CrossHeader(DimensionCalculator key, Object data, GroupValueIndex gvi) {
        super(key, data);
        this.setGroupValueIndex(gvi);
    }

    private static CrossNode getLeftNode(CrossNode valueNode, int[] index, int i) {
        if (index.length > i) {
            return getLeftNode(valueNode.getLeftChild(index[i]), index, ++i);
        }
        return valueNode;
    }

    private static CrossNode getBottomNode(CrossNode valueNode, int[] index, int i) {
        if (index.length > i) {
            return getBottomNode(valueNode.getTopChild(index[i]), index, ++i);
        }
        return valueNode;
    }

    /**
     * node转化成CrossHeader方法
     */
    @Override
    protected CrossHeader createNewNode() {
        CrossHeader header = new CrossHeader(key, this.getData(), null);
        header.setShowValue(getShowValue());
        header.setGroupValueIndex(this.getGroupValueIndex());
        return header;
    }

    /**
     * 合并this 与 参数header为一个header
     *
     * @param header 被合并的header
     * @return 新的CrossHeader
     */
    public Node OrMerge(CrossHeader header) {
        return OrMerge(header, null);
    }

    /**
     * 获取交叉根节点
     *
     * @return 交叉根节点
     */
    public CrossNode getValue() {
        return value;
    }

    /**
     * 设置交叉节点的根节点值
     *
     * @param value 交叉根节点
     */
    public void setValue(CrossNode value) {
        this.value = value;
    }

    /**
     * 根据位置直接获取右方子节点的值
     *
     * @param index 子节点的位置定位
     * @param key   值的key
     * @return 右方子节点的值
     */
    public Number getLeftValue(int[] index, Object key) {
        return getLeftNode(index).getSummaryValue(key);
    }

    /**
     * 获取右方子节点
     *
     * @param index 子节点的位置定位
     * @return 右方子节点
     */
    public CrossNode getLeftNode(int[] index) {
        if (index == null) {
            return getValue();
        }
        return getLeftNode(getValue(), index, 0);
    }

    /**
     * 根据位置和key获取下方子节点的值
     *
     * @param index 位置
     * @param key   值的key
     * @return 下方节点值
     */
    public Number getBottomValue(int[] index, Object key) {
        return getBottomNode(index).getSummaryValue(key);
    }

    /**
     * 根据位置获取下方子节点
     *
     * @param index 位置
     * @return 下方子节点
     */
    public CrossNode getBottomNode(int[] index) {
        if (index == null) {
            return getValue();
        }
        return getBottomNode(getValue(), index, 0);
    }

    /**
     * 构建左边关系
     *
     * @param top 上方的head
     */
    public void buildLeftRelation(CrossHeader top) {
        CrossNode node = getValue();
        if (node == null) {
            return;
        }
        dealWithChild(node, new int[0], top);
    }

    private void dealWithChild(CrossNode node, int[] index, CrossHeader top) {
        top.setValue(node);
        dealWithNode(index);
        for (int i = 0; i < node.getTopChildLength(); i++) {
            int[] index_s = new int[index.length + 1];
            System.arraycopy(index, 0, index_s, 0, index.length);
            index_s[index.length] = i;
            dealWithChild(node.getTopChild(i), index_s, (CrossHeader) top.getChild(i));
        }
    }

    private void dealWithNode(int[] index) {
        CrossNode n = this.getBottomNode(index);
        CrossNode t = null;
        for (int i = 0; i < this.getChildLength(); i++) {
            CrossHeader header = (CrossHeader) this.getChild(i);
            CrossNode child = header.getBottomNode(index);
            header.dealWithNode(index);
            n.addLeftChild(child);
            if (t != null) {
                CubeReadingUtils.setSibing(t, child);
            }
            t = child;
        }

    }

    /**
     * 构建右边关系
     *
     * @param top      上方head
     * @param baseNode 未知？？？？没作用？？
     */
    public void dealWithChildLeft(CrossHeader top, CrossNode baseNode) {
        CrossNode temp = null;
        for (int i = 0, len = this.getChildLength(); i < len; i++) {
            CrossHeader child = (CrossHeader) this.getChild(i);
            CrossNode node = new CrossNode(top, child);
            child.setValue(node);
            top.dealWithChildTop(child, node);
            child.dealWithChildLeft(top, node);
            if (temp != null) {
                CubeReadingUtils.setSibing(temp, node);
            }
            temp = node;
        }
    }

    protected void dealWithChildTop4Merge(CrossHeader left, CrossNode baseNode, CrossNode left1, CrossNode left2, TargetGettingKey key2) {
        CrossNode temp = null;
        for (int i = 0, len = this.getChildLength(); i < len; i++) {
            CrossHeader child = (CrossHeader) this.getChild(i);
            Object v = child.getData();
            CrossNode left1V = left1 != null ? left1.getTopChild(v) : null;
            CrossNode left2V = left2 != null ? left2.getTopChild(v) : null;
            CrossNode node = new CrossNode(child, left);
            node.mergeValue(left1V, left2V, key2);
            baseNode.addTopChild(node);
            child.dealWithChildTop4Merge(left, node, left1V, left2V, key2);
            if (temp != null) {
                CubeReadingUtils.setSibing(temp, node);
            }
            temp = node;
        }
    }

    protected void dealWithChildLeft4Merge(CrossHeader top, CrossNode baseNode, CrossHeader left1, CrossHeader left2, TargetGettingKey key2) {
        CrossNode temp = null;
        for (int i = 0, len = this.getChildLength(); i < len; i++) {
            CrossHeader child = (CrossHeader) this.getChild(i);
            Object v = child.getData();
            CrossHeader left1H = left1 != null ? (CrossHeader) left1.getChild(v) : null;
            CrossHeader left2H = left2 != null ? (CrossHeader) left2.getChild(v) : null;
            CrossNode left1V = left1H != null ? left1H.getValue() : null;
            CrossNode left2V = left2H != null ? left2H.getValue() : null;
            CrossNode node = new CrossNode(top, child);
            node.mergeValue(left1V, left2V, key2);
            child.setValue(node);
            top.dealWithChildTop4Merge(child, node, left1V, left2V, key2);
            child.dealWithChildLeft4Merge(top, node, left1H, left2H, key2);
            if (temp != null) {
                CubeReadingUtils.setSibing(temp, node);
            }
            temp = node;
        }
    }

    /**
     * 构建下方子关系
     *
     * @param left     左边节点
     * @param baseNode 方法比较奇怪 TODO 需要调整
     */
    public void dealWithChildTop(CrossHeader left, CrossNode baseNode) {
        CrossNode temp = null;
        for (int i = 0, len = this.getChildLength(); i < len; i++) {
            CrossHeader child = (CrossHeader) this.getChild(i);
            CrossNode node = new CrossNode(child, left);
            baseNode.addTopChild(node);
            child.dealWithChildTop(left, node);
            if (temp != null) {
                CubeReadingUtils.setSibing(temp, node);
            }
            temp = node;
        }
    }

    /**
     * 构建左边子节点用于计算
     *
     * @param top      上方
     * @param baseNode 方法比较奇怪 TODO 需要调整
     */
    public void dealWithChildLeft4Calculate(CrossHeader top, CrossNode baseNode, TargetCalculator[] calculators) {
        CrossNode temp = null;
        for (int i = 0, len = this.getChildLength(); i < len; i++) {
            CrossHeader child = (CrossHeader) this.getChild(i);
            CrossNode node = new CrossNode4Calculate(top, child, calculators);
            child.setValue(node);
            top.dealWithChildTop4Calculate(child, node, calculators);
            child.dealWithChildLeft4Calculate(top, node, calculators);
            if (temp != null) {
                CubeReadingUtils.setSibing(temp, node);
            }
            temp = node;
        }
    }

    /**
     * Connery：
     * 构造用于交叉表计算的BaseNode
     * 遍历头节点，每个头节点Crossheader和全部left的Crossheader，构成一个计算CrossNode
     * 加入到baseNode中。
     * 同时处理头节点Crossheader的子节点。
     *
     * @param left     注
     * @param baseNode 注
     */
    public void dealWithChildTop4Calculate(CrossHeader left, CrossNode baseNode, TargetCalculator[] calculators) {
        CrossNode temp = null;

        for (int i = 0, len = this.getChildLength(); i < len; i++) {
            CrossHeader child = (CrossHeader) this.getChild(i);
            CrossNode node = new CrossNode4Calculate(child, left, calculators);
            baseNode.addTopChild(node);
            child.dealWithChildTop4Calculate(left, node, calculators);
            if (temp != null) {
                CubeReadingUtils.setSibing(temp, node);
            }
            temp = node;
        }
    }

    /**
     * 过滤只要取根节点的值来判断就可以了
     *
     * @param key 值的key
     */
    @Override
    public Number getSummaryValue(Object key) {
        if (value == null) {
            return null;
        }
        return value.getSummaryValue(key);
    }

    @Override
    public Map getSummaryValue() {
        if (value == null) {
            return new HashMap();
        }
        return value.getSummaryValue();
    }

    /**
     * 根据值过滤创建新的节点
     *
     * @param rowDimension 维度
     * @param targetsMap   所有的指标
     * @param top          上head
     * @return 新的left节点
     */
    public CrossHeader createResultFilterNodeWithTopValue(BIDimension[] rowDimension,
                                                          Map<String, TargetCalculator> targetsMap, CrossHeader top) {
        CrossHeader header = createResultFilterNodeWithTopValue(0, rowDimension, targetsMap, top);
        if (header == null) {
            CrossHeader header1 = createNewNode();
            header1.setValue(value.cloneWithTopChildNode(top));
            return header1;
        }
        return header;
    }


    private CrossHeader createResultFilterNodeWithTopValue(int index, BIDimension[] rowDimension,
                                                           Map<String, TargetCalculator> targetsMap, CrossHeader top) {
        CrossHeader newnode = createNewNode();
        newnode.value = this.value.cloneWithTopChildNode(top);
        ChildsMap childs = this.childs;
        CrossHeader tempNode = null;
        for (int i = 0; i < childs.size(); i++) {
            CrossHeader temp_node = (CrossHeader) childs.get(i);
            if (rowDimension[index].showNode(temp_node, targetsMap)) {
                CrossHeader child = temp_node.createResultFilterNodeWithTopValue(index + 1, rowDimension, targetsMap, top);
                if (child == null) {
                    continue;
                }
                if (tempNode != null) {
                    CubeReadingUtils.setSibing(tempNode, child);
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
     * 根据值过滤创建新的节点
     *
     * @param targetFilterMap 指标过滤
     * @param targetsMap      所有的指标
     * @return 新的节点
     */
    public CrossHeader createResultFilterNodeWithTopValue(Map<String, ResultFilter> targetFilterMap,
                                                          Map<String, TargetCalculator> targetsMap) {
        return createTargetFilterNodeWithTopValue(targetFilterMap, targetsMap);
    }

    private CrossHeader createTargetFilterNodeWithTopValue(Map<String, ResultFilter> targetFilterMap,
                                                           Map<String, TargetCalculator> targetsMap) {
        CrossHeader newnode = createNewNode();
        newnode.value = this.value.cloneWithTopChildNode();
        ChildsMap childs = this.childs;
        CrossHeader tempNode = null;
        for (int i = 0; i < childs.size(); i++) {
            CrossHeader temp_node = (CrossHeader) childs.get(i);
            boolean showNode = true;
            if (targetFilterMap != null) {
                Iterator<ResultFilter> it = targetFilterMap.values().iterator();
                while (it.hasNext()) {
                    if (!it.next().showNode(temp_node, targetsMap, null)) {
                        showNode = false;
                        break;
                    }
                }
            }
            if (showNode) {
                CrossHeader child = temp_node.createTargetFilterNodeWithTopValue(targetFilterMap, targetsMap);
                if (child == null) {
                    continue;
                }
                if (tempNode != null) {
                    CubeReadingUtils.setSibing(tempNode, child);
                }
                newnode.addChild(child);
                tempNode = child;
            }
        }
        if (tempNode == null && childs.size() > 0) {
            return newnode;
        }
        return newnode;
    }

    /**
     * 创建值排序后的左节点
     *
     * @param targetSort ??这是什么 @pony
     * @param targetsMap 所有的指标
     * @param top        上节点
     * @return 排序后的左方节点
     */
    public CrossHeader createSortedNodeWithTopValue(NameObject targetSort,
                                                    Map<String, TargetGettingKey> targetsMap, CrossHeader top) {
        return createTargetSortedNodeWithTopValue(targetSort, targetsMap, top);
    }

    private CrossHeader createTargetSortedNodeWithTopValue(NameObject targetSort,
                                                           Map<String, TargetGettingKey> targetsMap, CrossHeader top) {
        CrossHeader newnode = createNewNode();
        if (value == null) {
            return newnode;
        }
        newnode.value = this.value.cloneWithTopChildNode(top);
        ChildsMap childs = this.childs;
        CrossHeader tempNode = null;
        String sort_target = targetSort.getName();
        List<CrossHeader> childNodes = childs.getNodeList();
        final TargetGettingKey target_key = sort_target != null ? targetsMap.get(sort_target) : null;
        final int sortType = (Integer) targetSort.getObject();
        if (target_key != null) {
            Collections.sort(childNodes, new Comparator<CrossHeader>() {
                @Override
                public int compare(CrossHeader o1, CrossHeader o2) {
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
            CrossHeader temp_node = childNodes.get(i);
            CrossHeader child = temp_node.createTargetSortedNodeWithTopValue(targetSort, targetsMap, top);
            //清除兄弟关系
            temp_node.setSibling(null);
            if (tempNode != null) {
                CubeReadingUtils.setSibing(tempNode, child);
            }
            newnode.addChild(child);
            tempNode = child;
        }
        return newnode;
    }

    /**
     * 根据排序规则创建新节点
     *
     * @param rowDimension 维度
     * @param targetsMap   所有指标
     * @param top          上位置
     * @return 新节点
     */
    public CrossHeader createSortedNodeWithTopValue(BIDimension[] rowDimension,
                                                    Map<String, TargetGettingKey> targetsMap, CrossHeader top) {
        return createSortedNodeWithTopValue(0, rowDimension, targetsMap, top);
    }

    private CrossHeader createSortedNodeWithTopValue(int index, BIDimension[] rowDimension,
                                                     Map<String, TargetGettingKey> targetsMap, CrossHeader top) {

        CrossHeader newnode = createNewNode();
        newnode.value = this.value.cloneWithTopChildNode(top);
        if (rowDimension.length == index) {
            return newnode;
        }

        ChildsMap childs = this.childs;
        CrossHeader tempNode = null;
        String sort_target = rowDimension[index].getSortTarget();
        List<CrossHeader> childNodes = childs.getNodeList();
        final TargetGettingKey target_key = sort_target != null ? targetsMap.get(sort_target) : null;
        final int sortType = rowDimension[index].getSortType();
        if (target_key != null) {
            Collections.sort(childNodes, new Comparator<CrossHeader>() {
                @Override
                public int compare(CrossHeader o1, CrossHeader o2) {
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
            CrossHeader temp_node = childNodes.get(i);
            CrossHeader child = temp_node.createSortedNodeWithTopValue(index + 1, rowDimension, targetsMap, top);
            //清除兄弟关系
            temp_node.setSibling(null);
            if (tempNode != null) {
                CubeReadingUtils.setSibing(tempNode, child);
            }
            newnode.addChild(child);
            tempNode = child;
        }
        return newnode;
    }

    /**
     * 创建排序的节点
     *
     * @param index       位置
     * @param sortType    排序规则
     * @param sort_target 排序指标
     * @param targetsMap  所有指标
     * @return
     */
    protected CrossHeader createSortedNodeWithTopValue(final int[] index, final int sortType, String sort_target, Map<String, TargetGettingKey> targetsMap) {
        CrossHeader newnode = createNewNode();
        newnode.value = this.value.cloneWithTopChildNode();
        ChildsMap childs = this.childs;
        CrossHeader tempNode = null;
        List<CrossHeader> childNodes = childs.getNodeList();
        final TargetGettingKey target_key = sort_target != null ? targetsMap.get(sort_target) : null;
        if (target_key != null) {
            Collections.sort(childNodes, new Comparator<CrossHeader>() {
                @Override
                public int compare(CrossHeader o1, CrossHeader o2) {
                    Number v1 = o1.getBottomValue(index, target_key);
                    Number v2 = o2.getBottomValue(index, target_key);
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
            CrossHeader temp_node = childNodes.get(i);
            CrossHeader child = temp_node.createSortedNodeWithTopValue(index, sortType, sort_target, targetsMap);
            //清除兄弟关系
            temp_node.setSibling(null);
            if (tempNode != null) {
                CubeReadingUtils.setSibing(tempNode, child);
            }
            newnode.addChild(child);
            tempNode = child;
        }
        return newnode;
    }

    /**
     * 创建只有key值的新左节点
     *
     * @param key 值的key
     * @return 注释
     */
    @Override
    public CrossHeader createNewTargetValueNode(TargetGettingKey key) {
        CrossHeader n = createNewNode();
        n.value = value.createTopChildNewTargetValueNode(key);
        int clen = childs.size();
        CrossHeader tempNode = null;
        for (int i = 0; i < clen; i++) {
            CrossHeader temp_node = (CrossHeader) childs.get(i);
            CrossHeader child = temp_node.createNewTargetValueNode(key);
            if (tempNode != null) {
                CubeReadingUtils.setSibing(tempNode, child);
            }
            n.addChild(child);
            tempNode = child;
        }
        return n;
    }

    /**
     * 把null变成0
     *
     * @param keys 所有keys
     */
    public void clearNullSummary(TargetGettingKey[] keys) {
        if (this.getChildLength() == 0) {
            return;
        }
        int childLen = childs.size();
        for (int i = 0; i < childLen; i++) {
            ((CrossHeader) childs.get(i)).clearNullSummary(keys);
        }
        this.value.clearNullSummary(keys);
    }


    /**
     * 克隆新的值
     *
     * @return 注释
     */
    @Override
    public Node createCloneNodeWithValue() {
        CrossHeader node = (CrossHeader) super.createCloneNodeWithValue();
        node.setValue(getValue());
        return node;
    }

    /**
     * 克隆新的值
     *
     * @return 注释
     */
    @Override
    public Node createCloneNodeWithValue(int start, int end) {
        CrossHeader node = (CrossHeader) super.createCloneNodeWithValue(start, end);
        node.setValue(getValue());
        return node;
    }

    /**
     * 获取第几页的值
     *
     * @param page 页码
     * @return 注释
     */
    @Override
    public Node createPageNode(int page) {
        CrossHeader node = (CrossHeader) super.createPageNode(page);
        node.setValue(getValue());
        return node;
    }

    /**
     * 获取第几页的值
     *
     * @param page 页码
     * @return 注释
     */
    @Override
    public Node createTopPageNode(int page) {
        CrossHeader node = (CrossHeader) super.createTopPageNode(page);
        node.setValue(getValue());
        return node;
    }

    /**
     * 创建node的前count个
     *
     * @param count 个数
     * @return 一个新的node
     */
    @Override
    public Node createBeforeCountNode(int count) {
        CrossHeader node = (CrossHeader) super.createBeforeCountNode(count);
        node.setValue(getValue());
        return node;
    }

    /**
     * 创建node的后count个
     *
     * @param count 个数
     * @return 一个新的node
     */
    @Override
    public Node createAfterCountNode(int count) {
        CrossHeader node = (CrossHeader) super.createAfterCountNode(count);
        node.setValue(getValue());
        return node;
    }

    /**
     * 合并两个
     *
     * @param node1 新的
     * @param shift 偏移量
     * @return 新的node
     */
    public Node createShiftCountNode(CrossHeader node1, int shift) {
        CrossHeader node = (CrossHeader) super.createShiftCountNode(node1, shift);
        node.setValue(getValue());
        return node;
    }


    /**
     * 注释
     *
     * @param keys                注
     * @param dimensions          住
     * @param index               主
     * @param isSummaryCalculated 注
     * @return 注释 注
     * @throws com.fr.json.JSONException
     */

    public JSONObject toJSONObject(BIDimension[] dimensions, TargetGettingKey[] keys, int index, boolean isSummaryCalculated) throws JSONException {
        JSONObject jo = new JSONObject();
        if (getData() != null) {
            jo.put("n", dimensions[index].toString(getData()));
        }
        if (isSummaryCalculated) {
            jo.put("s", value.toJSONObject(keys));
        }
        int childsSize = childs.size();
        if (childsSize > 0) {
            JSONArray children = new JSONArray();
            for (int i = 0; i < childsSize; i++) {
                children.put(((CrossHeader) childs.get(i)).toJSONObject(dimensions, keys, index + 1, isSummaryCalculated));
            }
            jo.put("c", children);
        } else if (!isSummaryCalculated) {
            JSONArray children = new JSONArray();
            for (int i = 0; i < keys.length; i++) {
                JSONObject target = new JSONObject();
                target.put("n", keys[i].getTargetName());
                children.put(target);
            }
            if (children.length() > 0) {
                jo.put("c", children);
            }
        }
        return jo;
    }
}