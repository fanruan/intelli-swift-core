package com.fr.bi.conf.base.datasource;

import com.fr.base.FRContext;
import com.fr.base.ModifiedTable;
import com.fr.base.TableData;
import com.fr.data.impl.Connection;
import com.fr.data.impl.storeproc.StoreProcedure;
import com.fr.file.DatasourceManager;
import com.fr.file.DatasourceManagerProvider;
import com.fr.stable.bridge.StableFactory;
import com.fr.stable.xml.XMLPrintWriter;
import com.fr.stable.xml.XMLableReader;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by wang on 2017/2/14.
 */
public class DatasourceManagerProxy implements DatasourceManagerProxyProvider {
    private static final long serialVersionUID = 6041015609398873366L;

    private DatasourceManagerProvider realDatasourceManager;
    public DatasourceManagerProxy(){

    }
    public DatasourceManagerProxy(DatasourceManagerProvider realSource){
        this.realDatasourceManager = realSource;
    }

    public static DatasourceManagerProxyProvider getDatasourceManager(){
        return StableFactory.getMarkedObject(DatasourceManagerProxyProvider.XML_TAG, DatasourceManagerProxyProvider.class);
    }
    @Override
    public void updateUserRelatedOperationMap(String s, String s1) {
        realDatasourceManager.updateUserRelatedOperationMap(s,s1);
    }

    @Override
    public ModifiedTable getConnectionTotalModifiedtables() {
        return realDatasourceManager.getConnectionTotalModifiedtables();
    }

    @Override
    public ModifiedTable getTableDataTotalModifiedTable() {
        return realDatasourceManager.getTableDataTotalModifiedTable();
    }

    @Override
    public String fileName() {
        return realDatasourceManager.fileName();
    }

    @Override
    public boolean readXMLFile() {
        return realDatasourceManager.readXMLFile();
    }

    @Override
    public void readFromInputStream(InputStream inputStream) throws Exception {
        realDatasourceManager.readFromInputStream(inputStream);
    }

    @Override
    public Iterator getConnectionNameIterator() {
        return realDatasourceManager.getConnectionNameIterator();
    }

    @Override
    public Connection getConnection(String s) {
        return realDatasourceManager.getConnection(s);
    }

    @Override
    public <T extends Connection> T getConnection(String s, Class<? extends Connection> aClass) {
        return realDatasourceManager.getConnection(s, aClass);
    }

    @Override
    public boolean renameConnection(String s, String s1) {
        return realDatasourceManager.renameConnection(s, s1);
    }

    @Override
    public void putConnection(String s, Connection connection) {
        realDatasourceManager.putConnection(s, connection);
    }

    @Override
    public void removeConnection(String s) {
        realDatasourceManager.removeConnection(s);
    }

    @Override
    public void clearAllConnection() {
        realDatasourceManager.clearAllConnection();
    }

    @Override
    public void updateSelfConnectionTotalModifiedTable(ModifiedTable modifiedTable, String s) {
        realDatasourceManager.updateSelfConnectionTotalModifiedTable(modifiedTable,s);
    }

    @Override
    public void updateSelfTableDataTotalModifiedTable(ModifiedTable modifiedTable, String s) {
        realDatasourceManager.updateSelfTableDataTotalModifiedTable(modifiedTable, s);
    }

    @Override
    public void acceptConnectionOnceModifyTable(ModifiedTable modifiedTable, String s) {
        realDatasourceManager.acceptConnectionOnceModifyTable(modifiedTable, s);
    }

    @Override
    public void acceptTableDataOnceModifyTable(ModifiedTable modifiedTable, String s) {
        realDatasourceManager.acceptTableDataOnceModifyTable(modifiedTable, s);
    }

    @Override
    public void clearModifiedTable() {
        realDatasourceManager.clearModifiedTable();
    }

    @Override
    public HashMap<String, Connection> getConnectionModifyDetails() {
        return realDatasourceManager.getConnectionModifyDetails();
    }

    @Override
    public HashMap<String, TableData> getTableDataModifyDetails() {
        return realDatasourceManager.getTableDataModifyDetails();
    }

    @Override
    public ModifiedTable checkTableDataModifyTable(DatasourceManager datasourceManager, String s) {
        return realDatasourceManager.checkConnectionModifyTable(datasourceManager, s);
    }

    @Override
    public ModifiedTable getTableDataLocalModifyTable() {
        return realDatasourceManager.getTableDataLocalModifyTable();
    }

