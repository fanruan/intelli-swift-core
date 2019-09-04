package com.fr.swift.exception.inspect;

import com.fr.swift.SwiftContext;
import com.fr.swift.basics.base.selector.ProxySelector;
import com.fr.swift.selector.ClusterSelector;
import com.fr.swift.service.ServiceContext;
import com.fr.swift.service.SwiftService;

import java.util.Set;

/**
 * @author Marvin
 * @version 1.1
 * Created by Marvin on 8/30/2019
 */
public class RpcServiceHealthInspector implements ComponentHealthInspector<Set<String>, SwiftService> {

    public static final RpcServiceHealthInspector INSTANCE = new RpcServiceHealthInspector();

    public static RpcServiceHealthInspector getInstance() {
        return INSTANCE;
    }

    @Override
    public Set<String> inspect(SwiftService inspectedService) {
        if (ClusterSelector.getInstance().getFactory().isMaster()) {
            return ProxySelector.getProxy(ServiceContext.class).inspectSlaveRpcHealth(inspectedService);
        } else {
            return SwiftContext.get().getBean(ServiceContext.class).inspectMasterRpcHealth(inspectedService);
        }
    }

}
