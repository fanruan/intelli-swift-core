/*
 * (c) the authors Licensed under the Apache License, Version 2.0.
 */

package com.fr.swift.cloud.bitmap.roaringbitmap;

import com.fr.swift.cloud.bitmap.roaringbitmap.buffer.MappeableBitmapContainer;
import com.fr.swift.cloud.bitmap.roaringbitmap.buffer.MappeableContainer;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.nio.LongBuffer;
import java.util.Arrays;
import java.util.Iterator;


/**
 * Simple bitset-like container.
 */
public final class BitmapContainer extends Container implements Cloneable {
    /**
     * optimization flag: whether the cardinality of the bitmaps is maintained through branchless
     * operations
     */
    public static final boolean USE_BRANCHLESS = true;
    protected static final int MAX_CAPACITY = 1 << 16;
    private static final long serialVersionUID = 2L;
    // 64 words can have max 32 runs per word, max 2k runs
    // bail out early when the number of runs is excessive, without
    // an exact count (just a decent lower bound)
    private static final int BLOCKSIZE = 128;
    // nruns value for which RunContainer.serializedSizeInBytes ==
    // BitmapContainer.getArraySizeInBytes()
    private final int MAXRUNS = (getArraySizeInBytes() - 2) / 4;
    long[] bitmap;
    int cardinality;

    /**
     * Create a bitmap container with all bits set to false
     */
    public BitmapContainer() {
        this.cardinality = 0;
        this.bitmap = new long[MAX_CAPACITY / 64];
    }

    /**
     * Create a bitmap container with a run of ones from firstOfRun to lastOfRun. Caller must ensure
     * that the range isn't so small that an ArrayContainer should have been created instead
     *
     * @param firstOfRun first index
     * @param lastOfRun  last index (range is exclusive)
     */
    public BitmapContainer(final int firstOfRun, final int lastOfRun) {
        this.cardinality = lastOfRun - firstOfRun;
        this.bitmap = new long[MAX_CAPACITY / 64];
        Util.setBitmapRange(bitmap, firstOfRun, lastOfRun);
    }

    private BitmapContainer(int newCardinality, long[] newBitmap) {
        this.cardinality = newCardinality;
        this.bitmap = Arrays.copyOf(newBitmap, newBitmap.length);
    }


    /**
     * Create a new container, no copy is made.
     *
     * @param newBitmap      content
     * @param newCardinality desired cardinality.
     */
    public BitmapContainer(long[] newBitmap, int newCardinality) {
        this.cardinality = newCardinality;
        this.bitmap = newBitmap;
    }


    /**
     * Creates a new non-mappeable container from a mappeable one. This copies the data.
     *
     * @param bc the original container
     */
    public BitmapContainer(MappeableBitmapContainer bc) {
        this.cardinality = bc.getCardinality();
        this.bitmap = bc.toLongArray();
    }

    /**
     * Return a bitmap iterator over this array
     *
     * @param bitmap array to be iterated over
     * @return an iterator
     */
    public static ShortIterator getReverseShortIterator(long[] bitmap) {
        return new ReverseBitmapContainerShortIterator(bitmap);
    }

    /**
     * Return a bitmap iterator over this array
     *
     * @param bitmap array to be iterated over
     * @return an iterator
     */
    public static PeekableShortIterator getShortIterator(long[] bitmap) {
        return new BitmapContainerShortIterator(bitmap);
    }

    // the parameter is for overloading and symmetry with ArrayContainer
    protected static int serializedSizeInBytes(int unusedCardinality) {
        return MAX_CAPACITY / 8;
    }

    @Override
    public Container add(int begin, int end) {
        // TODO: may need to convert to a RunContainer
        if (end == begin) {
            return clone();
        }
        if ((begin > end) || (end > (1 << 16))) {
            throw new IllegalArgumentException("Invalid range [" + begin + "," + end + ")");
        }
        BitmapContainer answer = clone();
        Util.setBitmapRange(answer.bitmap, begin, end);
        answer.computeCardinality();
        return answer;
    }


    @Override
    public Container add(final short i) {
        final int x = Util.toIntUnsigned(i);
        final long previous = bitmap[x / 64];
        long newval = previous | (1L << x);
        bitmap[x / 64] = newval;
        if (USE_BRANCHLESS) {
            cardinality += (previous ^ newval) >>> x;
        } else if (previous != newval) {
            ++cardinality;
        }
        return this;
    }

    @Override
    public ArrayContainer and(final ArrayContainer value2) {
        final ArrayContainer answer = new ArrayContainer(value2.content.length);
        int c = value2.cardinality;
        for (int k = 0; k < c; ++k) {
            short v = value2.content[k];
            if (this.contains(v)) {
                answer.content[answer.cardinality++] = v;
            }
        }
        return answer;
    }

