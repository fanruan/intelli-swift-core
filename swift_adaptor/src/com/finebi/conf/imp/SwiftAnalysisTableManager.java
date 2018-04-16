package com.finebi.conf.imp;

import com.finebi.base.constant.FineEngineType;
import com.finebi.conf.constant.BIConfConstants;
import com.finebi.conf.constant.ConfConstant;
import com.finebi.conf.internalimp.analysis.bean.operator.add.AddNewColumnBean;
import com.finebi.conf.internalimp.analysis.bean.operator.add.AddNewColumnValueBean;
import com.finebi.conf.internalimp.analysis.bean.operator.add.gettime.GetFieldTimeValueBean;
import com.finebi.conf.internalimp.analysis.bean.operator.add.gettime.GetFieldTimeValueItem;
import com.finebi.conf.internalimp.analysis.bean.operator.add.group.custom.number.NumberMaxAndMinValue;
import com.finebi.conf.internalimp.analysis.bean.operator.group.DimensionSelectValue;
import com.finebi.conf.internalimp.analysis.bean.operator.group.DimensionValueBean;
import com.finebi.conf.internalimp.analysis.bean.operator.group.GroupBean;
import com.finebi.conf.internalimp.analysis.bean.operator.group.GroupDoubleValueBean;
import com.finebi.conf.internalimp.analysis.bean.operator.group.GroupValueBean;
import com.finebi.conf.internalimp.analysis.bean.operator.group.ViewBean;
import com.finebi.conf.internalimp.analysis.bean.operator.setting.FieldSettingBeanItem;
import com.finebi.conf.internalimp.analysis.operator.setting.FieldSettingOperator;
import com.finebi.conf.internalimp.field.FineBusinessFieldImp;
import com.finebi.conf.service.engine.analysis.EngineAnalysisTableManager;
import com.finebi.conf.structure.analysis.operator.FineOperator;
import com.finebi.conf.structure.analysis.table.FineAnalysisTable;
import com.finebi.conf.structure.bean.field.FineBusinessField;
import com.finebi.conf.structure.filter.FineFilter;
import com.finebi.conf.structure.result.BIDetailTableResult;
import com.fr.swift.adaptor.transformer.DataSourceFactory;
import com.fr.swift.adaptor.transformer.FieldFactory;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.provider.DataProvider;
import com.fr.swift.provider.impl.SwiftDataProvider;
import com.fr.swift.source.DataSource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class created on 2018-1-29 10:28:24
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public class SwiftAnalysisTableManager implements EngineAnalysisTableManager {

    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(SwiftAnalysisTableManager.class);

    private DataProvider dataProvider;

    public SwiftAnalysisTableManager() {
        this.dataProvider = new SwiftDataProvider();
    }

    @Override
    public BIDetailTableResult getPreViewResult(FineAnalysisTable table, int rowCount) {
        try {
            //字段设置居然要返回上一层的结果
            if (table.getOperator() != null && table.getOperator().getType() == ConfConstant.AnalysisType.FIELD_SETTING) {
                table = table.getBaseTable();
            }
            return dataProvider.getDetailPreviewByFields(table, rowCount);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public boolean delTable(FineAnalysisTable table) {
        return false;
    }

    @Override
    public boolean addTable(FineAnalysisTable table) {
        return false;
    }

    @Override
    public List<FineAnalysisTable> getAllAnalysistTable() {
        return null;
    }

    @Override
    public List<FineBusinessField> getFields(FineAnalysisTable table) throws Exception {
        //nice job foundation
        //字段设置居然要返回上一层的结果
        try {
            Map<String, Integer> groupMap = checkGroupByOperator(table);
            List<FineBusinessField> fields;
            if (table.getOperator() != null && table.getOperator().getType() == ConfConstant.AnalysisType.FIELD_SETTING) {
                List<FieldSettingBeanItem> fieldSettings = ((FieldSettingOperator) table.getOperator()).getValue().getValue();
                fields = FieldFactory.transformColumns2Fields(DataSourceFactory.transformDataSource(table.getBaseTable()).getMetadata(), table.getId());
                for (int i = 0; i < fields.size(); i++) {
                    if (!fieldSettings.isEmpty()) {
                        ((FineBusinessFieldImp) (fields.get(i))).setEnable(fieldSettings.get(i).isUsed());
                        ((FineBusinessFieldImp) (fields.get(i))).setUsable(fieldSettings.get(i).isUsed());
                        ((FineBusinessFieldImp) (fields.get(i))).setName(fieldSettings.get(i).getName());
                    }
                }
            } else {
                fields = FieldFactory.transformColumns2Fields(DataSourceFactory.transformDataSource(table).getMetadata(), table.getId());
            }
            if (!groupMap.isEmpty()){
                for (FineBusinessField field : fields){
                    if (groupMap.containsKey(field.getName())){
                        field.setFieldGroupType(groupMap.get(field.getName()));
                    }
                }
            }
            return fields;
        } catch (Exception e) {
            LOGGER.error(e);
        }
        return new ArrayList<FineBusinessField>();
    }

    /**
     * nice job 居然要引擎处理格式。。。
     * @param table
     * @return
     */
    private Map<String, Integer> checkGroupByOperator(FineAnalysisTable table) {
        Map<String, Integer> groupMap = new HashMap<String, Integer>();
        while (table != null && table.getOperator() != null){
            FineOperator op = table.getOperator();
            if (op.getType() == ConfConstant.AnalysisType.GROUP){
                GroupBean bean = op.getValue();
                GroupValueBean valueBean = bean.getValue();
                Map<String, DimensionValueBean> dimensionBean = valueBean.getDimensions();
                ViewBean viewBean = valueBean.getView();
                List<String> dimensions = viewBean.getDimension();
                if (!dimensionBean.isEmpty() && dimensions != null) {
                    for (int i = 0; i < dimensions.size(); i++){
                        DimensionValueBean tempBean = dimensionBean.get(dimensions.get(i));
                        List<DimensionSelectValue> value = tempBean.getValue();
                        DimensionSelectValue selectValue = value.get(0);
                        if (selectValue.getType() == BIConfConstants.CONF.GROUP.TYPE.DOUBLE){
                            groupMap.put(tempBean.getName(), ((GroupDoubleValueBean) selectValue).getChildValue());
                        }
                    }
                }

            }
            if (op.getType() == ConfConstant.AnalysisType.ADD_COLUMN){
                AddNewColumnBean bean = op.getValue();
                AddNewColumnValueBean value = bean.getValue();
                if (value.getType() == BIConfConstants.CONF.ADD_COLUMN.TIME.TYPE){
                    GetFieldTimeValueItem tempBean = ((GetFieldTimeValueBean) value).getValue();
                    groupMap.put(value.getName(), tempBean.getUnit());
                }
            }
            table = table.getBaseTable();
        }
        return groupMap;
    }

    @Override
    public void saveAnalysisTable(FineAnalysisTable table) {

    }

    @Override
    public BIDetailTableResult getPreViewResult(FineAnalysisTable table, FineFilter filter, int rowCount) {
        return null;
    }

    @Override
    public NumberMaxAndMinValue getNumberMaxAndMinValue(FineAnalysisTable table, String fieldName) {

        try {
            DataSource dataSource = DataSourceFactory.transformDataSource(table);
            return dataProvider.getNumberMaxAndMinValue(dataSource, fieldName);
        } catch (Exception ignore) {
        }
        return new NumberMaxAndMinValue();
    }

    @Override
    public List<Object> getColumnValue(FineAnalysisTable table, String fieldName) {
        try {
            DataSource dataSource = DataSourceFactory.transformDataSource(table);
            return dataProvider.getGroupPreviewByFields(dataSource, fieldName);
        } catch (Exception e) {
            LOGGER.error(e);
        }
        return new ArrayList<Object>();
    }

    @Override
    public FineEngineType getEngineType() {
        return FineEngineType.Cube;
    }

}
