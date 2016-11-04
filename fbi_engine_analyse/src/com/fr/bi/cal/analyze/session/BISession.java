package com.fr.bi.cal.analyze.session;

import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.conf.BICubeConfigureCenter;
import com.finebi.cube.conf.pack.data.BIPackageID;
import com.finebi.cube.conf.pack.data.IBusinessPackageGetterService;
import com.finebi.cube.conf.table.BIBusinessTable;
import com.finebi.cube.conf.table.BusinessTable;
import com.finebi.cube.relation.BITableRelation;
import com.fr.bi.cal.analyze.cal.result.ComplexAllExpalder;
import com.fr.bi.cal.analyze.cal.sssecret.PageIteratorGroup;
import com.fr.bi.cal.analyze.executor.detail.key.DetailSortKey;
import com.fr.bi.cal.analyze.report.report.widget.BIDetailWidget;
import com.fr.bi.cal.analyze.report.report.widget.TableWidget;
import com.fr.bi.cal.report.main.impl.BIWorkBook;
import com.fr.bi.cal.stable.engine.TempCubeTask;
import com.fr.bi.cal.stable.loader.CubeReadingTableIndexLoader;
import com.fr.bi.cal.stable.loader.CubeTempModelReadingTableIndexLoader;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.conf.report.BIReport;
import com.fr.bi.conf.report.BIWidget;
import com.fr.bi.conf.utils.BIModuleUtils;
import com.fr.bi.fs.BIReportNode;
import com.fr.bi.fs.BIReportNodeLock;
import com.fr.bi.fs.BIReportNodeLockDAO;
import com.fr.bi.stable.constant.BIExcutorConstant;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.data.key.date.BIDay;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.log.CubeGenerateStatusProvider;
import com.fr.data.TableDataSource;
import com.fr.fs.base.entity.CompanyRole;
import com.fr.fs.base.entity.CustomRole;
import com.fr.fs.control.CompanyRoleControl;
import com.fr.fs.control.CustomRoleControl;
import com.fr.fs.control.UserControl;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.general.GeneralContext;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.main.FineBook;
import com.fr.main.TemplateWorkBook;
import com.fr.main.workbook.ResultWorkBook;
import com.fr.report.report.ResultReport;
import com.fr.report.stable.fun.Actor;
import com.fr.stable.bridge.StableFactory;
import com.fr.stable.script.CalculatorProvider;
import com.fr.web.core.SessionDealWith;
import com.fr.web.core.SessionIDInfor;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by GUY on 2015/4/8.
 */
public class BISession extends BIAbstractSession {

    private boolean isEdit;
    private boolean isRealTime;
    private BIReportNode node;
    //pony 缓存loader
    private ICubeDataLoader loader;
    private CubeGenerateStatusProvider provider;
    private String tempTableMd5;
    private String tempTableId;
    //pony 明细表缓存的分页信息
    private Map<DetailSortKey, ConcurrentHashMap<Integer, Integer>> detailIndexMap = new ConcurrentHashMap<DetailSortKey, ConcurrentHashMap<Integer, Integer>>();
    private Map<DetailSortKey, ConcurrentHashMap<Integer, Object[]>> detailValueMap = new ConcurrentHashMap<DetailSortKey, ConcurrentHashMap<Integer, Object[]>>();

    //统计组建的分页信息
    private Map<String, ConcurrentHashMap<Object, PageIteratorGroup>> pageGroup = new ConcurrentHashMap<String, ConcurrentHashMap<Object, PageIteratorGroup>>();
    private Map<String, ConcurrentHashMap<Object, PageIteratorGroup>> partpageGroup = new ConcurrentHashMap<String, ConcurrentHashMap<Object, PageIteratorGroup>>();

    //young 当前用户（普通）的角色信息
    private List<CustomRole> customRoles = new ArrayList<CustomRole>();
    private List<CompanyRole> companyRoles = new ArrayList<CompanyRole>();

