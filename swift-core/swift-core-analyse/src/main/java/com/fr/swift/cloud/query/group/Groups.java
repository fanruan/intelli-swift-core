package com.fr.swift.cloud.query.group;

import com.fr.swift.cloud.query.group.impl.GroupImpl;
import com.fr.swift.cloud.query.group.impl.GroupWrapper;

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