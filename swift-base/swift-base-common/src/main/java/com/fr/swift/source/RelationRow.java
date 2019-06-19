package com.fr.swift.source;

import java.util.List;

/**
 * Create by lifan on 2019-06-14 14:55
 */
public interface RelationRow extends Row {
    Object getValue(int index);

    int getSize();

    void addElement(Object object);

    void addAllRowElement(RelationRow relationRow);

    List<Object> getValues();

    void setRow(List<Object> values);

    void removeElement(int index);
}
