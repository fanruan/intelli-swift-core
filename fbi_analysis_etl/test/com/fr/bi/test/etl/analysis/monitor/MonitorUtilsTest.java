package com.fr.bi.test.etl.analysis.monitor;

import com.finebi.cube.api.ICubeDataLoader;
import com.fr.base.TableData;
import com.fr.bi.base.BICore;
import com.fr.bi.common.inter.Traversal;
import com.fr.bi.conf.report.BIWidget;
import com.fr.bi.etl.analysis.conf.AnalysisBusiTable;
import com.fr.bi.etl.analysis.data.AnalysisCubeTableSource;
import com.fr.bi.etl.analysis.data.AnalysisETLSourceField;
import com.fr.bi.etl.analysis.data.UserCubeTableSource;
import com.fr.bi.etl.analysis.manager.BIAnalysisBusiPackManagerProvider;
import com.fr.bi.etl.analysis.monitor.MonitorUtils;
import com.fr.bi.etl.analysis.monitor.SimpleTable;
import com.fr.bi.etl.analysis.monitor.TableRelation;
import com.fr.bi.etl.analysis.monitor.TableRelationTree;
import com.fr.bi.stable.constant.BIBaseConstant;
import com.fr.bi.stable.data.db.BIDataValue;
import com.fr.bi.stable.data.db.ICubeFieldSource;
import com.fr.bi.stable.data.db.IPersistentTable;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.data.source.SourceFile;
import com.fr.bi.stable.exception.BITableAbsentException;
import com.fr.fs.control.UserControl;
import com.fr.json.JSONObject;
import com.fr.stable.bridge.StableFactory;
import com.fr.stable.xml.XMLPrintWriter;
import com.fr.stable.xml.XMLableReader;
import junit.framework.TestCase;
import org.easymock.EasyMock;
import org.easymock.IMocksControl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by daniel on 2017/1/22.
 */
public class MonitorUtilsTest extends TestCase {


    public void testGetTableRelations() throws BITableAbsentException {
        long userId = UserControl.getInstance().getSuperManagerID();
        int travelCount = new Integer(10).intValue();
        IMocksControl control = EasyMock.createControl();
        BIAnalysisBusiPackManagerProvider provider = control.createMock(BIAnalysisBusiPackManagerProvider.class);
        StableFactory.registerMarkedObject(BIAnalysisBusiPackManagerProvider.XML_TAG, provider);
        for (int i = 0; i < travelCount; i++) {
            String id = String.valueOf(i + 1);
            provider.getTable(id, userId);
            EasyMock.expectLastCall().andReturn(createEasyMockTable(id)).anyTimes();
        }
        control.replay();
        for (int i = 0; i < travelCount; i++) {
            String id = String.valueOf(i + 1);
            List<TableRelation> list = MonitorUtils.getTableRelations(new SimpleTable(id), userId);
            for (TableRelation r : list) {
                assertTableRelation(r);
            }
        }
    }

    private void assertTableRelation(TableRelation r) {
        TableRelation tr = r.getNext();
        if (tr != null) {
            assertTrue(relationMap.get(r.getTable().getId()).contains(tr.getTable()));
            assertTableRelation(tr);
        }
    }

    private AnalysisBusiTable createEasyMockTable(String id) {
        IMocksControl control = EasyMock.createControl();
        AnalysisBusiTable table = control.createMock(AnalysisBusiTable.class);
        table.getTableSource();
        EasyMock.expectLastCall().andReturn(createEasyMockSource(id)).anyTimes();
        control.replay();
        return table;
    }

    private static Map<String, Set> relationMap = new HashMap<String, Set>();

    static {
        Set set = new HashSet();
        set.add(new SimpleTable("2"));
        set.add(new SimpleTable("3"));
        relationMap.put("1", set);
        set = new HashSet();
        set.add(new SimpleTable("3"));
        relationMap.put("2", set);
        set = new HashSet();
        set.add(new SimpleTable("4"));
        set.add(new SimpleTable("5"));
        set.add(new SimpleTable("6"));
        relationMap.put("3", set);
        set = new HashSet();
        set.add(new SimpleTable("4"));
        set.add(new SimpleTable("6"));
        relationMap.put("5", set);
        set = new HashSet();
        set.add(new SimpleTable("7"));
        set.add(new SimpleTable("8"));
        set.add(new SimpleTable("9"));
        relationMap.put("4", set);
        set = new HashSet();
        set.add(new SimpleTable("7"));
        set.add(new SimpleTable("8"));
        set.add(new SimpleTable("9"));
        relationMap.put("10", set);
    }

