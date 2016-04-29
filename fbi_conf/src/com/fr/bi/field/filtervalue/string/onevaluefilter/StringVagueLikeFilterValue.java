package com.fr.bi.field.filtervalue.string.onevaluefilter;

import com.fr.bi.stable.utils.program.BIPhoneticismUtils;

/**
 * 模糊包含(拼音首字母包含)
 * @author windy
 */
public class StringVagueLikeFilterValue  extends StringOneValueFilterValue  {

    private static final long serialVersionUID = 8800780063479152525L;
    private static String XML_TAG = "StringVagueLikeFilterValue";

    @Override
    public boolean isMatchValue(String key) {
        String keyPinyin = BIPhoneticismUtils.getPingYin(key);
        return key.indexOf(value) !=-1 || keyPinyin.indexOf(value) != -1;
    }
}
