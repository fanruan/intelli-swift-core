package com.fr.bi.cal.analyze.cal.index.loader;

import com.finebi.cube.conf.table.BusinessTable;
import com.finebi.cube.relation.BITableSourceRelation;
import com.fr.bi.cal.analyze.cal.multithread.MultiThreadManagerImpl;
import com.fr.bi.cal.analyze.cal.result.*;
import com.fr.bi.cal.analyze.cal.result.operator.*;
import com.fr.bi.cal.analyze.cal.sssecret.*;
import com.fr.bi.cal.analyze.cal.store.GroupKey;
import com.fr.bi.cal.analyze.exception.NoneRegisterationException;
import com.fr.bi.cal.analyze.report.report.widget.BISummaryWidget;
import com.fr.bi.cal.analyze.report.report.widget.TableWidget;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.conf.VT4FBI;
import com.fr.bi.conf.report.widget.field.dimension.BIDimension;
import com.fr.bi.field.target.calculator.cal.CalCalculator;
import com.fr.bi.field.target.calculator.cal.FormulaCalculator;
import com.fr.bi.field.target.calculator.sum.CountCalculator;
import com.fr.bi.field.target.key.cal.BICalculatorTargetKey;
import com.fr.bi.field.target.target.BISummaryTarget;
import com.fr.bi.field.target.target.cal.BICalculateTarget;
import com.fr.bi.manager.PerformancePlugManager;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.report.key.TargetGettingKey;
import com.fr.bi.stable.report.result.BINode;
import com.fr.bi.stable.report.result.DimensionCalculator;
import com.fr.bi.stable.report.result.TargetCalculator;
import com.fr.bi.stable.utils.BITravalUtils;
import com.fr.bi.stable.utils.BIUserUtils;
import com.fr.fs.control.UserControl;
import com.fr.general.ComparatorUtils;
import com.fr.general.GeneralContext;
import com.fr.general.Inter;
import com.fr.stable.ArrayUtils;
import com.fr.stable.EnvChangedListener;
import com.fr.stable.collections.lazy.LazyCalculateContainer;
import com.fr.stable.collections.lazy.LazyValueCreator;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 管理索引
 *
 * @author Daniel
 */
public class CubeIndexLoader {

    private static Map<Long, CubeIndexLoader> userMap = new ConcurrentHashMap<Long, CubeIndexLoader>();
    private static LazyCalculateContainer<NodeAndPageInfo> lazyContainer = new LazyCalculateContainer<NodeAndPageInfo>();
    private long userId;
    /**
     * TODO 暂时先用这个，后面再优化
     */
    public CubeIndexLoader(long userId) {
        this.userId = userId;
    }

    protected CubeIndexLoader() {
    }

    /**
     * 环境改变
     */
    public static void envChanged() {
        synchronized (CubeIndexLoader.class) {
            Iterator<Entry<Long, CubeIndexLoader>> iter = userMap.entrySet().iterator();
            while (iter.hasNext()) {
                Entry<Long, CubeIndexLoader> entry = iter.next();
                CubeIndexLoader loader = entry.getValue();
                if (loader != null) {
                    loader.releaseAll();
                }
            }
            userMap.clear();
        }
    }

    public static void releaseUser(long userId) {
        synchronized (CubeIndexLoader.class) {
            Long key = userId;
            boolean useAdministrtor = BIUserUtils.isAdministrator(userId);
            if (useAdministrtor) {
                key = UserControl.getInstance().getSuperManagerID();
            }
            CubeIndexLoader manager = userMap.get(key);
            if (manager != null) {
                manager.releaseAll();
                userMap.remove(key);
            }
        }
    }

    public static CubeIndexLoader getInstance(long userId) {
        synchronized (CubeIndexLoader.class) {
            Long key = userId;
            boolean useAdministrtor = BIUserUtils.isAdministrator(userId);
            if (useAdministrtor) {
                key = UserControl.getInstance().getSuperManagerID();
            }
            CubeIndexLoader loader = userMap.get(key);
            if (loader == null) {
                loader = new CubeIndexLoader(key);
                userMap.put(key, loader);
            }
            return loader;
        }
    }

    public static void calculateTargets(List<TargetCalculator> targetCalculator, List<? extends CalCalculator> calculateTargets, BINode n) {
        int size = calculateTargets.size();
        Set<TargetGettingKey> set = new HashSet<TargetGettingKey>();
        Iterator<TargetCalculator> cit = targetCalculator.iterator();
        while (cit.hasNext()) {
            set.add(cit.next().createTargetGettingKey());
        }
        while (!calculateTargets.isEmpty() && n != null) {
            Iterator<? extends CalCalculator> iter = calculateTargets.iterator();
            while (iter.hasNext()) {
                CalCalculator calCalculator = iter.next();
                if (calCalculator.isAllFieldsReady(set)) {
                    calCalculator.calCalculateTarget(n);
                    set.add(calCalculator.createTargetGettingKey());
                    iter.remove();
                }
            }
            int newSize = calculateTargets.size();
            //没有可以计算的值了 说明有值被删除了，强制计算 将值设置为0
            if (size == newSize) {
                iter = calculateTargets.iterator();
                while (iter.hasNext()) {
                    CalCalculator calCalculator = iter.next();
                    calCalculator.calCalculateTarget(n);
                    set.add(calCalculator.createTargetGettingKey());
                    iter.remove();
                }
            } else {
                size = newSize;
            }
        }
    }

