package com.finebi.cube.structure.table;

import com.finebi.cube.data.ICubeResourceDiscovery;
import com.finebi.cube.data.output.primitive.ICubeIntegerWriter;
import com.finebi.cube.exception.BICubeColumnAbsentException;
import com.finebi.cube.exception.BICubeRelationAbsentException;
import com.finebi.cube.exception.IllegalRelationPathException;
import com.finebi.cube.location.ICubeResourceLocation;
import com.finebi.cube.location.ICubeResourceRetrievalService;
import com.finebi.cube.relation.BITableSourceRelation;
import com.finebi.cube.structure.*;
import com.finebi.cube.structure.column.*;
import com.finebi.cube.structure.table.property.BICubeTableProperty;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.stable.data.db.BIDataValue;
import com.fr.bi.stable.data.db.ICubeFieldSource;
import com.fr.bi.stable.utils.program.BINonValueUtils;
import com.fr.bi.stable.utils.program.BIStringUtils;
import com.fr.stable.StringUtils;
import com.fr.stable.collections.array.IntArray;

import java.util.*;

/**
 * This class created on 2016/3/3.
 *
 * @author Connery
 * @since 4.0
 */
public class BICubeTableEntity implements CubeTableEntityService {

    private static final long serialVersionUID = -6166087552875875738L;
    protected ITableKey tableKey;
    protected ICubeResourceRetrievalService resourceRetrievalService;
    protected ICubeTableColumnManagerService columnManager;
    protected ICubeRelationManagerService relationManager;
    protected ICubeResourceLocation currentLocation;
    protected ICubeTablePropertyService tableProperty;
    protected ICubeResourceDiscovery discovery;
    private ICubeIntegerWriter removedLineWriter;

    public BICubeTableEntity(ITableKey tableKey, ICubeResourceRetrievalService resourceRetrievalService, ICubeResourceDiscovery discovery) {
        try {
            this.tableKey = tableKey;
            this.resourceRetrievalService = resourceRetrievalService;
            this.discovery = discovery;
            currentLocation = resourceRetrievalService.retrieveResource(tableKey);
            tableProperty = new BICubeTableProperty(currentLocation, discovery);
            if (tableProperty.isPropertyExist()) {
                columnManager = new BICubeTableColumnManager(tableKey, resourceRetrievalService, getAllFields(), discovery);
            }
            relationManager = new BICubeTableRelationEntityManager(this.resourceRetrievalService, this.tableKey, discovery);
        } catch (Exception e) {
            throw BINonValueUtils.beyondControl(e.getMessage(), e);
        }
    }

    public static BIColumnKey convert(ICubeFieldSource field) {
        return BIColumnKey.covertColumnKey(field);
    }

    public void buildStructure() {
        columnManager.buildStructure();
        tableProperty.buildStructure();
    }

    private void flushProperty() {
        if (tableProperty != null) {
            /**
             * 必须使用强制是否
             * 否则简单的clear的话只是通知不再引用句柄。
             * 实际资源没有被close掉。
             */
//            tableProperty.forceRelease();
            tableProperty.clear();
        }
        tableProperty = new BICubeTableProperty(currentLocation, discovery);

    }

    @Override
    public void recordTableStructure(List<ICubeFieldSource> fields) {
        /**
         * tableProperty缓存了上一次的map文件对象。
         * 详见BICubeTable单元测试中的testPropertyExceptionData测试用例
         */
        flushProperty();
        tableProperty.recordTableStructure(fields);
        if (columnManager != null) {
            columnManager.forceReleaseWriter();
        }
        columnManager = new BICubeTableColumnManager(tableKey, resourceRetrievalService, getAllFields(), discovery);
    }

    @Override
    public void recordRowCount(long rowCount) {
        tableProperty.recordRowCount(rowCount);
    }

    @Override
    public void recordLastExecuteTime(long time) {
        tableProperty.recordLastExecuteTime(time);
    }

    @Override
    public void recordCurrentExecuteTime() {
        tableProperty.recordCurrentExecuteTime();
    }

    @Override
    public void recordRemovedLine(TreeSet<Integer> removedLine) {
        if (null == removedLine || removedLine.isEmpty()) {
            tableProperty.recordRemovedList(0, -1);
            return;
        }
        Iterator<Integer> it = removedLine.iterator();
        int row = 0;
        while (it.hasNext()) {
            tableProperty.recordRemovedList(row++, it.next());
        }
    }

    @Override
    public void recordParentsTable(List<ITableKey> parents) {
        tableProperty.recordParentsTable(parents);
    }

    @Override
    public List<ITableKey> getParentsTable() {
        return tableProperty.getParentsTable();
    }

    @Override
    public void addDataValue(BIDataValue originalDataValue) throws BICubeColumnAbsentException {
        addValue(originalDataValue, false);
    }

    @Override
    public void increaseAddDataValue(BIDataValue originalDataValue) throws BICubeColumnAbsentException {
        addValue(originalDataValue, true);
    }

