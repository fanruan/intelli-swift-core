package com.fr.swift.exception.inspect;

import com.fr.swift.SwiftContext;
import com.fr.swift.basics.base.selector.ProxySelector;
import com.fr.swift.exception.inspect.bean.RpcHealthInfoBean;
import com.fr.swift.selector.ClusterSelector;
import com.fr.swift.service.ServiceContext;

import java.util.Set;

/**
 * @author Marvin
 * @version 1.1
 * Created by Marvin on 8/30/2019
 */
public class RpcServiceHealthInspector implements ComponentHealthInspector<Set<String>, RpcHealthInfoBean> {

    public static final RpcServiceHealthInspector INSTANCE = new RpcServiceHealthInspector();

    public static RpcServiceHealthInspector getInstance() {
        return INSTANCE;
    }

    @Override
    public Set<String> inspect(RpcHealthInfoBean info) {
        if (ClusterSelector.getInstance().getFactory().isMaster()) {
            return ProxySelector.getProxy(ServiceContext.class).inspectSlaveRpcHealth(info.getTarget());
        } else {
            if (info.isInspectOtherSlave()) {
                return SwiftContext.get().getBean(ServiceContext.class).inspectMasterRpcHealth(info.getTarget(), true);
            } else {
                return SwiftContext.get().getBean(ServiceContext.class).inspectMasterRpcHealth(info.getTarget(), false);
            }
        }
    }

    @Override
    public Set<String> inspect() {
        return null;
    }

}
