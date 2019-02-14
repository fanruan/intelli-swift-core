package com.fr.swift.service.handler.global;

import com.fr.swift.ClusterNodeService;
import com.fr.swift.SwiftContext;
import com.fr.swift.basic.URL;
import com.fr.swift.basics.ProxyFactory;
import com.fr.swift.basics.base.selector.ProxySelector;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.beans.exception.SwiftBeanException;
import com.fr.swift.cluster.ClusterEntity;
import com.fr.swift.cluster.service.ClusterSwiftServerService;
import com.fr.swift.config.bean.SwiftServiceInfoBean;
import com.fr.swift.config.service.SwiftClusterSegmentService;
import com.fr.swift.config.service.SwiftServiceInfoService;
import com.fr.swift.db.Where;
import com.fr.swift.event.ClusterEvent;
import com.fr.swift.event.ClusterEventType;
import com.fr.swift.event.ClusterListenerHandler;
import com.fr.swift.event.ClusterType;
import com.fr.swift.event.SwiftEventDispatcher;
import com.fr.swift.event.analyse.SegmentLocationRpcEvent;
import com.fr.swift.event.base.AbstractGlobalRpcEvent;
import com.fr.swift.event.global.RemoveSegLocationRpcEvent;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.SegmentDestination;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SegmentLocationInfo;
import com.fr.swift.selector.ClusterSelector;
import com.fr.swift.service.AnalyseService;
import com.fr.swift.service.BaseService;
import com.fr.swift.service.DeleteService;
import com.fr.swift.service.HistoryService;
import com.fr.swift.service.RealtimeService;
import com.fr.swift.service.ServiceType;
import com.fr.swift.service.UploadService;
import com.fr.swift.service.handler.EventHandlerExecutor;
import com.fr.swift.service.handler.SwiftServiceHandlerManager;
import com.fr.swift.service.handler.base.AbstractHandler;
import com.fr.swift.source.SourceKey;
import com.fr.swift.structure.Pair;
import com.fr.swift.task.TaskKey;
import com.fr.swift.task.TaskResult;
import com.fr.swift.task.impl.TaskEvent;
import com.fr.swift.util.Crasher;
import com.fr.swift.util.Util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * @author yee
 * @date 2018/6/27
 */
@SwiftBean
public class SwiftGlobalEventHandler extends AbstractHandler<AbstractGlobalRpcEvent> {

    private SwiftServiceInfoService serviceInfoService = SwiftContext.get().getBean(SwiftServiceInfoService.class);

    private SwiftClusterSegmentService segmentService = SwiftContext.get().getBean(SwiftClusterSegmentService.class);

