package com.fr.bi.etl.analysis.conf;

import com.finebi.cube.conf.field.BIBusinessField;
import com.finebi.cube.conf.field.BusinessField;
import com.finebi.cube.conf.table.BIBusinessTable;
import com.fr.bi.etl.analysis.Constants;
import com.fr.bi.etl.analysis.manager.BIAnalysisETLManagerCenter;
import com.fr.bi.stable.data.BIFieldID;
import com.fr.bi.stable.data.db.PersistentField;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 小灰灰 on 2015/12/11.
 */
public class AnalysisBusiTable extends BIBusinessTable {

    private String describe;
    private long userId;

    public AnalysisBusiTable(String id, long userId) {
        super(id, "");
        this.userId = userId;
    }

    public long getUserId() {
        return userId;
    }

    public void setSource(CubeTableSource source) {
        this.source = source;
        initFields();
    }


    private void initFields() {
        String tableId = getID().getIdentity();
        List<BusinessField> fields = new ArrayList<BusinessField>();
        for (PersistentField f : source.getPersistentTable().getFieldList()){
            fields.add(new BIBusinessField(this, new BIFieldID(tableId + f.getFieldName()), f.getFieldName(), f.getType(), f.getColumnSize()));
        }
        setFields(fields);
    }

    public CubeTableSource getSource() {
        if (source == null) {
            try {
                source = BIAnalysisETLManagerCenter.getDataSourceManager().getTableSource(this);
            } catch (Exception e) {
                BILogger.getLogger().error(e.getMessage(), e);
            }
        }
        if (source == null) {
            BILogger.getLogger().info("UserEtl source missed");
        }
        return source;
    }

    public String getDescribe() {
        return describe;
    }

    public JSONObject createJSON() throws Exception {
        JSONObject jo = super.createJSON();
        jo.put("describe", describe);
        return jo;
    }

    @Override
    public void setFields(List<BusinessField> fields) {
        super.setFields(fields);
        if (fields != null){
            List<String> usedFieldNames = new ArrayList<String>();
            for (BusinessField f : fields){
                usedFieldNames.add(f.getFieldName());
            }
            setUsedFieldNames(usedFieldNames);
        }
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    protected int getTableType() {
        return Constants.BUSINESS_TABLE_TYPE.ANALYSIS_TYPE;
    }

}