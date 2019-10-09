package com.fr.swift.result;

/**
 * @author lucifer
 * @date 2019/7/24
 * @description
 * @since swift 1.1
 */
public interface MutableResultSet extends SwiftResultSet {

    boolean hasNewSubfields();

    String getCurrentTableName();
}