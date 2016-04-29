package com.fr.bi.cal.analyze.report.report.widget.tree;

import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by roy on 16/4/21.
 */
public class AbstractTreeNodeExecutor {
    protected int floors;
    protected List<List<String>> valueList;
    protected String selectedValuesString;


    public void parseJSON(JSONObject jo, List<List<String>> dataList) throws JSONException {
        if (jo.has("floors")) {
            floors = jo.getInt("floors");
        }
        valueList = dataList;
        if (jo.has("selected_values")) {
            selectedValuesString = jo.getString("selected_values");
        }
    }


    protected List<String> createData(String[] parentValues, int times) throws JSONException {
        List<String> dataList = new ArrayList<String>();
//        List<String> targetDataList = getTargetDataList().get(parentValues.length);
        List<String> targetDataList = valueList.get(parentValues.length);
        if (parentValues.length == 0) {
            if (times == -1) {
                return targetDataList;
            }
            if ((times - 1) * BIReportConstant.TREE.TREE_ITEM_COUNT_PER_PAGE < targetDataList.size()) {
                int start = (times - 1) * BIReportConstant.TREE.TREE_ITEM_COUNT_PER_PAGE;
                for (int i = start; i < start + BIReportConstant.TREE.TREE_ITEM_COUNT_PER_PAGE && i < targetDataList.size(); i++) {
                    dataList.add(targetDataList.get(i));
                }
            }
            return dataList;
        } else {
            if (times == 1) {
//                List<String> rootList = getTargetDataList().get(0);
                List<String> rootList = valueList.get(0);
                dataList.add(targetDataList.get(rootList.indexOf(parentValues[0])));
            }
            return dataList;
        }
    }

//    protected List<List<String>> getTargetDataList() throws JSONException {
//        List<List<String>> allTargetDataList = new ArrayList<List<String>>();
//        for (int i = 0; i < floors; i++) {
//            List<String> singleTargetDataList = new ArrayList<String>();
//            for (int j = 0; j < dataJa.length(); j++) {
//                singleTargetDataList.add(dataJa.getJSONArray(j).getString(i));
//            }
//            allTargetDataList.add(singleTargetDataList);
//        }
//        return allTargetDataList;
//    }


}
