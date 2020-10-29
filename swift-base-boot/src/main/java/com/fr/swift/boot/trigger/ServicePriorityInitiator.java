package com.fr.swift.boot.trigger;

import com.fr.swift.trigger.BaseServiceInitiator;
import com.fr.swift.trigger.SwiftPriorityInitTrigger;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lucifer
 * @date 2020/4/23
 * @description
 * @since swift 1.1
 */
public class ServicePriorityInitiator extends BaseServiceInitiator {

    private static ServicePriorityInitiator INSTANCE = new ServicePriorityInitiator();

    private ServicePriorityInitiator() {
        super(new ArrayList<>());
    }

    public static ServicePriorityInitiator getInstance() {
        return INSTANCE;
    }
}
