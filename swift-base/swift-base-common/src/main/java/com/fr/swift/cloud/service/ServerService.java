package com.fr.swift.cloud.service;

import com.fr.swift.cloud.annotation.service.InnerService;

/**
 * This class created on 2018/8/7
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
@InnerService
public interface ServerService {

    public void startServerService() throws Exception;

    public void stopServerService() throws Exception;
}
