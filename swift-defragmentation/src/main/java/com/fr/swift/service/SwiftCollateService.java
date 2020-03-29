package com.fr.swift.service;

import com.fr.swift.SwiftContext;
import com.fr.swift.annotation.SwiftService;
import com.fr.swift.basics.base.selector.ProxySelector;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.config.entity.SwiftSegmentBucket;
import com.fr.swift.config.entity.SwiftTableAllotRule;
import com.fr.swift.config.service.SwiftSegmentBucketService;
import com.fr.swift.config.service.SwiftSegmentLocationService;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    private transient SwiftSegmentLocationService segLocationSvc;

    private transient SwiftSegmentManager segmentManager;

    private transient Database database;

    private transient SwiftSegmentService swiftSegmentService;

    private transient SwiftSegmentBucketService bucketService;

    private transient SwiftTableAllotRuleService allotRuleService;

    private SwiftCollateService() {
    }

    @Override
    public boolean start() throws SwiftServiceException {
        super.start();
        segmentManager = SwiftContext.get().getBean("localSegmentProvider", SwiftSegmentManager.class);
        database = SwiftDatabase.getInstance();
        swiftSegmentService = SwiftContext.get().getBean("segmentServiceProvider", SwiftSegmentService.class);
        segLocationSvc = SwiftContext.get().getBean(SwiftSegmentLocationService.class);
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
        segLocationSvc = null;
        bucketService = null;
        allotRuleService = null;
        return true;
    }

    @Override
    public void appointCollate(final SourceKey tableKey, final List<SegmentKey> segmentKeysList) throws Exception {
        Set<SegmentKey> segmentKeysSet = new HashSet<>(segmentKeysList);

        SwiftSegmentBucketService bucketService = SwiftContext.get().getBean(SwiftSegmentBucketService.class);
        Map<SegmentKey, Integer> bucketIndexMap = bucketService.getBucketByTable(tableKey).getBucketIndexMap();

        Map<Integer, List<SegmentKey>> segKeysByBucketMap = new HashMap<>();
        bucketIndexMap.entrySet()
                .stream()
                .filter((entry) -> segmentKeysSet.contains(entry.getKey()))
                .forEach((entry) -> segKeysByBucketMap.computeIfAbsent(entry.getValue(), k -> new ArrayList<>()).add(entry.getKey()));

        Map<Integer, List<SegmentKey>> collateMap = new HashMap<>();
        segKeysByBucketMap.entrySet()
                .stream()
                .filter((entry) -> entry.getValue().size() >= SwiftFragmentFilter.FRAGMENT_NUMBER)
                .forEach((entry) -> collateMap.computeIfAbsent(entry.getKey(), k -> new ArrayList<>()).addAll(entry.getValue()));

        for (Map.Entry<Integer, List<SegmentKey>> bucketListEntry : collateMap.entrySet()) {
            try {
                //分批collate，一批 5 - 100 块
                List<SegmentKey> allSegmentKeyList = bucketListEntry.getValue();
                if (allSegmentKeyList.size() < SwiftFragmentFilter.MAX_FRAGMENT_NUMBER) {
                    collateSegments(tableKey, allSegmentKeyList);
                } else {
                    int batch = allSegmentKeyList.size() / SwiftFragmentFilter.MAX_FRAGMENT_NUMBER;
                    int count = 0;
                    for (; count < batch; count++) {
                        collateSegments(tableKey, allSegmentKeyList.subList(count * SwiftFragmentFilter.MAX_FRAGMENT_NUMBER, (count + 1) * SwiftFragmentFilter.MAX_FRAGMENT_NUMBER));
                    }
                    collateSegments(tableKey, allSegmentKeyList.subList(count * SwiftFragmentFilter.MAX_FRAGMENT_NUMBER, allSegmentKeyList.size()));
                }
            } catch (Exception e) {
                SwiftLoggers.getLogger().error(e);
            }
        }
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
        SwiftLoggers.getLogger().info("Prepare collate task! Original segs: {}! ", allCollateSegKeys.toString());
        if (!database.existsTable(tableKey)) {
            throw new TableNotExistException(tableKey);
        }
        Table table = database.getTable(tableKey);
        List<SegmentKey> filterFragments = new SwiftFragmentFilter(alloter).filter(allCollateSegKeys);
        SwiftLoggers.getLogger().info("Start collate task! Collate segs: {}! ", filterFragments.toString());

        //DEC-7562  check collate的segs配置是否还存在,任何一个不存在直接退出。
        List<SegmentKey> allSegmentKeyList = swiftSegmentService.getSegmentByKey(tableKey.getId());
        if (!allSegmentKeyList.containsAll(allCollateSegKeys)) {
            return;
        }
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
                SwiftLoggers.getLogger().info("Failed collate! Collate segs: {}", collateSegKeys);
                // 合并失败
                continue;
            }
            fireUploadHistory(newSegmentKeys);
            clearCollatedSegment(collateSegKeys, tableKey);
            SwiftLoggers.getLogger().info("Finish collate ! Collated segs: {} ; New segs: {}", collateSegKeys.toString(), newSegmentKeys);
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
        SwiftEventDispatcher.fire(SyncSegmentLocationEvent.PUSH_SEG, newKeys);
    }

    private void clearCollatedSegment(final List<SegmentKey> collateSegKeys, final SourceKey tableKey) {
        segLocationSvc.delete(new HashSet<>(collateSegKeys));
        swiftSegmentService.removeSegments(collateSegKeys);
        CommonExecutor.get().execute(new Runnable() {
            @Override
            public void run() {
                for (SegmentKey collateSegKey : collateSegKeys) {
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
