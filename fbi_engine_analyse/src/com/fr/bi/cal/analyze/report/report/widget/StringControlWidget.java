package com.fr.bi.cal.analyze.report.report.widget;

import com.finebi.cube.api.ICubeColumnIndexReader;
import com.finebi.cube.api.ICubeTableService;
import com.finebi.cube.api.ICubeValueEntryGetter;
import com.finebi.cube.relation.BITableSourceRelation;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.conf.report.widget.field.dimension.BIDimension;
import com.fr.bi.conf.session.BISessionProvider;
import com.fr.bi.stable.constant.BIJSONConstant;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.constant.DBConstant;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.gvi.traversal.SingleRowTraversalAction;
import com.fr.bi.stable.io.newio.NIOConstant;
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
        GroupValueIndex gvi = createFilterGVI(new DimensionCalculator[]{calculator}, dimension.getStatisticElement().getTableBelongTo(), session.getLoader(), session.getUserId());
        ICubeColumnIndexReader reader = calculator.createNoneSortGroupValueMapGetter(dimension.getStatisticElement().getTableBelongTo(), session.getLoader());
        List<Integer> list;
        if (dimension.getGroup().getType() != BIReportConstant.GROUP.ID_GROUP || dimension.getGroup().getType() != BIReportConstant.GROUP.NO_GROUP){
            list = createCustomGroupIndex(gvi, reader);
        } else {
            list = createIDGroupIndex(gvi, dimension, session);
        }
        Set<String> selected_value = new HashSet<String>();

        if (selected_values != null && StringUtils.isNotEmpty(selected_values)) {
            JSONArray selectedValueArray = new JSONArray(selected_values);
            selected_value.addAll(Arrays.asList(BIJsonUtils.jsonArray2StringArray(selectedValueArray)));
        }
        if(data_type == DBConstant.REQ_DATA_TYPE.REQ_GET_DATA_LENGTH){
            return new JSONObject().put(BIJSONConstant.JSON_KEYS.VALUE, getSearchCount(reader, selected_value, list));
        }
        if(data_type == DBConstant.REQ_DATA_TYPE.REQ_GET_ALL_DATA || times < 1){
            return getSearchResult(reader, selected_value, 0, list.size(), list);
        } else {
            return getSearchResult(reader, selected_value, (times - 1) * STEP, times * STEP, list);
        }
    }

    private List<Integer> createIDGroupIndex(GroupValueIndex gvi, BIDimension dimension, BISessionProvider session) {
        ICubeTableService ti = session.getLoader().getTableIndex(dimension.getStatisticElement().getTableBelongTo().getTableSource());
        final ICubeValueEntryGetter getter = ti.getValueEntryGetter(dimension.createKey(dimension.getStatisticElement()), new ArrayList<BITableSourceRelation>());
        if (gvi.getRowsCountWithData() == ti.getRowCount()){
            return new AbstractList<Integer>() {
                @Override
                public Integer get(int index) {
                    return index;
                }

                @Override
                public int size() {
                    return getter.getGroupSize();
                }
            };
        }
        final Integer[] groupIndex = new Integer[getter.getGroupSize()];
        gvi.Traversal(new SingleRowTraversalAction() {
            @Override
            public void actionPerformed(int row) {
                int groupRow = getter.getPositionOfGroupByRow(row);
                if (groupRow != NIOConstant.INTEGER.NULL_VALUE){
                    groupIndex[groupRow] = groupRow;
                }
            }
        });
        List<Integer> list = new ArrayList<Integer>();
        for (Integer integer : groupIndex){
            if (integer != null){
                list.add(integer);
            }
        }
        return list;
    }

    private List<Integer> createCustomGroupIndex(GroupValueIndex gvi, ICubeColumnIndexReader reader) {
        List<Integer> list = new ArrayList<Integer>();
        Iterator<Map.Entry<String, GroupValueIndex>> it = reader.iterator();
        int index = 0;
        while (it.hasNext()){
            if (!it.next().getValue().AND(gvi).isAllEmpty()){
               list.add(index);
            }
            index ++;
        }
        return list;
    }

    private int getSearchCount(ICubeColumnIndexReader reader, Set selectedValue, List<Integer> list) {
        int count = 0;
        String keyword = this.keyword.toLowerCase();
        for (int i = 0; i < list.size(); i ++){
            Object ob = reader.getGroupValue(list.get(i));
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

    private JSONObject getSearchResult(ICubeColumnIndexReader reader, Set selectedValue, int start, int end, List<Integer> list) throws JSONException {
        JSONArray ja = new JSONArray();
        JSONObject jo = new JSONObject();
        boolean hasNext = false;
        List<String> find = new ArrayList<String>();
        List<String> match = new ArrayList<String>();
        int matched = 0;
        String key = this.keyword.toLowerCase();
        for (int i = 0; i < list.size(); i ++){
            Object ob = reader.getGroupValue(list.get(i));
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