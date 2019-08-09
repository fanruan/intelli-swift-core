package com.fr.swift.config.service.impl;

import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.config.SwiftConfigConstants;
import com.fr.swift.config.bean.CommonConnectorConfig;
import com.fr.swift.config.bean.FineIOConnectorConfig;
import com.fr.swift.config.command.SwiftConfigEntityCommandBus;
import com.fr.swift.config.command.impl.SwiftConfigEntityCommandBusImpl;
import com.fr.swift.config.query.SwiftConfigEntityQueryBus;
import com.fr.swift.config.query.impl.SwiftConfigEntityQueryBusImpl;
import com.fr.swift.config.service.SwiftFineIOConnectorService;
import com.fr.swift.cube.io.impl.fineio.connector.CommonConnectorType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yee
 * @date 2018-12-20
 */
@SwiftBean(name = "swiftFineIOConfigServiceImpl")
public class SwiftFineIOConfigServiceImpl implements SwiftFineIOConnectorService {
    private SwiftConfigEntityCommandBus commandBus = new SwiftConfigEntityCommandBusImpl();
    private SwiftConfigEntityQueryBus queryBus = new SwiftConfigEntityQueryBusImpl();

    private Map<SwiftConfigConstants.Namespace, List<ConfChangeListener>> changeListeners = new HashMap<>();

    @Override
    public FineIOConnectorConfig getCurrentConfig(SwiftConfigConstants.Namespace type) {
        return queryBus.select(type, FineIOConnectorConfig.class, new CommonConnectorConfig(CommonConnectorType.LZ4));
    }

    @Override
    public void setCurrentConfig(FineIOConnectorConfig config, SwiftConfigConstants.Namespace namespace) {
        for (ConfChangeListener confChangeListener : changeListeners.get(namespace)) {
            confChangeListener.change(config);
        }
        commandBus.delete(namespace);
        commandBus.merge(namespace, config);
    }

    @Override
    public void registerListener(ConfChangeListener listener, SwiftConfigConstants.Namespace type) {
        if (null == changeListeners.get(type)) {
            changeListeners.put(type, new ArrayList<ConfChangeListener>());
        }
        changeListeners.get(type).add(listener);
    }
}
