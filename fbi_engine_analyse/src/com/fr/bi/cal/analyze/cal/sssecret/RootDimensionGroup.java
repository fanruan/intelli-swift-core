package com.fr.bi.cal.analyze.cal.sssecret;

import com.finebi.cube.api.ICubeTableService;
import com.finebi.cube.api.ICubeValueEntryGetter;
<<<<<<< HEAD
=======
import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.conf.BICubeConfigureCenter;
import com.finebi.cube.conf.pack.data.BIPackageID;
import com.finebi.cube.conf.table.BIBusinessTable;
>>>>>>> 67b55d486e769f445942f15883303ca839ffd092
import com.finebi.cube.conf.table.BusinessTable;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.cal.analyze.cal.index.loader.MetricGroupInfo;
import com.fr.bi.cal.analyze.cal.index.loader.TargetAndKey;
import com.fr.bi.cal.analyze.cal.result.NodeExpander;
import com.fr.bi.cal.analyze.cal.sssecret.diminfo.MergeIteratorCreator;
import com.fr.bi.cal.analyze.session.BISession;
<<<<<<< HEAD
=======
import com.fr.bi.conf.base.auth.data.BIPackageAuthority;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.conf.report.BIWidget;
import com.fr.bi.conf.report.widget.field.target.filter.TargetFilter;
import com.fr.bi.field.dimension.calculator.DateDimensionCalculator;
import com.fr.bi.field.dimension.calculator.NoneDimensionCalculator;
import com.fr.bi.field.dimension.calculator.NumberDimensionCalculator;
import com.fr.bi.field.dimension.calculator.StringDimensionCalculator;
import com.fr.bi.field.filtervalue.date.evenfilter.DateKeyTargetFilterValue;
import com.fr.bi.field.filtervalue.string.rangefilter.StringINFilterValue;
import com.fr.bi.field.target.filter.TargetFilterFactory;
import com.fr.bi.stable.constant.BIBaseConstant;
import com.fr.bi.stable.data.BITable;
>>>>>>> 67b55d486e769f445942f15883303ca839ffd092
import com.fr.bi.stable.data.db.ICubeFieldSource;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.engine.index.key.IndexKey;
<<<<<<< HEAD
=======
import com.fr.bi.stable.exception.BITableUnreachableException;
import com.fr.bi.stable.gvi.GVIUtils;
>>>>>>> 67b55d486e769f445942f15883303ca839ffd092
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.report.result.DimensionCalculator;
import com.fr.bi.stable.utils.program.BINonValueUtils;
import com.fr.cache.list.IntList;
import com.fr.fs.control.UserControl;
import com.fr.general.ComparatorUtils;
import com.fr.web.core.SessionDealWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 根，用于保存游标等其他信息，感觉可以优化成一个游标就完了
 *
 * @author Daniel
 */
public class RootDimensionGroup implements IRootDimensionGroup {

    protected List<MetricGroupInfo> metricGroupInfoList;
    protected MergeIteratorCreator[] mergeIteratorCreators;
    protected BISession session;
    protected boolean useRealData;

    protected ICubeValueEntryGetter[][] getters;
    protected DimensionCalculator[][] columns;
    protected ICubeTableService[] tis;
    private ISingleDimensionGroup[] singleDimensionGroupCache;
    protected BusinessTable[] metrics;
    protected List<TargetAndKey>[] summaryLists;
    protected NoneDimensionGroup root;
    protected int rowSize;

    protected RootDimensionGroup(){

    }

    public RootDimensionGroup(List<MetricGroupInfo> metricGroupInfoList, MergeIteratorCreator[] mergeIteratorCreators, BISession session, boolean useRealData) {
        this.metricGroupInfoList = metricGroupInfoList;
        this.mergeIteratorCreators = mergeIteratorCreators;
        this.session = session;
        this.useRealData = useRealData;
        init();
    }

    protected void init() {
        initIterator();
        initGetterAndRows();
        initRoot();
    }

