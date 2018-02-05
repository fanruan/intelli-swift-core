package com.fr.swift.source;

import com.fr.swift.source.alloter.LineSourceAlloter;

/**
 * Created by pony on 2017/11/22.
 */
public class SwiftSourceAlloterFactory {

    public static SwiftSourceAlloter createSourceAlloter(SourceKey sourceKey){
        return new LineSourceAlloter(sourceKey);
    }
}
