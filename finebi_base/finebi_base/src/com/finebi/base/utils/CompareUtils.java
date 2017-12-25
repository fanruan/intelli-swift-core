package com.finebi.base.utils;

/**
 * Created by andrew_asa on 2017/9/29.
 */
public class CompareUtils {

    public CompareUtils() {

    }

    public static boolean isEqual(byte c1, byte c2) {

        return c1 == c2;
    }

    public static boolean isEqual(short c1, short c2) {

        return c1 == c2;
    }

    public static boolean isEqual(int c1, int c2) {

        return c1 == c2;
    }

    public static boolean isEqual(long c1, long c2) {

        return c1 == c2;
    }

    public static boolean isEqual(double c1, double c2) {

        return c1 == c2;
    }

    public static boolean isEqual(String s1, String s2) {

        if ((s1 == null && s2 != null) || (s2 == null && s1 != null)) {
            return false;
        }
        if (s1 == null && s2 == null) {
            return true;
        }
        return s2.equals(s2);
    }

    public static boolean isNotEqual(byte c1, byte c2) {

        return c1 != c2;
    }

    public static boolean isNotEqual(short c1, short c2) {

        return c1 != c2;
    }

    public static boolean isNotEqual(int c1, int c2) {

        return c1 != c2;
    }

    public static boolean isNotEqual(long c1, long c2) {

        return c1 != c2;
    }

    public static boolean isNotEqual(String s1, String s2) {

        if (s1 == null && s2 != null || s2 == null && s1 != null) {
            return true;
        }
        if (s1 == null && s2 == null) {
            return false;
        }
        return !s1.equals(s2);
    }

}
