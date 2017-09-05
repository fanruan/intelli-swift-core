package com.fr.bi.etl.analysis.manager;

import com.finebi.cube.common.log.BILogger;
import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.conf.BISystemDataManager;
import com.finebi.cube.conf.field.BusinessField;
import com.finebi.cube.conf.pack.data.BIBusinessPackage;
import com.finebi.cube.conf.pack.data.BIGroupTagName;
import com.finebi.cube.conf.pack.data.BIPackageID;
import com.finebi.cube.conf.pack.data.BIPackageName;
import com.finebi.cube.conf.pack.data.IBusinessPackageGetterService;
import com.finebi.cube.conf.pack.group.BIBusinessGroup;
import com.finebi.cube.conf.singletable.SingleTableUpdateManager;
import com.finebi.cube.conf.table.BIBusinessTable;
import com.finebi.cube.conf.table.BusinessTable;
import com.finebi.cube.conf.utils.BILogHelper;
import com.fr.bi.base.BIBusinessPackagePersistThread;
import com.fr.bi.base.BIUser;
import com.fr.bi.common.factory.BIFactoryHelper;
import com.fr.bi.conf.data.pack.exception.BIGroupAbsentException;
import com.fr.bi.conf.data.pack.exception.BIGroupDuplicateException;
import com.fr.bi.conf.data.pack.exception.BIPackageAbsentException;
import com.fr.bi.conf.data.pack.exception.BIPackageDuplicateException;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.etl.analysis.Constants;
import com.fr.bi.etl.analysis.conf.AnalysisBusiTable;
import com.fr.bi.etl.analysis.data.AnalysisCubeTableSource;
import com.fr.bi.etl.analysis.data.AnalysisETLSourceFactory;
import com.fr.bi.exception.BIFieldAbsentException;
import com.fr.bi.exception.BIKeyAbsentException;
import com.fr.bi.exception.BIKeyDuplicateException;
import com.fr.bi.stable.data.BITableID;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.exception.BITableAbsentException;
import com.fr.bi.stable.utils.program.BIStringUtils;
import com.fr.bi.web.service.action.BISaveAnalysisETLTableAction;
import com.fr.fs.base.entity.User;
import com.fr.fs.control.UserControl;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;
import com.fr.stable.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * 先注释掉普通用户看管理员的部分
 * Created by 小灰灰 on 2015/12/11.
 */
public class AnalysisBusiPackManager extends BISystemDataManager<SingleUserAnalysisBusiPackManager> implements BIAnalysisBusiPackManagerProvider {

    private static final String TAG = "AnalysisBusiPackManager";
    private static final long serialVersionUID = -5566625380376195712L;
    private static final BILogger LOGGER = BILoggerFactory.getLogger(AnalysisBusiPackManager.class);

    public SingleUserAnalysisBusiPackManager getUserAnalysisBusiPackManager(long userId) {
        try {
            SingleUserAnalysisBusiPackManager manager = getValue(userId);
            if (!manager.checkVersion()) {
                persistData(userId);
            }
            return manager;
        } catch (BIKeyAbsentException e) {

            throw new NullPointerException("Please check the userID:" + userId + ",which getIndex a empty manager");
        }
    }

    @Override
    public SingleUserAnalysisBusiPackManager constructUserManagerValue(Long userId) {
        return BIFactoryHelper.getObject(SingleUserAnalysisBusiPackManager.class, userId);
    }

    @Override
    public String managerTag() {
        return TAG;
    }

    @Override
    public String persistUserDataName(long key) {
        return "sue" + File.separator + "pack" + key;
    }

