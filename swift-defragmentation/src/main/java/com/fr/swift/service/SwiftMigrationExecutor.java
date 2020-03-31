package com.fr.swift.service;

import com.fr.swift.SwiftContext;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.config.entity.SwiftSegmentBucketElement;
import com.fr.swift.config.entity.SwiftTableAllotRule;
import com.fr.swift.config.service.SwiftSegmentBucketService;
import com.fr.swift.config.service.SwiftSegmentLocationService;
import com.fr.swift.config.service.SwiftSegmentService;
import com.fr.swift.config.service.SwiftTableAllotRuleService;
import com.fr.swift.cube.CubePathBuilder;
import com.fr.swift.cube.io.Types;
import com.fr.swift.cube.io.location.ResourceLocation;
import com.fr.swift.db.Database;
import com.fr.swift.db.Table;
import com.fr.swift.db.impl.SwiftDatabase;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.property.SwiftProperty;
import com.fr.swift.segment.CacheColumnSegment;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SegmentService;
import com.fr.swift.segment.SegmentUtils;
import com.fr.swift.segment.column.BitmapIndexedColumn;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.segment.operator.collate.segment.SegmentBuilder;
import com.fr.swift.service.executor.MigrationExecutor;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.alloter.impl.BaseAllotRule;
import com.fr.swift.source.alloter.impl.hash.HashAllotRule;
import com.fr.swift.source.alloter.impl.hash.function.DateAppIdHashFunction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

    private SwiftSegmentService swiftSegmentService;

    private SegmentService segmentService;

    private SwiftTableAllotRuleService allotRuleService;

    private SwiftSegmentBucketService bucketService;

    private SwiftSegmentLocationService locationService;

    private Database database;


    public SwiftMigrationExecutor() {
    }

    @Override
    public void start() {
        if (SwiftProperty.getProperty().isMigration()) {
            swiftSegmentService = SwiftContext.get().getBean(SwiftSegmentService.class);
            segmentService = SwiftContext.get().getBean(SegmentService.class);
            allotRuleService = SwiftContext.get().getBean(SwiftTableAllotRuleService.class);
            bucketService = SwiftContext.get().getBean(SwiftSegmentBucketService.class);
            locationService = SwiftContext.get().getBean(SwiftSegmentLocationService.class);
            database = SwiftDatabase.getInstance();
            triggerMigration();
        }
    }

    @Override
    public void stop() {
        swiftSegmentService = null;
        segmentService = null;
        allotRuleService = null;
        bucketService = null;
        locationService = null;
        database = null;
    }


    private void triggerMigration() {
        try {
            Set<String> tableNamesSet = SwiftProperty.getProperty().getMigrationTableSet();
            tableNamesSet.removeIf(String::isEmpty);
            Map<SourceKey, List<SegmentKey>> allSegmentsMap = swiftSegmentService.getOwnSegments(SwiftProperty.getProperty().getMachineId());
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
        Table table = database.getTable(tableKey);
        SwiftMetaData tableMetadata = table.getMetadata();
        List<String> fieldNames = tableMetadata.getFieldNames();

        HashAllotRule hashAllotRule = getOrInitAllotRule(tableKey, fieldNames);

        if (!segmentKeys.isEmpty()) {
            for (SegmentKey segmentKey : segmentKeys) {
                Segment segment = segmentService.getSegment(segmentKey);
                Column appIdColumn = segment.getColumn(new ColumnKey(APPID));
                Column yearMonthColumn = segment.getColumn(new ColumnKey(YEARMONTH));
                BitmapIndexedColumn appIdBitMapIndex = appIdColumn.getBitmapIndex();
                BitmapIndexedColumn yearMonthBitmapIndex = yearMonthColumn.getBitmapIndex();
                DictionaryEncodedColumn appIdKeyIndex = appIdColumn.getDictionaryEncodedColumn();
                DictionaryEncodedColumn yearMonthKeyIndex = yearMonthColumn.getDictionaryEncodedColumn();

                boolean success = true;
                for (int i = 1; i < appIdKeyIndex.size(); i++) {
                    String appId = appIdKeyIndex.getValue(i).toString();
                    ImmutableBitMap appIdBitMap = appIdBitMapIndex.getBitMapIndex(i);
                    for (int j = 1; j < yearMonthKeyIndex.size(); j++) {
                        ImmutableBitMap yearMonthBitMap = yearMonthBitmapIndex.getBitMapIndex(j);
                        ImmutableBitMap unionBitMap = appIdBitMap.getAnd(yearMonthBitMap);

                        if (unionBitMap.getCardinality() > 0) {
                            String yearMonth = yearMonthKeyIndex.getValue(j).toString();
                            int bucketIndex = hashAllotRule.getHashFunction().indexOf(new ArrayList<Object>(2) {{
                                add(yearMonth);
                                add(appId);
                            }});

                            SegmentKey newSegmentKey = swiftSegmentService.tryAppendSegment(tableKey, Types.StoreType.FINE_IO);
                            String cubePath = new CubePathBuilder(newSegmentKey).setTempDir(CURRENT_DIR).build();
                            ResourceLocation location = new ResourceLocation(cubePath, newSegmentKey.getStoreType());
                            Segment newSegment = new CacheColumnSegment(location, tableMetadata);
                            try {
                                SegmentBuilder segmentBuilder = new SegmentBuilder(newSegment, fieldNames, Collections.singletonList(segment), Collections.singletonList(unionBitMap));
                                segmentBuilder.build();
                                SegmentUtils.releaseHisSeg(newSegment);
                                locationService.saveOnNode(SwiftProperty.getProperty().getMachineId(), (Collections.singleton(newSegmentKey)));
                                bucketService.save(new SwiftSegmentBucketElement(tableKey, bucketIndex, newSegmentKey.getId()));
                            } catch (Throwable e) {
                                try {
                                    SegmentUtils.releaseHisSeg(newSegment);
                                    SegmentUtils.clearSegment(newSegmentKey);
                                    swiftSegmentService.delete(Collections.singletonList(newSegmentKey));
                                } catch (Exception ignore) {
                                    SwiftLoggers.getLogger().error("ignore exception", ignore);
                                }
                                success = false;
                                SwiftLoggers.getLogger().error("build new segment failed", e);
                            }
                            SwiftLoggers.getLogger().info("migrate table: {} segment: {}  bitMap: {} to bucket {} success:",
                                    tableKey.toString(), segmentKey.getId(), unionBitMap.toString(), bucketIndex, success);
                        }
                    }
                }
                if (success) {
                    swiftSegmentService.delete(segmentKeys);
                }
            }
        }
    }

    private HashAllotRule getOrInitAllotRule(SourceKey tableKey, List<String> fieldNames) {
        HashAllotRule hashAllotRule;
        SwiftTableAllotRule allotRule = allotRuleService.getByTale(tableKey);
        if (allotRule == null || allotRule.getAllotRule().getType() != BaseAllotRule.AllotType.HASH) {
            hashAllotRule = new HashAllotRule(
                    new int[]{fieldNames.indexOf(YEARMONTH), fieldNames.indexOf(APPID)},
                    new DateAppIdHashFunction(0));
            allotRuleService.save(new SwiftTableAllotRule(tableKey.getId(), hashAllotRule.getType().name(), hashAllotRule));
        } else {
            hashAllotRule = (HashAllotRule) allotRule.getAllotRule();
        }
        return hashAllotRule;
    }
}
