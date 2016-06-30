package com.fr.bi.cal.analyze.cal.sssecret;

import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.api.ICubeTableService;
import com.finebi.cube.conf.table.BusinessTable;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.cal.analyze.cal.result.AllCalNode;
import com.fr.bi.cal.analyze.cal.result.Node;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.engine.cal.NodeResultDealer;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.report.result.DimensionCalculator;
import com.fr.bi.stable.report.result.TargetCalculator;
import com.fr.bi.stable.structure.CubeValueEntryNode;
import com.fr.bi.stable.utils.BIServerUtils;

/**
 * Created by loy on 16/6/22.
 */
public class AllCalSingleDimensionGroup extends NoneDimensionGroup implements ISingleDimensionGroup {

    protected TargetCalculator calculator;
    protected volatile AllCalNode root;

//    protected DimensionCalculator column;
    protected transient DimensionCalculator[] pcolumns;

//    protected transient int[] pckindex;

//    protected transient Object[] data;

//    protected transient int ckIndex;

//    private transient boolean useRealData = true;
//
//    private transient int demoGroupLimit = BIBaseConstant.PART_DATA_GROUP_LIMIT;

    private ICubeTableService cubeTableService;

    public AllCalSingleDimensionGroup(BusinessTable tableKey, DimensionCalculator[] pcolumns, /*int[] pckindex, DimensionCalculator column, Object[] data, int ckIndex,*/ GroupValueIndex gvi, ICubeDataLoader loader/*, boolean useRealData, int demoGroupLimit*/) {
        this.tableKey = tableKey;
        this.loader = loader;
        this.cubeTableService = loader.getTableIndex(tableKey.getTableSource());
        this.pcolumns = pcolumns;
//        this.column = column;
//        this.pckindex = pckindex;
//        this.ckIndex = ckIndex;
//        this.data = data;
//        this.useRealData = useRealData;
        this.initRoot(gvi);
//        this.demoGroupLimit = demoGroupLimit;
        if (isTurnOnWhenInit()) {
            turnOnExecutor();
        }
    }

    protected boolean isTurnOnWhenInit() {
        return true;
    }

    public void turnOnExecutor() {
        BIKey[] keys = new BIKey[pcolumns.length];
        boolean[] sortType = new boolean[pcolumns.length];
        for (int i = 0; i < pcolumns.length; i++) {
            keys[i] = pcolumns[i].createKey();
            sortType[i] = pcolumns[i].getSortType() != BIReportConstant.SORT.DESC;
        }
        NodeResultDealer dealer = BIServerUtils.createDimensonDealer(keys, null, sortType);
        CubeValueEntryNode calRootNode = new CubeValueEntryNode();
        dealer.dealWithNode(cubeTableService, root.getGroupValueIndex(), calRootNode);
        copyNode(calRootNode, root, 0);
    }

    private void copyNode(CubeValueEntryNode calParentNode, AllCalNode parentNode, int deep){
        CubeValueEntryNode[] calChildNodes = calParentNode.getChildList();
        for(CubeValueEntryNode calChildNode : calChildNodes){
            AllCalNode n = new AllCalNode(pcolumns[deep], calChildNode.getT());
            n.setGroupValueIndex(calChildNode.getGvi());
            parentNode.addChild(n);
            if(calChildNode.getChildList() != null && calChildNode.getChildList().length > 0){
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
        return root.getIndexByValue(value);
    }

    @Override
    public NoneDimensionGroup getChildDimensionGroup(int row) {
        AllCalNode node = (AllCalNode) root.getChild(row);
        return createDimensionGroup(tableKey, node.getGroupValueIndex(), getLoader());
    }

    @Override
    public Object getChildData(int row) {
        AllCalNode node = (AllCalNode) root.getChild(row);
        return node.getData();
    }

    @Override
    public String getChildShowName(int row) {
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
        notifyMainThread();
    }

    private void notifyMainThread() {
        synchronized (AllCalSingleDimensionGroup.this) {
            AllCalSingleDimensionGroup.this.notifyAll();
        }
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
