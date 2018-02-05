package com.fr.swift.service;


import com.fr.swift.exception.SwiftServiceException;
import com.fr.swift.stuff.RealTimeIndexingStuff;

/**
 * Created by pony on 2017/10/10.
 */
public class SwiftRealTimeService extends AbstractSwiftService{
    public boolean start() throws SwiftServiceException {
        super.start();
        return true;
    }

    @Override
    public ServiceType getServiceType() {
        return ServiceType.REAL_TIME;
    }

    public void index(RealTimeIndexingStuff stuff){

    }
}
