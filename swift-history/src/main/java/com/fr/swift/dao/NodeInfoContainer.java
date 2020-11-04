package com.fr.swift.dao;

import com.fr.swift.SwiftContext;
import com.fr.swift.config.entity.SwiftNodeInfo;
import com.fr.swift.config.service.SwiftNodeInfoService;
import com.fr.swift.db.NodeType;
import com.fr.swift.executor.task.bean.MigrateBean;
import com.fr.swift.executor.task.bean.info.MigrateInfo;
import com.fr.swift.executor.task.bean.info.PlanningInfo;

import java.time.Period;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Heng.J
 * @date 2020/10/27
 * @description 仅主节点从数据库读取和维持缓存
 * @since swift-1.2.0
 */
public enum NodeInfoContainer implements NodeInfoService {

    MASTER;

    private SwiftNodeInfoService nodeInfoService = SwiftContext.get().getBean(SwiftNodeInfoService.class);

    private DateTimeFormatter yearMonthFormatter = DateTimeFormatter.ofPattern("yyyyMM");
    // 节点id(swift.properties) : 节点
    private Map<String, SwiftNodeInfo> nodeIdMap = new HashMap<>();
    // 结点类型 : 节点id
    private Map<NodeType, Set<String>> nodeTypeMap = new HashMap<>();
    // 节点id : 时间信息
    private Map<String, MonthInfo> nodeMonthInfoMap = new HashMap<>();
    // 月份   : 节点id
    private Map<String, Set<String>> monthNodeMap = new HashMap<>();
    // 节点id : 迁移信息
    private Map<String, List<PlanningInfo>> nodeMigrateInfoMap = new HashMap<>();
    // 可执行任务节点
    private Set<String> taskAvailableSet = new HashSet<>();

    NodeInfoContainer() {
        List<SwiftNodeInfo> nodeInfoList = nodeInfoService.getAllNodeInfo();
        for (SwiftNodeInfo nodeInfo : nodeInfoList) {
            String nodeId = nodeInfo.getNodeId();
            nodeIdMap.put(nodeId, nodeInfo);
            nodeTypeMap.computeIfAbsent(nodeInfo.getNodeType(), k -> new HashSet<>()).add(nodeInfo.getNodeId());
            nodeMonthInfoMap.put(nodeId, new MonthInfo(nodeInfo.getBeginMonth(), nodeInfo.getEndMonth(), nodeInfo.getMonthNum()));
            nodeMigrateInfoMap.put(nodeId, initMigrateInfoList(nodeInfo));
            if (nodeInfo.getMigrateType().isAcceptable()) {
                taskAvailableSet.add(nodeId);
            }
        }
        initMonthNodeMap();
    }

    @Override
    public Set<String> getTaskTargets(String yearMonth) {
        return monthNodeMap.get(yearMonth);
    }

    @Override
    public SwiftNodeInfo getNodeInfo(String clusterId) {
        return nodeIdMap.get(clusterId);
    }

    @Override
    public Set<String> getMigrateNodeIds() {
        return getNodeInfos(NodeType.TASK);
    }

    @Override
    public Set<String> getNodeInfos(NodeType nodeType) {
        return nodeTypeMap.getOrDefault(nodeType, Collections.emptySet());
    }

    @Override
    public List<PlanningInfo> getMigrateInfos(String clusterId) {
        return nodeMigrateInfoMap.getOrDefault(clusterId, Collections.emptyList());
    }

    @Override
    public boolean isAcceptable(String clusterId) {
        return taskAvailableSet.contains(clusterId);
    }

    @Override
    public void clearCache() {
        nodeIdMap.clear();
        nodeTypeMap.clear();
        nodeMonthInfoMap.clear();
        monthNodeMap.clear();
        nodeMonthInfoMap.clear();
        taskAvailableSet.clear();
    }

    private void initMonthNodeMap() {
        for (String nodeId : nodeTypeMap.get(NodeType.TASK)) {
            MonthInfo monthInfo = nodeMonthInfoMap.get(nodeId);
            YearMonth temp = monthInfo.getBegin();
            YearMonth end = monthInfo.getEnd();
            while (temp.compareTo(end) <= 0) {
                monthNodeMap.computeIfAbsent(temp.format(yearMonthFormatter), k -> new HashSet<>()).add(nodeId);
                temp = temp.plus(Period.ofMonths(1));
            }
        }
    }

    private List<PlanningInfo> initMigrateInfoList(SwiftNodeInfo nodeInfo) {
        List<PlanningInfo> result = new ArrayList<>();
        if (nodeInfo.getNodeType().isMigrated() && nodeInfo.getMigrateType().needMigrated()) {
            MonthInfo monthInfo = nodeMonthInfoMap.get(nodeInfo.getNodeId());
            YearMonth temp = monthInfo.getEnd().minus(Period.ofMonths(monthInfo.getMonthNum()));
            YearMonth begin = monthInfo.getBegin();
            while (temp.compareTo(begin) >= 0) {
                result.add(new MigrateInfo(nodeInfo.getMigrateTime(), MigrateBean.of(begin.format(yearMonthFormatter), nodeInfo.getMigrateTarget())));
                begin = begin.plus(Period.ofMonths(1));
            }
        }
        return result;
    }

    private class MonthInfo {
        private YearMonth begin;
        private YearMonth end;
        private int monthNum;

        public MonthInfo(String beginMonth, String endMonth, int monthNum) {
            this.begin = YearMonth.parse(beginMonth, yearMonthFormatter);
            this.end = YearMonth.parse(endMonth, yearMonthFormatter);
            this.monthNum = monthNum;
        }

        public YearMonth getBegin() {
            return begin;
        }

        public YearMonth getEnd() {
            return end;
        }

        public int getMonthNum() {
            return monthNum;
        }
    }

}
