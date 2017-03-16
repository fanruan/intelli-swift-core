package com.finebi.cube;

/**
 * This class created on 2016/6/3.
 *
 * @author Connery
 * @since 4.0
 */
public class CubeVersionImpl implements CubeVersion {
    private long version;

    public CubeVersionImpl(long version) {
        this.version = version;
    }

    @Override
    public long getValue() {
        return version;
    }

    @Override
    public void recordValue(long value) {

    }
}
