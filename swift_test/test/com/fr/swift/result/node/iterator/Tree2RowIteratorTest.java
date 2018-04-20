package com.fr.swift.result.node.iterator;

import com.fr.swift.util.function.Supplier;
import junit.framework.TestCase;
import org.junit.Before;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Lyon on 2018/4/19.
 */
public class Tree2RowIteratorTest extends TestCase {

    private Tree root;

    @Before
    public void setUp() throws Exception {
        // {key, {T1, T2, ...}}
        //  {0, {
        //          {1, {0, 3}},
        //          {2, {}},
        //          {3, {3, 5}
        //      }
        //  }
        // rows = [[0, 1, 0], [0, 1, 3], [0, 2, null], [0, 3, 3], [0, 3, 5]]
        root = new Tree(0);
        List<Tree> level1 = Arrays.asList(new Tree[] {new Tree(1), new Tree(2), new Tree(3)});
        root.setChildren(level1);
        level1.get(0).setChildren(Arrays.asList(new Tree[] {new Tree(0), new Tree(3)}));
        level1.get(2).setChildren(Arrays.asList(new Tree[] {new Tree(3), new Tree(5)}));
    }

    public void test() {
        Iterator<List<Integer>> iterator = new Tree2RowIterator<Integer, Tree>(3, root);
        assertTrue(Arrays.equals(iterator.next().toArray(), new Integer[] {0, 1, 0}));
        assertTrue(Arrays.equals(iterator.next().toArray(), new Integer[] {0, 1, 3}));
        assertTrue(Arrays.equals(iterator.next().toArray(), new Integer[] {0, 2, null}));
        assertTrue(Arrays.equals(iterator.next().toArray(), new Integer[] {0, 3, 3}));
        assertTrue(Arrays.equals(iterator.next().toArray(), new Integer[] {0, 3, 5}));
    }

    private static class Tree implements Supplier<Integer>, Iterable<Tree> {
        private int value;
        List<Tree> children = new ArrayList<>(0);

        public Tree(int value) {
            this.value = value;
        }

        public void setChildren(List<Tree> children) {
            this.children = children;
        }

        @Override
        public Integer get() {
            return value;
        }

        @Override
        public Iterator<Tree> iterator() {
            return children.iterator();
        }
    }
}
