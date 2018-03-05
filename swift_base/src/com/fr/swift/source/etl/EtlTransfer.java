package com.fr.swift.source.etl;

import com.fr.swift.segment.Segment;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.source.SwiftSourceTransfer;
import com.fr.swift.util.Util;

import java.util.List;

/**
 * @author pony
 * @date 2017/12/5
 */
public class EtlTransfer implements SwiftSourceTransfer {

    private ETLTransferOperator operator;
    private SwiftMetaData metaData;
    private List<SwiftMetaData> basedMetas;
    private List<Segment[]> basedSegments;

    public EtlTransfer(ETLTransferOperator operator, SwiftMetaData metaData, List<SwiftMetaData> basedMetas, List<Segment[]> basedSegments) {
        Util.requireNonNull(operator, metaData, basedMetas, basedMetas);
        this.operator = operator;
        this.metaData = metaData;
        this.basedMetas = basedMetas;
        this.basedSegments = basedSegments;
    }

    @Override
    public SwiftResultSet createResultSet() {
        return operator.createResultSet(metaData, basedMetas, basedSegments);
    }
}
