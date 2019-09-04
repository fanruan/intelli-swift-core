package com.fr.swift.exception.inspect.bean;

import com.fr.swift.service.SwiftService;

/**
 * @author Marvin
 * @version 1.1
 * Created by Marvin on 9/4/2019
 */
public class RpcHealthInfoBean implements ComponentHealthInfo<SwiftService> {

    SwiftService target;

    /**
     * master节点检查其它节点的rpc健康状况后将结果返回给本节点
     */
    private boolean inspectOtherSlave;

    public RpcHealthInfoBean(SwiftService target, boolean inspectOtherMaster) {
        this.target = target;
        this.inspectOtherSlave = inspectOtherMaster;
    }

    public boolean isInspectOtherSlave() {
        return inspectOtherSlave;
    }

    @Override
    public SwiftService getTarget() {
        return target;
    }

}
