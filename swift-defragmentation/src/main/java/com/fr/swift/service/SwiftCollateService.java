package com.fr.swift.service;

import com.fr.swift.annotation.SwiftService;
import com.fr.swift.config.service.SwiftSegmentService;
import com.fr.swift.config.service.impl.SwiftSegmentServiceProvider;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.cube.CubeUtil;
import com.fr.swift.cube.io.Types;
import com.fr.swift.cube.io.Types.StoreType;
import com.fr.swift.cube.io.location.ResourceLocation;
import com.fr.swift.db.Database;
import com.fr.swift.db.Table;
import com.fr.swift.db.impl.SwiftDatabase;
import com.fr.swift.event.SwiftEventDispatcher;
import com.fr.swift.exception.SwiftServiceException;
import com.fr.swift.exception.TableNotExistException;
import com.fr.swift.repository.SwiftRepositoryManager;
import com.fr.swift.segment.HistorySegmentImpl;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SegmentResultSet;
import com.fr.swift.segment.SegmentUtils;
import com.fr.swift.segment.SwiftSegmentManager;
import com.fr.swift.segment.collate.CollateResultSet;
import com.fr.swift.segment.collate.FragmentCollectRule;
import com.fr.swift.segment.collate.SwiftFragmentCollectRule;
import com.fr.swift.segment.event.SegmentEvent;
import com.fr.swift.segment.operator.collate.HistoryCollater;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.source.alloter.SwiftSourceAlloter;
import com.fr.swift.source.alloter.impl.line.LineAllotRule;
import com.fr.swift.source.alloter.impl.line.LineSourceAlloter;
import com.fr.swift.source.resultset.CoSwiftResultSet;
import com.fr.swift.task.service.ServiceTaskExecutor;
import com.fr.swift.task.service.ServiceTaskType;
import com.fr.swift.task.service.SwiftServiceCallable;
import com.fr.swift.util.MonitorUtil;
import com.fr.swift.util.concurrent.CommonExecutor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * This class created on 2018/7/9
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
@SwiftService(name = "collate")
public class SwiftCollateService extends AbstractSwiftService implements CollateService {

    private static final long serialVersionUID = 7259915342007294244L;

    private transient SwiftSegmentManager segmentManager;

    private transient Database database;

    private transient ServiceTaskExecutor taskExecutor;

