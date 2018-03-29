package com.fr.swift.adaptor.preview;

import com.finebi.conf.constant.BICommonConstants;
import com.finebi.conf.constant.ConfConstant;
import com.finebi.conf.internalimp.analysis.bean.operator.add.group.custom.number.NumberMaxAndMinValue;
import com.finebi.conf.internalimp.analysis.bean.operator.sort.SortBean;
import com.finebi.conf.internalimp.analysis.bean.operator.sort.SortBeanItem;
import com.finebi.conf.structure.analysis.operator.FineOperator;
import com.finebi.conf.structure.analysis.table.FineAnalysisTable;
import com.finebi.conf.structure.bean.table.FineBusinessTable;
import com.finebi.conf.structure.result.BIDetailTableResult;
import com.fr.swift.adaptor.struct.SwiftDetailTableResult;
import com.fr.swift.adaptor.struct.SwiftEmptyResult;
import com.fr.swift.adaptor.struct.SwiftSegmentDetailResult;
import com.fr.swift.adaptor.transformer.IndexingDataSourceFactory;
import com.fr.swift.adaptor.transformer.SortFactory;
import com.fr.swift.generate.preview.MinorSegmentManager;
import com.fr.swift.generate.preview.MinorUpdater;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.query.sort.SortType;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.etl.ETLSource;
import com.fr.swift.structure.array.IntList;
import com.fr.swift.structure.array.IntListFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * This class created on 2018-1-29 12:02:53
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public class SwiftFieldsDataPreview {


    public BIDetailTableResult getDetailPreviewByFields(FineBusinessTable table, int rowCount) throws Exception {

        DataSource dataSource = IndexingDataSourceFactory.transformDataSource(table);
        IntList sortIndex = IntListFactory.createHeapIntList();
        List<SortType> sorts = new ArrayList<SortType>();
        //分析表排序加上属性
        List<SortBeanItem> sortBeanItemList = getSortItems(table);
        for (SortBeanItem sortBeanItem : sortBeanItemList) {
            sortIndex.add(dataSource.getMetadata().getColumnIndex(sortBeanItem.getName()));
            sorts.add(SortFactory.transformSort(sortBeanItem.getSortType()).getSortType());
        }
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

    public NumberMaxAndMinValue getNumberMaxAndMinValue(ETLSource dataSource, String fieldName) {
        try {
            double max,min;
            if (dataSource != null) {
                if (!MinorSegmentManager.getInstance().isSegmentsExist(dataSource.getSourceKey())) {
                    MinorUpdater.update(dataSource);
                }
                List<Segment> segments = MinorSegmentManager.getInstance().getSegment(dataSource.getSourceKey());
                max = Double.NEGATIVE_INFINITY;
                min = Double.POSITIVE_INFINITY;
                for (Segment sg : segments) {
                    Column c = sg.getColumn(new ColumnKey(fieldName));
                    DictionaryEncodedColumn dic = c.getDictionaryEncodedColumn();
                    try {
                        double tempValue = Double.parseDouble(dic.getValue(dic.size() -1).toString());
                        max = Math.max(tempValue, max);
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                    for(int i = 1; i < dic.size(); i++) {
                        Object tempValue = dic.getValue(i);
                        if(tempValue != null) {
                            min = Math.min(Double.parseDouble(tempValue.toString()), min);
                            break;
                        }
                    }
                }
                NumberMaxAndMinValue maxAndMin = new NumberMaxAndMinValue();
                maxAndMin.setMax(max);
                maxAndMin.setMin(min);
                return maxAndMin;
            }
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
        }
        return new NumberMaxAndMinValue();
    }

    public List<Object> getGroupPreviewByFields(DataSource dataSource, String fieldName) {

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

    public List<SortBeanItem> getSortItems(FineBusinessTable table) {
        if (table == null){
            return new ArrayList<SortBeanItem>();
        }
        if (table.getType() == BICommonConstants.TABLE.ANALYSIS) {
            FineOperator op = ((FineAnalysisTable) table).getOperator();
            if (op != null && op.getType() == ConfConstant.AnalysisType.SORT) {
                return op.<SortBean>getValue().getValue();
            } else {
                return getSortItems(((FineAnalysisTable) table).getBaseTable());
            }
        }
        return new ArrayList<SortBeanItem>();
    }
}

