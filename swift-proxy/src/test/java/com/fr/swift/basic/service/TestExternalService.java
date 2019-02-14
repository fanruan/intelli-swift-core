package com.fr.swift.basic.service;

import com.fr.swift.basics.annotation.ProxyService;

/**
 * This class created on 2019/1/4
 *
 * @author Lucifer
 * @description
 */
@ProxyService(value = TestExternalService.class, type = ProxyService.ServiceType.EXTERNAL)
public class TestExternalService {
}
