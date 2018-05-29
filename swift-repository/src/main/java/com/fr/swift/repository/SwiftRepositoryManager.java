package com.fr.swift.repository;

/**
 * @author yee
 * @date 2018/5/28
 */
public class SwiftRepositoryManager {
    private static final SwiftRepositoryManager manager = new SwiftRepositoryManager();

    private SwiftRepositoryManager() {
    }

    public static SwiftRepositoryManager getManager() {
        return manager;
    }
}