    private void initIterator() {
        if (metricGroupInfoList == null || metricGroupInfoList.isEmpty()) {
            BINonValueUtils.beyondControl("invalid parameters");
        }
        rowSize = metricGroupInfoList.get(0).getRows().length;
        for (MetricGroupInfo info : metricGroupInfoList) {
            if (info.getRows().length != rowSize) {
                throw new RuntimeException("invalid parameters");
            }
        }
        this.singleDimensionGroupCache = new ISingleDimensionGroup[rowSize];
    }

    protected void initGetterAndRows() {
        getters = new ICubeValueEntryGetter[rowSize][metricGroupInfoList.size()];
        columns = new DimensionCalculator[rowSize][metricGroupInfoList.size()];
        tis = new ICubeTableService[metricGroupInfoList.size()];
        for (int i = 0; i < metricGroupInfoList.size(); i++) {
            DimensionCalculator[] rs = metricGroupInfoList.get(i).getRows();
            tis[i] = session.getLoader().getTableIndex(metricGroupInfoList.get(i).getMetric().getTableSource());
            for (int j = 0; j < rs.length; j++) {
                columns[j][i] = rs[j];
                getters[j][i] =  session.getLoader().getTableIndex(getSource(rs[j])).getValueEntryGetter(createKey(rs[j]), rs[j].getRelationList());
            }
        }
    }

    protected void initRoot() {
        metrics = new BusinessTable[metricGroupInfoList.size()];
        summaryLists = new ArrayList[metricGroupInfoList.size()];
        GroupValueIndex[] gvis = new GroupValueIndex[metricGroupInfoList.size()];
        for (int i = 0; i < metricGroupInfoList.size(); i++) {
            metrics[i] = metricGroupInfoList.get(i).getMetric();
            summaryLists[i] = metricGroupInfoList.get(i).getSummaryList();
            gvis[i] = metricGroupInfoList.get(i).getFilterIndex();
        }
        root = NoneDimensionGroup.createDimensionGroup(metrics, summaryLists, tis, gvis, session.getLoader());
    }

    private CubeTableSource getSource(DimensionCalculator column) {
        //多对多
        if (column.getDirectToDimensionRelationList().size() > 0) {
            ICubeFieldSource primaryField = column.getDirectToDimensionRelationList().get(0).getPrimaryField();
            return primaryField.getTableBelongTo();
        }
        return column.getField().getTableBelongTo().getTableSource();
    }

    private BIKey createKey(DimensionCalculator column) {
        //多对多
        if (column.getDirectToDimensionRelationList().size() > 0) {
            ICubeFieldSource primaryField = column.getDirectToDimensionRelationList().get(0).getPrimaryField();
            return new IndexKey(primaryField.getFieldName());
        }
        return column.createKey();
    }

    @Override
    public NoneDimensionGroup getRoot() {
        return root;
    }

    public int[] getValueStartRow(Object[] value) {
        IntList result = new IntList();
        getValueStartRow(root, value, 0, result);
        for (int i = value.length; i < rowSize; i++) {
            result.add(-1);
        }
        return result.toArray();
    }

    private void getValueStartRow(NoneDimensionGroup ng, Object[] value, int deep, IntList list) {
        if (deep >= value.length) {
            return;
        }
        ISingleDimensionGroup sg = getSingleDimensionGroupCache(value, ng, deep);
        int i = sg.getChildIndexByValue(value[deep]);
        if (i == -1) {
            return;
        }
        list.add(i);
        NoneDimensionGroup currentNg = sg.getChildDimensionGroup(i);
        if (currentNg == NoneDimensionGroup.NULL) {
            return;
        }
        getValueStartRow(currentNg, value, deep + 1, list);
    }

    private ISingleDimensionGroup getSingleDimensionGroupCache(Object[] value, NoneDimensionGroup ng, int deep) {
        return createSingleDimensionGroup(value, ng, deep);
    }

