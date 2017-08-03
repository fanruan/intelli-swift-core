package com.fr.bi.cal.analyze.report.report.widget;

import com.finebi.cube.api.ICubeColumnIndexReader;
import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.api.ICubeTableService;
import com.finebi.cube.api.ICubeValueEntryGetter;
import com.finebi.cube.conf.table.BusinessTable;
import com.finebi.cube.relation.BITableSourceRelation;
import com.fr.bi.cal.analyze.cal.index.loader.CubeIndexLoader;
import com.fr.bi.cal.analyze.cal.result.Node;
import com.fr.bi.cal.analyze.executor.utils.GlobalFilterUtils;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.conf.report.WidgetType;
import com.fr.bi.conf.report.widget.field.dimension.BIDimension;
import com.fr.bi.conf.session.BISessionProvider;
import com.fr.bi.report.result.DimensionCalculator;
import com.fr.bi.stable.constant.BIJSONConstant;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.constant.DBConstant;
import com.fr.bi.stable.gvi.GVIUtils;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.io.sortlist.ArrayLookupHelper;
import com.fr.bi.stable.utils.BICollectionUtils;
import com.fr.bi.stable.utils.program.BIJsonUtils;
import com.fr.bi.stable.utils.program.BIPhoneticismUtils;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;
import com.fr.stable.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

public class StringControlWidget extends TableWidget {

    private static final int STEP = 100;

    private static final long serialVersionUID = 8869194713947245611L;

    private int data_type = -2;

    private int times = -1;

    private String selectedValues;

    private String[] keywords = new String[0];

    private boolean needDoLoadGroup = false;

    private BISession session = null;

    @Override
    public int isOrder() {

        return 0;
    }

    public JSONObject createDataJSON(BISessionProvider session, HttpServletRequest req) throws Exception {

        this.session = (BISession) session;
        BIDimension dimension = getDimensions()[0];
        DimensionCalculator calculator = dimension.createCalculator(dimension.getStatisticElement(), new ArrayList<BITableSourceRelation>());
        Set<String> selectedValue = new HashSet<String>();

        if (selectedValues != null && StringUtils.isNotEmpty(selectedValues)) {
            JSONArray selectedValueArray = new JSONArray(selectedValues);
            selectedValue.addAll(Arrays.asList(BIJsonUtils.jsonArray2StringArray(selectedValueArray)));
        }

        if (needDoLoadGroup) {
            Node node = CubeIndexLoader.getInstance(session.getUserId()).loadGroup(this, getViewTargets(), getViewDimensions(), getViewDimensions(), getTargets(), -1, true, (BISession) session);
            List<Object> list = new ArrayList<Object>();
            for (Node child : node.getChilds()) {
                list.add(child.getShowValue());
            }
            return getCustomGroupResult(list, selectedValue, calculator);
        } else {
            GroupValueIndex gvi = createFilterGVI(new DimensionCalculator[]{calculator}, dimension.getStatisticElement().getTableBelongTo(), session.getLoader(), session.getUserId());
            ICubeColumnIndexReader reader = calculator.createNoneSortGroupValueMapGetter(dimension.getStatisticElement().getTableBelongTo(), session.getLoader());

            if (dimension.getGroup() != null && dimension.getGroup().getType() != BIReportConstant.GROUP.ID_GROUP && dimension.getGroup().getType() != BIReportConstant.GROUP.NO_GROUP) {
                return getCustomGroupResult(gvi, reader, selectedValue, calculator);
            } else {
                ICubeTableService ti = session.getLoader().getTableIndex(dimension.getStatisticElement().getTableBelongTo().getTableSource());
                ICubeValueEntryGetter getter = ti.getValueEntryGetter(dimension.createKey(dimension.getStatisticElement()), new ArrayList<BITableSourceRelation>());
                return createIDGroupIndex(gvi, reader, selectedValue, getter, calculator.getComparator());
            }
        }
    }

    public GroupValueIndex createFilterGVI(DimensionCalculator[] row, BusinessTable targetKey, ICubeDataLoader loader, long userId) {

        GroupValueIndex gvi = super.createFilterGVI(row, targetKey, loader, userId);
        // 跳转的
        BusinessTable target = getBaseTable();
        if (getGlobalFilterWidget() != null) {
            // 如果已经设置了源字段和目标字段
            if (((AbstractBIWidget) getGlobalFilterWidget()).getGlobalSourceAndTargetFieldList().size() > 0) {
                gvi = GVIUtils.AND(gvi, GlobalFilterUtils.getSettingSourceAndTargetJumpFilter(this, userId, session, target, ((AbstractBIWidget) getGlobalFilterWidget()).getBaseTable()));
            } else {
                gvi = GVIUtils.AND(gvi, GlobalFilterUtils.getNotSettingSourceAndTargetJumpFilter(session, target, this, false));
            }
        }
        return gvi;
    }