    private static boolean needCreateNewIterator(int type) {
        return type == -1 || type == 0;
    }

    private static Operator createRowOperator(int type, BISummaryWidget widget) {
        Operator operator;
        switch (type) {
            case BIReportConstant.TABLE_PAGE_OPERATOR.ALL_PAGE:
                operator = new AllPageOperator();
                break;
            case BIReportConstant.TABLE_PAGE_OPERATOR.REFRESH:
                operator = new NextPageOperator(widget.getMaxRow());
                break;
            case BIReportConstant.TABLE_PAGE_OPERATOR.ROW_NEXT:
                operator = new NextPageOperator(widget.getMaxRow());
                break;
            case BIReportConstant.TABLE_PAGE_OPERATOR.ROW_PRE:
                operator = new LastPageOperator(widget.getMaxRow());
                break;
            case BIReportConstant.TABLE_PAGE_OPERATOR.EXPAND:
                operator = new RefreshPageOperator(widget.getClickValue(), widget.getMaxRow());
                break;
            default:
                operator = new RefreshPageOperator(widget.getMaxRow());
                break;
        }
        return operator;
    }

    private static Operator createColumnOperator(int type, BISummaryWidget widget) {
        //pony 横向的改成全部展示
        Operator operator;
        switch (type) {
            case BIReportConstant.TABLE_PAGE_OPERATOR.ALL_PAGE:
                operator = new AllPageOperator();
                break;
            case BIReportConstant.TABLE_PAGE_OPERATOR.REFRESH:
                operator = new NextPageOperator(widget.getMaxCol());
                break;
            case BIReportConstant.TABLE_PAGE_OPERATOR.COLUMN_NEXT:
                operator = new NextPageOperator(widget.getMaxCol());
                break;
            case BIReportConstant.TABLE_PAGE_OPERATOR.COLUMN_PRE:
                operator = new LastPageOperator(widget.getMaxCol());
                break;
            case BIReportConstant.TABLE_PAGE_OPERATOR.EXPAND:
                operator = new RefreshPageOperator(widget.getClickValue(), widget.getMaxCol());
                break;
            default:
                operator = new RefreshPageOperator(widget.getMaxCol());
                break;
        }
        return operator;
    }

    private static BISummaryTarget[] createUsedSummaryTargets(
            BIDimension[] rowDimension,
            BISummaryTarget[] usedTarget, BISummaryTarget[] sumTarget) {
        List<BISummaryTarget> usedSummaryList = new ArrayList<BISummaryTarget>(usedTarget.length + 1);
        Set<String> targetSet = new HashSet<String>();
        for (int i = 0; i < usedTarget.length; i++) {
            if (usedTarget[i] != null && (!targetSet.contains(usedTarget[i].getValue()))) {
                targetSet.add(usedTarget[i].getValue());
                usedSummaryList.add(usedTarget[i]);
                if (usedTarget[i] instanceof BICalculateTarget) {
                    createCalculateUseTarget(sumTarget, (BICalculateTarget) usedTarget[i], targetSet, usedSummaryList);
                }
            }
        }
        for (int i = 0, ilen = rowDimension.length; i < ilen; i++) {
            List<String> targetList = rowDimension[i].getUsedTargets();
            for (int j = 0, size = targetList.size(); j < size; j++) {
                String name = targetList.get(j);
                if (!targetSet.contains(name)) {
                    BISummaryTarget target = BITravalUtils.getTargetByName(name, sumTarget);
                    if (target != null) {
                        targetSet.add(target.getValue());
                        usedSummaryList.add(target);
                        if (target instanceof BICalculateTarget) {
                            createCalculateUseTarget(sumTarget, (BICalculateTarget) target, targetSet, usedSummaryList);
                        }
                    }
                }
            }
        }
        return usedSummaryList.toArray(new BISummaryTarget[usedSummaryList.size()]);
    }

    private static void createCalculateUseTarget(BISummaryTarget[] sumTarget, BICalculateTarget target,
                                                 Set<String> targetSet, List<BISummaryTarget> usedSummaryList) {
        List<BISummaryTarget> usedList = target.createCalculateUseTarget(sumTarget);
        for (int i = 0, size = usedList.size(); i < size; i++) {
            BISummaryTarget t = usedList.get(i);
            if (t != null && (!targetSet.contains(t.getValue()))) {
                targetSet.add(t.getValue());
                usedSummaryList.add(t);
                if (t instanceof BICalculateTarget) {
                    createCalculateUseTarget(sumTarget, (BICalculateTarget) t, targetSet, usedSummaryList);
                }
            }
        }
    }

    static {
        GeneralContext.addEnvChangedListener(new EnvChangedListener() {

            @Override
            public void envChanged() {
                //FIXME 释放所有的
//                CubeReadingTableIndexLoader.envChanged();

            }
        });
    }

