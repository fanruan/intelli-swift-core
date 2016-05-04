package com.fr.bi.cal.analyze.report.report.widget.tree;

import com.fr.bi.cal.analyze.executor.paging.Paging;
import com.fr.bi.cal.analyze.report.report.widget.TreeWidget;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.general.Inter;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

import java.util.List;

/**
 * Created by roy on 16/4/21.
 */
public class GetDisplayTreeNodeExecutor extends AbstractTreeNodeExecutor {


    public GetDisplayTreeNodeExecutor(TreeWidget widget, Paging paging, BISession session) {
        super(widget, paging, session);
    }

    public JSONObject getResultJSON() throws JSONException {
        JSONArray result = new JSONArray();
        JSONObject jo = new JSONObject();
        JSONObject selected_values = new JSONObject();
        if (selectedValuesString != null) {
            selected_values = new JSONObject(selectedValuesString);
        }
        if (selected_values.length() == 0) {
            return jo;
        }
        doCheck(result, new String[0], String.valueOf(0), 0, selected_values);
        jo.put("items", result);
        return jo;
    }

    public void doCheck(JSONArray result, String[] parents, String pID, int floors, JSONObject selectedValues) throws JSONException {

        JSONArray names = selectedValues.names();
        if (floors == this.floors) {
            return;
        }
        if (names == null || names.length() == 0) {
            List<String> vl = createData(parents, -1);
            for (int i = 0; i < vl.size(); i++) {
                String aVl = vl.get(i);
                String id = pID + "_" + i;
                createOneJson(result, aVl, getPID(id), id, floors == this.floors - 1 ? 0 : getChildren(selectedValues.optJSONObject(aVl), parents));
                String[] newParents = new String[parents.length + 1];
                for (int j = 0; j < parents.length; j++) {
                    newParents[j] = parents[j];
                }
                newParents[parents.length] = aVl;
                doCheck(result, newParents, id, floors + 1, new JSONObject());
            }
            return;
        }
        for (int i = 0; i < names.length(); i++) {
            String name = names.getString(i);
            String id = getID(pID, parents, name);
            createOneJson(result, name, getPID(id), id, floors == this.floors - 1 ? 0 : getChildren(selectedValues.optJSONObject(name), parents));
            JSONObject nextJO = selectedValues.optJSONObject(name);
            String[] newParents = new String[parents.length + 1];
            for (int j = 0; j < parents.length; j++) {
                newParents[j] = parents[j];
            }
            newParents[parents.length] = name;
            doCheck(result, newParents, id, floors + 1, nextJO);
        }
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

    private int getChildren(JSONObject selectedValues, String[] parents) throws JSONException {
        if (selectedValues == null) {
            return 0;
        }
        if (selectedValues.length() == 0) {
            return 1;
        }
        return selectedValues.names().length();
    }

    private String getID(String pID, String[] parentValues, String value) throws JSONException {
        return pID + "_" + createData(parentValues, -1).indexOf(value);
    }


}
