package com.fr.bi.cal.analyze.cal.result;

import com.fr.bi.stable.constant.BIJSONConstant;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by sheldon on 14-9-11.
 * 创建复杂报表的expander
 */
public class ComplexExpander {
    public static final CrossExpander ALL_EXPANDER = new CrossExpander(NodeExpander.ALL_EXPANDER, NodeExpander.ALL_EXPANDER);


    private ArrayList<NodeExpander> x_expanders = new ArrayList<NodeExpander>();
    private ArrayList<NodeExpander> y_expanders = new ArrayList<NodeExpander>();
    private int columnLength = 0;
    private int rowLength = 0;


    public ComplexExpander() {

    }

    public ComplexExpander(int columnLength, int rowLength) {
        this.columnLength = columnLength;
        this.rowLength = rowLength;
    }

    public NodeExpander getXExpander(int regionIndex) {

        if (regionIndex < x_expanders.size()) {
            return x_expanders.get(regionIndex);
        } else {
            x_expanders.add(new NodeExpander());
            return getXExpander(regionIndex);
        }
    }

    public NodeExpander getYExpander(int regionIndex) {

        if (regionIndex < y_expanders.size()) {
            return y_expanders.get(regionIndex);
        } else {
            y_expanders.add(new NodeExpander());
            return getYExpander(regionIndex);
        }
    }


    /**
     * 根据行列序号创建相应的交叉表expander
     *
     * @param row    行区域
     * @param column 列区域
     * @return 交叉表的expander
     */
    public CrossExpander createCrossNode(int row, int column) {
        return new CrossExpander(getXExpander(column), getYExpander(row));
    }

    /**
     * 解析
     *  {
     *      x: {},
     *      y: {
     *          type: isAll，
     *          value: [[{
     *              name: "",
     *              children: [{name: ""}]
     *          }, {
     *              name: ""
     *          }], [{name: ""}]]
     *      }
     *  }
     * @param jo json对象
     * @throws com.fr.json.JSONException
     */
    public void parseJSON(JSONObject jo) throws JSONException {
        if (jo.has(BIJSONConstant.JSON_KEYS.EXPANDER_X)) {
            JSONObject xjo = jo.getJSONObject(BIJSONConstant.JSON_KEYS.EXPANDER_X);
            JSONArray ja = xjo.getJSONArray(BIJSONConstant.JSON_KEYS.VALUE);
            addNodeExpander(x_expanders, columnLength, ja.length(), xjo.optBoolean(BIJSONConstant.JSON_KEYS.TYPE));
            for (int i = 0; i < ja.length(); i++) {
                NodeExpander x = x_expanders.get(i);
                x.parseJSON(ja.getJSONArray(i));
            }
        }
        if (jo.has(BIJSONConstant.JSON_KEYS.EXPANDER_Y)) {
            JSONObject xjo = jo.getJSONObject(BIJSONConstant.JSON_KEYS.EXPANDER_Y);
            JSONArray ja = xjo.getJSONArray(BIJSONConstant.JSON_KEYS.VALUE);
            addNodeExpander(y_expanders, rowLength, ja.length(), xjo.optBoolean(BIJSONConstant.JSON_KEYS.TYPE));
            for (int i = 0; i < ja.length(); i++) {
                NodeExpander x = y_expanders.get(i);
                x.parseJSON(ja.getJSONArray(i));
            }
        }
    }

    private void addNodeExpander(ArrayList<NodeExpander> nodeExpanders, int expanderLen, int len, boolean isAll) {
        if (nodeExpanders.size() < len) {
            for (int i = nodeExpanders.size(); i < len; i++) {
                NodeExpander expander = new NodeExpander();
                if (isAll){
                    expander.createAllexpander(expanderLen);
                }
                nodeExpanders.add(expander);
            }
        }
    }

}