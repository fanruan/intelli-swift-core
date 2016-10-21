package com.fr.bi.cal.analyze.report.report.widget.treelabel;

import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.bi.cal.analyze.executor.paging.Paging;
import com.fr.bi.cal.analyze.report.report.widget.TreeLabelWidget;
import com.fr.bi.cal.analyze.report.report.widget.tree.GetTreeTreeNodeExecutor;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.stable.utils.program.BIJsonUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;
import com.fr.stable.ArrayUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by fay on 2016/10/17.
 */
public class GetTreeLabelExecutor extends AbstractTreeLabelExecutor {
    private String id;
    private String parentValuesString;
    private int floors = 0;

    public GetTreeLabelExecutor(TreeLabelWidget widget, Paging paging, BISession session) {
        super(widget, paging, session);
    }

    public void parseJSON(JSONObject jo) throws JSONException {
        super.parseJSON(jo);
        if(jo.has("parent_values")) {
            parentValuesString = jo.getString("parent_values");
        }
        if(jo.has("floors")) {
            floors = jo.getInt("floors");
        }
        if(jo.has("id")) {
            id = jo.getString("id");
        }
    }

    public JSONObject getResultJSON() throws JSONException {
        String[] values = new String[0];
        JSONArray parentValues = null;
        if (parentValuesString != null) {
            parentValues = new JSONArray(parentValuesString);
            values = BIJsonUtils.jsonArray2StringArray(parentValues);
        }
        ArrayList<JSONArray> vl = new ArrayList<JSONArray>();
        if(id != null) {
            getAllData(vl, values, id, 0);
        } else {
            getAllData(vl, values, null, 0);
        }


        //如果结果为空，直接返回
        if (vl.size() == 0) {
            return new JSONObject();
        }

        JSONObject jo = new JSONObject();

        jo.put("items", vl);
        return jo;
    }

    private void getAllData(ArrayList<JSONArray> result, String[] values, String id, int floor) throws JSONException {
        List<String> vl;
        vl = createData(values, floors, 1);
        if (!vl.isEmpty()) {
            if (result.size() > floor) {
                concatArray(result.get(floor), createJSONArrayForTree(vl, id));
            } else {
                result.add(new JSONArray());
                concatArray(result.get(floor), createJSONArrayForTree(vl, id));
            }
            for (int i = 0;i<vl.size();i++) {
                String[] val = { vl.get(i) };
                String temp;
                if (id == null) {
                    temp = "_" + (i+1);
                } else {
                    temp  = id+ "_" +(i+1);
                }
                if(values.length < widget.getViewDimensions().length - 1 - floors) {
                    getAllData(result, ArrayUtils.addAll(values, val), temp, floor+1);
                }
            }
        }
    }

    private void concatArray(JSONArray arr1, JSONArray arr2)
            throws JSONException {
        for (int i = 0; i < arr2.length(); i++) {
            arr1.put(arr2.get(i));
        }
    }

    private JSONArray createJSONArrayForTree(List<String> list, String id) {
        JSONArray ja = new JSONArray();
        int len = list.size();
        if (len != 0) {
            for (int i = 0; i < len; i++) {
                JSONObject nodeJa = new JSONObject();
                try {
                    if (id == null) {
                        nodeJa.put("id", "_" + (i + 1));
                    } else {
                        nodeJa.put("pId", id);
                        nodeJa.put("id", id + "_" + (i + 1));
                    }
                    nodeJa.put("text", list.get(i));
                    nodeJa.put("title", list.get(i));
                    nodeJa.put("value", list.get(i));
                } catch (JSONException e) {
                    BILoggerFactory.getLogger().error(e.getMessage(), e);
                }

                ja.put(nodeJa);
            }
        }
        return ja;
    }
}
