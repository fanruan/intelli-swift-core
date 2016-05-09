package com.fr.bi.etl.analysis.data;

import com.fr.bi.base.BIBasicCore;
import com.fr.bi.base.BIUser;
import com.fr.bi.cal.analyze.report.report.BIWidgetFactory;
import com.fr.bi.conf.report.BIWidget;
import com.fr.bi.etl.analysis.Constants;
import com.fr.bi.etl.analysis.manager.BIAnalysisETLManagerCenter;
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
                return new AnalysisBaseTableSource(createWidget(jo.getJSONObject("operator"), userId), type);
            case Constants.ETL_TYPE.SELECT_NONE_DATA :
                return new AnalysisBaseTableSource(createWidget(jo.getJSONObject("operator"), userId), type);
            default :
                AnalysisETLTableSource source = new AnalysisETLTableSource();
                JSONArray parents = jo.getJSONArray("parents");
                List<AnalysisTableSource> ps = new ArrayList<AnalysisTableSource>();
                for (int i = 0; i < parents.length(); i ++){
                    ps.add(createOneTableSource(parents.getJSONObject(i), userId));
                }
                if (jo.has("invalidIndex")){
                    source.setInvalidIndex(jo.getInt("invalidIndex"));
                }
                source.setParents(ps);
                source.setOperators(AnalysisETLOperatorFactory.createOperatorsByJSON(jo, userId));
                return source;
        }
    }

    private static BIWidget createWidget(JSONObject jo, long userId) throws Exception {
        if (jo.has("core")){
            AnalysisTableSource source = BIAnalysisETLManagerCenter.getDataSourceManager().getTableSourceByCore(BIBasicCore.generateValueCore(jo.getString("core")), new BIUser(userId));
            if (source.getType() == Constants.TABLE_TYPE.BASE){
                return ((AnalysisBaseTableSource)source).getWidget();
            }
        }
        return BIWidgetFactory.parseWidget(jo, userId);
    }

}