    /**
     * @param gv       表示一行的值
     * @param index    上一次游标的位置
     * @param deep     维度的层级
     * @param expander 展开信息
     * @param list     当前的游标
     */
    public ReturnStatus getNext(GroupConnectionValue gv,
                                 int[] index,
                                 int deep,
                                 NodeExpander expander,
                                 IntList list) {
        if (expander == null) {
            return ReturnStatus.GroupEnd;
        }
        if (rowSize == 0) {
            return ReturnStatus.Success;
        }
        ISingleDimensionGroup sg = getCacheDimensionGroup(gv, deep);
        //如果往下移动，行数就加1
        if (notNextChild(index, deep, expander, sg)) {
            index[deep]++;
        }
        ReturnStatus returnStatus = findCurrentValue(sg, gv, list, index[deep]);
        //如果越界，就一直往上移，直到不越界或者结束。
        while (returnStatus == ReturnStatus.GroupOutOfBounds) {
            //如果找到根节点还是越界，说明结束了。
            if (deep == 0) {
                return ReturnStatus.GroupEnd;
            }
            //把index数组deep位置后面的都清了，下移一位开始找，每次seek的时候这个while循环至多执行一次。
            Arrays.fill(index, deep, index.length, -1);
            list.remove(list.size() - 1);
            deep -= 1;
            gv = gv.getParent();
            sg = getCacheDimensionGroup(gv, deep);
            expander = expander.getParent();
            returnStatus = findCurrentValue(sg, gv, list, ++index[deep]);
        }
        //如果往下展开，就继续往下
        NodeExpander ex = expander.getChildExpander(sg.getChildShowName(index[deep]));
        if (ex != null && deep + 1 < index.length) {
            if (ReturnStatus.GroupEnd == getNext(gv.getChild(), index, deep + 1, ex, list)){
                return ReturnStatus.GroupEnd;
            }
        }
        return ReturnStatus.Success;
    }

    //最后一个维度或者初始化的情况或者没有展开的情况必定是往下的
    private boolean notNextChild(int[] index, int deep, NodeExpander expander, ISingleDimensionGroup sg) {
        if (index.length == deep + 1 || index[deep] == -1) {
            return true;
        }
        String showValue = sg.getChildShowName(index[deep]);
        return showValue != null && expander.getChildExpander(showValue) == null;
    }

    private ISingleDimensionGroup getCacheDimensionGroup(GroupConnectionValue gv, int deep) {
        if (singleDimensionGroupCache[deep] == null || !ComparatorUtils.equals(singleDimensionGroupCache[deep].getData(), getParentsValuesByGv(gv, deep))) {
            singleDimensionGroupCache[deep] = createSingleDimensionGroup(gv, deep);
        }
        return singleDimensionGroupCache[deep];
    }

    protected ISingleDimensionGroup createSingleDimensionGroup(GroupConnectionValue gv, int deep) {
        return createSingleDimensionGroup(getParentsValuesByGv(gv, deep), gv.getCurrentValue(), deep);
    }

    protected ISingleDimensionGroup createSingleDimensionGroup(Object[] data, NoneDimensionGroup ng, int deep) {
<<<<<<< HEAD
        return ng.createSingleDimensionGroup(columns[deep], getters[deep], data, mergeIteratorCreators[deep], useRealData);
=======
        ISingleDimensionGroup sg;
        if ((ComparatorUtils.equals(root.getTableKey(), BITable.BI_EMPTY_TABLE()) && deep > 0)) {
            sg = ng.createNoneTargetSingleDimensionGroup(cks, cks[deep], data, deep, getters[deep], getCKGvigetter(data, deep), useRealData);
        } else {
            sg = ng.createSingleDimensionGroup(cks, cks[deep], data, deep, getters[deep], useRealData);
        }
        return sg;
    }

