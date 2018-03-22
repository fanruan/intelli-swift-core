package com.fr.swift.query.group;

import com.fr.swift.query.group.impl.GroupImpl;
import com.fr.swift.query.group.impl.NoGroupRule;

/**
 * @author anchore
 * @date 2018/1/26
 */
public class Groups {
    public static <Base, Derive> Group newGroup(GroupRule<Base, Derive> groupRule) {
        return new GroupImpl<Base, Derive>(groupRule);
    }

    public static <Base> Group<Base, Base> newNoGroup() {
        return new GroupImpl<Base, Base>(new NoGroupRule<Base>());
    }
}