package com.fr.swift;

/**
 * This class created on 2018/5/29
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public interface ClusterNodeService<T> {

    String SERVICE = "cluster_master_service";

    /**
     * @return
     */
    boolean competeMaster();

    /**
     * @param t 退出的节点
     * @return
     */
    boolean competeMaster(T t);
}