    @Override
    public Container and(final BitmapContainer value2) {
        int newCardinality = 0;
        for (int k = 0; k < this.bitmap.length; ++k) {
            newCardinality += Long.bitCount(this.bitmap[k] & value2.bitmap[k]);
        }
        if (newCardinality > ArrayContainer.DEFAULT_MAX_SIZE) {
            final BitmapContainer answer = new BitmapContainer();
            for (int k = 0; k < answer.bitmap.length; ++k) {
                answer.bitmap[k] = this.bitmap[k] & value2.bitmap[k];
            }
            answer.cardinality = newCardinality;
            return answer;
        }
        ArrayContainer ac = new ArrayContainer(newCardinality);
        Util.fillArrayAND(ac.content, this.bitmap, value2.bitmap);
        ac.cardinality = newCardinality;
        return ac;
    }

    @Override
    public Container and(RunContainer x) {
        return x.and(this);
    }

    @Override
    public int andCardinality(final ArrayContainer value2) {
        int answer = 0;
        int c = value2.cardinality;
        for (int k = 0; k < c; ++k) {
            short v = value2.content[k];
            if (this.contains(v)) {
                answer++;
            }
        }
        return answer;
    }

    @Override
    public int andCardinality(final BitmapContainer value2) {
        int newCardinality = 0;
        for (int k = 0; k < this.bitmap.length; ++k) {
            newCardinality += Long.bitCount(this.bitmap[k] & value2.bitmap[k]);
        }
        return newCardinality;
    }

    @Override
    public int andCardinality(RunContainer x) {
        return x.andCardinality(this);
    }

    @Override
    public Container andNot(final ArrayContainer value2) {
        final BitmapContainer answer = clone();
        int c = value2.cardinality;
        for (int k = 0; k < c; ++k) {
            short v = value2.content[k];
            final int i = Util.toIntUnsigned(v) >>> 6;
            long w = answer.bitmap[i];
            long aft = w & (~(1L << v));
            answer.bitmap[i] = aft;
            answer.cardinality -= (w ^ aft) >>> v;
        }
        if (answer.cardinality <= ArrayContainer.DEFAULT_MAX_SIZE) {
            return answer.toArrayContainer();
        }
        return answer;
    }

    @Override
    public Container andNot(final BitmapContainer value2) {
        int newCardinality = 0;
        for (int k = 0; k < this.bitmap.length; ++k) {
            newCardinality += Long.bitCount(this.bitmap[k] & (~value2.bitmap[k]));
        }
        if (newCardinality > ArrayContainer.DEFAULT_MAX_SIZE) {
            final BitmapContainer answer = new BitmapContainer();
            for (int k = 0; k < answer.bitmap.length; ++k) {
                answer.bitmap[k] = this.bitmap[k] & (~value2.bitmap[k]);
            }
            answer.cardinality = newCardinality;
            return answer;
        }
        ArrayContainer ac = new ArrayContainer(newCardinality);
        Util.fillArrayANDNOT(ac.content, this.bitmap, value2.bitmap);
        ac.cardinality = newCardinality;
        return ac;
    }

    @Override
    public Container andNot(RunContainer x) {
        // could be rewritten as return andNot(x.toBitmapOrArrayContainer());
        BitmapContainer answer = this.clone();
        for (int rlepos = 0; rlepos < x.nbrruns; ++rlepos) {
            int start = Util.toIntUnsigned(x.getValue(rlepos));
            int end = start + Util.toIntUnsigned(x.getLength(rlepos)) + 1;
            Util.resetBitmapRange(answer.bitmap, start, end);
        }
        answer.computeCardinality();
        if (answer.getCardinality() > ArrayContainer.DEFAULT_MAX_SIZE) {
            return answer;
        } else {
            return answer.toArrayContainer();
        }
    }

    @Override
    public void clear() {
        if (cardinality != 0) {
            cardinality = 0;
            Arrays.fill(bitmap, 0);
        }
    }

    @Override
    public BitmapContainer clone() {
        return new BitmapContainer(this.cardinality, this.bitmap);
    }

    /**
     * Recomputes the cardinality of the bitmap.
     */
    protected void computeCardinality() {
        this.cardinality = 0;
        for (int k = 0; k < this.bitmap.length; k++) {
            this.cardinality += Long.bitCount(this.bitmap[k]);
        }
    }

    @Override
    public boolean contains(final short i) {
        final int x = Util.toIntUnsigned(i);
        return (bitmap[x / 64] & (1L << x)) != 0;
    }

    @Override
    public void deserialize(DataInput in) throws IOException {
        // little endian
        this.cardinality = 0;
        for (int k = 0; k < bitmap.length; ++k) {
            long w = Long.reverseBytes(in.readLong());
            bitmap[k] = w;
            this.cardinality += Long.bitCount(w);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof BitmapContainer) {
            BitmapContainer srb = (BitmapContainer) o;
            if (srb.cardinality != this.cardinality) {
                return false;
            }
            return Arrays.equals(this.bitmap, srb.bitmap);
        } else if (o instanceof RunContainer) {
            return o.equals(this);
        }
        return false;
    }


    /**
     * Fill the array with set bits
     *
     * @param array container (should be sufficiently large)
     */
    protected void fillArray(final short[] array) {
        int pos = 0;
        int base = 0;
        for (int k = 0; k < bitmap.length; ++k) {
            long bitset = bitmap[k];
            while (bitset != 0) {
                long t = bitset & -bitset;
                array[pos++] = (short) (base + Long.bitCount(t - 1));
                bitset ^= t;
            }
            base += 64;
        }
    }

