package com.fr.swift.cloud.config.dao;

import com.fr.swift.cloud.log.SwiftLoggers;
import com.github.rholder.retry.RetryException;
import com.github.rholder.retry.Retryer;
import com.github.rholder.retry.RetryerBuilder;
import com.github.rholder.retry.StopStrategies;
import com.github.rholder.retry.WaitStrategies;
import org.hibernate.exception.LockAcquisitionException;

import javax.persistence.OptimisticLockException;
import javax.persistence.PessimisticLockException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Heng.J
 * @date 2020/8/6
 * @description
 * @since swift-1.2.0
 */
public class SwiftDaoUtils {

    /**
     * 重复提交解决锁死
     * 针对 OptimisticLockException 和 LockAcquisitionException
     * 尝试10次 每次间隔500ms
     *
     * @param runnable 业务逻辑代码
     */
    public static void deadlockFreeCommit(Runnable runnable) {
        Retryer<Boolean> retryer = RetryerBuilder.<Boolean>newBuilder()
                .retryIfExceptionOfType(LockAcquisitionException.class)
                .retryIfExceptionOfType(OptimisticLockException.class)
                .withStopStrategy(StopStrategies.stopAfterAttempt(10))
                .withWaitStrategy(WaitStrategies.fixedWait(500, TimeUnit.MILLISECONDS))
                .build();
        try {
            AtomicInteger i = new AtomicInteger(0);
            retryer.call(() -> {
                int count = i.getAndIncrement();
                if (count > 0) {
                    SwiftLoggers.getLogger().warn("current have committed : {} times", count);
                }
                runnable.run();
                return true;
            });
        } catch (ExecutionException | RetryException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 重复提交解决悲观锁
     * 针对 PessimisticLockException
     * 本身SHOW VARIABLES LIKE 'innodb_lock_wait_timeout' = 50s
     * 尝试3次 每次间隔1s, 最坏耗时152S
     */
    public static void pessimisticLockFreeCommit(Runnable runnable) {
        Retryer<Boolean> retryer = RetryerBuilder.<Boolean>newBuilder()
                .retryIfExceptionOfType(PessimisticLockException.class)
                .withStopStrategy(StopStrategies.stopAfterAttempt(3))
                .withWaitStrategy(WaitStrategies.fixedWait(1, TimeUnit.SECONDS))
                .build();
        try {
            AtomicInteger i = new AtomicInteger(0);
            retryer.call(() -> {
                int count = i.getAndIncrement();
                if (count > 0) {
                    SwiftLoggers.getLogger().warn("pessimistic have committed: {} times", count);
                }
                runnable.run();
                return true;
            });
        } catch (ExecutionException | RetryException e) {
            throw new RuntimeException(e);
        }
    }
}
