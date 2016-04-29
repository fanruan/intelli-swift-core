package com.finebi.cube.tools;

/**
 * This class created on 2016/4/1.
 *
 * @author Connery
 * @since 4.0
 */
public class BIByteArrayTestTool {
    public static byte[] getByteArrayOne() {
        return getByteArray(Byte.valueOf("1"), 1000000);
    }

    public static byte[] getByteArray(byte value, int time) {
        int count = time;
        byte[] content = new byte[count];
        for (int i = 0; i < count; i++) {
            content[i] = value;
        }
        return content;
    }

    public static byte[] getByteArrayTwo() {
        return getByteArray(Byte.valueOf("2"), 1000000);
    }

    public static byte[] getByteArrayThree() {
        return getByteArray(Byte.valueOf("3"), 1000000);
    }
}
