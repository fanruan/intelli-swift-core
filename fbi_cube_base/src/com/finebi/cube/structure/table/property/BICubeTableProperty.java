package com.finebi.cube.structure.table.property;

import com.finebi.cube.data.ICubeResourceDiscovery;
import com.finebi.cube.data.input.ICubeIntegerReaderWrapper;
import com.finebi.cube.data.input.ICubeLongReaderWrapper;
import com.finebi.cube.data.input.ICubeStringReader;
import com.finebi.cube.data.output.ICubeIntegerWriterWrapper;
import com.finebi.cube.data.output.ICubeLongWriterWrapper;
import com.finebi.cube.data.output.ICubeStringWriter;
import com.finebi.cube.exception.BIResourceInvalidException;
import com.finebi.cube.location.ICubeResourceLocation;
import com.finebi.cube.structure.BITableKey;
import com.finebi.cube.structure.ICubeTablePropertyService;
import com.finebi.cube.structure.ITableKey;
import com.fr.bi.stable.data.db.BICubeFieldSource;
import com.fr.bi.stable.data.db.CubeFieldSource;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.bi.stable.utils.program.BINonValueUtils;
import com.fr.json.JSONObject;

import java.util.*;

/**
 * This class created on 2016/3/28.
 *
 * @author Connery
 * @since 4.0
 */
public class BICubeTableProperty implements ICubeTablePropertyService {
    private ICubeResourceLocation currentLocation;
    private static String MAIN_DATA = "field";
    private static String ROW_COUNT_DATA = "count";
    private static String VERSION_DATA = "version";
    private static String TIMESTAMP_DATA = "timestamp";
    private static String SUPER_TABLES = "st";

    private List<CubeFieldSource> tableFields = null;
    private List<ITableKey> parentTable = null;

    private ICubeResourceDiscovery discovery;

    private ICubeStringWriter fieldInfoWriter;
    private ICubeStringReader fieldInfoReader;
    private ICubeStringWriter parentsWriter;
    private ICubeStringReader parentsReader;
    private ICubeIntegerWriterWrapper versionWriter;
    private ICubeIntegerReaderWrapper versionReader;

    private ICubeLongWriterWrapper rowCountWriter;
    private ICubeLongReaderWrapper rowCountReader;

    private ICubeLongWriterWrapper timeStampWriter;
    private ICubeLongReaderWrapper timeStampReader;

    private ParentFieldProperty parentFieldProperty;

    public BICubeTableProperty(ICubeResourceLocation currentLocation, ICubeResourceDiscovery discovery) {
        this.currentLocation = currentLocation.copy();
        this.discovery = discovery;
        parentFieldProperty = new ParentFieldProperty(currentLocation, discovery);
    }

    protected boolean isFieldWriterAvailable() {
        return fieldInfoWriter != null;
    }

    protected void resetFiledWriter() {
        if (isFieldWriterAvailable()) {
            fieldInfoWriter.clear();
            fieldInfoWriter = null;
        }
    }


    protected boolean isFieldReaderAvailable() {
        return fieldInfoReader != null;
    }

    protected boolean isVersionWriterAvailable() {
        return versionWriter != null;
    }

    protected boolean isVersionReaderAvailable() {
        return versionReader != null;
    }

    protected boolean isRowCountWriterAvailable() {
        return rowCountWriter != null;
    }

    protected boolean isRowCountReaderAvailable() {
        return rowCountReader != null;
    }

    protected boolean isTimeStampWriterAvailable() {
        return timeStampWriter != null;
    }

    protected boolean isTimeStampReaderAvailable() {
        return timeStampReader != null;
    }

    protected boolean isParentWriterAvailable() {
        return parentsWriter != null;
    }

    protected boolean isParentReaderAvailable() {
        return parentsReader != null;
    }

    protected boolean isFieldInit() {
        return tableFields != null;
    }

    protected boolean isParentTableInit() {
        return parentTable != null;
    }

    private void initialFieldInfoReader() throws Exception {
        ICubeResourceLocation mainLocation = this.currentLocation.buildChildLocation(MAIN_DATA);
        mainLocation.setStringType();
        mainLocation.setReaderSourceLocation();
        fieldInfoReader = (ICubeStringReader) discovery.getCubeReader(mainLocation);

    }

    private void initialFieldInfoWriter() throws Exception {
        ICubeResourceLocation mainLocation = this.currentLocation.buildChildLocation(MAIN_DATA);
        mainLocation.setStringType();
        mainLocation.setWriterSourceLocation();
        fieldInfoWriter = (ICubeStringWriter) discovery.getCubeWriter(mainLocation);
    }

