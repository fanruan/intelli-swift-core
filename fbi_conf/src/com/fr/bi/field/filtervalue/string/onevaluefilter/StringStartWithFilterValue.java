package com.fr.bi.field.filtervalue.string.onevaluefilter;

public class StringStartWithFilterValue extends StringOneValueFilterValue {
    /**
	 * 
	 */
	private static final long serialVersionUID = 8978803144186435914L;
	private static String XML_TAG = "StringStartWithFilterValue";

    /**
     * 是否包含
     *
     * @param key 字段
     * @return 包含
     */
    @Override
    public boolean isMatchValue(String key) {
        return key.startsWith(value);
    }

}