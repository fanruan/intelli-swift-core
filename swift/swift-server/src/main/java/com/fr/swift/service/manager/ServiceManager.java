package com.fr.swift.service.manager;

import java.util.List;

/**
 * This class created on 2018/8/7
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public interface ServiceManager<T> {

    void registerService(T swiftService) throws Exception;

    void registerService(List<T> swiftServiceList) throws Exception;

    void unregisterService(T swiftService) throws Exception;

    void unregisterService(List<T> swiftServiceList) throws Exception;
}
