package com.fr.swift.jdbc.rpc.serializable.clazz;

/**
 * @author yee
 * @date 2019-01-11
 */
public class ClassLoaderClassResolver implements ClassResolver {

    private ClassLoader classLoader;

    public ClassLoaderClassResolver(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public Class<?> resolve(String className) throws ClassNotFoundException {
        try {
            return classLoader.loadClass(className);
        } catch (ClassNotFoundException e) {
            return Class.forName(className, false, classLoader);
        }
    }
}
