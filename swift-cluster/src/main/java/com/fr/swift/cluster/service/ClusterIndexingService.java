package com.fr.swift.cluster.service;

import com.fineio.FineIO;
import com.fr.event.Event;
import com.fr.event.EventDispatcher;
import com.fr.event.Listener;
import com.fr.invoke.Reflect;
import com.fr.swift.annotation.RpcMethod;
import com.fr.swift.annotation.RpcService;
import com.fr.swift.annotation.RpcServiceType;
import com.fr.swift.basics.Invoker;
import com.fr.swift.basics.ProxyFactory;
import com.fr.swift.basics.Result;
import com.fr.swift.basics.URL;
import com.fr.swift.basics.base.SwiftInvocation;
import com.fr.swift.basics.base.selector.ProxySelector;
import com.fr.swift.basics.base.selector.UrlSelector;
import com.fr.swift.config.bean.SwiftServiceInfoBean;
import com.fr.swift.config.service.SwiftServiceInfoService;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.core.cluster.SwiftClusterService;
import com.fr.swift.event.base.SwiftRpcEvent;
import com.fr.swift.event.global.TaskDoneRpcEvent;
import com.fr.swift.exception.SwiftServiceException;
import com.fr.swift.info.ServerCurrentStatus;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.netty.rpc.client.AsyncRpcCallback;
import com.fr.swift.netty.rpc.client.async.RpcFuture;
import com.fr.swift.netty.rpc.server.RpcServer;
import com.fr.swift.repository.SwiftRepositoryManager;
import com.fr.swift.service.AbstractSwiftService;
import com.fr.swift.service.IndexingService;
import com.fr.swift.service.ServiceType;
import com.fr.swift.service.listener.SwiftServiceListenerHandler;
import com.fr.swift.structure.Pair;
import com.fr.swift.stuff.IndexingStuff;
import com.fr.swift.task.TaskKey;
import com.fr.swift.task.TaskResult;
import com.fr.swift.task.impl.TaskEvent;
import com.fr.swift.upload.AbstractUploadRunnable;
import com.fr.third.springframework.beans.factory.annotation.Autowired;
import com.fr.third.springframework.beans.factory.annotation.Qualifier;
import com.fr.third.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.util.List;

/**
 * @author yee
 * @date 2018/8/6
 */
@Service
@RpcService(type = RpcServiceType.CLIENT_SERVICE, value = IndexingService.class)
public class ClusterIndexingService extends AbstractSwiftService implements IndexingService {

    @Autowired
    private transient RpcServer server;

    @Autowired(required = false)
    @Qualifier("indexingService")
    private IndexingService indexingService;
    @Autowired
    private transient SwiftRepositoryManager repositoryManager;

    @Override
    @RpcMethod(methodName = "index")
    public <Stuff extends IndexingStuff> void index(Stuff stuff) {
        indexingService.index(stuff);
    }

    @Override
    @RpcMethod(methodName = "currentStatus")
    public ServerCurrentStatus currentStatus() {
        return indexingService.currentStatus();
    }

    @Override
    public boolean start() throws SwiftServiceException {
        super.start();
        initListener();
        Reflect.on(indexingService).call("initTaskGenerator");
        return true;
    }

    public void initListener() {
        EventDispatcher.listen(TaskEvent.LOCAL_DONE, new Listener<Pair<TaskKey, TaskResult>>() {
            @Override
            public void on(Event event, final Pair<TaskKey, TaskResult> result) {
                SwiftLoggers.getLogger().info("rpc通知server任务完成");
                try {
                    runRpc(new TaskDoneRpcEvent(result));
                    FineIO.doWhenFinished(new ClusterUploadRunnable(result, indexingService.getID()));
                } catch (Exception e) {
                    SwiftLoggers.getLogger().error(e);
                }
            }
        });
    }

//    @Override
//    public boolean shutdown() throws SwiftServiceException {
//        EventDispatcher.stopListen(listener);
//        return super.shutdown();
//    }

    private URL getMasterURL() {
        List<SwiftServiceInfoBean> swiftServiceInfoBeans = SwiftContext.get().getBean(SwiftServiceInfoService.class).getServiceInfoByService(SwiftClusterService.SERVICE);
        SwiftServiceInfoBean swiftServiceInfoBean = swiftServiceInfoBeans.get(0);
        return UrlSelector.getInstance().getFactory().getURL(swiftServiceInfoBean.getServiceInfo());
    }

    private RpcFuture runRpc(Object... args) throws Exception {
        URL masterURL = getMasterURL();
        ProxyFactory factory = ProxySelector.getInstance().getFactory();
        Invoker invoker = factory.getInvoker(null, SwiftServiceListenerHandler.class, masterURL, false);
        Result invokeResult = invoker.invoke(new SwiftInvocation(server.getMethodByName("rpcTrigger"), args));
        RpcFuture future = (RpcFuture) invokeResult.getValue();
        if (null != future) {
            return future;
        }
        throw new Exception(invokeResult.getException());
    }

    private class ClusterUploadRunnable extends AbstractUploadRunnable {

        public ClusterUploadRunnable(Pair<TaskKey, TaskResult> result, String id) {
            super(result, id);
        }

        @Override
        protected void upload(URI src, URI dest) throws IOException {
            repositoryManager.currentRepo().copyToRemote(src, dest);
        }

        @Override
        public void doAfterUpload(SwiftRpcEvent event) throws Exception {
            runRpc(event).addCallback(new AsyncRpcCallback() {
                @Override
                public void success(Object result) {

                }

                @Override
                public void fail(Exception e) {

                }
            });
        }
    }

    @Override
    public ServiceType getServiceType() {
        return ServiceType.INDEXING;
    }
}
