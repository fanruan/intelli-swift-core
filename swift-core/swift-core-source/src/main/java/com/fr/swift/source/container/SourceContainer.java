package com.fr.swift.source.container;

import com.fr.swift.source.Source;

import java.util.Collection;

/**
 * This class created on 2017-12-19 11:41:59
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public interface SourceContainer<T extends Source> {

    T addSource(T source);

    Collection<T> addSources(Collection<T> sources);

    T removeSource(String sourceKey);

    Source removeSource(T source);

    Collection<T> removeSourcesByKeys(Collection<String> sourceKeys);

    Collection<T> removeSources(Collection<T> sources);

    T getSourceByKey(String sourceKey);

    Collection<T> getAllSources();

    Collection<T> getSourcesByKeys(Collection<String> sourceKeys);

    void clear();

    int size();
}
