package com.fr.swift.config.service.impl;

import com.fr.general.ComparatorUtils;
import com.fr.swift.config.bean.SwiftFileSystemConfig;
import com.fr.swift.config.convert.SwiftFileSystemConvert;
import com.fr.swift.config.service.SwiftConfigService;
import com.fr.swift.config.service.SwiftRepositoryConfService;
import com.fr.third.springframework.beans.factory.annotation.Autowired;
import com.fr.third.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yee
 * @date 2018/7/6
 */
@Service("swiftRepositoryConfService")
public class SwiftRepositoryConfServiceImpl implements SwiftRepositoryConfService {
    private static final SwiftFileSystemConvert CONVERT = new SwiftFileSystemConvert();

    private List<ConfChangeListener> changeListeners = new ArrayList<ConfChangeListener>();
    @Autowired
    private SwiftConfigService configService;

    @Override
    public SwiftFileSystemConfig getCurrentRepository() {
        return configService.getConfigBean(CONVERT);
    }

    @Override
    public boolean setCurrentRepository(SwiftFileSystemConfig config) {
        SwiftFileSystemConfig current = getCurrentRepository();
        if (null == current || !ComparatorUtils.equals(config, current)) {
            configService.deleteConfigBean(CONVERT, current);
            for (ConfChangeListener changeListener : changeListeners) {
                changeListener.change(config);
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
