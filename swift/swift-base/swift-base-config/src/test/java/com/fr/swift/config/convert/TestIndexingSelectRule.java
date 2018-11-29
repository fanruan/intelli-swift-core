package com.fr.swift.config.convert;

import com.fr.swift.config.IndexingSelectRule;
import com.fr.swift.config.bean.ServerCurrentStatus;

import java.util.List;

/**
 * @author yee
 * @date 2018-11-29
 */
public class TestIndexingSelectRule implements IndexingSelectRule {
    private String name;

    public TestIndexingSelectRule() {
    }

    public TestIndexingSelectRule(String name) {
        this.name = name;
    }


    @Override
    public String toString() {
        return name;
    }

    @Override
    public ServerCurrentStatus select(List<ServerCurrentStatus> statuses) throws Exception {
        return new ServerCurrentStatus("LOCAL");
    }
}
