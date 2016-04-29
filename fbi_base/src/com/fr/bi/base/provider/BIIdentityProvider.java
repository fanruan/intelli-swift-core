package com.fr.bi.base.provider;

/**
 * Created by GUY on 2015/4/25.
 */
public interface BIIdentityProvider<T> {
    T getIdentityValue();

    void setIdentityValue(T identity);
}