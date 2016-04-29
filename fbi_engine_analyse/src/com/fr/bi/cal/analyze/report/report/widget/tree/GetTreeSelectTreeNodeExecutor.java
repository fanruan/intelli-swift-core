package com.fr.bi.cal.analyze.report.report.widget.tree;

import com.fr.bi.stable.utils.program.BIJsonUtils;
import com.fr.bi.stable.utils.program.BIPhoneticismUtils;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by roy on 16/4/21.
 */
public class GetTreeSelectTreeNodeExecutor extends AbstractTreeNodeExecutor {
    private String notSelectedValueString;
    private String keyword;
    private String parent_values;

    @Override
    public void parseJSON(JSONObject jo, List<List<String>> dataList) throws JSONException {
        super.parseJSON(jo, dataList);
        if (jo.has("not_selected_value")) {
            notSelectedValueString = jo.getString("not_selected_value");
        }

        if (jo.has("keyword")) {
            keyword = jo.getString("keyword");
        }

        if (jo.has("parent_values")) {
            parent_values = jo.getString("parent_values");
        }
    }

    public JSONObject getResultJSON() throws JSONException {
        String[] parent = new String[0];
        JSONObject jo = new JSONObject();
        if (parent_values != null) {
            JSONArray parentObject = new JSONArray(parent_values);
            parent = BIJsonUtils.jsonArray2StringArray(parentObject);
        }

        JSONObject selected_values = new JSONObject();
        if (selectedValuesString != null) {
            selected_values = new JSONObject(selectedValuesString);
        }
        if (selected_values.length() == 0) {
            return jo;
        }

        jo = selected_values;
        dealWithSelectValues(jo, notSelectedValueString, parent, floors, keyword);
        return jo;
    }

    private void dealWithSelectValues(JSONObject selected_values, String notSelectedValueString, String[] parent, int floors, String keyword) throws JSONException {
        String[] p = new String[parent.length + 1];
        System.arraycopy(parent, 0, p, 0, parent.length);
        p[parent.length] = notSelectedValueString;

        if (isChild(selected_values, p)) {
            List<String[]> result = new ArrayList<String[]>();
            boolean finded = search(parent.length + 1, floors, parent, notSelectedValueString, keyword, result);

            if (finded) {

                JSONObject next = selected_values;
                int i;

                for (i = 0; i < p.length; i++) {
                    String v = p[i];
                    JSONObject t = next.optJSONObject(v);
                    if (t == null) {
                        if (next.names() == null || next.names().length() == 0) {
                            String[] split = new String[i];
                            System.arraycopy(p, 0, split, 0, i);
                            List<String> expanded = createData(split, -1);
                            for (String ex : expanded) {
                                if (i == p.length - 1 && ComparatorUtils.equals(ex, notSelectedValueString)) {
                                    continue;
                                }
                                next.put(ex, new JSONObject());
                            }
                            next = next.optJSONObject(v);
                        } else {
                            next.put(v, next = new JSONObject());
                        }
                    } else {
                        next = t;
                    }
                }
                if (!result.isEmpty()) {
                    for (String[] arr : result) {
                        buildTree(selected_values, arr);
                    }
                }
            }
        }
    }

    private boolean isChild(JSONObject jo, String[] parent) {
        JSONObject t = jo;
        for (String v : parent) {
            if (!t.has(v)) {
                return false;
            }
            t = t.optJSONObject(v);
            if (t == null || t.length() == 0) {
                return true;
            }
        }
        return true;
    }

    private boolean search(int deep, int floor, String[] parents, String value, String keyword, List<String[]> result) throws JSONException {

        String[] newParents = new String[parents.length + 1];
        System.arraycopy(parents, 0, newParents, 0, parents.length);
        newParents[parents.length] = value;
        if (isMatch(value, keyword)) {
            return true;
        }
        if (deep >= floor) {
            return false;
        }

        List<String> vl = createData(newParents, -1);

        List<String> notSearch = new ArrayList<String>();
        boolean can = false;

        for (int i = 0, len = vl.size(); i < len; i++) {
            if (search(deep + 1, floor, newParents, vl.get(i), keyword, result)) {
                can = true;
            } else {
                notSearch.add(vl.get(i));
            }
        }
        if (can) {
            for (String v : notSearch) {
                String[] next = new String[newParents.length + 1];
                System.arraycopy(newParents, 0, next, 0, newParents.length);
                next[newParents.length] = v;
                result.add(next);
            }
        }
        return can;
    }

    private boolean isMatch(String name, String keyword) {
        String py = BIPhoneticismUtils.getPingYin(name);
        if (name.toUpperCase().contains(keyword.toUpperCase())
                || py.toUpperCase().contains(keyword.toUpperCase())) {
            return true;
        }
        return false;
    }

    private void buildTree(JSONObject jo, String[] values) throws JSONException {
        JSONObject t = jo;
        for (String v : values) {
            if (!t.has(v)) {
                t.put(v, new JSONObject());
            }
            t = t.optJSONObject(v);
        }
    }
}
