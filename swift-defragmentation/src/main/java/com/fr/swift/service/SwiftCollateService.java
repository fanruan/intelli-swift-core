package com.fr.swift.service;

import com.fr.swift.SwiftContext;
import com.fr.swift.annotation.SwiftService;
import com.fr.swift.basics.base.selector.ProxySelector;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.config.entity.SwiftSegmentBucket;
import com.fr.swift.config.entity.SwiftTableAllotRule;
import com.fr.swift.config.service.SwiftSegmentBucketService;
import com.fr.swift.config.service.SwiftSegmentService;
import com.fr.swift.config.service.SwiftTableAllotRuleService;
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
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SegmentUtils;
import com.fr.swift.segment.SwiftSegmentManager;
import com.fr.swift.segment.collate.SwiftFragmentClassify;
import com.fr.swift.segment.collate.SwiftFragmentFilter;
import com.fr.swift.segment.event.SegmentEvent;
import com.fr.swift.segment.event.SyncSegmentLocationEvent;
import com.fr.swift.segment.operator.collate.segment.HisSegmentMerger;
import com.fr.swift.segment.operator.collate.segment.HisSegmentMergerImpl;
import com.fr.swift.service.listener.RemoteSender;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.alloter.SwiftSourceAlloter;
import com.fr.swift.source.alloter.impl.BaseAllotRule;
import com.fr.swift.source.alloter.impl.hash.HashAllotRule;
import com.fr.swift.source.alloter.impl.hash.HistoryHashSourceAlloter;
import com.fr.swift.source.alloter.impl.line.HistoryLineSourceAlloter;
import com.fr.swift.source.alloter.impl.line.LineAllotRule;
import com.fr.swift.util.concurrent.CommonExecutor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * This class created on 2018/7/9
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
@SwiftService(name = "collate")
@SwiftBean(name = "collate")
public class SwiftCollateService extends AbstractSwiftService implements CollateService {

    private static final long serialVersionUID = 7259915342007294244L;

    private transient SwiftSegmentManager segmentManager;

    private transient Database database;

    private transient SwiftSegmentService swiftSegmentService;

    private SwiftSegmentBucketService bucketService;

    private SwiftTableAllotRuleService allotRuleService;

    private SwiftCollateService() {
    }

    @Override
    public boolean start() throws SwiftServiceException {
        super.start();
        segmentManager = SwiftContext.get().getBean("localSegmentProvider", SwiftSegmentManager.class);
        database = SwiftDatabase.getInstance();
        swiftSegmentService = SwiftContext.get().getBean("segmentServiceProvider", SwiftSegmentService.class);
        bucketService = SwiftContext.get().getBean(SwiftSegmentBucketService.class);
        allotRuleService = SwiftContext.get().getBean(SwiftTableAllotRuleService.class);
        return true;
    }

    @Override
    public boolean shutdown() throws SwiftServiceException {
        super.shutdown();
        segmentManager = null;
        database = null;
        swiftSegmentService = null;
        bucketService = null;
        allotRuleService = null;
        return true;
    }

    @Override
    public void appointCollate(final SourceKey tableKey, final List<SegmentKey> segmentKeyList) throws Exception {
        collateSegments(tableKey, segmentKeyList);
    }

    @Override
    public ServiceType getServiceType() {
        return ServiceType.COLLATE;
    }

    private void collateSegments(SourceKey tableKey, final List<SegmentKey> collateSegKeys) throws Exception {
        SwiftTableAllotRule allotRule = allotRuleService.getAllotRuleByTable(tableKey);
        SwiftSegmentBucket segmentBucket = bucketService.getBucketByTable(tableKey);
        SwiftSourceAlloter alloter;
        if (allotRule == null) {
            alloter = new HistoryLineSourceAlloter(tableKey, new LineAllotRule());
        } else {
            if (allotRule.getAllotRule().getType() == BaseAllotRule.AllotType.HASH) {
                alloter = new HistoryHashSourceAlloter(tableKey, (HashAllotRule) allotRule.getAllotRule());
            } else {
                alloter = new HistoryLineSourceAlloter(tableKey, (LineAllotRule) allotRule.getAllotRule());
            }
        }
        collateSegments(tableKey, collateSegKeys, alloter, segmentBucket);
    }

    private void collateSegments(SourceKey tableKey, final List<SegmentKey> allCollateSegKeys, SwiftSourceAlloter alloter, SwiftSegmentBucket segmentBucket) throws Exception {
        if (!database.existsTable(tableKey)) {
            throw new TableNotExistException(tableKey);
        }
        Table table = database.getTable(tableKey);

        List<SegmentKey> filterFragments = new SwiftFragmentFilter(alloter).filter(allCollateSegKeys);
        Map<Integer, List<SegmentKey>> classifiedFragments = new SwiftFragmentClassify(segmentBucket).classify(filterFragments);

        for (Map.Entry<Integer, List<SegmentKey>> classifiedFragmentEntry : classifiedFragments.entrySet()) {
            List<SegmentKey> collateSegKeys = classifiedFragmentEntry.getValue();
            HisSegmentMerger merger = new HisSegmentMergerImpl();
            List<Segment> segments = getSegmentsByKeys(collateSegKeys);
            if (!collateSegKeys.isEmpty() && segments.isEmpty()) {
                // 要合并的块全部标记删除了
                clearCollatedSegment(collateSegKeys, tableKey);
                continue;
            }
            List<SegmentKey> newSegmentKeys = merger.merge(table, segments, alloter, classifiedFragmentEntry.getKey());
            if (newSegmentKeys.isEmpty()) {
                // 合并失败
                continue;
            }
            fireUploadHistory(newSegmentKeys);
            clearCollatedSegment(collateSegKeys, tableKey);
        }
    }

    private List<Segment> getSegmentsByKeys(List<SegmentKey> segmentKeys) {
        List<Segment> segments = new ArrayList<Segment>();
        for (SegmentKey segmentKey : segmentKeys) {
            Segment segment = segmentManager.getSegment(segmentKey);
            if (segment.getAllShowIndex().getCardinality() > 0) {
                segments.add(segment);
            }
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
