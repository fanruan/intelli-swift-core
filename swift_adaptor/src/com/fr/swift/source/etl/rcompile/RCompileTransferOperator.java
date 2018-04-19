package com.fr.swift.source.etl.rcompile;

import com.finebi.conf.algorithm.DMDataModel;
import com.fr.swift.segment.Segment;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.source.etl.ETLTransferOperator;

import java.util.List;

/**
 * Created by Handsome on 2018/3/30 0030 11:38
 */
public class RCompileTransferOperator implements ETLTransferOperator {

    private DMDataModel dataModel;

    public RCompileTransferOperator(DMDataModel dataModel) {
        this.dataModel = dataModel;
    }

    @Override
    public SwiftResultSet createResultSet(SwiftMetaData metaData, List<SwiftMetaData> basedMetas, List<Segment[]> basedSegments) {
        return new RCompileResultSet(dataModel, metaData);
    }
}
