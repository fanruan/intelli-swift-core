package com.fr.bi.cal.analyze.report.report.widget;

import com.finebi.cube.api.ICubeColumnIndexReader;
import com.finebi.cube.api.ICubeTableService;
import com.finebi.cube.api.ICubeValueEntryGetter;
import com.finebi.cube.relation.BITableSourceRelation;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.conf.report.widget.field.dimension.BIDimension;
import com.fr.bi.conf.session.BISessionProvider;
import com.fr.bi.field.dimension.calculator.DateDimensionCalculator;
import com.fr.bi.stable.constant.BIJSONConstant;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.constant.DBConstant;
import com.fr.bi.stable.gvi.AllShowRoaringGroupValueIndex;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.gvi.traversal.SingleRowTraversalAction;
import com.fr.bi.stable.io.newio.NIOConstant;
import com.fr.bi.stable.io.sortlist.ArrayLookupHelper;
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
import com.fr.stable.collections.array.IntArray;

import java.util.*;

public class ListLabelWidget extends BISummaryWidget {

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
        Set<String> selected_value = new HashSet<String>();

        if (selected_values != null && StringUtils.isNotEmpty(selected_values)) {
            JSONArray selectedValueArray = new JSONArray(selected_values);
            selected_value.addAll(Arrays.asList(BIJsonUtils.jsonArray2StringArray(selectedValueArray)));
        }
        if (dimension.getGroup().getType() != BIReportConstant.GROUP.ID_GROUP && dimension.getGroup().getType() != BIReportConstant.GROUP.NO_GROUP) {
            return getCustomGroupResult(gvi, reader, selected_value, calculator);
        } else {
            ICubeTableService ti = session.getLoader().getTableIndex(dimension.getStatisticElement().getTableBelongTo().getTableSource());
            ICubeValueEntryGetter getter = ti.getValueEntryGetter(dimension.createKey(dimension.getStatisticElement()), new ArrayList<BITableSourceRelation>());
            return createIDGroupIndex(gvi, reader, selected_value, getter, calculator.getComparator());
        }
    }

    private enum SearchMode{
        PY, START_WITH
    }

    private abstract class SimpleIntArray{
        public abstract int get(int index);

        public abstract int size();
    }

    //超过50w只搜索开头是
    private static final int START_WITH_LIMIT = 500000;

    private JSONObject createIDGroupIndex(GroupValueIndex gvi, ICubeColumnIndexReader reader, Set<String> selected_value, final ICubeValueEntryGetter getter, Comparator comparator) throws JSONException{
        SearchMode mode = SearchMode.PY;
        int start = 0, end = getter.getGroupSize();
        if (getter.getGroupSize() > START_WITH_LIMIT){
            mode = SearchMode.START_WITH;
            start = ArrayLookupHelper.getStartIndex4StartWith(reader, keyword, comparator);
            end = ArrayLookupHelper.getEndIndex4StartWith(reader, keyword, comparator) + 1;
        }
        SimpleIntArray groupArray;
        if (gvi instanceof AllShowRoaringGroupValueIndex){
            final int fstart = start, size = start == -1 ? 0 :  end - start;
            groupArray = new SimpleIntArray() {
                @Override
                public int get(int index) {
                    return index + fstart;
                }

                @Override
                public int size() {
                    return size;
                }
            };
        } else {
            final int[] groupIndex = new int[getter.getGroupSize()];
            Arrays.fill(groupIndex, NIOConstant.INTEGER.NULL_VALUE);
            gvi.Traversal(new SingleRowTraversalAction() {
                @Override
                public void actionPerformed(int row) {
                    int groupRow = getter.getPositionOfGroupByRow(row);
                    if (groupRow != NIOConstant.INTEGER.NULL_VALUE) {
                        groupIndex[groupRow] = groupRow;
                    }
                }
            });
            final IntArray array = new IntArray();
            if (start != -1){
                for (int i = start; i < end; i ++){
                    if (groupIndex[i] != NIOConstant.INTEGER.NULL_VALUE){
                        array.add(i);
                    }
                }
            }
            groupArray = new SimpleIntArray() {
                @Override
                public int get(int index) {
                    return array.get(index);
                }
                @Override
                public int size() {
                    return array.size;
                }
            };
        }
        if (data_type == DBConstant.REQ_DATA_TYPE.REQ_GET_DATA_LENGTH) {
            return JSONObject.create().put(BIJSONConstant.JSON_KEYS.VALUE, getSearchCount(reader, selected_value, groupArray, mode));
        }
        if (data_type == DBConstant.REQ_DATA_TYPE.REQ_GET_ALL_DATA || times < 1) {
            return getSearchResult(reader, selected_value, 0, groupArray.size(), groupArray, mode);
        } else {
            return getSearchResult(reader, selected_value, (times - 1) * STEP, times * STEP, groupArray, mode);
        }
    }

    private JSONObject getCustomGroupResult(GroupValueIndex gvi, ICubeColumnIndexReader reader, Set<String> selected_value, DimensionCalculator calculator) throws JSONException {
        List<Object> list = new ArrayList<Object>();
        Iterator<Map.Entry<Object, GroupValueIndex>> it = reader.iterator();
        while (it.hasNext()) {
            Map.Entry<Object, GroupValueIndex> entry = it.next();
            if (entry.getValue().hasSameValue(gvi)) {
                list.add(entry.getKey());
            }
        }
        if (data_type == DBConstant.REQ_DATA_TYPE.REQ_GET_DATA_LENGTH) {
            return JSONObject.create().put(BIJSONConstant.JSON_KEYS.VALUE, getSearchCount(selected_value, list));
        }
        if (data_type == DBConstant.REQ_DATA_TYPE.REQ_GET_ALL_DATA || times < 1) {
            return getSearchResult(selected_value, 0, list.size(), list, calculator);
        } else {
            return getSearchResult(selected_value, (times - 1) * STEP, times * STEP, list, calculator);
        }
    }

    private int getSearchCount(ICubeColumnIndexReader reader, Set selectedValue, SimpleIntArray array, SearchMode mode) {
        if (selectedValue.isEmpty() && mode == SearchMode.START_WITH){
            return array.size();
        }
        int count = 0;
        String keyword = this.keyword.toLowerCase();
        for (int i = 0; i < array.size(); i++) {
            Object ob = reader.getGroupValue(array.get(i));
            if (ob == null) {
                continue;
            }
            String str = ob.toString();
            if (match(str, keyword, selectedValue, mode)) {
                count++;
            }
        }
        return count;
    }

    private int getSearchCount(Set selectedValue, List<Object> list) {
        int count = 0;
        String keyword = this.keyword.toLowerCase();
        for (Object ob : list){
            if (ob == null) {
                continue;
            }
            if (match(ob.toString(), keyword, selectedValue, SearchMode.PY)) {
                count++;
            }
        }
        return count;
    }

    private boolean match(String value, String keyword, Set selectedValue, SearchMode mode) {
        if (selectedValue.contains(value)) {
            return false;
        }
        if (StringUtils.isEmpty(keyword)) {
            return true;
        }
        if (mode == SearchMode.START_WITH){
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

    private JSONObject getSearchResult(Set selectedValue, int start, int end, List<Object> list, DimensionCalculator calculator) throws JSONException {
        JSONArray ja = JSONArray.create();
        JSONObject jo = JSONObject.create();
        boolean hasNext = false;
        List<String> find = new ArrayList<String>();
        List<String> match = new ArrayList<String>();
        int matched = 0;
        String key = this.keyword.toLowerCase();
        for (Object ob : list){
            if (calculator instanceof DateDimensionCalculator && calculator.getGroup().getType() == BIReportConstant.GROUP.M) {
                ob = ((Integer) ob) + 1;
            }
            if (ob == null) {
                continue;
            }

            String str = ob.toString();
            if (match(str, key, selectedValue, SearchMode.PY)) {
                if (matched >= start && matched < end) {
                    if (ComparatorUtils.equals(keyword, str)) {
                        match.add(str);
                    } else {
                        find.add(str);
                    }
                } else if (matched >= end) {
                    hasNext = true;
                    break;
                }
                matched++;
            }
        }
        for (String s : match) {
            ja.put(s);
        }
        for (String s : find) {
            ja.put(s);
        }
        jo.put(BIJSONConstant.JSON_KEYS.VALUE, ja);
        jo.put(BIJSONConstant.JSON_KEYS.HAS_NEXT, hasNext);
        return jo;
    }


    private JSONObject getSearchResult(ICubeColumnIndexReader reader, Set selectedValue, int start, int end, SimpleIntArray array, SearchMode mode) throws JSONException {
        JSONArray ja = JSONArray.create();
        JSONObject jo = JSONObject.create();
        boolean hasNext = false;
        List<String> find = new ArrayList<String>();
        List<String> match = new ArrayList<String>();
        int matched = 0;
        String key = this.keyword.toLowerCase();
        for (int i = 0; i < array.size(); i++) {
            Object ob = reader.getGroupValue(array.get(i));
            String str = ob.toString();
            if (match(str, key, selectedValue, mode)) {
                if (matched >= start && matched < end) {
                    if (ComparatorUtils.equals(keyword, str)) {
                        match.add(str);
                    } else {
                        find.add(str);
                    }
                } else if (matched >= end) {
                    hasNext = true;
                    break;
                }
                matched++;
            }
        }
        for (String s : match) {
            ja.put(s);
        }
        for (String s : find) {
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