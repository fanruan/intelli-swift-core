package com.fr.swift.redis;

import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.property.SwiftProperty;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.List;

/**
 * This class created on 2018/6/21
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 * todo 使用类型自补充
 */
@SwiftBean(name = "redisClient")
public class RedisClient {

    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(RedisClient.class);

    private JedisPool jedisPool;
    private final SwiftProperty swiftProperty = SwiftProperty.getProperty();

    public RedisClient() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(100);
        poolConfig.setMaxIdle(100);
        poolConfig.setMaxWaitMillis(1000);
        poolConfig.setTestOnBorrow(true);
        jedisPool = new JedisPool(new GenericObjectPoolConfig(), swiftProperty.getRedisIp(), swiftProperty.getRedisPort(), swiftProperty.getRedisTimeout(), swiftProperty.getRedisPassward());
    }

    /**
     * 将一个或多个值 value 插入到列表 key 的表尾(最右边)。
     *
     * @param key
     * @param values
     */
    public void rpush(final String key, final String... values) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.rpush(key, values);
        } catch (Exception e) {
            LOGGER.error(e);
        } finally {
            returnJedis(jedis);
        }
    }

    /**
     * 返回列表 key 中指定区间内的元素，区间以偏移量 start 和 stop 指定。
     * 下标(index)参数 start 和 stop 都以 0 为底，也就是说，以 0 表示列表的第一个元素，以 1 表示列表的第二个元素，以此类推。
     * 你也可以使用负数下标，以 -1 表示列表的最后一个元素， -2 表示列表的倒数第二个元素，以此类推。
     *
     * @param key
     * @param start
     * @param end
     * @return
     */
    public List<String> lrange(String key, long start, long end) throws SwiftRedisException {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.lrange(key, start, end);
        } catch (Exception e) {
            LOGGER.error(e);
            throw new SwiftRedisException(e);
        } finally {
            returnJedis(jedis);
        }
    }

    /**
     * 对一个列表进行修剪(trim)，就是说，让列表只保留指定区间内的元素，不在指定区间之内的元素都将被删除。
     * 举个例子，执行命令 LTRIM list 0 2 ，表示只保留列表 list 的前三个元素，其余元素全部删除。
     * 下标(index)参数 start 和 stop 都以 0 为底，也就是说，以 0 表示列表的第一个元素，以 1 表示列表的第二个元素，以此类推。
     * 你也可以使用负数下标，以 -1 表示列表的最后一个元素， -2 表示列表的倒数第二个元素，以此类推。
     * 当 key 不是列表类型时，返回一个错误。
     *
     * @param key
     * @param start
     * @param end
     * @return
     */
    public String ltrim(String key, long start, long end) throws SwiftRedisException {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.ltrim(key, start, end);
        } catch (Exception e) {
            LOGGER.error(e);
            throw new SwiftRedisException(e);
        } finally {
            returnJedis(jedis);
        }
    }

    /**
     * 返回列表 key 的长度
     * 如果 key 不存在，则 key 被解释为一个空列表，返回 0
     * 如果 key 不是列表类型，返回一个错误
     *
     * @param key
     * @return
     * @throws SwiftRedisException
     */
    public long llen(final String key) throws SwiftRedisException {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.llen(key);
        } catch (Exception e) {
            LOGGER.error(e);
            throw new SwiftRedisException(e);
        } finally {
            returnJedis(jedis);
        }
    }

    public String set(final String key, final String value) throws SwiftRedisException {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.set(key, value);
        } catch (Exception e) {
            LOGGER.error(e);
            throw new SwiftRedisException(e);
        } finally {
            returnJedis(jedis);
        }
    }

    public String get(final String key) throws SwiftRedisException {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.get(key);
        } catch (Exception e) {
            LOGGER.error(e);
            throw new SwiftRedisException(e);
        } finally {
            returnJedis(jedis);
        }
    }

    /**
     * 清除DB
     */
    public void flushDB() {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.flushDB();
        } catch (Exception e) {
            LOGGER.error(e);
        } finally {
            returnJedis(jedis);
        }
    }

    /**
     * 切换当前redis db
     *
     * @param dbIndex
     */
    public void switchDB(int dbIndex) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.select(dbIndex);
        } catch (Exception e) {
            LOGGER.error(e);
        } finally {
            returnJedis(jedis);
        }
    }

    public Jedis getJedis() {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis;
        } catch (Exception e) {
            LOGGER.error(e);
            returnJedis(jedis);
        }
        return null;
    }

    public void returnJedis(Jedis jedis) {
        try {
            if (jedis != null) {
                jedis.close();
            }
        } catch (Exception e) {
            LOGGER.error(e);
        }
    }
}
