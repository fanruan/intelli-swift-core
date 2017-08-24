package com.fr.bi.cal.analyze.cal.result;

import com.fr.bi.conf.report.widget.field.dimension.BIDimension;
import com.fr.bi.report.key.TargetGettingKey;
import com.fr.bi.report.key.XTargetGettingKey;
import com.fr.bi.report.result.BIXLeftNode;
import com.fr.bi.stable.utils.BICollectionUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

/**
 * 交叉表的横向Node
 * Created by 小灰灰 on 2017/7/7.
 */
public class XLeftNode extends Node implements BIXLeftNode {

    //每个指标根据根节点的过滤条件保存过滤
    private Number[][] xValue;

    public XLeftNode(int sumLength, int topLen) {

        super(sumLength);
        xValue = new Number[sumLength][topLen];
    }

    public XLeftNode(Object data, int sumLength, int topLen) {

        super(data, sumLength);
        xValue = new Number[sumLength][topLen];
    }

    public void setXValue(XTargetGettingKey key, Number sumValue) {

        xValue[key.getTargetIndex()][key.getSubIndex()] = sumValue;
    }

    public void setXValue(Number[][] xValue) {

        this.xValue = xValue;
    }


    @Override
    public void setSummaryValue(TargetGettingKey key, Number value) {

        if (key instanceof XTargetGettingKey) {
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

        if (xValue == null || xValue.length - 1 < key.getTargetIndex()) {
            return null;
        }
        Number[] xv = xValue[key.getTargetIndex()];
        return xv[xv.length - 1];
    }

    public Number getXValue(XTargetGettingKey key) {

        return xValue[key.getTargetIndex()][key.getSubIndex()];
    }

    public Number[][] getXValue() {

        return xValue;
    }

    public JSONObject toJSONObject(BIDimension[] dimensions, TargetGettingKey[] keys, Node topIndex, int index) throws JSONException {

        JSONObject jo = JSONObject.create();
        if (index > -1) {
            jo.put("n", dimensions[index].toString(getData()));
        }
        jo.put("s", toJSONObject(topIndex, keys));
        int childSize = childs.size();
        if (childSize > 0) {
            JSONArray children = JSONArray.create();
            for (int i = 0; i < childSize; i++) {
                children.put(((XLeftNode) (childs.get(i))).toJSONObject(dimensions, keys, topIndex, index + 1));
            }
            jo.put("c", children);
        }
        return jo;
    }

    private JSONObject toJSONObject(Node topIndex, TargetGettingKey[] keys) throws JSONException {

        JSONObject jo = JSONObject.create();
        JSONArray summary = JSONArray.create();
        jo.put("s", summary);
        for (int i = 0; i < keys.length; i++) {
            Integer index = (Integer) topIndex.getData();
            if (index != null) {
                summary.put(BICollectionUtils.cubeValueToWebDisplay(xValue[keys[i].getTargetIndex()][index]));
            } else {
                summary.put("--");
            }
        }
        if (keys.length == 0) {
            summary.put("--");
        }
        if (topIndex.getChildLength() > 0) {
            JSONArray child = JSONArray.create();
            jo.put("c", child);
            for (int i = 0; i < topIndex.getChildLength(); i++) {
                child.put(toJSONObject(topIndex.getChild(i), keys));
            }
        }
        return jo;
    }

    public Number[] getSubValues(XTargetGettingKey key) {

        Number[] v = new Number[xValue.length];
        for (int i = 0; i < v.length; i++) {
            v[i] = xValue[i][key.getSubIndex()];
        }
        return v;
    }
}
