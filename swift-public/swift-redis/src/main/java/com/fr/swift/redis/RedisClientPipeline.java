package com.fr.swift.redis;

import com.fr.swift.SwiftContext;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

/**
 * This class created on 2018/6/26
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class RedisClientPipeline {

    private Jedis jedis;
    private Pipeline pipeline;
    private RedisClient redisClient;

    public RedisClientPipeline() {
        this.redisClient = (RedisClient) SwiftContext.get().getBean("redisClient");
        this.jedis = redisClient.getJedis();
        this.pipeline = jedis.pipelined();
    }

    public boolean switchPipeline() {
        if (pipeline.isInMulti()) {
            pipeline.discard();
            return true;
        }
        return false;
    }

    public boolean switchMultiPipeline() {
        if (!pipeline.isInMulti()) {
            pipeline.multi();
            return true;
        }
        return false;
    }

    public void exec() {
        pipeline.exec();
    }

    public void sync() {
        pipeline.syncAndReturnAll();
    }

    public void close() {
        redisClient.returnJedis(jedis);
    }

    public void rPush(final String key, final String... values) {
        pipeline.rpush(key, values);
    }
}
