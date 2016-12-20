package com.fr.bi.stable.gvi;

import com.fr.bi.stable.gvi.array.ICubeTableIndexReader;
import com.fr.bi.stable.gvi.traversal.SingleRowTraversalAction;

/**
 * Created by GUY on 2015/3/11.
 */
public class GVIUtils {

    public static GroupValueIndex getTableLinkedOrGVI(GroupValueIndex currentIndex, final ICubeTableIndexReader reader) {
        if (currentIndex != null) {

            final GroupValueIndexOrHelper helper = new GroupValueIndexOrHelper();
            currentIndex.Traversal(new SingleRowTraversalAction() {
                @Override
                public void actionPerformed(int rowIndices) {
                    helper.add(reader.get(rowIndices));
                }
            });
            return helper.compute();
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

    public static boolean isAllShowRoaringGroupValueIndex(GroupValueIndex valueIndex) {
        return ((AbstractGroupValueIndex)valueIndex).getType() == GroupValueIndexCreator.ROARING_INDEX_All_SHOW.getType();
    }

    public static boolean isAllEmptyRoaringGroupValueIndex(GroupValueIndex valueIndex) {
        return valueIndex.getRowsCountWithData() == 0;
    }

    public static boolean isIDGroupValueIndex(GroupValueIndex valueIndex) {
        return ((AbstractGroupValueIndex)valueIndex).getType() == GroupValueIndexCreator.ROARING_INDEX_ID.getType();
    }
}