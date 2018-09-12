package com.fr.swift.task.cube;

import com.fr.swift.task.Operation;
import com.fr.swift.task.impl.SwiftTaskKey;

/**
 * @author anchore
 * @date 2018/7/10
 */
public class CubeTaskKey extends SwiftTaskKey {
    public CubeTaskKey(int round, String name) {
        super(round, name, CubeOperation.NULL);
    }

    public CubeTaskKey(int round, String name, Operation operation) {
        super(round, name, operation);
    }

    public CubeTaskKey(int round, String name, Operation operation, String info) {
        super(round, name, operation, info);
    }
}