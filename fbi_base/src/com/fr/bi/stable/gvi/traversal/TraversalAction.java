package com.fr.bi.stable.gvi.traversal;

import com.fr.bi.common.inter.Traversal;
import com.fr.bi.stable.utils.code.BILogger;

public interface TraversalAction extends Traversal<int[]> {

    public TraversalAction PRINT = new TraversalAction() {

        @Override
        public void actionPerformed(int[] rowIndices) {
            BILogger.getLogger().info(rowIndices.toString());
        }
    };
}