package com.fr.swift.service;

import com.fr.swift.context.SwiftContext;
import com.fr.swift.db.Table;
import com.fr.swift.db.impl.SwiftDatabase;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.util.concurrent.PoolThreadFactory;
import com.fr.swift.util.concurrent.SwiftExecutors;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * This class created on 2018/9/3
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class CollateExecutor implements Runnable {

    public CollateExecutor() {
        SwiftExecutors.newScheduledThreadPool(1, new PoolThreadFactory(getClass())).
                scheduleWithFixedDelay(this, 0, 1, TimeUnit.HOURS);
    }

    @Override
    public void run() {
        triggerCollate();
    }

    private void triggerCollate() {
        try {
            List<Table> tables = SwiftDatabase.getInstance().getAllTables();
            for (Table table : tables) {
                //todo 增加策略，是否需要触发
                SwiftContext.get().getBean(CollateService.class).autoCollate(table.getSourceKey());
            }
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
        }
    }
}
