package com.fr.swift.service;

import com.fr.swift.basics.base.selector.ProxySelector;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.source.SourceKey;
import com.fr.swift.util.concurrent.PoolThreadFactory;
import com.fr.swift.util.concurrent.SwiftExecutors;

import java.util.Collections;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * This class created on 2018/9/3
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
@SwiftBean
public final class CollateExecutor implements Runnable {

    private ScheduledExecutorService executorService;

    private CollateExecutor() {

    }

    public void start() {
        executorService = SwiftExecutors.newScheduledThreadPool(1, new PoolThreadFactory(getClass()));
        executorService.scheduleWithFixedDelay(this, 60, 60, TimeUnit.MINUTES);
    }

    public void stop() {
        executorService.shutdown();
    }

    @Override
    public void run() {
        triggerCollate();
    }

    private void triggerCollate() {
        try {
            ProxySelector.getProxy(CollateService.class).appointCollate(new SourceKey(), Collections.<SegmentKey>emptyList());
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
        }
    }
}
