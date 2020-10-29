package com.fr.swift.cluster.base.initiator;

import com.fr.swift.cluster.base.exception.NotSlaveNodeException;
import com.fr.swift.cluster.base.selector.ClusterNodeSelector;
import com.fr.swift.trigger.BaseServiceInitiator;
import com.fr.swift.trigger.TriggerEvent;

import java.util.ArrayList;

/**
 * @Author: lucifer
 * @Description:
 * @Date: Created in 2020/10/28
 */
public class SlaveServiceInitiator extends BaseServiceInitiator {

    private static SlaveServiceInitiator INSTANCE = new SlaveServiceInitiator();

    private SlaveServiceInitiator() {
        super(new ArrayList<>());
    }

    public static SlaveServiceInitiator getInstance() {
        return INSTANCE;
    }

    @Override
    public void triggerByPriority(TriggerEvent event) {
        if (event == TriggerEvent.INIT) {
            if (ClusterNodeSelector.getInstance().getContainer().getCurrentNode().isMaster()) {
                throw new NotSlaveNodeException();
            }
        }
         super.triggerByPriority(event);
    }
}