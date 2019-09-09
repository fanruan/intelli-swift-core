package com.fr.swift.exception.inspect.service;

import com.fr.swift.basics.annotation.InvokeMethod;
import com.fr.swift.basics.annotation.Target;
import com.fr.swift.basics.handler.EchoProcessHandler;
import com.fr.swift.service.ServiceType;
import com.fr.swift.service.SwiftService;
import com.fr.swift.service.bean.RpcHealthResultBean;

import java.util.Set;

/**
 * @author Marvin
 * @version 1.1
 * Created by Marvin on 9/5/2019
 */
public interface ComponentHealthInspectService extends SwiftService {

    Set<RpcHealthResultBean> inspectMasterRpcHealth(ServiceType target);

    Set<RpcHealthResultBean> inspectOtherSlaveRpcHealthOverMaser(ServiceType target);

    @InvokeMethod(value = EchoProcessHandler.class, target = Target.ALL)
    Set<RpcHealthResultBean> inspectSlaveRpcHealth(ServiceType target);
}
