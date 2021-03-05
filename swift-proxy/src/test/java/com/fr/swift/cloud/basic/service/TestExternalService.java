package com.fr.swift.cloud.basic.service;

import com.fr.swift.cloud.basics.annotation.ProxyService;

/**
 * This class created on 2019/1/4
 *
 * @author Lucifer
 * @description
 */
@ProxyService(value = TestExternalService.class, type = ProxyService.ServiceType.EXTERNAL)
public class TestExternalService {
}
