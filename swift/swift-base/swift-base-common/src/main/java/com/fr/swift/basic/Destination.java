package com.fr.swift.basic;

/**
 * This class created on 2018/5/26
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public interface Destination {

    /**
     * clusterId or ip address
     *
     * @return
     */
    String getId();

    @Override
    boolean equals(Object o);

    @Override
    int hashCode();
}
