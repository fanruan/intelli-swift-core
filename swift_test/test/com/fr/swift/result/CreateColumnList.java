package com.fr.swift.result;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.bitmap.MutableBitMap;
import com.fr.swift.bitmap.impl.BitSetMutableBitMap;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.segment.column.BitmapIndexedColumn;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.DetailColumn;
import com.fr.swift.segment.column.DictionaryEncodedColumn;


import java.util.Comparator;

public class CreateColumnList {


    public Column getIntColumn() {

        return new Column() {
            @Override
            public DictionaryEncodedColumn getDictionaryEncodedColumn() {
                return createIntDicColumn();
            }

            @Override
            public BitmapIndexedColumn getBitmapIndex() {
                return createIntIndexedColumn();
            }

            @Override
            public DetailColumn getDetailColumn() {
                return createIntDetailColumn();
            }

            @Override
            public IResourceLocation getLocation() {
                return null;
            }
        };
    }

    private DetailColumn createIntDetailColumn() {

        final int[] data = {2, 2, 4, 3, 2, 3, 4, 2};
        return new TempDetailColumn() {

            @Override
            public int getInt(int pos) {
                return data[pos];
            }
        };
    }

    private DictionaryEncodedColumn createIntDicColumn() {
        final int[] keys = {2, 3, 4};
        final int[] index = {0, 0, 2, 1, 0, 1, 2, 0};
        return new TempDictColumn() {

            @Override
            public int size() {
                return 4;
            }


            @Override
            public Object getValue(int index) {
                return keys[index];
            }


            @Override
            public int getIndexByRow(int row) {
                return index[row];
            }


            @Override
            public Comparator getComparator() {
                return new Comparator() {
                    @Override
                    public int compare(Object o1, Object o2) {
                        Integer i1 = (Integer) o1;
                        Integer i2 = (Integer) o2;
                        return i1.compareTo(i2);
                    }
                };
            }

        };
    }

    private BitmapIndexedColumn createIntIndexedColumn() {
        final MutableBitMap[] bitMaps = new MutableBitMap[3];
        bitMaps[0] = BitSetMutableBitMap.newInstance();
        bitMaps[1] = BitSetMutableBitMap.newInstance();
        bitMaps[2] = BitSetMutableBitMap.newInstance();

        bitMaps[0].add(0);
        bitMaps[0].add(1);
        bitMaps[0].add(4);
        bitMaps[0].add(7);
        bitMaps[1].add(3);
        bitMaps[1].add(5);
        bitMaps[2].add(2);
        bitMaps[2].add(6);
        return new TempBitmapColumn() {

            @Override
            public ImmutableBitMap getBitMapIndex(int index) {
                return bitMaps[index];
            }

        };
    }

    public Column getLongColumn() {
        return new Column() {
            @Override
            public DictionaryEncodedColumn getDictionaryEncodedColumn() {
                return createLongDicColumn();
            }

            @Override
            public BitmapIndexedColumn getBitmapIndex() {
                return createLongIndexedColumn();
            }

            @Override
            public DetailColumn getDetailColumn() {
                return createLongDetailColumn();
            }

            @Override
            public IResourceLocation getLocation() {
                return null;
            }
        };
    }

    private DetailColumn createLongDetailColumn() {
        final long[] data = {12, 18, 23, 18, 23, 18, 23, 12};
        return new TempDetailColumn(){

            @Override
            public long getLong(int pos) {
                return data[pos];
            }

        };

    }

    private DictionaryEncodedColumn createLongDicColumn() {
        final long[] keys = {12, 18, 23};
        final int[] index = {0, 1, 2, 1, 2, 1, 2, 0};
        return new TempDictColumn() {

            @Override
            public int size() {
                return 3;
            }


            @Override
            public Object getValue(int index) {
                return keys[index];
            }


            @Override
            public int getIndexByRow(int row) {
                return index[row];
            }


            @Override
            public Comparator getComparator() {
                return new Comparator() {
                    @Override
                    public int compare(Object o1, Object o2) {
                        Long l1 = (Long) o1;
                        Long l2 = (Long) o2;
                        return l1.compareTo(l2);
                    }
                };
            }

        };
    }

    private BitmapIndexedColumn createLongIndexedColumn() {
        final MutableBitMap[] bitMaps = new MutableBitMap[3];
        bitMaps[0] = BitSetMutableBitMap.newInstance();
        bitMaps[1] = BitSetMutableBitMap.newInstance();
        bitMaps[2] = BitSetMutableBitMap.newInstance();
//        bitMaps[3] = BitSetMutableBitMap.newInstance();

        bitMaps[0].add(0);
        bitMaps[0].add(7);
        bitMaps[1].add(1);
        bitMaps[1].add(3);
        bitMaps[1].add(5);
        bitMaps[2].add(2);
        bitMaps[2].add(4);
        bitMaps[2].add(6);
        return new TempBitmapColumn() {

            @Override
            public ImmutableBitMap getBitMapIndex(int index) {
                return bitMaps[index];
            }

        };
    }

