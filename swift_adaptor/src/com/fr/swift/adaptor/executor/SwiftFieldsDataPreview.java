package com.fr.swift.adaptor.executor;

import com.finebi.conf.structure.bean.field.FineBusinessField;
import com.finebi.conf.structure.bean.table.FineBusinessTable;
import com.finebi.conf.structure.result.BIDetailCell;
import com.finebi.conf.structure.result.BIDetailTableResult;
import com.fr.general.ComparatorUtils;
import com.fr.swift.adaptor.struct.SwiftCombineDetailResult;
import com.fr.swift.adaptor.transformer.IndexingDataSourceFactory;
import com.fr.swift.source.DataSource;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * This class created on 2018-1-29 12:02:53
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public class SwiftFieldsDataPreview {

    private SwiftDBEngineExecutor swiftDBEngineExecutor;

    public SwiftFieldsDataPreview() {
        swiftDBEngineExecutor = new SwiftDBEngineExecutor();
    }

    public BIDetailTableResult getDetailPreviewByFields(LinkedHashMap<FineBusinessField, FineBusinessTable> fieldTableMap, int rowCount) throws Exception {
        List<List<BIDetailCell>> columnDataLists = new ArrayList<List<BIDetailCell>>();
        int realRowCount = 0;
        for (Map.Entry<FineBusinessField, FineBusinessTable> entry : fieldTableMap.entrySet()) {
            DataSource dataSource = IndexingDataSourceFactory.transformDataSource(entry.getValue());
            BIDetailTableResult detailTableResult = swiftDBEngineExecutor.getPreviewData(entry.getValue(), rowCount);
            int index = 0;
            for (int i = 1; i <= dataSource.getMetadata().getColumnCount(); i++) {
                if (ComparatorUtils.equals(entry.getKey().getName(), dataSource.getMetadata().getColumnName(i))) {
                    index = i;
                    break;
                }
            }
            List<BIDetailCell> columnDataList = new ArrayList<BIDetailCell>();
            if (index != 0) {
                while (detailTableResult.hasNext()) {
                    columnDataList.add(detailTableResult.next().get(index - 1));
                }
                realRowCount = columnDataList.size();
            }
            columnDataLists.add(columnDataList);
        }
        BIDetailTableResult result = new SwiftCombineDetailResult(columnDataLists, realRowCount);
        return result;
    }
}

