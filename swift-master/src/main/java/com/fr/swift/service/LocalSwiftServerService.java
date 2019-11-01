package com.fr.swift.service;

import com.fr.swift.basic.URL;
import com.fr.swift.db.Where;
import com.fr.swift.event.base.AbstractGlobalRpcEvent;
import com.fr.swift.event.base.SwiftRpcEvent;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.source.SourceKey;
import com.fr.swift.structure.Pair;

import java.io.Serializable;
import java.util.Collections;
import java.util.Set;

/**
 * @author pony
 * @date 2017/11/14
 * 本地swift服务
 */
public class LocalSwiftServerService extends AbstractSwiftServerService {

    private static final long serialVersionUID = 9167111609239393074L;

    private RealtimeService realTimeService;

    private HistoryService historyService;

    private AnalyseService analyseService;

    private CollateService collateService;

    private DeleteService deleteService;

    private UploadService uploadService;

    @Override
    public Serializable trigger(SwiftRpcEvent event) {
        try {
            if (event.type() == SwiftRpcEvent.EventType.GLOBAL) {
                switch (((AbstractGlobalRpcEvent) event).subEvent()) {
                    case DELETE:
                        Pair<SourceKey, Where> content = (Pair<SourceKey, Where>) event.getContent();
                        deleteService.delete(content.getKey(), content.getValue());
                        return null;
                    default:
                }
            }
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
        }
        return null;
    }

    @Override
    public void registerService(SwiftService service) {
        synchronized (this) {
            switch (service.getServiceType()) {
                case ANALYSE:
                    analyseService = (AnalyseService) service;
                    break;
                case HISTORY:
                    historyService = (HistoryService) service;
                    break;
                case REAL_TIME:
                    realTimeService = (RealtimeService) service;
                    break;
                case COLLATE:
                    collateService = (CollateService) service;
                    break;
                case DELETE:
                    deleteService = (DeleteService) service;
                    break;
                case UPLOAD:
                    uploadService = (UploadService) service;
                    break;
                default:
                    throw new UnsupportedOperationException(String.format("service %s is not allowed to register", service));
            }
        }
    }

    @Override
    public void unRegisterService(SwiftService service) {
        synchronized (this) {
            switch (service.getServiceType()) {
                case ANALYSE:
                    analyseService = null;
                    break;
                case HISTORY:
                    historyService = null;
                    break;
                case REAL_TIME:
                    realTimeService = null;
                    break;
                case COLLATE:
                    collateService = null;
                    break;
                case DELETE:
                    deleteService = null;
                    break;
                case UPLOAD:
                    uploadService = null;
                    break;
                default:
                    SwiftLoggers.getLogger().warn("you have deregistered a unknown service {}", service);
            }
        }
    }

    @Override
    public Set<URL> getNodeUrls(Class<?> proxyIface) {
        return Collections.emptySet();
    }
}
