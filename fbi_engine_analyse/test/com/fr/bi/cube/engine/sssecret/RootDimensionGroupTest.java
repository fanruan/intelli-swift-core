package com.fr.bi.cube.engine.sssecret;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Connery on 2014/12/16.
 */
public class RootDimensionGroupTest extends TestCase {
    public void testTravelToPositionPage() {
        int[] shrinkPos = new int[]{1, 2, 3};

        List<int[]> pageIndex_1 = new ArrayList<int[]>();
        pageIndex_1.add(new int[]{1, 2});
        boolean exception = false;
        try {
            int i = RootDimensionGroup.findPageIndexDichotomy(shrinkPos, pageIndex_1, 0, pageIndex_1.size() - 1);
            assertEquals(i, 0);
        } catch (Exception ex) {
            exception = true;
        }
        assertTrue(exception);
    }

    public void test_one_small() {
        int[] shrinkPos = new int[]{1, 2, 3};

        List<int[]> pageIndex_1 = new ArrayList<int[]>();
        pageIndex_1.add(new int[]{1, 2});
        boolean exception = false;
        try {
            int i = RootDimensionGroup.findPageIndexDichotomy(shrinkPos, pageIndex_1, 0, pageIndex_1.size() - 1);
            assertEquals(i, 0);
        } catch (Exception ex) {
            exception = true;
        }
        assertTrue(exception);

    }

    public void test_one_big() {
        int[] shrinkPos = new int[]{1, 2, 3};
        boolean exception = false;
        List<int[]> pageIndex = new ArrayList<int[]>();
        pageIndex.add(new int[]{1, 2, 4});
        try {
            int i = RootDimensionGroup.findPageIndexDichotomy(shrinkPos, pageIndex, 0, pageIndex.size() - 1);
            assertEquals(i, 0);
        } catch (Exception ex) {
            exception = true;
        }
        assertFalse(exception);

    }

    public void test_two_small() {
        int[] shrinkPos = new int[]{1, 2, 3};
        boolean exception = false;
        List<int[]> pageIndex_3 = new ArrayList<int[]>();
        pageIndex_3.add(new int[]{1, 2});
        pageIndex_3.add(new int[]{1, 1});
        try {
            int i = RootDimensionGroup.findPageIndexDichotomy(shrinkPos, pageIndex_3, 0, pageIndex_3.size() - 1);
        } catch (Exception ex) {
            exception = true;
        }
        assertTrue(exception);
    }

    public void test_two_big() {
        int[] shrinkPos = new int[]{1, 2, 3};
        boolean exception = false;
        List<int[]> pageIndex = new ArrayList<int[]>();
        pageIndex.add(new int[]{1, 2, 4});
        pageIndex.add(new int[]{1, 2, 5});

        try {
            int i = RootDimensionGroup.findPageIndexDichotomy(shrinkPos, pageIndex, 0, pageIndex.size() - 1);
            assertEquals(i, 0);
        } catch (Exception ex) {
            exception = true;
        }
        assertFalse(exception);
    }

    public void test_three_normal_1() {
        int[] shrinkPos = new int[]{1, 2, 3};
        boolean exception = false;
        List<int[]> pageIndex = new ArrayList<int[]>();

        pageIndex.add(new int[]{1, 1});
        pageIndex.add(new int[]{1, 2});
        pageIndex.add(new int[]{1, 4});


        try {
            int i = RootDimensionGroup.findPageIndexDichotomy(shrinkPos, pageIndex, 0, pageIndex.size() - 1);
            assertEquals(i, 2);
        } catch (Exception ex) {
            exception = true;
        }
        assertFalse(exception);
    }

    public void test_three_big() {
        int[] shrinkPos = new int[]{1, 2, 3};
        boolean exception = false;
        List<int[]> pageIndex = new ArrayList<int[]>();
        pageIndex.add(new int[]{1, 2, 4});
        pageIndex.add(new int[]{1, 2, 5});
        pageIndex.add(new int[]{1, 2, 6});

        try {
            int i = RootDimensionGroup.findPageIndexDichotomy(shrinkPos, pageIndex, 0, pageIndex.size() - 1);
            assertEquals(i, 0);
        } catch (Exception ex) {
            exception = true;
        }
        assertFalse(exception);
    }

