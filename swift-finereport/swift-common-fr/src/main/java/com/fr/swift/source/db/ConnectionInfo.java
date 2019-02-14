package com.fr.swift.source.db;

import com.fr.data.impl.Connection;

/**
 * This class created on 2017/4/11.
 *
 * @author Connery
 * @since Advanced FineBI Analysis 1.0
 */
public interface ConnectionInfo {

    String getSchema();

    Connection getFrConnection();
}
