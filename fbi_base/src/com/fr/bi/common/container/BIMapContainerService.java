package com.fr.bi.common.container;

import com.fr.bi.exception.BIKeyAbsentException;
import com.fr.bi.exception.BIKeyDuplicateException;

/**
 * Key_Value的容器
 * Created by Connery on 2015/12/7.
 */
public interface BIMapContainerService<K, V> {
    V getValue(K key) throws BIKeyAbsentException;

    void register(K key, V value) throws BIKeyDuplicateException;

    Boolean containsKey(K key);

    void remove(K key) throws BIKeyAbsentException;

    void clear();
}