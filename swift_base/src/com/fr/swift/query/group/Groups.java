package com.fr.swift.query.group;

import com.fr.swift.query.group.impl.GroupImpl;

/**
 * @author anchore
 * @date 2018/1/26
 */
public class Groups {
    public static <Base, Derive> Group newGroup(GroupRule<Base, Derive> groupRule) {
        return new GroupImpl<Base, Derive>(groupRule);
    }
}