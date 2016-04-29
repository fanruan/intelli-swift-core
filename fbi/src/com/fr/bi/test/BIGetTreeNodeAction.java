package com.fr.bi.test;

import com.fr.bi.stable.utils.code.BILogger;

import com.fr.bi.stable.utils.program.BIJsonUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;
import com.fr.stable.StableUtils;
import com.fr.web.core.ActionNoSessionCMD;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * Created by guy on 14-10-18.
 */
public class BIGetTreeNodeAction extends ActionNoSessionCMD {

    /**
     * 方法
     *
     * @param req 参数
     * @param res 参数
     * @throws Exception
     */
    @Override
    public void actionCMD(HttpServletRequest req, HttpServletResponse res) throws Exception {
        String id = WebUtils.getHTTPRequestParameter(req, "id");
        String timesString = WebUtils.getHTTPRequestParameter(req, "times");//第几次加载
        String checkedString = WebUtils.getHTTPRequestParameter(req, "check_state");
        String floorString = WebUtils.getHTTPRequestParameter(req, "floors");

        String parentValuesString = WebUtils.getHTTPRequestParameter(req, "parent_values");
        String selectedValuesString = WebUtils.getHTTPRequestParameter(req, "selected_values");

        String[] values = new String[0];
        JSONArray parentValues = null;
        if (parentValuesString != null) {
            parentValues = new JSONArray(parentValuesString);
            values = BIJsonUtils.jsonArray2StringArray(parentValues);
        }
        int times = -1;
        if (timesString != null) {
            times = Integer.parseInt(timesString);
        }
        int floors = 1;
        if (floorString != null) {
            floors = Integer.parseInt(floorString);
        }
        boolean checked = false;
        boolean half = false;
        if (checkedString != null) {
            JSONObject checkState = new JSONObject(checkedString);
            checked = checkState.getBoolean("checked");
            half = checkState.getBoolean("half");
        }
        boolean hasChild = (values.length + 1 < floors);

        List<String> vl = randomData(values, times, selectedValuesString);

        //如果结果为空，直接返回
        if (vl.isEmpty()) {
            WebUtils.printAsJSON(res, new JSONObject());
            return;
        }

        int pLen = parentValues == null ? 0 : parentValues.length();
        Map<String, Node> valueMap = new HashMap<String, Node>();
        if (judgetState(pLen, checked, half, selectedValuesString)) {
            valueMap = dealWidthSelectedValue(values, selectedValuesString);
        }

        JSONObject jo = new JSONObject();
        jo.put("hasNext", hasNext(values, times));
        jo.put("items", createJSONArrayForTree(vl, id, times, checked, half, hasChild, parentValues, valueMap));
        WebUtils.printAsJSON(res, jo);
    }

    private Map<String, Node> dealWidthSelectedValue(String[] values, String selectedValuesString) throws JSONException {
        //已选中的当前节点数据 0表示未选，1表示半选，2表示全选
        Map<String, Node> valueMap = new HashMap<String, Node>();
        JSONObject selectedValues = new JSONObject(selectedValuesString);
        for (String v : values) {
            if (selectedValues == null) {
                break;
            }
            selectedValues = selectedValues.optJSONObject(v);
        }
        if (selectedValues != null) {
            JSONArray names = selectedValues.names();
            for (int i = 0; names != null && i < names.length(); i++) {
                String name = names.getString(i);
                JSONObject nextJO = selectedValues.optJSONObject(name);
                if (nextJO == null) {
                    valueMap.put(name, new Node(0, 0));
                    continue;
                }
                if (nextJO.names() == null) {
                    valueMap.put(name, new Node(2, 0));
                    continue;
                }
                Set<String> nextNames = new HashSet<String>();
                for (int k = 0; k < nextJO.names().length(); k++) {
                    String t = nextJO.names().optString(k);
                    JSONObject to = nextJO.optJSONObject(t);
                    if (to == null || to.names() == null || to.names().length() == 0) {
                        nextNames.add(t);
                    }
                }
                valueMap.put(name, new Node(1, nextNames.size()));
            }
        }
        return valueMap;
    }

