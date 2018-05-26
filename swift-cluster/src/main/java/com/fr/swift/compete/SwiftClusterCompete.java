package com.fr.swift.compete;

/**
 * This class created on 2018/5/25
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public interface SwiftClusterCompete {

    boolean setMaster(String newMasterId);

    void computeMaster();

    String getMaster();
}
