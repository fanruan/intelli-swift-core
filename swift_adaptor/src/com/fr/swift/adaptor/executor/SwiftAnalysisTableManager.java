package com.fr.swift.adaptor.executor;

import com.finebi.base.constant.FineEngineType;
import com.finebi.conf.constant.ConfConstant;
import com.finebi.conf.internalimp.analysis.bean.operator.add.group.custom.number.NumberMaxAndMinValue;
import com.finebi.conf.internalimp.analysis.bean.operator.setting.FieldSettingBeanItem;
import com.finebi.conf.internalimp.analysis.operator.setting.FieldSettingOperator;
import com.finebi.conf.internalimp.field.FineBusinessFieldImp;
import com.finebi.conf.service.engine.analysis.EngineAnalysisTableManager;
import com.finebi.conf.structure.analysis.table.FineAnalysisTable;
import com.finebi.conf.structure.bean.field.FineBusinessField;
import com.finebi.conf.structure.filter.FineFilter;
import com.finebi.conf.structure.result.BIDetailTableResult;
import com.fr.swift.adaptor.preview.SwiftFieldsDataPreview;
import com.fr.swift.adaptor.transformer.FieldFactory;
import com.fr.swift.adaptor.transformer.IndexingDataSourceFactory;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.source.etl.EtlSource;

import java.util.List;

/**
 * This class created on 2018-1-29 10:28:24
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public class SwiftAnalysisTableManager implements EngineAnalysisTableManager {

    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(SwiftAnalysisTableManager.class);

    private SwiftFieldsDataPreview swiftFieldsDataPreview;

    public SwiftAnalysisTableManager() {
        swiftFieldsDataPreview = new SwiftFieldsDataPreview();
    }

    @Override
    public BIDetailTableResult getPreViewResult(FineAnalysisTable table, int rowCount) {
        try {
            //nice job foundation
            //字段设置居然要返回上一层的结果
            if (table.getOperator() != null && table.getOperator().getType() == ConfConstant.AnalysisType.FIELD_SETTING){
                table = table.getBaseTable();
            }
            return swiftFieldsDataPreview.getDetailPreviewByFields(table, rowCount);
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
        if (table.getOperator() != null && table.getOperator().getType() == ConfConstant.AnalysisType.FIELD_SETTING){
            List<FieldSettingBeanItem> fieldSettings = ((FieldSettingOperator) table.getOperator()).getValue().getValue();
            List<FineBusinessField> pFields = FieldFactory.transformColumns2Fields(IndexingDataSourceFactory.transformDataSource(table.getBaseTable()).getMetadata(),table.getId());
            for (int i = 0; i < pFields.size(); i++){
                if (!fieldSettings.isEmpty() && !fieldSettings.get(i).isUsed()){
                    ((FineBusinessFieldImp)(pFields.get(i))).setUsable(false);
                }
            }
            return pFields;
        }
        return FieldFactory.transformColumns2Fields(IndexingDataSourceFactory.transformDataSource(table).getMetadata(),table.getId());
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
            EtlSource dataSource = (EtlSource) IndexingDataSourceFactory.transformDataSource(table);
            return swiftFieldsDataPreview.getNumberMaxAndMinValue(dataSource, fieldName);
        } catch (Exception ignore) {
        }
        return new NumberMaxAndMinValue();
    }

    @Override
    public List<Object> getColumnValue(FineAnalysisTable table, String fieldName) {
        try {
            EtlSource dataSource = (EtlSource) IndexingDataSourceFactory.transformDataSource(table);
            return swiftFieldsDataPreview.getGroupPreviewByFields(dataSource, fieldName);
        } catch (Exception e) {
        }
        return null;
    }

    @Override
    public FineEngineType getEngineType() {
        return FineEngineType.Cube;
    }

}
