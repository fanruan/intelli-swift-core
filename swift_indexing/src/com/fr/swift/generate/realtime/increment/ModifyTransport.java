package com.fr.swift.generate.realtime.increment;

import com.fr.swift.flow.FlowRuleController;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.SwiftMetaData;

/**
 * This class created on 2018-1-5 14:43:29
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public class ModifyTransport implements IncrementTransport {

    private DataSource dataSource;
    private DataSource modifyDataSource;
    private SwiftMetaData swiftMetaData;
    private FlowRuleController flowRuleController;

    public ModifyTransport(DataSource dataSource, DataSource modifyDataSource, SwiftMetaData swiftMetaData, FlowRuleController flowRuleController) {
        this.dataSource = dataSource;
        this.modifyDataSource = modifyDataSource;
        this.swiftMetaData = swiftMetaData;
        this.flowRuleController = flowRuleController;

//        increaseProcessor = new IncreaseTransport(this.dataSource, this.modifyDataSource, this.swiftMetaData, this.flowRuleController);
//        desreaseProcessor = new DecreaseTransport(this.dataSource, this.modifyDataSource, this.swiftMetaData);
    }

    @Override
    public void doIncrementTransport() {
    }
}
