/**
 *
 */
package com.fr.bi.cal.analyze.cal.result;

import com.fr.bi.conf.report.widget.field.dimension.BIDimension;
import com.fr.bi.conf.report.widget.field.dimension.filter.ResultFilter;
import com.fr.bi.stable.report.key.TargetGettingKey;
import com.fr.bi.stable.report.result.TargetCalculator;
import com.fr.general.NameObject;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

import java.util.Map;

/**
 * @author Daniel
 *         交叉步表的根节点
 */
public class NewCrossRoot {
    private CrossHeader left;

    private CrossHeader top;

    /**
     * 构造函数
     *
     * @param left 左边节点
     * @param top  上方节点
     */
    public NewCrossRoot(CrossHeader left, CrossHeader top) {
        this.left = left;
        this.top = top;
    }

    /**
     * 获取左边节点
     *
     * @return
     */
    public CrossHeader getLeft() {
        return left;
    }

    /**
     * 获取上方节点
     *
     * @return
     */
    public CrossHeader getTop() {
        return top;
    }


    /**
     * 合并两个NewCrossNode与this
     *
     * @param root 被合并的对象
     * @param key2 被合并对象的key
     * @return 合并后的新对象
     */
    public NewCrossRoot OrMerge(NewCrossRoot root, TargetGettingKey key2) {
        if (root == null) {
            return this;
        }
        CrossHeader left = (CrossHeader) this.left.OrMerge(root.left);
        CrossHeader top = (CrossHeader) this.top.OrMerge(root.top);
        NewCrossRoot res = new NewCrossRoot(left, top);
        res.OrMergeValue(this, root, key2);
        return res;
    }

    /**
     * @param
     * @param
     */
    private void OrMergeValue(NewCrossRoot root1, NewCrossRoot root2, TargetGettingKey key2) {
        CrossNode baseNode = new CrossNode(left, top);
        left.setValue(baseNode);
        top.setValue(baseNode);
        CrossHeader left1 = root1.left;
        CrossHeader left2 = root2.left;
        baseNode.mergeValue(left1.getValue(), left2.getValue(), key2);
        top.dealWithChildTop4Merge(left, baseNode, left1.getValue(), left2.getValue(), key2);
        left.dealWithChildLeft4Merge(top, baseNode, left1, left2, key2);
        left.buildLeftRelation(top);
    }

    /**
     * 维度过滤
     *
     * @param rowDimension 行维度
     * @param colDimension 列维度
     * @param targetsMap   所有指标
     * @return 新的节点
     */
    public NewCrossRoot createResultFilterNodeWithTopValue(BIDimension[] rowDimension, BIDimension[] colDimension,
                                                           Map<String, TargetCalculator> targetsMap) {
        CrossHeader top = (CrossHeader) this.top.createResultFilterNode(colDimension, targetsMap);
        CrossHeader left = this.left.createResultFilterNodeWithTopValue(rowDimension, targetsMap, top);
        left.buildLeftRelation(top);
        return new NewCrossRoot(left, top);

    }

    /**
     * 获取值过滤新节点
     *
     * @param targetFilterMap 过滤信息
     * @param targetsMap      所有指标
     * @return 新节点
     */
    public NewCrossRoot createResultFilterNodeWithTopValue(Map<String, ResultFilter> targetFilterMap,
                                                           Map<String, TargetCalculator> targetsMap) {
        if (targetFilterMap == null || targetFilterMap.isEmpty()) {
            return this;
        }
        CrossHeader left = this.left.createResultFilterNodeWithTopValue(targetFilterMap, targetsMap);
        CrossHeader top = (CrossHeader) this.top.createCloneNode();
        left.buildLeftRelation(top);
        return new NewCrossRoot(left, top);

    }

    /**
     * 维度排序
     *
     * @param rowDimension 行维度
     * @param colDimension 列维度
     * @param targetsMap   所有指标
     * @return 新节点
     */
    public NewCrossRoot createSortedNode(BIDimension[] rowDimension, BIDimension[] colDimension,
                                         Map<String, TargetGettingKey> targetsMap) {
        CrossHeader top = (CrossHeader) this.top.createSortedNode(colDimension, targetsMap);
        CrossHeader left = this.left.createSortedNodeWithTopValue(rowDimension, targetsMap, top);
        left.buildLeftRelation(top);
        return new NewCrossRoot(left, top);
    }

