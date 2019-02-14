package com.fr.swift.basic.service;

/**
 * This class created on 2019/1/4
 *
 * @author Lucifer
 * @description
 */

import com.fr.swift.basics.annotation.ProxyService;

@ProxyService(value = TestInternalService.class, type = ProxyService.ServiceType.INTERNAL)
public class TestInternalService {
}
