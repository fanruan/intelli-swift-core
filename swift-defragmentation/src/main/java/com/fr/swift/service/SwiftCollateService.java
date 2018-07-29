package com.fr.swift.service;

import com.fr.general.ComparatorUtils;
import com.fr.swift.annotation.RpcMethod;
import com.fr.swift.annotation.RpcService;
import com.fr.swift.annotation.RpcServiceType;
import com.fr.swift.config.bean.SegmentKeyBean;
import com.fr.swift.config.service.SwiftSegmentService;
import com.fr.swift.config.service.SwiftSegmentServiceProvider;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.cube.CubeUtil;
import com.fr.swift.cube.io.Types;
import com.fr.swift.cube.io.Types.StoreType;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.cube.io.location.ResourceLocation;
import com.fr.swift.db.Database;
import com.fr.swift.db.Table;
import com.fr.swift.db.impl.SwiftDatabase;
import com.fr.swift.exception.SegmentKeyException;
import com.fr.swift.exception.TableNotExistException;
import com.fr.swift.segment.HistorySegmentImpl;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SegmentResultSet;
import com.fr.swift.segment.SegmentUtils;
import com.fr.swift.segment.SwiftSegmentManager;
import com.fr.swift.segment.collate.FragmentCollectRule;
import com.fr.swift.segment.collate.SwiftFragmentCollectRule;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.merge.CoSwiftResultSet;
import com.fr.swift.segment.operator.Collater;
import com.fr.swift.segment.operator.collate.HistoryCollater;
import com.fr.swift.segment.operator.column.SwiftColumnDictMerger;
import com.fr.swift.segment.operator.column.SwiftColumnIndexer;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.source.alloter.SegmentInfo;
import com.fr.swift.source.alloter.SwiftSourceAlloter;
import com.fr.swift.source.alloter.impl.line.LineRowInfo;
import com.fr.swift.source.alloter.impl.line.LineSourceAlloter;
import com.fr.swift.task.service.ServiceTaskExecutor;
import com.fr.swift.task.service.ServiceTaskType;
import com.fr.swift.task.service.SwiftServiceCallable;
import com.fr.swift.util.FileUtil;
import com.fr.swift.util.concurrent.CommonExecutor;
import com.fr.third.springframework.beans.factory.annotation.Autowired;
import com.fr.third.springframework.stereotype.Service;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * This class created on 2018/7/9
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
@Service("collateService")
@RpcService(value = CollateService.class, type = RpcServiceType.CLIENT_SERVICE)
public class SwiftCollateService extends AbstractSwiftService implements CollateService {

    private transient SwiftSegmentManager segmentManager = (SwiftSegmentManager) SwiftContext.get().getBean("localSegmentProvider");

    private transient Database database = SwiftDatabase.getInstance();

    @Autowired
    private transient ServiceTaskExecutor taskExecutor;

    public void setTaskExecutor(ServiceTaskExecutor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }

    private transient ServiceTaskType serviceTaskType = ServiceTaskType.COLLATE;

    @Override
    @RpcMethod(methodName = "autoCollateRealtime")
    public void autoCollateRealtime(final SourceKey tableKey) throws Exception {
        taskExecutor.submit(new SwiftServiceCallable(tableKey, serviceTaskType) {
            @Override
            public void doJob() throws Exception {
                collateSegments(tableKey, Types.StoreType.MEMORY);
            }
        });
    }

    @Override
    @RpcMethod(methodName = "autoCollateHistory")
    public void autoCollateHistory(final SourceKey tableKey) throws Exception {
        taskExecutor.submit(new SwiftServiceCallable(tableKey, serviceTaskType) {
            @Override
            public void doJob() throws Exception {
                collateSegments(tableKey, Types.StoreType.FINE_IO);
            }
        });
    }

    @Override
    @RpcMethod(methodName = "appointCollateRealtime")
    public void appointCollateRealtime(final List<SegmentKey> segmentKeyList) throws Exception {
        final SourceKey tableKey = checkSegmentKeys(segmentKeyList, Types.StoreType.MEMORY);
        taskExecutor.submit(new SwiftServiceCallable(tableKey, serviceTaskType) {
            @Override
            public void doJob() throws Exception {
                collateSegments(tableKey, Types.StoreType.MEMORY, segmentKeyList);
            }
        });
    }

    @Override
    @RpcMethod(methodName = "appointCollateHistory")
    public void appointCollateHistory(final List<SegmentKey> segmentKeyList) throws Exception {
        final SourceKey tableKey = checkSegmentKeys(segmentKeyList, Types.StoreType.FINE_IO);
        taskExecutor.submit(new SwiftServiceCallable(tableKey, serviceTaskType) {
            @Override
            public void doJob() throws Exception {
                collateSegments(tableKey, Types.StoreType.FINE_IO, segmentKeyList);
            }
        });

    }

    @Override
    @RpcMethod(methodName = "persistRealtime")
    public void persistRealtime(SourceKey tableKey, Segment segment) {
    }

    @Override
    public ServiceType getServiceType() {
        return ServiceType.COLLATE;
    }

    private FragmentCollectRule collectRule = new SwiftFragmentCollectRule();

    private void collateSegments(SourceKey tableKey, Types.StoreType storeType) throws Exception {
        List<SegmentKey> segKeys = segmentManager.getSegmentKeys(tableKey);
        collateSegments(tableKey, storeType, collectRule.collect(segKeys));
    }