    /**
     * 获取排序的节点
     *
     * @param targetSort 排序信息
     * @param targetsMap 所有指标
     * @return 新节点
     */
    public NewCrossRoot createSortedNode(NameObject targetSort,
                                         Map<String, TargetGettingKey> targetsMap) {
        CrossHeader top = (CrossHeader) this.top.createSortedNode(targetSort, targetsMap);
        CrossHeader left = this.left.createSortedNodeWithTopValue(targetSort, targetsMap, top);
        left.buildLeftRelation(top);
        return new NewCrossRoot(left, top);
    }

    /**
     * 获取分页的节点
     *
     * @param page 页码
     * @return 新节点
     */
    public NewCrossRoot createPageNode(int page) {
        CrossHeader left = (CrossHeader) this.left.createPageNode(page);
        CrossHeader top = (CrossHeader) this.top.createCloneNodeWithValue();
        return new NewCrossRoot(left, top);
    }

    public NewCrossRoot createCrossPageNode(int vpage) {
        CrossHeader left = (CrossHeader) this.left.createPageNode(vpage);
        CrossHeader top = (CrossHeader) this.top.createCloneNodeWithValue();
        return new NewCrossRoot(left, top);
    }

    /**
     * 创建node的前count个
     *
     * @param count 个数
     * @return 一个新的node
     */
    public NewCrossRoot createBeforeCountNode(int count) {
        CrossHeader left = (CrossHeader) this.left.createBeforeCountNode(count);
        CrossHeader top = (CrossHeader) this.top.createCloneNodeWithValue();
        return new NewCrossRoot(left, top);
    }

    /**
     * 从当前root里面获取后 20 - shift个，root2里面获取前shift，构建新的root
     *
     * @param root2 后一个root
     * @param shift 偏移量
     * @return 新的noderoot
     */
    public NewCrossRoot createShiftRootNode(NewCrossRoot root2, int shift) {
        CrossHeader top = (CrossHeader) this.top.createCloneNodeWithValue();
        CrossHeader left = null;

        if (root2 == null) {
            left = (CrossHeader) this.left.createAfterCountNode(Node.PAGE_PER_GROUP - shift);

        } else {
            left = (CrossHeader) this.left.createShiftCountNode(root2.getLeft(), shift);
        }
        return new NewCrossRoot(left, top);
    }

    /**
     * 获取第几页偏移shift个单位的root
     *
     * @param page  页码
     * @param shift 偏移量
     * @return 新节点
     */
    public NewCrossRoot createPageNode(int page, int shift) {
        CrossHeader left = (CrossHeader) this.left.createPageNode(page, shift);
        CrossHeader top = (CrossHeader) this.top.createCloneNodeWithValue();
        return new NewCrossRoot(left, top);
    }

    /**
     * 根据纵向某列进行排序
     *
     * @param index       位置
     * @param sortType    排序规则
     * @param sort_target 排序指标
     * @param targetsMap  所有指标
     * @return 新节点
     */
    public NewCrossRoot sortByResult(final int[] index, final int sortType, String sort_target, Map<String, TargetGettingKey> targetsMap) {
        CrossHeader left = this.left.createSortedNodeWithTopValue(index, sortType, sort_target, targetsMap);
        CrossHeader top = (CrossHeader) this.top.createCloneNode();
        left.buildLeftRelation(top);
        return new NewCrossRoot(left, top);
    }

    /**
     * 根据key创建新指标节点
     *
     * @param targetGettingKey key值
     * @return 新节点
     */
    public NewCrossRoot createNewTargetValueNode(
            TargetGettingKey targetGettingKey) {
        CrossHeader left = this.left.createNewTargetValueNode(targetGettingKey);
        CrossHeader top = (CrossHeader) this.top.createCloneNode();
        left.buildLeftRelation(top);
        return new NewCrossRoot(left, top);
    }


    /**
     * 注释
     *
     * @param keys           注
     * @param leftDimensions 主
     * @param topDimensions  主
     * @return 注释
     * @throws com.fr.json.JSONException
     */
    public JSONObject toJSONObject(BIDimension[] leftDimensions, BIDimension[] topDimensions, TargetGettingKey[] keys) throws JSONException {
        JSONObject jo = new JSONObject();
        jo.put("l", left.toJSONObject(leftDimensions, keys, -1, true));
        jo.put("t", top.toJSONObject(topDimensions, keys, -1, false));
        return jo;
    }

}