    @Override
    public void fillLeastSignificant16bits(int[] x, int i, int mask) {
        int pos = i;
        int base = mask;
        for (int k = 0; k < bitmap.length; ++k) {
            long bitset = bitmap[k];
            while (bitset != 0) {
                long t = bitset & -bitset;
                x[pos++] = base + Long.bitCount(t - 1);
                bitset ^= t;
            }
            base += 64;
        }
    }


    @Override
    public Container flip(short i) {
        final int x = Util.toIntUnsigned(i);
        int index = x / 64;
        long bef = bitmap[index];
        long mask = 1L << x;
        if (cardinality == ArrayContainer.DEFAULT_MAX_SIZE + 1) {// this is
            // the
            // uncommon
            // path
            if ((bef & mask) != 0) {
                --cardinality;
                bitmap[index] &= ~mask;
                return this.toArrayContainer();
            }
        }
        // TODO: check whether a branchy version could be faster
        cardinality += 1 - 2 * ((bef & mask) >>> x);
        bitmap[index] ^= mask;
        return this;
    }

    @Override
    protected int getArraySizeInBytes() {
        return MAX_CAPACITY / 8;
    }

    @Override
    public int getCardinality() {
        return cardinality;
    }

    @Override
    public ShortIterator getReverseShortIterator() {
        return new ReverseBitmapContainerShortIterator(this.bitmap);
    }

    @Override
    public PeekableShortIterator getShortIterator() {
        return new BitmapContainerShortIterator(this.bitmap);
    }

    @Override
    public int getSizeInBytes() {
        return this.bitmap.length * 8;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(this.bitmap);
    }

    @Override
    public Container iadd(int begin, int end) {
        // TODO: may need to convert to a RunContainer
        if (end == begin) {
            return this;
        }
        if ((begin > end) || (end > (1 << 16))) {
            throw new IllegalArgumentException("Invalid range [" + begin + "," + end + ")");
        }
        Util.setBitmapRange(bitmap, begin, end);
        computeCardinality();
        return this;
    }

    @Override
    public Container iand(final ArrayContainer b2) {
        return b2.and(this);// no inplace possible
    }

    @Override
    public Container iand(final BitmapContainer b2) {
        int newCardinality = 0;
        for (int k = 0; k < this.bitmap.length; ++k) {
            newCardinality += Long.bitCount(this.bitmap[k] & b2.bitmap[k]);
        }
        if (newCardinality > ArrayContainer.DEFAULT_MAX_SIZE) {
            for (int k = 0; k < this.bitmap.length; ++k) {
                this.bitmap[k] = this.bitmap[k] & b2.bitmap[k];
            }
            this.cardinality = newCardinality;
            return this;
        }
        ArrayContainer ac = new ArrayContainer(newCardinality);
        Util.fillArrayAND(ac.content, this.bitmap, b2.bitmap);
        ac.cardinality = newCardinality;
        return ac;
    }

    @Override
    public Container iand(RunContainer x) {
        // could probably be replaced with return iand(x.toBitmapOrArrayContainer());
        final int card = x.getCardinality();
        if (card <= ArrayContainer.DEFAULT_MAX_SIZE) {
            // no point in doing it in-place
            ArrayContainer answer = new ArrayContainer(card);
            answer.cardinality = 0;
            for (int rlepos = 0; rlepos < x.nbrruns; ++rlepos) {
                int runStart = Util.toIntUnsigned(x.getValue(rlepos));
                int runEnd = runStart + Util.toIntUnsigned(x.getLength(rlepos));
                for (int runValue = runStart; runValue <= runEnd; ++runValue) {
                    if (this.contains((short) runValue)) {// it looks like contains() should be cheap enough
                        // if accessed sequentially
                        answer.content[answer.cardinality++] = (short) runValue;
                    }
                }
            }
            return answer;
        }
        int start = 0;
        for (int rlepos = 0; rlepos < x.nbrruns; ++rlepos) {
            int end = Util.toIntUnsigned(x.getValue(rlepos));
            Util.resetBitmapRange(this.bitmap, start, end);
            start = end + Util.toIntUnsigned(x.getLength(rlepos)) + 1;
        }
        Util.resetBitmapRange(this.bitmap, start, Util.maxLowBitAsInteger() + 1);
        computeCardinality();
        if (getCardinality() > ArrayContainer.DEFAULT_MAX_SIZE) {
            return this;
        } else {
            return toArrayContainer();
        }
    }

    @Override
    public Container iandNot(final ArrayContainer b2) {
        for (int k = 0; k < b2.cardinality; ++k) {
            this.remove(b2.content[k]);
        }
        if (cardinality <= ArrayContainer.DEFAULT_MAX_SIZE) {
            return this.toArrayContainer();
        }
        return this;
    }

