package com.fr.bi.cal.analyze.report.report.widget.chart.filter;

import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.conf.table.BusinessTable;
import com.fr.bi.base.BICore;
import com.fr.bi.conf.report.widget.field.filtervalue.FilterValue;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.report.key.TargetGettingKey;
import com.fr.bi.stable.report.result.BINode;
import com.fr.bi.stable.report.result.DimensionCalculator;
import com.fr.general.ComparatorUtils;
import com.fr.general.FRLogger;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.stable.xml.XMLPrintWriter;
import com.fr.stable.xml.XMLableReader;

/**
 * Created by eason on 2017/5/26.
 */
public class DotChartAndFilter implements FilterValue<JSONObject> {

    FilterValue[] filters;
    JSONArray config;

    public DotChartAndFilter(JSONObject jo, long userId, JSONArray data){
        try {
            if(jo.has("filterValue")){
                this.config = jo.optJSONArray("filterValue");

                this.filters = new FilterValue[this.config.length()];

                for(int i = 0, count = this.filters.length; i < count; i++){
                    this.filters[i] = ChartFilterFactory.parseFilterValue(this.config.optJSONObject(i), userId, data);
                }
            }
        }catch (Exception e){
            FRLogger.getLogger().error(e.getMessage(), e);
        }
    }

    @Override
    public boolean isMatchValue(JSONObject v) {

        if(filters == null || config == null){
            return false;
        }

        for(int i = 0, len = filters.length; i < len; i++){

            JSONObject config = this.config.optJSONObject(i);
            String key = config.optString("key");
            double value = 0;
            if(ComparatorUtils.equals(key, "x")){
                value = v.optDouble("x", 0);
            }else if(ComparatorUtils.equals(key, "y")){
                value = v.optDouble("y", 0);
            }else if(ComparatorUtils.equals(key, "z")){
                value = v.optDouble("size", 0);
            }

            if(!filters[i].isMatchValue(value)){
                return false;
            }
        }

        return true;
    }

    @Override
    public void parseJSON(JSONObject jo, long userId) throws Exception {

    }

    @Override
    public BICore fetchObjectCore() {
        return null;
    }

    @Override
    public JSONObject createJSON() throws Exception {
        return null;
    }

    @Override
    public void readXML(XMLableReader xmLableReader) {

    }

    @Override
    public void writeXML(XMLPrintWriter xmlPrintWriter) {

    }

    @Override
    public GroupValueIndex createFilterIndex(DimensionCalculator dimension, BusinessTable target, ICubeDataLoader loader, long userId) {
        return null;
    }

    @Override
    public boolean canCreateFilterIndex() {
        return false;
    }

    @Override
    public boolean isAllCalculatorFilter() {
        return false;
    }

    @Override
    public boolean showNode(BINode node, TargetGettingKey targetKey, ICubeDataLoader loader) {
        return false;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return null;
    }
}
