package com.fr.swift.service;

import com.fr.swift.SwiftContext;
import com.fr.swift.basic.URL;
import com.fr.swift.basics.Invoker;
import com.fr.swift.basics.InvokerCreator;
import com.fr.swift.basics.annotation.RegisteredHandler;
import com.fr.swift.basics.annotation.Target;
import com.fr.swift.basics.base.handler.BaseProcessHandler;
import com.fr.swift.basics.base.selector.UrlSelector;
import com.fr.swift.basics.exception.TargetNodeOfflineException;
import com.fr.swift.basics.handler.TaskProcessHandler;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.beans.annotation.SwiftScope;
import com.fr.swift.executor.config.ExecutorTaskService;
import com.fr.swift.executor.task.impl.PlanningExecutorTask;
import com.fr.swift.log.SwiftLoggers;

import java.lang.reflect.Method;

/**
 * @author Heng.J
 * @date 2020/10/23
 * @description
 * @since swift-1.2.0
 */
@SwiftBean
@SwiftScope("prototype")
@RegisteredHandler(TaskProcessHandler.class)
public class SwiftTaskProcessHandler extends BaseProcessHandler implements TaskProcessHandler {

    public SwiftTaskProcessHandler(InvokerCreator invokerCreator) {
        super(invokerCreator);
    }

    @Override
    protected URL processUrl(Target[] targets, Object... args) {
        if (nodeContainer.getOnlineNodes().containsKey(args[1])) {
            return UrlSelector.getInstance().getFactory().getURL(args[1]);
        }
        throw new TargetNodeOfflineException((String) args[1]);
    }

    @Override
    public Object processResult(Method method, Target[] targets, Object... args) throws Throwable {
        String taskContent = (String) args[0];
        String clusterId = (String) args[1];
        final Class<?> proxyClass = method.getDeclaringClass();
        final Class<?>[] parameterTypes = method.getParameterTypes();
        final String methodName = method.getName();
        try {
            Invoker<?> invoker = invokerCreator.createSyncInvoker(proxyClass, processUrl(targets, args));
            invoke(invoker, proxyClass, method, methodName, parameterTypes, args);
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
            backupPlanningTask(taskContent, clusterId);
        }
        return true;
    }

    // 其他节点掉了同delete, 保存任务到数据库
    private void backupPlanningTask(String taskContent, String clusterId) {
        ExecutorTaskService executorTaskService = SwiftContext.get().getBean(ExecutorTaskService.class);
        try {
            executorTaskService.save(PlanningExecutorTask.ofByCluster(taskContent, clusterId));
        } catch (Exception ignore) {
            SwiftLoggers.getLogger().warn(ignore);
        }
    }
}
