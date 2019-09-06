package com.fr.swift.exception.inspect;

import com.fr.swift.SwiftContext;
import com.fr.swift.basics.base.selector.ProxySelector;
import com.fr.swift.exception.inspect.bean.RpcHealthInfoBean;
import com.fr.swift.exception.inspect.service.ComponentHealthInspectService;
import com.fr.swift.selector.ClusterSelector;
import com.fr.swift.service.bean.RpcHealthResultBean;

import java.util.Set;

/**
 * @author Marvin
 * @version 1.1
 * Created by Marvin on 8/30/2019
 */
public class RpcServiceHealthInspector implements ComponentHealthInspector<Set<RpcHealthResultBean>, RpcHealthInfoBean> {

    public static final RpcServiceHealthInspector INSTANCE = new RpcServiceHealthInspector();

    public static RpcServiceHealthInspector getInstance() {
        return INSTANCE;
    }

    /**
     * @param info 包含rpc检测的对象和一个用来判断是否检测其它slave的布尔变量inspectOtherSlave
     * @return 返回一个包含RpcHealthResultBean的Set，RpcRpcHealthResultBean包含探测结果为健康的service的id和name
     */
    @Override
    public Set<RpcHealthResultBean> inspect(RpcHealthInfoBean info) {
        if (ClusterSelector.getInstance().getFactory().isMaster()) {
            //master通过动态代理调用slave上的inspectSlaveRpcHealth方法
            return ProxySelector.getProxy(ComponentHealthInspectService.class).inspectSlaveRpcHealth(info.getTarget());
        } else {
            if (info.isInspectOtherSlave()) {
                //slave首先向master发送rpc事件，然后master检查其它slave的rpc服务，并将结果返回给发送rpc事件的slave
                return SwiftContext.get().getBean(ComponentHealthInspectService.class).inspectOtherSlaveRpcHealthOverMaser(info.getTarget());
            } else {
                //slave只检查与master的通信情况，master返回自己的id，serviceName为"TO_MASTER_RPC_SERVICE"
                return SwiftContext.get().getBean(ComponentHealthInspectService.class).inspectMasterRpcHealth(info.getTarget());
            }
        }
    }

}
