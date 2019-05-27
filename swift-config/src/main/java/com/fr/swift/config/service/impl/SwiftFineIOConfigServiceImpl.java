package com.fr.swift.config.service.impl;

import com.fr.swift.SwiftContext;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.config.bean.FineIOConnectorConfig;
import com.fr.swift.config.convert.FineIOConfigConvert;
import com.fr.swift.config.service.SwiftConfigService;
import com.fr.swift.config.service.SwiftFineIOConnectorService;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.repository.exception.RepoNotFoundException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yee
 * @date 2018-12-20
 */
@SwiftBean(name = "swiftFineIOConfigServiceImpl")
public class SwiftFineIOConfigServiceImpl implements SwiftFineIOConnectorService {
    private static final FineIOConfigConvert CONFIG_CONVERT = new FineIOConfigConvert();
    private SwiftConfigService configService = SwiftContext.get().getBean(SwiftConfigService.class);
    private List<ConfChangeListener> changeListeners = new ArrayList<ConfChangeListener>();

    @Override
    public FineIOConnectorConfig getCurrentConfig() {
        return configService.getConfigBean(CONFIG_CONVERT);
    }

    @Override
    public void setCurrentConfig(FineIOConnectorConfig config) {
        FineIOConnectorConfig current = getCurrentConfig();
        if (null != config && !config.equals(current)) {
            configService.deleteConfigBean(CONFIG_CONVERT, current);
            configService.updateConfigBean(CONFIG_CONVERT, config);
            for (ConfChangeListener changeListener : changeListeners) {
                try {
                    changeListener.change(config);
                } catch (RepoNotFoundException e) {
                    SwiftLoggers.getLogger().warn("Cannot find default repository config.", e);
                } catch (Exception e) {
                    SwiftLoggers.getLogger().error("Cannot set swift repository config.", e);
                }
            }
        }
    }

    @Override
    public void registerListener(ConfChangeListener listener) {
        changeListeners.add(listener);
    }

}
