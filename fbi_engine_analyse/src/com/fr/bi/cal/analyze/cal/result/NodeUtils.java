package com.fr.bi.cal.analyze.cal.result;


import com.fr.bi.stable.operation.sort.comp.ComparatorFacotry;
import com.fr.bi.stable.report.key.TargetGettingKey;
import com.fr.bi.stable.report.result.BINode;
import com.fr.bi.stable.structure.tree.NTree;

/**
 * Created by Hiram on 2015/1/27.
 */
public class NodeUtils {

    public static Double getTopN(BINode node, TargetGettingKey key, int N) {
        Double nLine;
        int count = node.getChildLength();
        N = Math.min(N, count);
        NTree<Double> tree = N < (count << 2) ? new NTree<Double>(ComparatorFacotry.DOUBLE_DESC, N) : new NTree<Double>(ComparatorFacotry.DOUBLE_ASC, count + 1 - N);
        for (int i = 0; i < count; i++) {
            BINode child = node.getChild(i);
            Number v = child.getSummaryValue(key);
            if (v != null) {
                tree.add(v.doubleValue());
            }
        }
        nLine = tree.getLineValue();
        if (nLine == null) {
            nLine = Double.POSITIVE_INFINITY;
        }
        return nLine;
    }


    public static void setSiblingBetweenFirstAndLastChild(BINode root) {
        BINode siblingOlder = null;
        for (int i = 0; i < root.getChildLength(); i++) {
            BINode parent = root.getChild(i);
            if (parent.getChildLength() != 0) {
                setSiblingBetweenFirstAndLastChild(parent);
            }
            if (i + 1 < root.getChildLength()) {
                BINode nextParent = root.getChild(i + 1);
                BINode tmpLastChild = parent.getLastChild();
                BINode tmpFirstChild = nextParent.getFirstChild();

                if (tmpLastChild != null && tmpFirstChild == null) {
                    siblingOlder = tmpLastChild;
                } else if ((tmpLastChild != null && tmpFirstChild != null)) {
                    setSiblingRecursively(tmpLastChild, tmpFirstChild);
                    siblingOlder = tmpFirstChild;
                } else if (tmpLastChild == null && tmpFirstChild != null) {
                    setSiblingRecursively(siblingOlder, tmpFirstChild);
                    siblingOlder = tmpFirstChild;
                }
            }
        }
    }

    private static void setSiblingRecursively(BINode tmpLastChild, BINode tmpFirstChild) {
        while (tmpLastChild != null && tmpFirstChild != null) {
            tmpLastChild.setSibling(tmpFirstChild);
            tmpLastChild = tmpLastChild.getLastChild();
            tmpFirstChild = tmpFirstChild.getFirstChild();
        }

    }
}