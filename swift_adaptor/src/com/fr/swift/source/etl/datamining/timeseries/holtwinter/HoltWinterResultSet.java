package com.fr.swift.source.etl.datamining.timeseries.holtwinter;

import com.finebi.conf.internalimp.analysis.bean.operator.datamining.AlgorithmBean;
import com.finebi.conf.rlang.algorithm.timeseries.RHoltWinters;
import com.fr.swift.segment.Segment;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.etl.datamining.timeseries.AbstractTimeSeriesResultSet;

import java.util.List;

/**
 * Created by Jonas on 2018/3/12 9:00
 */
public class HoltWinterResultSet extends AbstractTimeSeriesResultSet {

    public HoltWinterResultSet(AlgorithmBean algorithmBean, SwiftMetaData selfMetaData, SwiftMetaData baseMetaData, List<Segment> segmentList) throws Exception {
        super(algorithmBean,selfMetaData,baseMetaData,segmentList);
        timeSeriesAlgorithm = new RHoltWinters();
    }

    protected void setExtraConfiguration(){
        RHoltWinters rHoltWinters = (RHoltWinters)timeSeriesAlgorithm;
        rHoltWinters.setIncludeAdditiveEffect(true);
        rHoltWinters.setIncludeSeasonalCorrection(true);
        rHoltWinters.setIncludeTrendCorrection(true);
    }
}
