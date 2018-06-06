package com.fr.swift.service;

import com.fr.swift.exception.SwiftServiceException;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.stuff.IndexingStuff;

/**
 *
 * @author pony
 * @date 2017/10/10
 */
public class SwiftIndexingService extends AbstractSwiftService implements IndexingService {
    private static final long serialVersionUID = -7430843337225891194L;

    public SwiftIndexingService(String id) {
        super(id);
    }

    public SwiftIndexingService() {
    }

    @Override
    public boolean start() throws SwiftServiceException {
        super.start();
        return true;
    }

    @Override
    public ServiceType getServiceType() {
        return ServiceType.INDEXING;
    }

    @Override
    public void index(IndexingStuff stuff) {
        SwiftLoggers.getLogger().info("indexing stuff");
    }
}