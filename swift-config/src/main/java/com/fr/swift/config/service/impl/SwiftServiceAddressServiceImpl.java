package com.fr.swift.config.service.impl;

import com.fr.swift.SwiftContext;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.config.SwiftConfigConstants;
import com.fr.swift.config.bean.RpcServiceAddressBean;
import com.fr.swift.config.bean.SwiftConfigBean;
import com.fr.swift.config.dao.SwiftConfigDao;
import com.fr.swift.config.oper.ConfigSession;
import com.fr.swift.config.service.SwiftConfigService;
import com.fr.swift.config.service.SwiftServiceAddressService;
import com.fr.swift.util.Strings;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author yee
 * @date 2018/6/27
 */
@SwiftBean(name = "swiftServiceAddressService")
@Deprecated
public class SwiftServiceAddressServiceImpl implements SwiftServiceAddressService {
    private final SwiftConfigService.ConfigConvert<RpcServiceAddressBean> CONVERT = new SwiftConfigService.ConfigConvert<RpcServiceAddressBean>() {
        @Override
        public RpcServiceAddressBean toBean(SwiftConfigDao<SwiftConfigBean> dao, ConfigSession session, Object... args) throws SQLException {
            SwiftConfigBean host = dao.select(session, getKey(args[0].toString(), "host"));
            SwiftConfigBean port = dao.select(session, getKey(args[0].toString(), "port"));
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
        public List<SwiftConfigBean> toEntity(RpcServiceAddressBean bean, Object... args) {
            List<SwiftConfigBean> list = new ArrayList<SwiftConfigBean>();
            list.add(new SwiftConfigBean(getKey(args[0], "host"), bean.getAddress()));
            list.add(new SwiftConfigBean(getKey(args[0], "port"), bean.getPort()));
            return Collections.unmodifiableList(list);
        }

        private String getKey(Object name, String key) {
            return String.format("%s.%s.%s", SwiftConfigConstants.FRConfiguration.SERVICE_ADDRESS_NAMESPACE, name, key);
        }
    };

    private SwiftConfigService configService = SwiftContext.get().getBean(SwiftConfigService.class);

    @Override
    public boolean addOrUpdateAddress(String serviceName, RpcServiceAddressBean address) {
        if (Strings.isEmpty(serviceName)) {
            return false;
        }
        return configService.updateConfigBean(CONVERT, address, serviceName);
    }

    @Override
    public RpcServiceAddressBean getAddress(String serviceName) {
        return configService.getConfigBean(CONVERT, serviceName);
    }

}
