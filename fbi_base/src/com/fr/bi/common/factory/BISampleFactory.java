package com.fr.bi.common.factory;

/**
 * BI的简单工厂
 * Created by Connery on 2016/1/21.
 */
public abstract class BISampleFactory {
    public abstract Object getSpecificObject(Object specificObj);
}