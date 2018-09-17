package com.fr.swift.service;

import com.fr.swift.info.ServerCurrentStatus;
import com.fr.swift.structure.Pair;
import com.fr.swift.stuff.IndexingStuff;
import com.fr.swift.task.TaskKey;
import com.fr.swift.task.TaskResult;

/**
 * @author anchore
 * @date 2018/6/4
 */
public interface IndexingService extends SwiftService {
    /**
     * 全量导入
     *
     * @param stuff stuff
     */
    <Stuff extends IndexingStuff> void index(Stuff stuff);

    ServerCurrentStatus currentStatus();

    void setListenerWorker(ListenerWorker listenerWorker);

    interface ListenerWorker {
        void work(Pair<TaskKey, TaskResult> result);
    }
}