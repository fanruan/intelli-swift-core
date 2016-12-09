package com.fr.bi.common.factory;

/**
 * Key名称重复异常
 * Created by Connery on 2015/12/8.
 */
public class BIFactoryKeyAbsentException extends Exception{
    private static final long serialVersionUID = -878178473604394615L;

    public BIFactoryKeyAbsentException(String message) {
        super(message);
    }
}