    private transient SwiftSegmentService swiftSegmentService;
    private transient SwiftRepositoryManager repositoryManager;


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
        swiftSegmentService = SwiftContext.get().getBean(SwiftSegmentServiceProvider.class);
        repositoryManager = SwiftContext.get().getBean(SwiftRepositoryManager.class);
        return true;
    }

    @Override
    public boolean shutdown() throws SwiftServiceException {
        super.shutdown();
        segmentManager = null;
        database = null;
        taskExecutor = null;
        swiftSegmentService = null;
        repositoryManager = null;
        return true;
    }

    @Override
    public void autoCollateRealtime(final SourceKey tableKey) throws Exception {
        final List<SegmentKey> segmentKeys = segmentManager.getSegmentKeys(tableKey);
        checkSegmentKeys(segmentKeys, Types.StoreType.MEMORY);
        taskExecutor.submit(new SwiftServiceCallable(tableKey, ServiceTaskType.COLLATE) {
            @Override
            public void doJob() throws Exception {
                collateSegments(tableKey, segmentKeys);
            }
        });
    }

    @Override
    public void autoCollateHistory(final SourceKey tableKey) throws Exception {
        final List<SegmentKey> segmentKeys = segmentManager.getSegmentKeys(tableKey);
        checkSegmentKeys(segmentKeys, Types.StoreType.FINE_IO);
        taskExecutor.submit(new SwiftServiceCallable(tableKey, ServiceTaskType.COLLATE) {
            @Override
            public void doJob() throws Exception {
                collateSegments(tableKey, segmentKeys);
            }
        });
    }

    @Override
    public void appointCollate(final SourceKey tableKey, final List<SegmentKey> segmentKeyList) throws Exception {
        taskExecutor.submit(new SwiftServiceCallable(tableKey, ServiceTaskType.COLLATE) {
            @Override
            public void doJob() throws Exception {
                collateSegments(tableKey, segmentKeyList);
            }
        });
    }

    @Override
    public void autoCollate(final SourceKey tableKey) throws Exception {
        taskExecutor.submit(new SwiftServiceCallable(tableKey, ServiceTaskType.COLLATE) {
            @Override
            public void doJob() throws Exception {
                collateSegments(tableKey);
            }
        });
    }

    @Override
    public ServiceType getServiceType() {
        return ServiceType.COLLATE;
    }

    private transient FragmentCollectRule collectRule = new SwiftFragmentCollectRule();

    private void collateSegments(SourceKey tableKey) throws Exception {
        List<SegmentKey> segKeys = segmentManager.getSegmentKeys(tableKey);
        collateSegments(tableKey, collectRule.collect(segKeys));
    }

    private void collateSegments(SourceKey tableKey, final List<SegmentKey> collateSegKeys) throws Exception {
        MonitorUtil.start();
        if (collateSegKeys.isEmpty()) {
            return;
        }

        if (!database.existsTable(tableKey)) {
            throw new TableNotExistException(tableKey);
        }
        Table table = database.getTable(tableKey);

        SwiftSourceAlloter alloter = new LineSourceAlloter(table.getSourceKey());
        SwiftMetaData metadata = table.getMetadata();

        SwiftResultSet swiftResultSet = newCollateResultSet(getSegmentsByKeys(collateSegKeys),
                ((LineAllotRule) alloter.getAllotRule()).getStep(), table.getMetadata());

        List<Segment> newSegs = new ArrayList<Segment>();
        final List<SegmentKey> newKeys = new ArrayList<SegmentKey>();
        Segment newSeg;
        do {
            SegmentKey newSegKey = swiftSegmentService.tryAppendSegment(tableKey, StoreType.FINE_IO);
            newKeys.add(newSegKey);
            newSeg = newHistorySegment(newSegKey, metadata);
            new HistoryCollater(newSeg).collate(swiftResultSet);
            newSegs.add(newSeg);
            swiftResultSet.close();
        } while (alloter.isFull(newSeg));

        // todo 暂时同步做索引
        SegmentUtils.indexSegmentIfNeed(newSegs);

        fireUploadHistory(newKeys);

        clearCollatedSegment(collateSegKeys);
    }

    private SwiftResultSet newCollateResultSet(List<Segment> segs, int alloterCount, SwiftMetaData swiftMetaData) {
        List<SwiftResultSet> resultSets = new ArrayList<SwiftResultSet>();
        for (Segment seg : segs) {
            resultSets.add(new SegmentResultSet(seg));
        }
        return new CollateResultSet(new CoSwiftResultSet(resultSets), alloterCount, swiftMetaData);
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

    private Segment newHistorySegment(SegmentKey segKey, SwiftMetaData meta) {
        String segPath = CubeUtil.getSegPath(segKey);
        return new HistorySegmentImpl(new ResourceLocation(segPath, Types.StoreType.FINE_IO), meta);
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
            SwiftEventDispatcher.fire(SegmentEvent.UPLOAD_HISTORY, newSegKey);
        }
    }

    private void clearCollatedSegment(final List<SegmentKey> collateSegKeys) {
        CommonExecutor.get().execute(new Runnable() {
            @Override
            public void run() {
                for (SegmentKey collateSegKey : collateSegKeys) {
                    swiftSegmentService.removeSegments(Collections.singletonList(collateSegKey));
                    SegmentUtils.clearSegment(collateSegKey);
                    if (collateSegKey.getStoreType().isPersistent()) {
                        SwiftEventDispatcher.fire(SegmentEvent.REMOVE_HISTORY, collateSegKey);
                    }
                }
            }
        });
    }
}
