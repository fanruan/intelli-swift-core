package com.fr.bi.cal.analyze.cal.result;

import com.fr.bi.conf.report.widget.field.dimension.BIDimension;
import com.fr.bi.report.key.TargetGettingKey;
import com.fr.bi.report.key.XTargetGettingKey;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

/**交叉表的横向Node
 * Created by 小灰灰 on 2017/7/7.
 */
public class XLeftNode extends Node {
    private Number[][] xValue;
    public XLeftNode(int sumLength, int topLen) {
        super(sumLength);
        xValue = new Number[sumLength][topLen];
    }

    public XLeftNode(Object data, int sumLength, int topLen) {
        super(data, sumLength);
        xValue = new Number[sumLength][topLen];
    }

    public void setXValue(XTargetGettingKey key, Number sumValue){
        xValue[key.getTargetIndex()][key.getSubIndex()] = sumValue;
    }


    @Override
    public Number getSummaryValue(TargetGettingKey key) {
        return getXValue((XTargetGettingKey) key);
    }

    public Number getXValue(XTargetGettingKey key) {
        return xValue[key.getTargetIndex()][key.getSubIndex()];
    }

    @Override
    public JSONObject toJSONObject(BIDimension[] dimensions, TargetGettingKey[] keys, int index) throws JSONException {
        JSONObject jo = JSONObject.create();
        if(index > -1){
            jo.put("n", dimensions[index].toString(getData()));
        }
        jo.put("s", toJSONObject(keys));
        int childSize = childs.size();
        if (childSize > 0) {
            JSONArray children = JSONArray.create();
            for (int i = 0; i < childSize; i++) {
                children.put( childs.get(i).toJSONObject(dimensions, keys, index + 1));
            }
            jo.put("c", children);
        }
        return jo;
    }

    private JSONObject toJSONObject(TargetGettingKey[] keys) throws JSONException {
        JSONObject jo = new JSONObject();
        JSONArray summary = new JSONArray();
        jo.put("s", summary);
        for (int i = 0; i < keys.length; i++) {
            //summary.put(getSummaryValue(keys[i]));
        }
        if(keys.length == 0){
            summary.put("--");
        }
//        if (topChilds.size() > 0) {
//            JSONArray child = new JSONArray();
//            jo.put("c", child);
//            for (int i = 0; i < topChilds.size(); i++) {
//                child.put(topChilds.get(i).toJSONObject(keys));
//            }
//        }
        return jo;
    }
}
