package com.fr.bi.cal.analyze.cal.sssecret;

import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.api.ICubeTableService;
import com.finebi.cube.conf.table.BIBusinessTable;
import com.finebi.cube.conf.table.BusinessTable;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.cal.analyze.cal.result.AllCalNode;
import com.fr.bi.cal.analyze.cal.result.Node;
import com.fr.bi.cal.analyze.cal.store.GroupKey;
import com.fr.bi.cal.analyze.cal.store.UserRightColumnKey;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.engine.cal.NodeResultDealer;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.report.result.DimensionCalculator;
import com.fr.bi.stable.report.result.TargetCalculator;
import com.fr.bi.stable.structure.CubeValueEntryNode;
import com.fr.bi.stable.utils.BIServerUtils;
import com.fr.general.ComparatorUtils;

import java.util.Arrays;

/**
 * Created by loy on 16/6/22.
 */
public class AllCalSingleDimensionGroup extends NoneDimensionGroup implements ISingleDimensionGroup {

    private static AllCalSingleDimensionGroup allCal;

    protected volatile AllCalNode root;

    protected transient DimensionCalculator[] pcolumns;

    private boolean doSort;

    public AllCalSingleDimensionGroup(BusinessTable tableKey, DimensionCalculator[] pcolumns,  GroupValueIndex gvi, ICubeDataLoader loader, boolean doSort) {
        this.tableKey = tableKey;
        this.loader = loader;
        this.pcolumns = pcolumns;
        this.doSort = doSort;
        this.initRoot(gvi);
        if (isTurnOnWhenInit()) {
            turnOnExecutor();
        }
        allCal = this;
    }

    public AllCalSingleDimensionGroup(int[] colindex){
        AllCalSingleDimensionGroup rootNode = allCal;
        this.tableKey = rootNode.tableKey;
        this.loader = rootNode.loader;
        this.pcolumns = rootNode.pcolumns;
        this.doSort = rootNode.doSort;
        Node n = rootNode.root;
        for (int idx : colindex){
            if(idx == -1){
                break;
            }
            n = n.getChild(idx);
        }
        this.root = (AllCalNode)n;
    }

    public static AllCalSingleDimensionGroup createInstance(BusinessTable tableKey, DimensionCalculator[] pcolumns, int[] pckindex, GroupValueIndex gvi, Object[] data, int ckIndex, ICubeDataLoader loader, boolean doSort) {
//        int[] empty = new int[pckindex.length];
//        Arrays.fill(empty, -1);
//        if(!Arrays.equals(empty, pckindex)){
//            return new AllCalSingleDimensionGroup(pckindex);
//        }

        if (allCal != null && loader == null) {
            int[] empty = new int[pckindex.length];
            Arrays.fill(empty, -1);
            return new AllCalSingleDimensionGroup(empty);
        }
        if (checkShouldCalculateAll(tableKey, pcolumns, gvi, data, ckIndex, loader)) {
            return new AllCalSingleDimensionGroup(tableKey, pcolumns, gvi, loader, doSort);
        }
        int[] colindex = new int[ckIndex];
        Arrays.fill(colindex, -1);
        int[] cp = colindex.clone();
        //TODO 指标筛选操作需要优化
        findGviInChildren(colindex, 0, allCal.root, gvi);
        if(Arrays.equals(colindex, cp)){
            return new AllCalSingleDimensionGroup(tableKey, pcolumns, gvi, loader, doSort);
        }
        return new AllCalSingleDimensionGroup(colindex);
    }

    private static boolean checkShouldCalculateAll(BusinessTable tableKey, DimensionCalculator[] pcolumns,  GroupValueIndex gvi, Object[] data, int ckIndex, ICubeDataLoader loader){
        if(allCal == null){
            return true;
        }
        if(ckIndex == 0){
            return true;
        }
        if(allCal.tableKey != tableKey){
            return true;
        }
        return false;
    }

