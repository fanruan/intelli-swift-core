package com.fr.bi.cal.analyze.cal.utils;

import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.api.ICubeTableService;
import com.finebi.cube.relation.BITableSourceRelation;
import com.fr.base.TableData;
import com.fr.bi.cal.analyze.cal.result.CrossNode;
import com.fr.bi.cal.analyze.cal.result.Node;
import com.fr.bi.conf.report.widget.field.dimension.BIDimension;
import com.fr.bi.field.target.target.BISummaryTarget;
import com.fr.bi.stable.connection.DirectTableConnectionFactory;
import com.fr.bi.stable.engine.index.utils.TableIndexUtils;
import com.fr.bi.stable.report.key.TargetGettingKey;
import com.fr.bi.stable.report.result.DimensionCalculator;
import com.fr.bi.stable.report.result.LightNode;
import com.fr.data.impl.EmbeddedTableData;
import edu.emory.mathcs.backport.java.util.Arrays;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

/**
 * Created by 小灰灰 on 2014/10/10.
 */
public class CubeReadingUtils {

    public static void setSibing(LightNode last, LightNode first) {
        LightNode lastChild = last.getLastChild();
        LightNode firstChild = first.getFirstChild();
        if (lastChild != null && firstChild != null) {
            lastChild.setSibling(firstChild);
            setSibing(lastChild, firstChild);
        }
    }

    /**
     * @param node
     */
    private static void setSibing(Node node) {
        Node temp = null;
        for (int i = 0; i < node.getChildLength(); i++) {
            Node n = node.getChild(i);
            setSibing(n);
            if (temp != null) {
                temp.setSibling(n);
                setSibing(temp, n);
            }
            temp = n;
        }
    }

    public static void setSibing(CrossNode last, CrossNode first) {
        CrossNode lastChild = last.getLeftLastChild();
        CrossNode firstChild = first.getLeftFirstChild();
        if (lastChild != null && firstChild != null) {
            lastChild.setBottomSibling(firstChild);
            setSibing(lastChild, firstChild);
        }
        lastChild = last.getTopLastChild();
        firstChild = first.getTopFirstChild();
        if (lastChild != null && firstChild != null) {
            lastChild.setRightSibling(firstChild);
            setSibing(lastChild, firstChild);
        }
    }

    /**
     * 创建图表数组
     *
     * @param node    node节点
     * @param rows    行
     * @param summary 目标
     * @return tableData对象
     */

    public static TableData createChartTableData(Node node, BIDimension[] rows, BISummaryTarget[] summary) {
        setSibing(node);
        EmbeddedTableData resData = new EmbeddedTableData();
        for (int i = 0, len = rows.length; i < len; i++) {
            resData.addColumn(rows[i].getValue(), String.class);
        }
        for (int i = 0, len = summary.length; i < len; i++) {
            resData.addColumn(summary[i].getValue(), Double.class);
        }
        Node n = node;
        while (n.getFirstChild() != null) {
            n = n.getFirstChild();
        }
        int slen = summary.length;
        TargetGettingKey[] key = new TargetGettingKey[slen];
        for (int i = 0; i < slen; i++) {
            key[i] = new TargetGettingKey(summary[i].createSummaryCalculator().createTargetKey(), summary[i].getValue());
        }
        while (n != null) {
            List rowList = new ArrayList();
            Node temp = n;
            for (int i = 0; i < slen; i++) {
                Number v = temp.getSummaryValue(key[i]);
                rowList.add(v == null ? 0 : v);
            }
            int i = rows.length;
            while (temp.getParent() != null) {
                Object data = temp.getData();

                BIDimension dim = rows[--i];
                String s = dim.toString(data);
                rowList.add(0, s);
                temp = temp.getParent();
            }
            resData.addRow(rowList);
            n = n.getSibling();
        }
        return resData;
    }


    /**
     * TODO 这里只是优化父元素的读取，尚未优化子表
     * 获取相同路径上的维度的值用于运算
     *
     * @param value      当前父的值
     * @param currentKey 父的列信息
     * @param childKey   子的维度信息
     * @return 子维度的可能值
     */
    public static Object[] getChildValuesAsParentOrSameTable(Object value, DimensionCalculator currentKey, final DimensionCalculator childKey, ICubeDataLoader loader) {
        BITableSourceRelation[] relationChild = childKey.getRelationList().toArray(new BITableSourceRelation[childKey.getRelationList().size()]);
        if (relationChild == null) {
            relationChild = new BITableSourceRelation[0];
        }
        BITableSourceRelation[] currentRelation = currentKey.getRelationList().toArray(new BITableSourceRelation[currentKey.getRelationList().size()]);
        if (currentRelation == null) {
            currentRelation = new BITableSourceRelation[0];
        }
        BITableSourceRelation[] targetRelation = new BITableSourceRelation[relationChild.length - currentRelation.length];
        System.arraycopy(relationChild, 0, targetRelation, 0, targetRelation.length);
        final TreeSet treeSet = new TreeSet(childKey.getComparator());
        ICubeTableService ti = loader.getTableIndex(currentKey.getField().getTableBelongTo().getTableSource());
        ICubeTableService cTi = loader.getTableIndex(childKey.getField().getTableBelongTo().getTableSource());
        Object[] res = TableIndexUtils.getValueFromGvi(cTi, cTi.getColumnIndex(childKey.getField()),
                ti.getIndexes(ti.getColumnIndex(currentKey.getField()), new Object[]{value}),
                DirectTableConnectionFactory.createConnectionRow(Arrays.asList(targetRelation), loader));
        for (int i = 0; i < res.length; i++) {
            if (res[i] != null) {
                treeSet.add(res[i]);
            }
        }
        return treeSet.toArray(new Object[treeSet.size()]);
    }



}