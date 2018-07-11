package com.fr.swift.generate;

import com.fr.swift.task.WorkerTask;

/**
 * This class created on 2018/4/18
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public interface SwiftTableBuilder extends WorkerTask.Worker {
    void build() throws Exception;
}
