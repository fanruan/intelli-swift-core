package com.fr.bi.field.filtervalue.string.rangefilter;

import com.fr.bi.base.BICore;
import com.fr.bi.base.BICoreGenerator;
import com.fr.bi.base.annotation.BICoreField;
import com.fr.bi.common.BICoreService;
import com.fr.general.ComparatorUtils;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by 小灰灰 on 2016/1/6.
 */
public class StringValueSet implements BICoreService,Serializable{
    public static final int CONTAINS = 1;
    private static final long serialVersionUID = 7735531976604940179L;
    @BICoreField
    private Set<String> values = new HashSet<String>();
    @BICoreField
    private int type;

    public StringValueSet() {
    }

    public StringValueSet(Set<String> values, int type) {
        this.values = values;
        this.type = type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Set<String> getValues() {
        return values;
    }

    public boolean isContains(){
        return type == CONTAINS;
    }

    public boolean contains(String s){
        return isContains() ? (values.isEmpty() || values.contains(s))  : !values.contains(s);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof StringValueSet)) {
            return false;
        }

        StringValueSet that = (StringValueSet) o;

        if (type != that.type) {
            return false;
        }
        return ComparatorUtils.equals(values, that.values);

    }

    @Override
    public int hashCode() {
        int result = values.hashCode();
        result = 31 * result + type;
        return result;
    }

    @Override
    public BICore fetchObjectCore() {
        return new BICoreGenerator(this).fetchObjectCore();
    }
}