    @Override
    public Container iandNot(final BitmapContainer b2) {
        int newCardinality = 0;
        for (int k = 0; k < this.bitmap.length; ++k) {
            newCardinality += Long.bitCount(this.bitmap[k] & (~b2.bitmap[k]));
        }
        if (newCardinality > ArrayContainer.DEFAULT_MAX_SIZE) {
            for (int k = 0; k < this.bitmap.length; ++k) {
                this.bitmap[k] = this.bitmap[k] & (~b2.bitmap[k]);
            }
            this.cardinality = newCardinality;
            return this;
        }
        ArrayContainer ac = new ArrayContainer(newCardinality);
        Util.fillArrayANDNOT(ac.content, this.bitmap, b2.bitmap);
        ac.cardinality = newCardinality;
        return ac;
    }

    @Override
    public Container iandNot(RunContainer x) {
        // could probably be replaced with return iandNot(x.toBitmapOrArrayContainer());
        for (int rlepos = 0; rlepos < x.nbrruns; ++rlepos) {
            int start = Util.toIntUnsigned(x.getValue(rlepos));
            int end = start + Util.toIntUnsigned(x.getLength(rlepos)) + 1;
            Util.resetBitmapRange(this.bitmap, start, end);
        }
        computeCardinality();
        if (getCardinality() > ArrayContainer.DEFAULT_MAX_SIZE) {
            return this;
        } else {
            return toArrayContainer();
        }
    }

    protected Container ilazyor(ArrayContainer value2) {
        this.cardinality = -1;// invalid
        int c = value2.cardinality;
        for (int k = 0; k < c; ++k) {
            short v = value2.content[k];
            final int i = Util.toIntUnsigned(v) >>> 6;
            this.bitmap[i] |= (1L << v);
        }
        return this;
    }

    protected Container ilazyor(BitmapContainer x) {
        this.cardinality = -1;// invalid
        for (int k = 0; k < this.bitmap.length; k++) {
            this.bitmap[k] |= x.bitmap[k];
        }
        return this;
    }

    protected Container ilazyor(RunContainer x) {
        // could be implemented as return ilazyor(x.toTemporaryBitmap());
        cardinality = -1; // invalid
        for (int rlepos = 0; rlepos < x.nbrruns; ++rlepos) {
            int start = Util.toIntUnsigned(x.getValue(rlepos));
            int end = start + Util.toIntUnsigned(x.getLength(rlepos)) + 1;
            Util.setBitmapRange(this.bitmap, start, end);
        }
        return this;
    }

    @Override
    public Container inot(final int firstOfRange, final int lastOfRange) {
        if (lastOfRange - firstOfRange == MAX_CAPACITY) {
            Util.flipBitmapRange(bitmap, firstOfRange, lastOfRange);
            cardinality = MAX_CAPACITY - cardinality;
        } else if (lastOfRange - firstOfRange > MAX_CAPACITY / 2) {
            Util.flipBitmapRange(bitmap, firstOfRange, lastOfRange);
            computeCardinality();
        } else {
            cardinality += Util.flipBitmapRangeAndCardinalityChange(bitmap, firstOfRange, lastOfRange);
        }
        if (cardinality <= ArrayContainer.DEFAULT_MAX_SIZE) {
            return toArrayContainer();
        }
        return this;
    }

