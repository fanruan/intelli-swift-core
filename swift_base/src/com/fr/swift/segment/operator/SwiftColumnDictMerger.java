package com.fr.swift.segment.operator;

import com.fr.swift.cube.task.WorkerTask;

/**
 * This class created on 2018/4/18
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public interface SwiftColumnDictMerger extends WorkerTask.Worker {
    void mergeDict() throws Exception;
}