    public void test_two_normal() {
        int[] shrinkPos = new int[]{1, 2, 3};
        boolean exception = false;
        List<int[]> pageIndex = new ArrayList<int[]>();
        pageIndex.add(new int[]{1, 2, 2});
        pageIndex.add(new int[]{1, 2, 5});

        try {
            int i = RootDimensionGroup.findPageIndexDichotomy(shrinkPos, pageIndex, 0, pageIndex.size() - 1);
            assertEquals(i, 1);
        } catch (Exception ex) {
            exception = true;
        }
        assertFalse(exception);
    }

    public void test_three_normal() {
        int[] shrinkPos = new int[]{1, 2, 3};
        boolean exception = false;
        List<int[]> pageIndex = new ArrayList<int[]>();
        pageIndex.add(new int[]{1, 2, 2});
        pageIndex.add(new int[]{1, 2, 5});
        pageIndex.add(new int[]{1, 2, 6});

        try {
            int i = RootDimensionGroup.findPageIndexDichotomy(shrinkPos, pageIndex, 0, pageIndex.size() - 1);
            assertEquals(i, 1);
        } catch (Exception ex) {
            exception = true;
        }
        assertFalse(exception);
    }

    public void test_three_normal_2() {
        int[] shrinkPos = new int[]{1, 2, 3};
        boolean exception = false;
        List<int[]> pageIndex = new ArrayList<int[]>();
        pageIndex.add(new int[]{1, 2, 1});

        pageIndex.add(new int[]{1, 2, 2});
        pageIndex.add(new int[]{1, 2, 6});

        try {
            int i = RootDimensionGroup.findPageIndexDichotomy(shrinkPos, pageIndex, 0, pageIndex.size() - 1);
            assertEquals(i, 2);
        } catch (Exception ex) {
            exception = true;
        }
        assertFalse(exception);
    }

    public void test_four_normal_1() {
        int[] shrinkPos = new int[]{1, 2, 3};
        boolean exception = false;

        List<int[]> pageIndex = new ArrayList<int[]>();
        pageIndex.add(new int[]{1, 2, 2});
        pageIndex.add(new int[]{1, 2, 4});
        pageIndex.add(new int[]{1, 2, 5});

        pageIndex.add(new int[]{1, 2, 6});
        try {
            int i = RootDimensionGroup.findPageIndexDichotomy(shrinkPos, pageIndex, 0, pageIndex.size() - 1);
            assertEquals(i, 1);
        } catch (Exception ex) {
            exception = true;
        }
        assertFalse(exception);
    }

    public void test_four_normal_2() {
        int[] shrinkPos = new int[]{1, 2, 3};
        boolean exception = false;
        List<int[]> pageIndex = new ArrayList<int[]>();
        pageIndex.add(new int[]{1, 2, 1});

        pageIndex.add(new int[]{1, 2, 2});
        pageIndex.add(new int[]{1, 2, 5});
        pageIndex.add(new int[]{1, 2, 6});
        try {
            int i = RootDimensionGroup.findPageIndexDichotomy(shrinkPos, pageIndex, 0, pageIndex.size() - 1);
            assertEquals(i, 2);
        } catch (Exception ex) {
            exception = true;
        }
        assertFalse(exception);
    }

    public void test_four_normal_3() {
        int[] shrinkPos = new int[]{1, 2, 3};
        boolean exception = false;
        List<int[]> pageIndex = new ArrayList<int[]>();
        pageIndex.add(new int[]{1, 2,});

        pageIndex.add(new int[]{1, 2, 1});
        pageIndex.add(new int[]{1, 2, 2});
        pageIndex.add(new int[]{1, 2, 6});
        try {
            int i = RootDimensionGroup.findPageIndexDichotomy(shrinkPos, pageIndex, 0, pageIndex.size() - 1);
            assertEquals(i, 3);
        } catch (Exception ex) {
            exception = true;
        }
        assertFalse(exception);
    }

    public void test_four_big() {
        int[] shrinkPos = new int[]{1, 2, 3};
        boolean exception = false;
        List<int[]> pageIndex = new ArrayList<int[]>();
        pageIndex.add(new int[]{1, 2, 4});
        pageIndex.add(new int[]{1, 2, 5});
        pageIndex.add(new int[]{1, 2, 6});
        pageIndex.add(new int[]{1, 2, 7});

        try {
            int i = RootDimensionGroup.findPageIndexDichotomy(shrinkPos, pageIndex, 0, pageIndex.size() - 1);
            assertEquals(i, 0);
        } catch (Exception ex) {
            exception = true;
        }
        assertFalse(exception);
    }

