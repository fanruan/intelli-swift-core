package com.fr.bi.etl.analysis.data;

import com.finebi.cube.api.ICubeDataLoader;
import com.fr.bi.common.inter.Traversal;
import com.fr.bi.conf.data.source.AbstractETLTableSource;
import com.fr.bi.conf.data.source.operator.IETLOperator;
import com.fr.bi.etl.analysis.Constants;
import com.fr.bi.stable.data.db.BIColumn;
import com.fr.bi.stable.data.db.BIDataValue;
import com.fr.bi.stable.data.db.DBField;
import com.fr.bi.stable.data.db.DBTable;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by 小灰灰 on 2015/12/14.
 */
public class AnalysisETLTableSource extends AbstractETLTableSource<IETLOperator, AnalysisTableSource> implements AnalysisTableSource{

    private transient Map<Long, UserTableSource> userBaseTableMap = new ConcurrentHashMap<Long, UserTableSource>();

    private int invalidIndex;

    private String id;

    private List<AnalysisETLSourceField> fieldList;

    @Override
    public DBTable getDbTable() {
        if (dbTable == null) {
            dbTable = new DBTable(null, fetchObjectCore().getID().getIdentityValue(), null);
            for (AnalysisETLSourceField c : fieldList){
                dbTable.addColumn(new BIColumn(c.getFieldName(), c.getFieldType()));
            }
        }
        return dbTable;
    }

    @Override
    public JSONObject createJSON() throws Exception {
        JSONObject jo = super.createJSON();
        if (fieldList != null){
            JSONArray ja = new JSONArray();
            for (AnalysisETLSourceField f : fieldList){
                ja.put(f.createJSON());
            }
            jo.put(Constants.FIELDS, ja);
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
    public long read(Traversal<BIDataValue> travel, DBField[] field, ICubeDataLoader loader) {
        throw new RuntimeException("Only UserTableSource can read");
    }

    public AnalysisETLTableSource(String id, List<AnalysisETLSourceField> fieldList) {
        this.id = id;
        this.fieldList = fieldList;
    }

    @Override
    public UserTableSource createUserTableSource(long userId) {
        UserTableSource source = userBaseTableMap.get(userId);
        if (source == null){
            synchronized (userBaseTableMap){
                UserTableSource tmp = userBaseTableMap.get(userId);
                if (tmp == null){
                    List<UserTableSource> parents = new ArrayList<UserTableSource>();
                    for (AnalysisTableSource parent : getParents()){
                        parents.add(parent.createUserTableSource(userId));
                    }
                    source = new UserETLTableSource(getETLOperators(), parents, userId);
                    userBaseTableMap.put(userId, source);
                } else {
                    source = tmp;
                }
            }
        }
        return source;
    }

}