package com.fr.swift.cube.nio;

public abstract class NIOReadWriter {


    protected abstract long getPageStep();

    protected abstract void releaseChild();

    protected abstract void initChild();

}