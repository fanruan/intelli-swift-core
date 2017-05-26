package com.fr.bi.stable.structure.array;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * Created by daniel on 2017/2/8.
 */
public final class MemoryUtils {

    private static Unsafe unsafe;


    static {
        try {
            Field f =Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            unsafe = (Unsafe) f.get(null);
        } catch (Exception e) {
        }
    }

    private static final long arrayBaseOffset = (long)unsafe.arrayBaseOffset(byte[].class);

    /**
     * 复制数组对象的值到堆外内存葱address开始的地址
     * @param src
     * @param address
     * @param size
     */
    public static void copyMemory(byte[] src, long address, long size) {
        copyMemory(src, 0, address, size);
    }

    /**
     * 复制数组对象的值到堆外内存从address开始的地址
     * @param src 源数组
     * @param off 数字开始为止
     * @param address 目标地址
     * @param size 目标长度
     */
    public static void copyMemory(byte[] src, long off, long address, long size) {
        unsafe.copyMemory(src, arrayBaseOffset + off, null, address, size);
    }


    /**
     * 复制数组对象的值到堆外内存葱address开始的地址
     * @param src
     * @param address
     */
    public static void copyMemory(byte[] src, long address) {
        copyMemory(src, address, src.length);
    }

    /**
     * 将从地址address开始的byte读size长度保存到dest从off开始的位置
     * @param dest
     * @param off
     * @param address
     * @param size
     */
    public static void readMemory(byte[] dest, long off, long address, long size) {
        unsafe.copyMemory(null, address, dest, arrayBaseOffset + off , size);
    }

    /**
     * 将从地址address开始的byte读size长度保存到dest从0开始的位置
     * @param dest
     * @param address
     * @param size
     */
    public static void readMemory(byte[] dest, long address, long size) {
        readMemory(dest, 0, address, size);
    }

    /**
     * 将从地址address开始的byte保存到dest
     * @param dest
     * @param address
     */
    public static void readMemory(byte[] dest, long address) {
        readMemory(dest, address, dest.length);
    }

    /**
     * 同System.arraycopy 效率应该更高
     * @param src
     * @param srcPos
     * @param dest
     * @param destPos
     * @param length
     */
    public static void arraycopy(byte[] src,  int  srcPos,
                                 byte[] dest, int destPos,
                                        int length) {
        unsafe.copyMemory(src, arrayBaseOffset + srcPos, dest, arrayBaseOffset + destPos, length);
    }

    /**
     * 复制数组
     * @param src
     * @param dest
     */
    public static void copyMemory(byte[] src, byte[] dest) {
        copyMemory(src, dest, Math.min(dest.length, src.length));
    }


    /**
     * 复制数组
     * @param src
     * @param dest
     * @param size
     */
    public static void copyMemory(byte[] src, byte[] dest, int size) {
        unsafe.copyMemory(src, arrayBaseOffset, dest, arrayBaseOffset, size);
    }


    /**
     * 分配指定大小的内存
     * @param size
     * @return
     */
    public static long allocate(long size){
        return unsafe.allocateMemory(size);
    }

    /**
     * reallocate用法：参数1 老的地址
     * 参数2 新的总内存大小
     * 返回新的地址替换原来的地址
     * @param address
     * @param size
     * @return
     */
    public static long reallocate(long address, long size) {
        return unsafe.reallocateMemory(address, size);
    }

    /**
     * 从address位置开始置0
     * @param address
     * @param size
     */
    public static void fill0(long address, long size) {
        unsafe.setMemory(address, size, (byte) 0);
    }

    /**
     * 释放内存的方法
     * @param s
     */
    public static void free(long s){
        unsafe.freeMemory(s);
    }

    /**
     * 给指定位置赋值
     * @param s
     * @param offset
     * @param b
     */
    public static void put(long s, long offset, byte b){
        unsafe.putByte(s + offset, b);
    }


    /**
     * get byte 方法
     * @param s
     * @param offset 连续不考虑长度
     * @return
     */
    public static byte getByte(long s, int offset){
        return unsafe.getByte(s + offset);
    }
    /**
     * get byte 方法
     * @param s
     * @param offset 连续不考虑长度
     * @return
     */
    public static int getInt(long s, long offset){
        return unsafe.getInt(s + (offset << MemoryConstants.OFFSET_INT));
    }
    /**
     * get byte 方法
     * @param s
     * @param offset 连续不考虑长度
     * @return
     */
    public static void put(long s, long offset, int v){
        unsafe.putInt(s + (offset << MemoryConstants.OFFSET_INT), v);
    }
    /**
     * get byte 方法
     * @param s
     * @param offset 连续不考虑长度
     * @return
     */
    public static long getLong(long s, long offset){
        return unsafe.getLong(s + (offset << MemoryConstants.OFFSET_LONG));
    }
    /**
     * get byte 方法
     * @param s
     * @param offset 连续不考虑长度
     * @return
     */
    public static void put(long s, long offset, long v){
        unsafe.putLong(s + (offset << MemoryConstants.OFFSET_LONG), v);
    }
    /**
     * get byte 方法
     * @param s
     * @param offset 连续不考虑长度
     * @return
     */
    public static char getChar(long s, long offset){
        return unsafe.getChar(s + (offset << MemoryConstants.OFFSET_CHAR));
    }
    /**
     * get byte 方法
     * @param s
     * @param offset 连续不考虑长度
     * @return
     */
    public static void put(long s, long offset, char v){
        unsafe.putChar(s + (offset << MemoryConstants.OFFSET_CHAR), v);
    }
    /**
     * get byte 方法
     * @param s
     * @param offset 连续不考虑长度
     * @return
     */
    public static short getShort(long s, long offset){
        return unsafe.getShort(s + (offset << MemoryConstants.OFFSET_SHORT));
    }
    /**
     * get byte 方法
     * @param s
     * @param offset 连续不考虑长度
     * @return
     */
    public static void put(long s, long offset, short v){
        unsafe.putShort(s + (offset << MemoryConstants.OFFSET_SHORT), v);
    }
    /**
     * get byte 方法
     * @param s
     * @param offset 连续不考虑长度
     * @return
     */
    public static float getFloat(long s, long offset){
        return unsafe.getFloat(s + (offset << MemoryConstants.OFFSET_FLOAT));
    }
    /**
     * get byte 方法
     * @param s
     * @param offset 连续不考虑长度
     * @return
     */
    public static void put(long s, long offset, float v){
        unsafe.putFloat(s + (offset << MemoryConstants.OFFSET_FLOAT), v);
    }
    /**
     * get byte 方法
     * @param s
     * @param offset 连续不考虑长度
     * @return
     */
    public final static double getDouble(long s, long offset){
        return unsafe.getDouble(s + (offset << MemoryConstants.OFFSET_DOUBLE));
    }
    /**
     * get byte 方法
     * @param s
     * @param offset 连续不考虑长度
     * @return
     */
    public static void put(long s, long offset, double v){
        unsafe.putDouble(s + (offset << MemoryConstants.OFFSET_DOUBLE), v);
    }
}
