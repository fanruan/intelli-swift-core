package com.fr.swift.service;

import com.fr.swift.annotation.service.OuterService;
import com.fr.swift.exception.SwiftServiceException;

/**
 * @author pony
 * @date 2017/10/10
 * swift的服务
 */
@OuterService
public interface SwiftService {

    /**
     * 启动服务
     *
     * @return 是否成功
     * @throws SwiftServiceException ex
     */
    boolean start() throws Exception;

    /**
     * 关闭服务
     *
     * @return 是否成功
     * @throws SwiftServiceException ex
     */
    boolean shutdown() throws Exception;

    /**
     * 服务的类型。
     *
     * @return type
     */
    ServiceType getServiceType();

    /**
     * id, unregister的时候要区分下
     *
     * @return id
     */
    String getId();

    /**
     * 设置id
     *
     * @param id id
     */
    void setId(String id);

}
