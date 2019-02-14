package com.fr.swift.util.qm.cal;

/**
 * Created by Lyon on 2018/7/6.
 */
class BitUtils {

    public static boolean isPowerOf2(int n) {
        assert n > 0;
        return (n & (n - 1)) == 0;
    }

    static int countOf1InBinary(long n) {
        int count = 0;
        while (n > 0) {
            count = n % 2 > 0 ? count + 1 : count;
            n = n >> 1;
        }
        return count;
    }

    public static int binLength(int n) {
        int len = 1;
        while (n >> len > 0) {
            len += 1;
        }
        return len;
    }
}