    private void collateSegments(SourceKey tableKey, Types.StoreType storeType, final List<SegmentKey> collateSegKeys) throws Exception {
        List<SegmentKey> segmentKeys = segmentManager.getSegmentKeys(tableKey);
        if (collateSegKeys.isEmpty()) {
            return;
        }
        if (storeType != StoreType.MEMORY && collateSegKeys.size() < 2) {
            return;
        }
        if (!database.existsTable(tableKey)) {
            throw new TableNotExistException(tableKey);
        }
        Table table = database.getTable(tableKey);

        SwiftSourceAlloter alloter = new LineSourceAlloter(table.getSourceKey());
        SegmentKey maxSegmentKey = SegmentUtils.getMaxSegmentKey(segmentKeys);
        SwiftMetaData metadata = table.getMetadata();
        SwiftResultSet swiftResultSet = newSwiftResultSet(getSegmentsByKeys(collateSegKeys));

        List<SegmentKey> newSegKeys = new ArrayList<SegmentKey>();
        List<Segment> newSegs = new ArrayList<Segment>();
        Segment newSeg;
        int newOrder = maxSegmentKey == null ? 0 : maxSegmentKey.getOrder() + 1;
        do {
            newSeg = newHistorySegment(table, alloter.allot(new LineRowInfo(0)), newOrder);
            Collater collater = new HistoryCollater(newSeg);
            collater.collate(swiftResultSet);

            IResourceLocation location = newSeg.getLocation();

            SegmentKey newSegKey = new SegmentKeyBean(tableKey.getId(), URI.create(tableKey.getId() + "/seg" + newOrder), newOrder, location.getStoreType(), newSeg.getMetaData().getSwiftSchema());
            newSegKeys.add(newSegKey);
            newSegs.add(newSeg);
            newOrder++;
            swiftResultSet.close();
        } while (alloter.isFull(newSeg));

        // todo 暂时同步做索引
        for (int i = 0; i < metadata.getColumnCount(); i++) {
            ((SwiftColumnIndexer) SwiftContext.get().getBean("columnIndexer", table, new ColumnKey(metadata.getColumnName(i + 1)), newSegs)).buildIndex();
            ((SwiftColumnDictMerger) SwiftContext.get().getBean("columnDictMerger", table, new ColumnKey(metadata.getColumnName(i + 1)), newSegs)).mergeDict();
        }

        persistSegment(tableKey, segmentKeys, collateSegKeys, newSegKeys);

        clearCollatedSegment(collateSegKeys);
    }

    private SwiftResultSet newSwiftResultSet(List<Segment> segs) {
        List<SwiftResultSet> resultSets = new ArrayList<SwiftResultSet>();
        for (Segment seg : segs) {
            resultSets.add(new SegmentResultSet(seg));
        }
        return new CoSwiftResultSet(resultSets);
    }

    /**
     * 验证指定合并的segmentkeys是否符合要求
     *
     * @param segmentKeys
     * @param storeType
     * @throws Exception
     */
    private SourceKey checkSegmentKeys(List<SegmentKey> segmentKeys, Types.StoreType storeType) throws Exception {
        if (segmentKeys == null || segmentKeys.isEmpty()) {
            throw new SegmentKeyException("segmentKeys is null or empty!");
        }
        SegmentKey firstSegmentKey = segmentKeys.get(0);
        for (SegmentKey segmentKey : segmentKeys) {
            if (!ComparatorUtils.equals(segmentKey.getTable(), firstSegmentKey.getTable())) {
                throw new SegmentKeyException("segmentKeys tableKey is not same!");
            }
            if (segmentKey.getStoreType() != storeType) {
                throw new SegmentKeyException("segmentKeys storeType si not same as " + storeType + "!");
            }
        }
        return firstSegmentKey.getTable();
    }

    private void persistSegment(SourceKey tableKey, List<SegmentKey> oldSegKeys, List<SegmentKey> collateSegKeys, List<SegmentKey> newSegKeys) {
        List<SegmentKey> persistSegKeys = new ArrayList<SegmentKey>();
        persistSegKeys.addAll(newSegKeys);
        persistSegKeys.addAll(oldSegKeys);
        persistSegKeys.removeAll(collateSegKeys);
        SwiftSegmentService segmentService = SwiftSegmentServiceProvider.getProvider();
        segmentService.removeSegments(collateSegKeys);
        segmentService.updateSegments(tableKey.getId(), persistSegKeys);
    }

    private Segment newHistorySegment(DataSource dataSource, SegmentInfo segInfo, int segCount) {
        String segPath = String.format("%s/seg%d", CubeUtil.getTablePath(dataSource), segCount + segInfo.getOrder());
        return new HistorySegmentImpl(new ResourceLocation(segPath, Types.StoreType.FINE_IO), dataSource.getMetadata());
    }

    private List<Segment> getSegmentsByKeys(List<SegmentKey> segmentKeys) {
        List<Segment> segments = new ArrayList<Segment>();
        for (SegmentKey segmentKey : segmentKeys) {
            segments.add(segmentManager.getSegment(segmentKey));
        }
        return segments;
    }

    private void clearCollatedSegment(final List<SegmentKey> collateSegKeys) {
        CommonExecutor.get().execute(new Runnable() {
            @Override
            public void run() {
                for (SegmentKey collateSegKey : collateSegKeys) {
                    FileUtil.delete(collateSegKey.getAbsoluteUri().getPath());
                }
            }
        });
    }

    private HistorySegmentPutter historySegmentPutter = new HistorySegmentPutter();
}
