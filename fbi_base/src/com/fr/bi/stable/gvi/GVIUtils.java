package com.fr.bi.stable.gvi;

import com.fr.bi.stable.gvi.array.ICubeTableIndexReader;
import com.fr.bi.stable.gvi.traversal.SingleRowTraversalAction;

/**
 * Created by GUY on 2015/3/11.
 */
public class GVIUtils {

    public static GroupValueIndex getTableLinkedOrGVI(GroupValueIndex currentIndex, final ICubeTableIndexReader reader) {
        if (currentIndex != null) {

            final GroupValueIndex sgvi = new RoaringGroupValueIndex();
            currentIndex.Traversal(new SingleRowTraversalAction() {
                @Override
                public void actionPerformed(int rowIndices) {
                    sgvi.or(reader.get(rowIndices));
                }
            });
            return sgvi;
        }
        return null;
    }

    public static GroupValueIndex AND(GroupValueIndex a, GroupValueIndex b) {
        if (a == null) {
            return b;
        }
        if (b == null) {
            return a;
        }
        return a.AND(b);
    }

    public static GroupValueIndex OR(GroupValueIndex a, GroupValueIndex b) {
        if (a == null) {
            return b;
        }
        if (b == null) {
            return a;
        }
        return a.OR(b);
    }
}