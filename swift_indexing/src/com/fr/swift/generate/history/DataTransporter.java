package com.fr.swift.generate.history;

import com.fr.swift.cube.task.Task.Result;
import com.fr.swift.cube.task.impl.BaseWorker;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.manager.LocalSegmentOperatorProvider;
import com.fr.swift.segment.SegmentOperator;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.source.SwiftSourceTransfer;
import com.fr.swift.source.SwiftSourceTransferFactory;

/**
 * This class created on 2017-12-25 13:55:12
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public class DataTransporter extends BaseWorker {
    private DataSource dataSource;

    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(DataTransporter.class);

    public DataTransporter(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void work() {
        try {
            transport();
            workOver(Result.SUCCEEDED);
        } catch (Exception e) {
            LOGGER.error(e);
            workOver(Result.FAILED);
        }
    }

    public void transport() throws Exception {
        SwiftSourceTransfer transfer = SwiftSourceTransferFactory.createSourceTransfer(dataSource);
        SwiftResultSet resultSet = transfer.createResultSet();
        SegmentOperator operator = LocalSegmentOperatorProvider.getInstance().getIndexSegmentOperator(dataSource);
        operator.transport(resultSet);
        operator.finishTransport();
    }
}
