package com.fr.bi.fs;

/**
 * Created by wang on 2017/5/16.
 */
public interface BIUserEditViewAuthProvider {
    String XML_TAG = "BIUserEditViewAuth";

    int getUserEditViewAuth(long userId) throws Exception;
}