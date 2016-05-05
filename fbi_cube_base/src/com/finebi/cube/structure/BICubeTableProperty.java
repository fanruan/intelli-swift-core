package com.finebi.cube.structure;

import com.finebi.cube.data.ICubeResourceDiscovery;
import com.finebi.cube.data.input.ICubeIntegerReaderWrapper;
import com.finebi.cube.data.input.ICubeLongReaderWrapper;
import com.finebi.cube.data.input.ICubeStringReader;
import com.finebi.cube.data.output.ICubeIntegerWriterWrapper;
import com.finebi.cube.data.output.ICubeLongWriterWrapper;
import com.finebi.cube.data.output.ICubeStringWriter;
import com.finebi.cube.exception.BIResourceInvalidException;
import com.finebi.cube.location.ICubeResourceLocation;
import com.fr.bi.common.factory.BIFactoryHelper;
import com.fr.bi.stable.data.db.DBField;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.bi.stable.utils.program.BINonValueUtils;
import com.fr.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

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
    private List<DBField> tableFields = null;
    private ICubeResourceDiscovery discovery;

    private ICubeStringWriter fieldInfoWriter;
    private ICubeStringReader fieldInfoReader;

    private ICubeIntegerWriterWrapper versionWriter;
    private ICubeIntegerReaderWrapper versionReader;

    private ICubeLongWriterWrapper rowCountWriter;
    private ICubeLongReaderWrapper rowCountReader;

    private ICubeLongWriterWrapper timeStampWriter;
    private ICubeLongReaderWrapper timeStampReader;


    public BICubeTableProperty(ICubeResourceLocation currentLocation) {
        this.currentLocation = currentLocation.copy();
        discovery = BIFactoryHelper.getObject(ICubeResourceDiscovery.class);
        try {
        } catch (Exception e) {
            throw BINonValueUtils.beyondControl(e.getMessage(), e);
        }
    }

    protected boolean isFieldWriterAvailable() {
        return fieldInfoWriter != null;
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

    protected boolean isFieldInit() {
        return tableFields != null;
    }

    private void initialFieldInfoReader() throws Exception {
        ICubeResourceLocation mainLocation = this.currentLocation.buildChildLocation(MAIN_DATA);
        mainLocation.setStringType();
        mainLocation.setReaderSourceLocation();
        fieldInfoReader = (ICubeStringReader) discovery.getCubeReader(mainLocation);

    }

    private void initialFieldInfoWriter() throws Exception {
        if (!isFieldWriterAvailable()) {
            ICubeResourceLocation mainLocation = this.currentLocation.buildChildLocation(MAIN_DATA);
            mainLocation.setStringType();
            mainLocation.setWriterSourceLocation();
            fieldInfoWriter = (ICubeStringWriter) discovery.getCubeWriter(mainLocation);
        }
    }

    private void initialRowCountWriter() throws Exception {
        if (!isRowCountWriterAvailable()) {
            ICubeResourceLocation rowCountLocation = this.currentLocation.buildChildLocation(ROW_COUNT_DATA);
            rowCountLocation.setLongTypeWrapper();
            rowCountLocation.setWriterSourceLocation();
            rowCountWriter = (ICubeLongWriterWrapper) discovery.getCubeWriter(rowCountLocation);
        }
    }

    private void initialRowCountReader() throws Exception {
        if (!isRowCountReaderAvailable()) {
            ICubeResourceLocation rowCountLocation = this.currentLocation.buildChildLocation(ROW_COUNT_DATA);
            rowCountLocation.setLongTypeWrapper();
            rowCountLocation.setReaderSourceLocation();
            rowCountReader = (ICubeLongReaderWrapper) discovery.getCubeReader(rowCountLocation);
        }
    }

    private void initialTimeStampReader() throws Exception {
        if (!isTimeStampReaderAvailable()) {
            ICubeResourceLocation rowCountLocation = this.currentLocation.buildChildLocation(TIMESTAMP_DATA);
            rowCountLocation.setLongTypeWrapper();
            rowCountLocation.setReaderSourceLocation();
            timeStampReader = (ICubeLongReaderWrapper) discovery.getCubeReader(rowCountLocation);
        }

    }

    private void initialTimeStampWriter() throws Exception {
        if (!isTimeStampWriterAvailable()) {
            ICubeResourceLocation rowCountLocation = this.currentLocation.buildChildLocation(TIMESTAMP_DATA);
            rowCountLocation.setLongTypeWrapper();
            rowCountLocation.setWriterSourceLocation();
            timeStampWriter = (ICubeLongWriterWrapper) discovery.getCubeWriter(rowCountLocation);
        }
    }

    private void initialVersionReader() throws Exception {
        if (!isVersionReaderAvailable()) {
            ICubeResourceLocation rowCountLocation = this.currentLocation.buildChildLocation(VERSION_DATA);
            rowCountLocation.setIntegerTypeWrapper();
            rowCountLocation.setReaderSourceLocation();
            versionReader = (ICubeIntegerReaderWrapper) discovery.getCubeReader(rowCountLocation);
        }
    }

    private void initialVersionWriter() throws Exception {
        ICubeResourceLocation rowCountLocation = this.currentLocation.buildChildLocation(VERSION_DATA);
        rowCountLocation.setIntegerTypeWrapper();
        rowCountLocation.setWriterSourceLocation();
        versionWriter = (ICubeIntegerWriterWrapper) discovery.getCubeWriter(rowCountLocation);

    }

    public ICubeStringWriter getFieldInfoWriter() {
        try {
            initialFieldInfoWriter();
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
            initialVersionReader();
            return versionReader;
        } catch (Exception e) {
            throw BINonValueUtils.beyondControl(e);
        }
    }

    public ICubeLongWriterWrapper getRowCountWriter() {
        try {
            initialRowCountWriter();
            return rowCountWriter;
        } catch (Exception e) {
            throw BINonValueUtils.beyondControl(e);
        }
    }

    public ICubeLongReaderWrapper getRowCountReader() {
        try {
            initialRowCountReader();
            return rowCountReader;
        } catch (Exception e) {
            throw BINonValueUtils.beyondControl(e);
        }
    }

    public ICubeLongWriterWrapper getTimeStampWriter() {
        try {
            initialTimeStampWriter();
            return timeStampWriter;
        } catch (Exception e) {
            throw BINonValueUtils.beyondControl(e);
        }
    }

    public ICubeLongReaderWrapper getTimeStampReader() {
        try {
            initialTimeStampReader();
            return timeStampReader;
        } catch (Exception e) {
            throw BINonValueUtils.beyondControl(e);
        }
    }

    private void initialField() {
        if (getFieldInfoReader().canRead() && !isFieldInit()) {
            tableFields = new ArrayList<DBField>();
            try {
                int columnSize = Integer.parseInt(getFieldInfoReader().getSpecificValue(0));
                for (int pos = 1; pos <= columnSize; pos++) {
                    JSONObject jo = new JSONObject(getFieldInfoReader().getSpecificValue(pos));
                    DBField field = DBField.getBiEmptyField();
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


    @Override
    public void recordTableStructure(List<DBField> fields) {
        Iterator<DBField> fieldIterator = fields.iterator();
        int position = 0;
        getFieldInfoWriter().recordSpecificValue(position, String.valueOf(fields.size()));//First position records size of columns.
        position++;
        while (fieldIterator.hasNext()) {
            DBField field = fieldIterator.next();
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
    public List<DBField> getFieldInfo() {
        initialField();
        return tableFields;
    }

    @Override
    public Boolean isPropertyExist() {
        return getFieldInfoReader().canRead();
    }

    @Override
    public void clear() {
        if (isFieldWriterAvailable()) {
            fieldInfoWriter.clear();
        }
        if (isFieldReaderAvailable()) {
            fieldInfoReader.clear();
        }
        if (isVersionWriterAvailable()) {
            versionWriter.clear();
        }
        if (isVersionReaderAvailable()) {
            versionReader.clear();
        }
        if (isRowCountWriterAvailable()) {
            rowCountWriter.clear();
        }
        if (isRowCountReaderAvailable()) {
            rowCountReader.clear();
        }
        if (isTimeStampWriterAvailable()) {
            timeStampWriter.clear();
        }
        if (isTimeStampReaderAvailable()) {
            timeStampReader.clear();
        }
    }
}

