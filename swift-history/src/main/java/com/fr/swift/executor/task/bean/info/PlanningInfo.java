package com.fr.swift.executor.task.bean.info;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * @author Heng.J
 * @date 2020/10/29
 * @description
 * @since swift-1.2.0
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
@JsonSubTypes({
        @JsonSubTypes.Type(value = MigrateInfo.class),
})
public interface PlanningInfo {
}
