package com.fr.bi.test;

import com.fr.general.Inter;
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
 * Created by User on 2015/10/5.
 */
public class BIGetTreeDisplayNodeAction extends ActionNoSessionCMD {
    @Override
    public void actionCMD(HttpServletRequest req, HttpServletResponse res) throws Exception {

        String floorString = WebUtils.getHTTPRequestParameter(req, "floors");

        String selectedValuesString = WebUtils.getHTTPRequestParameter(req, "selected_values");

        int floors = 1;
        if (floorString != null) {
            floors = Integer.parseInt(floorString);
        }

        JSONObject selectedValues = new JSONObject();
        if (selectedValuesString != null) {
            selectedValues = new JSONObject(selectedValuesString);
        }

        JSONArray result = new JSONArray();

        JSONArray names = selectedValues.names();
        if (names == null) {
            WebUtils.printAsJSON(res, new JSONArray());
            return;
        }
        doCheck(result, new String[0], 0, selectedValues);

        JSONObject jo = new JSONObject();
        jo.put("items", result);
        WebUtils.printAsJSON(res, jo);
    }


    public String getID(String name) {
        String[] tmp = name.split(",");
        StringBuilder sb = new StringBuilder();
        String[] target = tmp[tmp.length - 1].split("_");
        for (int i = 1; i < target.length; ++i) {
            sb.append(String.valueOf(Integer.parseInt(target[i]) + 1));
            if (i != target.length - 1) {
                sb.append("_");
            }
        }
        return sb.toString();
    }

    //{"_0": {},"_5": {"_5_0":{}}}
    //{"_0": {},"_5": {"_5_0":{}, "_5_1": {"_5,_5_1_1": {}}}}

    private List<String> randomData(String[] parentValues) {
        List<String> res = new ArrayList<String>();
        String v = StableUtils.join(parentValues, ",");
        for (int i = 0; i < 20; i++) {
            res.add(v + "_" + i);
        }
        return res;
    }

    public void doCheck(JSONArray result, String[] parents, int floors, JSONObject selectedValues) throws JSONException {
        JSONArray names = selectedValues.names();
        if (floors >= 4) {
            return;
        }
        if (names == null || names.length() == 0) {
            List<String> vl = randomData(parents);
            for (String aVl : vl) {
                String id = getID(aVl);
                createOneJson(result, aVl, getPID(id), id, floors == 3 ? 0 : 20);
                String[] newParents = new String[parents.length + 1];
                for (int j = 0; j < parents.length; j++) {
                    newParents[j] = parents[j];
                }
                newParents[parents.length] = aVl;
                doCheck(result, newParents, floors + 1, new JSONObject());
            }
            return;
        }
        for (int i = 0; i < names.length(); i++) {
            String name = names.getString(i);
            String id = getID(name);
            createOneJson(result, name, getPID(id), id, floors == 3 ? 0 : getChildren(selectedValues.optJSONObject(name)));
            JSONObject nextJO = selectedValues.optJSONObject(name);
            String[] newParents = new String[parents.length + 1];
            for (int j = 0; j < parents.length; j++) {
                newParents[j] = parents[j];
            }
            newParents[parents.length] = name;
            doCheck(result, newParents, floors + 1, nextJO);
        }
    }

    public int getChildren(JSONObject children) {
        if (children == null) {
            return 0;
        }
        if (children.names() == null) {
            return 20;
        }
        return children.names().length();
    }

    public String getPID(String id) {

        String pId = "0";
        if (id.lastIndexOf('_') > -1) {
            pId = id.substring(0, id.lastIndexOf('_'));
        }
        return pId;
    }

    public void createOneJson(JSONArray result, String name, String pId, String id, int children) throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("id", id);
        obj.put("pId", pId);
        if (children == 0) {
            obj.put("text", name);
        } else {
            obj.put("text", name + "( " + Inter.getLocText("BI-Altogether") + children + Inter.getLocText("BI-Count") + " )");
        }
        obj.put("open", true);
        result.put(obj);
    }

    @Override
    public String getCMD() {
        return "get_display_tree_node";
    }
}