//package com.fr.swift.cloud.groovy;
//
//import com.fr.swift.config.ConfigInputUtil;
//import com.fr.swift.util.exception.LambdaWrapper;
//import groovy.lang.Binding;
//import groovy.lang.GroovyClassLoader;
//import groovy.lang.GroovyObject;
//import groovy.lang.Script;
//import groovy.util.GroovyScriptEngine;
//import groovy.util.ResourceException;
//import groovy.util.ScriptException;
//import org.codehaus.groovy.runtime.InvokerHelper;
//
//import java.io.File;
//import java.io.IOException;
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * @Author: lucifer
// * @Description:
// * @Date: Created in 2020/9/1
// */
//public class GroovyScriptFactory {
//    private static Map<String, Class<Script>> SCRIPT_CACHE = new HashMap<>();
//    private GroovyClassLoader classLoader = new GroovyClassLoader();
//    private static GroovyScriptFactory FACTORY = new GroovyScriptFactory();
//
//    /**
//     * 设置为单例模式
//     */
//    private GroovyScriptFactory() {
//    }
//
//    public static GroovyScriptFactory get() {
//        return FACTORY;
//    }
//
//    private Class getScript(String key) throws IOException {
//        String encodeStr = key;
//        return SCRIPT_CACHE.computeIfAbsent(encodeStr, LambdaWrapper.rethrowFunction(k -> classLoader.parseClass(new File(key))));
//    }
//
//    private Object run(Class<Script> script, Binding binding) {
//        Script scriptObj = InvokerHelper.createScript(script, binding);
//        Object result = scriptObj.run();
//        // 每次脚本执行完之后，一定要清理掉内存
//        classLoader.clearCache();
//        return result;
//    }
//
//    public Object scriptGetAndRun(String key, Binding binding) throws IOException {
//        return run(getScript(key), binding);
//    }
//
//    public static void main(String[] args) throws IOException, IllegalAccessException, InstantiationException, ResourceException, ScriptException {
//        String path = "groovy/Test1.java";
//        String configPath = ConfigInputUtil.getConfigPath(path);
////
//        Class script = new GroovyScriptEngine("D:\\swift\\target\\classes\\groovy").loadScriptByName("Test1.java");
//        GroovyObject object = (GroovyObject) script.newInstance();
//        object.invokeMethod("print", null);
//
////        Class script = get().getScript(configPath);
////        GroovyObject object = (GroovyObject) script.newInstance();
////        object.invokeMethod("print", null);
//        return;
//    }
//}