    private void initialRowCountWriter() throws Exception {
        ICubeResourceLocation rowCountLocation = this.currentLocation.buildChildLocation(ROW_COUNT_DATA);
        rowCountLocation.setLongTypeWrapper();
        rowCountLocation.setWriterSourceLocation();
        rowCountWriter = (ICubeLongWriterWrapper) discovery.getCubeWriter(rowCountLocation);

    }

    private void initialRowCountReader() throws Exception {
        ICubeResourceLocation rowCountLocation = this.currentLocation.buildChildLocation(ROW_COUNT_DATA);
        rowCountLocation.setLongTypeWrapper();
        rowCountLocation.setReaderSourceLocation();
        rowCountReader = (ICubeLongReaderWrapper) discovery.getCubeReader(rowCountLocation);

    }

    private void initialTimeStampReader() throws Exception {
        ICubeResourceLocation rowCountLocation = this.currentLocation.buildChildLocation(TIMESTAMP_DATA);
        rowCountLocation.setLongTypeWrapper();
        rowCountLocation.setReaderSourceLocation();
        timeStampReader = (ICubeLongReaderWrapper) discovery.getCubeReader(rowCountLocation);

    }

    private void initialTimeStampWriter() throws Exception {
        ICubeResourceLocation rowCountLocation = this.currentLocation.buildChildLocation(TIMESTAMP_DATA);
        rowCountLocation.setLongTypeWrapper();
        rowCountLocation.setWriterSourceLocation();
        timeStampWriter = (ICubeLongWriterWrapper) discovery.getCubeWriter(rowCountLocation);

    }

    private void initialVersionReader() throws Exception {
        ICubeResourceLocation rowCountLocation = this.currentLocation.buildChildLocation(VERSION_DATA);
        rowCountLocation.setIntegerTypeWrapper();
        rowCountLocation.setReaderSourceLocation();
        versionReader = (ICubeIntegerReaderWrapper) discovery.getCubeReader(rowCountLocation);

    }

    private void initialVersionWriter() throws Exception {
        ICubeResourceLocation rowCountLocation = this.currentLocation.buildChildLocation(VERSION_DATA);
        rowCountLocation.setIntegerTypeWrapper();
        rowCountLocation.setWriterSourceLocation();
        versionWriter = (ICubeIntegerWriterWrapper) discovery.getCubeWriter(rowCountLocation);
    }

    private void initialParentTableReader() throws Exception {
        ICubeResourceLocation rowCountLocation = this.currentLocation.buildChildLocation(SUPER_TABLES);
        rowCountLocation.setStringType();
        rowCountLocation.setReaderSourceLocation();
        parentsReader = (ICubeStringReader) discovery.getCubeReader(rowCountLocation);

    }

    private void initialParentTableWriter() throws Exception {
        ICubeResourceLocation rowCountLocation = this.currentLocation.buildChildLocation(SUPER_TABLES);
        rowCountLocation.setStringType();
        rowCountLocation.setWriterSourceLocation();
        parentsWriter = (ICubeStringWriter) discovery.getCubeWriter(rowCountLocation);
    }

    public ICubeStringWriter getFieldInfoWriter() {
        try {
            if (!isFieldWriterAvailable()) {
                initialFieldInfoWriter();
            }
            return fieldInfoWriter;
        } catch (Exception e) {
            throw BINonValueUtils.beyondControl(e);
        }
    }

    public ICubeStringReader getFieldInfoReader() {
        try {
            if (!isFieldReaderAvailable()) {
                initialFieldInfoReader();
            }
            return fieldInfoReader;
        } catch (Exception e) {
            throw BINonValueUtils.beyondControl(e);
        }
    }

    public ICubeIntegerWriterWrapper getVersionWriter() {
        try {
            if (!isVersionWriterAvailable()) {
                initialVersionWriter();
            }
            return versionWriter;
        } catch (Exception e) {
            throw BINonValueUtils.beyondControl(e);
        }
    }

    public ICubeIntegerReaderWrapper getVersionReader() {
        try {
            if (!isVersionReaderAvailable()) {
                initialVersionReader();
            }
            return versionReader;
        } catch (Exception e) {
            throw BINonValueUtils.beyondControl(e);
        }
    }

    public ICubeLongWriterWrapper getRowCountWriter() {
        try {
            if (!isRowCountWriterAvailable()) {
                initialRowCountWriter();
            }
            return rowCountWriter;
        } catch (Exception e) {
            throw BINonValueUtils.beyondControl(e);
        }
    }

