package com.fr.bi.cal.analyze.report.report.widget;

import com.finebi.cube.api.ICubeColumnIndexReader;
import com.finebi.cube.relation.BITableSourceRelation;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.conf.report.widget.field.dimension.BIDimension;
import com.fr.bi.conf.session.BISessionProvider;
import com.fr.bi.stable.constant.BIJSONConstant;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.constant.DBConstant;
import com.fr.bi.stable.report.result.DimensionCalculator;
import com.fr.bi.stable.utils.program.BIJsonUtils;
import com.fr.bi.stable.utils.program.BIPhoneticismUtils;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;
import com.fr.report.poly.PolyECBlock;
import com.fr.report.poly.TemplateBlock;
import com.fr.stable.StringUtils;

import java.util.*;

public class StringControlWidget extends BISummaryWidget {

    private static final int STEP = 100;
    private int data_type = -2;
    private int times = -1;
    private String selected_values;
    private String keyword = StringUtils.EMPTY;

    @Override
    public BIDimension[] getViewDimensions() {
        return new BIDimension[0];
    }

    @Override
    public BIDimension[] getViewTargets() {
        return new BIDimension[0];
    }

    @Override
    public int isOrder() {
        return 0;
    }

    public JSONObject createDataJSON(BISessionProvider session) throws JSONException {
        BIDimension dimension = getDimensions()[0];
        DimensionCalculator calculator = dimension.createCalculator(dimension.getStatisticElement(), new ArrayList<BITableSourceRelation>());
        ICubeColumnIndexReader reader = calculator.createNoneSortGroupValueMapGetter(dimension.getStatisticElement().getTableBelongTo(), session.getLoader());
        Set<String> selected_value = new HashSet<String>();

        if (selected_values != null && StringUtils.isNotEmpty(selected_values)) {
            JSONArray selectedValueArray = new JSONArray(selected_values);
            selected_value.addAll(Arrays.asList(BIJsonUtils.jsonArray2StringArray(selectedValueArray)));
        }
        if(data_type == DBConstant.REQ_DATA_TYPE.REQ_GET_DATA_LENGTH){
            return new JSONObject().put(BIJSONConstant.JSON_KEYS.VALUE, getSearchCount(reader, selected_value));
        }
        if(data_type == DBConstant.REQ_DATA_TYPE.REQ_GET_ALL_DATA || times < 1){
            return getSearchResult(reader, selected_value, 0, reader.sizeOfGroup());
        } else {
            return getSearchResult(reader, selected_value, (times - 1) * STEP, times * STEP);
        }
    }

    private int getSearchCount(ICubeColumnIndexReader reader, Set selectedValue) {
        int count = 0;
        String keyword = this.keyword.toLowerCase();
        for (int i = 0; i < reader.sizeOfGroup(); i ++){
            Object ob = reader.getGroupValue(i);
            if (ob == null){
                continue;
            }
            String str = ob.toString();
            if (match(str, keyword, selectedValue)){
                count++;
            }
        }
        return count;
    }

    private boolean match(String value, String keyword, Set selectedValue){
        if (selectedValue.contains(value)){
            return false;
        }
        if (StringUtils.isEmpty(keyword)){
            return true;
        }
        String strPinyin = BIPhoneticismUtils.getPingYin(value).toLowerCase();
        return strPinyin.contains(keyword) || value.contains(keyword);
    }

    @Override
    protected TemplateBlock createBIBlock(BISession session) {
        return new PolyECBlock();
    }

    @Override
    public void parseJSON(JSONObject jo, long userId) throws Exception {
        super.parseJSON(jo, userId);
        if (jo.has("text_options")) {
            JSONObject treeJo = jo.getJSONObject("text_options");
            if (treeJo.has("type")) {
                data_type = treeJo.getInt("type");
            }
            if (treeJo.has("times")) {
                times = treeJo.getInt("times");
            }
            selected_values = treeJo.optString("selected_values", StringUtils.EMPTY);
            keyword = treeJo.optString("keyword", StringUtils.EMPTY);
        }

    }

    private JSONObject getSearchResult(ICubeColumnIndexReader reader, Set selectedValue, int start, int end) throws JSONException {
        JSONArray ja = new JSONArray();
        JSONObject jo = new JSONObject();
        boolean hasNext = false;
        List<String> find = new ArrayList<String>();
        List<String> match = new ArrayList<String>();
        int matched = 0;
        String key = this.keyword.toLowerCase();
        for (int i = 0; i < reader.sizeOfGroup(); i ++){
            Object ob = reader.getGroupValue(i);
            if (ob == null){
                continue;
            }
            String str = ob.toString();
            if (match(str, key, selectedValue)){
                if (matched >= start && matched < end){
                    if(ComparatorUtils.equals(keyword, str)){
                        match.add(str);
                    }else{
                        find.add(str);
                    }
                } else if (matched >= end){
                    hasNext = true;
                    break;
                }
                matched ++;
            }
        }
        for (String s : match){
            ja.put(s);
        }
        for (String s : find){
            ja.put(s);
        }
        jo.put(BIJSONConstant.JSON_KEYS.VALUE, ja);
        jo.put(BIJSONConstant.JSON_KEYS.HAS_NEXT, hasNext);
        return jo;
    }

    @Override
    public int getType() {
        return BIReportConstant.WIDGET.STRING;
    }

}