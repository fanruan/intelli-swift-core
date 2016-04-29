package com.fr.bi.base;

import com.fr.bi.common.BICoreOperation;
import com.fr.bi.common.BICoreService;
import com.fr.bi.exception.BIAmountLimitUnmetException;
import com.fr.general.ComparatorUtils;

import javax.activation.UnsupportedDataTypeException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Connery on 2015/12/24.
 */
public abstract class BICore implements BICoreOperation {
    public static BICore EMPTY_CORE = new BIBasicCore();
    protected Integer amountLimit = Integer.MAX_VALUE;
    protected BIBasicIdentity value;

    /**
     *
     */
    protected List<Object> coreAttributes;

    public BICore() {
        coreAttributes = new ArrayList<Object>();
        value = new BIBasicIdentity("Empty");
    }

    public BICore(Object... attributes) throws UnsupportedDataTypeException, BIAmountLimitUnmetException {
        this();
        if (attributes != null) {
            for (int i = 0; i < attributes.length; i++) {
                registerAttribute(attributes[i]);
            }
        }

    }

    protected abstract BIBasicIdentity computeValue();


    private void dealWithBICoreType(BICore core) throws UnsupportedDataTypeException, BIAmountLimitUnmetException {
        Iterator<Object> it = core.getCoreAttributes().iterator();
        while (it.hasNext()) {
            registerAttribute(it.next());
        }
    }

    private BIBasicIdentity generateValue(String id) {
        return new BIBasicIdentity(id);
    }

    @Override
    public void clearCore() {
        value = new BIBasicIdentity("Empty");
        coreAttributes.clear();
    }

    public void registerAttribute(Object attribute) throws UnsupportedDataTypeException, BIAmountLimitUnmetException {

        if (attribute != null) {
            if (isAvailable(attribute)) {
                /**
                 * 当前注册的也是一个Core，将Core里面的属性分别注册
                 */
                if (attribute instanceof BICore) {
                    dealWithBICoreType((BICore) attribute);
                }
                /**
                 * 当前注册的也是一个CoreService，将对象的Core的ID进行注册
                 */
                if (attribute instanceof BICoreService) {
                    coreAttributes.add(((BICoreService) attribute).fetchObjectCore().getIDValue());
                } else {
                    coreAttributes.add(attribute);
                }
                value = computeValue();
            }
        }

    }

    protected abstract boolean isTypeSupport(Object value) throws UnsupportedDataTypeException;


    protected boolean fitAmountLimitation(Object value) throws BIAmountLimitUnmetException {
        if (coreAttributes.size() + 1 <= amountLimit) {
            return true;
        } else {
            throw new BIAmountLimitUnmetException("the legal amount of attribute can be registered is " + amountLimit + "," +
                    "can't pass it");
        }
    }

    private boolean isAvailable(Object value) throws UnsupportedDataTypeException, BIAmountLimitUnmetException {
        return isTypeSupport(value) && fitAmountLimitation(value);
    }

    @Override
    public void registerAttribute(Object... attributes) throws UnsupportedDataTypeException, BIAmountLimitUnmetException {
        if (attributes != null) {
            for (int i = 0; i < attributes.length; i++) {
                registerAttribute(attributes[i]);
            }
        }
    }

    @Override
    public Object getAttribute(Integer index) {
        return coreAttributes.get(index);
    }

    public List<Object> getCoreAttributes() {
        return Collections.unmodifiableList(coreAttributes);
    }

    public void setCoreAttributes(List<Object> coreAttributes) {
        this.coreAttributes = coreAttributes;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BICore)) {
            return false;
        }

        BICore biCore = (BICore) o;

        return !(value != null ? !ComparatorUtils.equals(value, biCore.value) : biCore.value != null);

    }

    @Override
    public int hashCode() {
        return value != null ? value.hashCode() : 0;
    }

    @Override
    public BIBasicIdentity getID() {
        return value;
    }

    @Override
    public String getIDValue() {
        return value.getIdentityValue();
    }

    @Override
    public String toString() {
        return "BICore{" +
                "amountLimit=" + amountLimit +
                ", value=" + value +
                ", coreAttributes=" + coreAttributes +
                '}';
    }
}