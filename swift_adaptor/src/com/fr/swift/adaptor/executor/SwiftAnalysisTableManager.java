package com.fr.swift.adaptor.executor;

import com.finebi.base.constant.FineEngineType;
import com.finebi.conf.constant.ConfConstant;
import com.finebi.conf.internalimp.analysis.bean.operator.add.group.custom.number.NumberMaxAndMinValue;
import com.finebi.conf.internalimp.analysis.bean.operator.sort.SortBean;
import com.finebi.conf.internalimp.analysis.bean.operator.sort.SortBeanItem;
import com.finebi.conf.service.engine.analysis.EngineAnalysisTableManager;
import com.finebi.conf.structure.analysis.operator.FineOperator;
import com.finebi.conf.structure.analysis.table.FineAnalysisTable;
import com.finebi.conf.structure.bean.field.FineBusinessField;
import com.finebi.conf.structure.filter.FineFilter;
import com.finebi.conf.structure.result.BIDetailTableResult;
import com.fr.swift.adaptor.preview.SwiftFieldsDataPreview;
import com.fr.swift.adaptor.transformer.FieldFactory;
import com.fr.swift.adaptor.transformer.IndexingDataSourceFactory;
import com.fr.swift.adaptor.transformer.SortFactory;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.query.sort.SortType;
import com.fr.swift.source.etl.ETLSource;
import com.fr.swift.structure.array.IntList;
import com.fr.swift.structure.array.IntListFactory;

import java.util.ArrayList;
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
            ETLSource dataSource = (ETLSource) IndexingDataSourceFactory.transformDataSource(table);
            FineOperator op = table.getOperator();
            IntList sortIndex = IntListFactory.createHeapIntList();
            List<SortType > sorts = new ArrayList<SortType>();
            if (op != null && op.getType() == ConfConstant.AnalysisType.SORT){
                List<SortBeanItem> sortBeanItemList = op.<SortBean>getValue().getValue();
                for (SortBeanItem sortBeanItem : sortBeanItemList) {
                    sortIndex.add(dataSource.getMetadata().getColumnIndex(sortBeanItem.getName()));
                    sorts.add(SortFactory.transformSort(sortBeanItem.getSortType()).getSortType());
                }
            }
            return swiftFieldsDataPreview.getDetailPreviewByFields(dataSource, rowCount, sortIndex, sorts);
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
        return null;
    }

    @Override
    public List<Object> getColumnValue(FineAnalysisTable table, String fieldName) {
        try {
            ETLSource dataSource = (ETLSource) IndexingDataSourceFactory.transformDataSource(table);
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