    public ICubeLongReaderWrapper getRowCountReader() {
        try {
            if (!isRowCountReaderAvailable()) {
                initialRowCountReader();
            }
            return rowCountReader;
        } catch (Exception e) {
            throw BINonValueUtils.beyondControl(e);
        }
    }

    public ICubeLongWriterWrapper getTimeStampWriter() {
        try {
            if (!isTimeStampWriterAvailable()) {
                initialTimeStampWriter();
            }
            return timeStampWriter;
        } catch (Exception e) {
            throw BINonValueUtils.beyondControl(e);
        }
    }

    public ICubeLongReaderWrapper getTimeStampReader() {
        try {
            if (!isTimeStampReaderAvailable()) {
                initialTimeStampReader();
            }
            return timeStampReader;
        } catch (Exception e) {
            throw BINonValueUtils.beyondControl(e);
        }
    }

    public ICubeStringWriter getParentsWriter() {
        try {
            if (!isParentWriterAvailable()) {
                initialParentTableWriter();
            }
            return parentsWriter;
        } catch (Exception e) {
            throw BINonValueUtils.beyondControl(e);
        }
    }

    public ICubeStringReader getParentsReader() {
        try {
            if (!isParentReaderAvailable()) {
                initialParentTableReader();
            }
            return parentsReader;
        } catch (Exception e) {
            throw BINonValueUtils.beyondControl(e);
        }
    }

    private void initialField() {
        if (getFieldInfoReader().canRead() && !isFieldInit()) {
            tableFields = new ArrayList<CubeFieldSource>();
            try {
                int columnSize = Integer.parseInt(getFieldInfoReader().getSpecificValue(0));
                for (int pos = 1; pos <= columnSize; pos++) {
                    JSONObject jo = new JSONObject(getFieldInfoReader().getSpecificValue(pos));
                    BICubeFieldSource field = new BICubeFieldSource(null, null, 0, 0);
                    field.parseJSON(jo);
                    tableFields.add(field);
                }
            } catch (BIResourceInvalidException e) {
                BILogger.getLogger().error(e.getMessage(), e);
                BINonValueUtils.beyondControl(e.getMessage(), e);
            } catch (Exception e) {
                BILogger.getLogger().error(e.getMessage(), e);
                BINonValueUtils.beyondControl(e.getMessage(), e);
            }
        }
    }

    private void initialParentsTable() {
        if (getParentsReader().canRead() && !isParentTableInit()) {
            parentTable = new ArrayList<ITableKey>();
            ICubeStringReader parentReader = getParentsReader();
            try {
                int columnSize = Integer.parseInt(parentReader.getSpecificValue(0));
                for (int pos = 1; pos <= columnSize; pos++) {
                    parentTable.add(new BITableKey(parentReader.getSpecificValue(pos)));
                }
            } catch (BIResourceInvalidException e) {
                BILogger.getLogger().error(e.getMessage(), e);
                BINonValueUtils.beyondControl(e.getMessage(), e);
            } catch (Exception e) {
                BILogger.getLogger().error(e.getMessage(), e);
                BINonValueUtils.beyondControl(e.getMessage(), e);
            }
        }
    }

    @Override
    public void recordTableStructure(List<CubeFieldSource> fields) {
        /**
         * 即便是空，也要记录是空数组0的长度。
         */
        if (fields == null) {
            fields = new ArrayList<CubeFieldSource>();
        }
        Iterator<CubeFieldSource> fieldIterator = fields.iterator();
        int position = 0;
        getFieldInfoWriter().recordSpecificValue(position, String.valueOf(fields.size()));//First position records size of columns.
        position++;
        while (fieldIterator.hasNext()) {
            CubeFieldSource field = fieldIterator.next();
            try {
                getFieldInfoWriter().recordSpecificValue(position, field.createJSON().toString());
                position++;
            } catch (Exception e) {
                BINonValueUtils.beyondControl("the field:" + field.toString() + " createJson method has problem.");
            }
        }
    }


    @Override
    public void recordRowCount(long rowCount) {
        getRowCountWriter().recordSpecificValue(0, rowCount);

    }

    @Override
    public void recordLastTime() {
        recordLastTime(System.currentTimeMillis());
    }

    @Override
    public void recordParentsTable(List<ITableKey> parentTables) {
        ICubeStringWriter writer = getParentsWriter();
        writer.recordSpecificValue(0, Integer.toString(parentTables.size()));
        int count = 1;
        for (ITableKey key : parentTables) {
            writer.recordSpecificValue(count++, key.getSourceID());
        }
    }

    @Override
    public List<ITableKey> getParentsTable() {
        initialParentsTable();
        return parentTable;
    }