    private enum SearchMode {
        PY, START_WITH
    }

    private JSONObject switchGetResultMethod(ICubeColumnIndexReader reader, Set<String> selectedValue, SimpleIntArray groupArray, SearchMode mode) throws JSONException {

        if (data_type == DBConstant.REQ_DATA_TYPE.REQ_GET_DATA_LENGTH) {
            return JSONObject.create().put(BIJSONConstant.JSON_KEYS.VALUE, getSearchCount(reader, selectedValue, groupArray, mode));
        }
        if (data_type == DBConstant.REQ_DATA_TYPE.REQ_GET_ALL_DATA || times < 1) {
            return getSearchResult(reader, selectedValue, 0, groupArray.size(), groupArray, mode);
        } else {
            return getSearchResult(reader, selectedValue, (times - 1) * STEP, times * STEP, groupArray, mode);
        }
    }

    //超过50w只搜索开头是
    private static final int START_WITH_LIMIT = 500000;

    private JSONObject createIDGroupIndex(GroupValueIndex gvi, ICubeColumnIndexReader reader, Set<String> selectedValue, final ICubeValueEntryGetter getter, Comparator comparator) throws JSONException {

        SearchMode mode = SearchMode.PY;
        int start = 0, end = getter.getGroupSize();
        final int[] limitStarts = new int[keywords.length];
        int[] limitEnds = new int[keywords.length];
        Arrays.fill(limitStarts, 0);
        Arrays.fill(limitEnds, 0);
        if (getter.getGroupSize() > START_WITH_LIMIT) {
            Arrays.fill(limitEnds, end);
            mode = SearchMode.START_WITH;
            for (int i = 0, len = keywords.length; i < len; i++) {
                start = ArrayLookupHelper.getStartIndex4StartWith(reader, keywords[i], comparator);
                end = ArrayLookupHelper.getEndIndex4StartWith(reader, keywords[i], comparator) + 1;
                limitStarts[i] = start;
                limitEnds[i] = end;
            }
        } else {
            if (limitEnds.length > 0) {
                limitEnds[0] = end;
            }
        }
        SimpleIntArray groupArray = this.createGroupArray(start, end, limitStarts, limitEnds, getter, gvi);
        return switchGetResultMethod(reader, selectedValue, groupArray, mode);
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

    private JSONObject getCustomGroupResult(List<Object> list, Set<String> selectedValue, DimensionCalculator calculator) throws JSONException {

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
        String[] keys = keywords;
        if (keys.length == 0) {
            keys = new String[]{""};
        }
        for (int i = 0; i < array.size(); i++) {
            Object ob = reader.getGroupValue(array.get(i));
            if (ob == null) {
                continue;
            }
            String str = ob.toString();
            for (String keyword : keys) {
                if (match(str, keyword, selectedValue, mode)) {
                    count++;
                    break;
                }
            }
        }
        return count;
    }

    private int getSearchCount(Set selectedValue, List<Object> list) {

        int count = 0;
        String[] keys = keywords;
        if (keys.length == 0) {
            keys = new String[]{""};
        }
        for (Object ob : list) {
            if (ob == null) {
                continue;
            }
            for (String keyword : keys) {
                if (match(ob.toString(), keyword, selectedValue, SearchMode.PY)) {
                    count++;
                    break;
                }
            }
        }
        return count;
    }

    private boolean match(String value, String keyword, Set selectedValue, SearchMode mode) {

        keyword = keyword.toLowerCase();
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
        return strPinyin.contains(keyword) || value.toLowerCase().contains(keyword);
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
            String keyword = treeJo.optString("keyword", "");
            if (keyword.isEmpty()) {
                if (treeJo.has("keywords")) {
                    JSONArray keyArray = treeJo.optJSONArray("keywords");
                    keywords = new String[keyArray.length()];
                    for (int i = 0, len = keyArray.length(); i < len; i++) {
                        keywords[i] = keyArray.optString(i, StringUtils.EMPTY);
                    }
                } else {
                    keywords = new String[0];
                }
            } else {
                keywords = new String[1];
                keywords[0] = keyword;
            }
        }
        if (this.getTargets().length > 0) {
            needDoLoadGroup = true;
        }
    }

    private boolean getReserveList(List<Object> list, Set selectedValue, int matched, int start, int end, List<String> find, List<String> match) {

        boolean hasNext = false;
        String[] keys = keywords;
        if (keys.length == 0) {
            keys = new String[]{""};
        }
        outer:
        for (int i = list.size() - 1; i > -1; i--) {
            Object ob = list.get(i);
            if (ob == null) {
                continue;
            }
            String str = ob.toString();
            for (String keyword : keys) {
                if (match(str, keyword, selectedValue, SearchMode.PY)) {
                    if (matched >= start && matched < end) {
                        if (ComparatorUtils.equals(keyword, str)) {
                            match.add(str);
                        } else {
                            find.add(str);
                        }
                    } else if (matched >= end) {
                        hasNext = true;
                        break outer;
                    }
                    matched++;
                    break;
                }
            }
        }
        return hasNext;
    }

