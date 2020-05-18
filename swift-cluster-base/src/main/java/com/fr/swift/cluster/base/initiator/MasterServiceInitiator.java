package com.fr.swift.cluster.base.initiator;

import com.fr.swift.trigger.BaseServiceInitiator;
import com.fr.swift.trigger.SwiftPriorityInitTrigger;

import java.util.ArrayList;
import java.util.List;

/**
 * This class created on 2020/5/7
 *
 * @author Kuifang.Liu
 */
public class MasterServiceInitiator extends BaseServiceInitiator {
    private static List<SwiftPriorityInitTrigger> triggers = new ArrayList<>();

    private static MasterServiceInitiator INSTANCE = new MasterServiceInitiator();

    private MasterServiceInitiator() {
        super(triggers);
    }

    public static MasterServiceInitiator getInstance() {
        return INSTANCE;
    }
}
