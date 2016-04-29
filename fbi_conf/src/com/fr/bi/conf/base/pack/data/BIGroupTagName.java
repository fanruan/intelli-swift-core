package com.fr.bi.conf.base.pack.data;

import com.fr.bi.base.BIName;

/**
 * Created by Connery on 2015/12/28.
 */
public class BIGroupTagName extends BIName implements Comparable,Cloneable {
    public BIGroupTagName(String name) {
        super(name);
    }

    public BIGroupTagName() {
        super();
    }

    public static BIGroupTagName generateBIGroupName(String groupName) {
        return new BIGroupTagName(groupName);
    }

    @Override
    public int compareTo(Object o) {
        return name.compareTo(((BIGroupTagName) o).getValue());
    }


}