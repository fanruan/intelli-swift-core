package com.fr.bi.cal.analyze.cal.sssecret;

public interface NodeDimensionIterator {

    void moveNext();

    GroupConnectionValue next();

    void pageEnd();

    boolean hasPrevious();

    boolean hasNext();

    int getPageIndex();
}