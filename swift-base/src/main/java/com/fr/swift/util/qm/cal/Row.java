package com.fr.swift.util.qm.cal;

import java.util.Arrays;

/**
 * Created by Lyon on 2018/7/6.
 */
class Row {
    // 标识当前是否配对过
    private boolean isPairedOf;
    // 当前行对应的原始最小项，介于0~((1L << 63) - 1)。至多能处理63个变量
    private long minTerm;
    // 消除的变量对应的位
    private long removedBits = 0;
    // 当前化简项中1的个数
    private int numberOf1s;
    // 消除过程中涉及到的最小项集合，化简项。大小为2^tableIndex
    private long[] simplifiedTerms;

    Row(long minTerm, long removedBits, int numberOf1s, long[] simplifiedTerms) {
        this.minTerm = minTerm;
        this.removedBits = removedBits;
        this.numberOf1s = numberOf1s;
        this.simplifiedTerms = simplifiedTerms;
    }

    public long getRemovedBits() {
        return removedBits;
    }

    public long getMinTerm() {
        return minTerm;
    }

    public long[] getSimplifiedTerms() {
        return simplifiedTerms;
    }

    public int getNumberOf1s() {
        return numberOf1s;
    }

    public void setPairedOf(boolean pairedOf) {
        isPairedOf = pairedOf;
    }

    public boolean isPairedOf() {
        return isPairedOf;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Row row = (Row) o;

        if (isPairedOf != row.isPairedOf) return false;
        if (minTerm != row.minTerm) return false;
        if (removedBits != row.removedBits) return false;
        if (numberOf1s != row.numberOf1s) return false;
        return Arrays.equals(simplifiedTerms, row.simplifiedTerms);
    }

    @Override
    public int hashCode() {
        int result = (isPairedOf ? 1 : 0);
        result = 31 * result + (int) (minTerm ^ (minTerm >>> 32));
        result = 31 * result + (int) (removedBits ^ (removedBits >>> 32));
        result = 31 * result + numberOf1s;
        result = 31 * result + Arrays.hashCode(simplifiedTerms);
        return result;
    }
}
