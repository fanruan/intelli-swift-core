package com.fr.swift.service;

import java.io.Serializable;

/**
 * Created by pony on 2017/10/10.
 */
public class SwiftHistoryService extends AbstractSwiftService implements Serializable {

    private static final long serialVersionUID = -6013675740141588108L;

    public SwiftHistoryService(String id) {
        super(id);
    }

    public SwiftHistoryService() {
    }

    @Override
    public ServiceType getServiceType() {
        return ServiceType.HISTORY;
    }
}
