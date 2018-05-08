package com.fr.swift.service;

import com.fr.swift.exception.SwiftServiceException;
import com.fr.swift.stuff.HistoryIndexingStuff;

/**
 * Created by pony on 2017/10/10.
 */
public class SwiftIndexingService extends AbstractSwiftService {
    @Override
    public boolean start() throws SwiftServiceException {
        super.start();
        return true;
    }

    @Override
    public ServiceType getServiceType() {
        return ServiceType.INDEXING;
    }

    public void index(HistoryIndexingStuff stuff){

    }
}