    @Override
    public <S extends Serializable> S handle(AbstractGlobalRpcEvent event) throws Exception {
        final ProxyFactory factory = ProxySelector.getInstance().getFactory();
        // todo 用表驱动法，分离switch匹配，和具体的处理逻辑
        switch (event.subEvent()) {
            case CHECK_MASTER:
                List<SwiftServiceInfoBean> masterServiceInfoBeanList = serviceInfoService.getServiceInfoByService(ClusterNodeService.SERVICE);
                if (masterServiceInfoBeanList.isEmpty()) {
                    Crasher.crash("Master is null!");
                }
                SwiftServiceInfoBean masterBean = masterServiceInfoBeanList.get(0);
                if (!Util.equals(ClusterSelector.getInstance().getFactory().getMasterId(), masterBean.getClusterId())) {
                    SwiftLoggers.getLogger().info("Master is not synchronized!");
                    try {
                        ClusterNodeService clusterNodeService = SwiftContext.get().getBean(ClusterNodeService.class);
                        clusterNodeService.competeMaster();
                    } catch (SwiftBeanException e) {
                        SwiftLoggers.getLogger().error(e);
                    }
                    ClusterListenerHandler.handlerEvent(new ClusterEvent(ClusterEventType.JOIN_CLUSTER, ClusterType.FR));
                }
                return (S) ClusterSelector.getInstance().getFactory().getCurrentId();
            case TASK_DONE:
                Pair<TaskKey, TaskResult> pair = (Pair<TaskKey, TaskResult>) event.getContent();
                SwiftEventDispatcher.fire(TaskEvent.DONE, pair);
                break;
            case CLEAN:
                String[] sourceKeys = (String[]) event.getContent();
                try {
                    if (null != sourceKeys) {
                        factory.getProxy(BaseService.class).cleanMetaCache(sourceKeys);
                    }
                } catch (Exception e) {
                    SwiftLoggers.getLogger().error(e);
                }
                break;
            case PUSH_SEG: {
                final SegmentLocationInfo info = (SegmentLocationInfo) event.getContent();
                EventHandlerExecutor.submit(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            SwiftServiceHandlerManager.getManager().
                                    handle(new SegmentLocationRpcEvent(SegmentLocationInfo.UpdateType.PART, info));
                        } catch (Exception e) {
                            SwiftLoggers.getLogger().error(e);
                        }
                    }
                });
                break;
            }
            case REMOVE_SEG: {
                RemoveSegLocationRpcEvent removeEvt = (RemoveSegLocationRpcEvent) event;
                String clusterId = removeEvt.getClusterId();
                SegmentLocationInfo info = (SegmentLocationInfo) event.getContent();
                AnalyseService analyseService = ProxySelector.getProxy(AnalyseService.class);

                for (Entry<SourceKey, List<SegmentDestination>> entry : info.getDestinations().entrySet()) {
                    ArrayList<String> segIds = new ArrayList<String>();
                    for (SegmentDestination segDst : entry.getValue()) {
                        segIds.add(segDst.getSegmentId());
                    }
                    analyseService.removeSegments(clusterId, entry.getKey(), segIds);
                }
                break;
            }
            case GET_ANALYSE_REAL_TIME:
                Map<String, ClusterEntity> realtime = ClusterSwiftServerService.getInstance().getClusterEntityByService(ServiceType.REAL_TIME);
                Map<String, ClusterEntity> analyse = ClusterSwiftServerService.getInstance().getClusterEntityByService(ServiceType.ANALYSE);
                Map<ServiceType, List<String>> result = new HashMap<ServiceType, List<String>>();
                makeResultMap(realtime, result, ServiceType.REAL_TIME);
                makeResultMap(analyse, result, ServiceType.ANALYSE);
                return (S) result;
            case DELETE:
                Pair<SourceKey, Where> content = (Pair<SourceKey, Where>) event.getContent();
                SourceKey tableKey = content.getKey();
                Where where = content.getValue();

                Map<URL, UploadService> uploadServices = factory.getPeerProxies(UploadService.class);

                Set<SegmentKey> uploadedSegKeys = new HashSet<SegmentKey>();

                for (Entry<URL, DeleteService> entry : factory.getPeerProxies(DeleteService.class).entrySet()) {
                    try {
                        if (entry.getValue().delete(tableKey, where)) {
                            // delete 提交成功
                            Map<SourceKey, List<SegmentKey>> ownSegKeys = segmentService.getOwnSegments(entry.getKey().getDestination().getId());
                            if (!ownSegKeys.containsKey(tableKey)) {
                                break;
                            }

                            Set<SegmentKey> segKeys = new HashSet<SegmentKey>(ownSegKeys.get(tableKey));
                            // 去除transient的seg
                            for (Iterator<SegmentKey> itr = segKeys.iterator(); itr.hasNext(); ) {
                                if (itr.next().getStoreType().isTransient()) {
                                    itr.remove();
                                }
                            }

                            // 去重
                            segKeys.removeAll(uploadedSegKeys);
                            uploadedSegKeys.addAll(segKeys);

                            if (!segKeys.isEmpty()) {
                                // 提交上传任务
                                uploadServices.get(entry.getKey()).uploadAllShow(segKeys);
                            }

                        }
                    } catch (Exception e) {
                        SwiftLoggers.getLogger().error(e);
                    }
                }
                break;
            case TRUNCATE:
                SourceKey truncateContent = (SourceKey) event.getContent();
                try {
                    factory.getProxy(RealtimeService.class).truncate(truncateContent);
                } catch (Exception e) {
                    SwiftLoggers.getLogger().error(e);
                }
                try {
                    factory.getProxy(HistoryService.class).truncate(truncateContent);
                } catch (Exception e) {
                    SwiftLoggers.getLogger().error(e);
                }
                break;
            default:
                break;
        }
        return null;
    }

    private void makeResultMap(Map<String, ClusterEntity> realtime, Map<ServiceType, List<String>> result, ServiceType type) {
        for (String id : realtime.keySet()) {
            if (result.get(type) == null) {
                result.put(type, new ArrayList<String>());
            }
            result.get(type).add(id);
        }
    }

}
