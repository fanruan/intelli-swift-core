package com.fr.swift.cloud.groovy;

import groovy.lang.GroovyObject;
import groovy.util.GroovyScriptEngine;
import groovy.util.ResourceException;
import groovy.util.ScriptException;

import java.io.File;
import java.io.IOException;

/**
 * @Author: lucifer
 * @Description:
 * @Date: Created in 2020/9/1
 */
public class GroovyFactory {

    private final static GroovyFactory INSTANCE = new GroovyFactory();

    private GroovyFactory() {
    }

    public static GroovyFactory get() {
        return INSTANCE;
    }

    public GroovyObject parseGroovyObject(File file) throws IllegalAccessException, InstantiationException, IOException, ResourceException, ScriptException {
        Class script = new GroovyScriptEngine(file.getParent()).loadScriptByName(file.getName());
        GroovyObject object = (GroovyObject) script.newInstance();
        return object;
    }
}
