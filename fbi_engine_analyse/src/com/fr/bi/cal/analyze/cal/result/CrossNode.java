/**
 *
 */
package com.fr.bi.cal.analyze.cal.result;

import com.fr.bi.cal.analyze.cal.utils.CubeReadingUtils;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.report.key.TargetGettingKey;
import com.fr.bi.stable.report.result.BICrossNode;
import com.fr.bi.stable.structure.collection.map.ChildsMap;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class CrossNode implements BICrossNode {
    /**
     *
     */
    private static final long serialVersionUID = -8363118907018094766L;

    private CrossHeader head;

    private CrossHeader left;

    private CrossNode topParent;

    private CrossNode leftParent;

    private CrossNode rightSibling;
    private CrossNode bottomSibling;

    private volatile Map summaryValue = new ConcurrentHashMap(1);

    private ChildsMap<CrossNode> topChilds = new ChildsMap<CrossNode>();

    private ChildsMap<CrossNode> leftChilds = new ChildsMap<CrossNode>();

    /**
     * 构造函数
     *
     * @param head 上部分的 CrossHeader
     * @param left 左边的CrossHeader
     */
    public CrossNode(CrossHeader head, CrossHeader left) {
        this.head = head;
        this.left = left;
    }

    /**
     * 设置汇总值
     */
    @Override
    public void setSummaryValue(Object key, Object value) {
        if (value != null) {
            value = ((Number)value).doubleValue();
            summaryValue.put(key, value);
        }
    }

    /**
     * 获取索引用于分组计算
     *
     * @return 索引
     */
    public GroupValueIndex getIndex() {
        return null;
    }

    /**
     * 获取索引用于值计算
     *
     * @return 索引
     */
    @Override
    public GroupValueIndex getIndex4Cal() {
        return null;
    }

    /**
     * 获取汇总的个数
     *
     * @return 个数
     */
    public int getSummarySize() {
        return summaryValue.size();
    }

    /**
     * 获取汇总值的迭代器
     *
     * @return 迭代器用于遍历
     */
    public Iterator getSummaryValueIterator() {
        return summaryValue.entrySet().iterator();
    }

    /**
     * 获取汇总值
     *
     * @param key 汇总值的key
     * @return 汇总的值
     */
    @Override
    public Number getSummaryValue(Object key) {
        return (Number) summaryValue.get(key);
    }

    @Override
	public Map getSummaryValue() {
        return summaryValue;
    }

    /**
     * 获取右边的兄弟节点
     *
     * @return 右边的兄弟节点
     */
    public CrossNode getRightSibling() {
        return this.rightSibling;
    }

    /**
     * 设置右边的兄弟节点
     *
     * @param node 右边的兄弟节点
     */
    public void setRightSibling(CrossNode node) {
        this.rightSibling = node;
    }

    /**
     * 获取下方兄弟节点
     *
     * @return 下方兄弟节点
     */
    @Override
    public CrossNode getBottomSibling() {
        return this.bottomSibling;
    }

    /**
     * 设置下方兄弟节点
     *
     * @param node 下方兄弟节点
     */
    public void setBottomSibling(CrossNode node) {
        this.bottomSibling = node;
    }

    /**
     * 添加下方的子节点
     *
     * @param child 子节点
     */
    public void addTopChild(CrossNode child) {
        child.setTopParent(this);
        if (!topChilds.isEmpty()) {
            CrossNode lastNode = getTopLastChild();
            lastNode.setRightSibling(child);
        }
        topChilds.put(child.head.getData(), child);
    }


    /**
     * 获取下方节点的最后一个值
     *
     * @return 下方节点的最后一个值
     */
    public CrossNode getTopLastChild() {
        return topChilds.getLastValue();
    }

    /**
     * 获取下方节点的第一个值
     *
     * @return 下方节点的第一个值
     */
    public CrossNode getTopFirstChild() {
        return topChilds.getFirstValue();
    }

    /**
     * 获取上父节点
     *
     * @return 上父节点
     */
    public CrossNode getTopParent() {
        return this.topParent;
    }

    /**
     * 设置下方的上父节点
     *
     * @param crossNode
     */
    private void setTopParent(CrossNode crossNode) {
        this.topParent = crossNode;
    }

    /**
     * 获取左父节点
     *
     * @return 左父节点
     */
    @Override
    public CrossNode getLeftParent() {
        return this.leftParent;
    }

    /**
     * 设置右父
     *
     * @param crossNode
     */
    private void setLeftParent(CrossNode crossNode) {
        this.leftParent = crossNode;
    }

    /**
     * 添加右子节点
     *
     * @param child 右子节点
     */
    public void addLeftChild(CrossNode child) {
        child.setLeftParent(this);
        if (!leftChilds.isEmpty()) {
            CrossNode lastNode = getLeftLastChild();
            lastNode.setBottomSibling(child);
        }
        leftChilds.put(child.left.getData(), child);
    }

    /**
     * 获取右子最后一个节点
     *
     * @return 右子最后一个节点
     */
    public CrossNode getLeftLastChild() {
        return leftChilds.getLastValue();
    }


    /**
     * 获取右子第一个节点
     *
     * @return 右子第一个节点
     */
    @Override
    public CrossNode getLeftFirstChild() {
        return leftChilds.getFirstValue();
    }

    /**
     * 转成string显示
     *
     * @return 蠢货，toString return string 不然return尼玛啊
     */
    @Override
    public String toString() {
        return "top:" + head.getData() + "   left:" + left.getData();
    }

    /**
     * 获取右边子的长度
     *
     * @return 获取右边子的长度
     */
    @Override
    public int getLeftChildLength() {
        return leftChilds.size();
    }

    /**
     * 获取下方子的长度
     *
     * @return 下方子的长度
     */
    public int getTopChildLength() {
        return topChilds.size();
    }

    /**
     * 根据位置获取右边的子节点
     *
     * @param index 第几个
     * @return 右边子节点
     */
    @Override
    public CrossNode getLeftChild(int index) {
        if (index < getLeftChildLength()) {
            return leftChilds.get(index);
        }
        return null;
    }

    /**
     * 根据位置获取下方的子节点
     *
     * @param index 第几个
     * @return 下方的子节点
     */
    public CrossNode getTopChild(int index) {
        if (index < getTopChildLength()) {
            return topChilds.get(index);
        }
        return null;
    }

    /**
     * 根据值获取下方的子节点
     *
     * @param v 值
     * @return 下方的子节点
     */
    public CrossNode getTopChild(Object v) {
        return topChilds.get(v);
    }

    /**
     * 根据值获取右边的子节点
     *
     * @param v 值
     * @return 右边的子节点
     */
    @Override
    public CrossNode getLeftChildByKey(Object v) {
        return leftChilds.get(v);
    }

    /**
     * 获取上head值
     *
     * @return 上head值
     */
    public CrossHeader getHead() {
        return head;
    }

    /**
     * 获取左head值
     *
     * @return 左head值
     */
    @Override
    public CrossHeader getLeft() {
        return left;
    }


    //FIXME 这里的如果是过滤需要重新设置一下crossNode的值

    /**
     * 根据head clone 下方边节点的Node
     *
     * @param top 父head用于过滤
     * @return clone 下方边节点的Node
     */
    public CrossNode cloneWithTopChildNode(CrossHeader top) {
        CrossNode n = new CrossNode(this.head, this.left);
        n.summaryValue.putAll(this.summaryValue);
        CrossNode t = null;
        for (int i = 0; i < top.getChildLength(); i++) {
            Object v = top.getChild(i).getData();
            CrossNode child = this.getTopChild(v).cloneWithTopChildNode((CrossHeader) top.getChild(i));
            n.addTopChild(child);
            if (t != null) {
                CubeReadingUtils.setSibing(t, child);
            }
            t = child;
        }
        return n;
    }


    /**
     * 只clone 下方节点的Node
     *
     * @return 只clone 下方节点的Node
     */
    public CrossNode cloneWithTopChildNode() {
        CrossNode n = new CrossNode(this.head, this.left);
        n.summaryValue.putAll(this.summaryValue);
        CrossNode t = null;
        for (int i = 0; i < this.getTopChildLength(); i++) {
            CrossNode child = this.getTopChild(i).cloneWithTopChildNode();
            n.addTopChild(child);
            if (t != null) {
                CubeReadingUtils.setSibing(t, child);
            }
            t = child;
        }
        return n;
    }

    /**
     * 将left1中的所有值和 left2中的key为key2的值合并在一起
     *
     * @param left1 节点1
     * @param left2 节点2
     * @param key2  节点2的key
     */
    public void mergeValue(CrossNode left1, CrossNode left2, TargetGettingKey key2) {
        if (left1 != null) {
            this.summaryValue.putAll(left1.summaryValue);
        }
        if (left2 != null) {
            Object v = left2.getSummaryValue(key2);
            if (v != null) {
                this.summaryValue.put(key2, v);
            }
        }
    }


    /**
     * 创建只有key的下方子的值
     *
     * @param key 值key
     * @return 只有key的下方子的值
     */
    public CrossNode createTopChildNewTargetValueNode(TargetGettingKey key) {
        CrossNode n = new CrossNode(this.head, this.left);
        if (key != null) {
            Object value = getSummaryValue(key.getTargetKey());
            if (value != null) {
                n.setSummaryValue(key, value);
            }
        }
        int clen = topChilds.size();
        CrossNode tempNode = null;
        for (int i = 0; i < clen; i++) {
            CrossNode temp_node = topChilds.get(i);
            CrossNode child = temp_node.createTopChildNewTargetValueNode(key);
            if (tempNode != null) {
                CubeReadingUtils.setSibing(tempNode, child);
            }
            n.addTopChild(child);
            tempNode = child;
        }
        return n;
    }


    public void clearNullSummary(TargetGettingKey[] keys) {
        for (int j = 0; j < topChilds.size(); j++) {
            topChilds.get(j).clearNullSummary(keys);
        }
        for (TargetGettingKey key : keys) {
            if (this.getSummaryValue(key) == null) {
                this.setSummaryValue(key, 0);
            }
        }

    }

    /**
     * 注释
     *
     * @param keys 注
     * @return 注释
     * @throws com.fr.json.JSONException
     */
    public JSONObject toJSONObject(TargetGettingKey[] keys) throws JSONException {
        JSONObject jo = new JSONObject();
        JSONArray summary = new JSONArray();
        jo.put("s", summary);
        for (int i = 0; i < keys.length; i++) {
            summary.put(getSummaryValue(keys[i]));
        }
        if(keys.length == 0){
            summary.put("--");
        }
        if (topChilds.size() > 0) {
            JSONArray child = new JSONArray();
            jo.put("c", child);
            for (int i = 0; i < topChilds.size(); i++) {
                child.put(topChilds.get(i).toJSONObject(keys));
            }
        }
        return jo;
    }

    @Override
    public GroupValueIndex getIndex4CalByTargetKey(TargetGettingKey key) {

        return null;
    }
}