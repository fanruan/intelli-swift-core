package com.fr.swift.result.node.xnode;

import com.fr.swift.query.aggregator.AggregatorValue;
import com.fr.swift.result.GroupNode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lyon on 2018/4/9.
 */
public class XLeftNode extends GroupNode<XLeftNode> {

    /**
     * 交叉表的行结构
     * getTargetLength 指标的个数
     * topGroupByRowCount 表头维度groupBy结果的行数，包括汇总行
     * xValues为AggregatorValue[getTargetLength][topGroupByRowCount]
     */
    private AggregatorValue[][] xValues;

    /**
     * valueArrayList为List<AggregatorValue[getTargetLength]>
     * 用于保存临时结果valueArrayList.size() = topGroupByRowCount
     */
    private List<AggregatorValue[]> valueArrayList;

    public XLeftNode(int deep, Object data) {
        super(deep, data);
    }

    public AggregatorValue[][] getXValue() {
        return xValues;
    }

    public void setXValues(List<AggregatorValue[]> valueArrayList) {
        this.xValues = create2DArrayValue(valueArrayList);
    }

    /**
     * 用来处理交叉表的计算指标
     * @return
     */
    public List<AggregatorValue[]> getValueArrayList() {
        return valueArrayList;
    }

    public void setValueArrayList(List<AggregatorValue[]> valueArrayList) {
        this.valueArrayList = valueArrayList;
    }

    /**
     * 根据表头groupBy结果的行号取对应的指标
     *
     * @param row
     * @return
     */
    public AggregatorValue[] getValuesByTopGroupByRow(int row) {
        assert xValues != null && xValues.length != 0 && xValues[0] != null;
        assert row < xValues[0].length;
        int targetLength = xValues.length;
        AggregatorValue[] values = new AggregatorValue[targetLength];
        for (int i = 0; i < targetLength; i++) {
            values[i] = xValues[i][row];
        }
        return values;
    }

    /**
     * 获取第一个子节点
     *
     * @return
     */
    public XLeftNode getFirstXLeftNode() {
        return getChildrenSize() == 0 ? null : getChild(0);
    }

    public static Number[][] toNumber2DArray(AggregatorValue[][] values) {
        Number[][] result = new Number[values == null ? 0 : values.length][];
        for (int i = 0; i < result.length; i++) {
            result[i] = toNumberArray(values[i]);
        }
        return result;
    }

    private static AggregatorValue[][] create2DArrayValue(List<AggregatorValue[]> valueArrayList) {
        int targetLength = valueArrayList.isEmpty() ? 0 : valueArrayList.get(0).length;
        AggregatorValue[][] result = new AggregatorValue[targetLength][];
        List<List<AggregatorValue>> targetListOfTopGroupByRows = new ArrayList<List<AggregatorValue>>(targetLength);
        for (int i = 0; i < targetLength; i++) {
            targetListOfTopGroupByRows.add(new ArrayList<AggregatorValue>());
        }
        for (int i = 0; i < valueArrayList.size(); i++) {
            // 这个values是表头groupBy的一行对应的结果指标，不管是否存在空值，表头所有行groupBy行对应的指标的长度都相同
            AggregatorValue[] value = valueArrayList.get(i);
            for (int j = 0; j < targetLength; j++) {
                targetListOfTopGroupByRows.get(j).add(value[j]);
            }
        }
        // List转为数组
        for (int i = 0; i < targetLength; i++) {
            List<AggregatorValue> values = targetListOfTopGroupByRows.get(i);
            result[i] = values.toArray(new AggregatorValue[values.size()]);
        }
        return result;
    }
}
