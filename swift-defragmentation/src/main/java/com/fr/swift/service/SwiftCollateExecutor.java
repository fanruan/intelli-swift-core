package com.fr.swift.service;

import com.fr.swift.SwiftContext;
import com.fr.swift.basics.base.selector.ProxySelector;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.config.service.SwiftSegmentService;
import com.fr.swift.config.service.impl.SwiftSegmentServiceProvider;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.service.executor.CollateExecutor;
import com.fr.swift.source.SourceKey;
import com.fr.swift.util.concurrent.PoolThreadFactory;
import com.fr.swift.util.concurrent.SwiftExecutors;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
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
public final class SwiftCollateExecutor implements Runnable, CollateExecutor {

    private ScheduledExecutorService executorService;

    private SwiftSegmentService swiftSegmentService;

    private static DateFormat DATE_FORMAT = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
    private static DateFormat DAY_FORMAT = new SimpleDateFormat("yy-MM-dd");
    private static long ONE_DAY = 24 * 60 * 60 * 1000;

    private SwiftCollateExecutor() {
    }


    @Override
    public void start() {
        long initDelay = getTimeMillis("4:00:00") - System.currentTimeMillis();
        initDelay = initDelay > 0 ? initDelay : ONE_DAY + initDelay;

        executorService = SwiftExecutors.newScheduledThreadPool(1, new PoolThreadFactory(getClass()));
//        executorService.scheduleWithFixedDelay(this, 60, 60, TimeUnit.MINUTES);
        executorService.scheduleAtFixedRate(this, initDelay, ONE_DAY, TimeUnit.MILLISECONDS);

        swiftSegmentService = SwiftContext.get().getBean(SwiftSegmentServiceProvider.class);
    }

    @Override
    public void stop() {
        executorService.shutdown();
    }

    private static long getTimeMillis(String time) {
        try {
            Date currentDate = DATE_FORMAT.parse(DAY_FORMAT.format(new Date()) + " " + time);
            return currentDate.getTime();
        } catch (ParseException e) {
            return 0;
        }
    }


    @Override
    public void run() {
        triggerCollate();
    }

    private void triggerCollate() {
        try {
            Map<SourceKey, List<SegmentKey>> allSegments = swiftSegmentService.getAllSegments();
            for (Map.Entry<SourceKey, List<SegmentKey>> tableEntry : allSegments.entrySet()) {
                List<SegmentKey> keys = new ArrayList<SegmentKey>();
                for (SegmentKey key : tableEntry.getValue()) {
                    if (key.getStoreType().isTransient()) {
                        continue;
                    }
                    keys.add(key);
                }
                if (!keys.isEmpty()) {
                    ProxySelector.getProxy(ServiceContext.class).appointCollate(tableEntry.getKey(), keys);
                }
            }
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
        }
    }
}
