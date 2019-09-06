package com.fr.swift.exception.inspect.bean;

import com.fr.swift.service.ServiceType;

import java.io.Serializable;

/**
 * @author Marvin
 * @version 1.1
 * Created by Marvin on 9/4/2019
 */
public class RpcHealthInfoBean implements ComponentHealthInfo<ServiceType>, Serializable {

    private static final long serialVersionUID = 5496211401916821361L;

    //被检测的目标rpc服务
    ServiceType target;

    //是否通过master检查其它节点
    boolean inspectOtherSlave;

    public RpcHealthInfoBean(ServiceType target) {
        this.target = target;
    }

    public RpcHealthInfoBean(ServiceType target, boolean inspectOtherMaster) {
        this.target = target;
        this.inspectOtherSlave = inspectOtherMaster;
    }

    public boolean isInspectOtherSlave() {
        return inspectOtherSlave;
    }

    @Override
    public ServiceType getTarget() {
        return target;
    }

}
