package com.fr.swift.query.limit;

/**
 * @Author: lucifer
 * @Description:
 * @Date: Created in 2020/11/26
 */
public class RangeLimit implements Limit {

    private static final long serialVersionUID = 957840477294066959L;
    private int start, end;

    public RangeLimit(int start, int end) {
        this.start = start;
        this.end = end;
    }

    @Override
    public int start() {
        return start;
    }

    @Override
    public int end() {
        return end;
    }
}
