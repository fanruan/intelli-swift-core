package com.fr.bi.cal.analyze.report.report.widget;

import com.finebi.cube.api.ICubeColumnIndexReader;
import com.finebi.cube.api.ICubeTableService;
import com.finebi.cube.api.ICubeValueEntryGetter;
import com.finebi.cube.relation.BITableSourceRelation;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.conf.report.WidgetType;
import com.fr.bi.conf.report.widget.field.dimension.BIDimension;
import com.fr.bi.conf.session.BISessionProvider;
import com.fr.bi.stable.constant.BIJSONConstant;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.constant.DBConstant;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.io.sortlist.ArrayLookupHelper;
import com.fr.bi.report.result.DimensionCalculator;
import com.fr.bi.stable.utils.BICollectionUtils;
import com.fr.bi.stable.utils.program.BIJsonUtils;
import com.fr.bi.stable.utils.program.BIPhoneticismUtils;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;
import com.fr.report.poly.PolyECBlock;
import com.fr.report.poly.TemplateBlock;
import com.fr.stable.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

public class ListLabelWidget extends SummaryWidget {

    private static final int STEP = 100;

    private int data_type = -2;

    private int times = -1;

    private String selectedValues;

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

    public JSONObject createDataJSON(BISessionProvider session, HttpServletRequest req) throws JSONException {

        BIDimension dimension = getDimensions()[0];
        DimensionCalculator calculator = dimension.createCalculator(dimension.getStatisticElement(), new ArrayList<BITableSourceRelation>());
        GroupValueIndex gvi = createFilterGVI(new DimensionCalculator[]{calculator}, dimension.getStatisticElement().getTableBelongTo(), session.getLoader(), session.getUserId());
        ICubeColumnIndexReader reader = calculator.createNoneSortGroupValueMapGetter(dimension.getStatisticElement().getTableBelongTo(), session.getLoader());
        Set<String> selectedValue = new HashSet<String>();

        if (selectedValues != null && StringUtils.isNotEmpty(selectedValues)) {
            JSONArray selectedValueArray = new JSONArray(selectedValues);
            selectedValue.addAll(Arrays.asList(BIJsonUtils.jsonArray2StringArray(selectedValueArray)));
        }
        if (dimension.getGroup().getType() != BIReportConstant.GROUP.ID_GROUP && dimension.getGroup().getType() != BIReportConstant.GROUP.NO_GROUP) {
            return getCustomGroupResult(gvi, reader, selectedValue, calculator);
        } else {
            ICubeTableService ti = session.getLoader().getTableIndex(dimension.getStatisticElement().getTableBelongTo().getTableSource());
            ICubeValueEntryGetter getter = ti.getValueEntryGetter(dimension.createKey(dimension.getStatisticElement()), new ArrayList<BITableSourceRelation>());
            return createIDGroupIndex(gvi, reader, selectedValue, getter, calculator.getComparator());
        }
    }

    private enum SearchMode {
        PY, START_WITH
    }

    //超过50w只搜索开头是
    private static final int START_WITH_LIMIT = 500000;

    private JSONObject createIDGroupIndex(GroupValueIndex gvi, ICubeColumnIndexReader reader, Set<String> selectedValue, final ICubeValueEntryGetter getter, Comparator comparator) throws JSONException {

        SearchMode mode = SearchMode.PY;
        int start = 0, end = getter.getGroupSize();
        if (getter.getGroupSize() > START_WITH_LIMIT) {
            mode = SearchMode.START_WITH;
            start = ArrayLookupHelper.getStartIndex4StartWith(reader, keyword, comparator);
            end = ArrayLookupHelper.getEndIndex4StartWith(reader, keyword, comparator) + 1;
        }
        SimpleIntArray groupArray = this.createGroupArray(start, end, new int[0], new int[0], getter, gvi);
        if (data_type == DBConstant.REQ_DATA_TYPE.REQ_GET_DATA_LENGTH) {
            return JSONObject.create().put(BIJSONConstant.JSON_KEYS.VALUE, getSearchCount(reader, selectedValue, groupArray, mode));
        }
        if (data_type == DBConstant.REQ_DATA_TYPE.REQ_GET_ALL_DATA || times < 1) {
            return getSearchResult(reader, selectedValue, 0, groupArray.size(), groupArray, mode);
        } else {
            return getSearchResult(reader, selectedValue, (times - 1) * STEP, times * STEP, groupArray, mode);
        }
    }

    private JSONObject getCustomGroupResult(GroupValueIndex gvi, ICubeColumnIndexReader reader, Set<String> selectedValue, DimensionCalculator calculator) throws JSONException {

        List<Object> list = new ArrayList<Object>();
        Iterator<Map.Entry<Object, GroupValueIndex>> it = reader.iterator();
        while (it.hasNext()) {
            Map.Entry<Object, GroupValueIndex> entry = it.next();
            if (entry.getValue().hasSameValue(gvi)) {
                list.add(entry.getKey());
            }
        }
        if (data_type == DBConstant.REQ_DATA_TYPE.REQ_GET_DATA_LENGTH) {
            return JSONObject.create().put(BIJSONConstant.JSON_KEYS.VALUE, getSearchCount(selectedValue, list));
        }
        if (data_type == DBConstant.REQ_DATA_TYPE.REQ_GET_ALL_DATA || times < 1) {
            return getSearchResult(selectedValue, 0, list.size(), list, calculator);
        } else {
            return getSearchResult(selectedValue, (times - 1) * STEP, times * STEP, list, calculator);
        }
    }

    private int getSearchCount(ICubeColumnIndexReader reader, Set selectedValue, SimpleIntArray array, SearchMode mode) {

        if (selectedValue.isEmpty() && mode == SearchMode.START_WITH) {
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
        for (Object ob : list) {
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
        if (mode == SearchMode.START_WITH) {
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
        if (jo.has("textOptions")) {
            JSONObject treeJo = jo.getJSONObject("textOptions");
            if (treeJo.has("type")) {
                data_type = treeJo.getInt("type");
            }
            if (treeJo.has("times")) {
                times = treeJo.getInt("times");
            }
            selectedValues = treeJo.optString("selectedValues", StringUtils.EMPTY);
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
        for (Object ob : list) {
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
        BIDimension dimension = getDimensions()[0];
        for (String s : match) {
            if (BICollectionUtils.isNotCubeNullKey(s) || dimension.showNullValue()) {
                ja.put(s);
            }
        }
        for (String s : find) {
            if (BICollectionUtils.isNotCubeNullKey(s) || dimension.showNullValue()) {
                ja.put(s);
            }
        }
        jo.put(BIJSONConstant.JSON_KEYS.VALUE, ja);
        jo.put(BIJSONConstant.JSON_KEYS.HAS_NEXT, hasNext);
        return jo;
    }

    @Override
    public WidgetType getType() {

        return WidgetType.STRING;
    }

    @Override
    public void reSetDetailTarget() {

    }
}