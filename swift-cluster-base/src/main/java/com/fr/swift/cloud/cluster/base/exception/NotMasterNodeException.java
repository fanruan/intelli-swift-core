package com.fr.swift.cloud.cluster.base.exception;

/**
 * @Author: lucifer
 * @Description:
 * @Date: Created in 2020/10/28
 */
public class NotMasterNodeException extends RuntimeException {

    public NotMasterNodeException() {
        this("current");
    }

    public NotMasterNodeException(String node) {
        super(String.format("[%s] node is not master!", node));
    }
}
