package com.fr.swift.source.core;


import com.fr.swift.exception.AmountLimitUnmetException;

import javax.activation.UnsupportedDataTypeException;

/**
 * Created by Connery on 2015/12/23.
 */
public interface CoreOperation {


    void registerAttribute(Object... attributes) throws UnsupportedDataTypeException, AmountLimitUnmetException;

    void clearCore();

    Object getAttribute(Integer index);

    String getValue();
}