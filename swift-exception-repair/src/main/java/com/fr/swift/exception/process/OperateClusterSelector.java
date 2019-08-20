package com.fr.swift.exception.process;

import com.fr.swift.exception.ExceptionInfo;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Marvin
 * @date 8/14/2019
 * @description
 * @since swift 1.1
 */
public abstract class OperateClusterSelector {

    public static Set<String> occupiedNodes = new HashSet<>();

    public static Set<String> getOccupiedNodes() {
        return occupiedNodes;
    }

    /**
     * 按照某种规则来选择节点处理异常
     *
     * @param info
     * @return
     */
    abstract String selectCluster(ExceptionInfo info);
}