    @Override
    public Set<IBusinessPackageGetterService> getAllPackages(long userId) {
        Set<IBusinessPackageGetterService> result = new HashSet<IBusinessPackageGetterService>();
        List<User> allUserList = new ArrayList<User>();
        try {
            allUserList = UserControl.getInstance().findAllUser();
        } catch (Exception e) {
            LOGGER.error("Get all users failure " + e.getMessage(), e);
        }
        for (User user : allUserList) {
            for (BIBusinessPackage biBusinessPackage : getUserAnalysisBusiPackManager(user.getId()).getAllPacks()) {
                result.add(biBusinessPackage);
            }
        }
        for (BIBusinessPackage biBusinessPackage : getUserAnalysisBusiPackManager(UserControl.getInstance().getSuperManagerID()).getAllPacks()) {
            result.add(biBusinessPackage);
        }
//        if (userId != UserControl.getInstance().getSuperManagerID()){
//            for (BIBusinessPackage biBusinessPackage : getUserAnalysisBusiPackManager(UserControl.getInstance().getSuperManagerID()).getAllPacks()) {
//                result.add(biBusinessPackage);
//            }
//        }
        return result;
    }

    @Override
    public Set<IBusinessPackageGetterService> getAllAnalysisPackages(long userId) {
        return getAllPackages(userId);
    }


    @Override
    public boolean isPackageDataChanged(long userId) {
        return false;
    }

    @Override
    public IBusinessPackageGetterService getPackage(long userId, BIPackageID packageID) throws BIPackageAbsentException {
        return null;
    }

    @Override
    public void renamePackage(long userId, BIPackageID packageID, BIPackageName packageName) throws BIPackageAbsentException, BIPackageDuplicateException {

    }

    @Override
    public void renameGroup(long userId, BIGroupTagName oldName, BIGroupTagName newName) throws BIGroupAbsentException, BIGroupDuplicateException {

    }

    @Override
    public void addPackage(long userId, BIBusinessPackage biBusinessPackage) throws BIPackageDuplicateException {

    }

    @Override
    public void finishGenerateCubes(long userId, Set<CubeTableSource> absentTables) {

    }

    @Override
    public void endBuildingCube(long userId, Set<CubeTableSource> absentTable) {

    }

    @Override
    public void updatePackage(long userId, BIBusinessPackage newBiBusinessPackage) throws BIPackageDuplicateException, BIPackageAbsentException {

    }

    @Override
    public Boolean containPackage(long userId, BIBusinessPackage biPackage) {
        return null;
    }

    @Override
    public Boolean containPackageID(long userId, BIPackageID packageID) {
        return null;
    }

    @Override
    public Boolean containGroup(long userId, BIGroupTagName groupTagName) {
        return null;
    }

    @Override
    public void removePackage(long userId, BIPackageID packageID) throws BIPackageAbsentException {

    }

    @Override
    public void removeGroup(long userId, BIGroupTagName groupTagName) throws BIGroupAbsentException {

    }

    @Override
    public boolean isTableIncreased(long userId) {
        return false;
    }

    @Override
    public boolean isTableNoChange(long userId) {
        return false;
    }

    @Override
    public void persistData(long userId) {
        persistUserData(userId);
    }

    @Override
    public void createEmptyGroup(long userId, BIGroupTagName groupTagName, long position) throws BIGroupDuplicateException {

    }

    @Override
    public Boolean isPackageTaggedSomeGroup(long userId, BIPackageID packageID) {
        return null;
    }

    @Override
    public Boolean isPackageTaggedSpecificGroup(long userId, BIPackageID packageID, BIGroupTagName groupTagName) throws BIGroupAbsentException {
        return null;
    }

    @Override
    public void stickGroupTagOnPackage(long userId, BIPackageID packageID, BIGroupTagName groupTagName) throws BIPackageAbsentException, BIGroupAbsentException, BIPackageDuplicateException {

    }

    @Override
    public void removeGroupTagFromPackage(long userId, BIPackageID packageID, BIGroupTagName groupTagName) throws BIPackageAbsentException, BIGroupAbsentException, BIPackageDuplicateException {

    }

    @Override
    public BIBusinessGroup getGroup(long userId, BIGroupTagName groupTagName) throws BIGroupAbsentException {
        return null;
    }

