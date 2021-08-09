package com.fr.swift.cloud.rpc.compress;

import com.fr.swift.cloud.util.Strings;
import com.fr.swift.cloud.util.compress.GZIPUtils;

import java.util.Arrays;

/**
 * @author Heng.J
 * @date 2021/7/30
 * @description
 * @since swift-1.2.0
 */
public enum CompressMode implements Compressor {

    NONE(Strings.EMPTY) {
        @Override
        public byte[] decompress(byte[] data) throws Exception {
            return data;
        }

        @Override
        public byte[] compress(byte[] data) throws Exception {
            return data;
        }
    }, GZIP("gzip") {
        @Override
        public byte[] decompress(byte[] data) throws Exception {
            return GZIPUtils.decompress(data);
        }

        @Override
        public byte[] compress(byte[] data) throws Exception {

            return GZIPUtils.compress(data);
        }
    };

    private final String algorithm;

    private int maxObjectSize;

    CompressMode(String algorithm) {
        this.algorithm = algorithm;
    }

    public static CompressMode getEnum(String algorithm) {
        return Arrays.stream(CompressMode.values())
                .filter(value -> value.algorithm.equals(algorithm))
                .findFirst()
                .orElse(NONE);
    }

    public boolean isCompressNeeded() {
        return !Strings.isEmpty(algorithm);
    }

    public void setMaxObjectSize(int maxObjectSize) {
        this.maxObjectSize = maxObjectSize;
    }

    public int getMaxObjectSize() {
        return maxObjectSize;
    }

}
