package com.finebi.base.constant;

/**
 * Created by kary on 2017/12/27.
 */
public enum FineEngineType {
    Cube(0),Direct(1);
    int engineType;
    FineEngineType(int engineType){
        this.engineType=engineType;
    }

    public int getEngineType() {
        return engineType;
    }
}
