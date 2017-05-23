package com.fr.bi.stable.structure.array;

/**
 * Created by daniel on 2017/2/10.
 */
public final class MemoryConstants {

    public final static int OFFSET_BYTE = 0;
    public final static int OFFSET_CHAR = 1;
    public final static int OFFSET_SHORT = OFFSET_CHAR;
    public final static int OFFSET_INT = 2;
    public final static int OFFSET_FLOAT = OFFSET_INT;
    public final static int OFFSET_LONG = 3;
    public final static int OFFSET_DOUBLE = OFFSET_LONG;

    public final static int STEP_BYTE = 1 << OFFSET_BYTE;
    public final static int STEP_CHAR = 1 << OFFSET_CHAR;
    public final static int STEP_SHORT = 1 << OFFSET_SHORT;
    public final static int STEP_INT = 1 << OFFSET_INT;
    public final static int STEP_FLOAT = 1 << OFFSET_FLOAT;
    public final static int STEP_LONG = 1 << OFFSET_LONG;
    public final static int STEP_DOUBLE = 1 << OFFSET_DOUBLE;
}