    /**
     * 释放
     */
    public void releaseIndexs() {
//        CubeReadingTableIndexLoader.envChanged();
    }
    private void checkRegisteration(BISummaryTarget[] allTargets, BIDimension[] allDimensions) {
        if (checkSupport(allTargets)) {
            return;
        }
        if (!VT4FBI.supportDatabaseUnion()) {
            BusinessTable modelKey = null;
            boolean isException = false;
            for (int i = 0; i < allTargets.length; i++) {
                BISummaryTarget target = allTargets[i];
                BusinessTable iteratorKey = target.createSummaryCalculator().createTableKey();
                if (modelKey == null) {
                    modelKey = iteratorKey;
                } else {
                    if (modelKey.hashCode() != iteratorKey.hashCode()) {
                        isException = true;
                        break;
                    }
                }
            }

            if (!isException) {
                for (int i = 0; i < allDimensions.length; i++) {
                    BIDimension dimension = allDimensions[i];
                    BusinessTable iteratorKey = dimension.createTableKey();
                    if (modelKey == null) {
                        modelKey = iteratorKey;
                    } else {
                        if (modelKey.hashCode() != iteratorKey.hashCode()) {
                            isException = true;
                            break;
                        }
                    }
                }
            }
            if (isException) {
                NoneRegisterationException exception = new NoneRegisterationException();
                exception.setRegisterationMsg(Inter.getLocText("BI-License_Support_Analyze_One_Table"));
                throw exception;
            }
        }
    }

    private boolean checkSupport(BISummaryTarget[] allTargets) {
        if (VT4FBI.supportCalculateTarget() && VT4FBI.supportDatabaseUnion()) {
            return true;
        }
        if (!VT4FBI.supportCalculateTarget() && allTargets != null) {
            for (int i = 0; i < allTargets.length; i++) {
                BISummaryTarget target = allTargets[i];
                if (target instanceof BICalculateTarget) {
                    NoneRegisterationException exception = new NoneRegisterationException();
                    exception.setRegisterationMsg(Inter.getLocText("BI-Not_Support_Cal_Target"));
                    throw exception;
                }
            }
        }
        return false;
    }

    /**
     * 获取node值
     *
     * @param usedTarget   用到的指标
     * @param rowDimension 当前显示的维度
     * @param allDimension 所有的维度
     * @param sumTarget    当前计算的指标
     * @param page         当前页 current -1
     * @return 计算出的node
     */

    public Node loadGroup(BISummaryWidget widget, BISummaryTarget[] usedTarget, BIDimension[] rowDimension, BIDimension[] allDimension, BISummaryTarget[] sumTarget, int page, boolean useRealData,
                          //TODO 需要传到group内部处理只计算index不计算值
                          BISession session) throws Exception {
        return loadPageGroup(false, widget, usedTarget, rowDimension, allDimension, sumTarget, page, useRealData, session, NodeExpander.ALL_EXPANDER);
    }

    /**
     * 获取交叉表node值
     *
     * @param usedTarget   用到的指标
     * @param rowDimension 横向维度
     * @param sumTarget    当前计算的指标
     * @param page         当前页 current -1
     * @param colDimension 纵向维度
     * @param expander     展开情况
     * @return 计算出的node
     */
    public NewCrossRoot loadPageCrossGroup(BISummaryTarget[] usedTarget, BIDimension[] rowDimension, BIDimension[] colDimension, BISummaryTarget[] sumTarget,
                                           int page, boolean useRealData, BISession session, CrossExpander expander, BISummaryWidget widget) throws Exception {
        BIDimension[] allDimension = createBiDimensionAdpaters(rowDimension, colDimension);
        checkRegisteration(sumTarget, allDimension);
        /**
         * 交叉表调用createUsedSummaryTargets的时候把row和cross的dimension都传入进去，而不是只传rowDimension
         * 详见BI-2304
         */
        BISummaryTarget[] usedTargets = createUsedSummaryTargets(ArrayUtils.addAll(rowDimension, colDimension), usedTarget, sumTarget);
        int summaryLength = usedTargets.length;
        List targetGettingKey = new ArrayList();
        for (int i = 0; i < summaryLength; i++) {
            addTargetGettingKey(usedTargets[i], targetGettingKey);
        }
        LinkedList calculateTargets = new LinkedList();
        LinkedList noneCalculateTargets = new LinkedList();
        NewCrossRoot n = null;
        classifyTargets(session, usedTargets, summaryLength, calculateTargets, noneCalculateTargets);
        String widgetName = widget.getWidgetName();
        PageIteratorGroup pg = null;
        if (!needCreateNewIterator(page)) {
            pg = session.getPageIteratorGroup(useRealData, widgetName);
        } else {
            pg = new PageIteratorGroup();
            session.setPageIteratorGroup(useRealData, widgetName, pg);
        }
        if (hasAllCalCalculatorTargets(usedTargets)) {
            return getAllCalCrossRoot(rowDimension, colDimension, page, session, expander, widget, usedTargets, targetGettingKey, calculateTargets, pg);
        }
        NodeAndPageInfo leftInfo = getLeftInfo(rowDimension, page, expander, widget, session, usedTargets, calculateTargets, pg);
        NodeAndPageInfo topInfo = getTopInfo(colDimension, page, expander, widget, session, usedTargets, calculateTargets, pg);
        if (usedTargets.length != 0 && isEmpty(topInfo)) {
            leftInfo.getNode().getChilds().clear();
            leftInfo.setHasNext(false);
        }
        setPageSpiner(widget, leftInfo, topInfo);
        n = new NewCrossRoot(leftInfo.getNode().createCrossHeader(), topInfo.getNode().createCrossHeader());
        new CrossCalculator(session.getLoader()).execute(n, targetGettingKey, expander);
        int size = calculateTargets.size();
        Set set = new HashSet(targetGettingKey);
        dealCalculateTarget(calculateTargets, n, size, set);
        if (n == null) {
            n = new NewCrossRoot(new CrossHeader(), new CrossHeader());
        }
        return n;
    }

