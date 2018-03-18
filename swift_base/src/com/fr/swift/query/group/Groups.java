package com.fr.swift.query.group;

import com.fr.swift.query.group.impl.GroupImpl;
import com.fr.swift.query.group.impl.NoGroupRule;

/**
 * @author anchore
 * @date 2018/1/26
 */
public class Groups {
    public static Group newGroup(GroupRule groupRule) {
        return new GroupImpl(groupRule);
    }

    public static Group newNoGroup() {
        return newGroup(new NoGroupRule());
    }
}
