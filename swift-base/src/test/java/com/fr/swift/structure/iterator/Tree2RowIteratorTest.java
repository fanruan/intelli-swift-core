package com.fr.swift.structure.iterator;

import junit.framework.TestCase;
import org.junit.Ignore;

/**
 * Created by Lyon on 2018/4/19.
 */
@Ignore
public class Tree2RowIteratorTest extends TestCase {

//    private Tree root;
//
//    @Before
//    public void setUp() throws Exception {
//        // {key, {T1, T2, ...}}
//        //  {0, {
//        //          {1, {0, 3}},
//        //          {2, {}},
//        //          {3, {3, 5}
//        //      }
//        //  }
//        // rows = [[0, 1, 0], [0, 1, 3], [0, 2, null], [0, 3, 3], [0, 3, 5]]
//        root = new Tree(0);
//        List<Tree> level1 = Arrays.asList(new Tree(1), new Tree(2), new Tree(3));
//        root.setChildren(level1);
//        level1.get(0).setChildren(Arrays.asList(new Tree(0), new Tree(3)));
//        level1.get(2).setChildren(Arrays.asList(new Tree(3), new Tree(5)));
//    }
//
//    public void test() {
//        Iterator<List<Tree>> iterator = new Tree2RowIterator<Tree>(3, Arrays.asList(root).iterator(), new Function<Tree, Iterator<Tree>>() {
//            @Override
//            public Iterator<Tree> apply(Tree p) {
//                return p.iterator();
//            }
//        });
//        assertTrue(Arrays.equals(list2Array(iterator.next()), new Integer[] {0, 1, 0}));
//        assertTrue(Arrays.equals(list2Array(iterator.next()), new Integer[] {0, 1, 3}));
//        assertTrue(Arrays.equals(list2Array(iterator.next()), new Integer[] {0, 2, null}));
//        assertTrue(Arrays.equals(list2Array(iterator.next()), new Integer[] {0, 3, 3}));
//        assertTrue(Arrays.equals(list2Array(iterator.next()), new Integer[] {0, 3, 5}));
//    }
//
//    private static Integer[] list2Array(List<Tree> trees) {
//        Integer[] integers = new Integer[trees.size()];
//        for (int i = 0; i < trees.size(); i++) {
//            integers[i] = trees.get(i) == null ? null : trees.get(i).getValue();
//        }
//        return integers;
//    }
//
//    private static class Tree implements Iterable<Tree> {
//        private int value;
//        List<Tree> children = new ArrayList<>(0);
//
//        public Tree(int value) {
//            this.value = value;
//        }
//
//        public void setChildren(List<Tree> children) {
//            this.children = children;
//        }
//
//        public Integer getValue() {
//            return value;
//        }
//
//        @Override
//        public Iterator<Tree> iterator() {
//            return children.iterator();
//        }
//    }
}
