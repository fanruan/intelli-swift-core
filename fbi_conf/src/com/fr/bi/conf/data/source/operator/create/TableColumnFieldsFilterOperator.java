package com.fr.bi.conf.data.source.operator.create;

import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.conf.table.BIBusinessTable;
import com.fr.bi.base.annotation.BICoreField;
import com.fr.bi.conf.report.widget.field.target.filter.TargetFilter;
import com.fr.bi.field.target.filter.TargetFilterFactory;
import com.fr.bi.field.target.filter.general.GeneralANDFilter;
import com.fr.bi.field.target.filter.general.GeneralFilter;
import com.fr.bi.field.target.filter.general.GeneralORFilter;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.constant.DBConstant;
import com.fr.bi.stable.data.BITableID;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.json.JSONTransform;
import com.fr.stable.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 小灰灰 on 2016/5/11.
 */
public class TableColumnFieldsFilterOperator extends AbstractTableColumnFilterOperator {

    public static final String XML_TAG = "TableColumnFieldsFilterOperator";
    @BICoreField
    private List<FilterItem> filterList;
    private int type = BIReportConstant.FILTER_TYPE.AND;

    public TableColumnFieldsFilterOperator(long userId) {
        super(userId);
    }

    public TableColumnFieldsFilterOperator() {

    }

    @Override
    public String xmlTag() {
        return XML_TAG;
    }

    protected GroupValueIndex createFilterIndex(List<? extends CubeTableSource> parents, ICubeDataLoader loader) {
        if (filterList == null || filterList.isEmpty()) {
            return loader.getTableIndex(getSingleParentMD5(parents)).getAllShowIndex();
        }
        GroupValueIndex gvi = null;
        for (CubeTableSource parent : parents) {
            //TODO Connery 这里有问题Mark
            BIBusinessTable businessTable = new BIBusinessTable(new BITableID(StringUtils.EMPTY));
            businessTable.setSource(parent);
            GroupValueIndex temp = createFilter().createFilterIndex(businessTable, loader, loader.getUserId());
            if (gvi == null) {
                gvi = temp;
            } else {
                gvi = gvi.AND(temp);
            }
        }
        return gvi;
    }


    private TargetFilter createFilter() {
        GeneralFilter gf = this.type == BIReportConstant.FILTER_TYPE.AND ? new GeneralANDFilter() : new GeneralORFilter();
        TargetFilter[] children = new TargetFilter[filterList.size()];
        for (int i = 0; i < filterList.size(); i++) {
            children[i] = filterList.get(i).createFilter();
        }
        gf.setChilds(children);
        return gf;
    }

    /**
     * 将Java对象转换成JSON对象
     *
     * @return json对象
     * @throws Exception
     */
    @Override
    public JSONObject createJSON() throws Exception {
        JSONObject jo = new JSONObject();
        jo.put("type", this.type);
        if (filterList != null) {
            JSONArray ja = new JSONArray();
            jo.put("items", ja);
            for (FilterItem item : filterList) {
                ja.put(item.createJSON());
            }
        }
        return jo;
    }

    /**
     * 将JSON对象转换成java对象
     *
     * @param jo json对象
     * @throws Exception
     */
    @Override
    public void parseJSON(JSONObject jo) throws Exception {
        if (jo.has("type")) {
            this.type = jo.getInt("type");
        }
        if (jo.has("items")) {
            JSONArray ja = jo.getJSONArray("items");
            filterList = new ArrayList<FilterItem>();
            for (int i = 0; i < ja.length(); i++) {
                FilterItem item = new FilterItem();
                item.parseJSON(ja.getJSONObject(i));
                filterList.add(item);
            }
        }
    }

    private class FilterItem implements JSONTransform {
        @BICoreField
        private List<TargetFilter> filter;
        @BICoreField
        private String fieldName = StringUtils.EMPTY;
        @BICoreField
        private int fieldType = DBConstant.COLUMN.NUMBER;
        @BICoreField
        private int type = BIReportConstant.FILTER_TYPE.AND;

        @Override
        public JSONObject createJSON() throws Exception {
            JSONObject jo = new JSONObject();
            jo.put("type", this.type);
            jo.put("field_type", this.fieldType);
            jo.put("field_name", this.fieldName);
            if (filter != null) {
                JSONArray value = new JSONArray();
                for (TargetFilter f : filter) {
                    value.put(f.createJSON());
                }
                jo.put("value", value);
            }
            return jo;
        }

        @Override
        public void parseJSON(JSONObject jo) throws Exception {
            this.fieldName = jo.optString("field_name", StringUtils.EMPTY);
            this.fieldType = jo.optInt("field_type", DBConstant.COLUMN.NUMBER);
            this.type = jo.optInt("type", BIReportConstant.FILTER_TYPE.AND);
            if (jo.has("value")) {
                filter = new ArrayList<TargetFilter>();
                JSONArray ja = jo.getJSONArray("value");
                for (int i = 0; i < ja.length(); i++) {
                    filter.add(TargetFilterFactory.parseFilter(ja.getJSONObject(i), user.getUserId()));
                }
            }
        }

        private TargetFilter createFilter() {
            if (filter == null) {
                return null;
            }
            GeneralFilter gf = this.type == BIReportConstant.FILTER_TYPE.AND ? new GeneralANDFilter() : new GeneralORFilter();
            gf.setChilds(filter.toArray(new TargetFilter[filter.size()]));
            return gf;
        }

        public boolean hasAllCalculatorFilter() {
            for (TargetFilter f : filter) {
                if (f != null && f.hasAllCalculatorFilter()) {
                    return true;
                }
            }
            return false;
        }
    }

    @Override
    protected boolean hasAllCalculatorFilter() {
        for (FilterItem item : filterList) {
            if (item.hasAllCalculatorFilter()) {
                return true;
            }
        }
        return false;
    }
}
