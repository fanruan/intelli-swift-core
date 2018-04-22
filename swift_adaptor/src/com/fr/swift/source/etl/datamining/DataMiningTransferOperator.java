package com.fr.swift.source.etl.datamining;

import com.finebi.conf.internalimp.analysis.bean.operator.datamining.AlgorithmBean;
import com.fr.swift.segment.Segment;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.source.etl.ETLTransferOperator;

import java.util.List;

/**
 * Created by Jonas on 2018/3/12 9:00
 */
public class DataMiningTransferOperator implements ETLTransferOperator {

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
        switch (algorithmBean.getAlgorithmName()){
            case EMPTY:
            case HOLT_WINTERS:
                return new DataMiningResultSet(algorithmBean, metaData, basedMetas.get(0), basedSegments.get(0));
            default:
                return new DataMiningFilterResultSet(algorithmBean, metaData, basedMetas.get(0), basedSegments.get(0));
        }
    }
}
