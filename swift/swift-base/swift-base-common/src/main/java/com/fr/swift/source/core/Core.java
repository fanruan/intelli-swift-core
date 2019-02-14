package com.fr.swift.source.core;

import com.fr.swift.exception.AmountLimitUnmetException;

import javax.activation.UnsupportedDataTypeException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Connery on 2015/12/24.
 */
public abstract class Core implements CoreOperation {
    public static Core EMPTY_CORE = new BasicCore();
    private static final String EMPTY_VALUE = "Empty";
    protected Integer amountLimit = Integer.MAX_VALUE;
    protected String value;

    /**
     *
     */
    protected List<Object> coreAttributes;

    public Core() {
        coreAttributes = new ArrayList<Object>();
        value = EMPTY_VALUE;
    }

    public Core(Object... attributes) throws UnsupportedDataTypeException, AmountLimitUnmetException {
        this();
        if (attributes != null) {
            for (int i = 0; i < attributes.length; i++) {
                registerAttribute(attributes[i]);
            }
        }

    }

    protected abstract String computeValue();


    private void dealWithBICoreType(Core core) throws UnsupportedDataTypeException, AmountLimitUnmetException {
        Iterator<Object> it = core.getCoreAttributes().iterator();
        while (it.hasNext()) {
            registerAttribute(it.next());
        }
    }


    @Override
    public void clearCore() {
        value = EMPTY_VALUE;
        coreAttributes.clear();
    }

    public void registerAttribute(Object attribute) throws UnsupportedDataTypeException, AmountLimitUnmetException {

        if (attribute != null) {
            if (isAvailable(attribute)) {
                /**
                 * 当前注册的也是一个Core，将Core里面的属性分别注册
                 */
                if (attribute instanceof Core) {
                    dealWithBICoreType((Core) attribute);
                }
                /**
                 * 当前注册的也是一个CoreService，将对象的Core的ID进行注册
                 */
                if (attribute instanceof CoreService) {
                    coreAttributes.add(((CoreService) attribute).fetchObjectCore().getValue());
                } else {
                    coreAttributes.add(attribute);
                }
                value = computeValue();
            }
        }

    }

    protected abstract boolean isTypeSupport(Object value) throws UnsupportedDataTypeException;


    protected boolean fitAmountLimitation(Object value) throws AmountLimitUnmetException {
        if (coreAttributes.size() + 1 <= amountLimit) {
            return true;
        } else {
            throw new AmountLimitUnmetException("the legal amount of attribute can be registered is " + amountLimit + "," +
                    "can't pass it");
        }
    }

    private boolean isAvailable(Object value) throws UnsupportedDataTypeException, AmountLimitUnmetException {
        return isTypeSupport(value) && fitAmountLimitation(value);
    }

    @Override
    public void registerAttribute(Object... attributes) throws UnsupportedDataTypeException, AmountLimitUnmetException {
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
        if (!(o instanceof Core)) {
            return false;
        }

        Core biCore = (Core) o;

        return !(value != null ? !value.equals(biCore.value) : biCore.value != null);

    }

    @Override
    public int hashCode() {
        return value != null ? value.hashCode() : 0;
    }


    @Override
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Core{" +
                "amountLimit=" + amountLimit +
                ", value=" + value +
                ", coreAttributes=" + coreAttributes +
                '}';
    }
}