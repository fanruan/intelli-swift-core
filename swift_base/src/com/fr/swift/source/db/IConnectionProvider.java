package com.fr.swift.source.db;


/**
 * This class created on 2017/4/11.
 *
 * @author Connery
 * @since Advanced FineBI Analysis 1.0
 */
public interface IConnectionProvider {
    ConnectionInfo getConnection(String connectionName);
}
