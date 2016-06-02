package com.fr.bi.etl.analysis.data;

import com.finebi.cube.api.ICubeDataLoader;
import com.fr.bi.base.annotation.BICoreField;
import com.fr.bi.common.inter.Traversal;
import com.fr.bi.conf.data.source.AbstractETLTableSource;
import com.fr.bi.conf.data.source.operator.IETLOperator;
import com.fr.bi.etl.analysis.Constants;
import com.fr.bi.stable.data.db.*;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by 小灰灰 on 2015/12/14.
 */
public class AnalysisETLTableSource extends AbstractETLTableSource<IETLOperator, AnalysisCubeTableSource> implements AnalysisCubeTableSource {

    private transient Map<Long, UserCubeTableSource> userBaseTableMap = new ConcurrentHashMap<Long, UserCubeTableSource>();

    private int invalidIndex = -1;

    private String name;
    @BICoreField
    private List<AnalysisETLSourceField> fieldList;
//
//    @Override
//    public IPersistentTable getPersistentTable() {
//        if (dbTable == null) {
//            dbTable = new PersistentTable(null, fetchObjectCore().getID().getIdentityValue(), null);
//            for (AnalysisETLSourceField c : fieldList){
//                dbTable.addColumn(new PersistentField(c.getFieldName(), c.getFieldType()));
//            }
//        }
//        return dbTable;
//    }

    @Override
    public List<AnalysisETLSourceField> getFieldsList() {
        return fieldList;
    }

    @Override
    public JSONObject createJSON() throws Exception {
        JSONObject jo = super.createJSON();
        if (fieldList != null && !fieldList.isEmpty()){
            JSONArray ja = new JSONArray();
            for (AnalysisETLSourceField f : fieldList){
                ja.put(f.createJSON());
            }
            jo.put(Constants.FIELDS, ja);
        }
        jo.put("table_name", name);
        if (invalidIndex != -1){
            jo.put("invalidIndex", invalidIndex);
        }
        JSONArray tables = new JSONArray();
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
        return Constants.TABLE_TYPE.ETL;
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
        throw new RuntimeException("Only UserTableSource can read");
    }

    public AnalysisETLTableSource(List<AnalysisETLSourceField> fieldList, String name) {
        this.fieldList = fieldList;
        this.name = name;
    }

    @Override
    public UserCubeTableSource createUserTableSource(long userId) {
        UserCubeTableSource source = userBaseTableMap.get(userId);
        if (source == null){
            synchronized (userBaseTableMap){
                UserCubeTableSource tmp = userBaseTableMap.get(userId);
                if (tmp == null){
                    List<UserCubeTableSource> parents = new ArrayList<UserCubeTableSource>();
                    for (AnalysisCubeTableSource parent : getParents()){
                        parents.add(parent.createUserTableSource(userId));
                    }
                    source = new UserETLTableSource(getETLOperators(), parents, userId, fieldList);
                    userBaseTableMap.put(userId, source);
                } else {
                    source = tmp;
                }
            }
        }
        return source;
    }

}