package com.fr.swift.generate.realtime.increment;

import com.fr.swift.flow.FlowRuleController;
import com.fr.swift.flow.SwiftFlowResultSet;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.manager.LocalSegmentOperatorProvider;
import com.fr.swift.segment.SegmentOperator;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.source.SwiftSourceTransfer;
import com.fr.swift.source.SwiftSourceTransferFactory;

/**
 * This class created on 2018-1-5 14:43:00
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public class IncreaseTransport implements IncrementTransport {
    private DataSource dataSource;
    private DataSource increaseDataSource;
    private SwiftMetaData swiftMetaData;

    private FlowRuleController flowRuleController;
    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(IncreaseTransport.class);


    public IncreaseTransport(DataSource dataSource, DataSource increaseDataSource, SwiftMetaData swiftMetaData, FlowRuleController flowRuleController) {
        this.dataSource = dataSource;
        this.increaseDataSource = increaseDataSource;
        this.swiftMetaData = swiftMetaData;
        this.flowRuleController = flowRuleController;
    }

    @Override
    public void doIncrementTransport() {
        SwiftSourceTransfer increaseTransfer = SwiftSourceTransferFactory.createSourceTransfer(increaseDataSource);
        SwiftResultSet increaseResult = increaseTransfer.createResultSet();

        SwiftFlowResultSet swiftFlowResultSet = new SwiftFlowResultSet(increaseResult, flowRuleController);

        SegmentOperator operator = LocalSegmentOperatorProvider.getInstance().getRealtimeSegmentOperator(dataSource);
        try {
            operator.transport(swiftFlowResultSet);
        } catch (Exception e) {
            LOGGER.error(e);
        } finally {
            operator.finishTransport();
        }
    }
}
