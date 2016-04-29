package com.fr.bi.common;

import com.fr.bi.base.BIBasicCore;
import com.fr.bi.base.BICore;
import com.fr.bi.exception.BIAmountLimitUnmetException;

import javax.activation.UnsupportedDataTypeException;

/**
 * Created by Connery on 2015/12/24.
 */
public class BIMD5CoreWrapper extends BICoreWrapper {
    public BIMD5CoreWrapper(Object... attributes) throws UnsupportedDataTypeException, BIAmountLimitUnmetException {
        super(attributes);
    }

    @Override
	protected BICore generateCore(Object... attributes) throws UnsupportedDataTypeException, BIAmountLimitUnmetException {
        if (attributes == null || attributes.length != 1) {
            throw new BIAmountLimitUnmetException();
        }
        BICore core = new BIBasicCore();
        core.registerAttribute(attributes);
        return core;

    }

}