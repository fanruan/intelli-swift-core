/**
 *
 */
package com.fr.bi.cal.analyze.cal.result;

import com.fr.bi.conf.report.widget.field.dimension.BIDimension;
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