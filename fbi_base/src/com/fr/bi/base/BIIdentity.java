package com.fr.bi.base;

import com.fr.bi.base.provider.BIIdentityProvider;
import com.fr.general.ComparatorUtils;
import com.fr.stable.FCloneable;

/**
 * 对象判断相等都是基于ID来判断，
 * 特殊情况通过BICore来实现。
 * Created by Connery on 2015/12/11.
 */
public class BIIdentity<T> implements BIIdentityProvider<T>, FCloneable {
    protected T identity;

    public BIIdentity(T id) {
        this.identity = id;
    }

    public T getIdentity() {
        return identity;
    }

    public void setIdentity(T identity) {
        this.identity = identity;
    }

    @Override
    public T getIdentityValue() {
        return getIdentity();
    }

    @Override
    public void setIdentityValue(T identity) {
        setIdentity(identity);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BIIdentity)) {
            return false;
        }

        BIIdentity BIIdentity = (BIIdentity) o;

        if (!ComparatorUtils.equals(identity, BIIdentity.identity)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return identity != null ? identity.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "BIId{" +
                "identity='" + identity + '\'' +
                '}';
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

}