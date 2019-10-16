package com.fr.swift.beans.factory.classreading.basic.attribute;

import com.fr.swift.beans.factory.classreading.basic.reader.IntReader;

import java.io.InputStream;

/**
 * This class created on 2018/12/3
 *
 * @author Lucifer
 * @description todo 预留，暂时没用到
 * @since Advanced FineBI 5.0
 */
public class ExceptionTable {
    private int startPc;
    private int endPc;
    private int handlerPc;
    private int catchType;

    public void read(InputStream inputStream) {
        startPc = IntReader.read(inputStream);
        endPc = IntReader.read(inputStream);
        handlerPc = IntReader.read(inputStream);
        catchType = IntReader.read(inputStream);
    }

    public int getStartPc() {
        return startPc;
    }

    public int getEndPc() {
        return endPc;
    }

    public int getHandlerPc() {
        return handlerPc;
    }

    public int getCatchType() {
        return catchType;
    }
}
