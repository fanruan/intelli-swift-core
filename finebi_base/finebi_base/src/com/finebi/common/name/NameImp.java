package com.finebi.common.name;

import com.fr.bi.stable.utils.program.BINonValueUtils;
import com.fr.general.ComparatorUtils;

/**
 * This class created on 2017/4/11.
 *
 * @author Connery
 * @since Advanced FineBI Analysis 1.0
 */
public class NameImp implements Name {
    private String selfNameValue;
    private NameProvider parentNameProvider;
    public static final String SEPARATOR = "/";

    public NameImp(String selfNameValue, NameProvider parentName) {
        BINonValueUtils.checkNull(selfNameValue);
        //字段名称可能为空字符串
        checkName(selfNameValue);
        this.selfNameValue = selfNameValue;
        this.parentNameProvider = parentName;
    }

    private void checkName(String selfNameValue) {
        if ("".equals(selfNameValue)) {
            throw BINonValueUtils.beyondControl("The Name value can't be empty");
        }
    }

    public NameImp(String selfNameValue) {
        this(selfNameValue, null);
    }

    @Override
    public String value() {
        return selfNameValue;
    }

    @Override
    public String uniqueValue() {
        StringBuffer sb = new StringBuffer();
        if (isFirstName()) {
            sb.append(selfNameValue);
        } else if (parentNameProvider.getName() != null) {
            sb.append(parentNameProvider.getName().uniqueValue());
            sb.append(SEPARATOR);
            sb.append(selfNameValue);
        } else {
            throw BINonValueUtils.beyondControl("the name:" + selfNameValue + ". Parent's provider has null name");
        }
        return sb.toString();
    }

    private boolean isFirstName() {
        return parentNameProvider == null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof NameImp)) {
            return false;
        }

        NameImp nameImp = (NameImp) o;

        return ComparatorUtils.equals(uniqueValue(), nameImp.uniqueValue());
    }

    @Override
    public int hashCode() {
        int result = selfNameValue != null ? uniqueValue().hashCode() : 0;
        return result;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("uniqueName:[");
        sb.append(uniqueValue()).append("]");
        return sb.toString();
    }
}
