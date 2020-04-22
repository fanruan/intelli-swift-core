package com.fr.swift.executor.task.rule;

import java.util.Map;
import java.util.Objects;

/**
 * @author Heng.J
 * @date 2020/4/22
 * @description 相同任务判断
 * @since swift 1.1
 */
public class TaskKey {

    private String appId;

    private String yearMonth;

    private String version;

    public TaskKey(Map<String, String> taskContentMap) {
        this.appId = taskContentMap.get("clientAppId");
        this.yearMonth = taskContentMap.get("yearMonth");
        this.version = taskContentMap.get("version");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaskKey taskKey = (TaskKey) o;
        return Objects.equals(appId, taskKey.appId) &&
                Objects.equals(yearMonth, taskKey.yearMonth) &&
                Objects.equals(version, taskKey.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(appId, yearMonth, version);
    }
}
