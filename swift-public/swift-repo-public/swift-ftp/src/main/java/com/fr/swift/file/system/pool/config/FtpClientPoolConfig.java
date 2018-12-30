package com.fr.swift.file.system.pool.config;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

/**
 * @author yee
 * @date 2018-12-29
 */
public class FtpClientPoolConfig {
    private Integer maxTotal = 40;
    private Integer maxIdle = 20;
    private Integer minIdle = 0;
    private Boolean testOnCreate = false;
    private Boolean testOnBorrow = true;
    private Boolean testOnReturn = false;
    private Boolean testWhileIdle = false;
    private Long maxWaitMillis = -1L;
    private Boolean blockWhenExhausted = true;

    public GenericObjectPoolConfig getPoolConfig() {
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        config.setMaxTotal(getMaxTotal());
        config.setMaxIdle(getMaxIdle());
        config.setMinIdle(getMinIdle());
        config.setTestWhileIdle(getTestWhileIdle());
        config.setTestOnReturn(getTestOnReturn());
        config.setTestWhileIdle(getTestWhileIdle());
        config.setTestOnBorrow(getTestOnBorrow());
        config.setMaxWaitMillis(getMaxWaitMillis());
        config.setBlockWhenExhausted(getBlockWhenExhausted());
        return config;
    }

    public Integer getMaxTotal() {
        return maxTotal;
    }

    public void setMaxTotal(Integer maxTotal) {
        this.maxTotal = maxTotal;
    }

    public Integer getMaxIdle() {
        return maxIdle;
    }

    public void setMaxIdle(Integer maxIdle) {
        this.maxIdle = maxIdle;
    }

    public Integer getMinIdle() {
        return minIdle;
    }

    public void setMinIdle(Integer minIdle) {
        this.minIdle = minIdle;
    }

    public Boolean getTestOnCreate() {
        return testOnCreate;
    }

    public void setTestOnCreate(Boolean testOnCreate) {
        this.testOnCreate = testOnCreate;
    }

    public Boolean getTestOnBorrow() {
        return testOnBorrow;
    }

    public void setTestOnBorrow(Boolean testOnBorrow) {
        this.testOnBorrow = testOnBorrow;
    }

    public Boolean getTestOnReturn() {
        return testOnReturn;
    }

    public void setTestOnReturn(Boolean testOnReturn) {
        this.testOnReturn = testOnReturn;
    }

    public Boolean getTestWhileIdle() {
        return testWhileIdle;
    }

    public void setTestWhileIdle(Boolean testWhileIdle) {
        this.testWhileIdle = testWhileIdle;
    }

    public Long getMaxWaitMillis() {
        return maxWaitMillis;
    }

    public void setMaxWaitMillis(Long maxWaitMillis) {
        this.maxWaitMillis = maxWaitMillis;
    }

    public Boolean getBlockWhenExhausted() {
        return blockWhenExhausted;
    }

    public void setBlockWhenExhausted(Boolean blockWhenExhausted) {
        this.blockWhenExhausted = blockWhenExhausted;
    }
}
