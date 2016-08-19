package com.finebi.cube.data.disk.reader.primitive;

/**
 * This class created on 2016/8/17.
 *
 * @author Connery
 * @since Advanced FineBI Analysis 1.0
 */
public class ArrayInit4Test {
    private byte[] contain = new byte[1];


    public byte getValue(int index) {
        if (contain[getIndex(index)] == 0) {
            
            return contain[0];
        } else {
            return 2;
        }
    }

    public byte getTempParaValue(int index) {
        int temp = getIndex(index);
        return contain[temp];
    }

    private int getIndex(int index) {
        if (index >= contain.length) {
            byte[] temp = new byte[index + 1];
//            System.arraycopy(contain, 0, temp, 0, contain.length);
            temp[0] = 1;
            temp[1] = 1;
            contain = temp;
            System.out.println(contain);
        }
        return 0;
    }
}