    private NewCrossRoot getAllCalCrossRoot(BIDimension[] rowDimension, BIDimension[] colDimension, int page, BISession session,
                                            CrossExpander expander, BISummaryWidget widget, BISummaryTarget[] usedTargets,
                                            List targetGettingKey, LinkedList calculateTargets, PageIteratorGroup pg) throws Exception {
        NewCrossRoot n;
        NodeAndPageInfo leftInfo = getLeftInfo(rowDimension, page, expander, widget, session, usedTargets, calculateTargets, pg);
        NodeAndPageInfo leftAllInfo = getLeftInfo(rowDimension, -1, expander, widget, session, usedTargets, calculateTargets,
                new PageIteratorGroup());
        NodeAndPageInfo topInfo = getTopInfo(colDimension, page, expander, widget, session, usedTargets, calculateTargets, pg);
        if (usedTargets.length != 0 && isEmpty(topInfo)) {
            leftInfo.getNode().getChilds().clear();
            leftInfo.setHasNext(false);
        }
        setPageSpiner(widget, leftInfo, topInfo);
        n = new NewCrossRoot(leftAllInfo.getNode().createCrossHeader(), topInfo.getNode().createCrossHeader());
        new CrossCalculator(session.getLoader()).execute(n, targetGettingKey, expander);
        int size = calculateTargets.size();
        Set set = new HashSet(targetGettingKey);
        dealCalculateTarget(calculateTargets, n, size, set);
        if (n == null) {
            n = new NewCrossRoot(new CrossHeader(), new CrossHeader());
        }
        cutChilds(n.getLeft().getChilds(), leftInfo.getNode().getChilds());
        return n;
    }

    private void cutChilds(List<Node> childs, List<Node> childs1) {
        if (!childs1.isEmpty()) {
            Node start = childs1.get(0);
            Node end = childs1.get(childs1.size() - 1);
            Iterator<Node> it = childs.iterator();
            boolean shouldDelete = true;
            while (it.hasNext()) {
                Node n = it.next();
                if (ComparatorUtils.equals(n.getData(), start.getData())) {
                    shouldDelete = false;
                    cutChilds(n.getChilds(), start.getChilds());
                }
                if (shouldDelete) {
                    it.remove();
                }
                if (ComparatorUtils.equals(n.getData(), end.getData())) {
                    shouldDelete = true;
                    cutChilds(n.getChilds(), end.getChilds());
                }
            }
        }
    }

    private boolean hasAllCalCalculatorTargets(BISummaryTarget[] calculateTargets) {
        for (BISummaryTarget target : calculateTargets) {
            if (target.calculateAllPage()) {
                return true;
            }
        }
        return false;
    }

    private void setPageSpiner(BISummaryWidget widget, NodeAndPageInfo leftInfo, NodeAndPageInfo topInfo) {
        widget.setPageSpinner(BIReportConstant.TABLE_PAGE.VERTICAL_PRE, leftInfo.isHasPre());
        widget.setPageSpinner(BIReportConstant.TABLE_PAGE.VERTICAL_NEXT, leftInfo.isHasNext());
        widget.setPageSpinner(BIReportConstant.TABLE_PAGE.TOTAL_PAGE, leftInfo.getPage());
        widget.setPageSpinner(BIReportConstant.TABLE_PAGE.HORIZON_PRE, topInfo.isHasPre());
        widget.setPageSpinner(BIReportConstant.TABLE_PAGE.HORIZON_NEXT, topInfo.isHasNext());
    }

    private NodeAndPageInfo getTopInfo(BIDimension[] colDimension, int page, CrossExpander expander, BISummaryWidget widget,
                                       BISession session, BISummaryTarget[] usedTargets, LinkedList calculateTargets,
                                       PageIteratorGroup pg) throws Exception {
        BISummaryWidget topWidget = (BISummaryWidget) widget.clone();
        topWidget.getTargetFilterMap().clear();
        MultiThreadManagerImpl.getInstance().refreshExecutorService();
        NodeAndPageInfo topInfo = createPageGroupNode(topWidget, usedTargets, colDimension, page, expander.getXExpander(),
                session, calculateTargets, new ArrayList(), createColumnOperator(page, widget), pg, true, true);
        MultiThreadManagerImpl.getInstance().awaitExecutor(session);
        return topInfo;
    }

