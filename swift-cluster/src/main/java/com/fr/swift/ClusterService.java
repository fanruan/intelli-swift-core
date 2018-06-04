package com.fr.swift;

/**
 * This class created on 2018/5/29
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public interface ClusterService {
    boolean competeMaster();

    String rpcSend(String masterId, Object object);

    String rpcSend();
}