    private boolean judgetState(int len, boolean checked, boolean half, String selectedValuesString) {
        if (len > 0 && !checked) {
            return false;
        }
        return (len == 0 || (checked && half)) && selectedValuesString != null;
    }


    private CheckState getCheckState(String value, JSONArray parentValues, Map<String, Node> valueMap,
                                     boolean checked, boolean half, boolean hasChild) throws JSONException {
        boolean tempCheck = false, halfCheck = false;
        if (valueMap.containsKey(value)) {
            //可能是半选
            if (valueMap.get(value).getType() == 1) {
                String[] values;
                if (parentValues == null) {
                    values = new String[1];
                    values[0] = value;
                } else {
                    values = new String[parentValues.length() + 1];
                    for (int k = 0; k < parentValues.length(); k++) {
                        values[k] = parentValues.getString(k);
                    }
                    values[parentValues.length()] = value;
                }
                if (hasChild && getChildCount(values) != valueMap.get(value).getCount()) {
                    halfCheck = true;
                }
            } else if (valueMap.get(value).getType() == 2) {//处理父节点为根节点的情况
                tempCheck = true;
            }
        }
        boolean check;
        if (!checked && !halfCheck && !tempCheck) {
            check = valueMap.containsKey(value);
        } else {
            check = ((tempCheck || checked) && !half) || valueMap.containsKey(value);
        }
        return new CheckState(check, halfCheck);
    }

    private JSONArray createJSONArrayForTree(List<String> list, String id, int times,
                                             boolean checked, boolean half, boolean hasChild,
                                             JSONArray parentValues, Map<String, Node> valueMap) {
        JSONArray ja = new JSONArray();
        int len = list.size();

        if (len != 0) {
            for (int i = 0; i < len; i++) {
                JSONObject nodeJa = new JSONObject();
                try {
                    nodeJa.put("isParent", hasChild);
                    if (id == null) {
                        nodeJa.put("id", (i + 1));
                    } else {
                        nodeJa.put("id", id + "_" + times + "_" + (i + 1));
                    }
                    nodeJa.put("times", 1);
                    nodeJa.put("text", list.get(i));
                    nodeJa.put("title", list.get(i));
//                    nodeJa.put("parentValues", parentValues);

                    CheckState state = getCheckState(list.get(i), parentValues, valueMap, checked, half, hasChild);

                    nodeJa.put("checked", state.isCheck());
                    nodeJa.put("halfCheck", state.isHalf());

                } catch (JSONException e) {
                    BILogger.getLogger().error(e.getMessage(), e);
                }

                ja.put(nodeJa);
            }
        }
        return ja;
    }

    private class Node {
        int type;
        int count;

        public Node(int type, int count) {
            this.type = type;
            this.count = count;
        }

        //2为全选
        //1可能为半选
        public int getType() {
            return type;
        }

        public int getCount() {
            return count;
        }
    }

    private class CheckState {
        boolean check;
        boolean half;

        private CheckState(boolean check, boolean half) {
            this.check = check;
            this.half = half;
        }

        public boolean isCheck() {
            return check;
        }

        public boolean isHalf() {
            return half;
        }
    }


    private boolean hasNext(String[] parentValues, int times) {
        return times < 2;
    }

    private List<String> randomData(String[] parentValues, int times, String selectedValuesString) throws JSONException {
        List<String> res = new ArrayList<String>();
        if(times == -1){
            String v = StableUtils.join(parentValues, ",");
            for (int i = 0; i < 20; i++) {
                res.add(v + "_" + i);
            }
            return res;
        }
        if (times < 3) {
            String v = StableUtils.join(parentValues, ",");
            for (int i = (times - 1) * 10; i < times * 10; i++) {
                res.add(v + "_" + i);
            }
        }
        return res;
    }


    private int getChildCount(String[] values) {
        return 20;
    }

    @Override
    public String getCMD() {
        return "get_tree_node";
    }
}