    @Override
    public boolean intersects(ArrayContainer value2) {
        int c = value2.cardinality;
        for (int k = 0; k < c; ++k) {
            if (this.contains(value2.content[k])) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean intersects(BitmapContainer value2) {
        for (int k = 0; k < this.bitmap.length; ++k) {
            if ((this.bitmap[k] & value2.bitmap[k]) != 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean intersects(RunContainer x) {
        return x.intersects(this);
    }

    @Override
    public BitmapContainer ior(final ArrayContainer value2) {
        int c = value2.cardinality;
        for (int k = 0; k < c; ++k) {
            final int i = Util.toIntUnsigned(value2.content[k]) >>> 6;

            long bef = this.bitmap[i];
            long aft = bef | (1L << value2.content[k]);
            this.bitmap[i] = aft;
            if (USE_BRANCHLESS) {
                cardinality += (bef - aft) >>> 63;
            } else {
                if (bef != aft) {
                    cardinality++;
                }
            }
        }
        return this;
    }

    @Override
    public Container ior(final BitmapContainer b2) {
        this.cardinality = 0;
        for (int k = 0; k < this.bitmap.length; k++) {
            long w = this.bitmap[k] | b2.bitmap[k];
            this.bitmap[k] = w;
            this.cardinality += Long.bitCount(w);
        }
        return this;
    }

    @Override
    public Container ior(RunContainer x) {
        // could probably be replaced with return ior(x.toBitmapOrArrayContainer());
        for (int rlepos = 0; rlepos < x.nbrruns; ++rlepos) {
            int start = Util.toIntUnsigned(x.getValue(rlepos));
            int end = start + Util.toIntUnsigned(x.getLength(rlepos)) + 1;
            Util.setBitmapRange(this.bitmap, start, end);
        }
        computeCardinality();
        return this;
    }

    @Override
    public Container iremove(int begin, int end) {
        if (end == begin) {
            return this;
        }
        if ((begin > end) || (end > (1 << 16))) {
            throw new IllegalArgumentException("Invalid range [" + begin + "," + end + ")");
        }
        Util.resetBitmapRange(bitmap, begin, end);
        computeCardinality();
        if (getCardinality() <= ArrayContainer.DEFAULT_MAX_SIZE) {
            return toArrayContainer();
        }
        return this;
    }

    @Override
    public Iterator<Short> iterator() {
        return new Iterator<Short>() {
            final ShortIterator si = BitmapContainer.this.getShortIterator();

            @Override
            public boolean hasNext() {
                return si.hasNext();
            }

            @Override
            public Short next() {
                return si.next();
            }

            @Override
            public void remove() {
                // TODO: implement
                throw new RuntimeException("unsupported operation: remove");
            }
        };
    }

    @Override
    public Container ixor(final ArrayContainer value2) {
        int c = value2.cardinality;
        for (int k = 0; k < c; ++k) {
            short vc = value2.content[k];
            long mask = 1L << vc;
            final int index = Util.toIntUnsigned(vc) >>> 6;
            long ba = this.bitmap[index];
            // TODO: check whether a branchy version could be faster
            this.cardinality += 1 - 2 * ((ba & mask) >>> vc);
            this.bitmap[index] = ba ^ mask;
        }
        if (this.cardinality <= ArrayContainer.DEFAULT_MAX_SIZE) {
            return this.toArrayContainer();
        }
        return this;
    }


    @Override
    public Container ixor(BitmapContainer b2) {
        int newCardinality = 0;
        for (int k = 0; k < this.bitmap.length; ++k) {
            newCardinality += Long.bitCount(this.bitmap[k] ^ b2.bitmap[k]);
        }
        if (newCardinality > ArrayContainer.DEFAULT_MAX_SIZE) {
            for (int k = 0; k < this.bitmap.length; ++k) {
                this.bitmap[k] = this.bitmap[k] ^ b2.bitmap[k];
            }
            this.cardinality = newCardinality;
            return this;
        }
        ArrayContainer ac = new ArrayContainer(newCardinality);
        Util.fillArrayXOR(ac.content, this.bitmap, b2.bitmap);
        ac.cardinality = newCardinality;
        return ac;
    }

    @Override
    public Container ixor(RunContainer x) {
        // could probably be replaced with return ixor(x.toBitmapOrArrayContainer());
        for (int rlepos = 0; rlepos < x.nbrruns; ++rlepos) {
            int start = Util.toIntUnsigned(x.getValue(rlepos));
            int end = start + Util.toIntUnsigned(x.getLength(rlepos)) + 1;
            Util.flipBitmapRange(this.bitmap, start, end);
        }
        computeCardinality();
        if (this.getCardinality() > ArrayContainer.DEFAULT_MAX_SIZE) {
            return this;
        } else {
            return toArrayContainer();
        }
    }

    protected Container lazyor(ArrayContainer value2) {
        BitmapContainer answer = this.clone();
        answer.cardinality = -1;// invalid
        int c = value2.cardinality;
        for (int k = 0; k < c; ++k) {
            short v = value2.content[k];
            final int i = Util.toIntUnsigned(v) >>> 6;
            answer.bitmap[i] |= (1L << v);
        }
        return answer;
    }

    protected Container lazyor(BitmapContainer x) {
        BitmapContainer answer = new BitmapContainer();
        answer.cardinality = -1;// invalid
        for (int k = 0; k < this.bitmap.length; k++) {
            answer.bitmap[k] = this.bitmap[k] | x.bitmap[k];
        }
        return answer;
    }


    protected Container lazyor(RunContainer x) {
        BitmapContainer bc = clone();
        bc.cardinality = -1; // invalid
        for (int rlepos = 0; rlepos < x.nbrruns; ++rlepos) {
            int start = Util.toIntUnsigned(x.getValue(rlepos));
            int end = start + Util.toIntUnsigned(x.getLength(rlepos)) + 1;
            Util.setBitmapRange(bc.bitmap, start, end);
        }
        return bc;
    }

    @Override
    public Container limit(int maxcardinality) {
        if (maxcardinality >= this.cardinality) {
            return clone();
        }
        if (maxcardinality <= ArrayContainer.DEFAULT_MAX_SIZE) {
            ArrayContainer ac = new ArrayContainer(maxcardinality);
            int pos = 0;
            for (int k = 0; (ac.cardinality < maxcardinality) && (k < bitmap.length); ++k) {
                long bitset = bitmap[k];
                while ((ac.cardinality < maxcardinality) && (bitset != 0)) {
                    long t = bitset & -bitset;
                    ac.content[pos++] = (short) (k * 64 + Long.bitCount(t - 1));
                    ac.cardinality++;
                    bitset ^= t;
                }
            }
            return ac;
        }
        BitmapContainer bc = new BitmapContainer(maxcardinality, this.bitmap);
        int s = Util.toIntUnsigned(select(maxcardinality));
        int usedwords = (s + 63) / 64;
        int todelete = this.bitmap.length - usedwords;
        for (int k = 0; k < todelete; ++k) {
            bc.bitmap[bc.bitmap.length - 1 - k] = 0;
        }
        int lastword = s % 64;
        if (lastword != 0) {
            bc.bitmap[s / 64] &= (0xFFFFFFFFFFFFFFFFL >>> (64 - lastword));
        }
        return bc;
    }

    protected void loadData(final ArrayContainer arrayContainer) {
        this.cardinality = arrayContainer.cardinality;
        for (int k = 0; k < arrayContainer.cardinality; ++k) {
            final short x = arrayContainer.content[k];
            bitmap[Util.toIntUnsigned(x) / 64] |= (1L << x);
        }
    }

    /**
     * Find the index of the next set bit greater or equal to i, returns -1 if none found.
     *
     * @param i starting index
     * @return index of the next set bit
     */
    public int nextSetBit(final int i) {
        int x = i >> 6; // i / 64 with sign extension
        long w = bitmap[x];
        w >>>= i;
        if (w != 0) {
            return i + Long.numberOfTrailingZeros(w);
        }
        for (++x; x < bitmap.length; ++x) {
            if (bitmap[x] != 0) {
                return x * 64 + Long.numberOfTrailingZeros(bitmap[x]);
            }
        }
        return -1;
    }

    /**
     * Find the index of the next unset bit greater or equal to i, returns -1 if none found.
     *
     * @param i starting index
     * @return index of the next unset bit
     */
    public short nextUnsetBit(final int i) {
        int x = i / 64;
        long w = ~bitmap[x];
        w >>>= i;
        if (w != 0) {
            return (short) (i + Long.numberOfTrailingZeros(w));
        }
        ++x;
        for (; x < bitmap.length; ++x) {
            if (bitmap[x] != ~0L) {
                return (short) (x * 64 + Long.numberOfTrailingZeros(~bitmap[x]));
            }
        }
        return -1;
    }


    @Override
    public Container not(final int firstOfRange, final int lastOfRange) {
        BitmapContainer answer = clone();
        return answer.inot(firstOfRange, lastOfRange);
    }

    @Override
    int numberOfRuns() {
        int numRuns = 0;
        long nextWord = bitmap[0];

        for (int i = 0; i < bitmap.length - 1; i++) {
            long word = nextWord;
            nextWord = bitmap[i + 1];
            numRuns += Long.bitCount((~word) & (word << 1)) + ((word >>> 63) & ~nextWord);
        }

        long word = nextWord;
        numRuns += Long.bitCount((~word) & (word << 1));
        if ((word & 0x8000000000000000L) != 0) {
            numRuns++;
        }

        return numRuns;
    }

    /**
     * Computes the number of runs
     *
     * @return the number of runs
     */
    public int numberOfRunsAdjustment() {
        int ans = 0;
        long nextWord = bitmap[0];
        for (int i = 0; i < bitmap.length - 1; i++) {
            final long word = nextWord;

            nextWord = bitmap[i + 1];
            ans += ((word >>> 63) & ~nextWord);
        }
        final long word = nextWord;

        if ((word & 0x8000000000000000L) != 0) {
            ans++;
        }
        return ans;
    }

    /**
     * Counts how many runs there is in the bitmap, up to a maximum
     *
     * @param mustNotExceed maximum of runs beyond which counting is pointless
     * @return estimated number of courses
     */
    public int numberOfRunsLowerBound(int mustNotExceed) {
        int numRuns = 0;

        for (int blockOffset = 0; blockOffset < bitmap.length; blockOffset += BLOCKSIZE) {

            for (int i = blockOffset; i < blockOffset + BLOCKSIZE; i++) {
                long word = bitmap[i];
                numRuns += Long.bitCount((~word) & (word << 1));
            }
            if (numRuns > mustNotExceed) {
                return numRuns;
            }
        }
        return numRuns;
    }

    @Override
    public BitmapContainer or(final ArrayContainer value2) {
        final BitmapContainer answer = clone();
        int c = value2.cardinality;
        for (int k = 0; k < c; ++k) {
            short v = value2.content[k];
            final int i = Util.toIntUnsigned(v) >>> 6;
            long w = answer.bitmap[i];
            long aft = w | (1L << v);
            answer.bitmap[i] = aft;
            if (USE_BRANCHLESS) {
                answer.cardinality += (w - aft) >>> 63;
            } else {
                if (w != aft) {
                    answer.cardinality++;
                }
            }
        }
        return answer;
    }

    @Override
    public Container or(final BitmapContainer value2) {
        BitmapContainer value1 = this.clone();
        return value1.ior(value2);
    }

    @Override
    public Container or(RunContainer x) {
        return x.or(this);
    }

    /**
     * Find the index of the previous set bit less than or equal to i, returns -1 if none found.
     *
     * @param i starting index
     * @return index of the previous set bit
     */
    public int prevSetBit(final int i) {
        int x = i >> 6; // i / 64 with sign extension
        long w = bitmap[x];
        w <<= 64 - i - 1;
        if (w != 0) {
            return i - Long.numberOfLeadingZeros(w);
        }
        for (--x; x >= 0; --x) {
            if (bitmap[x] != 0) {
                return x * 64 + 63 - Long.numberOfLeadingZeros(bitmap[x]);
            }
        }
        return -1;
    }

    @Override
    public int rank(short lowbits) {
        int x = Util.toIntUnsigned(lowbits);
        int leftover = (x + 1) & 63;
        int answer = 0;
        for (int k = 0; k < (x + 1) / 64; ++k) {
            answer += Long.bitCount(bitmap[k]);
        }
        if (leftover != 0) {
            answer += Long.bitCount(bitmap[(x + 1) / 64] << (64 - leftover));
        }
        return answer;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        deserialize(in);
    }


    @Override
    public Container remove(int begin, int end) {
        if (end == begin) {
            return clone();
        }
        if ((begin > end) || (end > (1 << 16))) {
            throw new IllegalArgumentException("Invalid range [" + begin + "," + end + ")");
        }
        BitmapContainer answer = clone();
        Util.resetBitmapRange(answer.bitmap, begin, end);
        answer.computeCardinality();
        if (answer.getCardinality() <= ArrayContainer.DEFAULT_MAX_SIZE) {
            return answer.toArrayContainer();
        }
        return answer;
    }

    @Override
    public Container remove(final short i) {
        final int x = Util.toIntUnsigned(i);
        int index = x / 64;
        long bef = bitmap[index];
        long mask = 1L << x;
        if (cardinality == ArrayContainer.DEFAULT_MAX_SIZE + 1) {// this is
            // the
            // uncommon
            // path
            if ((bef & mask) != 0) {
                --cardinality;
                bitmap[x / 64] = bef & (~mask);
                return this.toArrayContainer();
            }
        }
        long aft = bef & (~mask);
        cardinality -= (aft - bef) >>> 63;
        bitmap[index] = aft;
        return this;
    }

    @Override
    public Container repairAfterLazy() {
        if (getCardinality() < 0) {
            computeCardinality();
            if (getCardinality() <= ArrayContainer.DEFAULT_MAX_SIZE) {
                return this.toArrayContainer();
            }
        }
        return this;
    }

    @Override
    public Container runOptimize() {
        int numRuns = numberOfRunsLowerBound(MAXRUNS); // decent choice

        int sizeAsRunContainerLowerBound = RunContainer.serializedSizeInBytes(numRuns);

        if (sizeAsRunContainerLowerBound >= getArraySizeInBytes()) {
            return this;
        }
        // else numRuns is a relatively tight bound that needs to be exact
        // in some cases (or if we need to make the runContainer the right
        // size)
        numRuns += numberOfRunsAdjustment();
        int sizeAsRunContainer = RunContainer.serializedSizeInBytes(numRuns);

        if (getArraySizeInBytes() > sizeAsRunContainer) {
            return new RunContainer(this, numRuns);
        } else {
            return this;
        }
    }

    @Override
    public short select(int j) {
        int leftover = j;
        for (int k = 0; k < bitmap.length; ++k) {
            int w = Long.bitCount(bitmap[k]);
            if (w > leftover) {
                return (short) (k * 64 + Util.select(bitmap[k], leftover));
            }
            leftover -= w;
        }
        throw new IllegalArgumentException("Insufficient cardinality.");
    }

    @Override
    public void serialize(DataOutput out) throws IOException {
        // little endian
        for (long w : bitmap) {
            out.writeLong(Long.reverseBytes(w));
        }
    }

    @Override
    public int serializedSizeInBytes() {
        return serializedSizeInBytes(0);
    }

    /**
     * Copies the data to an array container
     *
     * @return the array container
     */
    public ArrayContainer toArrayContainer() {
        ArrayContainer ac = new ArrayContainer(cardinality);
        ac.loadData(this);
        if (ac.getCardinality() != cardinality) {
            throw new RuntimeException("Internal error.");
        }
        return ac;
    }

    /**
     * Return the content of this container as a LongBuffer. This creates a copy and might be
     * relatively slow.
     *
     * @return the LongBuffer
     */
    public LongBuffer toLongBuffer() {
        LongBuffer lb = LongBuffer.allocate(bitmap.length);
        lb.put(bitmap);
        return lb;
    }

    @Override
    public MappeableContainer toMappeableContainer() {
        return new MappeableBitmapContainer(this);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        final ShortIterator i = this.getShortIterator();
        sb.append("{");
        while (i.hasNext()) {
            sb.append(i.next());
            if (i.hasNext()) {
                sb.append(",");
            }
        }
        sb.append("}");
        return sb.toString();
    }

    @Override
    public void trim() {
    }

    @Override
    protected void writeArray(DataOutput out) throws IOException {
        serialize(out);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        serialize(out);
    }

    @Override
    public Container xor(final ArrayContainer value2) {
        final BitmapContainer answer = clone();
        int c = value2.cardinality;
        for (int k = 0; k < c; ++k) {
            short vc = value2.content[k];
            final int index = Util.toIntUnsigned(vc) >>> 6;
            final long mask = 1L << vc;
            final long val = answer.bitmap[index];
            // TODO: check whether a branchy version could be faster
            answer.cardinality += 1 - 2 * ((val & mask) >>> vc);
            answer.bitmap[index] = val ^ mask;
        }
        if (answer.cardinality <= ArrayContainer.DEFAULT_MAX_SIZE) {
            return answer.toArrayContainer();
        }
        return answer;
    }

    @Override
    public Container xor(BitmapContainer value2) {
        int newCardinality = 0;
        for (int k = 0; k < this.bitmap.length; ++k) {
            newCardinality += Long.bitCount(this.bitmap[k] ^ value2.bitmap[k]);
        }
        if (newCardinality > ArrayContainer.DEFAULT_MAX_SIZE) {
            final BitmapContainer answer = new BitmapContainer();
            for (int k = 0; k < answer.bitmap.length; ++k) {
                answer.bitmap[k] = this.bitmap[k] ^ value2.bitmap[k];
            }
            answer.cardinality = newCardinality;
            return answer;
        }
        ArrayContainer ac = new ArrayContainer(newCardinality);
        Util.fillArrayXOR(ac.content, this.bitmap, value2.bitmap);
        ac.cardinality = newCardinality;
        return ac;
    }

    @Override
    public Container xor(RunContainer x) {
        return x.xor(this);
    }

    @Override
    public void forEach(short msb, IntConsumer ic) {
        int high = ((int) msb) << 16;
        for (int x = 0; x < bitmap.length; ++x) {
            long w = bitmap[x];
            while (w != 0) {
                long t = w & -w;
                ic.accept((x * 64 + Long.bitCount(t - 1)) | high);
                w ^= t;
            }
        }
    }

    @Override
    public BitmapContainer toBitmapContainer() {
        return this;
    }

}


final class BitmapContainerShortIterator implements PeekableShortIterator {

    long w;
    int x;

    long[] bitmap;

    BitmapContainerShortIterator() {
    }

    BitmapContainerShortIterator(long[] p) {
        wrap(p);
    }

    @Override
    public PeekableShortIterator clone() {
        try {
            return (PeekableShortIterator) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;// will not happen
        }
    }

    @Override
    public boolean hasNext() {
        return x < bitmap.length;
    }

    @Override
    public short next() {
        long t = w & -w;
        short answer = (short) (x * 64 + Long.bitCount(t - 1));
        w ^= t;
        while (w == 0) {
            ++x;
            if (x == bitmap.length) {
                break;
            }
            w = bitmap[x];
        }
        return answer;
    }


    @Override
    public int nextAsInt() {
        long t = w & -w;
        int answer = (x << 6) + Long.bitCount(t - 1);
        w ^= t;
        while (w == 0) {
            ++x;
            if (x == bitmap.length) {
                break;
            }
            w = bitmap[x];
        }
        return answer;
    }

    @Override
    public void remove() {
        // TODO: implement
        throw new RuntimeException("unsupported operation: remove");
    }


    public void wrap(long[] b) {
        bitmap = b;
        for (x = 0; x < bitmap.length; ++x) {
            if ((w = bitmap[x]) != 0) {
                break;
            }
        }
    }

    @Override
    public void advanceIfNeeded(short minval) {
        if (Util.toIntUnsigned(minval) >= (x + 1) * 64) {
            x = Util.toIntUnsigned(minval) / 64;
            w = bitmap[x];
            while (w == 0) {
                ++x;
                if (x == bitmap.length) {
                    return;
                }
                w = bitmap[x];
            }
        }
        while (hasNext() && (Util.toIntUnsigned(peekNext()) < Util.toIntUnsigned(minval))) {
            next(); // could be optimized
        }
    }

    @Override
    public short peekNext() {
        long t = w & -w;
        return (short) (x * 64 + Long.bitCount(t - 1));
    }
}


final class ReverseBitmapContainerShortIterator implements ShortIterator {

    long w;
    int x;

    long[] bitmap;

    ReverseBitmapContainerShortIterator() {
    }

    ReverseBitmapContainerShortIterator(long[] b) {
        wrap(b);
    }

    @Override
    public ShortIterator clone() {
        try {
            return (ShortIterator) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    @Override
    public boolean hasNext() {
        return x >= 0;
    }

    @Override
    public short next() {
        long t = w & -w;
        short answer = (short) ((x + 1) * 64 - 1 - Long.bitCount(t - 1));
        w ^= t;
        while (w == 0) {
            --x;
            if (x < 0) {
                break;
            }
            w = Long.reverse(bitmap[x]);
        }
        return answer;
    }

    @Override
    public int nextAsInt() {
        long t = w & -w;
        int answer = (x + 1) * 64 - 1 - Long.bitCount(t - 1);
        w ^= t;
        while (w == 0) {
            --x;
            if (x < 0) {
                break;
            }
            w = Long.reverse(bitmap[x]);
        }
        return answer;
    }

    @Override
    public void remove() {
        // TODO: implement
        throw new RuntimeException("unsupported operation: remove");
    }

    void wrap(long[] b) {
        bitmap = b;
        for (x = bitmap.length - 1; x >= 0; --x) {
            if ((w = Long.reverse(bitmap[x])) != 0) {
                break;
            }
        }
    }
}