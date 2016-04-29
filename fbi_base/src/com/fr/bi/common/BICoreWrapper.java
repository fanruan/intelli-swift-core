package com.fr.bi.common;

import com.fr.bi.base.BIBasicCore;
import com.fr.bi.base.BIBasicIdentity;
import com.fr.bi.base.BICore;
import com.fr.bi.exception.BIAmountLimitUnmetException;

import javax.activation.UnsupportedDataTypeException;

/**
 * 对象需要使用BICore的话，
 * 需要实现BICoreService，通过实现一个子类，并且以内部类的形式
 * 在构造函数中加入需要作为Core的属性的值。
 * 同时在实现BICoreService中方法时，new出此内部类。
 * <p/>
 * 可以参考AbstractTableSource中的Core
 * Created by Connery on 2015/12/23.
 */
public class BICoreWrapper implements BICoreOperation, BICoreService {

    private BICore interCore;

    public BICoreWrapper(Object... attributes) throws UnsupportedDataTypeException, BIAmountLimitUnmetException {
        interCore = generateCore(attributes);
    }

    protected BICore generateCore(Object... attributes) throws UnsupportedDataTypeException, BIAmountLimitUnmetException {
        return new BIInterCore(attributes);
    }

    @Override
    public BICore fetchObjectCore() {
        return interCore;
    }

    @Override
    public BIBasicIdentity getID() {
        return interCore.getID();
    }

    @Override
    public Object getAttribute(Integer index) {
        return null;
    }

    @Override
    public void registerAttribute(Object... attributes) throws UnsupportedDataTypeException, BIAmountLimitUnmetException {
        if (attributes != null) {
            for (int i = 0; i < attributes.length; i++) {
                interCore.registerAttribute(attributes[i]);
            }
        }
    }

    @Override
    public void clearCore() {
        interCore.clearCore();
    }

    @Override
    public String getIDValue() {
        return interCore.getIDValue();
    }

    public class BIInterCore extends BIBasicCore {
        public BIInterCore(Object... attributes) throws UnsupportedDataTypeException, BIAmountLimitUnmetException {
            super(attributes);
        }
    }
}