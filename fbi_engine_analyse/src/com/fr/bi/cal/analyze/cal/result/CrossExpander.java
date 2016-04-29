/**
 *
 */
package com.fr.bi.cal.analyze.cal.result;

import com.fr.json.JSONException;
import com.fr.json.JSONObject;



public class CrossExpander {

    public static final CrossExpander ALL_EXPANDER = new CrossExpander(NodeExpander.ALL_EXPANDER, NodeExpander.ALL_EXPANDER);


    private NodeExpander x = new NodeExpander();

    private NodeExpander y = new NodeExpander();

    public CrossExpander() {

    }

    public CrossExpander(NodeExpander x, NodeExpander y) {
        this.x = x;
        this.y = y;
    }

    public NodeExpander getXExpander() {
        return x;
    }

    public NodeExpander getYExpander() {
        return y;
    }

    /**
     * 解析
     *
     * @param jo json对象
     * @throws com.fr.json.JSONException
     */
    public void parseJSON(JSONObject jo) throws JSONException {
        x = new NodeExpander();
        y = new NodeExpander();
        if (jo.has("x")) {
            x.parseJSON(jo.getJSONArray("x"));
        }
        if (jo.has("y")) {
            y.parseJSON(jo.getJSONArray("y"));
        }
    }

    /**
     * 创建全部展开节点
     *
     * @param row 行号
     * @param col 列号
     */
    public void createXexpander(int col) {
        x.createAllexpander(col - 1);
    }

    public void createYexpander(int row) {
        y.createAllexpander(row - 1);
    }
}