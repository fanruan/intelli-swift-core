package com.finebi.cube.conf.pack.data;

import com.fr.bi.base.BIName;

/**
 * This class created on 2016/5/23.
 *
 * @author Connery
 * @since 4.0
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