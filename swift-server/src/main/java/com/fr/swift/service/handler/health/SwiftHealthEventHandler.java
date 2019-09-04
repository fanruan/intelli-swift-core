package com.fr.swift.service.handler.health;

import com.fr.swift.basic.URL;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.event.base.AbstractHealthInspectionRpcEvent;
import com.fr.swift.exception.inspect.ComponentHealthCheck;
import com.fr.swift.exception.inspect.RpcServiceHealthInspector;
import com.fr.swift.selector.ClusterSelector;
import com.fr.swift.service.SwiftService;
import com.fr.swift.service.handler.base.AbstractHandler;

import java.io.Serializable;
import java.util.Set;

/**
 * @author Marvin
 * @version 1.1
 * Created by Marvin on 8/30/2019
 */
@SwiftBean
public class SwiftHealthEventHandler extends AbstractHandler<AbstractHealthInspectionRpcEvent> {
    @Override
    public <S extends Serializable> S handle(AbstractHealthInspectionRpcEvent event) throws Exception {
        switch (event.subEvent()) {
            case INSPECT_MASTER:
                return (S) ClusterSelector.getInstance().getFactory().getMasterId();
            case INSPECT_SLAVE:
                return (S) inspectSlaveRpcHealth((SwiftService) event.getContent());
            default:
                throw new IllegalStateException("Unexpected value: " + event.subEvent());
        }
    }

    private Set<URL> inspectSlaveRpcHealth(SwiftService service) {
        ComponentHealthCheck rpcHealthChecker = new ComponentHealthCheck(RpcServiceHealthInspector.getInstance(), 30000);
        return rpcHealthChecker.check(service);
    }

}