    @Override
    public Set<BIBusinessPackage> getPackageByName(long userId, BIPackageName packName) {
        return null;
    }

    @Override
    public void startBuildingCube(long userId) {

    }

    @Override
    public void endBuildingCube(long userId) {

    }

    @Override
    public Boolean isPackagesEmpty(long userId) {
        return null;
    }

    @Override
    public JSONObject createGroupJSON(long userId) throws JSONException {
        return new JSONObject();
    }

    @Override
    public void parseGroupJSON(long userId, JSONObject jo) throws JSONException {

    }

    @Override
    public JSONObject createPackageJSON(long userId) throws Exception {
        return getUserAnalysisBusiPackManager(userId).createJSON(Locale.CHINA);
    }

    @Override
    public JSONObject createPackageJSON(long userId, Locale locale) throws Exception {
//        getUserAnalysisBusiPackManager(UserControl.getInstance().getSuperManagerID()).createJSON(locale);
//        JSONObject jo = getUserAnalysisBusiPackManager(userId).createJSON(locale);
//        if (userId != UserControl.getInstance().getSuperManagerID()){
//            JSONObject adminJO = getUserAnalysisBusiPackManager(UserControl.getInstance().getSuperManagerID()).createJSON(locale);
//            setEdit(adminJO);
//            jo.join(adminJO);
//        }
//        return jo;
        return getUserAnalysisBusiPackManager(userId).createJSON(locale);

    }

    @Override
    public JSONObject createAnalysisPackageJSON(long userId, Locale locale) throws Exception {
        return getUserAnalysisBusiPackManager(userId).createJSON(locale);
    }

    private void setEdit(JSONObject jo) throws Exception {
        if (jo.has(Constants.PACK_ID)) {
            JSONObject pack = jo.getJSONObject(Constants.PACK_ID);
            JSONArray tables = pack.getJSONArray("tables");
            for (int i = 0; i < tables.length(); i++) {
                tables.getJSONObject(i).put("inedible", true);
            }
        }
    }

    @Override
    public void removeTable(long userId, BIPackageID packageID, BITableID biTableID) throws BIPackageAbsentException, BITableAbsentException {

    }

    @Override
    public void addTable(long userId, BIPackageID packageID, BIBusinessTable biBusinessTable) throws BIPackageAbsentException {

    }

    /**
     * 完成生成cube
     *
     * @param userId 用户id
     */
    @Override
    public void finishGenerateCubes(long userId) {

    }

    @Override
    public boolean isTableReduced(long userId) {
        return false;
    }

    public SingleTableUpdateManager getSingleTableUpdateManager(long userId) {
        return null;
    }

    /**
     * 更新
     */
    @Override
    public void envChanged() {

    }

    @Override
    public void addTable(AnalysisBusiTable table) {
        getUserAnalysisBusiPackManager(table.getUserId()).addTable(table);
    }

    @Override
    public void removeTable(String tableId, long userId) {
        getUserAnalysisBusiPackManager(userId).removeTable(tableId);
    }

    /**
     * 先到管理员那找下，再找自己的吧
     *
     * @param tableId
     * @param userId
     * @return
     * @throws BITableAbsentException
     */
    @Override
    public AnalysisBusiTable getTable(String tableId, long userId) throws BITableAbsentException {
//        try{
//            return getUserAnalysisBusiPackManager(UserControl.getInstance().getSuperManagerID()).getTable(tableId);
//        } catch (BITableAbsentException e){
        return getUserAnalysisBusiPackManager(userId).getTable(tableId);
//        }
    }

    @Override
    public Set<BusinessTable> getAllTables(long userId) {
        return getUserAnalysisBusiPackManager(userId).getAllTables();
    }

    @Override
    public boolean isCurrentMetaChanged(long userId) {
        return false;
    }

    @Override
    public boolean isCurrentMetaChangedAfterBuilding(long userId) {
        return false;
    }

    @Override
    public Set<BusinessTable> getAnalysisAllTables(long userId) {
        return null;
    }

