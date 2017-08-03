package com.fr.bi.cal.analyze.report.report.widget.treelabel;
import com.fr.bi.cal.analyze.executor.paging.Paging;
import com.fr.bi.cal.analyze.report.report.widget.TreeLabelWidget;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

import java.util.*;

public class GetTreeLabelExecutor extends AbstractTreeLabelExecutor {
    private String selectedValues;
    private int floors = -1;

    public GetTreeLabelExecutor(TreeLabelWidget widget, Paging paging, BISession session) {
        super(widget, paging, session);
    }

    public void parseJSON(JSONObject jo) throws JSONException {
        super.parseJSON(jo);
        if (jo.has("floors")) {
            floors = jo.getInt("floors");
        }
        if (jo.has("selectedValues")) {
            selectedValues = jo.getString("selectedValues");
        }
    }

    public JSONObject getResultJSON() throws JSONException {
        ArrayList<LinkedHashSet<String>> vl = new ArrayList<LinkedHashSet<String>>();
        List<List<String>> allSelectedValues = new ArrayList<List<String>>();
        JSONArray selectedVals = new JSONArray(selectedValues);
        if (selectedVals.length() == 0) {
            for (int i =0;i< widget.getViewDimensions().length; i++) {
                selectedVals.put(new JSONArray("['_*_']"));
            }
        }
        for (int i = 0; i < selectedVals.length(); i++) {
            ArrayList<String> listData = new ArrayList<String>();
            JSONArray jArray = selectedVals.getJSONArray(i);
            for (int idx = 0; idx < jArray.length(); idx++) {
                listData.add(jArray.getString(idx));
            }
            allSelectedValues.add(listData);
        }

        getAllData(vl, allSelectedValues,0);

        JSONObject jo = new JSONObject();

        jo.put("items", vl);
        jo.put("values", allSelectedValues);
        return jo;
    }

    private void getAllData(ArrayList<LinkedHashSet<String>> result, List<List<String>> values, int floor)
            throws JSONException{
        if(floor >= values.size()) {
            return;
        }

        List<String> vl = createData(values, floor);
        if (result.size() > floor) {
            concatSetAndList(result.get(floor), vl);
        } else {
            result.add(new LinkedHashSet<String>());
            concatSetAndList(result.get(floor), vl);
        }
        if(floor > floors && (values.size() > floor)) {
            values.set(floor, checkSelectedValues(vl, values.get(floor)));
        }
        dealSelectValues(result, values);
        getAllData(result, values, floor + 1);
    }

    private List<String> checkSelectedValues(List<String> values, List<String> selects) {
        List<String> result = new ArrayList<String>();
        if(selects.contains("_*_")) {
            return selects;
        }
        for (String select : selects) {
            if(values.contains(select)) {
                result.add(select);
            }
        }
        if(result.size() == 0) {
            result.add("_*_");
        }
        return result;
    }

    private LinkedHashSet<String> concatSetAndList(LinkedHashSet<String> set, List<String> list) {
        for (String str : list) {
            set.add(str);
        }
        return set;
    }

    private void dealSelectValues(ArrayList<LinkedHashSet<String>> items,List<List<String>> values) {
        for (int i = 0; i < values.size(); i++) {
            if(items.size() > i) {
                for (int j = 0; j < values.get(i).size(); j++) {
                    if (!(items.get(i).contains(values.get(i).get(j)) || "_*_".equals(values.get(i).get(j)))) {
                        values.get(i).remove(j);
                    }
                }
            }
        }
    }
}
