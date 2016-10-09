package com.fr.bi.stable.gvi.traversal;

import com.fr.bi.common.inter.Traversal;
import com.finebi.cube.common.log.BILoggerFactory;

public interface TraversalAction extends Traversal<int[]> {

    public TraversalAction PRINT = new TraversalAction() {

        @Override
        public void actionPerformed(int[] rowIndices) {
            BILoggerFactory.getLogger().info(rowIndices.toString());
        }
    };
}