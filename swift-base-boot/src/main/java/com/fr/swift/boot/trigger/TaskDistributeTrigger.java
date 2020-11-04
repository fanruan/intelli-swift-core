package com.fr.swift.boot.trigger;

import com.fr.swift.SwiftContext;
import com.fr.swift.basics.base.selector.ProxySelector;
import com.fr.swift.dao.NodeInfoService;
import com.fr.swift.executor.task.bean.PlanningBean;
import com.fr.swift.executor.task.bean.info.PlanningInfo;
import com.fr.swift.executor.task.impl.PlanningExecutorTask;
import com.fr.swift.executor.type.SwiftTaskType;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.service.ServiceContext;
import com.fr.swift.trigger.SwiftPriorityInitTrigger;

import java.util.List;
import java.util.Set;

/**
 * @author Heng.J
 * @date 2020/10/23
 * @description
 * @since swift-1.2.0
 */
public class TaskDistributeTrigger implements SwiftPriorityInitTrigger {

    private NodeInfoService nodeInfoService = SwiftContext.get().getBean(NodeInfoService.class);

    /**
     * 启动或竞争成主节点
     * ——> 读取 SwiftNodeInfoEntity 配置
     * ——> 生成各类任务的计划任务 PlanningExecutorTask 放入任务队列
     * ——> 关闭接收继续添加新任务, 放到 SwiftBlockTaskEntity
     * ——> 等收到任何一个节点的信息, 更新SwiftNodeInfoEntity配置, 分发SwiftBlockTaskEntity相关任务
     */
    @Override
    public void init() {
        SwiftLoggers.getLogger().info("starting task distribute...");
        ServiceContext serviceContext = ProxySelector.getProxy(ServiceContext.class);
        Set<String> nodeIds = nodeInfoService.getMigrateNodeIds();
        for (String nodeId : nodeIds) {
            List<PlanningInfo> migrateInfos = nodeInfoService.getMigrateInfos(nodeId);
            if (migrateInfos.isEmpty()) {
                continue;
            }
            boolean success = false;
            try {
                for (PlanningInfo migrateInfo : migrateInfos) {
                    success = serviceContext.dispatch(new PlanningExecutorTask(new PlanningBean(migrateInfo, SwiftTaskType.MIGRATE)).toString(), nodeId);
                }
            } catch (Exception e) {
                SwiftLoggers.getLogger().error(e);
            }
            if (!success) {
                //H.J TODO : 2020/10/30 发送失败策略
            }
        }
    }

    @Override
    public void destroy() throws InterruptedException {
        SwiftLoggers.getLogger().info("stopping task distribute...");
        nodeInfoService.clearCache();
    }

    @Override
    public int priority() {
        return Priority.LOW.priority();
    }
}
