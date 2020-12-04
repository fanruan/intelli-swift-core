package com.fr.swift.query.info.bean.element;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @Author: lucifer
 * @Description:
 * @Date: Created in 2020/11/26
 */
public class LimitBean {
    @JsonProperty
    private int start;
    @JsonProperty
    private int end;

    private LimitBean() {
    }

    public LimitBean(int limit) {
        this(0, limit);
    }

    public LimitBean(int start, int end) {
        this.start = start;
        this.end = end;
    }

    public int start() {
        return start;
    }

    public int end() {
        return end;
    }
}