    public BusinessTable getAnalysisTableById(long userId, String tableId) throws BITableAbsentException {
        return null;
    }

    public BusinessTable getConfigTableById(long userId, String tableId) throws BITableAbsentException {
        return null;
    }

    @Override
    public BusinessField getAnalysisFieldByFieldId(long userId, String fieldId) throws BIFieldAbsentException {
        return null;
    }


    @Override
    public void parseSinglePackageJSON(long userId, BIPackageID packageId, JSONArray tableIdsJA, JSONObject usedFieldsJO, JSONObject tableDataJO) throws Exception {

    }

    @Override
    public void packageAddTableSource(long userId, BIPackageID packageId, String tableId, CubeTableSource source, boolean enSureFields) throws BIKeyDuplicateException, BIPackageAbsentException, BITableAbsentException {

    }

    @Override
    public void initialAnalysisResourceMap(long userId) {

    }

    //    @Override
    public String getPackageNameByTableId(String tableId) {
        return null;
    }

    @Override
    public JSONObject saveAnalysisETLTable(final long userId, String tableId, String newId, String tableName, String describe, String tableJSON) throws Exception {
        AnalysisBusiTable table = null;
        CubeTableSource source = null;
        CubeTableSource oriSource = null;
        if (StringUtils.isEmpty(newId)) {
            //编辑||新建
            table = new AnalysisBusiTable(tableId, userId);
            table.setDescribe(describe);
            JSONObject jo = new JSONObject(tableJSON);
            JSONArray items = jo.getJSONArray(Constants.ITEMS);
            BIAnalysisETLManagerCenter.getAliasManagerProvider().setAliasName(tableId, tableName, userId);
            source = AnalysisETLSourceFactory.createTableSource(items, userId);
            table.setSource(source);
            if (BIAnalysisETLManagerCenter.getDataSourceManager().containBusinessTable(table.getID())) {
                oriSource = BIAnalysisETLManagerCenter.getDataSourceManager().getTableSource(table);
            }
        } else {
            //复制
            table = new AnalysisBusiTable(newId, userId);
            BIAnalysisETLManagerCenter.getAliasManagerProvider().setAliasName(newId, tableName, userId);
            AnalysisBusiTable oldTable = BIAnalysisETLManagerCenter.getBusiPackManager().getTable(tableId, userId);
            source = oldTable.getTableSource();
            oriSource = oldTable.getTableSource();
            table.setSource(source);
            table.setDescribe(oldTable.getDescribe());
        }
        solveTable(table, source, oriSource, userId);
        JSONObject result = generateResultJson(userId, table, tableName);
        return result;
    }

    private void solveTable(AnalysisBusiTable table, CubeTableSource source, CubeTableSource oriSource, final long userId) {
        try {
            BIAnalysisETLManagerCenter.getDataSourceManager().addTableSource(table, table.getTableSource());
            BILoggerFactory.getLogger(BISaveAnalysisETLTableAction.class).info("*********Add AnalysisETL table*******");
            BIAnalysisETLManagerCenter.getBusiPackManager().addTable(table);
            BILoggerFactory.getLogger(BISaveAnalysisETLTableAction.class).info("The added table is: " + logTable(table));
            BILoggerFactory.getLogger(BISaveAnalysisETLTableAction.class).info("*********Add AnalysisETL table*******");
        } catch (BIKeyDuplicateException e) {
            LOGGER.error(e.getMessage(), e);
        }
        try {
            refreshTables(getUsedTables(table, oriSource));
            BIAnalysisETLManagerCenter.getUserETLCubeManagerProvider().refresh();
        } catch (Throwable exception) {
            LOGGER.error(BIStringUtils.append("analysisSource update failed cause refresh table failed", source.getSourceID(), exception.getMessage()), exception);
        }

        LOGGER.info("*********update packageLastModify*******");
        BIConfigureManagerCenter.getCubeConfManager().updatePackageLastModify();
        try {
            BIAnalysisETLManagerCenter.getUserETLCubeManagerProvider().checkTableIndex((AnalysisCubeTableSource) source, new BIUser(userId));
        } catch (Exception e) {
            LOGGER.error(BIStringUtils.append("analysisSource update failed", source.getSourceID(), e.getMessage()), e);
        }
        BIUserETLBusinessPackagePersistThreadHolder.getInstance().getBiBusinessPackagePersistThread().triggerWork(new BIBusinessPackagePersistThread.Action() {
            @Override
            public void work() {
                BIAnalysisETLManagerCenter.getDataSourceManager().persistData(userId);
                BIAnalysisETLManagerCenter.getAliasManagerProvider().persistData(userId);
                BIAnalysisETLManagerCenter.getBusiPackManager().persistData(userId);
            }
        });
    }

