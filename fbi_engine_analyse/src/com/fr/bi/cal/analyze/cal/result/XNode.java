package com.fr.bi.cal.analyze.cal.result;

import com.fr.bi.base.FinalInt;
import com.fr.bi.conf.report.widget.field.dimension.BIDimension;
import com.fr.bi.report.key.TargetGettingKey;
import com.fr.bi.report.result.BICrossNode;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

/**
 * Created by 小灰灰 on 2017/7/6.
 */
public class XNode implements BICrossNode {
    private Node top;
    private Node left;

    public XNode(Node top, Node left) {
        this.top = top;
        this.left = left;
    }

    public Node getLeft() {
        return left;
    }

    public Node getTop() {
        return top;
    }

    public JSONObject toJSONObject(BIDimension[] rowDimension, BIDimension[] colDimension, TargetGettingKey[] keys, boolean showTopSum) throws JSONException {
        JSONObject jo = JSONObject.create();
        Node topIndex = new Node(0);
        FinalInt finalInt = new FinalInt();
        finalInt.value = 0;
        buildTopIndexNode(topIndex, top, showTopSum, finalInt);
        //只有一个节点的时候居然要显示汇总，这边判断不符合常理，cubeindexloader里处理了，放再第二个位置上
        if (topIndex.getTotalLength() == 1){
            topIndex.setData(1);
        }
        jo.put("l", ((XLeftNode)left).toJSONObject(rowDimension, keys, topIndex, -1));
        jo.put("t", top.toTopJSONObject(colDimension, keys, -1));
        return jo;
    }

    //fixme xleftnode已经是横向的结果了，为了兼容前台，先根据top生成一个node，再根据之前旧的数据结构输出到前台。
    private void buildTopIndexNode(Node topIndex, Node top, boolean showSum, FinalInt finalInt) {
        for (Node n : top.getChilds()) {
            Node child = new Node(0);
            topIndex.addChild(child);
            buildTopIndexNode(child, n, showSum ,finalInt);
        }
        if ((top.getChildLength() == 0 || (showSum && top.getChildLength() != 1))) {
            topIndex.setData(finalInt.value);
            finalInt.value++;
        }
    }

    @Override
    public ResultType getResultType() {

        return ResultType.BICROSS;
    }
}
