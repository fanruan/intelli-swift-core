package com.fr.swift.provider.impl;

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
import com.fr.swift.adaptor.transformer.DataSourceFactory;
import com.fr.swift.adaptor.transformer.SortAdaptor;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.generate.preview.MinorSegmentManager;
import com.fr.swift.generate.preview.MinorUpdater;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.manager.LocalSegmentProvider;
import com.fr.swift.provider.DataProvider;
import com.fr.swift.query.sort.SortType;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.empty.EmptyDataSource;
import com.fr.swift.structure.array.IntList;
import com.fr.swift.structure.array.IntListFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class created on 2018/4/2
 *
 * @author Lucifer
 * @description 真实数据和预览数据provider
 * @since Advanced FineBI Analysis 1.0
 */
public class SwiftDataProvider implements DataProvider {

    @Override
    public List<Segment> getPreviewData(DataSource dataSource) throws Exception {
        if (isSwiftAvailable(dataSource)) {
            return getRealData(dataSource);
        }
        minorUpdate(dataSource);
        return MinorSegmentManager.getInstance().getSegment(dataSource.getSourceKey());
    }

    @Override
    public List<Segment> getRealData(DataSource dataSource) {
        return SwiftContext.get().getBean(LocalSegmentProvider.class).getSegment(dataSource.getSourceKey());
    }

    @Override
    public boolean isSwiftAvailable(DataSource dataSource) {
        return SwiftContext.get().getBean(LocalSegmentProvider.class).isSegmentsExist(dataSource.getSourceKey());
    }

    @Override
    public BIDetailTableResult getDetailPreviewByFields(FineBusinessTable table, int rowCount) throws SQLException {
        try {
            DataSource dataSource = DataSourceFactory.getDataSourceInCache(table);
            if (dataSource instanceof EmptyDataSource) {
                return new SwiftDetailTableResult(new SwiftEmptyResult(), 0, -1);
            }
            IntList sortIndex = IntListFactory.createHeapIntList();
            List<SortType> sorts = new ArrayList<SortType>();
            //分析表排序加上属性
            List<SortBeanItem> sortBeanItemList = getSortItems(table);
            for (SortBeanItem sortBeanItem : sortBeanItemList) {
                //可能有些字段排序的后来被删了
                try {
                    sortIndex.add(dataSource.getMetadata().getColumnIndex(sortBeanItem.getName()));
                    sorts.add(SortAdaptor.transformSort(sortBeanItem.getSortType()).getSortType());
                } catch (Exception ignore) {
                }
            }
            if (dataSource != null) {
                List<Segment> segments = this.getPreviewData(dataSource);

                SwiftMetaData swiftMetaData = dataSource.getMetadata();
                BIDetailTableResult realDetailResult = new SwiftSegmentDetailResult(segments, swiftMetaData, sortIndex, sorts);
                return realDetailResult;
            }
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
        }
        return new SwiftDetailTableResult(new SwiftEmptyResult(), 0, -1);
    }

    @Override
    public NumberMaxAndMinValue getNumberMaxAndMinValue(DataSource dataSource, String fieldName) {
        try {
            double max, min;
            if (dataSource != null) {
                List<Segment> segments = this.getPreviewData(dataSource);
                max = Double.NEGATIVE_INFINITY;
                min = Double.POSITIVE_INFINITY;
                for (Segment sg : segments) {
                    Column c = sg.getColumn(new ColumnKey(fieldName));
                    DictionaryEncodedColumn dic = c.getDictionaryEncodedColumn();
                    try {
                        double tempValue = Double.parseDouble(dic.getValue(dic.size() - 1).toString());
                        max = Math.max(tempValue, max);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    for (int i = 1; i < dic.size(); i++) {
                        Object tempValue = dic.getValue(i);
                        if (tempValue != null) {
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

    @Override
    public List<Object> getGroupPreviewByFields(DataSource dataSource, String fieldName) {
        try {
            if (dataSource != null) {
                //todo 这个接口还是List啊。还需要返回所有值。要有几百几千万的话。。
                List<Segment> segmentList = null;
                if (isSwiftAvailable(dataSource)) {
                    segmentList = getRealData(dataSource);
                }
                if (segmentList == null || segmentList.isEmpty()) {
                    segmentList = this.getPreviewData(dataSource);
                }
                List<Object> list = new ArrayList<Object>();
                for (Segment sg : segmentList) {
                    Column c = sg.getColumn(new ColumnKey(fieldName));
                    DictionaryEncodedColumn dic = c.getDictionaryEncodedColumn();
                    //字典编码的0号恒为0(无论空值存不存在),需要判断一下
                    for (int i = (c.getBitmapIndex().getNullIndex().isEmpty() ? 1 : 0); i < dic.size(); i++) {
                        list.add(dic.getValue(i));
                    }
                }
                return list;
            }
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
        }
        return new ArrayList<Object>();
    }

    private List<SortBeanItem> getSortItems(FineBusinessTable table) {
        if (table == null) {
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

    private void minorUpdate(DataSource dataSource) throws Exception {
        new MinorUpdater(dataSource).update();
    }
}