    private NodeAndPageInfo getLeftInfo(BIDimension[] rowDimension, int page, CrossExpander expander, BISummaryWidget widget,
                                        BISession session, BISummaryTarget[] usedTargets, LinkedList calculateTargets,
                                        PageIteratorGroup pg) throws Exception {
        MultiThreadManagerImpl.getInstance().refreshExecutorService();
        NodeAndPageInfo leftInfo = createPageGroupNode(widget, usedTargets, rowDimension, page, expander.getYExpander(),
                session, calculateTargets, new ArrayList(), createRowOperator(page, widget), pg, true, false);
        MultiThreadManagerImpl.getInstance().awaitExecutor(session);
        if (usedTargets.length != 0 && isEmpty(leftInfo)) {
            leftInfo.getNode().getChilds().clear();
            leftInfo.setHasNext(false);
        }
        return leftInfo;
    }

    private void dealCalculateTarget(LinkedList<CalCalculator> calculateTargets, NewCrossRoot n, int size, Set set) {
        while (!calculateTargets.isEmpty() && n != null) {
            Iterator<CalCalculator> iter = calculateTargets.iterator();
            while (iter.hasNext()) {
                CalCalculator calCalculator = iter.next();
                if (calCalculator.isAllFieldsReady(set)) {
                    calculateCrossTarget(calCalculator, n, calCalculator.createTargetGettingKey());
                    set.add(calCalculator.createTargetGettingKey());
                    iter.remove();
                }
            }
            int newSize = calculateTargets.size();
            //没有可以计算的值了 说明有值被删除了，强制计算 将值设置为0
            if (size == newSize) {
                iter = calculateTargets.iterator();
                while (iter.hasNext()) {
                    CalCalculator calCalculator = iter.next();
                    calculateCrossTarget(calCalculator, n, calCalculator.createTargetGettingKey());
                    set.add(calCalculator.createTargetGettingKey());
                    iter.remove();
                }
            } else {
                size = newSize;
            }
        }
    }

    private void classifyTargets(BISession session, BISummaryTarget[] usedTargets, int summaryLength, LinkedList calculateTargets,
                                 LinkedList noneCalculateTargets) {
        for (int i = 0; i < summaryLength; i++) {
            BISummaryTarget target = usedTargets[i];
            if (target == null) {
                continue;
            }
            TargetCalculator key = target.createSummaryCalculator();
            if (key instanceof BICalculatorTargetKey) {
                calculateTargets.add(new TargetGettingKey(key.createTargetKey(), target.getValue()));
            } else {
                noneCalculateTargets.add(new TargetGettingKey(key.createTargetKey(), target.getValue()));
            }
            BusinessTable[] summarys = getTableDefines(key);
            if (summarys == null || summarys.length == 0) {
                continue;
            }

        }
    }

    private BusinessTable[] getTableDefines(TargetCalculator key) {
        TargetCalculator[] calcualteTargets = key.createTargetCalculators();
        BusinessTable[] summarys = new BusinessTable[calcualteTargets.length];
        for (int j = 0; j < calcualteTargets.length; j++) {
            summarys[j] = calcualteTargets[j].createTableKey();
        }
        return summarys;
    }

    private void addTargetGettingKey(BISummaryTarget usedTarget, List<TargetCalculator> targetCalculators) {
        BISummaryTarget target = usedTarget;
        if (target == null) {
            return;
        }
        TargetCalculator key = target.createSummaryCalculator();
        TargetCalculator[] summarys = key.createTargetCalculators();
        if (summarys == null) {
            return;
        }
        int slen = summarys.length;
        for (int p = 0; p < slen; p++) {
            targetCalculators.add(summarys[p]);
        }
    }

    private BIDimension[] createBiDimensionAdpaters(BIDimension[] rowDimension, BIDimension[] colDimension) {
        BIDimension[] allDimension = new BIDimension[rowDimension.length + colDimension.length];
        for (int i = 0; i < rowDimension.length; i++) {
            allDimension[i] = rowDimension[i];
        }
        for (int i = 0; i < colDimension.length; i++) {
            allDimension[i + rowDimension.length] = colDimension[i];
        }
        return allDimension;
    }

    private boolean isEmpty(NodeAndPageInfo info) {
        if (info.getNode().getChilds().isEmpty()) {
            return true;
        }
        Map<TargetGettingKey, GroupValueIndex> targetIndexValueMap = info.getNode().getTargetIndexValueMap();
        return targetIndexValueMap == null || targetIndexValueMap.isEmpty();
    }

    private void calculateCrossTarget(CalCalculator calCalculator, NewCrossRoot root, TargetGettingKey key1) {
        CrossHeader top = root.getTop();
        calculateCrossTarget(calCalculator, top, key1);
    }

    private void calculateCrossTarget(CalCalculator calCalculator, CrossHeader header, TargetGettingKey key1) {
        calCalculator.calCalculateTarget(header.getValue(), key1);
        for (int i = 0; i < header.getChildLength(); i++) {
            calculateCrossTarget(calCalculator, (CrossHeader) header.getChild(i), key1);
        }
    }

