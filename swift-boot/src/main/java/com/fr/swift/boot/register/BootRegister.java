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
import com.fr.swift.executor.task.ExecutorTypeContainer;
import com.fr.swift.executor.task.impl.CollateExecutorTask;
import com.fr.swift.executor.task.impl.DeleteExecutorTask;
import com.fr.swift.executor.task.impl.DownloadExecutorTask;
import com.fr.swift.executor.task.impl.HistoryImportExecutorTask;
import com.fr.swift.executor.task.impl.RealtimeInsertExecutorTask;
import com.fr.swift.executor.task.impl.RecoveryExecutorTask;
import com.fr.swift.executor.task.impl.TransferExecutorTask;
import com.fr.swift.executor.task.impl.TruncateExecutorTask;
import com.fr.swift.executor.task.impl.UploadExecutorTask;
import com.fr.swift.executor.type.SwiftTaskType;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.event.MaskHistoryListener;
import com.fr.swift.segment.event.PushSegmentLocationListener;
import com.fr.swift.segment.event.RemoveHistoryListener;
import com.fr.swift.segment.event.RemoveSegmentLocationListener;
import com.fr.swift.segment.event.TransferRealtimeListener;
import com.fr.swift.segment.event.UploadHistoryListener;

import java.util.List;
import java.util.Map;

/**
 * This class created on 2019/3/5
 *
 * @author Lucifer
 * @description
 */
public class BootRegister {

    public static void registerEntity(ClassLoader loader) throws ClassNotFoundException {
        SwiftConfigRegistryImpl.INSTANCE.registerEntity("com.fr.swift.config.entity.SwiftSegmentEntity", loader);
        SwiftConfigRegistryImpl.INSTANCE.registerEntity("com.fr.swift.config.entity.SwiftColumnIndexingConf", loader);
        SwiftConfigRegistryImpl.INSTANCE.registerEntity("com.fr.swift.config.entity.SwiftConfigEntity", loader);
        SwiftConfigRegistryImpl.INSTANCE.registerEntity("com.fr.swift.config.entity.SwiftSegmentLocationEntity", loader);
        SwiftConfigRegistryImpl.INSTANCE.registerEntity("com.fr.swift.config.entity.SwiftServiceInfoEntity", loader);
        SwiftConfigRegistryImpl.INSTANCE.registerEntity("com.fr.swift.config.entity.SwiftTablePathEntity", loader);
        SwiftConfigRegistryImpl.INSTANCE.registerEntity("com.fr.swift.base.meta.SwiftMetaDataBean", loader);
        SwiftConfigRegistryImpl.INSTANCE.registerEntity("com.fr.swift.executor.config.SwiftExecutorTaskEntity", loader);
        SwiftConfigRegistryImpl.INSTANCE.registerEntity("com.fr.swift.config.entity.SwiftJdbcServerInfo", loader);
        SwiftConfigRegistryImpl.INSTANCE.registerEntity("com.fr.swift.config.entity.SwiftSegmentBucketElement", loader);
        SwiftConfigRegistryImpl.INSTANCE.registerEntity("com.fr.swift.config.entity.SwiftTableAllotRule", loader);
        SwiftConfigRegistryImpl.INSTANCE.registerEntity("com.fr.swift.exception.ExceptionInfoBean", loader);
    }

    public static void registerExecutorTask() {
        ExecutorTypeContainer.getInstance().registerClass(SwiftTaskType.REALTIME, RealtimeInsertExecutorTask.class);
        ExecutorTypeContainer.getInstance().registerClass(SwiftTaskType.RECOVERY, RecoveryExecutorTask.class);
        ExecutorTypeContainer.getInstance().registerClass(SwiftTaskType.TRANSFER, TransferExecutorTask.class);
        ExecutorTypeContainer.getInstance().registerClass(SwiftTaskType.DELETE, DeleteExecutorTask.class);
        ExecutorTypeContainer.getInstance().registerClass(SwiftTaskType.TRUNCATE, TruncateExecutorTask.class);
        ExecutorTypeContainer.getInstance().registerClass(SwiftTaskType.COLLATE, CollateExecutorTask.class);
        ExecutorTypeContainer.getInstance().registerClass(SwiftTaskType.UPLOAD, UploadExecutorTask.class);
        ExecutorTypeContainer.getInstance().registerClass(SwiftTaskType.DOWNLOAD, DownloadExecutorTask.class);
        ExecutorTypeContainer.getInstance().registerClass(SwiftTaskType.HISTORY, HistoryImportExecutorTask.class);
    }

    public static void registerListener() {
        TransferRealtimeListener.listen();
        UploadHistoryListener.listen();
        MaskHistoryListener.listen();
        RemoveHistoryListener.listen();
        PushSegmentLocationListener.listen();
        RemoveSegmentLocationListener.listen();
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