    @Override
    public ModifiedTable checkConnectionModifyTable(DatasourceManager datasourceManager, String s) {
        return realDatasourceManager.checkTableDataModifyTable(datasourceManager,s);
    }

    @Override
    public ModifiedTable getConnectionLocalModifyTable() {
        return realDatasourceManager.getConnectionLocalModifyTable();
    }

    @Override
    public DatasourceManager getBackUpManager() {
        return realDatasourceManager.getBackUpManager();
    }

    @Override
    public void synchronizedWithServer(DatasourceManager datasourceManager, String s) {
        realDatasourceManager.synchronizedWithServer(datasourceManager,s);
    }

    @Override
    public void synchronizedWithServer() {
        realDatasourceManager.synchronizedWithServer();
    }

    @Override
    public void doWithTableDataConfilct(ModifiedTable modifiedTable) {
        realDatasourceManager.doWithTableDataConfilct(modifiedTable);
    }

    @Override
    public void doWithConnectionConflict(ModifiedTable modifiedTable) {
        realDatasourceManager.doWithConnectionConflict(modifiedTable);
    }

    @Override
    public Iterator getTableDataNameIterator() {
        return realDatasourceManager.getTableDataNameIterator();
    }

    @Override
    public TableData getTableData(String s) {
        return realDatasourceManager.getTableData(s);
    }

    @Override
    public TableData isEmptyTableData(String s) {
        return realDatasourceManager.isEmptyTableData(s);
    }

    @Override
    public void putTableData(String s, TableData tableData) {
        realDatasourceManager.putTableData(s,tableData);
    }

    @Override
    public boolean renameTableData(String s, String s1) {
        return realDatasourceManager.renameTableData(s,s1);
    }

    @Override
    public int isConnectionMapContainsRename() {
        return realDatasourceManager.isConnectionMapContainsRename();
    }

    @Override
    public int isTableDataMapContainsRename() {
        return realDatasourceManager.isTableDataMapContainsRename();
    }

    @Override
    public void removeTableData(String s) {
        realDatasourceManager.removeTableData(s);
    }

    @Override
    public void clearAllTableData() {
        realDatasourceManager.clearAllTableData();
    }

    @Override
    public Iterator getADHOCNameIterator() {
        return realDatasourceManager.getADHOCNameIterator();
    }

    @Override
    public TableData getADHOC(String s) {
        return realDatasourceManager.getADHOC(s);
    }

    @Override
    public void putADHOC(String s, TableData tableData) {
        realDatasourceManager.putADHOC(s,tableData);
    }

    @Override
    public void removeADHOC(String s) {
        realDatasourceManager.removeADHOC(s);
    }

    @Override
    public void clearAllADHOC() {
        realDatasourceManager.clearAllADHOC();
    }

    @Override
    public Iterator getProcedureNameIterator() {
        return realDatasourceManager.getProcedureNameIterator();
    }

    @Override
    public List<String> getProcedureNames() {
        return realDatasourceManager.getProcedureNames();
    }

    @Override
    public StoreProcedure getProcedure(String s) {
        return realDatasourceManager.getProcedure(s);
    }

    @Override
    public StoreProcedure getProcedureBy_dsName(String s) {
        return realDatasourceManager.getProcedureBy_dsName(s);
    }

    @Override
    public boolean isProcedureName(String s) {
        return realDatasourceManager.isProcedureName(s);
    }

    @Override
    public StoreProcedure getProcedureByName(String s) {
        return realDatasourceManager.getProcedureByName(s);
    }

    @Override
    public void putProcedure(String s, StoreProcedure storeProcedure) {
        realDatasourceManager.putProcedure(s,storeProcedure);
    }

    @Override
    public void clearAllProcedure() {
        realDatasourceManager.clearAllProcedure();
    }

    @Override
    public void readXML(XMLableReader xmLableReader) {
        realDatasourceManager.readXML(xmLableReader);
    }

    @Override
    public void writeXML(XMLPrintWriter xmlPrintWriter) {
        realDatasourceManager.writeXML(xmlPrintWriter);
    }

    @Override
    public boolean writeResource() throws Exception {
        return realDatasourceManager.writeResource();
    }

    @Override
    public void writeContextResource() throws Exception {
        FRContext.getCurrentEnv().writeResource(getDatasourceManager());
    }
}
