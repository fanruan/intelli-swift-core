package com.fr.swift.file.system.pool;

import com.fr.swift.context.SwiftContext;
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
public class RemoteFactoryCreator {
    private static RemoteFactoryCreator creator;
    private Map<String, SwiftFileSystemFactory> factoryMap = new ConcurrentHashMap<String, SwiftFileSystemFactory>();

    private RemoteFactoryCreator() {
        init();
    }

    public static RemoteFactoryCreator creator() {
        if (null == creator) {
            synchronized (RemoteFactoryCreator.class) {
                if (null == creator) {
                    creator = new RemoteFactoryCreator();
                }
            }
        }
        return creator;
    }

    private void init() {
        Map<String, Object> serviceBeanMap = SwiftContext.get().getBeansWithAnnotation(FileSystemFactory.class);
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