    private AnalysisCubeTableSource createEasyMockSource(final String id) {
        return new AnalysisCubeTableSource() {

            @Override
            public void getParentAnalysisBaseTableIds(Set<SimpleTable> set) {
                Set rs = relationMap.get(id);
                if (rs != null) {
                    set.addAll(rs);
                }
            }

            public TableRelationTree getAllProcessAnalysisTablesWithRelation() {
                return null;
            }

            @Override
            public void resetTargetsMap() {

            }

            @Override
            public UserCubeTableSource createUserTableSource(long userId) {
                return null;
            }

            @Override
            public List<AnalysisETLSourceField> getFieldsList() {
                return null;
            }

            @Override
            public void getSourceUsedAnalysisETLSource(Set<AnalysisCubeTableSource> set) {

            }

            @Override
            public void getSourceNeedCheckSource(Set<AnalysisCubeTableSource> set) {

            }

            @Override
            public void refreshWidget() {

            }

            @Override
            public Set<BIWidget> getWidgets() {
                return null;
            }

            @Override
            public void reSetWidgetDetailGetter() {

            }

            @Override
            public BICore fetchObjectCore() {
                return null;
            }

            @Override
            public IPersistentTable getPersistentTable() {
                return null;
            }

            @Override
            public String getSourceID() {
                return null;
            }

            @Override
            public String getTableName() {
                return null;
            }

            @Override
            public ICubeFieldSource[] getFieldsArray(Set<CubeTableSource> sources) {
                return new ICubeFieldSource[0];
            }

            @Override
            public Set<ICubeFieldSource> getParentFields(Set<CubeTableSource> sources) {
                return null;
            }

            @Override
            public Set<ICubeFieldSource> getFacetFields(Set<CubeTableSource> sources) {
                return null;
            }

            @Override
            public Set<ICubeFieldSource> getSelfFields(Set<CubeTableSource> sources) {
                return null;
            }

            @Override
            public Map<Integer, Set<CubeTableSource>> createGenerateTablesMap() {
                return null;
            }

            @Override
            public List<Set<CubeTableSource>> createGenerateTablesList() {
                return null;
            }

            @Override
            public int getLevel() {
                return 0;
            }

            @Override
            public int getType() {
                return 0;
            }

            @Override
            public long read(Traversal<BIDataValue> travel, ICubeFieldSource[] field, ICubeDataLoader loader) {
                return new Long(0).longValue();
            }

            @Override
            public long read4Part(Traversal<BIDataValue> travel, ICubeFieldSource[] field, ICubeDataLoader loader, int start, int end) {
                return new Long(0).longValue();
            }

            @Override
            public long read4Part(Traversal<BIDataValue> traversal, ICubeFieldSource[] cubeFieldSources, String sql, long rowCount) {
                return new Long(0).longValue();
            }

            @Override
            public Set getFieldDistinctNewestValues(String fieldName, ICubeDataLoader loader, long userId) {
                return null;
            }

            @Override
            public Set getFieldDistinctValuesFromCube(String fieldName, ICubeDataLoader loader, long userId) {
                return null;
            }

            @Override
            public JSONObject createPreviewJSON(ArrayList<String> fields, ICubeDataLoader loader, long userId) throws Exception {
                return null;
            }

            @Override
            public TableData createTableData(List<String> fields, ICubeDataLoader loader, long userId) throws Exception {
                return null;
            }

            @Override
            public JSONObject createPreviewJSONFromCube(ArrayList<String> fields, ICubeDataLoader loader) throws Exception {
                return null;
            }

            @Override
            public boolean needGenerateIndex() {
                return false;
            }

            @Override
            public Map<BICore, CubeTableSource> createSourceMap() {
                return null;
            }

            @Override
            public SourceFile getSourceFile() {
                return null;
            }

            @Override
            public Set<String> getUsedFields(CubeTableSource source) {
                return null;
            }

            @Override
            public void refresh() {

            }

            @Override
            public boolean isIndependent() {
                return false;
            }

            @Override
            public Set<CubeTableSource> getSourceUsedBaseSource(Set<CubeTableSource> set, Set<CubeTableSource> helper) {
                return null;
            }

            @Override
            public boolean canExecute() throws Exception {
                return false;
            }

            @Override
            public boolean hasAbsentFields() {
                return false;
            }

            @Override
            public String getModuleName() {
                return BIBaseConstant.MODULE_NAME.CORE_MODULE;
            }

            @Override
            public JSONObject createJSON() throws Exception {
                return null;
            }

            @Override
            public void readXML(XMLableReader reader) {

            }

            @Override
            public void writeXML(XMLPrintWriter writer) {

            }

            @Override
            public Object clone() throws CloneNotSupportedException {
                return null;
            }
        };
    }
}