    public BISession(String remoteAddress, BIWeblet let, long userId) {
        super(remoteAddress, let, userId);
    }

    private BISession(String remoteAddress, BIWeblet let, long userId, BIReportNode node) {
        super(remoteAddress, let, userId);
        this.node = node;
        updateTime();
    }

    public BISession(String remoteAddress, BIWeblet let, long userId, Locale locale, BIReportNode node) {
        this(remoteAddress, let, userId, node);
        // TODO:richie在这里改变国际化的环境
        GeneralContext.setLanguage(1);
        updateTime();
        initRoles();
    }

    //TODO : 这边userid 也要把loader什么的换下，在这边实现起来不好
    public long getUserIdFromSession(HttpServletRequest req) {
        if (isShareReq) {
            return accessUserId;
        } else {
            return ServiceUtils.getCurrentUserID(req);
        }
    }

    public void removeBookByName(String widgetName) {

    }

    /**
     * 强奸
     */
    public void forceEdit() {
        BIReportNodeLockDAO lockDAO = StableFactory.getMarkedObject(BIReportNodeLockDAO.class.getName(), BIReportNodeLockDAO.class);
        if(lockDAO == null){
            this.isEdit = true;
            return;
        }
        lockDAO.forceLock(sessionID, node.getUserId(), node.getId());
        this.isEdit = true;
    }

    /**
     * 半推半就
     *
     * @param isEdit
     * @return
     */
    public boolean setEdit(boolean isEdit) {
        BIReportNodeLockDAO lockDAO = StableFactory.getMarkedObject(BIReportNodeLockDAO.class.getName(), BIReportNodeLockDAO.class);
        if(lockDAO == null){
            this.isEdit = isEdit;
            return isEdit;
        }
        if (isEdit) {
            isEdit = lockDAO.lock(sessionID, node.getUserId(), node.getId());
            if (!isEdit) {
                List<BIReportNodeLock> locks = lockDAO.getLock(node.getUserId(), node.getId());
                boolean doForce = true;
                for (BIReportNodeLock l : locks) {
                    SessionIDInfor ss = SessionDealWith.getSessionIDInfor(l.getSessionId());
                    if (ss instanceof BISession) {
                        long t = ((BISession) ss).lastTime;
                        //45- 30 超过15-45秒还没反應可能是没有心跳
                        if (System.currentTimeMillis() - t < 45000) {
                            doForce = false;
                            break;
                        }
                    }

                }
                if (doForce) {
                    forceEdit();
                    return this.isEdit;
                }
            }
        } else {
            releaseLock();
        }
        this.isEdit = isEdit;
        return isEdit;
    }

    private void releaseLock() {
        BIReportNodeLockDAO lockDAO = StableFactory.getMarkedObject(BIReportNodeLockDAO.class.getName(), BIReportNodeLockDAO.class);
        if(lockDAO == null){
            return;
        }
        BIReportNodeLock lock = lockDAO.getLock(this.sessionID, node.getUserId(), node.getId());
        if (lock != null) {
            lockDAO.release(lock);
        }
    }

    private void initRoles() {
        try {
            if (this.getUserId() != UserControl.getInstance().getSuperManagerID()) {
                Set<CustomRole> cusRoles = CustomRoleControl.getInstance().getCustomRoleSet(this.getUserId());
                for (CustomRole role : cusRoles) {
                    customRoles.add(role);
                }
                Set<CompanyRole> comRoles = CompanyRoleControl.getInstance().getCompanyRoleSet(this.getUserId());
                for (CompanyRole role : comRoles) {
                    companyRoles.add(role);
                }
            }
        } catch (Exception e) {
            BILoggerFactory.getLogger().error(e.getMessage());
        }
    }