    private boolean getList(List<Object> list, Set selectedValue, int matched, int start, int end, List<String> find, List<String> match) {

        boolean hasNext = false;
        String[] keys = keywords;
        if (keys.length == 0) {
            keys = new String[]{""};
        }
        outer:
        for (Object ob : list) {
            if (ob == null) {
                continue;
            }
            String str = ob.toString();
            for (String keyword : keys) {
                if (match(str, keyword, selectedValue, SearchMode.PY)) {
                    if (matched >= start && matched < end) {
                        if (ComparatorUtils.equals(keyword, str)) {
                            match.add(str);
                        } else {
                            find.add(str);
                        }
                    } else if (matched >= end) {
                        hasNext = true;
                        break outer;
                    }
                    matched++;
                    break;
                }
            }
        }
        return hasNext;
    }

    private JSONObject getSearchResult(Set selectedValue, int start, int end, List<Object> list, DimensionCalculator calculator) throws JSONException {

        JSONArray ja = JSONArray.create();
        JSONObject jo = JSONObject.create();
        boolean hasNext = false;
        List<String> find = new ArrayList<String>();
        List<String> match = new ArrayList<String>();
        int matched = 0;
        if (getDimensions()[0].getSort().getSortType() == BIReportConstant.SORT.NUMBER_DESC || getDimensions()[0].getSort().getSortType() == BIReportConstant.SORT.DESC) {
            hasNext = getReserveList(list, selectedValue, matched, start, end, find, match);
        } else {
            hasNext = getList(list, selectedValue, matched, start, end, find, match);
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

    private boolean getReserveListWithArray(ICubeColumnIndexReader reader, SimpleIntArray array, Set selectedValue, int matched, int start, int end, List<String> find, List<String> match, SearchMode mode) {

        boolean hasNext = false;
        String[] keys = keywords;
        if (keys.length == 0) {
            keys = new String[]{""};
        }
        outer:
        for (int i = array.size() - 1; i > -1; i--) {
            Object ob = reader.getGroupValue(array.get(i));
            String str = BICollectionUtils.isCubeNullKey(ob) ? StringUtils.EMPTY : ob.toString();
            for (String keyword : keys) {
                if (match(str, keyword, selectedValue, mode)) {
                    if (matched >= start && matched < end) {
                        if (StringUtils.isNotEmpty(keyword) && ComparatorUtils.equals(keyword, str)) {
                            match.add(str);
                        } else {
                            find.add(str);
                        }
                    } else if (matched >= end) {
                        hasNext = true;
                        break outer;
                    }
                    matched++;
                    break;
                }
            }
        }
        return hasNext;
    }

    private boolean getListWithArray(ICubeColumnIndexReader reader, SimpleIntArray array, Set selectedValue, int matched, int start, int end, List<String> find, List<String> match, SearchMode mode) {

        boolean hasNext = false;
        String[] keys = keywords;
        if (keys.length == 0) {
            keys = new String[]{""};
        }
        outer:
        for (int i = 0; i < array.size(); i++) {
            Object ob = reader.getGroupValue(array.get(i));
            String str = null;
            if (BICollectionUtils.isCubeNullKey(ob)) {
                str = StringUtils.EMPTY;
            } else {
                str = ob.toString();
            }
            for (String keyword : keys) {
                if (match(str, keyword, selectedValue, mode)) {
                    if (matched >= start && matched < end) {
                        if (StringUtils.isNotEmpty(keyword) && ComparatorUtils.equals(keyword, str)) {
                            match.add(str);
                        } else {
                            find.add(str);
                        }
                    } else if (matched >= end) {
                        hasNext = true;
                        break outer;
                    }
                    matched++;
                    break;
                }
            }
        }
        return hasNext;
    }


    private JSONObject getSearchResult(ICubeColumnIndexReader reader, Set selectedValue, int start, int end, SimpleIntArray array, SearchMode mode) throws JSONException {

        JSONArray ja = JSONArray.create();
        JSONObject jo = JSONObject.create();
        boolean hasNext = false;
        List<String> find = new ArrayList<String>();
        List<String> match = new ArrayList<String>();
        int matched = 0;
        if (getDimensions()[0].getSort().getSortType() == BIReportConstant.SORT.NUMBER_DESC || getDimensions()[0].getSort().getSortType() == BIReportConstant.SORT.DESC) {
            hasNext = getReserveListWithArray(reader, array, selectedValue, matched, start, end, find, match, mode);
        } else {
            hasNext = getListWithArray(reader, array, selectedValue, matched, start, end, find, match, mode);
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
    public WidgetType getType() {

        return WidgetType.STRING;
    }

}