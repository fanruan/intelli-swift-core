package com.fr.swift.config.service.impl;

import com.fr.swift.SwiftContext;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.config.SwiftConfigConstants;
import com.fr.swift.config.bean.SwiftConfigBean;
import com.fr.swift.config.convert.base.AbstractSimpleConfigConvert;
import com.fr.swift.config.dao.SwiftConfigDao;
import com.fr.swift.config.oper.ConfigSession;
import com.fr.swift.config.service.SwiftConfigService;
import com.fr.swift.config.service.SwiftCubePathService;
import com.fr.swift.context.ContextProvider;
import com.fr.swift.property.SwiftProperty;
import com.fr.swift.util.Strings;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yee
 * @date 2018/6/6
 */
@SwiftBean(name = "swiftPathService")
public class SwiftCubePathServiceImpl implements SwiftCubePathService {
    private List<PathChangeListener> listeners = new ArrayList<PathChangeListener>();
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
            String path = SwiftContext.get().getBean(ContextProvider.class).getContextPath();
            for (SwiftConfigBean swiftConfigEntity : toEntity(path)) {
                dao.saveOrUpdate(session, swiftConfigEntity);
            }
            return path;
        }

        @Override
        protected String getNameSpace() {
            return SwiftConfigConstants.FRConfiguration.CUBE_PATH_NAMESPACE + "." + SwiftProperty.getProperty().getClusterId();
        }
    };

    private SwiftConfigService configService = SwiftContext.get().getBean(SwiftConfigService.class);

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
