package com.fr.swift.cloud.query.limit;

/**
 * @Author: lucifer
 * @Description:
 * @Date: Created in 2020/11/26
 */
public class SingleLimit implements Limit {

    private static final long serialVersionUID = 3062357026928219912L;
    private int limit;

    public SingleLimit(int limit) {
        this.limit = limit;
    }

    @Override
    public int start() {
        return 0;
    }

    @Override
    public int end() {
        return limit;
    }
}
