package com.fr.bi.etl.analysis.data;

import com.finebi.cube.api.ICubeDataLoader;
import com.fr.bi.base.annotation.BICoreField;
import com.fr.bi.common.inter.Traversal;
import com.fr.bi.conf.report.BIWidget;
import com.fr.bi.etl.analysis.Constants;
import com.fr.bi.stable.data.BITable;
import com.fr.bi.stable.data.Table;
import com.fr.bi.stable.data.db.BIColumn;
import com.fr.bi.stable.data.db.BIDataValue;
import com.fr.bi.stable.data.db.DBField;
import com.fr.bi.stable.data.db.DBTable;
import com.fr.bi.stable.data.source.AbstractCubeTableSource;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Created by 小灰灰 on 2015/12/21.
 */
public class AnalysisBaseTableSource extends AbstractCubeTableSource implements AnalysisTableSource {
    private transient Map<Long, UserTableSource> userBaseTableMap = new ConcurrentHashMap<Long, UserTableSource>();
    @BICoreField
    protected BIWidget widget;
    private int etlType;
    private List<AnalysisETLSourceField> fieldList;

    public BIWidget getWidget() {
        return widget;
    }


    public AnalysisBaseTableSource(BIWidget widget, int etlType, List<AnalysisETLSourceField> fieldList) {
        this.widget = widget;
        this.etlType = etlType;
        this.fieldList = fieldList;
    }


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
    public Set<Table> createTableKeys() {
        Set set = new HashSet<Table>();
        set.add(new BITable(fetchObjectCore().getIDValue()));
        return set;
    }

    @Override
    public int getType() {
        return Constants.TABLE_TYPE.BASE;
    }

    @Override
    public long read(Traversal<BIDataValue> travel, DBField[] field, ICubeDataLoader loader) {
        return 0;
    }

    @Override
    public UserTableSource createUserTableSource(long userId) {
        UserTableSource source = userBaseTableMap.get(userId);
        if (source == null){
            synchronized (userBaseTableMap){
                UserTableSource tmp = userBaseTableMap.get(userId);
                if (tmp == null){
                    source = new UserBaseTableSource(widget, etlType, userId, fieldList);
                    userBaseTableMap.put(userId, source);
                } else {
                    source = tmp;
                }
            }
        }
        return source;
    }

    @Override
    public JSONObject createJSON() throws Exception {
        JSONObject jo =  super.createJSON();
        JSONObject widget = new JSONObject();
        if (etlType == Constants.ETL_TYPE.SELECT_NONE_DATA){
            widget.put("core", fetchObjectCore().getIDValue());
        }
        if (fieldList != null){
            JSONArray ja = new JSONArray();
            for (AnalysisETLSourceField f : fieldList){
                ja.put(f.createJSON());
            }
            jo.put(Constants.FIELDS, ja);
        }
        jo.put("etlType", etlType);
        jo.put("operator", widget);
        return jo;
    }
}