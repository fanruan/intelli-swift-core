package com.fr.bi.stable.io.newio;

public abstract class NIOReadWriter {


    protected abstract long getPageStep();

    protected abstract void releaseChild();

    protected abstract void initChild();

}