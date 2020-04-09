package com.fr.swift.config.service.impl;

import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.config.service.SwiftCubePathService;
import com.fr.swift.context.ContextUtil;
import com.fr.swift.property.SwiftProperty;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yee
 * @date 2018/6/6
 */
@SwiftBean(name = "swiftPathService")
public class SwiftCubePathServiceImpl implements SwiftCubePathService {
    private List<PathChangeListener> listeners = new ArrayList<PathChangeListener>();

    private static boolean isValidPath(String path) {
        return path != null && !StringUtils.isBlank(path);
    }

    @Override
    public void registerPathChangeListener(PathChangeListener listener) {
        listeners.add(listener);
    }

    @Override
    public boolean setSwiftPath(final String path) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getSwiftPath() {
        return SwiftProperty.get().getCubesPath() == null ? ContextUtil.getClassPath() : SwiftProperty.get().getCubesPath();
    }
}