    protected GroupValueIndex getCKGvigetter(GroupConnectionValue gv, int deep) {
        DimensionCalculator ck = cks[deep];
        GroupValueIndex gvi = iter.createFilterGvi(ck);
        int i = deep;
        GroupConnectionValue v = gv;
        while (i != 0) {
            i--;
            DimensionCalculator ckp = cks[i];
            Object value = v.getData();
            if (value == null || ckp.getRelationList() == null) {
                v = v.getParent();
                continue;
            }
            if (ckp instanceof DateDimensionCalculator) {
                Set<BIDateValue> currentSet = new HashSet<BIDateValue>();
                /**
                 * 螺旋分析这里会出现空字符串
                 */
                if (value instanceof Number) {
                    currentSet.add(BIDateValueFactory.createDateValue(ckp.getGroup().getType(), (Number) value));
                } else {
                    currentSet.add(null);
                }

                DateKeyTargetFilterValue dktf = new DateKeyTargetFilterValue(((DateDimensionCalculator) ckp).getGroupDate(), currentSet);
                GroupValueIndex pgvi = dktf.createFilterIndex(ckp, ck.getField().getTableBelongTo(), BICubeManager.getInstance().fetchCubeLoader(session.getUserId()), session.getUserId());
                if (pgvi != null) {
                    gvi = getCKGroupValueIndex(ck, gvi, pgvi);
                }
            } else if (ckp instanceof StringDimensionCalculator) {
                if (value == BIBaseConstant.EMPTY_NODE_DATA) {
                    v = v.getParent();
                    continue;
                }
                Set currentSet = ((StringDimensionCalculator) ckp).createFilterValueSet((String) value, session.getLoader());
                StringINFilterValue stf = new StringINFilterValue(currentSet);
                BITableRelationPath firstPath = null;
                try {
                    firstPath = BICubeConfigureCenter.getTableRelationManager().getFirstPath(session.getLoader().getUserId(), ck.getField().getTableBelongTo(), ckp.getField().getTableBelongTo());
                } catch (BITableUnreachableException e) {
                    continue;
                }
                if (ComparatorUtils.equals(ck.getField().getTableBelongTo(), ckp.getField().getTableBelongTo())) {
                    firstPath = new BITableRelationPath();
                }
                if (firstPath == null) {
                    continue;
                }
                GroupValueIndex pgvi = stf.createFilterIndex(new NoneDimensionCalculator(ckp.getField(), BIConfUtils.convert2TableSourceRelation(firstPath.getAllRelations())),
                        ck.getField().getTableBelongTo(), session.getLoader(), session.getUserId());
                gvi = getCKGroupValueIndex(ck, gvi, pgvi);
            } else if (ckp instanceof NumberDimensionCalculator) {
                if (value == BIBaseConstant.EMPTY_NODE_DATA) {
                    v = v.getParent();
                    continue;
                }
                BITableRelationPath firstPath = null;
                try {
                    firstPath = BICubeConfigureCenter.getTableRelationManager().getFirstPath(session.getLoader().getUserId(), ck.getField().getTableBelongTo(), ckp.getField().getTableBelongTo());
                } catch (BITableUnreachableException e) {
                    continue;
                }
                if (ComparatorUtils.equals(ck.getField().getTableBelongTo(), ckp.getField().getTableBelongTo())) {
                    firstPath = new BITableRelationPath();
                }
                if (firstPath == null) {
                    continue;
                }
                ckp.setRelationList(BIConfUtils.convert2TableSourceRelation(firstPath.getAllRelations()));
                GroupValueIndex pgvi = ckp.createNoneSortGroupValueMapGetter(ck.getField().getTableBelongTo(), session.getLoader()).getIndex(value);
                gvi = getCKGroupValueIndex(ck, gvi, pgvi);
            }
            if (widget instanceof TableWidget) {
                GroupValueIndex filterGvi = ((TableWidget) widget).getFilter().createFilterIndex(ck, ck.getField().getTableBelongTo(), session.getLoader(), session.getUserId());
                if (filterGvi != null) {
                    gvi = filterGvi.AND(gvi);
                }
            }
            v = v.getParent();
        }
        return gvi;
>>>>>>> 67b55d486e769f445942f15883303ca839ffd092
    }

    private GroupValueIndex getCKGroupValueIndex(DimensionCalculator ck, GroupValueIndex gvi, GroupValueIndex pgvi) {
        gvi = gvi.AND(pgvi);
        GroupValueIndex authGVI = getAuthGroupValueIndex(ck);
        gvi = gvi.AND(authGVI);
        return gvi;
    }

    private GroupValueIndex getAuthGroupValueIndex(DimensionCalculator ck) {
        Long userId = session.getUserId();
        GroupValueIndex authGVI = null;
        if (userId != UserControl.getInstance().getSuperManagerID()) {
            List<TargetFilter> filters = getAuthFilter(userId);
            for (int k = 0; k < filters.size(); k++) {
                if (authGVI == null) {
                    authGVI = filters.get(k).createFilterIndex(ck, ck.getField().getTableBelongTo(), session.getLoader(), userId);
                } else {
                    authGVI = GVIUtils.OR(authGVI, filters.get(k).createFilterIndex(ck, ck.getField().getTableBelongTo(), session.getLoader(), userId));
                }
            }
        }
        return authGVI;
    }

