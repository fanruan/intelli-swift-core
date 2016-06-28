package com.fr.bi.etl.analysis.data;

import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.conf.table.BusinessTable;
import com.fr.bi.base.annotation.BICoreField;
import com.fr.bi.common.inter.Traversal;
import com.fr.bi.common.persistent.xml.BIIgnoreField;
import com.fr.bi.conf.report.BIWidget;
import com.fr.bi.conf.report.widget.field.BITargetAndDimension;
import com.fr.bi.conf.report.widget.field.dimension.BIDimension;
import com.fr.bi.etl.analysis.Constants;
import com.fr.bi.field.target.detailtarget.BIAbstractDetailTarget;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.constant.DBConstant;
import com.fr.bi.stable.data.BITable;
import com.fr.bi.stable.data.db.*;
import com.fr.bi.stable.data.source.AbstractCubeTableSource;
import com.fr.bi.stable.data.source.CubeTableSource;
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
public class AnalysisBaseTableSource extends AbstractCubeTableSource implements AnalysisCubeTableSource {
    @BIIgnoreField
    private transient Map<Long, UserCubeTableSource> userBaseTableMap = new ConcurrentHashMap<Long, UserCubeTableSource>();
    @BICoreField
    protected BIWidget widget;
    private int etlType;
    @BICoreField
    private List<AnalysisETLSourceField> fieldList;
    private String name;
    private String widgetTableId;
    public BIWidget getWidget() {
        return widget;
    }


    public AnalysisBaseTableSource(BIWidget widget, int etlType, List<AnalysisETLSourceField> fieldList, String name, String widgetTableId) {
        this.widget = widget;
        this.etlType = etlType;
        this.fieldList = fieldList;
        this.name = name;
        this.widgetTableId = widgetTableId;
    }


    @Override
    public IPersistentTable getPersistentTable() {
        if (dbTable == null) {
            dbTable = new PersistentTable(null, fetchObjectCore().getID().getIdentityValue(), null);
            for (int i = 0; i < fieldList.size(); i++){
                AnalysisETLSourceField c = fieldList.get(i);
                int sqlType = i < widget.getViewDimensions().length? getSqlType(i) : BIDBUtils.biTypeToSql(c.getFieldType());
                dbTable.addColumn(new PersistentField(c.getFieldName(), sqlType));
            }

        }
        return dbTable;
    }

    private int getSqlType(int index) {
        if (widget.getType() != BIReportConstant.WIDGET.DETAIL){
            return getTableWidgetSqlType(index);
        } else {
            return getDetailWidgetSqlType(index);
        }
    }

    private int getDetailWidgetSqlType(int index) {
        BIAbstractDetailTarget target = (BIAbstractDetailTarget) widget.getDimensions()[index];
        if (target.isCalculateTarget()) {
           return Types.DOUBLE;
        }  else if(target.getStatisticElement().getFieldType() == DBConstant.COLUMN.NUMBER) {
            return BIDBUtils.classTypeToSql(target.getStatisticElement().getClassType()) ;
        }else {
            return getTypeByGroup(target.getGroup());
        }
    }

    private int getTableWidgetSqlType(int index){
        BIDimension dim = (BIDimension) widget.getDimensions()[index];
        if (dim.getStatisticElement().getFieldType() == DBConstant.COLUMN.NUMBER){
            return (dim.getGroup().getType() == BIReportConstant.GROUP.NO_GROUP
                    || dim.getGroup().getType() == BIReportConstant.GROUP.ID_GROUP) ?
                    BIDBUtils.classTypeToSql(dim.getStatisticElement().getClassType())  : Types.VARCHAR;
        } else {
            return getTypeByGroup(dim.getGroup());
        }
    }

    private int getTypeByGroup(IGroup group) {
        switch (group.getType()){
            case BIReportConstant.GROUP.Y:
            case BIReportConstant.GROUP.M:
            case BIReportConstant.GROUP.S:
            case BIReportConstant.GROUP.MD:
            case BIReportConstant.GROUP.W:
                return Types.INTEGER;
            case BIReportConstant.GROUP.YMD:
            case BIReportConstant.GROUP.YMDHMS:
                return Types.DATE;
            default:
                return Types.VARCHAR;
        }
    }



    public Set<BusinessTable> createTableKeys() {
        Set set = new HashSet<BusinessTable>();
        set.add(new BITable(fetchObjectCore().getIDValue()));
        return set;
    }

    @Override
    public int getType() {
        return Constants.TABLE_TYPE.BASE;
    }

    @Override
    public long read(Traversal<BIDataValue> travel, ICubeFieldSource[] field, ICubeDataLoader loader) {
        return 0;
    }

    @Override
    public UserCubeTableSource createUserTableSource(long userId) {
        UserCubeTableSource source = userBaseTableMap.get(userId);
        if (source == null){
            synchronized (userBaseTableMap){
                UserCubeTableSource tmp = userBaseTableMap.get(userId);
                if (tmp == null){
                    source = new UserBaseTableSource(widget, etlType, userId, fieldList, name, widgetTableId);
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
    public void getSourceUsedAnalysisETLSource(Set<AnalysisCubeTableSource> set) {
        if(set.contains(this)){
            return;
        }
        for (BITargetAndDimension dim : widget.getViewDimensions()){
            if (dim.createTableKey() != null && dim.createTableKey().getTableSource() != null){
                CubeTableSource source = dim.createTableKey().getTableSource();
                if (source.getType() ==  Constants.TABLE_TYPE.BASE || source.getType() ==  Constants.TABLE_TYPE.ETL){
                    ((AnalysisCubeTableSource)source).getSourceUsedAnalysisETLSource(set);
                }
            }
        }
        for (BITargetAndDimension target : widget.getViewTargets()){
            if (target.createTableKey() != null && target.createTableKey().getTableSource() != null){
                CubeTableSource source = target.createTableKey().getTableSource();
                if (source.getType() ==  Constants.TABLE_TYPE.BASE || source.getType() ==  Constants.TABLE_TYPE.ETL){
                    ((AnalysisCubeTableSource)source).getSourceUsedAnalysisETLSource(set);
                }
            }
        }
        set.add(this);
    }

    @Override
    public JSONObject createJSON() throws Exception {
        JSONObject jo =  super.createJSON();
        JSONObject widget = new JSONObject();
        if (etlType == Constants.ETL_TYPE.SELECT_NONE_DATA){
            widget.put("widgetTableId", widgetTableId);
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

    /**
     * @return
     */
    @Override
    public Set<CubeTableSource> getSourceUsedBaseSource(Set<CubeTableSource> set, Set<CubeTableSource> helper) {
        if(helper.contains(this)){
            return set;
        }
        helper.add(this);
        for (BITargetAndDimension dim : widget.getViewDimensions()){
            if (dim.createTableKey() != null && dim.createTableKey().getTableSource() != null){
                dim.createTableKey().getTableSource().getSourceUsedBaseSource(set, helper);
            }
        }
        for (BITargetAndDimension target : widget.getViewTargets()){
            if (target.createTableKey() != null && target.createTableKey().getTableSource() != null){
                target.createTableKey().getTableSource().getSourceUsedBaseSource(set, helper);
            }
        }
        return set;
    }
}