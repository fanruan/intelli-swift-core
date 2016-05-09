package com.fr.bi.etl.analysis.data;

import com.fr.bi.cal.analyze.report.report.BIWidgetFactory;
import com.fr.bi.etl.analysis.Constants;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by 小灰灰 on 2016/4/7.
 */
public class AnalysisETLSourceFactory {
    public static AnalysisTableSource createTableSource(JSONArray ja, long userId) throws Exception {
        if (ja.length() == 1){
            return createOneTableSource(ja.getJSONObject(0), userId);
        } else {
            List<AnalysisTableSource> sources = new ArrayList<AnalysisTableSource>();
            for (int i = 0; i < ja.length(); i++){
                sources.add(createOneTableSource(ja.getJSONObject(i), userId));
            }
            return new AnalysisTempTableSource(sources);
        }
    }

    private static AnalysisTableSource createOneTableSource(JSONObject jo, long userId) throws Exception {
        int type = jo.getInt("etlType");
        switch (type){
            case Constants.ETL_TYPE.SELECT_DATA :
                return new AnalysisBaseTableSource(BIWidgetFactory.parseWidget(jo.getJSONObject("operator"), userId), type);
            case Constants.ETL_TYPE.SELECT_NONE_DATA :
                return new AnalysisBaseTableSource(BIWidgetFactory.parseWidget(jo.getJSONObject("operator"), userId), type);
            default :
                AnalysisETLTableSource source = new AnalysisETLTableSource();
                JSONObject tableJSON = jo.getJSONObject("value");
                JSONArray parents = tableJSON.getJSONArray("parents");
                List<AnalysisTableSource> ps = new ArrayList<AnalysisTableSource>();
                for (int i = 0; i < parents.length(); i ++){
                    ps.add(createOneTableSource(parents.getJSONObject(i), userId));
                }
                if (jo.has("invalidIndex")){
                    source.setInvalidIndex(jo.getInt("invalidIndex"));
                }
                source.setParents(ps);
                source.setOperators(AnalysisETLOperatorFactory.createOperatorsByJSON(tableJSON.getJSONObject("oprators"), userId));
                return source;
        }
    }


}