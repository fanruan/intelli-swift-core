package com.fr.bi.field.filtervalue.string.onevaluefilter;

public class StringLikeFilterValue extends StringOneValueFilterValue {
    /**
	 * 
	 */
	private static final long serialVersionUID = 4387789363108952430L;
	private static String XML_TAG = "StringLikeFilterValue";


    /**
     * 是否包含
     *
     * @param key 字段
     * @return 包含
     */
    @Override
    public boolean isMatchValue(String key) {
        return key.indexOf(value) != -1;
    }


}