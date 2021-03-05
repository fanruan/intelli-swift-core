package com.fr.swift.cloud.source.alloter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fr.swift.cloud.source.alloter.impl.hash.HashAllotRule;
import com.fr.swift.cloud.source.alloter.impl.line.LineAllotRule;

/**
 * @author anchore
 * @date 2018/6/5
 */
@JsonIgnoreProperties("type")
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, defaultImpl = LineAllotRule.class)
@JsonSubTypes({
        @JsonSubTypes.Type(value = LineAllotRule.class),
        @JsonSubTypes.Type(value = HashAllotRule.class),
})
public interface AllotRule {
    Type getType();

    int getCapacity();

    interface Type {
        String name();
    }

    String getCubePath(int order);
}