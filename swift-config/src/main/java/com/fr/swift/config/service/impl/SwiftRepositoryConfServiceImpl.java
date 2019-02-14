package com.fr.swift.config.service.impl;

import com.fr.swift.SwiftContext;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.config.convert.SwiftFileSystemConvert;
import com.fr.swift.config.service.SwiftConfigService;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.repository.SwiftFileSystemConfig;
import com.fr.swift.repository.exception.DefaultRepoNotFoundException;
import com.fr.swift.service.SwiftRepositoryConfService;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yee
 * @date 2018/7/6
 */
@SwiftBean(name = "swiftRepositoryConfService")
public class SwiftRepositoryConfServiceImpl implements SwiftRepositoryConfService {
    private static final SwiftFileSystemConvert CONVERT = new SwiftFileSystemConvert();

    private List<ConfChangeListener> changeListeners = new ArrayList<ConfChangeListener>();
    private SwiftConfigService configService = SwiftContext.get().getBean(SwiftConfigService.class);

    @Override
    public SwiftFileSystemConfig getCurrentRepository() {
        return configService.getConfigBean(CONVERT);
    }

    @Override
    public boolean setCurrentRepository(SwiftFileSystemConfig config) {
        if (null == config) {
            return false;
        }
        SwiftFileSystemConfig current = null;
        try {
            current = getCurrentRepository();
        } catch (Exception e) {
            SwiftLoggers.getLogger().warn("Cannot find swift repository config.");
        }
        if (null == current || !config.equals(current)) {
            configService.deleteConfigBean(CONVERT, current);
            for (ConfChangeListener changeListener : changeListeners) {
                try {
                    changeListener.change(config);
                } catch (DefaultRepoNotFoundException e) {
                    SwiftLoggers.getLogger().warn("Cannot find default repository config.", e);
                } catch (Exception e) {
                    SwiftLoggers.getLogger().error("Cannot set swift repository config.", e);
                }
            }
            return configService.updateConfigBean(CONVERT, config);
        }
        return false;
    }

    @Override
    public void registerListener(ConfChangeListener listener) {
        changeListeners.add(listener);
    }
}
