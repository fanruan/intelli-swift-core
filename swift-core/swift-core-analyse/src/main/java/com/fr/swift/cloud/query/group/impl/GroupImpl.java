package com.fr.swift.cloud.query.group.impl;

import com.fr.swift.cloud.query.group.GroupRule;

/**
 * @author anchore
 * @date 2018/1/31
 */
public class GroupImpl<Base, Derive> extends BaseGroup<Base, Derive> {
    public GroupImpl(GroupRule rule) {
        super(rule);
    }
}