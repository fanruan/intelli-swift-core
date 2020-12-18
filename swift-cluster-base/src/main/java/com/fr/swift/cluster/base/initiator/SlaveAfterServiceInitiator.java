package com.fr.swift.cluster.base.initiator;

import com.fr.swift.cluster.base.exception.NotSlaveNodeException;
import com.fr.swift.cluster.base.selector.ClusterNodeSelector;
import com.fr.swift.trigger.BaseServiceInitiator;
import com.fr.swift.trigger.TriggerEvent;

import java.util.ArrayList;

/**
 * @author Heng.J
 * @date 2020/12/18
 * @description 应用online节点信息服务的启停
 * @since swift-1.2.0
 */
public class SlaveAfterServiceInitiator extends BaseServiceInitiator {

    private static SlaveAfterServiceInitiator INSTANCE = new SlaveAfterServiceInitiator();

    private SlaveAfterServiceInitiator() {
        super(new ArrayList<>());
    }

    public static SlaveAfterServiceInitiator getInstance() {
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