package com.fr.swift.bitmap.impl;

/**
 * @author anchore
 */
final class Util {
    static int bytesToInt(byte[] bytes) {
        int i = 0;
        i |= (bytes[0] & 0xFF);
        i |= ((bytes[1] & 0xFF) << 8);
        i |= ((bytes[2] & 0xFF) << 16);
        i |= ((bytes[3] & 0xFF) << 24);
        return i;
    }

    static byte[] intToBytes(int i) {
        return new byte[]{
                (byte) (i & 0xFF),
                (byte) ((i >> 8) & 0xFF),
                (byte) ((i >> 16) & 0xFF),
                (byte) ((i >> 24) & 0xFF),
        };
    }
}