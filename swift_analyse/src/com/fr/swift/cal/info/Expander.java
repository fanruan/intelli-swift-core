package com.fr.swift.cal.info;

import com.fr.general.ComparatorUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 处理节点是否展开的类对象， 与node结构类似
 *
 * @author Daniel
 */
public class Expander implements Serializable {

    public static final AllExpander ALL_EXPANDER = new AllExpander();
    private static final long serialVersionUID = 8243791897427634617L;
    protected Expander parent;
    private boolean expandAll;
    private int len;
    private Map<String, Expander> childExpanderMap = new HashMap<String, Expander>();

    /**
     * 加一个子点
     *
     * @param name 名字
     */
    protected void addChild(String name) {
        Expander ne = new Expander();
        ne.setParent(this);
        childExpanderMap.put(name, ne);
    }

    public Expander getParent() {
        return this.parent;
    }

    protected void setParent(Expander parent) {
        this.parent = parent;
    }


    /**
     * 第index节点是否展开
     *
     * @param name 次序
     * @return 是否展开
     */
    public boolean isChildExpand(String name) {
        if (expandAll) {
            Expander child = childExpanderMap.get(name);
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


    public Expander getChildExpander(String name) {
        if (expandAll) {
            if (childExpanderMap.get(name) == null) {
                if (len <= 0) {
                    return null;
                } else {
                    Expander c = new AllExpander(len - 1, len);
                    c.setParent(this);
                    return c;
                }
            } else {
                if (len <= 1) {
                    return null;
                } else {
                    Expander child = childExpanderMap.get(name);
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
     * @throws JSONException
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Expander that = (Expander) o;

        if (expandAll != that.expandAll) {
            return false;
        }
        if (len != that.len) {
            return false;
        }
        return ComparatorUtils.equals(childExpanderMap,that.childExpanderMap);

    }

    @Override
    public int hashCode() {
        int result = (expandAll ? 1 : 0);
        result = 31 * result + len;
        //ponyHashMap的hashcode有问题，这里用size区分下吧，锅丢给equals处理
        result = 31 * result + childExpanderMap.size();
        return result;
    }
}