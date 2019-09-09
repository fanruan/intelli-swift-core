package com.fr.swift.exception.inspect.service;

import com.fr.swift.basics.annotation.ProxyService;
import com.fr.swift.basics.base.selector.ProxySelector;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.converter.ServiceTypeConverter;
import com.fr.swift.exception.event.ServiceHealthInspectionRpcEvent;
import com.fr.swift.exception.inspect.bean.RpcHealthInfoBean;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.service.AbstractSwiftService;
import com.fr.swift.service.ServiceType;
import com.fr.swift.service.SwiftService;
import com.fr.swift.service.bean.RpcHealthResultBean;
import com.fr.swift.service.listener.RemoteSender;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Marvin
 * @version 1.1
 * Created by Marvin on 9/5/2019
 */

@SwiftBean
@ProxyService(ComponentHealthInspectService.class)

/**
 * 所有的健康探测方法均返回一个健康的服务构成的集合，如果集合为空则认为探测的服务是不健康的
 */
public class SwiftComponentHealthInspectService extends AbstractSwiftService implements ComponentHealthInspectService {
    @Override
    public Set<RpcHealthResultBean> inspectMasterRpcHealth(ServiceType target) {
        SwiftLoggers.getLogger().debug("Inspect RpcHealth to Master");
        try {
            //通过RemoteSender发送健康检查rpc事件
            return (Set<RpcHealthResultBean>) ProxySelector.getProxy(RemoteSender.class).trigger(new ServiceHealthInspectionRpcEvent().inspectOnlyMaster());
        } catch (Exception e) {
            SwiftLoggers.getLogger().error("Master Communication inspect failed:", e);
        }
        return Collections.emptySet();
    }

    @Override
    public Set<RpcHealthResultBean> inspectOtherSlaveRpcHealthOverMaser(ServiceType target) {
        SwiftLoggers.getLogger().debug("Inspect Other Slave RpcHealth over Master");
        try {
            //通过RemoteSender发送健康检查rpc事件，并且infoBean.inspectOtherSlave为true，通过master检查其它节点
            RpcHealthInfoBean infoBean = new RpcHealthInfoBean(target);
            return (Set<RpcHealthResultBean>) ProxySelector.getProxy(RemoteSender.class).trigger(new ServiceHealthInspectionRpcEvent().inspectOtherSlavesOverMaster(infoBean));
        } catch (Exception e) {
            SwiftLoggers.getLogger().error("Master Communication inspect failed:", e);
        }
        return Collections.emptySet();
    }

    @Override
    public Set<RpcHealthResultBean> inspectSlaveRpcHealth(ServiceType target) {
        //slave检查自己本地的service是否可用，master通过SwiftEchoProcessHandler调用各个节点上的该方法
        //返回集合是因为某些服务要依赖其它的服务，比如realtime和history需要依赖SwiftUploadService和SwiftDeleteService，indexing需要依赖collate
        //检查结果不止一个服务，详见ServiceBeanFactory.getSwiftServiceByNames方法
        Set<RpcHealthResultBean> infoBeans = new HashSet<>();
        //TODO 如何直接根据类型找到本地的服务

        List<SwiftService> swiftServices = ServiceTypeConverter.toSwiftService(target);
        for (SwiftService service : swiftServices) {

            RpcHealthResultBean bean = new RpcHealthResultBean(target);
            //如果能获取到id和name就认为该任务是正常的
            try {
                bean.setServiceName(service.getClass().getName()).setServiceId(service.getId());
            } catch (Exception e) {
                SwiftLoggers.getLogger().error("Slave Local Service inspect failed:", e);
                continue;
            }
            infoBeans.add(bean);
        }
        return infoBeans;

    }

    @Override
    public ServiceType getServiceType() {
        return ServiceType.HEALTH_INSPECT;
    }
}
