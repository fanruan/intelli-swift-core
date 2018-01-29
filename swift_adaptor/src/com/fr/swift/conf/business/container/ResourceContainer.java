package com.fr.swift.conf.business.container;

import java.util.List;

/**
 * This class created on 2018-1-29 11:34:44
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public interface ResourceContainer<T> {
    void saveResources(List<T> resourceList);

    List<T> getResources();
}
