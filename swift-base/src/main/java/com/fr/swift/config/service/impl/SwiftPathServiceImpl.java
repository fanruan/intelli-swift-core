package com.fr.swift.config.service.impl;

import com.fr.config.Configuration;
import com.fr.swift.config.SwiftCubePathConfig;
import com.fr.swift.config.service.SwiftPathService;
import com.fr.third.springframework.stereotype.Service;
import com.fr.transaction.Configurations;
import com.fr.transaction.Worker;

/**
 * @author yee
 * @date 2018/6/6
 */
@Service("swiftPathService")
public class SwiftPathServiceImpl implements SwiftPathService {
    private SwiftCubePathConfig swiftCubePathConfig = SwiftCubePathConfig.getInstance();

    @Override
    public boolean setSwiftPath(final String path) {
        return Configurations.update(new SwiftPathConfigWorker() {
            @Override
            public void run() {
                swiftCubePathConfig.setPath(path);
            }
        });
    }

    @Override
    public String getSwiftPath() {
        return this.swiftCubePathConfig.getPath();
    }

    private abstract class SwiftPathConfigWorker implements Worker {
        @Override
        public Class<? extends Configuration>[] targets() {
            return new Class[]{SwiftCubePathConfig.class};
        }
    }
}
