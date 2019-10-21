package com.fr.swift.config.service.impl;

import com.fr.swift.SwiftContext;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.config.bean.FineIOConnectorConfig;
import com.fr.swift.config.convert.FineIOConfigConvert;
import com.fr.swift.config.service.SwiftConfigService;
import com.fr.swift.config.service.SwiftFineIOConnectorService;
import com.fr.swift.log.SwiftLoggers;

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
    private SwiftConfigService configService = SwiftContext.get().getBean(SwiftConfigService.class);
    private Map<Type, List<ConfChangeListener>> changeListeners = new HashMap<>();

    public SwiftFineIOConfigServiceImpl() {
        changeListeners.put(Type.CONNECTOR, new ArrayList<ConfChangeListener>());
        changeListeners.put(Type.PACKAGE, new ArrayList<ConfChangeListener>());
    }

    @Override
    public FineIOConnectorConfig getCurrentConfig(Type type) {
        FineIOConfigConvert convert = type == Type.CONNECTOR ? FineIOConfigConvert.CONNECTOR : FineIOConfigConvert.PACKAGE;
        return configService.getConfigBean(convert);
    }

    @Override
    public void setCurrentConfig(FineIOConnectorConfig config, Type type) {
        FineIOConfigConvert convert = type == Type.CONNECTOR ? FineIOConfigConvert.CONNECTOR : FineIOConfigConvert.PACKAGE;
        FineIOConnectorConfig current = getCurrentConfig(type);
        if (null != config && !config.equals(current)) {
            configService.deleteConfigBean(convert, current);
            configService.updateConfigBean(convert, config);
            List<ConfChangeListener> confChangeListeners = changeListeners.get(type);
            for (ConfChangeListener changeListener : confChangeListeners) {
                try {
                    changeListener.change(config);
                } catch (Exception e) {
                    SwiftLoggers.getLogger().error("Cannot set swift repository config.", e);
                }
            }
        }
    }

    @Override
    public void registerListener(ConfChangeListener listener, Type type) {
        if (null == changeListeners.get(type)) {
            changeListeners.put(type, new ArrayList<ConfChangeListener>());
        }
        changeListeners.get(type).add(listener);
    }
}
