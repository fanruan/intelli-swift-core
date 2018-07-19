package com.fr.swift.service;

import com.fr.swift.config.entity.SwiftTablePathEntity;
import com.fr.swift.config.service.SwiftMetaDataService;
import com.fr.swift.config.service.SwiftPathService;
import com.fr.swift.config.service.SwiftTablePathService;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.db.Where;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.query.info.bean.query.QueryInfoBean;
import com.fr.swift.query.info.bean.query.QueryInfoBeanFactory;
import com.fr.swift.query.session.factory.SessionFactory;
import com.fr.swift.repository.SwiftRepository;
import com.fr.swift.repository.SwiftRepositoryManager;
import com.fr.swift.rpc.annotation.RpcMethod;
import com.fr.swift.rpc.annotation.RpcService;
import com.fr.swift.rpc.annotation.RpcServiceType;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SwiftSegmentManager;
import com.fr.swift.segment.operator.delete.WhereDeleter;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.task.service.ServiceTaskExecutor;
import com.fr.swift.task.service.ServiceTaskType;
import com.fr.swift.task.service.SwiftServiceCallable;
import com.fr.swift.util.FileUtil;
import com.fr.third.springframework.beans.factory.annotation.Autowired;
import com.fr.third.springframework.beans.factory.annotation.Qualifier;
import com.fr.third.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author pony
 * @date 2017/10/10
 */
@Service("historyService")
@RpcService(value = HistoryService.class, type = RpcServiceType.CLIENT_SERVICE)
public class SwiftHistoryService extends AbstractSwiftService implements HistoryService, Serializable {
    private static final long serialVersionUID = -6013675740141588108L;

    @Autowired
    @Qualifier("localSegmentProvider")
    private transient SwiftSegmentManager segmentManager;

    @Autowired
    private transient ServiceTaskExecutor taskExecutor;

    @Autowired
    private transient SwiftPathService pathService;

    @Autowired
    private transient SwiftMetaDataService metaDataService;
    @Autowired
    private transient SwiftTablePathService tablePathService;

    private SwiftHistoryService() {
    }

    @Override
    @RpcMethod(methodName = "load")
    public void load(Map<String, Set<URI>> remoteUris, boolean replace) throws IOException {
        String path = pathService.getSwiftPath();
        SwiftRepository repository = SwiftRepositoryManager.getManager().currentRepo();
        if (null != remoteUris && !remoteUris.isEmpty()) {
            for (String sourceKey : remoteUris.keySet()) {
                Set<URI> sets = remoteUris.get(sourceKey);
                if (!sets.isEmpty()) {
                    SwiftMetaData metaData = metaDataService.getMetaDataByKey(sourceKey);
                    int tmp = 0;
                    SwiftTablePathEntity entity = tablePathService.get(sourceKey);
                    if (null == entity) {
                        entity = new SwiftTablePathEntity(sourceKey, tmp);
                        tablePathService.saveOrUpdate(entity);
                        replace = true;
                    } else {
                        tmp = entity.getTablePath();
                        if (replace) {
                            tmp += 1;
                            entity.setTmpDir(tmp);
                            tablePathService.saveOrUpdate(entity);
                        }
                    }
                    for (URI uri : sets) {
                        String cubePath = String.format("%s/%s/%d/%s", path, metaData.getSwiftSchema().getDir(), tmp, uri.getPath());
                        String remotePath = String.format("%s/%s", metaData.getSwiftSchema().getDir(), uri.getPath());
                        repository.copyFromRemote(URI.create(remotePath), URI.create(cubePath));
                    }
                    if (replace) {
                        entity = tablePathService.get(sourceKey);
                        int current = entity.getTablePath();
                        entity.setLastPath(current);
                        entity.setTablePath(tmp);
                        tablePathService.saveOrUpdate(entity);
                        String cubePath = String.format("%s/%s/%d/%s", path, metaData.getSwiftSchema().getDir(), tmp, sourceKey);
                        FileUtil.delete(cubePath);
                        new File(cubePath).getParentFile().delete();
                    }
                }
            }
        } else {
            SwiftLoggers.getLogger().warn("Receive an empty URI set. Skip loading.");
        }
    }

    @Override
    @RpcMethod(methodName = "cleanMetaCache")
    public void cleanMetaCache(String[] sourceKeys) {
        SwiftContext.get().getBean(SwiftMetaDataService.class).cleanCache(sourceKeys);
    }


    @Override
    public ServiceType getServiceType() {
        return ServiceType.HISTORY;
    }

    @Override
    @RpcMethod(methodName = "historyQuery")
    public SwiftResultSet query(final String queryDescription) throws Exception {
        try {
            final QueryInfoBean bean = QueryInfoBeanFactory.create(queryDescription);
            SessionFactory factory = SwiftContext.get().getBean(SessionFactory.class);
            return factory.openSession(bean.getQueryId()).executeQuery(bean);
        } catch (IOException e) {
            throw new SQLException(e);
        }
    }

    @Override
    @RpcMethod(methodName = "historyDelete")
    public boolean delete(final SourceKey sourceKey, final Where where) throws Exception {
        taskExecutor.submit(new SwiftServiceCallable(sourceKey, ServiceTaskType.DELETE) {
            @Override
            public void doJob() throws Exception {
                List<Segment> segments = segmentManager.getSegment(sourceKey);
                for (Segment segment : segments) {
                    WhereDeleter whereDeleter = (WhereDeleter) SwiftContext.get().getBean("decrementer", sourceKey, segment);
                    whereDeleter.delete(where);
                }
            }
        });
        return true;
    }
}
