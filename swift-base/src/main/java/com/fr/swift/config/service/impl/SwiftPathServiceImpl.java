package com.fr.swift.config.service.impl;

import com.fr.config.Configuration;
import com.fr.general.ComparatorUtils;
import com.fr.swift.config.SwiftCubePathConfig;
import com.fr.swift.config.service.SwiftPathService;
import com.fr.third.springframework.stereotype.Service;
import com.fr.transaction.Configurations;
import com.fr.transaction.Worker;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yee
 * @date 2018/6/6
 */
@Service("swiftPathService")
public class SwiftPathServiceImpl implements SwiftPathService {
    private SwiftCubePathConfig swiftCubePathConfig = SwiftCubePathConfig.getInstance();
    private List<PathChangeListener> listeners = new ArrayList<PathChangeListener>();

    @Override
    public boolean setSwiftPath(final String path) {
        return Configurations.update(new SwiftPathConfigWorker() {
            @Override
            public void run() {
                String oldPath = swiftCubePathConfig.getPath();
                if (ComparatorUtils.equals(oldPath, path)) {
                    return;
                }
                swiftCubePathConfig.setPath(path);
                for (PathChangeListener listener : listeners) {
                    listener.changed(path);
                }
            }
        });
    }

    @Override
    public String getSwiftPath() {
        return this.swiftCubePathConfig.getPath();
    }

    @Override
    public void registerPathChangeListener(PathChangeListener listener) {
        listeners.add(listener);
    }

    private abstract class SwiftPathConfigWorker implements Worker {
        @Override
        public Class<? extends Configuration>[] targets() {
            return new Class[]{SwiftCubePathConfig.class};
        }
    }
}
