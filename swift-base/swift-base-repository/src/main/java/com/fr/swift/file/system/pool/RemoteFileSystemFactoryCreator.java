package com.fr.swift.file.system.pool;

import com.fr.swift.SwiftContext;
import com.fr.swift.file.system.annotation.PackageConnectorFactory;
import com.fr.swift.file.system.factory.SwiftFileSystemFactory;
import com.fr.swift.repository.exception.RepoNotFoundException;

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
        Map<String, Object> serviceBeanMap = SwiftContext.get().getBeansByAnnotations(PackageConnectorFactory.class);
        if (null != serviceBeanMap && !serviceBeanMap.isEmpty()) {
            for (Object factoryBean : serviceBeanMap.values()) {
                PackageConnectorFactory factory = factoryBean.getClass().getAnnotation(PackageConnectorFactory.class);
                factoryMap.put(factory.name(), (SwiftFileSystemFactory) factoryBean);
            }
        }
    }

    public SwiftFileSystemFactory getFactory(String type) {
        SwiftFileSystemFactory factory = factoryMap.get(type);
        if (null == factory) {
            throw new RepoNotFoundException(String.format("Repository which named '%s' is not found", type));
        }
        return factory;
    }
}