    private JSONObject generateResultJson(long userId, AnalysisBusiTable table, String tableName) throws Exception {
        JSONObject result = new JSONObject();
        JSONObject packages = BIAnalysisETLManagerCenter.getBusiPackManager().createPackageJSON(userId);
        JSONObject translations = new JSONObject();
        translations.put(table.getID().getIdentity(), tableName);
        JSONObject tableJSONWithFieldsInfo = table.createJSONWithFieldsInfo(userId);
        JSONObject tableFields = tableJSONWithFieldsInfo.getJSONObject("tableFields");
        JSONObject tables = new JSONObject();
        tables.put(table.getID().getIdentity(), tableFields);
        JSONObject fields = tableJSONWithFieldsInfo.getJSONObject("fieldsInfo");
        result.put("packages", packages);
        result.put("translations", translations);
        result.put("tables", tables);
        result.put("fields", fields);
        return result;
    }

    private String logTable(BusinessTable table) {
        try {
            return BILogHelper.logAnalysisETLTable(table) +
                    "\n" + "*********Fields of AnalysisETL table*******" +
                    BILogHelper.logAnalysisETLTableField(table, "") +
                    "\n" + "*********Fields of AnalysisETL table*******";
        } catch (Exception e) {
            BILoggerFactory.getLogger(BIBusinessPackage.class).error(e.getMessage(), e);
            return "";
        }
    }

    private Set<AnalysisBusiTable> getUsedTables(AnalysisBusiTable table, CubeTableSource oriSource) {
        Set<AnalysisBusiTable> usedTables = new HashSet<AnalysisBusiTable>();
        if (null != oriSource) {
            for (BusinessTable businessTable : BIAnalysisETLManagerCenter.getDataSourceManager().getAllBusinessTable()) {
                if (businessTable.getTableSource() != null && ComparatorUtils.equals(businessTable.getTableSource().getSourceID(), oriSource.getSourceID())) {
                    usedTables.add((AnalysisBusiTable) businessTable);
                }
            }
        }
        usedTables.add(table);
        return usedTables;
    }

    private void refreshTables(Set<AnalysisBusiTable> usedTables) {
        Map<BusinessTable, CubeTableSource> refreshTables = new HashMap<BusinessTable, CubeTableSource>();
        try {
            for (BusinessTable table : usedTables) {
                AnalysisCubeTableSource s = null;
                if (null != table) {
                    s = (AnalysisCubeTableSource) BIAnalysisETLManagerCenter.getDataSourceManager().getTableSource(table);
                    s.refreshWidget();
                }
                refreshTables.put(table, s);
            }
        } catch (BIKeyAbsentException e) {
            BILoggerFactory.getLogger(this.getClass()).error(e.getMessage(), e);
        }
        for (BusinessTable table : refreshTables.keySet()) {
            try {
                BIAnalysisETLManagerCenter.getDataSourceManager().addTableSource(table, refreshTables.get(table));
            } catch (BIKeyDuplicateException e) {
                BILoggerFactory.getLogger(this.getClass()).error(e.getMessage(), e);
            }
        }
    }
}