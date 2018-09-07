package com.fr.swift.jdbc.rpc.holder;

import com.fr.swift.api.rpc.DetectService;
import com.fr.swift.api.rpc.holder.AbstractServiceAddressHolder;
import com.fr.swift.jdbc.rpc.invoke.ClientProxy;
import com.fr.swift.jdbc.rpc.invoke.ClientProxyPool;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.service.ServiceType;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yee
 * @date 2018/8/23
 */
public class JdbcAddressHolder extends AbstractServiceAddressHolder {

    private static ConcurrentHashMap<String, JdbcAddressHolder> instances = new ConcurrentHashMap<String, JdbcAddressHolder>();

    private JdbcAddressHolder(String address) {
        super(address);
    }

    private JdbcAddressHolder(String host, int port) {
        super(host + ":" + port);
    }

    public static JdbcAddressHolder getHolder(String address) {
        if (null == instances.get(address)) {
            instances.put(address, new JdbcAddressHolder(address));
        }
        return instances.get(address);
    }

    public static JdbcAddressHolder getHolder(String host, int port) {
        port = port == -1 ? 7000 : port;
        String address = host + ":" + port;
        if (null == instances.get(address)) {
            instances.put(address, new JdbcAddressHolder(host, port));
        }
        return instances.get(address);
    }

    @Override
    protected Map<ServiceType, List<String>> detectiveAddress(String address) throws Exception {
        ClientProxy proxy = null;
        try {
            proxy = ClientProxyPool.getInstance().borrowObject(address);
            return proxy.getProxy(DetectService.class).detectiveAnalyseAndRealTime(address);
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
            ClientProxyPool.getInstance().invalidateObject(address, proxy);
            throw e;
        } finally {
            ClientProxyPool.getInstance().returnObject(address, proxy);
        }
    }

}
