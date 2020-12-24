package com.fr.swift.executor.task.info;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fr.swift.service.info.TaskInfo;

/**
 * @author Heng.J
 * @date 2020/10/29
 * @description
 * @since swift-1.2.0
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
@JsonSubTypes({
        @JsonSubTypes.Type(value = MigScheduleInfo.class),
        @JsonSubTypes.Type(value = MigTriggerInfo.class),
        @JsonSubTypes.Type(value = ClearConflictInfo.class),
})
public interface PlanningInfo extends TaskInfo {
}
