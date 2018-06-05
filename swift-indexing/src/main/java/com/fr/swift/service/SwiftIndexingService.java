package com.fr.swift.service;

import com.fr.swift.exception.SwiftServiceException;
import com.fr.swift.stuff.HistoryIndexingStuff;

import java.io.Serializable;

/**
 * Created by pony on 2017/10/10.
 */
public class SwiftIndexingService extends AbstractSwiftService implements Serializable {

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

    public void index(HistoryIndexingStuff stuff){

    }
}
