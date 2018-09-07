package com.fr.swift.query.group;

import com.fr.swift.query.group.impl.GroupImpl;
import com.fr.swift.query.group.impl.GroupWrapper;

/**
 * @author anchore
 * @date 2018/1/26
 */
public class Groups {
    public static Group newGroup(GroupRule groupRule) {
        return new GroupImpl(groupRule);
    }

    public static <Old, New> Group<Old, New> wrap(Group<Old, New> origin, GroupRule rule) {
        return new GroupWrapper<Old, New>(origin, rule);
    }
}