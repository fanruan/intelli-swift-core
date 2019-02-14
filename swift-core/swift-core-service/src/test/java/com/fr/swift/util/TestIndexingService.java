package com.fr.swift.util;

import com.fr.swift.annotation.SwiftService;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.config.bean.ServerCurrentStatus;
import com.fr.swift.service.AbstractSwiftService;
import com.fr.swift.service.IndexingService;
import com.fr.swift.service.ServiceType;
import com.fr.swift.stuff.IndexingStuff;

/**
 * This class created on 2019/1/4
 *
 * @author Lucifer
 * @description
 */
@SwiftService(name = "indexing")
@SwiftBean(name = "indexing")
public class TestIndexingService extends AbstractSwiftService implements IndexingService {
    @Override
    public <Stuff extends IndexingStuff> void index(Stuff stuff) {

    }

    @Override
    public ServerCurrentStatus currentStatus() {
        return null;
    }

    @Override
    public ServiceType getServiceType() {
        return null;
    }
}
