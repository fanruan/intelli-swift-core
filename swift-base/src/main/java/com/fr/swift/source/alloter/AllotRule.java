package com.fr.swift.source.alloter;

import com.fr.swift.source.alloter.impl.line.LineAllotRule;
import com.fr.third.fasterxml.jackson.annotation.JsonIgnore;
import com.fr.third.fasterxml.jackson.annotation.JsonSubTypes;
import com.fr.third.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fr.third.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fr.third.fasterxml.jackson.annotation.JsonTypeInfo.Id;

/**
 * @author anchore
 * @date 2018/6/5
 */
@JsonTypeInfo(use = Id.NAME)
@JsonSubTypes({
        @Type(value = LineAllotRule.class)
})
public interface AllotRule {
    @JsonIgnore
    Type getType();

    enum Type {
        //
        LINE, HASH
    }
}