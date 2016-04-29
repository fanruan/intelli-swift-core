package com.fr.bi.test;

import com.fr.bi.stable.utils.program.BIJsonUtils;
import com.fr.bi.stable.utils.program.BIPhoneticismUtils;

import com.fr.general.ComparatorUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;
import com.fr.stable.StableUtils;
import com.fr.web.core.ActionNoSessionCMD;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;


public class BIGetTreeSelectNodeAction extends ActionNoSessionCMD {

    @Override
    public void actionCMD(HttpServletRequest req, HttpServletResponse res) throws Exception {

        String selectedValuesString = WebUtils.getHTTPRequestParameter(req, "selected_values");
        String notSelectedValueString = WebUtils.getHTTPRequestParameter(req, "not_selected_value");
        String keyword = WebUtils.getHTTPRequestParameter(req, "keyword");
        String floorsString = WebUtils.getHTTPRequestParameter(req, "floors");
        String parentValueString = WebUtils.getHTTPRequestParameter(req, "parent_values");

        JSONObject selected_values = new JSONObject();
        if (selectedValuesString != null) {
            selected_values = new JSONObject(selectedValuesString);
        }

        String[] parent = new String[0];
        if (parentValueString != null) {
            JSONArray parentObject = new JSONArray(parentValueString);
            parent = BIJsonUtils.jsonArray2StringArray(parentObject);
        }

        if (selected_values.names() == null || selected_values.names().length() == 0) {
            WebUtils.printAsJSON(res, new JSONObject());
            return;
        }

        int floors = floorsString == null ? 0 : Integer.parseInt(floorsString);

        dealWithSelectValues(selected_values, notSelectedValueString, parent, floors, keyword);
        WebUtils.printAsJSON(res, selected_values);
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
                            List<String> expanded = randomData(split);
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
            if (t == null || t.names() == null || t.names().length() == 0) {
                return true;
            }
        }
        return true;
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

    private boolean isMatch(String name, String keyword) {
        String py = BIPhoneticismUtils.getPingYin(name);
        if (name.toUpperCase().contains(keyword.toUpperCase())
                || py.toUpperCase().contains(keyword.toUpperCase())) {
            return true;
        }
        return false;
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

        List<String> vl = randomData(newParents);

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

    private List<String> randomData(String[] parentValues) {
        List<String> res = new ArrayList<String>();
        String v = StableUtils.join(parentValues, ",");
        for (int i = 0; i < 20; i++) {
            res.add(v + "_" + i);
        }
        return res;
    }

    @Override
    public String getCMD() {
        return "get_part_tree_selected_node";
    }
}