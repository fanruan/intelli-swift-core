package com.fr.bi.common.factory;

/**
 * Key名称重复异常
 * Created by Connery on 2015/12/8.
 */
public class BIFactoryKeyDuplicateException extends Exception{
    private static final long serialVersionUID = -2527198399789152230L;

    public BIFactoryKeyDuplicateException(String message) {
        super(message);
    }
}