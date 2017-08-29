package com.fr.bi.cal.analyze.cal.sssecret;

import com.fr.bi.report.result.BICrossLeftNode;
import com.fr.bi.report.result.BIXLeftNode;
import com.fr.bi.report.key.TargetGettingKey;
import com.fr.bi.report.key.XTargetGettingKey;
import com.fr.bi.stable.gvi.GroupValueIndex;

import java.util.Comparator;

/**
 * Created by 小灰灰 on 2017/7/13.
 */
public class XMetricMergeResult extends MetricMergeResult implements BIXLeftNode {
    //每个指标根据根节点的过滤条件保存过滤
    private Number[][] xValue;
    public XMetricMergeResult(Object data, int sumLen, GroupValueIndex[] gvis, int topLen) {
        super(data, sumLen, gvis);
        xValue = new Number[sumLen][topLen];
    }

    public XMetricMergeResult(Comparator c, Object data, int sumLen, GroupValueIndex[] gvis, int topLen) {
        super(c, data, sumLen, gvis);
        xValue = new Number[sumLen][topLen];
    }

    public void setXValue(XTargetGettingKey key, Number sumValue) {
        if (key.getTargetIndex() < xValue.length){
            xValue[key.getTargetIndex()][key.getSubIndex()] = sumValue;
        }
    }

    public Number[][] getXValue(){
        return xValue;
    }

    @Override
    public void setXValue(Number[][] xValue) {
        this.xValue = xValue;
    }

    @Override
    public void setSummaryValue(TargetGettingKey key, Number value) {
        if (key instanceof XTargetGettingKey){
            setXValue((XTargetGettingKey) key, value);
        } else {
            super.setSummaryValue(key, value);
        }
    }

    @Override
    public Number getSummaryValue(TargetGettingKey key) {
        if (key instanceof XTargetGettingKey) {
            return getXValue((XTargetGettingKey) key);
        }
        return getRootValue(key);
    }

    private Number getRootValue(TargetGettingKey key) {
        if (xValue == null || xValue.length - 1 < key.getTargetIndex()){
            return null;
        }
        Number[] xv = xValue[key.getTargetIndex()];
        return xv[xv.length - 1];
    }

    public Number getXValue(XTargetGettingKey key) {
        if (xValue == null || xValue.length - 1 < key.getTargetIndex()){
            return null;
        }
        return xValue[key.getTargetIndex()][key.getSubIndex()];
    }

    public Number[] getSubValues(XTargetGettingKey key) {

        return getSubValues(key.getSubIndex());
    }

    @Override
    public Number[] getSubValues(int index) {

        Number[] v = new Number[xValue.length];
        for (int i = 0; i < v.length; i++) {
            v[i] = xValue[i][index];
        }
        return v;
    }

    @Override
    public BICrossLeftNode getFirstCrossLeftNode() {

        return (BICrossLeftNode)getFirstChild();
    }
}
