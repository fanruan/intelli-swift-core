package com.fr.swift.service.handler.health;

import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.event.base.AbstractHealthInspectionRpcEvent;
import com.fr.swift.exception.inspect.ComponentHealthCheck;
import com.fr.swift.exception.inspect.RpcServiceHealthInspector;
import com.fr.swift.exception.inspect.bean.ComponentHealthInfo;
import com.fr.swift.selector.ClusterSelector;
import com.fr.swift.service.ServiceType;
import com.fr.swift.service.bean.RpcHealthResultBean;
import com.fr.swift.service.handler.base.AbstractHandler;

import java.io.Serializable;
import java.util.Collections;
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
                return (S) getMaserRpcHealthInfoBean();
            case INSPECT_SLAVE:
                return (S) inspectOtherSlaveRpcHealthOverMaster((ComponentHealthInfo) event.getContent());
            default:
                throw new IllegalStateException("Unexpected value: " + event.subEvent());
        }
    }

    /**
     * @param info 探测服务信息，info.getTarget可以得到探测目标
     * @return RpcHealthResultBean集合，该RpcHealthResultBean包含所有健康服务的id和name(发起请求的节点本身的服务健康情况也包含在该集合内，使用时注意去除；加入自动去重会使得逻辑变得较复杂)
     */
    private Set<RpcHealthResultBean> inspectOtherSlaveRpcHealthOverMaster(ComponentHealthInfo info) {
        ComponentHealthCheck rpcHealthChecker = new ComponentHealthCheck(RpcServiceHealthInspector.getInstance(), 30000);
        return rpcHealthChecker.check(info);
    }

    /**
     * @return id为master的id，name为特定的字符串"TO_MASTER_RPC_SERVICE"
     */
    private Set<RpcHealthResultBean> getMaserRpcHealthInfoBean() {
        RpcHealthResultBean bean = new RpcHealthResultBean(ServiceType.HEALTH_INSPECT);
        bean.setServiceName("TO_MASTER_RPC_SERVICE").setServiceId(ClusterSelector.getInstance().getFactory().getMasterId());
        return Collections.singleton(bean);
    }

}
