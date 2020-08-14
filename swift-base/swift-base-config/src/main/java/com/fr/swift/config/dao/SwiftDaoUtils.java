package com.fr.swift.config.dao;

import com.fr.swift.log.SwiftLoggers;
import com.github.rholder.retry.Retryer;
import com.github.rholder.retry.RetryerBuilder;
import com.github.rholder.retry.StopStrategies;
import com.github.rholder.retry.WaitStrategies;
import org.hibernate.Transaction;
import org.hibernate.exception.LockAcquisitionException;

import java.util.concurrent.TimeUnit;

/**
 * @author Heng.J
 * @date 2020/8/6
 * @description
 * @since swift-1.2.0
 */
public class SwiftDaoUtils {

    /**
     * 重复提交解决锁死
     * 针对 OptimisticLockException
     * 尝试10次 每次间隔500ms
     *
     * @param transaction
     */
    public static void deadlockFreeCommit(Transaction transaction) {
        Retryer<Boolean> retryer = RetryerBuilder.<Boolean>newBuilder()
                .retryIfExceptionOfType(LockAcquisitionException.class)
                .withStopStrategy(StopStrategies.stopAfterAttempt(10))
                .withWaitStrategy(WaitStrategies.fixedWait(500, TimeUnit.MILLISECONDS))
                .build();
        try {
            retryer.call(() -> {
                transaction.commit();
                return true;
            });
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
        }
    }
}
