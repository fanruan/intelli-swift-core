package com.fr.swift.config.service.impl;

import com.fr.swift.SwiftContext;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.config.SwiftConfigConstants;
import com.fr.swift.config.command.SwiftConfigEntityCommandBus;
import com.fr.swift.config.command.impl.SwiftConfigEntityCommandBusImpl;
import com.fr.swift.config.query.SwiftConfigEntityQueryBus;
import com.fr.swift.config.query.impl.SwiftConfigEntityQueryBusImpl;
import com.fr.swift.config.service.SwiftCubePathService;
import com.fr.swift.context.ContextProvider;
import com.fr.swift.util.Strings;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yee
 * @date 2018/6/6
 */
@SwiftBean(name = "swiftPathService")
public class SwiftCubePathServiceImpl implements SwiftCubePathService {
    private SwiftConfigEntityCommandBus commandBus = new SwiftConfigEntityCommandBusImpl();
    private SwiftConfigEntityQueryBus queryBus = new SwiftConfigEntityQueryBusImpl();
    private List<PathChangeListener> listeners = new ArrayList<PathChangeListener>();

    private static boolean isValidPath(String path) {
        return path != null && !Strings.isEmpty(path) && !"__EMPTY__".equals(path);
    }

    @Override
    public void registerPathChangeListener(PathChangeListener listener) {
        listeners.add(listener);
    }

    @Override
    public boolean setSwiftPath(final String path) {
        if (isValidPath(path)) {
            try {
                String oldPath = getSwiftPath();
                if (oldPath.equals(path)) {
                    return false;
                }
                commandBus.merge(SwiftConfigConstants.Namespace.SWIFT_CUBE_PATH, path);
                for (PathChangeListener listener : listeners) {
                    listener.changed(path);
                }
                return true;
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    @Override
    public String getSwiftPath() {
        return queryBus.select(SwiftConfigConstants.Namespace.SWIFT_CUBE_PATH, String.class, SwiftContext.get().getBean(ContextProvider.class).getContextPath());
    }
}
