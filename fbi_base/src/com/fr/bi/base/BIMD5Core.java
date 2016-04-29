package com.fr.bi.base;

import javax.activation.UnsupportedDataTypeException;

/**
 * 兼容MD5，如果属性是String类型的话，那么就直接返回
 * String值作为value的ID值。
 * <p/>
 * Created by Connery on 2015/12/24.
 */

public class BIMD5Core extends BIBasicCore {
    public static BIMD5Core EMPTY_CORE = new BIMD5Core();
    public BIMD5Core() {
        amountLimit = 1;
    }

    @Override
    protected BIBasicIdentity computeValue() {
        if(coreAttributes.size()!=1){
            throw new UnsupportedOperationException("BIMDCore can't just support too many attribute ");
        }
        return new BIBasicIdentity((String) coreAttributes.get(0));
    }

    @Override
    protected boolean isTypeSupport(Object value) throws UnsupportedDataTypeException {
        return value instanceof String;
    }
}