package com.fr.swift.base.json;

import com.fr.swift.base.json.annotation.JsonSubTypes;
import com.fr.swift.base.json.annotation.JsonTypeInfo;

/**
 * @author yee
 * @date 2018-12-04
 */
@JsonTypeInfo(
        property = "type",
        defaultImpl = TestA.class
)
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



