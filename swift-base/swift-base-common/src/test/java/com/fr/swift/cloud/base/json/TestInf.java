package com.fr.swift.cloud.base.json;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * @author yee
 * @date 2018-12-04
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME, property = "type", defaultImpl = TestA.class)
@JsonSubTypes({
        @JsonSubTypes.Type(value = TestA.class, name = "A"),
        @JsonSubTypes.Type(value = TestB.class, name = "B"),
        @JsonSubTypes.Type(value = TestC.class, name = "C")
})
public interface TestInf {
    Type getType();

    enum Type {
        A, B, C
    }
}