    private static boolean findGviInChildren(int[] index, int deep, Node p, GroupValueIndex gvi){
        for (int i = 0; i < p.getChildLength(); i++){
            Node child = p.getChild(i);
//            if(child.getGroupValueIndex().equals(gvi)){
//                index[deep] = i;
//                return true;
//            }
//            else if(child.getChilds() != null && child.getChilds().size() > 0){
//                if(deep + 1 < index.length) {
//                    boolean r = findGviInChildren(index, deep + 1, child, gvi);
//                    if (r) {
//                        return true;
//                    }
//                }
//            }

            index[deep] = i;
            if(deep + 1 == index.length){
                if(child.getGroupValueIndex().equals(gvi)){
                    return true;
                }
            }
            else if(child.getChilds() != null && child.getChilds().size() > 0){
                if(deep + 1 < index.length) {
                    boolean r = findGviInChildren(index, deep + 1, child, gvi);
                    if (r) {
                        return true;
                    }
                }
            }
        }
        index[deep] = -1;
        return false;
    }

    protected boolean isTurnOnWhenInit() {
        return true;
    }

    public void turnOnExecutor() {
        NodeResultDealer dealer;
        if(doSort) {
            boolean[] sortType = new boolean[pcolumns.length];
            for (int i = 0; i < pcolumns.length; i++) {
                sortType[i] = pcolumns[i].getSortType() != BIReportConstant.SORT.DESC;
            }
            dealer = BIServerUtils.createAllCalDimensonDealer(pcolumns, null, sortType, loader);
        }
        else{
            dealer = BIServerUtils.createAllCalDimensonDealer(pcolumns, null, loader);
        }
        CubeValueEntryNode calRootNode = new CubeValueEntryNode();
        dealer.dealWithNode(root.getGroupValueIndex(), calRootNode);
        copyNode(calRootNode, root, 0);
    }

    private void copyNode(CubeValueEntryNode calParentNode, AllCalNode parentNode, int deep){
        CubeValueEntryNode[] calChildNodes = calParentNode.getChildren();
        for(CubeValueEntryNode calChildNode : calChildNodes){
            AllCalNode n = new AllCalNode(pcolumns[deep], calChildNode.getT());
            n.setGroupValueIndex(calChildNode.getGvi());
            parentNode.addChild(n);
            if(calChildNode.getChildren() != null && calChildNode.getChildren().length > 0){
                copyNode(calChildNode, n, deep + 1);
            }
        }
    }

    protected void initRoot(GroupValueIndex gvi) {
        root = new AllCalNode(null, null);
        root.setGroupValueIndex(gvi);

    }

    @Override
    public int getChildIndexByValue(Object value) {
        return root.getIndexByValue(value, doSort);
    }

    @Override
    public NoneDimensionGroup getChildDimensionGroup(int row) {
        AllCalNode node = (AllCalNode) root.getChild(row);
        if(node == null){
            return NoneDimensionGroup.NULL;
        }
        return createDimensionGroup(tableKey, node.getGroupValueIndex(), getLoader());
    }

    @Override
    public Object getChildData(int row) {
        if(row > root.getChildLength() - 1){
            throw GroupOutOfBoundsException.create(-1);
        }
        AllCalNode node = (AllCalNode) root.getChild(row);
        return node.getData();
    }

    @Override
    public String getChildShowName(int row) {
        if(row > root.getChildLength() - 1){
            throw GroupOutOfBoundsException.create(-1);
        }
        AllCalNode node = (AllCalNode) root.getChild(row);
        return node.getShowValue();
    }

    @Override
    public Node getChildNode(int row) {
        AllCalNode child = (AllCalNode) root.getChild(row);
        if (child == null) {
            if (row == 0) {
                child = createEmptyChild();
                addRootChild(child);
            } else {
                return null;
            }
        }
        return child;
    }

    protected AllCalNode createEmptyChild() {
        AllCalNode child = new AllCalNode(null, null);
        GroupValueIndex gvi = root.getGroupValueIndex();
        child.setGroupValueIndex(gvi);
        return child;
    }

    private void addRootChild(AllCalNode child) {
        root.addChild(child);
    }

    private AllCalNode getChild(int row) {
        if (row < getCurrentTotalRow()) {
            return (AllCalNode) root.getChild(row);
        }
        return null;
    }

    @Override
    public Node getRoot() {
        return root;
    }

    @Override
    public void release() {

    }

    @Override
    public int getCurrentTotalRow() {
        return root.getChildLength();
    }
}
