package com.finebi.base.utils.data.characters;


import com.finebi.base.utils.data.string.StringUtils;

/**
 * Created by andrew_asa on 2017/10/10.
 */
public class CharUtils {

    public static char[] stringToCharacters(String s) {

        if (StringUtils.isEmpty(s)) {
            return new char[0];
        }
        return s.toCharArray();
    }
}
