package com.fr.swift.service;

import com.fr.swift.SwiftContext;
import com.fr.swift.annotation.SwiftService;
import com.fr.swift.basics.annotation.ProxyService;
import com.fr.swift.basics.base.selector.ProxySelector;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.config.service.SwiftSegmentService;
import com.fr.swift.cube.io.Types;
import com.fr.swift.db.Database;
import com.fr.swift.db.Table;
import com.fr.swift.db.impl.SwiftDatabase;
import com.fr.swift.event.SwiftEventDispatcher;
import com.fr.swift.event.base.SwiftRpcEvent;
import com.fr.swift.event.history.HistoryRemoveEvent;
import com.fr.swift.exception.SwiftServiceException;
import com.fr.swift.exception.TableNotExistException;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.property.SwiftProperty;
import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SegmentResultSet;
import com.fr.swift.segment.SegmentUtils;
import com.fr.swift.segment.SwiftSegmentManager;
import com.fr.swift.segment.collate.FragmentCollectRule;
import com.fr.swift.segment.collate.SwiftFragmentCollectRule;
import com.fr.swift.segment.event.SegmentEvent;
import com.fr.swift.segment.event.SyncSegmentLocationEvent;
import com.fr.swift.segment.operator.Collater;
import com.fr.swift.segment.operator.collate.HistoryCollater;
import com.fr.swift.service.listener.RemoteSender;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.alloter.SwiftSourceAlloter;
import com.fr.swift.source.alloter.impl.line.HistoryLineSourceAlloter;
import com.fr.swift.source.alloter.impl.line.LineAllotRule;
import com.fr.swift.source.resultset.CoSwiftResultSet;
import com.fr.swift.task.service.ServiceTaskExecutor;
import com.fr.swift.task.service.ServiceTaskType;
import com.fr.swift.task.service.SwiftServiceCallable;
import com.fr.swift.util.concurrent.CommonExecutor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * This class created on 2018/7/9
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
@SwiftService(name = "collate")
@SwiftBean(name = "collate")
@ProxyService(CollateService.class)
public class SwiftCollateService extends AbstractSwiftService implements CollateService {

    private static final long serialVersionUID = 7259915342007294244L;

    private transient SwiftSegmentManager segmentManager;

    private transient Database database;

    private transient ServiceTaskExecutor taskExecutor;

    private transient SwiftSegmentService swiftSegmentService;


    public void setTaskExecutor(ServiceTaskExecutor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }

    private SwiftCollateService() {
    }

    @Override
    public boolean start() throws SwiftServiceException {
        super.start();
        segmentManager = SwiftContext.get().getBean("localSegmentProvider", SwiftSegmentManager.class);
        database = SwiftDatabase.getInstance();
        taskExecutor = SwiftContext.get().getBean(ServiceTaskExecutor.class);
        swiftSegmentService = SwiftContext.get().getBean("segmentServiceProvider", SwiftSegmentService.class);
        return true;
    }

    @Override
    public boolean shutdown() throws SwiftServiceException {
        super.shutdown();
        segmentManager = null;
        database = null;
        taskExecutor = null;
        swiftSegmentService = null;
        return true;
    }

