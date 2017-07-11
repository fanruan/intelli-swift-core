package com.fr.bi.cal.analyze.cal.result;

import com.fr.bi.conf.report.widget.field.dimension.BIDimension;
import com.fr.bi.report.key.TargetGettingKey;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

/**
 * Created by 小灰灰 on 2017/7/6.
 */
public class XNode {
    private Node top;
    private Node left;

    public XNode(Node top, Node left) {
        this.top = top;
        this.left = left;
    }

    public Node getLeft() {
        return left;
    }

    public Node getTop() {
        return top;
    }

    public JSONObject toJSONObject(BIDimension[] rowDimension, BIDimension[] colDimension, TargetGettingKey[] keys) throws JSONException {
        JSONObject jo = JSONObject.create();
        jo.put("l", left.toJSONObject(rowDimension, keys, -1));
        jo.put("t", top.toTopJSONObject(colDimension, keys, -1));
        return jo;
    }
}
