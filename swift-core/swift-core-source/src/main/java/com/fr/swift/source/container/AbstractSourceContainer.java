package com.fr.swift.source.container;

import com.fr.swift.source.Source;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class created on 2017-12-19 11:51:43
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public abstract class AbstractSourceContainer<T extends Source> implements SourceContainer<T> {

    protected Map<String, T> sourceMap;

    public AbstractSourceContainer() {
        this.sourceMap = new ConcurrentHashMap<String, T>();
    }

    @Override
    public T addSource(T source) {
        return sourceMap.put(source.getSourceKey().getId(), source);
    }

    @Override
    public Collection<T> addSources(Collection<T> sources) {
        List<T> addSourceList = new ArrayList<T>(sources);
        for (T source : sources) {
            this.addSource(source);
        }
        return addSourceList;
    }

    @Override
    public T removeSource(String sourceKey) {
        return sourceMap.remove(sourceKey);
    }

    @Override
    public T removeSource(T source) {
        return this.removeSource(source.getSourceKey().getId());
    }

    @Override
    public Collection<T> removeSourcesByKeys(Collection<String> sourceKeys) {
        List<T> removeSourceList = new ArrayList<T>(sourceKeys.size());
        for (String sourceKey : sourceKeys) {
            T removedSource = this.removeSource(sourceKey);
            if (removedSource != null) {
                removeSourceList.add(removedSource);
            }
        }
        return removeSourceList;
    }

    @Override
    public Collection<T> removeSources(Collection<T> sources) {
        List<T> removeSourceList = new ArrayList<T>(sources.size());
        for (T source : sources) {
            T removedSource = this.removeSource(source.getSourceKey().getId());
            if (removedSource != null) {
                removeSourceList.add(removedSource);
            }
        }
        return removeSourceList;
    }

    @Override
    public T getSourceByKey(String sourceKey) {
        return sourceMap.get(sourceKey);
    }

    @Override
    public Collection<T> getAllSources() {
        return new ArrayList<T>(sourceMap.values());
    }

    @Override
    public Collection<T> getSourcesByKeys(Collection<String> sourceKeys) {
        List<T> sourceList = new ArrayList<T>(sourceKeys.size());
        for (String sourceKey : sourceKeys) {
            T source = this.getSourceByKey(sourceKey);
            if (source != null) {
                sourceList.add(source);
            }
        }
        return sourceList;
    }

    @Override
    public void clear() {
        sourceMap.clear();
    }

    @Override
    public int size() {
        return sourceMap.size();
    }
}
