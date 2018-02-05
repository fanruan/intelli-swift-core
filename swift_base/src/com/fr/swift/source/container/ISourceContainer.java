package com.fr.swift.source.container;

import com.fr.swift.source.ISource;

import java.util.Collection;

/**
 * This class created on 2017-12-19 11:41:59
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public interface ISourceContainer<T extends ISource> {

    T addSource(T source);

    Collection<T> addSources(Collection<T> sources);

    T removeSource(String sourceKey);

    ISource removeSource(T source);

    Collection<T> removeSourcesByKeys(Collection<String> sourceKeys);

    Collection<T> removeSources(Collection<T> sources);

    T getSourceByKey(String sourceKey);

    Collection<T> getAllSources();

    Collection<T> getSourcesByKeys(Collection<String> sourceKeys);

    void clear();

    int size();
}
