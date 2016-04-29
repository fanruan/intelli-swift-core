package com.fr.bi.field.filtervalue.string.onevaluefilter;

import com.fr.bi.stable.utils.program.BIPhoneticismUtils;

/**
 * @author windy
 */
public class StringNotVagueLikeFilterValue extends StringOneValueFilterValue  {

    private static final long serialVersionUID = 6789923451708317231L;
    private static String XML_TAG = "StringNotVagueLikeFilterValue";

    @Override
    public boolean isMatchValue(String key) {
        String keyPinyin = BIPhoneticismUtils.getPingYin(key);
        return keyPinyin.indexOf(value) == -1 && key.indexOf(value) == -1;
    }
}
