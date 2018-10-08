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

    private RedisClientPipeline redisPipline;

    public RedisTransactionManager() {
        redisPipline = new RedisClientPipeline();
    }

    @Override
    public void start() {
        super.start();
        redisPipline.switchMultiPipeline();
    }

    @Override
    public void commit() {
        super.commit();
        redisPipline.exec();
        redisPipline.sync();
    }

    @Override
    public void rollback() {
        super.rollback();
    }

    @Override
    public void close() {
        super.close();
        redisPipline.close();
    }

    public RedisClientPipeline getRedisPipline() {
        return redisPipline;
    }
}
