package com.fr.swift.generate.realtime;

import com.fr.swift.cube.task.TaskResult.Type;
import com.fr.swift.cube.task.impl.BaseWorker;
import com.fr.swift.cube.task.impl.TaskResultImpl;
import com.fr.swift.flow.FlowRuleController;
import com.fr.swift.generate.Transporter;
import com.fr.swift.generate.realtime.increment.DecreaseTransport;
import com.fr.swift.generate.realtime.increment.IncreaseTransport;
import com.fr.swift.generate.realtime.increment.IncrementTransport;
import com.fr.swift.generate.realtime.increment.ModifyTransport;
import com.fr.swift.increment.Increment;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.SwiftMetaData;

import java.util.ArrayList;
import java.util.List;

/**
 * This class created on 2018-1-4 15:35:50
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public class RealtimeDataTransporter extends BaseWorker implements Transporter {
    private DataSource dataSource;
    private SwiftMetaData swiftMetaData;
    private Increment increment;
    private FlowRuleController flowRuleController;

    private List<IncrementTransport> incrementTransportList;

    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(RealtimeDataTransporter.class);

    public RealtimeDataTransporter(DataSource dataSource, Increment increment) {
        this.dataSource = dataSource;
        this.swiftMetaData = dataSource.getMetadata();
        this.increment = increment;
        this.flowRuleController = new FlowRuleController();
        initProcessors();
    }

    public RealtimeDataTransporter(DataSource dataSource, Increment increment, FlowRuleController flowRuleController) {
        this.dataSource = dataSource;
        this.swiftMetaData = dataSource.getMetadata();
        this.increment = increment;
        this.flowRuleController = flowRuleController;
        initProcessors();
    }


    private void initProcessors() {
        incrementTransportList = new ArrayList<IncrementTransport>();
        if (increment.getIncreaseSource() != null) {
            IncrementTransport increaseTransport = new IncreaseTransport(dataSource, increment.getIncreaseSource(), swiftMetaData, flowRuleController);
            incrementTransportList.add(increaseTransport);
        }
        if (increment.getDecreaseSource() != null) {
            IncrementTransport desreaseTransport = new DecreaseTransport(dataSource, increment.getDecreaseSource(), swiftMetaData);
            incrementTransportList.add(desreaseTransport);
        }
        if (increment.getModifySource() != null) {
            IncrementTransport modifyTransport = new ModifyTransport(dataSource, increment.getDecreaseSource(), swiftMetaData, flowRuleController);
            incrementTransportList.add(modifyTransport);
        }

        if (increment.getIncreaseExcelSource() != null) {
            IncreaseTransport increaseTransport = new IncreaseTransport(dataSource, increment.getIncreaseExcelSource(), swiftMetaData, flowRuleController);
            incrementTransportList.add(increaseTransport);
        }
    }

    @Override
    public void work() {
        try {
            transport();
            workOver(new TaskResultImpl(Type.SUCCEEDED));
        } catch (Exception e) {
            LOGGER.error(e);
            workOver(new TaskResultImpl(Type.FAILED, e));
        }
    }

    @Override
    public void transport() throws Exception {
        for (IncrementTransport incrementTransport : incrementTransportList) {
            incrementTransport.doIncrementTransport();
        }
    }

    @Override
    public List<String> getIndexFieldsList() {
        return dataSource.getMetadata().getFieldNames();
    }
}
