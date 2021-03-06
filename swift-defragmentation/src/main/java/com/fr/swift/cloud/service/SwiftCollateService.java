package com.fr.swift.cloud.service;

import com.fr.swift.cloud.SwiftContext;
import com.fr.swift.cloud.annotation.SwiftService;
import com.fr.swift.cloud.beans.annotation.SwiftBean;
import com.fr.swift.cloud.config.entity.SwiftSegmentBucket;
import com.fr.swift.cloud.config.entity.SwiftTableAllotRule;
import com.fr.swift.cloud.config.service.SwiftSegmentLocationService;
import com.fr.swift.cloud.config.service.SwiftSegmentService;
import com.fr.swift.cloud.config.service.SwiftTableAllotRuleService;
import com.fr.swift.cloud.db.Database;
import com.fr.swift.cloud.db.Table;
import com.fr.swift.cloud.db.impl.SwiftDatabase;
import com.fr.swift.cloud.event.SwiftEventDispatcher;
import com.fr.swift.cloud.exception.SwiftServiceException;
import com.fr.swift.cloud.exception.TableNotExistException;
import com.fr.swift.cloud.log.SwiftLoggers;
import com.fr.swift.cloud.property.SwiftProperty;
import com.fr.swift.cloud.segment.SegmentKey;
import com.fr.swift.cloud.segment.SegmentService;
import com.fr.swift.cloud.segment.SegmentUtils;
import com.fr.swift.cloud.segment.collate.SwiftFragmentClassify;
import com.fr.swift.cloud.segment.collate.SwiftFragmentFilter;
import com.fr.swift.cloud.segment.event.SegmentEvent;
import com.fr.swift.cloud.segment.event.SyncSegmentLocationEvent;
import com.fr.swift.cloud.segment.operator.collate.segment.HisSegmentMerger;
import com.fr.swift.cloud.segment.operator.collate.segment.HisSegmentMergerImpl;
import com.fr.swift.cloud.segment.operator.collate.segment.SegmentPartition;
import com.fr.swift.cloud.source.SourceKey;
import com.fr.swift.cloud.source.alloter.SwiftSourceAlloter;
import com.fr.swift.cloud.source.alloter.impl.BaseAllotRule;
import com.fr.swift.cloud.source.alloter.impl.hash.HashAllotRule;
import com.fr.swift.cloud.source.alloter.impl.hash.HistoryHashSourceAlloter;
import com.fr.swift.cloud.source.alloter.impl.line.HistoryLineSourceAlloter;
import com.fr.swift.cloud.source.alloter.impl.line.LineAllotRule;
import com.fr.swift.cloud.util.concurrent.CommonExecutor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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


    private transient SegmentService segmentService;

    private transient Database database;

    private transient SwiftSegmentService swiftSegmentService;

    private transient SwiftTableAllotRuleService allotRuleService;

    private transient SwiftSegmentLocationService locationService;


    private SwiftCollateService() {
    }

    @Override
    public boolean start() throws SwiftServiceException {
        super.start();
        segmentService = SwiftContext.get().getBean(SegmentService.class);
        database = SwiftDatabase.getInstance();
        swiftSegmentService = SwiftContext.get().getBean(SwiftSegmentService.class);
        allotRuleService = SwiftContext.get().getBean(SwiftTableAllotRuleService.class);
        locationService = SwiftContext.get().getBean(SwiftSegmentLocationService.class);
        return true;
    }

    @Override
    public boolean shutdown() throws SwiftServiceException {
        super.shutdown();
        segmentService = null;
        database = null;
        swiftSegmentService = null;
        allotRuleService = null;
        locationService = null;
        return true;
    }

    @Override
    public List<SegmentKey> appointCollate(final SourceKey tableKey, final List<SegmentKey> segmentKeysList) throws Exception {
        SwiftTableAllotRule allotRule = allotRuleService.getByTale(tableKey);
        SwiftSegmentBucket segmentBucket = segmentService.getBucketByTable(tableKey);
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
        return collateSegments(tableKey, segmentKeysList, alloter, segmentBucket);
    }

    @Override
    public ServiceType getServiceType() {
        return ServiceType.COLLATE;
    }

    private List<SegmentKey> collateSegments(SourceKey tableKey, final List<SegmentKey> allCollateSegKeys, SwiftSourceAlloter alloter, SwiftSegmentBucket segmentBucket) throws Exception {
        SwiftLoggers.getLogger().info("Prepare collate task! Original segs: {}! ", allCollateSegKeys.toString());
        if (!database.existsTable(tableKey)) {
            throw new TableNotExistException(tableKey);
        }
        Table table = database.getTable(tableKey);
        List<SegmentKey> filterFragments = new SwiftFragmentFilter(alloter).filter(allCollateSegKeys);
        SwiftLoggers.getLogger().info("Start collate task! Collate segs: {}! ", filterFragments.toString());
        //DEC-7562  check collate的segs配置是否还存在,任何一个不存在直接退出。
        List<SegmentKey> allSegmentKeyList = segmentService.getSegmentKeys(tableKey);
        if (!allSegmentKeyList.containsAll(allCollateSegKeys)) {
            return Collections.EMPTY_LIST;
        }
        Map<Integer, List<SegmentPartition>> classifiedFragments = new SwiftFragmentClassify(segmentBucket, alloter.getAllotRule()).classify(filterFragments);
        List<SegmentKey> resultSegs = new ArrayList<>();
        for (Map.Entry<Integer, List<SegmentPartition>> itemEntry : classifiedFragments.entrySet()) {
            HisSegmentMerger merger = new HisSegmentMergerImpl();
            List<SegmentKey> collateSegKeys = itemEntry.getValue().stream().flatMap(s -> s.getSegmentKeys().stream()).collect(Collectors.toList());
            if (collateSegKeys.isEmpty()) {
                SwiftLoggers.getLogger().info("Termate collate! Table :{},bucket:{},collated segs empty", tableKey.getId(), itemEntry.getKey());
                continue;
            }
            List<SegmentKey> newSegmentKeys = merger.merge(table, itemEntry.getValue(), itemEntry.getKey());
            if (newSegmentKeys.isEmpty()) {
                SwiftLoggers.getLogger().info("Failed collate! Table :{},bucket:{},collated segs:{}", tableKey.getId(), itemEntry.getKey(), collateSegKeys);
                continue;
            }
            locationService.saveOnNode(SwiftProperty.get().getMachineId(), new HashSet<>(newSegmentKeys));
            segmentService.addSegments(newSegmentKeys);
            swiftSegmentService.update(newSegmentKeys);
            try {
                clearCollatedSegment(collateSegKeys);
            } catch (Throwable e) {
                checkAndRollback(collateSegKeys, newSegmentKeys);
                throw e;
            }
            resultSegs.addAll(newSegmentKeys);
            SwiftLoggers.getLogger().info("Finish collate ! Table :{},bucket:{},collated segs:{} ,new segs: {}", tableKey.getId(), itemEntry.getKey(), collateSegKeys, newSegmentKeys);
        }
        return resultSegs;
    }

    private void clearCollatedSegment(final List<SegmentKey> collateSegKeys) {
        swiftSegmentService.delete(collateSegKeys);
        segmentService.removeSegments(collateSegKeys);

        CommonExecutor.get().execute(() -> {
            for (SegmentKey collateSegKey : collateSegKeys) {
                SegmentUtils.clearSegment(collateSegKey);
                if (collateSegKey.getStoreType().isPersistent()) {
                    // TODO: 2019/1/24 先改成同步fire，避免fr rpc timeout
                    SwiftEventDispatcher.syncFire(SegmentEvent.REMOVE_HISTORY, collateSegKey);
                }
            }
        });
        SwiftEventDispatcher.asyncFire(SyncSegmentLocationEvent.REMOVE_SEG, collateSegKeys);
    }

    private void checkAndRollback(List<SegmentKey> originalSegKeys, List<SegmentKey> newSegmentKeys) {
        SwiftLoggers.getLogger().error("Collate failed! Check original segs : {}", originalSegKeys);

        // 文件是否都存在
        boolean fileExists = originalSegKeys.stream().allMatch(SegmentUtils::existSegment);

        // 配置是否都存在
        Set<SegmentKey> existIds = swiftSegmentService.getByIds(originalSegKeys.stream().map(SegmentKey::getId).collect(Collectors.toSet()));
        boolean configExists = existIds.size() == originalSegKeys.size();
        if (fileExists && configExists) {
            // 文件和配置都在, 删除新块
            rollback(newSegmentKeys);
        } else if (fileExists) {
            // 文件存在 + 配置不存在, 配置大概率无法删除, 对已经删除的新增回去, 后删除新块, 仅对segments表
            // 暂时设置 innodb_rollback_on_timeout =ON 保证3张表删除的原子性, 应该也不会出现
            originalSegKeys.removeIf(existIds::contains);
            SwiftLoggers.getLogger().info("Segs config lost : {}", originalSegKeys);
            swiftSegmentService.save(originalSegKeys);
            rollback(newSegmentKeys);
        }
    }

    private void rollback(List<SegmentKey> newSegmentKeys) {
        //如果是因为锁或等待进入rollback, 新块操作不会有这方面问题
        SwiftLoggers.getLogger().error("Collate failed! Clear new segs : {}", newSegmentKeys);
        swiftSegmentService.delete(newSegmentKeys);
        segmentService.removeSegments(newSegmentKeys);
        newSegmentKeys.forEach(SegmentUtils::clearSegment);
        SwiftLoggers.getLogger().info("Clear success!");
    }
}
