package com.fr.swift.config.convert;

import com.fr.general.ComparatorUtils;
import com.fr.stable.StringUtils;
import com.fr.swift.config.SwiftConfigConstants;
import com.fr.swift.config.bean.FtpRepositoryConfigBean;
import com.fr.swift.config.bean.HdfsRepositoryConfigBean;
import com.fr.swift.config.bean.SwiftFileSystemConfig;
import com.fr.swift.config.bean.SwiftFileSystemType;
import com.fr.swift.config.dao.SwiftConfigDao;
import com.fr.swift.config.entity.SwiftConfigEntity;
import com.fr.swift.config.service.SwiftConfigService;
import com.fr.swift.log.SwiftLoggers;
import com.fr.third.org.hibernate.Session;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yee
 * @date 2018/7/6
 */
public class SwiftFileSystemConvert implements SwiftConfigService.ConfigConvert<SwiftFileSystemConfig> {
    @Override
    public SwiftFileSystemConfig toBean(SwiftConfigDao<SwiftConfigEntity> dao, Session session, Object... args) throws SQLException {
        SwiftConfigEntity entity = dao.select(session, getKey("type"));
        if (null == entity) {
            return null;
        }
        try {
            SwiftFileSystemType type = SwiftFileSystemType.valueOf(entity.getConfigValue());
            switch (type) {
                case FR:
                    return null;
                default:
                    return readConfig(dao, session, type);
            }
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e.getMessage());
            return null;
        }
    }

    @Override
    public List<SwiftConfigEntity> toEntity(SwiftFileSystemConfig swiftFileSystemConfig, Object... args) {
        return createEntity(swiftFileSystemConfig);
    }

    private String getKey(String key) {
        return String.format("%s.%s", SwiftConfigConstants.FRConfiguration.REPOSITORY_CONF_NAMESPACE, key);
    }

    private SwiftFileSystemConfig readConfig(SwiftConfigDao<SwiftConfigEntity> dao, Session session, SwiftFileSystemType type) throws SQLException {
        SwiftFileSystemConfig configBean = null;
        try {
            switch (type) {
                case FTP:
                    configBean = new FtpRepositoryConfigBean();
                    break;
                case HDFS:
                    configBean = new HdfsRepositoryConfigBean();
                    break;
            }
            Field[] fields = configBean.getClass().getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                SwiftConfigEntity entity = dao.select(session, getKey(type.name() + "." + field.getName()));
                if (null != entity) {
                    String value = entity.getConfigValue();
                    if (ComparatorUtils.equals(value, "__EMPTY__")) {
                        value = StringUtils.EMPTY;
                    }
                    field.set(configBean, value);
                }
            }
            return configBean;
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    private List<SwiftConfigEntity> createEntity(SwiftFileSystemConfig config) {
        Field[] fields = config.getClass().getDeclaredFields();
        List<SwiftConfigEntity> list = new ArrayList<SwiftConfigEntity>();
        list.add(new SwiftConfigEntity(getKey("type"), config.getType().name()));
        try {
            for (Field field : fields) {
                field.setAccessible(true);
                Object obj = field.get(config);
                if (null == obj) {
                    obj = StringUtils.EMPTY;
                }
                list.add(new SwiftConfigEntity(getKey(config.getType().name() + "." + field.getName()), obj.toString()));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return list;
    }

}
