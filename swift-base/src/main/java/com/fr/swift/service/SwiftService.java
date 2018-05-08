package com.fr.swift.service;

import com.fr.swift.exception.SwiftServiceException;

/**
 * Created by pony on 2017/10/10.
 * swift的服务
 */
public interface SwiftService {

    /**
     * 启动服务
     * @return 是否成功
     * @throws SwiftServiceException
     */
    boolean start() throws SwiftServiceException;

    /**
     * 关闭服务
     * @return 是否成功
     * @throws SwiftServiceException
     */
    boolean shutdown() throws SwiftServiceException;

    /**
     * 服务的类型。
     * @return
     */
    ServiceType getServiceType();

    /**
     * id,unregister的时候要区分下
     * @return
     */
    String getID();

}