    private void addValue(BIDataValue originalDataValue, boolean isIncrease) throws BICubeColumnAbsentException {
        int columnIndex = originalDataValue.getCol();
        int rowNumber = originalDataValue.getRow();
        Object value = originalDataValue.getValue();

        ICubeFieldSource field = getAllFields().get(columnIndex);
        ICubeColumnEntityService columnService = getColumnManager().getColumn(BIColumnKey.covertColumnKey(field));
        try {
            if (!isIncrease) {
                columnService.addOriginalDataValue(rowNumber, value);
            } else {
                columnService.increaseAddOriginalDataValue(rowNumber, value);
            }
        } catch (ClassCastException e) {
            throw BINonValueUtils.beyondControl(BIStringUtils.append(e.getMessage(), "Table:" + tableKey != null ? tableKey.getSourceID() : StringUtils.EMPTY
                    , "Field column index:" + columnIndex
                    , "Field column is:" + field.getFieldName()
                    , "Field row number:" + rowNumber
                    , "the value:" + value)
                    , e);
        }
    }

    private List<ICubeFieldSource> getAllFields() {
        return tableProperty.getFieldInfo();
    }

    @Override
    public boolean checkRelationVersion(List<BITableSourceRelation> relations, int relation_version) {
        return false;
    }

    @Override
    public boolean checkRelationVersion(BIKey key, List<BITableSourceRelation> relations, int relation_version) {
        return false;
    }

    @Override
    public boolean checkCubeVersion() {
        return false;
    }

    @Override
    public void copyDetailValue(CubeTableEntityService cube, long rowCount) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<ICubeFieldSource> getFieldInfo() {
        return tableProperty.getFieldInfo();
    }

    public Set<BIColumnKey> getCubeColumnInfo() {
        return getColumnManager().getCubeColumnInfo();
    }

    @Override
    public ICubeFieldSource getSpecificColumn(String fieldName) throws BICubeColumnAbsentException {
        Iterator<ICubeFieldSource> fieldIterator = getFieldInfo().iterator();
        while (fieldIterator.hasNext()) {
            ICubeFieldSource field = fieldIterator.next();
            if (field.getFieldName().equals(fieldName)) {
                return field;
            }
        }
        throw new BICubeColumnAbsentException();
    }

    @Override
    public int getRowCount() {
        return tableProperty.getRowCount();
    }

    @Override
    public IntArray getRemovedList() {
        return tableProperty.getRemovedList();
    }

    @Override
    public Date getLastExecuteTime() {
        return tableProperty.getLastExecuteTime();
    }

    @Override
    public Date getCurrentExecuteTime() {
        return tableProperty.getCurrentExecuteTime();
    }

    @Override
    public CubeColumnReaderService getColumnDataGetter(BIColumnKey columnKey) throws BICubeColumnAbsentException {
        return getColumnManager().getColumn(columnKey);
    }

    private ICubeTableColumnManagerService getColumnManager() {
        if (columnManager == null || columnManager.getCubeColumnInfo().size() == 0) {
            columnManager = new BICubeTableColumnManager(tableKey, resourceRetrievalService, getAllFields(), discovery);
        }
        return columnManager;
    }

    @Override
    public CubeColumnReaderService getColumnDataGetter(String columnName) throws BICubeColumnAbsentException {
        ICubeFieldSource field = getSpecificColumn(columnName);
        return getColumnDataGetter(convert(field));
    }

    @Override
    public void clear() {
        if (columnManager != null) {
            columnManager.clear();
        }

        relationManager.clear();
        tableProperty.clear();
//        tableProperty.forceRelease();
    }

    @Override
    public void forceReleaseWriter() {
        if (columnManager != null) {
            columnManager.forceReleaseWriter();
        }
        relationManager.forceReleaseWriter();
        tableProperty.forceReleaseWriter();
    }

    @Override
    public void forceReleaseReader() {
        if (columnManager != null) {
            columnManager.forceReleaseReader();
        }
        relationManager.forceReleaseReader();
        tableProperty.forceReleaseReader();
    }

    @Override
    public CubeRelationEntityGetterService getRelationIndexGetter(BICubeTablePath path) throws BICubeRelationAbsentException, BICubeColumnAbsentException, IllegalRelationPathException {
        return relationManager.getRelationService(path);
    }

    @Override
    public void recordFieldNamesFromParent(Set<String> fieldNames) {
        tableProperty.recordFieldNamesFromParent(fieldNames);
    }

    @Override
    public Set<String> getFieldNamesFromParent() {
        return tableProperty.getFieldNamesFromParent();
    }

    @Override
    public boolean tableDataAvailable() {
        return tableProperty.isRowCountAvailable() || tableProperty.isVersionAvailable();
    }

    @Override
    public boolean relationExists(BICubeTablePath path) {
        try {
            return relationManager.relationExists(path);
        } catch (IllegalRelationPathException e) {
            throw BINonValueUtils.illegalArgument(path.toString() + " the path is so terrible");
        }
    }

    @Override
    public boolean isRowCountAvailable() {
        return tableProperty.isRowCountAvailable();
    }

    public boolean isLastExecuteTimeAvailable() {
        return tableProperty.isLastExecuteTimeAvailable();
    }

    @Override
    public boolean isCurrentExecuteTimeAvailable() {
        return tableProperty.isCurrentExecuteTimeAvailable();
    }

    public long getCubeVersion() {
        return tableProperty.getCubeVersion();
    }

    @Override
    public void addVersion(long version) {
        tableProperty.addVersion(version);

    }

    @Override
    public void setTableOwner(ITableKey owner) {
        relationManager.setOwner(owner);
        columnManager.setOwner(owner);
    }

    @Override
    public boolean isRemovedListAvailable() {
        return tableProperty.isRemovedListAvailable();
    }

    @Override
    public Boolean isVersionAvailable() {
        return tableProperty.isVersionAvailable();
    }
}
