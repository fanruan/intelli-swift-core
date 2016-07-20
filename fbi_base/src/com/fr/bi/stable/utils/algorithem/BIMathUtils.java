package com.fr.bi.stable.utils.algorithem;

import java.util.Random;

/**
 * Created by 小灰灰 on 2016/7/20.
 */
public class BIMathUtils {

    static public final float E = 2.7182818f;

    static public Random random = new BIRandom();

    static public int random (int range) {
        return random.nextInt(range + 1);
    }

    static public int nextPowerOfTwo (int value) {
        if (value == 0) return 1;
        value--;
        value |= value >> 1;
        value |= value >> 2;
        value |= value >> 4;
        value |= value >> 8;
        value |= value >> 16;
        return value + 1;
    }


}