    public JSONObject getAllAvailableCubeData(HttpServletRequest req) throws Exception {
        long userId = this.getUserId();
        long manageId = UserControl.getInstance().getSuperManagerID();
        JSONObject groups = new JSONObject();
        JSONObject packages = new JSONObject();
        JSONObject relations = new JSONObject();
        JSONObject connections = new JSONObject();
        JSONObject tables = new JSONObject();
        JSONObject source = new JSONObject();
        JSONObject fields = new JSONObject();
        JSONObject translations = new JSONObject();
        JSONObject excelViews = new JSONObject();
        try {
            JSONObject allGroups = BICubeConfigureCenter.getPackageManager().createGroupJSON(userId);
            JSONObject allPacks = BIModuleUtils.createAnalysisPackJSON(userId, req.getLocale());
            List<BIPackageID> authPacks = new ArrayList<BIPackageID>();
            //从分组中去掉allPacks没有的业务包
            Iterator<String> gIds = allGroups.keys();
            while (gIds.hasNext()) {
                String gId = gIds.next();
                JSONObject oneGroup = allGroups.getJSONObject(gId);
                JSONArray nChildren = new JSONArray();
                if(oneGroup.has("children")) {
                    JSONArray children = oneGroup.getJSONArray("children");
                    for(int i = 0; i < children.length(); i++) {
                        JSONObject child = children.getJSONObject(i);
                        if(allPacks.has(child.getString("id"))) {
                            nChildren.put(child);
                        }
                    }
                    oneGroup.put("children", nChildren);
                }
                allGroups.put(gId, oneGroup);
            }
            //管理员
            if (manageId == userId) {
                packages = allPacks;
                groups = allGroups;
            } else {
                //前台能看到的业务包
                authPacks = BIModuleUtils.getAvailablePackID(userId);
                for (BIPackageID pId : authPacks) {
                    if (allPacks.has(pId.getIdentityValue())) {
                        packages.put(pId.getIdentityValue(), allPacks.getJSONObject(pId.getIdentityValue()));
                    }
                }

                //分组
                Iterator<String> groupIds = allGroups.keys();
                while (groupIds.hasNext()) {
                    String groupId = groupIds.next();
                    JSONObject group = allGroups.getJSONObject(groupId);
                    JSONArray nChildren = new JSONArray();
                    if(group.has("children")) {
                        JSONArray children = group.getJSONArray("children");
                        for(int i = 0; i < children.length(); i++) {
                            JSONObject child = children.getJSONObject(i);
                            String childId = child.getString("id");
                            if(packages.has(childId)) {
                                nChildren.put(child);
                            }
                        }
                        group.put("children", nChildren);
                    }
                    if(nChildren.length() > 0) {
                        groups.put(groupId, group);
                    }
                }
            }

            translations = BIModuleUtils.createAliasJSON(userId);
            relations = BICubeConfigureCenter.getTableRelationManager().createRelationsPathJSON(manageId);
            excelViews = BIConfigureManagerCenter.getExcelViewManager().createJSON(manageId);
            Set<IBusinessPackageGetterService> packs = BIModuleUtils.getAllPacks(userId);
            for (IBusinessPackageGetterService p : packs) {
                if (manageId != userId && !authPacks.contains(p.getID())) {
                    continue;
                }
                for (BIBusinessTable t : (Set<BIBusinessTable>) p.getBusinessTables()) {
                    JSONObject jo = t.createJSONWithFieldsInfo(userId);
                    JSONObject tableFields = jo.getJSONObject("tableFields");
                    tables.put(t.getID().getIdentityValue(), tableFields);
                    JSONObject fieldsInfo = jo.getJSONObject("fieldsInfo");
                    fields.join(fieldsInfo);
                }
            }
            Set<BITableRelation> connectionSet = BICubeConfigureCenter.getTableRelationManager().getAllTableRelation(userId);
            JSONArray connectionJA = new JSONArray();
            for (BITableRelation connection : connectionSet) {
                connectionJA.put(connection.createJSON());
            }
            connections.put("connectionSet", connectionJA);

        } catch (Exception e) {
            BILoggerFactory.getLogger().error(e.getMessage(), e);
        }

        JSONObject jo = new JSONObject();
        jo.put("source", source);
        jo.put("groups", groups);
        jo.put("packages", packages);
        jo.put("relations", relations == null ? new JSONObject() : relations);
        jo.put("connections", connections);
        jo.put("translations", translations);
        jo.put("tables", tables);
        jo.put("fields", fields);
        jo.put("excel_views", excelViews);
        return jo;
    }

