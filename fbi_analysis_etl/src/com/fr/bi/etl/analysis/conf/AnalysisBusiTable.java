package com.fr.bi.etl.analysis.conf;

import com.fr.bi.conf.base.pack.data.BIBusinessTable;
import com.fr.bi.etl.analysis.Constants;
import com.fr.bi.etl.analysis.manager.BIAnalysisETLManagerCenter;
import com.fr.bi.stable.data.source.ITableSource;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.json.JSONObject;

/**
 * Created by 小灰灰 on 2015/12/11.
 */
public class AnalysisBusiTable extends BIBusinessTable {

    private String describe;

    public AnalysisBusiTable(String id, long userId) {
        super(id, userId);
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public void setSource(ITableSource source){
        this.source = source;
    }

    @Override
    public ITableSource getSource() {
        if (source == null) {
            try {
                source = BIAnalysisETLManagerCenter.getDataSourceManager().getTableSourceByID(getID(), getUser());
            } catch (Exception e) {
                BILogger.getLogger().error(e.getMessage(), e);
            }
        }
        if (source == null) {
            BILogger.getLogger().info("UserEtl source missed");
        }
        return source;
    }

    @Override
    public JSONObject createJSON() throws Exception {
        JSONObject jo = super.createJSON();
        jo.put("describe", describe);
        return jo;
    }

    protected int getTableType(){
        return Constants.BUSINESS_TABLE_TYPE.ANALYSIS_TYPE;
    }

}