package com.fr.swift.rpc.invoke;

import com.fr.swift.Invocation;
import com.fr.swift.Invoker;
import com.fr.swift.Result;
import com.fr.swift.URL;
import com.fr.swift.result.SwiftResult;
import com.fr.swift.rpc.bean.RpcRequest;
import com.fr.swift.rpc.bean.RpcResponse;
import com.fr.swift.rpc.client.RpcClient;
import com.fr.third.jodd.util.StringUtil;

import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

/**
 * This class created on 2018/6/7
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class RPCInvoker<T> implements Invoker<T> {

    private final T proxy;

    private final Class<T> type;

    private final URL url;

    public RPCInvoker(T proxy, Class<T> type, URL url) {
        if (type == null) {
            throw new IllegalArgumentException("interface == null");
        }
        this.proxy = proxy;
        this.type = type;
        this.url = url;
    }

    @Override
    public Class<T> getInterface() {
        return type;
    }

    @Override
    public Result invoke(Invocation invocation) {
        try {
            return new SwiftResult(doInvoke(proxy, invocation.getMethodName(), invocation.getParameterTypes(), invocation.getArguments()));
        } catch (InvocationTargetException e) {
            return new SwiftResult(e);
        } catch (Throwable e) {
            return new SwiftResult(e);
        }
    }

    @Override
    public URL getUrl() {
        return url;
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public void destroy() {

    }

    protected Object doInvoke(T proxy, String methodName, Class<?>[] parameterTypes, Object[] arguments) throws Throwable {
        String serviceAddress = url.getDestination().getId();
        RpcRequest request = new RpcRequest();
        request.setRequestId(UUID.randomUUID().toString());
        request.setInterfaceName(type.getName());
        request.setMethodName(methodName);
        request.setParameterTypes(parameterTypes);
        request.setParameters(arguments);

        String[] array = StringUtil.split(serviceAddress, ":");
        String host = array[0];
        int port = Integer.parseInt(array[1]);
        RpcClient client = new RpcClient(host, port);
        RpcResponse response = client.send(request);
        if (response == null) {
            throw new RuntimeException("response is null");
        }
        if (response.hasException()) {
            throw response.getException();
        } else {
            return response.getResult();
        }
    }
}
