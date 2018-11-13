package com.fr.swift.boot;

import com.fr.swift.boot.upgrade.SegmentRecoveryAspect;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.generate.ColumnDictMerger;
import com.fr.swift.generate.ColumnIndexer;
import com.fr.swift.manager.IndexingSegmentManager;
import com.fr.swift.manager.LocalSegmentProvider;
import com.fr.swift.netty.rpc.registry.impl.SimpleServiceDiscovery;
import com.fr.swift.netty.rpc.registry.impl.SimpleServiceRegistry;
import com.fr.swift.netty.rpc.server.RpcServer;
import com.fr.swift.property.SwiftProperty;
import com.fr.swift.query.info.bean.query.QueryInfoBeanFactory;
import com.fr.swift.query.session.factory.SessionFactoryImpl;
import com.fr.swift.redis.RedisClient;
import com.fr.swift.relation.column.RelationColumnImpl;
import com.fr.swift.repository.manager.SwiftRepositoryManager;
import com.fr.swift.segment.Decrementer;
import com.fr.swift.segment.HistorySegmentImpl;
import com.fr.swift.segment.Incrementer;
import com.fr.swift.segment.RealTimeSegmentImpl;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.backup.FileAllshowIndexBackup;
import com.fr.swift.segment.backup.FileSegmentBackup;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.insert.HistoryBlockInserter;
import com.fr.swift.segment.operator.insert.SwiftInserter;
import com.fr.swift.segment.recover.FileSegmentRecovery;
import com.fr.swift.segment.rule.DefaultSegmentDestSelectRule;
import com.fr.swift.service.QueryIndexService;
import com.fr.swift.service.handler.history.rule.DefaultDataSyncRule;
import com.fr.swift.service.handler.indexing.rule.DefaultIndexingSelectRule;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.alloter.SwiftSourceAlloter;
import com.fr.swift.task.service.SwiftServiceTaskExecutor;
import com.fr.swift.transaction.FileTransactionManager;
import com.fr.third.springframework.context.annotation.Bean;
import com.fr.third.springframework.context.annotation.ComponentScan;
import com.fr.third.springframework.context.annotation.Configuration;
import com.fr.third.springframework.context.annotation.EnableAspectJAutoProxy;
import com.fr.third.springframework.context.annotation.Import;
import com.fr.third.springframework.context.annotation.PropertySource;
import com.fr.third.springframework.context.annotation.Scope;
import com.fr.third.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import java.util.List;

/**
 * @author anchore
 * @date 2018/10/17
 */
@Configuration
@EnableAspectJAutoProxy
@PropertySource(value = {
        "classpath:swift.properties",
        "classpath:swift-beans.properties"
})
@ComponentScan({
        // RPC和HTTP服务
        "com.fr.swift.netty.rpc.service",
        "com.fr.swift.http.service",
        // 各种Service
        "com.fr.swift.service.listener",
        "com.fr.swift.service",
        "com.fr.swift.cluster.service",
        "com.fr.swift.config",
        "com.fr.swift.property",
        // RM NM节点
        "com.fr.swift.rm",
        "com.fr.swift.nm",
        // 共享存储
        "com.fr.swift.file",
        "com.fr.swift.api"
})
@Import({
        LocalSegmentProvider.class,
        IndexingSegmentManager.class,
        SimpleServiceRegistry.class,
        SimpleServiceDiscovery.class,
        RpcServer.class,
        SessionFactoryImpl.class,
        RedisClient.class,
        QueryIndexService.class,
        QueryInfoBeanFactory.class,
        DefaultDataSyncRule.class,
        DefaultIndexingSelectRule.class,
        DefaultSegmentDestSelectRule.class,
        SwiftRepositoryManager.class,
        FileSegmentRecovery.class,
        SegmentRecoveryAspect.class
})
public class SwiftContextConfiguration {

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean(name = {"inserter"})
    @Scope("prototype")
    public SwiftInserter getSwiftInserter(Segment seg) {
        return new SwiftInserter(seg);
    }

    @Bean(name = {"historyBlockInserter"})
    @Scope("prototype")
    public HistoryBlockInserter getHistoryBlockInserter(DataSource dataSource) {
        return new HistoryBlockInserter(dataSource);
    }

    @Bean(name = {"incrementer"})
    @Scope("prototype")
    public Incrementer getIncrementer(DataSource dataSource, SwiftSourceAlloter alloter) {
        return new Incrementer(dataSource, alloter);
    }

    @Bean(name = {"decrementer"})
    @Scope("prototype")
    public Decrementer getDecrementer(SegmentKey segKey) {
        return new Decrementer(segKey);
    }

    @Bean(name = {"relationColumn"})
    @Scope("prototype")
    public RelationColumnImpl getRelationColumnImpl(ColumnKey columnKey) {
        return new RelationColumnImpl(columnKey);
    }

    @Bean(name = {"columnIndexer"})
    @Scope("prototype")
    public ColumnIndexer getColumnIndexer(DataSource dataSource, ColumnKey key, List<Segment> segments) {
        return new ColumnIndexer(dataSource, key, segments);
    }

    @Bean(name = {"columnIndexer"})
    @Scope("prototype")
    public ColumnIndexer getColumnIndexer(SwiftMetaData meta, ColumnKey key, List<Segment> segments) {
        return new ColumnIndexer(meta, key, segments);
    }

    @Bean(name = {"columnDictMerger"})
    @Scope("prototype")
    public ColumnDictMerger getColumnDictMerger(DataSource dataSource, ColumnKey key, List<Segment> segments) {
        return new ColumnDictMerger(dataSource, key, segments);
    }

    @Bean(name = {"columnDictMerger"})
    @Scope("prototype")
    public ColumnDictMerger getColumnDictMerger(SwiftMetaData meta, ColumnKey key, List<Segment> segments) {
        return new ColumnDictMerger(meta, key, segments);
    }

    @Bean(name = {"realtimeSegment"})
    @Scope("prototype")
    public RealTimeSegmentImpl getRealTimeSegmentImpl(IResourceLocation parent, SwiftMetaData meta) {
        return new RealTimeSegmentImpl(parent, meta);
    }

    @Bean(name = {"historySegment"})
    @Scope("prototype")
    public HistorySegmentImpl getHistorySegmentImpl(IResourceLocation parent, SwiftMetaData meta) {
        return new HistorySegmentImpl(parent, meta);
    }

    @Bean(name = {"serviceTaskExecutor"})
    public SwiftServiceTaskExecutor getSwiftServiceTaskExecutor() {
        return new SwiftServiceTaskExecutor(10);
    }

    @Bean(name = {"segmentBackup"})
    @Scope("prototype")
    public FileSegmentBackup getFileSegmentBackup(Segment segment, Segment currentSegment, List<String> fields) {
        return new FileSegmentBackup(segment, currentSegment, fields);
    }

    @Bean(name = {"transactionManager"})
    @Scope("prototype")
    public FileTransactionManager getFileTransactionManager(Segment hisSegment) {
        return new FileTransactionManager(hisSegment);
    }

    @Bean(name = {"allShowIndexBackup"})
    @Scope("prototype")
    public FileAllshowIndexBackup getFileAllshowIndexBackup(Segment segment) {
        return new FileAllshowIndexBackup(segment);
    }
}