    public CubeGenerateStatusProvider getProvider() {
        return provider;
    }

    public void setProvider(CubeGenerateStatusProvider provider) {
        this.provider = provider;
    }

    public String getTempTableMd5() {
        return tempTableMd5;
    }

    public String getTempTableId() {
        return tempTableId;
    }

    public void setTempTableMd5(String tempTableMd5) {
        this.tempTableMd5 = tempTableMd5;
    }

    public void setTempTableId(String tempTableId) {
        this.tempTableId = tempTableId;
    }

    public boolean isRealTime() {
        return isRealTime;
    }

    public void setIsRealTime(boolean isRealTime) {
        this.isRealTime = isRealTime;
    }

    public ResultWorkBook getExportBookByName(String name) throws CloneNotSupportedException {
        BIWidget widget = report.getWidgetByName(name);
        if (widget != null) {
            widget = (BIWidget) widget.clone();
            switch (widget.getType()) {
                case BIReportConstant.WIDGET.TABLE:
                case BIReportConstant.WIDGET.CROSS_TABLE:
                case BIReportConstant.WIDGET.COMPLEX_TABLE:
                    ((TableWidget) widget).setComplexExpander(new ComplexAllExpalder());
                    ((TableWidget) widget).setOperator(BIReportConstant.TABLE_PAGE_OPERATOR.ALL_PAGE);
                    break;
                case BIReportConstant.WIDGET.DETAIL:
                    ((BIDetailWidget) widget).setPage(BIExcutorConstant.PAGINGTYPE.NONE);
                    break;
            }

            widget.setWidgetName(widget.getWidgetName() + Math.random());
            TemplateWorkBook workBook = widget.createWorkBook(this);
            return ((BIWorkBook) workBook).execute4BI(getParameterMap4Execute());
        }
        return null;
    }


    @Override
    public String getDurationPrefix() {
        return null;
    }

    @Override
    public boolean isPackAvailableForDesign(String packageName) {
        return false;
    }

    @Override
    public FineBook getContextBook() {
        return null;
    }

    @Override
    public Actor getActor() {
        return null;
    }

    @Override
    public ResultReport getReport2Show(int reportIndex) {
        return null;
    }

    @Override
    public int getReportCount() {
        return 0;
    }

    @Override
    public String getReportName(int index) {
        return null;
    }

    @Override
    public TemplateWorkBook getWorkBookDefine() {
        return null;
    }

    @Override
    public TableDataSource getTableDataSource() {
        return null;
    }

    @Override
    protected Object resolveVariable(Object var, CalculatorProvider ca) {
        return null;
    }

    @Override
    public String getWebTitle() {
        return null;
    }

    @Override
    public List<CustomRole> getCustomRoles() {
        return customRoles;
    }

    @Override
    public List<CompanyRole> getCompanyRoles() {
        return companyRoles;
    }

    @Override
    public void release() {
        detailIndexMap.clear();
        detailValueMap.clear();
        partpageGroup.clear();
        pageGroup.clear();
        releaseLock();
    }

    @Override
    public ICubeDataLoader getLoader() {
        synchronized (this) {
            if (!isRealTime()) {
                return CubeReadingTableIndexLoader.getInstance(accessUserId);
            } else {
                if (loader == null) {
                    loader = CubeTempModelReadingTableIndexLoader.getInstance(new TempCubeTask(getTempTableMd5(), getTempTableId(), getUserId()));
                }
                return loader;
            }
        }
    }

