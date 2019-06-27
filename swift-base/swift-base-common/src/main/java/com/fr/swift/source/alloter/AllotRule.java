package com.fr.swift.source.alloter;

import com.fr.swift.base.json.annotation.JsonIgnoreProperties;
import com.fr.swift.base.json.annotation.JsonSubTypes;
import com.fr.swift.base.json.annotation.JsonTypeInfo;
import com.fr.swift.source.alloter.impl.hash.HashAllotRule;
import com.fr.swift.source.alloter.impl.line.LineAllotRule;

/**
 * @author anchore
 * @date 2018/6/5
 */
@JsonIgnoreProperties("type")
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, defaultImpl = LineAllotRule.class)
@JsonSubTypes({
        @JsonSubTypes.Type(value = LineAllotRule.class),
        @JsonSubTypes.Type(value = HashAllotRule.class)

})
public interface AllotRule {
    Type getType();

    int getCapacity();

    interface Type {
        String name();
    }
}