    @Override
    public void autoCollateRealtime(final SourceKey tableKey) throws Exception {
        final List<SegmentKey> segmentKeys = segmentManager.getSegmentKeys(tableKey);
        checkSegmentKeys(segmentKeys, Types.StoreType.MEMORY);
        taskExecutor.submit(new SwiftServiceCallable<Void>(tableKey, ServiceTaskType.COLLATE, new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                collateSegments(tableKey, segmentKeys);
                return null;
            }
        }));
    }

    @Override
    public void autoCollateHistory(final SourceKey tableKey) throws Exception {
        final List<SegmentKey> segmentKeys = segmentManager.getSegmentKeys(tableKey);
        checkSegmentKeys(segmentKeys, Types.StoreType.FINE_IO);
        taskExecutor.submit(new SwiftServiceCallable<Void>(tableKey, ServiceTaskType.COLLATE, new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                collateSegments(tableKey, segmentKeys);
                return null;
            }
        }));
    }

    @Override
    public void appointCollate(final SourceKey tableKey, final List<SegmentKey> segmentKeyList) throws Exception {
        taskExecutor.submit(new SwiftServiceCallable<Void>(tableKey, ServiceTaskType.COLLATE, new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                collateSegments(tableKey, segmentKeyList);
                return null;
            }
        }));
    }

    @Override
    public void autoCollate(final SourceKey tableKey) throws Exception {
        taskExecutor.submit(new SwiftServiceCallable<Void>(tableKey, ServiceTaskType.COLLATE, new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                collateSegments(tableKey);
                return null;
            }
        }));
    }

    @Override
    public ServiceType getServiceType() {
        return ServiceType.COLLATE;
    }

    private void collateSegments(SourceKey tableKey) throws Exception {
        List<SegmentKey> segKeys = segmentManager.getSegmentKeys(tableKey);
        collateSegments(tableKey, segKeys);
    }

    private void collateSegments(SourceKey tableKey, final List<SegmentKey> collateSegKeys) throws Exception {
        SwiftSourceAlloter alloter = new HistoryLineSourceAlloter(tableKey, new LineAllotRule(LineAllotRule.STEP));
        collateSegments(tableKey, collateSegKeys, alloter);
    }

    private void collateSegments(SourceKey tableKey, final List<SegmentKey> allCollateSegKeys, SwiftSourceAlloter alloter) throws Exception {
        FragmentCollectRule collectRule = new SwiftFragmentCollectRule(alloter);
        List<SegmentKey> collateSegKeys = collectRule.collect(allCollateSegKeys);
        if (collateSegKeys.isEmpty()) {
            return;
        }
        if (!database.existsTable(tableKey)) {
            throw new TableNotExistException(tableKey);
        }
        Table table = database.getTable(tableKey);
        SwiftResultSet swiftResultSet = newCollateResultSet(getSegmentsByKeys(collateSegKeys));
        Collater collater = new HistoryCollater(table, alloter);
        collater.collate(swiftResultSet);
        List<SegmentKey> newSegmentKeys = collater.getNewSegments();
        List<Segment> newSegs = SegmentUtils.newSegments(newSegmentKeys);

        // todo 暂时同步做索引
        SegmentUtils.indexSegmentIfNeed(newSegs);

        fireUploadHistory(newSegmentKeys);

        clearCollatedSegment(collateSegKeys, tableKey);
    }

    private SwiftResultSet newCollateResultSet(List<Segment> segs) {
        List<SwiftResultSet> resultSets = new ArrayList<SwiftResultSet>();
        for (Segment seg : segs) {
            resultSets.add(new SegmentResultSet(seg));
        }
        return new CoSwiftResultSet(resultSets);
    }

    /**
     * check的segmentkeys的storeType
     *
     * @param segmentKeys
     * @param storeType
     */
    private void checkSegmentKeys(final List<SegmentKey> segmentKeys, Types.StoreType storeType) {
        Iterator<SegmentKey> iterator = segmentKeys.iterator();
        while (iterator.hasNext()) {
            SegmentKey segmentKey = iterator.next();
            if (segmentKey.getStoreType() != storeType) {
                iterator.remove();
            }
        }
    }

    private List<Segment> getSegmentsByKeys(List<SegmentKey> segmentKeys) {
        List<Segment> segments = new ArrayList<Segment>();
        for (SegmentKey segmentKey : segmentKeys) {
            segments.add(segmentManager.getSegment(segmentKey));
        }
        return segments;
    }

    private static void fireUploadHistory(List<SegmentKey> newKeys) {
        SwiftSegmentManager manager = SwiftContext.get().getBean("localSegmentProvider", SwiftSegmentManager.class);
        for (SegmentKey newSegKey : newKeys) {
            manager.getSegment(newSegKey);
            // TODO: 2019/1/24 先改成同步fire，避免fr rpc timeout
            SwiftEventDispatcher.syncFire(SegmentEvent.UPLOAD_HISTORY, newSegKey);
        }
    }

    private void clearCollatedSegment(final List<SegmentKey> collateSegKeys, final SourceKey tableKey) {
        CommonExecutor.get().execute(new Runnable() {
            @Override
            public void run() {
                for (SegmentKey collateSegKey : collateSegKeys) {
                    swiftSegmentService.removeSegments(Collections.singletonList(collateSegKey));
                    SegmentUtils.clearSegment(collateSegKey);
                    if (collateSegKey.getStoreType().isPersistent()) {
                        // TODO: 2019/1/24 先改成同步fire，避免fr rpc timeout
                        SwiftEventDispatcher.syncFire(SegmentEvent.REMOVE_HISTORY, collateSegKey);
                    }
                }
                //通知master删collate的块
                if (SwiftProperty.getProperty().isCluster()) {
                    try {
                        SwiftRpcEvent event = new HistoryRemoveEvent(collateSegKeys, tableKey, SwiftProperty.getProperty().getClusterId());
                        ProxySelector.getProxy(RemoteSender.class).trigger(event);
                    } catch (Exception e) {
                        SwiftLoggers.getLogger().error(e);
                    }
                }
            }
        });
        SwiftEventDispatcher.fire(SyncSegmentLocationEvent.REMOVE_SEG, collateSegKeys);
    }
}
