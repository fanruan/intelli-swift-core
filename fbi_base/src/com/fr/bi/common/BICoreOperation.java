package com.fr.bi.common;

import com.fr.bi.base.BIBasicIdentity;
import com.fr.bi.exception.BIAmountLimitUnmetException;

import javax.activation.UnsupportedDataTypeException;

/**
 * Created by Connery on 2015/12/23.
 */
public interface BICoreOperation {


    void registerAttribute(Object... attributes) throws UnsupportedDataTypeException, BIAmountLimitUnmetException;

    void clearCore();

    Object getAttribute(Integer index);

    BIBasicIdentity getID();

    String getIDValue();
}