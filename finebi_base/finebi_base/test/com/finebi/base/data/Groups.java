package com.finebi.base.data;


import com.finebi.base.data.xml.item.XmlListItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by andrew_asa on 2017/10/11.
 */
public class Groups {

    @XmlListItem(clazz = Group.class)
    List<Group> groups = new ArrayList<Group>();

    public Groups() {

    }

    public void addGroup(Group group) {

        groups.add(group);
    }

    public List<Group> getGroups() {

        return groups;
    }
}