    protected Object[] getParentsValuesByGv(GroupConnectionValue groupConnectionValue, int deep) {
        ArrayList al = new ArrayList();
        GroupConnectionValue gv = groupConnectionValue;
        while (deep-- > 0) {
            al.add(gv.getData());
            gv = gv.getParent();
        }
        int len = al.size();
        Object[] obs = new Object[len];
        for (int i = 0; i < len; i++) {
            obs[i] = al.get(len - 1 - i);
        }
        return obs;
    }

    private ReturnStatus findCurrentValue(ISingleDimensionGroup sg, GroupConnectionValue gv, IntList list, int row) {
        NoneDimensionGroup nds = sg.getChildDimensionGroup(row);
        if (nds == NoneDimensionGroup.NULL) {
            return ReturnStatus.GroupOutOfBounds;
        }
        GroupConnectionValue ngv = createGroupConnectionValue(sg, row, nds);
        list.add(row);
        ngv.setParent(gv);
        return ReturnStatus.Success;
    }

    protected GroupConnectionValue createGroupConnectionValue(ISingleDimensionGroup sg, int row, NoneDimensionGroup nds) {
        GroupConnectionValue ngv = new GroupConnectionValue(sg.getChildData(row), nds);
        return ngv;
    }

    @Override
    public IRootDimensionGroup createClonedRoot() {
        RootDimensionGroup rootDimensionGroup = (RootDimensionGroup) createNew();
        rootDimensionGroup.metricGroupInfoList = metricGroupInfoList;
        rootDimensionGroup.mergeIteratorCreators = mergeIteratorCreators;
        rootDimensionGroup.session = session;
        rootDimensionGroup.useRealData = useRealData;
        rootDimensionGroup.getters = getters;
        rootDimensionGroup.columns = columns;
        rootDimensionGroup.tis = tis;
        rootDimensionGroup.singleDimensionGroupCache = singleDimensionGroupCache.clone();
        rootDimensionGroup.metrics = metrics;
        rootDimensionGroup.summaryLists = summaryLists;
        rootDimensionGroup.root = root;
        rootDimensionGroup.rowSize = rowSize;
        return rootDimensionGroup;
    }

    protected IRootDimensionGroup createNew(){
        return new RootDimensionGroup();
    }

    private List<TargetFilter> getAuthFilter(long userId) {
        List<TargetFilter> filters = new ArrayList<TargetFilter>();
        List<BIPackageID> authPacks;
        String sessionId = session.getSessionID();
        if (sessionId != null && SessionDealWith.hasSessionID(sessionId)) {
            authPacks = BIConfigureManagerCenter.getAuthorityManager().getAuthPackagesBySession(session.getCompanyRoles(),session.getCustomRoles());
        } else {
            authPacks = BIConfigureManagerCenter.getAuthorityManager().getAuthPackagesByUser(userId);
        }
        for (int i = 0; i < authPacks.size(); i++) {
            List<BIPackageAuthority> packAuths;
            if (sessionId != null && SessionDealWith.hasSessionID(sessionId)) {
                packAuths = BIConfigureManagerCenter.getAuthorityManager().getPackageAuthBySession(authPacks.get(i), session.getCompanyRoles(),session.getCustomRoles());
            } else {
                packAuths = BIConfigureManagerCenter.getAuthorityManager().getPackageAuthByID(authPacks.get(i), userId);
            }
            for (int j = 0; j < packAuths.size(); j++) {
                BIPackageAuthority auth = packAuths.get(j);
                if (auth.getFilter() != null) {
                    try {
                        TargetFilter filter = TargetFilterFactory.parseFilter(auth.getFilter(), userId);
                        filters.add(filter);
                    } catch (Exception e) {
                        BILoggerFactory.getLogger().error(e.getMessage(), e);
                    }
                }
            }
        }
        return filters;
    }
}