package com.fr.swift.generate;

import com.fr.swift.cube.task.Task;
import com.fr.swift.cube.task.impl.BaseWorker;
import com.fr.swift.cube.task.impl.LocalTaskGroup;
import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.source.DataSource;

/**
 * @Author: Lucifer
 * @Description:
 * @Date: Created in 2018-3-6
 */
public abstract class BaseTableBuilder extends BaseWorker {

    protected DataSource dataSource;

    protected LocalTaskGroup taskGroup;

    public BaseTableBuilder(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    protected abstract void init() throws SwiftMetaDataException;

    @Override
    public void work() {
        try {
            init();
            taskGroup.run();
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
            workOver(Task.Result.FAILED);
        }
    }
}
