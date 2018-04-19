package com.fr.swift.generate.realtime;

import com.fr.swift.flow.FlowRuleController;
import com.fr.swift.generate.BaseTableBuilder;
import com.fr.swift.increment.Increment;
import com.fr.swift.source.DataSource;

/**
 * This class created on 2018-1-19 15:33:35
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 * fixme copy过来的，实现改成realtime的
 */
public class RealtimeTableBuilder extends BaseTableBuilder {

    public RealtimeTableBuilder(DataSource dataSource, Increment increment, FlowRuleController flowRuleController) {
        super(dataSource, true);
        this.transporter = new RealtimeDataTransporter(dataSource, increment, flowRuleController);
    }
}
