package com.fr.bi.cal.analyze.report.report.widget.treelabel;

import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.bi.cal.analyze.executor.paging.Paging;
import com.fr.bi.cal.analyze.report.report.widget.TreeLabelWidget;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.utils.program.BIJsonUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;
import com.fr.stable.ArrayUtils;

import java.util.*;

/**
 * Created by fay on 2016/10/17.
 */
public class GetTreeLabelExecutor extends AbstractTreeLabelExecutor {
    private String parentValues;
    private String selectedValues;
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
        if (jo.has("selectedValues")) {
            selectedValues = jo.getString("selectedValues");
        }
    }

    public JSONObject getResultJSON() throws JSONException {
        String id;
        String[] values;
        ArrayList<JSONArray> vl = new ArrayList<JSONArray>();
        JSONArray selected = new JSONArray(selectedValues);
        if (parentValues != null) {
            JSONArray pvalues = new JSONArray(parentValues);
            for (int i = 0; i < pvalues.length(); i++) {
                id = pvalues.getJSONObject(i).getString("id");
                values = BIJsonUtils.jsonArray2StringArray(new JSONArray(pvalues.getJSONObject(i).getString("value")));
                getAllData(vl, values, id, 0, selected);
            }
        }

        JSONObject jo = new JSONObject();

        jo.put("items", vl);
        return jo;
    }

    private void getAllData(ArrayList<JSONArray> result, String[] values, String id, final int floor, final JSONArray selected)
            throws JSONException {
        if (floor < result.size() &&
                result.get(floor).length() >= BIReportConstant.TREE_LABEL.TREE_LABEL_ITEM_COUNT_NUM) {
            return;
        }
        List<String> vl = createData(values, 0, 1);

        if (selected.length() > floor) {
            final JSONArray selectedValues = selected.getJSONArray(floor);
            Collections.sort(vl, new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) {
                    int pre = 0, next = 0;
                    try {
                        pre = containString(selectedValues, o1);
                        next = containString(selectedValues, o2);
                        return pre - next;
                    } catch (JSONException e) {
                        return 0;
                    }
                }
            });
        }

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
                    getAllData(result, ArrayUtils.addAll(values, val), temp, floor + 1, selected);
                } else {
                    break;
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

    private int containString(JSONArray array, String str)
            throws JSONException {
        int flag = -1;
        for (int i = 0; i < array.length(); i++) {
            if (str.equals(array.getString(i))) {
                flag = 1;
            }
        }
        return flag;
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
