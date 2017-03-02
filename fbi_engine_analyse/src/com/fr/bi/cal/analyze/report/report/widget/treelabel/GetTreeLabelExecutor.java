package com.fr.bi.cal.analyze.report.report.widget.treelabel;
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
        ArrayList<LinkedHashSet<String>> staticVl = new ArrayList<LinkedHashSet<String>>();
        ArrayList<LinkedHashSet<String>> otherVl = new ArrayList<LinkedHashSet<String>>();
        ArrayList<LinkedHashSet<String>> vl = new ArrayList<LinkedHashSet<String>>();
        List<List<String>> staticSelectedValues = new ArrayList<List<String>>();
        List<List<String>> otherSelectedValues = new ArrayList<List<String>>();
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
            if (floors >= 0 && floors >= i) {
                staticSelectedValues.add(listData);
            } else {
                otherSelectedValues.add(listData);
            }
            allSelectedValues.add(listData);
        }
//        List<String> temp = new ArrayList<String>();
//        recursive(staticSelectedValues, staticLinks, 0, temp);
//        temp = new ArrayList<String>();
//        recursive(otherSelectedValues, otherLinks, 0, temp);

//        for(List<String> filters : staticSelectedValues) {
//            getAllData(staticVl, filters.toArray(new String[filters.size()]), otherSelectedValues, 0);
//        }
        getStaticData(otherVl, allSelectedValues,0);

        JSONObject jo = new JSONObject();

        vl = otherVl;
        vl.addAll(staticVl);
        jo.put("items", vl);
        jo.put("values", selectedVals);
        return jo;
    }

//    private void getAllData(ArrayList<LinkedHashSet<String>> result, List<List<String>> values, List<List<String>> otherValues, int floor)
//            throws JSONException {
//        if (values.size() >= widget.getViewDimensions().length - 1) {
//            return;
//        }
//        if (result.size() > floor + 1 &&
//                result.get(floor + 1).size() > BIReportConstant.TREE_LABEL.TREE_LABEL_ITEM_COUNT_NUM &&
//                result.get(floor).size() > BIReportConstant.TREE_LABEL.TREE_LABEL_ITEM_COUNT_NUM ) {
//            return;
//        }
//
//        List<String> vl = createData(values, 0, 1);
//
//        if (result.size() > floor) {
//            concatSetAndList(result.get(floor), vl);
//        } else {
//            result.add(new LinkedHashSet<String>());
//            concatSetAndList(result.get(floor), vl);
//        }
//        String[] next = otherValues.get(floor).toArray(new String[otherValues.get(floor).size()]);
//        String[] filterValues = ArrayUtils.addAll(values, next);
//        getAllData(result, filterValues, otherValues, floor + 1);
//    }

    private void getStaticData(ArrayList<LinkedHashSet<String>> result, List<List<String>> values, int floor)
            throws JSONException{
        if(floor >= values.size()) {
            return;
        }
        List<List<String>> filter = new ArrayList<List<String>>();
        for (int i =0;i < floor; i++) {
            filter.add(values.get(i));
        }

        List<String> vl = createData( filter, 0, 1);
        if (result.size() > floor) {
            concatSetAndList(result.get(floor), vl);
        } else {
            result.add(new LinkedHashSet<String>());
            concatSetAndList(result.get(floor), vl);
        }
        getStaticData(result, values, floor + 1);
    }

    private LinkedHashSet<String> concatSetAndList(LinkedHashSet<String> set, List<String> list) {
        for (String str : list) {
            set.add(str);
        }
        return set;
    }
}