    public Column getDoubleColumn() {
        return new Column() {
            @Override
            public DictionaryEncodedColumn getDictionaryEncodedColumn() {
                return createDoubleDicColumn();
            }

            @Override
            public BitmapIndexedColumn getBitmapIndex() {
                return createDoubleIndexedColumn();
            }

            @Override
            public DetailColumn getDetailColumn() {
                return createDoubleDetailColumn();
            }

            @Override
            public IResourceLocation getLocation() {
                return null;
            }
        };
    }

    private DetailColumn createDoubleDetailColumn() {
        final double[] data = {9.5, 50.2, 40.1, 12.3, 9.5, 12.3, 40.1, 9.5};
        return new TempDetailColumn() {

            @Override
            public double getDouble(int pos) {
                return data[pos];
            }

        };
    }

    private DictionaryEncodedColumn createDoubleDicColumn() {
        final double[] keys = {9.5, 12.3, 40.1, 50.2};
        final int[] index = {0, 3, 2, 1, 0, 1, 2, 0};
        return new TempDictColumn() {

            @Override
            public int size() {
                return 4;
            }


            @Override
            public Object getValue(int index) {
                return keys[index];
            }


            @Override
            public int getIndexByRow(int row) {
                return index[row];
            }

            @Override
            public Comparator getComparator() {
                return new Comparator() {
                    @Override
                    public int compare(Object o1, Object o2) {
                        Double d1 = (Double) o1;
                        Double d2 = (Double) o2;
                        return d1.compareTo(d2);
                    }
                };
            }

        };
    }

    private BitmapIndexedColumn createDoubleIndexedColumn() {
        final MutableBitMap[] bitMaps = new MutableBitMap[4];
        bitMaps[0] = BitSetMutableBitMap.newInstance();
        bitMaps[1] = BitSetMutableBitMap.newInstance();
        bitMaps[2] = BitSetMutableBitMap.newInstance();
        bitMaps[3] = BitSetMutableBitMap.newInstance();

        bitMaps[0].add(0);
        bitMaps[0].add(4);
        bitMaps[0].add(7);
        bitMaps[1].add(3);
        bitMaps[1].add(5);
        bitMaps[2].add(2);
        bitMaps[2].add(6);
        bitMaps[3].add(1);
        return new TempBitmapColumn() {

            @Override
            public ImmutableBitMap getBitMapIndex(int index) {
                return bitMaps[index];
            }

        };
    }

    public Column getStringColumn() {
        return new Column() {
            @Override
            public DictionaryEncodedColumn getDictionaryEncodedColumn() {
                return createStringDicColumn();
            }

            @Override
            public BitmapIndexedColumn getBitmapIndex() {
                return createStringIndexedColumn();
            }

            @Override
            public DetailColumn getDetailColumn() {
                return createStringDetailColumn();
            }

            @Override
            public IResourceLocation getLocation() {
                return null;
            }
        };
    }

    private DetailColumn createStringDetailColumn() {
        final String data[] = {"A", "B", "C", "B", "C", "B", "A", "C"};
        return new TempDetailColumn() {

            @Override
            public Object get(int pos) {
                return data[pos];
            }

        };
    }

    private DictionaryEncodedColumn createStringDicColumn() {
        final String[] keys = {"A", "B", "C"};
        final int[] index = {0, 1, 2, 1, 2, 1, 0, 2};
        return new TempDictColumn() {
            @Override
            public int size() {
                return 3;
            }


            @Override
            public Object getValue(int index) {
                return keys[index];
            }


            @Override
            public int getIndexByRow(int row) {
                return index[row];
            }


            @Override
            public Comparator getComparator() {
                return new Comparator() {
                    @Override
                    public int compare(Object o1, Object o2) {
                        String s1 = (String) o1;
                        String s2 = (String) o2;
                        return s1.compareTo(s2);
                    }
                };
            }

        };
    }

    private BitmapIndexedColumn createStringIndexedColumn() {
        final MutableBitMap[] bitMaps = new MutableBitMap[3];
        bitMaps[0] = BitSetMutableBitMap.newInstance();
        bitMaps[1] = BitSetMutableBitMap.newInstance();
        bitMaps[2] = BitSetMutableBitMap.newInstance();
        bitMaps[0].add(0);
        bitMaps[0].add(6);
        bitMaps[1].add(1);
        bitMaps[1].add(3);
        bitMaps[1].add(5);
        bitMaps[2].add(2);
        bitMaps[2].add(4);
        bitMaps[2].add(7);
        return new TempBitmapColumn() {

            @Override
            public ImmutableBitMap getBitMapIndex(int index) {

                return bitMaps[index];
            }

        };
    }
}
