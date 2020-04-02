package com.fr.swift.service;

import com.fr.swift.SwiftContext;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.config.entity.SwiftTableAllotRule;
import com.fr.swift.config.service.SwiftConfigService;
import com.fr.swift.config.service.SwiftSegmentBucketService;
import com.fr.swift.config.service.SwiftSegmentLocationService;
import com.fr.swift.config.service.SwiftSegmentService;
import com.fr.swift.config.service.SwiftTableAllotRuleService;
import com.fr.swift.db.Database;
import com.fr.swift.db.Table;
import com.fr.swift.db.impl.SwiftDatabase;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.property.SwiftProperty;
import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SegmentResultSet;
import com.fr.swift.segment.SegmentUtils;
import com.fr.swift.segment.SwiftSegmentManager;
import com.fr.swift.segment.operator.Importer;
import com.fr.swift.service.executor.MigrationExecutor;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.alloter.impl.BaseAllotRule;
import com.fr.swift.source.alloter.impl.hash.HashAllotRule;
import com.fr.swift.source.alloter.impl.hash.HistoryHashSourceAlloter;
import com.fr.swift.source.alloter.impl.hash.function.DateAppIdHashFunction;
import com.fr.swift.source.resultset.progress.ProgressResultSet;
import com.fr.swift.util.concurrent.SwiftExecutors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * @author Heng.J
 * @date 2020/3/29
 * @description
 * @since swift 1.1
 */
@SwiftBean
public class SwiftMigrationExecutor implements MigrationExecutor {

    private static final String APPID = "appId";

    private static final String YEARMONTH = "yearMonth";

    public static final int CURRENT_DIR = 1;

    private SwiftSegmentService segmentService;

    private SwiftSegmentManager segmentManager;

    private SwiftTableAllotRuleService allotRuleService;

    private SwiftSegmentBucketService bucketService;

    private SwiftSegmentLocationService segLocationService;

    private SwiftConfigService configService;

    private Database database;


    public SwiftMigrationExecutor() {
    }

    @Override
    public void start() {
        if (SwiftProperty.getProperty().isMigration()) {
            segmentService = SwiftContext.get().getBean("segmentServiceProvider", SwiftSegmentService.class);
            segmentManager = SwiftContext.get().getBean("localSegmentProvider", SwiftSegmentManager.class);
            allotRuleService = SwiftContext.get().getBean(SwiftTableAllotRuleService.class);
            bucketService = SwiftContext.get().getBean(SwiftSegmentBucketService.class);
            segLocationService = SwiftContext.get().getBean(SwiftSegmentLocationService.class);
            configService = SwiftContext.get().getBean(SwiftConfigService.class);
            database = SwiftDatabase.getInstance();
            triggerMigration();
        }
    }

    @Override
    public void stop() {
        segmentService = null;
        segmentManager = null;
        allotRuleService = null;
        bucketService = null;
        segLocationService = null;
        database = null;
    }


    private void triggerMigration() {
        try {
            Set<String> tableNamesSet = SwiftProperty.getProperty().getMigrationTableSet();
            tableNamesSet.removeIf(String::isEmpty);
            Map<SourceKey, List<SegmentKey>> allSegmentsMap = segmentService.getOwnSegments();
            if (!tableNamesSet.isEmpty()) {
                allSegmentsMap.entrySet().removeIf((entry) -> !tableNamesSet.contains(entry.getKey().getId()));
            }

            for (Map.Entry<SourceKey, List<SegmentKey>> tableEntry : allSegmentsMap.entrySet()) {
                SourceKey tableKey = tableEntry.getKey();
                List<SegmentKey> keys = new ArrayList<>();

                tableEntry.getValue()
                        .stream()
                        .filter((key) -> (!key.getStoreType().isTransient()))
                        .forEach(keys::add);

                Map<SegmentKey, Integer> bucketIndexMap = bucketService.getBucketByTable(tableKey).getBucketIndexMap();
                Set<SegmentKey> hashedSegmentSet = bucketIndexMap.keySet();
                keys = keys.stream().filter(key -> !hashedSegmentSet.contains(key)).collect(Collectors.toList());

                if (!keys.isEmpty()) {
                    appointMigration(tableKey, keys);
                }
            }
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
        }
    }

