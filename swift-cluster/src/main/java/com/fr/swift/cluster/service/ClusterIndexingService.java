package com.fr.swift.cluster.service;

import com.fineio.FineIO;
import com.fr.swift.ClusterNodeService;
import com.fr.swift.annotation.RpcMethod;
import com.fr.swift.annotation.RpcService;
import com.fr.swift.annotation.RpcServiceType;
import com.fr.swift.basics.AsyncRpcCallback;
import com.fr.swift.basics.Invoker;
import com.fr.swift.basics.ProxyFactory;
import com.fr.swift.basics.Result;
import com.fr.swift.basics.RpcFuture;
import com.fr.swift.basics.URL;
import com.fr.swift.basics.base.SwiftInvocation;
import com.fr.swift.basics.base.selector.ProxySelector;
import com.fr.swift.basics.base.selector.UrlSelector;
import com.fr.swift.config.bean.SwiftServiceInfoBean;
import com.fr.swift.config.service.SwiftServiceInfoService;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.event.base.SwiftRpcEvent;
import com.fr.swift.event.global.TaskDoneRpcEvent;
import com.fr.swift.exception.SwiftServiceException;
import com.fr.swift.info.ServerCurrentStatus;
import com.fr.swift.log.SwiftLoggers;
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
import com.fr.swift.upload.AbstractUploadRunnable;
import com.fr.third.springframework.beans.factory.annotation.Autowired;
import com.fr.third.springframework.beans.factory.annotation.Qualifier;

import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.util.List;

/**
 * @author yee
 * @date 2018/8/6
 */
@RpcService(type = RpcServiceType.CLIENT_SERVICE, value = IndexingService.class)
public class ClusterIndexingService extends AbstractSwiftService implements IndexingService, Serializable {

    private static final long serialVersionUID = 3153509375653090856L;
    @Autowired
    private transient RpcServer server;

    @Autowired(required = false)
    @Qualifier("indexingService")
    private IndexingService indexingService;
    @Autowired(required = false)
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
    public void setListenerWorker(ListenerWorker listenerWorker) {
        indexingService.setListenerWorker(listenerWorker);
    }

    @Override
    public boolean start() throws SwiftServiceException {
        super.start();
        indexingService.setListenerWorker(new ListenerWorker() {
            @Override
            public void work(Pair<TaskKey, TaskResult> result) {
                SwiftLoggers.getLogger().info("rpc通知server任务完成");
                try {
                    runRpc(new TaskDoneRpcEvent(result));
                    FineIO.doWhenFinished(new ClusterUploadRunnable(result, indexingService.getID()));
                } catch (Exception e) {
                    SwiftLoggers.getLogger().error(e);
                }
            }
        });
        return true;
    }

//    @Override
//    public boolean shutdown() throws SwiftServiceException {
//        EventDispatcher.stopListen(listener);
//        return super.shutdown();
//    }

    private URL getMasterURL() {
        List<SwiftServiceInfoBean> swiftServiceInfoBeans = SwiftContext.get().getBean(SwiftServiceInfoService.class).getServiceInfoByService(ClusterNodeService.SERVICE);
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
