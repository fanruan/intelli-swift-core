package com.fr.swift.service;

import com.fr.swift.context.SwiftContext;
import com.fr.swift.db.Table;
import com.fr.swift.db.impl.SwiftDatabase;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.util.concurrent.PoolThreadFactory;
import com.fr.swift.util.concurrent.SwiftExecutors;
import com.fr.third.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * This class created on 2018/9/3
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
@Service
public class CollateExecutor implements Runnable {

    private CollateService collateService;

    private CollateExecutor() {
        SwiftExecutors.newScheduledThreadPool(1, new PoolThreadFactory(getClass())).
                scheduleWithFixedDelay(this, 0, 1, TimeUnit.HOURS);
        collateService = SwiftContext.get().getBean(CollateService.class);
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
                collateService.autoCollate(table.getSourceKey());
            }
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
        }
    }
}
