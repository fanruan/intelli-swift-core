package com.fr.swift.adaptor.preview;

import com.finebi.conf.structure.bean.field.FineBusinessField;
import com.finebi.conf.structure.bean.table.FineBusinessTable;
import com.finebi.conf.structure.result.BIDetailCell;
import com.finebi.conf.structure.result.BIDetailTableResult;
import com.fr.general.ComparatorUtils;
import com.fr.swift.adaptor.executor.SwiftTableEngineExecutor;
import com.fr.swift.adaptor.struct.SwiftCombineDetailResult;
import com.fr.swift.adaptor.struct.SwiftDetailTableResult;
import com.fr.swift.adaptor.struct.SwiftEmptyResult;
import com.fr.swift.adaptor.struct.SwiftSegmentDetailResult;
import com.fr.swift.adaptor.transformer.IndexingDataSourceFactory;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.query.sort.SortType;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.structure.array.IntList;

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

    private SwiftTableEngineExecutor swiftTableEngineExecutor;

    public SwiftFieldsDataPreview() {
        swiftTableEngineExecutor = new SwiftTableEngineExecutor();
    }

    public BIDetailTableResult getDetailPreviewByFields(DataSource dataSource, int rowCount, IntList sortIndex, List<SortType> sorts) throws Exception {

        try {
            if (dataSource != null) {
                MinorSegmentManager.getInstance().clear();
                if (!MinorSegmentManager.getInstance().isSegmentsExist(dataSource.getSourceKey())) {
                    MinorUpdater.update(dataSource);
                }
                List<Segment> segments = MinorSegmentManager.getInstance().getSegment(dataSource.getSourceKey());

                SwiftMetaData swiftMetaData = dataSource.getMetadata();
                BIDetailTableResult realDetailResult = new SwiftSegmentDetailResult(segments, swiftMetaData, sortIndex, sorts);
                return realDetailResult;
            }
            return new SwiftDetailTableResult(new SwiftEmptyResult());
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
            return new SwiftDetailTableResult(new SwiftEmptyResult());
        }
    }

    public BIDetailTableResult getDetailPreviewByFields(LinkedHashMap<FineBusinessField, FineBusinessTable> fieldTableMap, int rowCount) throws Exception {
        List<List<BIDetailCell>> columnDataLists = new ArrayList<List<BIDetailCell>>();
        int realRowCount = 0;
        for (Map.Entry<FineBusinessField, FineBusinessTable> entry : fieldTableMap.entrySet()) {
            DataSource dataSource = IndexingDataSourceFactory.transformDataSource(entry.getValue());
            BIDetailTableResult detailTableResult = swiftTableEngineExecutor.getPreviewData(entry.getValue(), rowCount);
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

    public List<Object> getGroupPreviewByFields(DataSource dataSource, String fieldName) throws Exception {

        try {
            if (dataSource != null) {
                if (!MinorSegmentManager.getInstance().isSegmentsExist(dataSource.getSourceKey())) {
                    MinorUpdater.update(dataSource);
                }
                List<Segment> segments = MinorSegmentManager.getInstance().getSegment(dataSource.getSourceKey());
                List<Object> list = new ArrayList<Object>();
                for (Segment sg : segments) {
                    Column c = sg.getColumn(new ColumnKey(fieldName));
                    DictionaryEncodedColumn dic = c.getDictionaryEncodedColumn();
                    for (int i = 0; i < dic.size(); i++) {
                        list.add(dic.getValue(i));
                    }
                }
                return list;
            }
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
        }
        return null;
    }
}

