package com.fr.swift.generate.realtime.increment;

import com.fr.swift.context.SwiftContext;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.manager.LineSegmentManager;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SwiftDataOperatorProvider;
import com.fr.swift.segment.operator.delete.WhereDeleter;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.source.SwiftSourceTransfer;
import com.fr.swift.source.SwiftSourceTransferFactory;

import java.util.List;

/**
 * This class created on 2018-1-5 14:43:18
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public class DecreaseTransport implements IncrementTransport {

    private DataSource dataSource;
    private DataSource decreaseDataSource;
    private SwiftMetaData swiftMetaData;
    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(DecreaseTransport.class);


    public DecreaseTransport(DataSource dataSource, DataSource decreaseDataSource, SwiftMetaData swiftMetaData) {
        this.dataSource = dataSource;
        this.decreaseDataSource = decreaseDataSource;
        this.swiftMetaData = swiftMetaData;
    }

    @Override
    public void doIncrementTransport() throws Exception {
        SwiftSourceTransfer decreaseTransfer = SwiftSourceTransferFactory.createSourceTransfer(decreaseDataSource);
        List<Segment> segmentList = new LineSegmentManager().getSegment(dataSource.getSourceKey());
        for (Segment segment : segmentList) {
            SwiftResultSet decreaseResult = decreaseTransfer.createResultSet();
            WhereDeleter deleter = SwiftContext.get().getBean(SwiftDataOperatorProvider.class).getRowDeleter(segment);
            deleter.deleteData(decreaseResult);
        }
    }
}
