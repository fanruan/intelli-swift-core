package com.fr.bi.sql.analysis.data;

import com.finebi.cube.api.ICubeDataLoader;
import com.fr.bi.base.annotation.BICoreField;
import com.fr.bi.common.inter.Traversal;
import com.fr.bi.conf.data.source.AbstractETLTableSource;
import com.fr.bi.conf.data.source.operator.IETLOperator;
import com.fr.bi.sql.analysis.Constants;
import com.fr.bi.stable.data.db.BIDataValue;
import com.fr.bi.stable.data.db.ICubeFieldSource;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;

import java.util.List;
import java.util.Set;

/**
 * Created by 小灰灰 on 2015/12/14.
 */
public class SQLETLTableSource extends AbstractETLTableSource<IETLOperator, AnalysisSQLTableSource> implements AnalysisSQLTableSource {


    private int invalidIndex = -1;

    private String name;
    @BICoreField
    private List<AnalysisSQLSourceField> fieldList;

    @Override
    public List<AnalysisSQLSourceField> getFieldsList() {
        return fieldList;
    }

    @Override
    public String toSql() {
        return null;
    }

    @Override
    public void getSourceUsedAnalysisSQLSource(Set<AnalysisSQLTableSource> sources, Set<AnalysisSQLTableSource> helper) {
        if(helper.contains(this)){
            return;
        }
        helper.add(this);
        for (AnalysisSQLTableSource source : getParents()){
            source.getSourceUsedAnalysisSQLSource(sources, helper);
        }
    }


    @Override
    public JSONObject createJSON() throws Exception {
        JSONObject jo = super.createJSON();
        if (fieldList != null && !fieldList.isEmpty()){
            JSONArray ja = JSONArray.create();
            for (AnalysisSQLSourceField f : fieldList){
                ja.put(f.createJSON());
            }
            jo.put(Constants.FIELDS, ja);
        }
        jo.put("table_name", name);
        if (invalidIndex != -1){
            jo.put("invalidIndex", invalidIndex);
        }
        JSONArray tables = JSONArray.create();
        for (int i = 0; i < parents.size(); i++) {
            tables.put(parents.get(i).createJSON());
        }
        jo.put(Constants.PARENTS, tables);
        AnalysisETLOperatorFactory.createJSONByOperators(jo,oprators);
        return jo;
    }

    public void setInvalidIndex(int invalidIndex) {
        this.invalidIndex = invalidIndex;
    }

    @Override
    public int getType() {
        return Constants.TABLE_TYPE.SQL_ETL;
    }

    /**
     * 写简单索引
     *
     * @param travel
     * @param field
     * @param loader
     * @return
     */
    @Override
    public long read(Traversal<BIDataValue> travel, ICubeFieldSource[] field, ICubeDataLoader loader) {
        return 0;
    }

    public SQLETLTableSource(List<AnalysisSQLSourceField> fieldList, String name, List<IETLOperator> operators, List<AnalysisSQLTableSource> parents) {
        super(operators, parents);
        this.fieldList = fieldList;
        this.name = name;
    }


}