    /**
     * 获取分页node值
     *
     * @param isHor        是否为纵向
     * @param usedTarget   用到的指标
     * @param rowDimension 横向维度
     * @param sumTarget    当前计算的指标
     * @param page         当前页 current -1
     * @param expander     展开情况
     * @param widget       组件名字
     * @param allDimension 全部的维度
     * @return 计算出的node
     */
    public Node loadPageGroup(boolean isHor, BISummaryWidget widget, final BISummaryTarget[] usedTarget,
                              final BIDimension[] rowDimension, BIDimension[] allDimension,
                              final BISummaryTarget[] sumTarget, int page, boolean useRealData,
                              //TODO 需要传到group内部处理只计算index不计算值
                              final BISession session, NodeExpander expander) throws Exception {
        checkRegisteration(sumTarget, allDimension);
        BISummaryTarget[] usedTargets = createUsedSummaryTargets(rowDimension, usedTarget, sumTarget);
        List<TargetCalculator> targetCalculator = new ArrayList<TargetCalculator>();
        List<FormulaCalculator> calculateTargets =  new ArrayList<FormulaCalculator>();
        PageIteratorGroup pg = null;

        String widgetName = widget.getWidgetName();
        if (needCreateNewIterator(page)) {
            pg = new PageIteratorGroup();
            session.setPageIteratorGroup(useRealData, widgetName, pg);
        } else {
            pg = session.getPageIteratorGroup(useRealData, widgetName);
        }
        MultiThreadManagerImpl.getInstance().refreshExecutorService();
        NodeAndPageInfo info = createPageGroupNode(widget, usedTargets, rowDimension, page, expander, session,
                calculateTargets, targetCalculator, isHor ? createColumnOperator(page, widget) : createRowOperator(page, widget), pg, false, isHor);
        MultiThreadManagerImpl.getInstance().awaitExecutor(session);
        Node n = info.getNode();
        widget.setPageSpinner(isHor ? BIReportConstant.TABLE_PAGE.HORIZON_PRE : BIReportConstant.TABLE_PAGE.VERTICAL_PRE, info.isHasPre());
        widget.setPageSpinner(isHor ? BIReportConstant.TABLE_PAGE.HORIZON_NEXT : BIReportConstant.TABLE_PAGE.VERTICAL_NEXT, info.isHasNext());
        widget.setPageSpinner(BIReportConstant.TABLE_PAGE.TOTAL_PAGE, info.getPage());
        calculateTargets(targetCalculator, calculateTargets, n);
        if (n == null) {
            n = new Node();
        }
        return n;
    }

    /**
     * 获取分页node值
     *
     * @param usedTarget      用到的指标
     * @param rowData         复杂表样数据
     * @param sumTarget       当前计算的指标
     * @param keys            指标
     * @param page            当前页 current -1
     * @param complexExpander 展开情况
     * @param isYExpand       情况
     * @param widget          组件
     * @param allDimension    全部的维度
     * @return 计算出的node
     */
    public Map<Integer, Node> loadComplexPageGroup(boolean isHor, TableWidget widget, final BISummaryTarget[] usedTarget,
                                                   final BIComplexExecutData rowData, BIDimension[] allDimension,
                                                   final BISummaryTarget[] sumTarget, TargetGettingKey[] keys,
                                                   int page, boolean useRealData,
                                                   //TODO 需要传到group内部处理只计算index不计算值
                                                   final BISession session, ComplexExpander complexExpander, boolean isYExpand) throws Exception {
        checkRegisteration(sumTarget, allDimension);

        Map<Integer, Node> nodeMap = new HashMap<Integer, Node>();
        for (int i = 0; i < rowData.getRegionIndex(); i++) {
            loadRegionComplexPageGroup(isHor, widget, usedTarget, rowData.getDimensionArray(i), allDimension, sumTarget,
                    keys, useRealData, (isYExpand ? complexExpander.getYExpander(i) : complexExpander.getXExpander(i)), session, nodeMap, i);
        }
        widget.setPageSpinner(isHor ? BIReportConstant.TABLE_PAGE.HORIZON_PRE : BIReportConstant.TABLE_PAGE.VERTICAL_PRE, 0);
        widget.setPageSpinner(isHor ? BIReportConstant.TABLE_PAGE.HORIZON_NEXT : BIReportConstant.TABLE_PAGE.VERTICAL_NEXT, 0);
        return nodeMap;
    }

