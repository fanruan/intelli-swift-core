package com.fr.swift.config.service.impl;

import com.fr.general.ComparatorUtils;
import com.fr.stable.StringUtils;
import com.fr.swift.config.SwiftConfigConstants;
import com.fr.swift.config.dao.SwiftConfigDao;
import com.fr.swift.config.entity.SwiftConfigEntity;
import com.fr.swift.config.service.SwiftConfigService;
import com.fr.swift.config.service.SwiftPathService;
import com.fr.swift.context.ContextUtil;
import com.fr.third.org.hibernate.Session;
import com.fr.third.springframework.beans.factory.annotation.Autowired;
import com.fr.third.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author yee
 * @date 2018/6/6
 */
@Service("swiftPathService")
public class SwiftPathServiceImpl implements SwiftPathService {
    private List<PathChangeListener> listeners = new ArrayList<PathChangeListener>();

    private final SwiftConfigService.ConfigConvert<String> CONVERT = new SwiftConfigService.ConfigConvert<String>() {
        @Override
        public String toBean(SwiftConfigDao<SwiftConfigEntity> dao, Session session, Object... args) throws SQLException {
            SwiftConfigEntity entity = dao.select(session, SwiftConfigConstants.FRConfiguration.CUBE_PATH_NAMESPACE);
            if (null != entity) {
                String path = entity.getConfigValue();
                if (isValidPath(path)) {
                    return path;
                }
            }
            String path = getDefaultPath();
            for (SwiftConfigEntity swiftConfigEntity : toEntity(path)) {
                dao.saveOrUpdate(session, swiftConfigEntity);
            }
            return path;
        }

        @Override
        public List<SwiftConfigEntity> toEntity(String string, Object... args) {
            SwiftConfigEntity entity = new SwiftConfigEntity();
            entity.setConfigKey(SwiftConfigConstants.FRConfiguration.CUBE_PATH_NAMESPACE);
            entity.setConfigValue(string);
            return Collections.singletonList(entity);
        }
    };

    @Autowired
    private SwiftConfigService configService;

    private static String getDefaultPath() {
        String classPath = ContextUtil.getClassPath();
        int idx = classPath.indexOf("WEB-INF");
        if (idx != -1) {
            return classPath.substring(0, idx);
        }
        return classPath + "/../";
    }

    private static boolean isValidPath(String path) {
        return path != null && !StringUtils.isBlank(path) && !ComparatorUtils.equals(path, "__EMPTY__");
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
                if (ComparatorUtils.equals(oldPath, path)) {
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