    @Override
    public void updateTime() {
        this.lastTime = System.currentTimeMillis();
        if (isRealTime()) {
            CubeTempModelReadingTableIndexLoader loader = (CubeTempModelReadingTableIndexLoader) CubeTempModelReadingTableIndexLoader.getInstance(new TempCubeTask(getTempTableMd5(), getTempTableId(), getUserId()));
            loader.updateTime();
        }
    }

    public void setWidgetDateMap(String widgetName, String name, String s, Object data) {

    }

    public boolean hasPackageAccessiblePrivilege(BusinessTable key) {
        return true;
    }

    public PageIteratorGroup getPageIteratorGroup(boolean useRealData, String widgetName) {
        return getPageIteratorGroup(useRealData, widgetName, 0);
    }

    @Override
    public BIReport getBIReport() {
        return report;
    }


    public PageIteratorGroup getPageIteratorGroup(boolean useRealData, String widgetName, int i) {
        Map<String, ConcurrentHashMap<Object, PageIteratorGroup>> pmap = useRealData ? pageGroup : partpageGroup;
        if (!pmap.containsKey(widgetName)) {
            throw new RuntimeException("error! page not found");
        }
        ConcurrentHashMap<Object, PageIteratorGroup> map = pmap.get(widgetName);
        return map.get(i);
    }

    public void setPageIteratorGroup(boolean useRealData, String widgetName, PageIteratorGroup pg) {
        setPageIteratorGroup(useRealData, widgetName, pg, 0);
    }

    public void setPageIteratorGroup(boolean useRealData, String widgetName, PageIteratorGroup pg, int i) {
        Map<String, ConcurrentHashMap<Object, PageIteratorGroup>> pmap = useRealData ? pageGroup : partpageGroup;
        ConcurrentHashMap<Object, PageIteratorGroup> map = pmap.get(widgetName);
        if (map == null) {
            pmap.put(widgetName, new ConcurrentHashMap<Object, PageIteratorGroup>());
            map = pmap.get(widgetName);
        }
        map.put(i, pg);
    }

    public BIDay getWidgetDatekey(String widgetName, String dimName, String v) {
        return null;
    }

    public GroupValueIndex createFilterGvi(BusinessTable key) {
        return getLoader().getTableIndex(key.getTableSource()).getAllShowIndex();
    }

    public Long getReportId() {
        return node.getId();
    }

    public int getDetailLastIndex(DetailSortKey key, int page) {
        int index = 0;
        ConcurrentHashMap<Integer, Integer> imap = detailIndexMap.get(key);
        if (imap != null && imap.containsKey(page)) {
            index = imap.get(page);
        }
        return index;
    }

    public Object[] getDetailLastValue(DetailSortKey key, int page) {
        ConcurrentHashMap<Integer, Object[]> vmap = detailValueMap.get(key);
        if (vmap != null && vmap.containsKey(page)) {
            return vmap.get(page);
        }
        return new Object[0];
    }

    public void setDetailIndexMap(DetailSortKey key, int page, int lastIndex) {
        ConcurrentHashMap<Integer, Integer> imap = detailIndexMap.get(key);
        if (imap != null) {
            imap.put(page, lastIndex);
        } else {
            imap = new ConcurrentHashMap<Integer, Integer>();
            detailIndexMap.put(key, imap);
            imap.put(page, lastIndex);
        }
    }

    public void setDetailValueMap(DetailSortKey key, int page, Object[] value) {
        ConcurrentHashMap<Integer, Object[]> vmap = detailValueMap.get(key);
        if (vmap != null) {
            vmap.put(page, value);
        } else {
            vmap = new ConcurrentHashMap<Integer, Object[]>();
            detailValueMap.put(key, vmap);
            vmap.put(page, value);
        }
    }

    /**
     * @return
     */
    public BIReportNode getReportNode() {
        return node;
    }
}