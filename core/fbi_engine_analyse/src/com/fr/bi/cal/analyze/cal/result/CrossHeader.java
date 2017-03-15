/**
 *
 */
package com.fr.bi.cal.analyze.cal.result;

import com.fr.bi.cal.analyze.cal.utils.CubeReadingUtils;
import com.fr.bi.conf.report.widget.field.dimension.BIDimension;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.report.key.TargetGettingKey;
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

    public CrossHeader(){

    }

    /**
     * 构造函数
     *
     * @param data   值
     */
    public CrossHeader(Comparator comparator, Object data) {
        super(comparator, data);
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
        CrossHeader header = new CrossHeader(this.getComparator(), this.getData());
        header.setShowValue(getShowValue());
        return header;
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
                CubeReadingUtils.setSibling(t, child);
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
                CubeReadingUtils.setSibling(temp, node);
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
                CubeReadingUtils.setSibling(temp, node);
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
                CubeReadingUtils.setSibling(temp, node);
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
                CubeReadingUtils.setSibling(temp, node);
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

    protected Map getNotNullSummaryValue() {
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
                CubeReadingUtils.setSibling(tempNode, child);
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
                CubeReadingUtils.setSibling(tempNode, child);
            }
            n.addChild(child);
            tempNode = child;
        }
        return n;
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
        if(index > -1){
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