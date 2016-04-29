package com.fr.bi.common.factory;

/**
 * Key名称重复异常
 * Created by Connery on 2015/12/8.
 */
public class BIFactoryKeyDuplicateException extends Exception{
    public BIFactoryKeyDuplicateException(String message) {
        super(message);
    }
}