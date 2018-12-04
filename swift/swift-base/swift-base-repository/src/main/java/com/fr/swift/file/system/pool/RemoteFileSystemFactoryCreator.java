package com.fr.swift.file.system.pool;

import com.fr.swift.SwiftContext;
import com.fr.swift.file.SwiftFileSystemType;
import com.fr.swift.file.system.annotation.FileSystemFactory;
import com.fr.swift.file.system.factory.SwiftFileSystemFactory;
import com.fr.swift.util.Assert;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yee
 * @date 2018/7/5
 */
public class RemoteFileSystemFactoryCreator {
    private static RemoteFileSystemFactoryCreator creator;
    private Map<String, SwiftFileSystemFactory> factoryMap = new ConcurrentHashMap<String, SwiftFileSystemFactory>();

    private RemoteFileSystemFactoryCreator() {
        init();
    }

    public static RemoteFileSystemFactoryCreator creator() {
        if (null == creator) {
            synchronized (RemoteFileSystemFactoryCreator.class) {
                if (null == creator) {
                    creator = new RemoteFileSystemFactoryCreator();
                }
            }
        }
        return creator;
    }

    private void init() {
        Map<String, Object> serviceBeanMap = SwiftContext.get().getBeansByAnnotations(FileSystemFactory.class);
        if (null != serviceBeanMap && !serviceBeanMap.isEmpty()) {
            for (Object factoryBean : serviceBeanMap.values()) {
                FileSystemFactory factory = factoryBean.getClass().getAnnotation(FileSystemFactory.class);
                factoryMap.put(factory.name(), (SwiftFileSystemFactory) factoryBean);
            }
        }
    }

    public SwiftFileSystemFactory getFactory(SwiftFileSystemType type) {
        SwiftFileSystemFactory factory = factoryMap.get(type.name());
        Assert.notNull(factory);
        return factory;
    }
}
