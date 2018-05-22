package com.fr.swift.segment.operator.column;

import com.fr.swift.cube.task.WorkerTask.Worker;

/**
 * @author anchore
 * @date 2018/5/22
 */
public interface SwiftTablePathIndexer extends Worker {
    void buildTablePath() throws Exception;
}