    private void appointMigration(SourceKey tableKey, List<SegmentKey> segmentKeys) {
        String startYearMonth = "201901";

        Table table = database.getTable(tableKey);
        SwiftMetaData tableMetadata = table.getMetadata();
        List<String> fieldNames = tableMetadata.getFieldNames();

        HashAllotRule hashAllotRule = getOrInitAllotRule(tableKey, fieldNames);

        Map<SegmentKey, Set<String>> map = new ConcurrentHashMap<>();
        ExecutorService executorService = SwiftExecutors.newFixedThreadPool(6);
        List<Future> futureList = new ArrayList<>();
        for (SegmentKey segmentKey : segmentKeys) {
            Future f = executorService.submit(() -> {
                Set<String> yearMonths = new HashSet<>();
                SwiftResultSet resultSet = new ProgressResultSet(new SegmentResultSet(SegmentUtils.newSegment(segmentKey)), tableKey.getId());
                try {
                    Importer importer = new MigImporter(table, new HistoryHashSourceAlloter(tableKey, hashAllotRule), startYearMonth, yearMonths);
                    importer.importData(resultSet);
                    map.put(segmentKey, yearMonths);
                } catch (Exception e) {
                    SwiftLoggers.getLogger().error(e);
                }
            });
            futureList.add(f);
        }
        for (Future future : futureList) {
            try {
                future.get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Map<String, Set<SegmentKey>> segMap = new HashMap<>();
        for (Map.Entry<SegmentKey, Set<String>> entry : map.entrySet()) {
            for (String yearMonth : entry.getValue()) {
                segMap.computeIfAbsent(yearMonth, n -> new HashSet<>()).add(entry.getKey());
            }
        }
        for (Map.Entry<String, Set<SegmentKey>> entry : segMap.entrySet()) {
            for (SegmentKey segmentKey : entry.getValue()) {
                SwiftResultSet resultSet = new ProgressResultSet(new SegmentResultSet(SegmentUtils.newSegment(segmentKey)), tableKey.getId());
                try {
                    Importer importer = new MigImporter(table, new HistoryHashSourceAlloter(tableKey, hashAllotRule), entry.getKey(), null);
                    importer.importData(resultSet);
                } catch (Exception e) {
                    SwiftLoggers.getLogger().error(e);
                }
            }
        }
        return;


//        List<List<SegmentKey>> lists = new ArrayList<>();
//        List<SegmentKey> list = null;
//        for (SegmentKey segmentKey : segmentKeys) {
//            if (list == null || list.size() >= 10) {
//                list = new ArrayList<>();
//                lists.add(list);
//            }
//            list.add(segmentKey);
//        }
//        for (List<SegmentKey> segmentKeyList : lists) {
//            Segment segment = new ReadonlyMultiSegment(segmentKeyList.stream().map(SegmentUtils::newSegment).collect(Collectors.toList()));
//            SwiftResultSet resultSet = new ProgressResultSet(new SegmentResultSet(segment), tableKey.getId());
//            try {
//                Importer importer = new MigImporter(table, new HistoryHashSourceAlloter(tableKey, hashAllotRule), ym);
//                importer.importData(resultSet);
//            } catch (Exception e) {
//                SwiftLoggers.getLogger().error(e);
//            }
//        }
    }

    private HashAllotRule getOrInitAllotRule(SourceKey tableKey, List<String> fieldNames) {
        HashAllotRule hashAllotRule;
        SwiftTableAllotRule allotRule = allotRuleService.getAllotRuleByTable(tableKey);
        if (allotRule == null || allotRule.getAllotRule().getType() != BaseAllotRule.AllotType.HASH) {
            hashAllotRule = new HashAllotRule(
                    new int[]{fieldNames.indexOf(YEARMONTH), fieldNames.indexOf(APPID)},
                    new DateAppIdHashFunction(0));
            allotRuleService.saveAllotRule(new SwiftTableAllotRule(tableKey.getId(), hashAllotRule.getType().name(), hashAllotRule));
        } else {
            hashAllotRule = (HashAllotRule) allotRule.getAllotRule();
        }
        return hashAllotRule;
    }
}
