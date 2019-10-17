package com.fr.swift.structure.iterator;

import com.fr.swift.bitmap.traversal.BreakTraversalAction;
import com.fr.swift.bitmap.traversal.TraversalAction;
import com.fr.swift.structure.array.IntListFactory;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lyon on 2017/12/5.
 */
public class IntListRowTraversalTest extends TestCase {

    public void testTraversal() {
        RowTraversal traversal = new IntListRowTraversal(IntListFactory.createRangeIntList(0, 10));
        final List<Integer> list = new ArrayList<Integer>();
        traversal.traversal(new TraversalAction() {
            @Override
            public void actionPerformed(int row) {
                list.add(row);
            }
        });
        assertEquals(11, list.size());
    }

    public void testBreakableTraversal() {
        RowTraversal traversal = new IntListRowTraversal(IntListFactory.createRangeIntList(1, 10));
        final List<Integer> list = new ArrayList<Integer>();
        traversal.breakableTraversal(new BreakTraversalAction() {
            @Override
            public boolean actionPerformed(int row) {
                list.add(row);
                return row % 6 == 0;
            }
        });
        assertEquals(6, list.size());
    }
}