    private void loadRegionComplexPageGroup(boolean isHor, TableWidget widget, BISummaryTarget[] usedTarget,
                                            BIDimension[] dimensionArray, BIDimension[] allDimension, BISummaryTarget[] sumTarget,
                                            TargetGettingKey[] keys, boolean useRealData, NodeExpander nodeExpanderPara,
                                            BISession session, Map<Integer, Node> nodeMap, int i) throws Exception {
        int calPage = -1;
        BIDimension[] rowDimension = dimensionArray;
        List<TargetCalculator> targetCalculators = new ArrayList<TargetCalculator>();
        LinkedList calculateTargets = new LinkedList();
        NodeExpander nodeExpander = nodeExpanderPara;
        PageIteratorGroup pg;
        if (!needCreateNewIterator(calPage) && session.getPageIteratorGroup(useRealData, widget.getWidgetName(), i) != null) {
            pg = session.getPageIteratorGroup(useRealData, widget.getWidgetName(), i);
        } else {
            pg = new PageIteratorGroup();
            session.setPageIteratorGroup(useRealData, widget.getWidgetName(), pg, i);
        }
        BISummaryTarget[] usedTargets = createUsedSummaryTargets(rowDimension, usedTarget, sumTarget);
        NodeAndPageInfo nodeInfo = createPageGroupNode(widget, usedTargets, rowDimension, calPage, nodeExpander, session,
                calculateTargets, targetCalculators, isHor ? createColumnOperator(calPage, widget) : createRowOperator(calPage, widget), pg, false, isHor);
        Node node = nodeInfo.getNode();
        if (usedTarget.length == 0) {
            node = node.createResultFilterNode(rowDimension, null);
        } else {
            Map<String, TargetGettingKey> targetsMap = new HashMap<String, TargetGettingKey>();
            Map<String, TargetCalculator> calMap = new HashMap<String, TargetCalculator>();
            for (int k = 0, len = sumTarget.length; k < len; k++) {
                BISummaryTarget target = sumTarget[k];
                TargetCalculator calculator = target.createSummaryCalculator();
                targetsMap.put(target.getValue(), calculator.createTargetGettingKey());
                calMap.put(target.getValue(), calculator);
            }
            node = node.createResultFilterNode(rowDimension, calMap);
            if (node == null) {
                node = new Node();
            }
            node = node.createResultFilterNode(widget.getTargetFilterMap(), calMap);
            if (node == null) {
                node = new Node();
            }
            if (widget.useTargetSort()) {
                node = node.createSortedNode(widget.getTargetSort(), targetsMap);
            } else {
                node = node.createSortedNode(rowDimension, targetsMap);
            }
            if (node == null) {
                node = new Node();
            }
        }
        calculateTargets(targetCalculators, calculateTargets, node);
        if (node == null) {
            node = new Node();
        }
        nodeMap.put(i, node);
    }

    /**
     * 处理计算指标
     *
     */
    private NodeAndPageInfo createPageGroupNode(BISummaryWidget widget, BISummaryTarget[] usedTargets, BIDimension[] rowDimension, int page,
                                                NodeExpander expander, BISession session, List<FormulaCalculator> formulaCalculateTargets,
                                                List<TargetCalculator> calculators, Operator op
            , PageIteratorGroup pg, boolean isCross, boolean isHor) throws Exception{
        int summaryLength = usedTargets.length;
        int rowLength = rowDimension.length;
        for (int i = 0; i < summaryLength; i++) {
            addTargetGettingKey(usedTargets[i], calculators);
        }

        if (rowLength != 0 && summaryLength == 0) {
            calculators.add(CountCalculator.NONE_TARGET_COUNT_CAL);
        } else {
            LoaderUtils.classifyTarget(usedTargets, formulaCalculateTargets);
        }
        NodeDimensionIterator iterator = isHor ? pg.getColumnIterator() : pg.getRowIterator();
        if (needCreateNewIterator(page) || iterator == null){
            iterator = new TreeIterator(rowLength);
        }
        op.moveIterator(iterator);
        NodeAndPageInfo info = PerformancePlugManager.getInstance().isExtremeConcurrency() ?
                lazyContainer.get(new WidgetKey(widget.fetchObjectCore(), isCross, isHor, expander, op, iterator.getStartIndex(), widget.getAuthFilter(userId)),
                new NodeAndPageInfoCreator(iterator, page, op, widget, usedTargets, rowDimension, isCross, isHor, session, expander))
                : new NodeAndPageInfoCreator(iterator, page, op, widget, usedTargets, rowDimension, isCross, isHor, session, expander).create();
        if (isHor) {
            pg.setColumnIterator(info.getIterator());
        } else {
            pg.setRowIterator(info.getIterator());
        }
        return info;
    }


    private class NodeAndPageInfoCreator implements LazyValueCreator<NodeAndPageInfo> {
        private NodeDimensionIterator iterator;
        private int page;
        private Operator op;
        private BISummaryWidget widget;
        private BISummaryTarget[] usedTargets;
        private BIDimension[] rowDimension;
        private boolean isCross;
        private boolean isHor;
        private BISession session;
        private NodeExpander expander;

        public NodeAndPageInfoCreator(NodeDimensionIterator iterator, int page, Operator op, BISummaryWidget widget,
                                      BISummaryTarget[] usedTargets, BIDimension[] rowDimension, boolean isCross,
                                      boolean isHor, BISession session, NodeExpander expander) {
            this.iterator = iterator;
            this.page = page;
            this.op = op;
            this.widget = widget;
            this.usedTargets = usedTargets;
            this.rowDimension = rowDimension;
            this.isCross = isCross;
            this.isHor = isHor;
            this.session = session;
            this.expander = expander;
        }

