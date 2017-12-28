package com.finebi.base.utils.data.random;


import com.fr.stable.StringUtils;

import java.util.UUID;

/**
 * Created by andrew_asa on 2017/11/3.
 */
public class RandomUtils {

    public static String getUUID() {

        return UUID.randomUUID().toString().replaceAll("-", StringUtils.EMPTY);
    }

}
