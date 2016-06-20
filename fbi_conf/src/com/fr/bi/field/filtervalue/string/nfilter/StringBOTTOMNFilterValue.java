/**
 *
 */
package com.fr.bi.field.filtervalue.string.nfilter;


import com.finebi.cube.api.ICubeDataLoader;
import com.fr.bi.conf.report.widget.field.filtervalue.NFilterValue;
import com.fr.bi.stable.report.key.TargetGettingKey;
import com.fr.bi.stable.report.result.LightNode;


public class StringBOTTOMNFilterValue extends StringNFilterValue implements NFilterValue{

    /**
	 * 
	 */
	private static final long serialVersionUID = -4920706147122905307L;
	private static String XML_TAG = "StringBOTTOMNFilterValue";

    /**
     * 是否显示记录
     *
     * @param node 节点
     * @return 是否显示
     */
    @Override
    public boolean showNode(LightNode node, TargetGettingKey targetKey, ICubeDataLoader loader) {
        LightNode parentNode = node.getParent();
        int count = parentNode.getChildLength();
        if (N < 1){
            return false;
        }
        if (N >= count){
            return true;
        }
        Comparable nline;
        if (N < count * 2){
            nline = parentNode.getChildBottomNValueLine(N);
        } else {
            nline = parentNode.getChildTOPNValueLine(count + 1 - N);
        }
        return nline != null && node.getComparator().compare(nline, node.getData()) <= 0;
    }
}