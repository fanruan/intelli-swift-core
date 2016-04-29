package com.fr.bi.cal.analyze.cal.result;

import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 处理节点是否展开的类对象， 与node结构类似
 *
 * @author Daniel
 */
public class NodeExpander {

    public static final NodeAllExpander ALL_EXPANDER = new NodeAllExpander();
    protected NodeExpander parent;
    private boolean expandAll;
    private int len;
    private Map<String, NodeExpander> childExpanderMap = new HashMap<String, NodeExpander>();

    /**
     * 加一个子点
     *
     * @param name 名字
     */
    public void addChild(String name) {
        NodeExpander ne = new NodeExpander();
        ne.setParent(this);
        childExpanderMap.put(name, ne);
    }

    public NodeExpander getParent() {
        return this.parent;
    }

    protected void setParent(NodeExpander parent) {
        this.parent = parent;
    }

    /**
     * 加一个子点
     *
     * @param name         名字
     * @param nodeExpander 节点
     */
    public void addChild(String name, NodeExpander nodeExpander) {
        if (nodeExpander != null) {
            nodeExpander.setParent(this);
        }
        childExpanderMap.put(name, nodeExpander);
    }

    /**
     * 第index节点是否展开
     *
     * @param name 次序
     * @return 是否展开
     */
    public boolean isChildExpand(String name) {
        if (expandAll) {
            NodeExpander child = childExpanderMap.get(name);
            if (child == null) {
                return true;
            }
            if (child.childExpanderMap.isEmpty()) {
                return false;
            }
            return true;
        } else {
            return childExpanderMap.containsKey(name);
        }
    }


    public NodeExpander getChildExpander(String name) {
        if (expandAll) {
            if (childExpanderMap.get(name) == null) {
                if (len <= 0) {
                    return null;
                } else {
                    NodeExpander c = new NodeAllExpander(len - 1, len);
                    c.setParent(this);
                    return c;
                }
            } else {
                if (len <= 1) {
                    return null;
                } else {
                    NodeExpander child = childExpanderMap.get(name);
                    if (child.childExpanderMap.isEmpty()) {
                        return null;
                    }
                    child.createAllexpander(len - 1);
                    return child;
                }

            }
        } else {
            return childExpanderMap.get(name);
        }
    }

    /**
     * 结构为 expander:[{index:0, expander:[{index:0},
     * {index:1, expander:[{index:1}]
     * }]
     * }],[{index:1}]
     *
     * @param ja json结构
     * @throws com.fr.json.JSONException
     */
    public void parseJSON(JSONArray ja) throws JSONException {
        for (int i = 0; i < ja.length(); i++) {
            JSONObject jo = ja.getJSONObject(i);
            if (jo.has("name")) {
                String name = jo.getString("name");
                this.addChild(name);
                if (jo.has("children")) {
                    childExpanderMap.get(name).parseJSON(jo.getJSONArray("children"));
                }
            }
        }
    }

    /**
     * 创建一个全部展开的expander
     *
     * @param len 长度
     */
    public void createAllexpander(int len) {
        this.expandAll = true;
        this.len = len;
    }


}