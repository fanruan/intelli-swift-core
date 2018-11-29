package com.fr.swift.config.service.impl;

import com.fr.swift.config.SwiftConfigConstants;
import com.fr.swift.config.bean.SwiftConfigBean;
import com.fr.swift.config.convert.base.AbstractSimpleConfigConvert;
import com.fr.swift.config.dao.SwiftConfigDao;
import com.fr.swift.config.oper.ConfigSession;
import com.fr.swift.config.service.SwiftConfigService;
import com.fr.swift.config.service.SwiftCubePathService;
import com.fr.swift.context.ContextUtil;
import com.fr.swift.event.ClusterEvent;
import com.fr.swift.event.ClusterEventListener;
import com.fr.swift.event.ClusterEventType;
import com.fr.swift.event.ClusterListenerHandler;
import com.fr.swift.selector.ClusterSelector;
import com.fr.swift.util.Strings;
import com.fr.third.springframework.beans.factory.annotation.Autowired;
import com.fr.third.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yee
 * @date 2018/6/6
 */
@Service("swiftPathService")
public class SwiftCubePathServiceImpl implements SwiftCubePathService {
    private List<PathChangeListener> listeners = new ArrayList<PathChangeListener>();
    private String clusterId = SwiftConfigConstants.LOCALHOST;
    private final SwiftConfigService.ConfigConvert<String> CONVERT = new AbstractSimpleConfigConvert<String>(String.class) {

        @Override
        public String toBean(SwiftConfigDao<SwiftConfigBean> dao, ConfigSession session, Object... args) throws SQLException {
            try {
                String path = super.toBean(dao, session, args);
                if (isValidPath(path)) {
                    return path;
                }
            } catch (Exception ignore) {
            }
            String path = ContextUtil.getContextPath();
            for (SwiftConfigBean swiftConfigEntity : toEntity(path)) {
                dao.saveOrUpdate(session, swiftConfigEntity);
            }
            return path;
        }

        @Override
        protected String getNameSpace() {
            return SwiftConfigConstants.FRConfiguration.CUBE_PATH_NAMESPACE + "." + clusterId;
        }
    };

    public SwiftCubePathServiceImpl() {
        ClusterListenerHandler.addInitialListener(new ClusterEventListener() {
            @Override
            public void handleEvent(ClusterEvent clusterEvent) {
                if (clusterEvent.getEventType() == ClusterEventType.JOIN_CLUSTER) {
                    clusterId = ClusterSelector.getInstance().getFactory().getCurrentId();
                } else if (clusterEvent.getEventType() == ClusterEventType.LEFT_CLUSTER) {
                    clusterId = SwiftConfigConstants.LOCALHOST;
                }
            }
        });
    }

    @Autowired
    private SwiftConfigService configService;

    private static boolean isValidPath(String path) {
        return path != null && !Strings.isEmpty(path) && !path.equals("__EMPTY__");
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
                configService.updateConfigBean(CONVERT, path);
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
        return configService.getConfigBean(CONVERT);
    }
}
