package com.fr.swift.config.service.impl;

import com.fr.stable.StringUtils;
import com.fr.swift.config.SwiftConfigConstants;
import com.fr.swift.config.bean.RpcServiceAddressBean;
import com.fr.swift.config.dao.SwiftConfigDao;
import com.fr.swift.config.entity.SwiftConfigEntity;
import com.fr.swift.config.service.SwiftConfigService;
import com.fr.swift.config.service.SwiftServiceAddressService;
import com.fr.third.org.hibernate.Session;
import com.fr.third.springframework.beans.factory.annotation.Autowired;
import com.fr.third.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author yee
 * @date 2018/6/27
 */
@Service("swiftServiceAddressService")
public class SwiftServiceAddressServiceImpl implements SwiftServiceAddressService {
    private final SwiftConfigService.ConfigConvert<RpcServiceAddressBean> CONVERT = new SwiftConfigService.ConfigConvert<RpcServiceAddressBean>() {
        @Override
        public RpcServiceAddressBean toBean(SwiftConfigDao<SwiftConfigEntity> dao, Session session, Object... args) throws SQLException {
            SwiftConfigEntity host = dao.select(session, getKey(args[0].toString(), "host"));
            SwiftConfigEntity port = dao.select(session, getKey(args[0].toString(), "port"));
            if (null == host || null == port) {
                return null;
            }
            RpcServiceAddressBean bean = new RpcServiceAddressBean();
            String hostValue = host.getConfigValue();
            String portValue = port.getConfigValue();
            bean.setAddress(hostValue);
            bean.setPort(portValue);
            return bean;
        }

        @Override
        public List<SwiftConfigEntity> toEntity(RpcServiceAddressBean bean, Object... args) {
            List<SwiftConfigEntity> list = new ArrayList<SwiftConfigEntity>();
            list.add(new SwiftConfigEntity(getKey(args[0], "host"), bean.getAddress()));
            list.add(new SwiftConfigEntity(getKey(args[0], "port"), bean.getPort()));
            return Collections.unmodifiableList(list);
        }

        private String getKey(Object name, String key) {
            return String.format("%s.%s.%s", SwiftConfigConstants.FRConfiguration.SERVICE_ADDRESS_NAMESPACE, name, key);
        }
    };

    @Autowired
    private SwiftConfigService configService;

    @Override
    public boolean addOrUpdateAddress(String serviceName, RpcServiceAddressBean address) {
        if (StringUtils.isEmpty(serviceName)) {
            return false;
        }
        return configService.updateConfigBean(CONVERT, address, serviceName);
    }

    @Override
    public RpcServiceAddressBean getAddress(String serviceName) {
        return configService.getConfigBean(CONVERT, serviceName);
    }

}