        @Override
        public NodeAndPageInfo create() {
            int summaryLength = usedTargets.length;
            int rowLength = rowDimension.length;
            if (iterator.getRoot() == null) {
                IRootDimensionGroup root;
                if (rowLength != 0 && summaryLength == 0) {
                    root = createPageGroupNodeWithNoSummary(widget, usedTargets, rowDimension, isCross, isHor, session, rowLength, page);
                } else {
                    root = createPageGroupNodeWithSummary(widget, usedTargets, rowDimension, session, isCross, isHor, summaryLength, rowLength, page);
                }
                iterator.setRoot(root);
            } else {
                iterator = iterator.createClonedIterator();
            }
            iterator.setExpander(expander);
            return GroupUtils.createNextPageMergeNode(iterator, op,  isHor ? widget.showColumnTotal() : widget.showRowToTal(), isCross);
        }
    }

    private IRootDimensionGroup createPageGroupNodeWithSummary(BISummaryWidget widget, BISummaryTarget[] usedTargets,
                                                               BIDimension[] rowDimension, BISession session, boolean isCross,
                                                               boolean isHor, int summaryLength, int rowLength, int page) {
        List<MetricGroupInfo> mergerInfoList = new ArrayList<MetricGroupInfo>();
        Map<GroupKey, MetricGroupInfo> map = new HashMap<GroupKey, MetricGroupInfo>();
        for (int i = 0; i < summaryLength; i++) {
            DimensionCalculator[] row = new DimensionCalculator[rowLength];
            BISummaryTarget target = usedTargets[i];
            if (target == null) {
                continue;
            }
            TargetCalculator key = target.createSummaryCalculator();
            TargetCalculator[] summarys = key.createTargetCalculators();
            if (summarys == null) {
                continue;
            }
            int slen = summarys.length;
            for (int p = 0; p < slen; p++) {
                TargetCalculator summary = summarys[p];
                BISummaryTarget bdt = target;
                BusinessTable targetKey = summary.createTableKey();
                LoaderUtils.fillRowDimension(widget, row, rowDimension, rowLength, bdt);
                GroupKey groupKey = new GroupKey(targetKey, row);
                MetricGroupInfo metricGroupInfo = map.get(groupKey);
                if (metricGroupInfo == null) {
                    GroupValueIndex gvi = widget.createFilterGVI(row, targetKey, session.getLoader(), session.getUserId()).AND(session.createFilterGvi(targetKey));
                    metricGroupInfo = new MetricGroupInfo(row, gvi, summary.createTableKey());
                    metricGroupInfo.addTargetAndKey(new TargetAndKey(target.getName(), summary, summary.createTargetGettingKey()));
                    map.put(groupKey, metricGroupInfo);
                    mergerInfoList.add(metricGroupInfo);
                } else {
                    metricGroupInfo.addTargetAndKey(new TargetAndKey(target.getName(), summary, summary.createTargetGettingKey()));
                }
            }
        }
        return getRootDimensionGroup(widget, usedTargets, rowDimension, session, mergerInfoList, isCross, isHor, page);
    }

    private IRootDimensionGroup createPageGroupNodeWithNoSummary(BISummaryWidget widget, BISummaryTarget[] usedTargets,
                                                                 BIDimension[] rowDimension, boolean isCross, boolean isHor,
                                                                 BISession session, int rowLength, int page) {
        DimensionCalculator[] row = new DimensionCalculator[rowLength];
        for (int i = 0; i < rowLength; i++) {
            row[i] = rowDimension[i].createCalculator(rowDimension[i].getStatisticElement(), new ArrayList<BITableSourceRelation>());
        }
        TargetCalculator summary = CountCalculator.NONE_TARGET_COUNT_CAL;
        BusinessTable tableBelongTo = row[0].getField().getTableBelongTo();
        GroupValueIndex gvi = widget.createFilterGVI(row, tableBelongTo, session.getLoader(), session.getUserId()).AND(session.createFilterGvi(tableBelongTo));
        MetricGroupInfo metricGroupInfo = new MetricGroupInfo(row, gvi, summary.createTableKey());
        metricGroupInfo.addTargetAndKey(new TargetAndKey(summary.getName(), summary, summary.createTargetGettingKey()));
        List<MetricGroupInfo> list = new ArrayList<MetricGroupInfo>();
        list.add(metricGroupInfo);
        return getRootDimensionGroup(widget, usedTargets, rowDimension, session, list, isCross, isHor, page);
    }

    private IRootDimensionGroup getRootDimensionGroup(BISummaryWidget widget, BISummaryTarget[] usedTargets, BIDimension[] rowDimension,
                                                      BISession session, List<MetricGroupInfo> metricGroupInfoList, boolean shouldSetIndex,
                                                      boolean isHor, int page) {
        boolean calAllPage = page == -1;
        boolean showSum = isHor ? widget.showColumnTotal() : widget.showRowToTal();
        NodeIteratorCreator iteratorCreator = new NodeIteratorCreator(metricGroupInfoList, rowDimension, usedTargets,
                widget.getTargetFilterMap(), widget.isRealData(), session, widget.getTargetSort(), widget.getFilter(),
                showSum, shouldSetIndex, calAllPage);
        return iteratorCreator.createRoot();
    }


    /**
     * 释放
     */
    public void releaseAll() {
        releaseIndexs();
    }
}