package com.fr.bi.sql.analysis.data;

import com.fr.bi.sql.analysis.Constants;
import com.fr.bi.sql.analysis.manager.BIAnalysisSQLManagerCenter;
import com.fr.bi.stable.data.BITableID;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.stable.StringUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by 小灰灰 on 2016/4/7.
 */
public class AnalysisSQLSourceFactory {
    public static AnalysisSQLTableSource createTableSource(JSONArray ja, long userId) throws Exception {
        if (ja.length() == 1){
            return createOneTableSource(ja.getJSONObject(0), userId);
        } else {
            List<AnalysisSQLTableSource> sources = new ArrayList<AnalysisSQLTableSource>();
            for (int i = 0; i < ja.length(); i++){
                sources.add(createOneTableSource(ja.getJSONObject(i), userId));
            }
            return new AnalysisSQLTempTableSource(sources);
        }
    }

    private static AnalysisSQLTableSource createOneTableSource(JSONObject jo, long userId) throws Exception {
        int type = jo.getInt(Constants.ETLTYPE);
        List<AnalysisSQLSourceField> fieldList = new ArrayList<AnalysisSQLSourceField>();
        if (jo.has(Constants.FIELDS)){
            JSONArray ja = jo.getJSONArray(Constants.FIELDS);
            for (int i = 0; i < ja.length(); i++){
                AnalysisSQLSourceField field = new AnalysisSQLSourceField();
                field.parseJSON(ja.getJSONObject(i));
                fieldList.add(field);
            }
        }
        String name = jo.optString(Constants.TABLE_NAME, StringUtils.EMPTY);
        switch (type){
            case Constants.ETL_TYPE.SELECT_DATA :
                return new AnalysisSQLBaseTableSource(jo.getString(Constants.DB_NAME), name, fieldList);
            case Constants.ETL_TYPE.SELECT_TABLE :
                String id = jo.getString(Constants.TABLE_ID);
                AnalysisSQLTableSource idSource = (AnalysisSQLTableSource) BIAnalysisSQLManagerCenter.getBusiPackManager().getTable(id, userId).getSource();
                return new AnalysisSQLIDTableSource(new BITableID(id),  fieldList, idSource);
            default :
                JSONArray parents = jo.getJSONArray("parents");
                List<AnalysisSQLTableSource> ps = new ArrayList<AnalysisSQLTableSource>();
                for (int i = 0; i < parents.length(); i ++){
                    ps.add(createOneTableSource(parents.getJSONObject(i), userId));
                }
                SQLETLTableSource source = new SQLETLTableSource(fieldList, name,AnalysisETLOperatorFactory.createOperatorsByJSON(jo, userId), ps );
                if (jo.has("invalidIndex")){
                source.setInvalidIndex(jo.getInt("invalidIndex"));
            }
            return source;
        }
    }
}