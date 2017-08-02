package com.fr.bi.etl.analysis.data;

import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.conf.table.BusinessTable;
import com.fr.bi.base.annotation.BICoreField;
import com.fr.bi.cal.analyze.report.report.widget.AbstractBIWidget;
import com.fr.bi.cal.analyze.report.report.widget.SummaryWidget;
import com.fr.bi.common.inter.Traversal;
import com.fr.bi.common.persistent.xml.BIIgnoreField;
import com.fr.bi.conf.report.BIWidget;
import com.fr.bi.conf.report.WidgetType;
import com.fr.bi.conf.report.widget.field.BITargetAndDimension;
import com.fr.bi.conf.report.widget.field.dimension.BIDimension;
import com.fr.bi.conf.report.widget.field.target.BITarget;
import com.fr.bi.conf.report.widget.field.target.filter.TargetFilter;
import com.fr.bi.etl.analysis.Constants;
import com.fr.bi.etl.analysis.manager.BIAnalysisETLManagerCenter;
import com.fr.bi.etl.analysis.monitor.*;
import com.fr.bi.exception.BIKeyAbsentException;
import com.fr.bi.field.target.detailtarget.BIAbstractDetailTarget;
import com.fr.bi.field.target.target.BISummaryTarget;
import com.fr.bi.stable.constant.BIBaseConstant;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.constant.DBConstant;
import com.fr.bi.stable.data.BITable;
import com.fr.bi.stable.data.db.*;
import com.fr.bi.stable.data.source.AbstractCubeTableSource;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.operation.group.IGroup;
import com.fr.bi.stable.utils.BIDBUtils;
import com.fr.general.ComparatorUtils;
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
public class
AnalysisBaseTableSource extends AbstractCubeTableSource implements AnalysisCubeTableSource {
    private static final long serialVersionUID = 2465659786011088351L;
    @BIIgnoreField
    private Map<SourceKey, UserCubeTableSource> userBaseTableMap = new ConcurrentHashMap<SourceKey, UserCubeTableSource>();
    @BICoreField
    protected BIWidget widget;
    protected int etlType;
    @BICoreField
    protected List<AnalysisETLSourceField> fieldList;
    protected String name;
    protected String widgetTableId;

    @Override
    public Set<BIWidget> getWidgets() {
        Set<BIWidget> widgets = new HashSet<BIWidget>();
        widgets.add(widget);
        return widgets;
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
            for (int i = 0; i < fieldList.size(); i++) {
                AnalysisETLSourceField c = fieldList.get(i);
                if (widget != null){
                    int sqlType = i < widget.getViewDimensions().length ? getSqlType(i) : BIDBUtils.biTypeToSql(c.getFieldType());
                    dbTable.addColumn(new PersistentField(c.getFieldName(), sqlType));
                }
            }

        }
        return dbTable;
    }

    private int getSqlType(int index) {
        if (widget.getType() != WidgetType.DETAIL) {
            return getTableWidgetSqlType(index);
        } else {
            return getDetailWidgetSqlType(index);
        }
    }

    private int getDetailWidgetSqlType(int index) {
        BIAbstractDetailTarget target = (BIAbstractDetailTarget) widget.getDimensions()[index];
        if (target.isCalculateTarget()) {
            return Types.DOUBLE;
        } else if (target.getStatisticElement() == null) {
            return Types.VARCHAR;
        } else if (target.getStatisticElement().getFieldType() == DBConstant.COLUMN.NUMBER) {
            return BIDBUtils.classTypeToSql(target.getStatisticElement().getClassType());
        } else {
            return getTypeByGroup(target.getGroup());
        }
    }

    private int getTableWidgetSqlType(int index) {
        BIDimension dim = (BIDimension) widget.getDimensions()[index];
        if (dim.getStatisticElement() == null) {
            return Types.VARCHAR;
        } else if (dim.getStatisticElement() == null) {
            return Types.VARCHAR;
        } else if (dim.getStatisticElement().getFieldType() == DBConstant.COLUMN.NUMBER) {
            return (dim.getGroup().getType() == BIReportConstant.GROUP.NO_GROUP
                    || dim.getGroup().getType() == BIReportConstant.GROUP.ID_GROUP) ?
                    BIDBUtils.classTypeToSql(dim.getStatisticElement().getClassType()) : Types.VARCHAR;
        } else {
            return getTypeByGroup(dim.getGroup());
        }
    }

    private int getTypeByGroup(IGroup group) {
        int type = Types.VARCHAR;
        switch (group.getType()) {
            case BIReportConstant.GROUP.Y:
            case BIReportConstant.GROUP.M:
            case BIReportConstant.GROUP.S:
            case BIReportConstant.GROUP.MD:
            case BIReportConstant.GROUP.W:
                type = Types.INTEGER;
                break;
            case BIReportConstant.GROUP.YMD:
            case BIReportConstant.GROUP.YMDHMS:
                type = Types.DATE;
                break;
        }

        return type;
    }


    public Set<BusinessTable> createTableKeys() {
        Set set = new HashSet<BusinessTable>();
        set.add(new BITable(fetchObjectCore().getIDValue()));
        return set;
    }

    @Override
    public int getType() {
        return BIBaseConstant.TABLE_TYPE.BASE;
    }

    @Override
    public long read(Traversal<BIDataValue> travel, ICubeFieldSource[] field, ICubeDataLoader loader) {
        return 0;
    }

    @Override
    public UserCubeTableSource createUserTableSource(long userId) {
        List<TargetFilter> filters = ((AbstractBIWidget)widget).getAuthFilter(userId);
        SourceKey key = new SourceKey(filters);
        UserCubeTableSource source = userBaseTableMap.get(key);
        if (source == null) {
            synchronized (userBaseTableMap) {
                UserCubeTableSource tmp = userBaseTableMap.get(key);
                if (tmp == null) {
                    source = new UserBaseTableSource(this, userId);
                    userBaseTableMap.put(key, source);
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
        if (!set.contains(this)) {
            set.add(this);
        }
        for (BITargetAndDimension dim : widget.getViewDimensions()) {
            if (dim.getStatisticElement() != null && dim.createTableKey() != null && dim.createTableKey().getTableSource() != null) {
                CubeTableSource source = dim.createTableKey().getTableSource();
                if (source.getType() == BIBaseConstant.TABLE_TYPE.BASE || source.getType() == BIBaseConstant.TABLE_TYPE.ETL) {
//                   通知更新，只通知直接上层
                    if (!set.contains(source)) {
                        set.add((AnalysisCubeTableSource) source);
                    }
                }
            }
        }
        for (BITargetAndDimension target : widget.getViewTargets()) {
            if (target.getStatisticElement() != null && target.createTableKey() != null && target.createTableKey().getTableSource() != null) {
                CubeTableSource source = target.createTableKey().getTableSource();
                if (source.getType() == BIBaseConstant.TABLE_TYPE.BASE || source.getType() == BIBaseConstant.TABLE_TYPE.ETL) {
                    //测试，通知更新，只通知直接上层
                    if (!set.contains(source)) {
                        set.add((AnalysisCubeTableSource) source);
                    }
                }
            }
        }
    }

    public void getSourceNeedCheckSource(Set<AnalysisCubeTableSource> set) {
        if (set.contains(this)) {
            return;
        }
        set.add(this);
    }

    public void refreshWidget() {
        widget.refreshSources();
        widget.reSetDetailTarget();
        try {
            widget.refreshColumns();
        } catch (Exception e) {
            BILoggerFactory.getLogger(this.getClass()).error(e.getMessage(), e);
        }
    }

    @Override
    public void refresh() {
        refreshWidget();
    }

    public void resetTargetsMap(){
        if (widget instanceof SummaryWidget){
            BISummaryTarget[] targets = ((SummaryWidget) widget).getTargets();
            Map<String, BITarget> targetMap = new ConcurrentHashMap<String, BITarget>();
            for (int i = 0; i < targets.length; i++) {
                targets[i].setSummaryIndex(i);
                targetMap.put(targets[i].getValue(), targets[i]);
                targets[i].setTargetMap(targetMap);
            }
        }
        if (widget != null){
            for (BITargetAndDimension dim : widget.getViewDimensions()) {
                if (dim.getStatisticElement() != null && dim.createTableKey() != null && dim.createTableKey().getTableSource() != null) {
                    CubeTableSource source = dim.createTableKey().getTableSource();
                    if (source.getType() == BIBaseConstant.TABLE_TYPE.BASE || source.getType() == BIBaseConstant.TABLE_TYPE.ETL) {
                        ((AnalysisCubeTableSource)source).resetTargetsMap();
                    }
                }
            }
            for (BITargetAndDimension target : widget.getViewTargets()) {
                if (target.getStatisticElement() != null && target.createTableKey() != null && target.createTableKey().getTableSource() != null) {
                    CubeTableSource source = target.createTableKey().getTableSource();
                    if (source.getType() == BIBaseConstant.TABLE_TYPE.BASE || source.getType() == BIBaseConstant.TABLE_TYPE.ETL) {
                        ((AnalysisCubeTableSource)source).resetTargetsMap();
                    }
                }
            }
        }
    }

    public void reSetWidgetDetailGetter() {
        widget.reSetDetailTarget();
    }

    public TableRelationTree getAllProcessAnalysisTablesWithRelation() {
        Set<SimpleTable> set = new HashSet<SimpleTable>();
        getParentAnalysisBaseTableIds(set);
        TableRelationTree tree = new TableRelationTree(new BaseSourceTable(this));
        for (SimpleTable id : set) {
            tree.addParent(new TableRelationTree(id));
        }
        return tree;
    }

    public void getParentAnalysisBaseTableIds(Set<SimpleTable> set) {
        for (BITargetAndDimension dim : widget.getViewDimensions()) {
            calculateImport(set, dim);
        }
        for (BITargetAndDimension target : widget.getViewTargets()) {
            calculateImport(set, target);
        }
    }

    private void calculateImport(Set<SimpleTable> set, BITargetAndDimension dim) {
        if (dim.getStatisticElement() != null) {
            if(dim.createTableKey() != null && dim.createTableKey().getTableSource() != null) {
                CubeTableSource source = dim.createTableKey().getTableSource();
                String id = dim.getStatisticElement().getTableBelongTo().getID().getIdentity();
                if (source.getType() == BIBaseConstant.TABLE_TYPE.BASE || source.getType() == BIBaseConstant.TABLE_TYPE.ETL) {
                    try {
                        //如果被删掉了就继续想上找
                        if( BIAnalysisETLManagerCenter.getDataSourceManager().getTableSource(dim.createTableKey()) != null){
                            set.add(new SimpleTable(id));
                        } else {
                            getParentAnalysisBaseTableIds(set);
                        }
                    } catch (BIKeyAbsentException e) {
                        set.add(new UETable(id));
                    }
                } else {
                    set.add(new CubeTable(id));
                }
            } else {
                set.add(new CubeTable(dim.getStatisticElement().getTableBelongTo().getID().getIdentity()));
            }
        }
    }


    @Override
    public JSONObject createJSON() throws Exception {
        JSONObject jo = super.createJSON();
        JSONObject widget = new JSONObject();
        if (etlType == Constants.ETL_TYPE.SELECT_NONE_DATA) {
            widget.put("widgetTableId", widgetTableId);
        }
        if (fieldList != null && !fieldList.isEmpty()) {
            JSONArray ja = new JSONArray();
            for (AnalysisETLSourceField f : fieldList) {
                ja.put(f.createJSON());
            }
            jo.put(Constants.FIELDS, ja);
        }
        jo.put("tableName", name);
        jo.put("etlType", etlType);
        jo.put("operator", widget);
        return jo;
    }
    /**
     * @return
     */
    @Override
    public Set<CubeTableSource> getSourceUsedBaseSource(Set<CubeTableSource> set, Set<CubeTableSource> helper) {
        if (helper.contains(this)) {
            return set;
        }
        helper.add(this);
        set.add(this);
        //                分析表网上走一层，找到自己的父表，
/*        for (BITargetAndDimension dim : widget.getViewDimensions()) {
            if (dim.createTableKey() != null && dim.createTableKey().getTableSource() != null) {
                dim.createTableKey().getTableSource().getSourceUsedBaseSource(set, helper);
            }
        }
        for (BITargetAndDimension target : widget.getViewTargets()) {
            if (target.createTableKey() != null && target.createTableKey().getTableSource() != null) {
                target.createTableKey().getTableSource().getSourceUsedBaseSource(set, helper);
            }
        }*/
        return set;
    }

    @Override
    public String getModuleName() {
        return BIBaseConstant.MODULE_NAME.ANALYSIS_ETL_MODULE;
    }

    public void clearUserBaseTableMap() {
        userBaseTableMap.clear();
        widget.refreshColumns();
    }

    private class SourceKey{
        private List<TargetFilter> targetFilterList;

        public SourceKey(List<TargetFilter> targetFilterList) {
            this.targetFilterList = targetFilterList;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            SourceKey widgetKey = (SourceKey) o;
            return targetFilterList != null ? ComparatorUtils.equals(targetFilterList, widgetKey.targetFilterList) : widgetKey.targetFilterList == null;

        }

        @Override
        public int hashCode() {
            return targetFilterList != null ? targetFilterList.hashCode() : 0;
        }
    }

}