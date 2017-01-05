/**
 *
 */
package com.fr.bi.field.filtervalue.string.nfilter;


import com.finebi.cube.api.ICubeDataLoader;
import com.fr.bi.base.annotation.BICoreField;
import com.fr.bi.conf.report.widget.field.filtervalue.NFilterValue;
import com.fr.bi.stable.report.key.TargetGettingKey;
import com.fr.bi.stable.report.result.BINode;


public class StringBOTTOMNFilterValue extends StringNFilterValue implements NFilterValue{

    /**
	 * 
	 */
	private static final long serialVersionUID = -4920706147122905307L;
	private static String XML_TAG = "StringBOTTOMNFilterValue";

    @BICoreField
    private String CLASS_TYPE = "StringBOTTOMNFilterValue";

    /**
     * 是否显示记录
     *
     * @param node 节点
     * @return 是否显示
     */
    @Override
    public boolean showNode(BINode node, TargetGettingKey targetKey, ICubeDataLoader loader) {
        throw new RuntimeException("Dimension N Filter should dealWith In Iterator, not after calculate all children");
    }
}