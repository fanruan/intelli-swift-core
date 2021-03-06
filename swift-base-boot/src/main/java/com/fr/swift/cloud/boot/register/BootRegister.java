package com.fr.swift.cloud.boot.register;

import com.fr.swift.cloud.SwiftContext;
import com.fr.swift.cloud.basics.ProcessHandler;
import com.fr.swift.cloud.basics.ProcessHandlerRegistry;
import com.fr.swift.cloud.basics.ServiceRegistry;
import com.fr.swift.cloud.basics.annotation.ProxyService;
import com.fr.swift.cloud.basics.annotation.RegisteredHandler;
import com.fr.swift.cloud.basics.base.ProxyProcessHandlerRegistry;
import com.fr.swift.cloud.basics.base.ProxyServiceRegistry;
import com.fr.swift.cloud.boot.trigger.ClusterInitTrigger;
import com.fr.swift.cloud.boot.trigger.ContainerCacheTrigger;
import com.fr.swift.cloud.boot.trigger.ServicePriorityInitiator;
import com.fr.swift.cloud.boot.trigger.SwiftServiceInitTrigger;
import com.fr.swift.cloud.boot.trigger.TaskDispatcherInitTrigger;
import com.fr.swift.cloud.boot.trigger.TaskDistributeTrigger;
import com.fr.swift.cloud.cluster.base.handler.JoinClusterListenerHandler;
import com.fr.swift.cloud.cluster.base.handler.LeftClusterListenerHandler;
import com.fr.swift.cloud.cluster.base.initiator.MasterServiceInitiator;
import com.fr.swift.cloud.config.SwiftConfigRegistryImpl;
import com.fr.swift.cloud.executor.task.ExecutorTypeContainer;
import com.fr.swift.cloud.executor.task.impl.CollateExecutorTask;
import com.fr.swift.cloud.executor.task.impl.DeleteExecutorTask;
import com.fr.swift.cloud.executor.task.impl.MigrateExecutorTask;
import com.fr.swift.cloud.executor.task.impl.PlanningExecutorTask;
import com.fr.swift.cloud.executor.task.impl.RealtimeInsertExecutorTask;
import com.fr.swift.cloud.executor.task.impl.RecoveryExecutorTask;
import com.fr.swift.cloud.executor.task.impl.TruncateExecutorTask;
import com.fr.swift.cloud.executor.type.SwiftTaskType;
import com.fr.swift.cloud.log.SwiftLoggers;
import com.fr.swift.cloud.quartz.ScheduleTaskTrigger;
import com.fr.swift.cloud.trigger.RefreshMigrateTaskTrigger;

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
        SwiftConfigRegistryImpl.INSTANCE.registerEntity("com.fr.swift.cloud.config.entity.SwiftSegmentEntity");
        SwiftConfigRegistryImpl.INSTANCE.registerEntity("com.fr.swift.cloud.config.entity.SwiftSegmentLocationEntity");
        SwiftConfigRegistryImpl.INSTANCE.registerEntity("com.fr.swift.cloud.config.entity.SwiftTablePathEntity");
        SwiftConfigRegistryImpl.INSTANCE.registerEntity("com.fr.swift.cloud.config.entity.SwiftMetaDataEntity");
        SwiftConfigRegistryImpl.INSTANCE.registerEntity("com.fr.swift.cloud.config.entity.SwiftSegmentBucketElement");
        SwiftConfigRegistryImpl.INSTANCE.registerEntity("com.fr.swift.cloud.config.entity.SwiftTableAllotRule");
        SwiftConfigRegistryImpl.INSTANCE.registerEntity("com.fr.swift.cloud.config.entity.SwiftUserInfo");
        SwiftConfigRegistryImpl.INSTANCE.registerEntity("com.fr.swift.cloud.config.entity.SwiftNodeInfoEntity");
        SwiftConfigRegistryImpl.INSTANCE.registerEntity("com.fr.swift.cloud.executor.config.SwiftExecutorTaskEntity");
        SwiftConfigRegistryImpl.INSTANCE.registerEntity("com.fr.swift.cloud.executor.config.SwiftBlockTaskEntity");
        SwiftConfigRegistryImpl.INSTANCE.registerEntity("com.fr.swift.cloud.executor.config.SwiftRepeatTaskEntity");
        SwiftConfigRegistryImpl.INSTANCE.registerEntity("com.fr.swift.cloud.executor.message.MessageSendingRecordEntity");
        SwiftConfigRegistryImpl.INSTANCE.registerEntity("com.fr.swift.cloud.executor.config.TaskBalanceEntity");

    }

    public static void registerExecutorTask() {
        ExecutorTypeContainer.getInstance().registerClass(SwiftTaskType.REALTIME, RealtimeInsertExecutorTask.class);
        ExecutorTypeContainer.getInstance().registerClass(SwiftTaskType.RECOVERY, RecoveryExecutorTask.class);
        ExecutorTypeContainer.getInstance().registerClass(SwiftTaskType.DELETE, DeleteExecutorTask.class);
        ExecutorTypeContainer.getInstance().registerClass(SwiftTaskType.TRUNCATE, TruncateExecutorTask.class);
        ExecutorTypeContainer.getInstance().registerClass(SwiftTaskType.COLLATE, CollateExecutorTask.class);
        ExecutorTypeContainer.getInstance().registerClass(SwiftTaskType.MIGRATE, MigrateExecutorTask.class);
        ExecutorTypeContainer.getInstance().registerClass(SwiftTaskType.PLANNING, PlanningExecutorTask.class);
    }

    public static void registerServiceTrigger() {
        ServicePriorityInitiator.getInstance().register(new SwiftServiceInitTrigger());
        ServicePriorityInitiator.getInstance().register(new TaskDispatcherInitTrigger());
        ServicePriorityInitiator.getInstance().register(new ClusterInitTrigger());
        ServicePriorityInitiator.getInstance().register(new ScheduleTaskTrigger());

        MasterServiceInitiator.getInstance().register(new ContainerCacheTrigger());
        MasterServiceInitiator.getInstance().register(new TaskDistributeTrigger());
        MasterServiceInitiator.getInstance().register(new RefreshMigrateTaskTrigger());

        JoinClusterListenerHandler.listen();
        LeftClusterListenerHandler.listen();
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
