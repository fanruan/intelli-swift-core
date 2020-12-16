package com.fr.swift.cluster.base.initiator;

import com.fr.swift.cluster.base.exception.NotMasterNodeException;
import com.fr.swift.cluster.base.selector.ClusterNodeSelector;
import com.fr.swift.trigger.BaseServiceInitiator;
import com.fr.swift.trigger.TriggerEvent;

import java.util.ArrayList;

/**
 * This class created on 2020/5/7
 *
 * @author Kuifang.Liu
 */
public class MasterServiceInitiator extends BaseServiceInitiator {

    private static MasterServiceInitiator INSTANCE = new MasterServiceInitiator();

    private MasterServiceInitiator() {
        super(new ArrayList<>());
    }

    public static MasterServiceInitiator getInstance() {
        return INSTANCE;
    }

    @Override
    public void triggerByPriority(TriggerEvent event) {
        if (event == TriggerEvent.INIT) {
            if (!ClusterNodeSelector.getInstance().getContainer().getCurrentNode().isMaster()) {
                throw new NotMasterNodeException();
            }
        }
        super.triggerByPriority(event);
    }
}
