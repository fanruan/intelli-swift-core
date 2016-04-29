package com.fr.bi.field.filtervalue.string.onevaluefilter;

public class StringEndWithFilterValue extends StringOneValueFilterValue {
    /**
	 * 
	 */
	private static final long serialVersionUID = 3158079887486027107L;
	private static String XML_TAG = "StringEndWithFilterValue";
    /* (non-Javadoc)
     * @see com.fr.bi.report.data.dimension.filter.value.string.StringFilterValue#showNode(com.fr.bi.cube.engine.result.Noder)
	 */

    /**
     * 是否包含
     *
     * @param key 字段
     * @return 包含
     */
    @Override
    public boolean isMatchValue(String key) {
        return key.endsWith(value);
    }


}