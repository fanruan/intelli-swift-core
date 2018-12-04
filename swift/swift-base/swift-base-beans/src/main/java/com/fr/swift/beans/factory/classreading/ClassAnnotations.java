package com.fr.swift.beans.factory.classreading;

import java.util.List;

/**
 * This class created on 2018/12/3
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class ClassAnnotations {

    private String className;
    private List<String> annotationNames;

    public ClassAnnotations(String className, List<String> annotationNames) {
        this.className = className;
        this.annotationNames = annotationNames;
    }

    public String getClassName() {
        return className;
    }

    public List<String> getAnnotationNames() {
        return annotationNames;
    }
}
