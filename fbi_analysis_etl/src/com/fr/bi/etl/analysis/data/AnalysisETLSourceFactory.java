package com.fr.bi.etl.analysis.data;

import com.fr.bi.cal.analyze.report.report.BIWidgetFactory;
import com.fr.bi.etl.analysis.Constants;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * Created by 小灰灰 on 2016/4/7.
 */
public class AnalysisETLSourceFactory {
    public static AnalysisTableSource createTableSource(JSONObject jo, long userId) throws Exception {
        if (jo.names() != null && jo.names().length() == 1){
            return createOneTableSource(jo.getJSONObject((String) jo.keys().next()), userId);
        } else {
            Iterator<String> it = jo.keys();
            List<AnalysisTableSource> sources = new ArrayList<AnalysisTableSource>();
            while (it.hasNext()){
                sources.add(createOneTableSource(jo.getJSONObject(it.next()), userId));
            }
            return new AnalysisTempTableSource(sources);
        }
    }

    private static AnalysisTableSource createOneTableSource(JSONObject jo, long userId) throws Exception {
        int type = jo.getInt("type");
        switch (type){
            case Constants.ETL_TYPE.SELECT_DATA :
                return new AnalysisBaseTableSource(BIWidgetFactory.parseWidget(jo.getJSONObject("value"), userId), type);
            case Constants.ETL_TYPE.SELECT_NONE_DATA :
                return new AnalysisBaseTableSource(BIWidgetFactory.parseWidget(jo.getJSONObject("value"), userId), type);
            default :
                AnalysisETLTableSource source = new AnalysisETLTableSource();
                JSONObject tableJSON = jo.getJSONObject("value");
                JSONArray parents = tableJSON.getJSONArray("parents");
                List<AnalysisTableSource> ps = new ArrayList<AnalysisTableSource>();
                for (int i = 0; i < parents.length(); i ++){
                    ps.add(createTableSource(parents.getJSONObject(i), userId));
                }
                source.setParents(ps);
                source.setOperators(AnalysisETLOperatorFactory.createOperatorsByJSON(tableJSON.getJSONObject("oprators"), userId));
                return source;
        }
    }


}