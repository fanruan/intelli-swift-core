package com.fr.swift.config.service.impl;

import com.fr.general.ComparatorUtils;
import com.fr.swift.config.SwiftCubePathConfig;
import com.fr.swift.config.service.SwiftPathService;
import com.fr.third.springframework.stereotype.Service;

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
        try {
            String oldPath = swiftCubePathConfig.getPath();
            if (ComparatorUtils.equals(oldPath, path)) {
                return false;
            }
            swiftCubePathConfig.setPath(path);
            for (PathChangeListener listener : listeners) {
                listener.changed(path);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String getSwiftPath() {
        return this.swiftCubePathConfig.getPath();
    }

    @Override
    public void registerPathChangeListener(PathChangeListener listener) {
        listeners.add(listener);
    }
}
