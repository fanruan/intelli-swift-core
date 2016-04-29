package com.fr.bi.cal.analyze.report.report.widget;

import com.fr.bi.cal.analyze.cal.index.loader.CubeIndexLoader;
import com.fr.bi.cal.analyze.cal.result.Node;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.conf.report.widget.field.dimension.BIDimension;
import com.fr.bi.conf.session.BISessionProvider;
import com.fr.bi.field.target.target.BISummaryTarget;
import com.fr.bi.stable.constant.BIJSONConstant;
import com.fr.bi.stable.constant.DBConstant;
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

    private int page = -1;
    private int data_type = -2;
    private int times = -1;
    private String selected_values;
    private String keyword;

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
        Node tree = CubeIndexLoader.getInstance(session.getUserId()).loadGroup(this, new BISummaryTarget[0], this.getDimensions(), this.getDimensions(), new BISummaryTarget[0], -1, true, (BISession) session);
        List<Node> nodelist = tree.getChilds();
        Iterator<Node> it = nodelist.iterator();
        Set<String> set = new HashSet<String>();
        while (it.hasNext()){
            set.add(it.next().getShowValue());
        }
        List<String> selected_value = new ArrayList<String>();

        if (selected_values != null && StringUtils.isNotEmpty(selected_values)) {
            JSONArray selectedValueArray = new JSONArray(selected_values);
            selected_value = Arrays.asList(BIJsonUtils.jsonArray2StringArray(selectedValueArray));
        }
        Set<String> result = getSearchResult(set, keyword, selected_value);
        if(data_type == DBConstant.REQ_DATA_TYPE.REQ_GET_DATA_LENGTH){
            return new JSONObject().put(BIJSONConstant.JSON_KEYS.VALUE, result.size());
        }
        if(data_type == DBConstant.REQ_DATA_TYPE.REQ_GET_ALL_DATA){
            JSONArray ja = new JSONArray(result);
            return new JSONObject().put(BIJSONConstant.JSON_KEYS.VALUE, ja);
        }
        JSONArray ja = new JSONArray(getItemsByTimes(result, times));
        JSONObject jo = new JSONObject();
        jo.put(BIJSONConstant.JSON_KEYS.VALUE, ja);
        jo.put(BIJSONConstant.JSON_KEYS.HAS_NEXT, hasNextByTimes(result, times));
        return jo;
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
            selected_values = treeJo.optString("selected_values", "");
            keyword = treeJo.optString("keyword", "");
        }

    }

    private boolean hasNextByTimes(Set<String> items, int times) {
        return times * 100 < items.size();
    }

    private Set<String> getItemsByTimes(Set<String> items, int times) {
        if(times == -1){
            return items;
        }
        Set<String> result = new HashSet<String>();
        int i = 0;
        Iterator<String> it = items.iterator();
        while (it.hasNext() && i < (times - 1) * 100){
            it.next();
            ++i;
        }
        while (it.hasNext() && i < times * 100){
            result.add(it.next());
            i++;
        }
        return result;
    }

    private Set<String> getSearchResult(Set<String> source, String keyword, List<String> selectedValue) throws JSONException {
        if(StringUtils.isNotEmpty(keyword) || !selectedValue.isEmpty()){
            Set<String> find = new HashSet<String>();
            Set<String> match = new HashSet<String>();
            keyword = keyword.toLowerCase();
            Iterator<String> it = source.iterator();
            while (it.hasNext()){
                String str = it.next();
                String strPinyin = BIPhoneticismUtils.getPingYin(str);
                if(strPinyin.contains(keyword) || str.contains(keyword)){
                    if(!selectedValue.contains(str)){
                        if(ComparatorUtils.equals(keyword, str)){
                            match.add(str);
                        }else{
                            find.add(str);
                        }
                    }
                }
            }
            match.addAll(find);
            return match;
        }
        return source;
    }

}
