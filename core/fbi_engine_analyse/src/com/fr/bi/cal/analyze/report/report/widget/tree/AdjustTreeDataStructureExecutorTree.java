package com.fr.bi.cal.analyze.report.report.widget.tree;

import com.fr.bi.cal.analyze.executor.paging.Paging;
import com.fr.bi.cal.analyze.report.report.widget.TreeWidget;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.stable.utils.program.BIJsonUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by roy on 16/4/21.
 */
public class AdjustTreeDataStructureExecutorTree extends AbstractTreeNodeExecutor {

    public AdjustTreeDataStructureExecutorTree(TreeWidget widget, Paging paging, BISession session) {
        super(widget, paging, session);
    }

    public JSONObject getResultJSON() throws JSONException {
        JSONObject jo = new JSONObject();
        JSONObject selected_values = new JSONObject();
        if (selectedValuesString != null) {
            selected_values = new JSONObject(selectedValuesString);
        }
        if (selected_values.length() == 0) {
            return jo;
        }

        List<String[]> result = new ArrayList<String[]>();
        JSONArray namesArray = selected_values.names();
        String[] names = BIJsonUtils.jsonArray2StringArray(namesArray);

        for (String name : names) {
            String[] t = new String[1];
            t[0] = name;
            result.add(t);
        }
        dealWithSelectedValues(selected_values, new String[0], result);

        for (String[] strs : result) {
            buildTree(jo, strs);
        }
        return jo;


    }

    private boolean dealWithSelectedValues(JSONObject jo, String[] parent, List<String[]> result) throws JSONException {
        if (jo.names() == null || jo.names().length() == 0) {
            return true;
        }
        JSONArray namesArray = jo.names();
        String[] names = BIJsonUtils.jsonArray2StringArray(namesArray);
        boolean can = true;
        for (String name : names) {
            String[] p = new String[parent.length + 1];
            System.arraycopy(parent, 0, p, 0, parent.length);
            p[parent.length] = name;
            if (!dealWithSelectedValues(jo.optJSONObject(name), p, result)) {
                JSONArray array = jo.optJSONObject(name).names();
                String[] ns = BIJsonUtils.jsonArray2StringArray(array);
                for (String n : ns) {
                    String[] t = new String[p.length + 1];
                    System.arraycopy(p, 0, t, 0, p.length);
                    t[p.length] = n;
                    result.add(t);
                }
                can = false;
            }
        }

        return can && isAllSelected(jo, parent);
    }

    private boolean isAllSelected(JSONObject jo, String[] parent) throws JSONException {
        return jo.names().length() == 0 || getChildCount(parent) == jo.names().length();
    }

    private int getChildCount(String[] values) throws JSONException {
        return createData(values, -1).size();

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
