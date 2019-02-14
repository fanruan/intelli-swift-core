package com.fr.swift.adaptor.transformer.etl;

import com.finebi.conf.internalimp.analysis.bean.operator.filter.FilterOperatorBean;
import com.finebi.conf.internalimp.analysis.table.FineAnalysisTableImpl;
import com.finebi.conf.structure.bean.table.FineBusinessTable;
import com.fr.swift.adaptor.transformer.EtlAdaptor;
import com.fr.swift.adaptor.transformer.FilterInfoFactory;
import com.fr.swift.generate.preview.MinorSegmentManager;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.query.filter.info.FilterInfo;
import com.fr.swift.segment.Segment;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.etl.columnfilter.ColumnFilterOperator;

import java.util.ArrayList;
import java.util.List;

/**
 * @author anchore
 * @date 2018/4/11
 */
public class ColumnFilterAdaptor {
    public static ColumnFilterOperator fromColumnFilterBean(FilterOperatorBean bean, FineBusinessTable table) {
        List<Segment> segments = new ArrayList<Segment>();
        try {
            DataSource source = EtlAdaptor.adaptEtlDataSource(((FineAnalysisTableImpl) table).getBaseTable());
            // TODO: 2018/3/16 这边直接通过minor来拿有问题。不能区分当前是部分数据还是全部数据的预览。需要anchore在上层处理:)
            segments = MinorSegmentManager.getInstance().getSegment(source.getSourceKey());
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
        }
        FilterInfo filterInfo = FilterInfoFactory.transformFilterBean(table.getName(), bean.getValue(), segments);
        return new ColumnFilterOperator(filterInfo);
    }
}