package com.fr.swift.source.etl.datamining;

import com.finebi.conf.internalimp.analysis.bean.operator.datamining.AlgorithmBean;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.Segment;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.source.etl.ETLTransferOperator;
import com.fr.swift.source.etl.datamining.timeseries.holtwinter.HoltWinterResultSet;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Jonas on 2018/3/12 9:00
 */
public class DataMiningTransferOperator implements ETLTransferOperator {
    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(DataMiningTransferOperator.class);

    private AlgorithmBean algorithmBean;

    public DataMiningTransferOperator(AlgorithmBean algorithmBean) {
        this.algorithmBean = algorithmBean;
    }

    /**
     * @param metaData      当前表的元数据
     * @param basedMetas    上一步表的元数据
     * @param basedSegments 上一步表的segment
     * @return 当前ETL操作处理后的数据
     */
    @Override
    public SwiftResultSet createResultSet(SwiftMetaData metaData, List<SwiftMetaData> basedMetas, List<Segment[]> basedSegments) {
        List<List<Segment>> tis = new ArrayList<List<Segment>>();
        for (Segment[] basedSegment : basedSegments) {
            tis.add(Arrays.asList(basedSegment));
        }
        try {
            switch (algorithmBean.getAlgorithmName()) {
                case HOLT_WINTERS:
                    return new HoltWinterResultSet(algorithmBean, metaData, basedMetas.get(0), tis.get(0));
                default:
                    return null;
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return new SwiftResultSet() {
                @Override
                public void close() throws SQLException {

                }

                @Override
                public boolean next() throws SQLException {
                    return false;
                }

                @Override
                public SwiftMetaData getMetaData() throws SQLException {
                    return null;
                }

                @Override
                public Row getRowData() throws SQLException {
                    return null;
                }
            };
        }
    }
}
