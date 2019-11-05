package com.fr.swift.service;

/**
 * This class created on 2018/8/21
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public interface SwiftManager {
    void startUp() throws Exception;

    void shutDown() throws Exception;

    boolean isRunning() throws Exception;
}
