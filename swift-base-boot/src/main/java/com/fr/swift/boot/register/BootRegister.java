package com.fr.swift.boot.register;

import com.fr.swift.SwiftContext;
import com.fr.swift.basics.ProcessHandler;
import com.fr.swift.basics.ProcessHandlerRegistry;
import com.fr.swift.basics.ServiceRegistry;
import com.fr.swift.basics.annotation.ProxyService;
import com.fr.swift.basics.annotation.RegisteredHandler;
import com.fr.swift.basics.base.ProxyProcessHandlerRegistry;
import com.fr.swift.basics.base.ProxyServiceRegistry;
import com.fr.swift.config.SwiftConfigRegistryImpl;
import com.fr.swift.executor.dispatcher.TaskDispatcher;
import com.fr.swift.executor.task.ExecutorTypeContainer;
import com.fr.swift.executor.task.impl.CollateExecutorTask;
import com.fr.swift.executor.task.impl.DeleteExecutorTask;
import com.fr.swift.executor.task.impl.RealtimeInsertExecutorTask;
import com.fr.swift.executor.task.impl.RecoveryExecutorTask;
import com.fr.swift.executor.task.impl.TransferExecutorTask;
import com.fr.swift.executor.task.impl.TruncateExecutorTask;
import com.fr.swift.executor.type.SwiftTaskType;
import com.fr.swift.log.SwiftLoggers;

import java.util.List;
import java.util.Map;

/**
 * This class created on 2019/3/5
 *
 * @author Lucifer
 * @description
 */
public class BootRegister {

    public static void registerEntity() throws ClassNotFoundException {
        SwiftConfigRegistryImpl.INSTANCE.registerEntity("com.fr.swift.config.entity.SwiftSegmentEntity");
        SwiftConfigRegistryImpl.INSTANCE.registerEntity("com.fr.swift.config.entity.SwiftSegmentLocationEntity");
        SwiftConfigRegistryImpl.INSTANCE.registerEntity("com.fr.swift.config.entity.SwiftTablePathEntity");
        SwiftConfigRegistryImpl.INSTANCE.registerEntity("com.fr.swift.config.entity.SwiftMetaDataEntity");
        SwiftConfigRegistryImpl.INSTANCE.registerEntity("com.fr.swift.executor.config.SwiftExecutorTaskEntity");

        SwiftConfigRegistryImpl.INSTANCE.registerEntity("com.fr.swift.config.entity.SwiftSegmentBucketElement");
        SwiftConfigRegistryImpl.INSTANCE.registerEntity("com.fr.swift.config.entity.SwiftTableAllotRule");

    }

    public static void registerExecutorTask() {
        ExecutorTypeContainer.getInstance().registerClass(SwiftTaskType.REALTIME, RealtimeInsertExecutorTask.class);
        ExecutorTypeContainer.getInstance().registerClass(SwiftTaskType.RECOVERY, RecoveryExecutorTask.class);
        ExecutorTypeContainer.getInstance().registerClass(SwiftTaskType.TRANSFER, TransferExecutorTask.class);
        ExecutorTypeContainer.getInstance().registerClass(SwiftTaskType.DELETE, DeleteExecutorTask.class);
        ExecutorTypeContainer.getInstance().registerClass(SwiftTaskType.TRUNCATE, TruncateExecutorTask.class);
        ExecutorTypeContainer.getInstance().registerClass(SwiftTaskType.COLLATE, CollateExecutorTask.class);
    }

    public static void registerListener() {
        TaskDispatcher.getInstance();
//        TransferRealtimeListener.listen();
    }

    public static void registerProxy() {
        //RPC远端可接收调用的SERVICE
        ServiceRegistry serviceRegistry = ProxyServiceRegistry.get();
        Map<String, Object> proxyServices = SwiftContext.get().getBeansByAnnotations(ProxyService.class);
        for (Map.Entry<String, Object> proxyService : proxyServices.entrySet()) {
            serviceRegistry.registerService(proxyService.getValue());
        }

        ProcessHandlerRegistry processHandlerRegistry = ProxyProcessHandlerRegistry.get();
        List<Class<?>> clazzList = SwiftContext.get().getClassesByAnnotations(RegisteredHandler.class);
        for (Class<?> clazz : clazzList) {
            try {
                RegisteredHandler registeredHandler = clazz.getAnnotation(RegisteredHandler.class);
                if (registeredHandler != null) {
                    processHandlerRegistry.addHandler(registeredHandler.value(), (Class<? extends ProcessHandler>) clazz);
                }
            } catch (IllegalArgumentException error) {
                SwiftLoggers.getLogger().error(error);
            }
        }
    }
}
