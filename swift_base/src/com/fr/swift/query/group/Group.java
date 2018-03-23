package com.fr.swift.query.group;

/**
 * @author pony
 * @date 2017/12/11
 */
public interface Group<Base, Derive> {
    GroupOperator<Base, Derive> getGroupOperator();

    GroupType getGroupType();
}