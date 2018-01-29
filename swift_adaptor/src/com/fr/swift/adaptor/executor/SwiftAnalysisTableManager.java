package com.fr.swift.adaptor.executor;

import com.finebi.base.constant.FineEngineType;
import com.finebi.conf.internalimp.analysis.operator.select.SelectFieldOperator;
import com.finebi.conf.internalimp.analysis.operator.select.SelectItem;
import com.finebi.conf.service.engine.analysis.EngineAnalysisTableManager;
import com.finebi.conf.structure.analysis.table.FineAnalysisTable;
import com.finebi.conf.structure.bean.field.FineBusinessField;
import com.finebi.conf.structure.bean.table.FineBusinessTable;
import com.finebi.conf.structure.result.BIDetailTableResult;
import com.fr.swift.adaptor.struct.SwiftDetailTableResult;
import com.fr.swift.adaptor.struct.SwiftEmptyResult;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * This class created on 2018-1-29 10:28:24
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public class SwiftAnalysisTableManager implements EngineAnalysisTableManager {

    @Override
    public BIDetailTableResult getPreViewResult(FineAnalysisTable table, int rowCount) {
        try {
            if (table.getBaseTable() == null) {
                SelectFieldOperator selectFieldOperators = table.getOperator();
                LinkedHashMap<FineBusinessField, FineBusinessTable> map = new LinkedHashMap<FineBusinessField, FineBusinessTable>();
                for (SelectItem selectItem : selectFieldOperators.getFields()) {
                    FineBusinessTable baseTable = selectItem.getTable();
                    FineBusinessField field = selectItem.getField();
                    map.put(field, baseTable);
                }
                return new SwiftFieldsDataPreview().getDetailPreviewByFields(map, rowCount);
            }
            return new SwiftDetailTableResult(new SwiftEmptyResult());
        } catch (Exception e) {
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
        return null;
    }

    @Override
    public void saveAnalysisTable(FineAnalysisTable table) throws Exception {

    }

    @Override
    public FineEngineType getEngineType() {
        return FineEngineType.Cube;
    }
}
