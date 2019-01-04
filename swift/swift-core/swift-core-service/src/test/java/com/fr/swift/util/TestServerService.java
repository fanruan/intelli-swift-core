package com.fr.swift.util;

import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.service.ServerService;

/**
 * This class created on 2019/1/4
 *
 * @author Lucifer
 * @description
 */
@com.fr.swift.annotation.ServerService(name = "rpc")
@SwiftBean(name = "rpc")
public class TestServerService implements ServerService {
    @Override
    public void startServerService() throws Exception {

    }

    @Override
    public void stopServerService() throws Exception {

    }
}
