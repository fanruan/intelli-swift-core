package com.fr.swift.executor.utils;

import com.fr.swift.executor.queue.ConsumeQueue;
import com.fr.swift.executor.task.ExecutorTask;
import com.fr.swift.executor.task.TaskRouter;
import com.fr.swift.executor.type.SwiftTaskType;
import com.fr.swift.log.SwiftLoggers;
import com.github.rholder.retry.RetryException;
import com.github.rholder.retry.Retryer;
import com.github.rholder.retry.RetryerBuilder;
import com.github.rholder.retry.StopStrategies;
import com.github.rholder.retry.WaitStrategies;

import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @author Heng.J
 * @date 2020/12/21
 * @description TaskRouter、ConsumeQueue中所有任务工具类
 * @since swift-1.2.0
 */
public class TaskQueueUtils {

    public static Retryer<Boolean> getTaskRetryer() {
        Retryer<Boolean> retryer = RetryerBuilder.<Boolean>newBuilder()
                .retryIfResult(result -> Objects.equals(result, true))
                .withStopStrategy(StopStrategies.neverStop())
                .withWaitStrategy(WaitStrategies.fixedWait(5, TimeUnit.MINUTES)) //H.J TODO : 2020/12/3 时间待确定
                .build();
        return retryer;
    }

    /**
     * 检查TaskRouter中是否有migIndex相关任务, 依次移动位置
     */
    public static void clearConflictTasks(String migrateIndex) throws ExecutionException, RetryException {
        getTaskRetryer().call(() -> TaskQueueUtils.increaseTaskPriority(migrateIndex));
    }

    private static boolean increaseTaskPriority(String key) {
        SwiftLoggers.getLogger().info("start to check and move index {} related tasks first", key);
        boolean hasConflict = false;
        int position = 0;
        for (ExecutorTask executorTask : TaskRouter.getInstance().getTaskView(true).getValue()) {
            if (executorTask.getTaskContent().contains(key) && executorTask.getExecutorTaskType().equals(SwiftTaskType.PLANNING)) {
                TaskRouter.getInstance().moveTask(executorTask, position++);
                hasConflict = true;
            }
        }
        if (!hasConflict) {
            hasConflict = ConsumeQueue.getInstance().getTaskList()
                    .stream()
                    .filter(task -> !task.getExecutorTaskType().equals(SwiftTaskType.PLANNING))
                    .map(ExecutorTask::getTaskContent).anyMatch(k -> k.contains(key));
        }
        return hasConflict;
    }
}
