package com.fr.swift.service;

import com.fr.swift.config.service.SwiftMetaDataService;
import com.fr.swift.config.service.SwiftPathService;
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
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.task.service.ServiceTaskExecutor;
import com.fr.swift.task.service.ServiceTaskType;
import com.fr.swift.task.service.SwiftServiceCallable;
import com.fr.third.springframework.beans.factory.annotation.Autowired;
import com.fr.third.springframework.beans.factory.annotation.Qualifier;
import com.fr.third.springframework.stereotype.Service;

import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.sql.SQLException;
import java.util.List;
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

    private SwiftHistoryService() {
    }

    @Override
    @RpcMethod(methodName = "load")
    public void load(Set<URI> remoteUris) throws IOException {
        if (null != remoteUris && !remoteUris.isEmpty()) {
            String path = pathService.getSwiftPath();
            SwiftRepository repository = SwiftRepositoryManager.getManager().currentRepo();
            for (URI remote : remoteUris) {
                repository.copyFromRemote(remote, URI.create(path + remote.getPath()));
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
