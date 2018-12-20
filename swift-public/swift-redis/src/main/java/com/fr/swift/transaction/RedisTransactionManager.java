package com.fr.swift.transaction;

import com.fr.swift.redis.RedisClientPipeline;

/**
 * This class created on 2018/6/26
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class RedisTransactionManager extends AbstractTransactionManager {

    private RedisClientPipeline redisPipeline;

    public RedisTransactionManager() {
        redisPipeline = new RedisClientPipeline();
    }

    @Override
    public void start() {
        super.start();
        redisPipeline.switchMultiPipeline();
    }

    @Override
    public void commit() {
        redisPipeline.exec();
        redisPipeline.sync();
    }

    @Override
    public void rollback() {
        super.rollback();
    }

    @Override
    public void close() {
        redisPipeline.close();
    }

    public RedisClientPipeline getRedisPipeline() {
        return redisPipeline;
    }
}
