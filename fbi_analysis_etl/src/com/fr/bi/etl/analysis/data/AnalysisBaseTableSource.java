package com.fr.bi.etl.analysis.data;

import com.finebi.cube.api.ICubeDataLoader;
import com.fr.bi.base.annotation.BICoreField;
import com.fr.bi.common.inter.Traversal;
import com.fr.bi.conf.report.BIWidget;
import com.fr.bi.conf.report.widget.field.dimension.BIDimension;
import com.fr.bi.etl.analysis.Constants;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.data.BITable;
import com.fr.bi.stable.data.Table;
import com.fr.bi.stable.data.db.BIColumn;
import com.fr.bi.stable.data.db.BIDataValue;
import com.fr.bi.stable.data.db.DBField;
import com.fr.bi.stable.data.db.PersistentTable;
import com.fr.bi.stable.data.source.AbstractCubeTableSource;
import com.fr.bi.stable.operation.group.IGroup;
import com.fr.bi.stable.utils.BIDBUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;

import java.sql.Types;
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
    private String name;

    public BIWidget getWidget() {
        return widget;
    }


    public AnalysisBaseTableSource(BIWidget widget, int etlType, List<AnalysisETLSourceField> fieldList, String name) {
        this.widget = widget;
        this.etlType = etlType;
        this.fieldList = fieldList;
        this.name = name;
    }


    @Override
    public PersistentTable getDbTable() {
        if (dbTable == null) {
            dbTable = new PersistentTable(null, fetchObjectCore().getID().getIdentityValue(), null);
            for (int i = 0; i < fieldList.size(); i++){
                AnalysisETLSourceField c = fieldList.get(i);
                int sqlType = (widget.getType() == BIReportConstant.WIDGET.TABLE && i < widget.getViewDimensions().length) ? getSqlTypeByGroupType(((BIDimension)widget.getViewDimensions()[i]).getGroup()) : BIDBUtils.biTypeToSql(c.getFieldType());
                dbTable.addColumn(new BIColumn(c.getFieldName(), sqlType));
            }

        }
        return dbTable;
    }

    private int getSqlTypeByGroupType(IGroup group) {
        switch (group.getType()){
            case BIReportConstant.GROUP.Y:
            case BIReportConstant.GROUP.M:
            case BIReportConstant.GROUP.S:
            case BIReportConstant.GROUP.MD:
                return Types.INTEGER;
            case BIReportConstant.GROUP.YMD:
                return Types.DATE;
            default:
                return Types.VARCHAR;
        }
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
                    source = new UserBaseTableSource(widget, etlType, userId, fieldList, name);
                    userBaseTableMap.put(userId, source);
                } else {
                    source = tmp;
                }
            }
        }
        return source;
    }

    @Override
    public List<AnalysisETLSourceField> getFieldsList() {
        return fieldList;
    }

    @Override
    public JSONObject createJSON() throws Exception {
        JSONObject jo =  super.createJSON();
        JSONObject widget = new JSONObject();
        if (etlType == Constants.ETL_TYPE.SELECT_NONE_DATA){
            widget.put("core", fetchObjectCore().getIDValue());
        }
        if (fieldList != null && !fieldList.isEmpty()){
            JSONArray ja = new JSONArray();
            for (AnalysisETLSourceField f : fieldList){
                ja.put(f.createJSON());
            }
            jo.put(Constants.FIELDS, ja);
        }
        jo.put("table_name", name);
        jo.put("etlType", etlType);
        jo.put("operator", widget);
        return jo;
    }
}