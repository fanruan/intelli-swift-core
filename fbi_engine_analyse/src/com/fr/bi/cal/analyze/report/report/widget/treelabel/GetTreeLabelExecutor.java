package com.fr.bi.cal.analyze.report.report.widget.treelabel;

import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.bi.cal.analyze.executor.paging.Paging;
import com.fr.bi.cal.analyze.report.report.widget.TreeLabelWidget;
import com.fr.bi.cal.analyze.report.report.widget.tree.GetTreeTreeNodeExecutor;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.stable.constant.BIReportConstant;
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
    private String parentValues;
    private int floors = 0;

    public GetTreeLabelExecutor(TreeLabelWidget widget, Paging paging, BISession session) {
        super(widget, paging, session);
    }

    public void parseJSON(JSONObject jo) throws JSONException {
        super.parseJSON(jo);
        if (jo.has("floors")) {
            floors = jo.getInt("floors");
        }
        if (jo.has("parentValues")) {
            parentValues = jo.getString("parentValues");
        }
    }

    public JSONObject getResultJSON() throws JSONException {
        String id;
        String[] values;
        ArrayList<JSONArray> vl = new ArrayList<JSONArray>();
        if (parentValues != null) {
            JSONArray pvalues = new JSONArray(parentValues);
            createCalculator();
            for (int i = 0; i < pvalues.length(); i++) {
                id = pvalues.getJSONObject(i).getString("id");
                values = BIJsonUtils.jsonArray2StringArray(new JSONArray(pvalues.getJSONObject(i).getString("value")));
                getAllData(vl, values, id, 0);
            }
        }

        JSONObject jo = new JSONObject();

        jo.put("items", vl);
        return jo;
    }

    private void getAllData(ArrayList<JSONArray> result, String[] values, String id, int floor) throws JSONException {
        if (floors < result.size() && result.get(floors).length() >= BIReportConstant.TREE_LABEL.TREE_LABEL_ITEM_COUNT_NUM) {
            return;
        }
        List<String> vl = createData(values, floors, 1);
        if (!vl.isEmpty()) {
            if (result.size() > floor) {
                concatArray(result.get(floor), createJSONArrayForTree(vl, id));
            } else {
                result.add(new JSONArray());
                concatArray(result.get(floor), createJSONArrayForTree(vl, id));
            }
            for (int i = 0; i < vl.size(); i++) {
                String[] val = {vl.get(i)};
                String temp;
                if (id == null) {
                    temp = "_" + (i + 1);
                } else {
                    temp = id + "_" + (i + 1);
                }
                if (values.length < widget.getViewDimensions().length - 1 - floors) {
                    getAllData(result, ArrayUtils.addAll(values, val), temp, floor + 1);
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
