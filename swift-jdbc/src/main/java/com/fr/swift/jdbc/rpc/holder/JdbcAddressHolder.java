package com.fr.swift.jdbc.rpc.holder;

import com.fr.swift.api.rpc.DetectService;
import com.fr.swift.api.rpc.holder.AbstractServiceAddressHolder;
import com.fr.swift.jdbc.mode.Mode;
import com.fr.swift.jdbc.proxy.invoke.ClientProxy;
import com.fr.swift.jdbc.proxy.invoke.ClientProxyPool;
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

    private Mode mode;

    private JdbcAddressHolder(String address, Mode mode) {
        super(address);
        this.mode = mode;
        detect();
    }

    private JdbcAddressHolder(String host, int port, Mode mode) {
        this(host + ":" + port, mode);
    }

    public static JdbcAddressHolder getHolder(String host, int port, Mode mode) {
        port = port == -1 ? 7000 : port;
        String address = host + ":" + port;
        if (null == instances.get(address)) {
            instances.put(address, new JdbcAddressHolder(host, port, mode));
        }
        return instances.get(address);
    }

    @Override
    protected Map<ServiceType, List<String>> detectiveAddress(String address) throws Exception {
        ClientProxy proxy = null;
        try {
            proxy = ClientProxyPool.getInstance(mode).borrowObject(address);
            return proxy.getProxy(DetectService.class).detectiveAnalyseAndRealTime(address);
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
            ClientProxyPool.getInstance(mode).invalidateObject(address, proxy);
            throw e;
        } finally {
            ClientProxyPool.getInstance(mode).returnObject(address, proxy);
        }
    }

}
