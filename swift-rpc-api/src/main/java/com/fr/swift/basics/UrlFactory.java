package com.fr.swift.basics;

import com.fr.swift.basic.URL;

/**
 * This class created on 2018/6/12
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public interface UrlFactory<T> {

    URL getURL(T t);

}
