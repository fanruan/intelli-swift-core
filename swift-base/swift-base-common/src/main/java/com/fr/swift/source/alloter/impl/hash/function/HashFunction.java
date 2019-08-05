package com.fr.swift.source.alloter.impl.hash.function;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * @author lucifer
 * @date 2019-06-24
 * @description
 * @since advanced swift 1.0
 */
@JsonIgnoreProperties("type")
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, defaultImpl = JdkHashFunction.class)
@JsonSubTypes({
        @JsonSubTypes.Type(value = JdkHashFunction.class),
        @JsonSubTypes.Type(value = TimeHashFunction.class),

})
public interface HashFunction {

    int indexOf(Object key);

    HashType getType();

}