    protected void recordLastTime(long time) {
        getTimeStampWriter().recordSpecificValue(0, time);
    }

    @Override
    public int getRowCount() {
        try {
            return Integer.parseInt(String.valueOf(getRowCountReader().getSpecificValue(0)));
        } catch (BIResourceInvalidException e) {
            BILogger.getLogger().error(e.getMessage(), e);
            BINonValueUtils.beyondControl(e.getMessage(), e);
        }
        return -1;
    }

    @Override
    public Date getCubeLastTime() {
        try {
            return new Date(getTimeStampReader().getSpecificValue(0));
        } catch (BIResourceInvalidException e) {
            BILogger.getLogger().error(e.getMessage(), e);
            BINonValueUtils.beyondControl(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public void recordTableGenerateVersion(int version) {
        getVersionWriter().recordSpecificValue(0, version);
    }

    @Override
    public int getTableVersion() {
        try {
            return Integer.parseInt(String.valueOf(getVersionReader().getSpecificValue(0)));
        } catch (BIResourceInvalidException e) {
            BILogger.getLogger().error(e.getMessage(), e);
            BINonValueUtils.beyondControl(e.getMessage(), e);
        }
        return -1;
    }

    @Override
    public List<CubeFieldSource> getFieldInfo() {
        initialField();
        return tableFields;
    }

    @Override
    public Boolean isPropertyExist() {
        return getFieldInfoReader().canRead();
    }

    @Override
    public Boolean isRowCountAvailable() {
        return getRowCountReader().canRead();
    }

    protected void resetFieldReader() {
        if (isFieldReaderAvailable()) {
            fieldInfoReader.clear();
            fieldInfoReader = null;
        }
    }

    @Override
    public void recordFieldNamesFromParent(Set<String> fieldNames) {
        parentFieldProperty.recordParentFields(fieldNames);
    }

    @Override
    public Set<String> getFieldNamesFromParent() {
        return parentFieldProperty.getParentFields();
    }

    protected void resetVersionWriter() {
        if (isVersionWriterAvailable()) {
            versionWriter.clear();
            versionWriter = null;
        }
    }

    protected void resetVersionReader() {
        if (isVersionReaderAvailable()) {
            versionReader.clear();
            versionReader = null;
        }
    }

    protected void resetRowCountWriter() {
        if (isRowCountWriterAvailable()) {
            rowCountWriter.clear();
            rowCountWriter = null;
        }
    }

    protected void resetRowCountReader() {
        if (isRowCountReaderAvailable()) {
            rowCountReader.clear();
            rowCountReader = null;
        }
    }

    protected void resetTimeStampWriter() {
        if (isTimeStampWriterAvailable()) {
            timeStampWriter.clear();
            timeStampWriter = null;
        }
    }

    protected void resetTimeStampReader() {
        if (isTimeStampReaderAvailable()) {
            timeStampReader.clear();
            timeStampReader = null;
        }
    }

    protected void resetParentWriter() {
        if (isParentWriterAvailable()) {
            parentsWriter.clear();
            parentsWriter = null;
        }
    }

    protected void resetParentReader() {
        if (isParentReaderAvailable()) {
            parentsReader.clear();
            parentsReader = null;
        }
    }

    @Override
    public void clear() {
        resetFiledWriter();
        resetFieldReader();
        resetVersionWriter();
        resetVersionReader();
        resetRowCountWriter();
        resetRowCountReader();
        resetTimeStampWriter();
        resetTimeStampReader();
        resetParentReader();
        resetParentWriter();
        parentFieldProperty.clear();
    }

    public void forceRelease() {
        if (isFieldWriterAvailable()) {
            fieldInfoWriter.forceRelease();
        }
        if (isFieldReaderAvailable()) {
            fieldInfoReader.forceRelease();
        }
        if (isVersionReaderAvailable()) {
            versionReader.forceRelease();
        }
        if (isVersionWriterAvailable()) {
            versionWriter.forceRelease();
        }
        if (isRowCountReaderAvailable()) {
            rowCountReader.forceRelease();
        }
        if (isRowCountWriterAvailable()) {
            rowCountWriter.forceRelease();
        }
        if (isTimeStampWriterAvailable()) {
            timeStampWriter.forceRelease();
        }
        if (isTimeStampReaderAvailable()) {
            timeStampReader.forceRelease();
        }
        if (isParentWriterAvailable()) {
            parentsWriter.forceRelease();
        }
        if (isParentReaderAvailable()) {
            parentsReader.forceRelease();
        }
        parentFieldProperty.forceRelease();
        clear();
    }
}

