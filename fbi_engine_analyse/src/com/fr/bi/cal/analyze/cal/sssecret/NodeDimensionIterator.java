package com.fr.bi.cal.analyze.cal.sssecret;

public interface NodeDimensionIterator {

    public void moveNext();

    public GroupConnectionValue next();


    public void PageEnd();


    public boolean hasPrevious();

    public boolean hasNext();

    public int getPageIndex();
}