    public void test_four_small() {
        int[] shrinkPos = new int[]{1, 2, 3};
        boolean exception = false;
        List<int[]> pageIndex = new ArrayList<int[]>();
        pageIndex.add(new int[]{1, 1, 4});

        pageIndex.add(new int[]{1, 1, 5});
        pageIndex.add(new int[]{1, 1, 6});
        pageIndex.add(new int[]{1, 1, 7});

        try {
            int i = RootDimensionGroup.findPageIndexDichotomy(shrinkPos, pageIndex, 0, pageIndex.size() - 1);
//            assertEquals(i, 3);
        } catch (Exception ex) {
            exception = true;
        }
        assertTrue(exception);
    }

    public void test_five_normal_1() {
        int[] shrinkPos = new int[]{1, 2, 3};
        boolean exception = false;
        List<int[]> pageIndex = new ArrayList<int[]>();
        pageIndex.add(new int[]{1, 1, 2});
        pageIndex.add(new int[]{1, 1, 4});
        pageIndex.add(new int[]{1, 1, 5});

        pageIndex.add(new int[]{1, 1, 6});
        pageIndex.add(new int[]{1, 3, 7});

        try {
            int i = RootDimensionGroup.findPageIndexDichotomy(shrinkPos, pageIndex, 0, pageIndex.size() - 1);
            assertEquals(i, 4);
        } catch (Exception ex) {
            exception = true;
        }
        assertFalse(exception);
    }

    public void test_five_normal_2() {
        int[] shrinkPos = new int[]{1, 2, 3};
        boolean exception = false;
        List<int[]> pageIndex = new ArrayList<int[]>();
        pageIndex.add(new int[]{1, 1, 2});
        pageIndex.add(new int[]{1, 1, 4});
        pageIndex.add(new int[]{1, 1, 5});

        pageIndex.add(new int[]{1, 3, 6});
        pageIndex.add(new int[]{1, 3, 7});

        try {
            int i = RootDimensionGroup.findPageIndexDichotomy(shrinkPos, pageIndex, 0, pageIndex.size() - 1);
            assertEquals(i, 3);
        } catch (Exception ex) {
            exception = true;
        }
        assertFalse(exception);
    }

    public void test_five_normal_3() {
        int[] shrinkPos = new int[]{1, 2, 3};
        boolean exception = false;
        List<int[]> pageIndex = new ArrayList<int[]>();
        pageIndex.add(new int[]{1, 1, 2});
        pageIndex.add(new int[]{1, 1, 4});
        pageIndex.add(new int[]{1, 3, 5});

        pageIndex.add(new int[]{1, 3, 6});

        pageIndex.add(new int[]{1, 3, 7});
        try {
            int i = RootDimensionGroup.findPageIndexDichotomy(shrinkPos, pageIndex, 0, pageIndex.size() - 1);
            assertEquals(i, 2);
        } catch (Exception ex) {
            exception = true;
        }
        assertFalse(exception);
    }

    public void test_five_normal_4() {
        int[] shrinkPos = new int[]{1, 2, 3};
        boolean exception = false;
        List<int[]> pageIndex = new ArrayList<int[]>();
        pageIndex.add(new int[]{1, 1, 2});
        pageIndex.add(new int[]{1, 3, 4});
        pageIndex.add(new int[]{1, 3, 5});

        pageIndex.add(new int[]{1, 3, 6});
        pageIndex.add(new int[]{1, 3, 7});

        try {
            int i = RootDimensionGroup.findPageIndexDichotomy(shrinkPos, pageIndex, 0, pageIndex.size() - 1);
            assertEquals(i, 1);
        } catch (Exception ex) {
            exception = true;
        }
        assertFalse(exception);
    }

    public void test_five_normal_6() {
        int[] shrinkPos = new int[]{1, 2, 3};
        boolean exception = false;
        List<int[]> pageIndex = new ArrayList<int[]>();
        pageIndex.add(new int[]{1, 1, 2});
        pageIndex.add(new int[]{1, 1, 3});
        pageIndex.add(new int[]{1, 1, 5});

        pageIndex.add(new int[]{1, 1, 6});
        pageIndex.add(new int[]{1, 2, 4});

        try {
            int i = RootDimensionGroup.findPageIndexDichotomy(shrinkPos, pageIndex, 0, pageIndex.size() - 1);
            assertEquals(i, 4);
        } catch (Exception ex) {
            exception = true;
        }
        assertFalse(exception);
    }
}