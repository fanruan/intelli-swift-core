package com.fr.swift.query.group.impl;

import com.fr.swift.query.group.GroupRule;

/**
 * @author anchore
 * @date 2018/1/31
 */
public class GroupImpl<Base, Derive> extends BaseGroup<Base, Derive> {
    public GroupImpl(GroupRule rule) {
        super(rule);
    }
}