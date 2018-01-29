package com.fr.swift.conf.business.container;

import java.util.List;

/**
 * This class created on 2018-1-29 11:42:55
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public abstract class AbstractResourceContainer<T> implements ResourceContainer<T> {

    protected List<T> resourceList;

    @Override
    public void saveResources(List<T> resourceList) {
        this.resourceList = resourceList;
    }

    @Override
    public List<T> getResources() {
        return resourceList;
    }
}
