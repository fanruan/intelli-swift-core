package com.fr.bi.cal.analyze.report.report.widget.tree;

import com.fr.bi.cal.analyze.executor.paging.Paging;
import com.fr.bi.cal.analyze.report.report.widget.TreeWidget;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.bi.stable.utils.program.BIJsonUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

import java.util.*;

/**
 * Created by roy on 16/4/19.
 */
public class GetTreeTreeNodeExecutor extends AbstractTreeNodeExecutor {
    private String id;
    private int times = -1;
    private String checkStateString;
    private String parentValuesString;

    public GetTreeTreeNodeExecutor(TreeWidget widget, Paging paging, BISession session) {
        super(widget, paging, session);
    }


    public void parseJSON(JSONObject jo) throws JSONException {
        super.parseJSON(jo);
        if (jo.has("id")) {
            id = jo.getString("id");
        }
        if (jo.has("times")) {
            times = jo.getInt("times");
        }
        if (jo.has("check_state")) {
            checkStateString = jo.getString("check_state");
        }
        if (jo.has("parent_values")) {
            parentValuesString = jo.getString("parent_values");
        }


    }


    public JSONObject getResultJSON() throws JSONException {
        String[] values = new String[0];
        JSONArray parentValues = null;
        if (parentValuesString != null) {
            parentValues = new JSONArray(parentValuesString);
            values = BIJsonUtils.jsonArray2StringArray(parentValues);
        }
        boolean checked = false;
        boolean half = false;
        if (checkStateString != null) {
            JSONObject checkState = new JSONObject(checkStateString);
            checked = checkState.getBoolean("checked");
            half = checkState.getBoolean("half");
        }
        boolean hasChild = (values.length + 1 < floors);
        List<String> vl;
        if (values.length > 0) {
            vl = createData(values, -1);
        } else {
            vl = createData(values, times);
        }


        //如果结果为空，直接返回
        if (vl.isEmpty()) {
            return new JSONObject();
        }

        JSONObject selected_values = new JSONObject();
        if (selectedValuesString != null) {
            selected_values = new JSONObject(selectedValuesString);
        }
        int pLen = parentValues == null ? 0 : parentValues.length();
        Map<String, Node> valueMap = new HashMap<String, Node>();
        if (judgeState(pLen, checked, half, selected_values)) {
            valueMap = dealWidthSelectedValue(values, selected_values);
        }

        JSONObject jo = new JSONObject();
        jo.put("hasNext", hasNext(values, times));
        jo.put("items", createJSONArrayForTree(vl, id, times, checked, half, hasChild, parentValues, valueMap));
        return jo;
    }

    private Map<String, Node> dealWidthSelectedValue(String[] values, JSONObject selectedValues) throws JSONException {
        //已选中的当前节点数据 0表示未选，1表示半选，2表示全选
        Map<String, Node> valueMap = new HashMap<String, Node>();
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

    private boolean judgeState(int len, boolean checked, boolean half, JSONObject selectedValues) {
        if (len > 0 && !checked) {
            return false;
        }
        return (len == 0 || (checked && half)) && selectedValues != null && selectedValues.length() > 0;
    }

    private boolean hasNext(String[] parentValues, int times) throws JSONException {
        return times * BIReportConstant.TREE.TREE_ITEM_COUNT_PER_PAGE < createData(parentValues, -1).size();
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

    private int getChildCount(String[] values) throws JSONException {
        return createData(values, -1).size();
    }


}
