package com.fr.swift.cloud.rpc.compress;


/**
 * @author Heng.J
 * @date 2021/7/30
 * @description
 * @since swift-1.2.0
 */
public interface Compressor {

    byte[] decompress(byte[] data) throws Exception;

    byte[] compress(byte[] data) throws Exception;
}
