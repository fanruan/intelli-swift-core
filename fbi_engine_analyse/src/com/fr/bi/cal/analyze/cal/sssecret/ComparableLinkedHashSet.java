package com.fr.bi.cal.analyze.cal.sssecret;

import com.fr.stable.StringUtils;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;

/**
 * Created by 小灰灰 on 2015/3/20.
 */
public class ComparableLinkedHashSet extends LinkedHashSet implements Comparable {

    @Override
    public int compareTo(Object o) {
        if (o != null) {
            if (o instanceof ComparableLinkedHashSet) {
                this.addAll((Collection) o);
                ((Collection) o).addAll(this);
            }
            return 0;
        }
        return 1;
    }

    @Override
    public String toString() {
        String dataString = "";
        Iterator dit = this.iterator();
        boolean isFrist = true;
        while (dit.hasNext()) {
            if (!isFrist) {
                dataString = dataString + ",";
            }
            dataString = dataString + dit.next();
            isFrist = false;
        }
        return dataString;
    }

    @Override
    public boolean add(Object ob) {
        if (ob == null) {
            return false;
        }
        if (StringUtils.isEmpty(ob.toString())) {
            return false;
        }
        return super.add(ob);
    }
}