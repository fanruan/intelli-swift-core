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
        ICubeResourceDiscovery discovery = BIFactoryHelper.getObject(ICubeResourceDiscovery.class);
        try {
            initialFieldInfo(discovery);
            initialRowCount(discovery);
            initialTimeStamp(discovery);
        } catch (Exception e) {
            throw BINonValueUtils.beyondControl(e.getMessage(), e);
        }
    }

    private void initialFieldInfo(ICubeResourceDiscovery discovery) throws Exception {
        ICubeResourceLocation mainLocation = this.currentLocation.buildChildLocation(MAIN_DATA);
        mainLocation.setStringType();
        mainLocation.setReaderSourceLocation();
        fieldInfoReader = (ICubeStringReader) discovery.getCubeReader(mainLocation);
        mainLocation.setWriterSourceLocation();
        fieldInfoWriter = (ICubeStringWriter) discovery.getCubeWriter(mainLocation);
    }

    private void initialRowCount(ICubeResourceDiscovery discovery) throws Exception {
        ICubeResourceLocation rowCountLocation = this.currentLocation.buildChildLocation(ROW_COUNT_DATA);
        rowCountLocation.setLongTypeWrapper();
        rowCountLocation.setReaderSourceLocation();
        rowCountReader = (ICubeLongReaderWrapper) discovery.getCubeReader(rowCountLocation);
        rowCountLocation.setWriterSourceLocation();
        rowCountWriter = (ICubeLongWriterWrapper) discovery.getCubeWriter(rowCountLocation);
    }

    private void initialTimeStamp(ICubeResourceDiscovery discovery) throws Exception {
        ICubeResourceLocation rowCountLocation = this.currentLocation.buildChildLocation(TIMESTAMP_DATA);
        rowCountLocation.setLongTypeWrapper();
        rowCountLocation.setWriterSourceLocation();
        timeStampWriter = (ICubeLongWriterWrapper) discovery.getCubeWriter(rowCountLocation);
        rowCountLocation.setReaderSourceLocation();
        timeStampReader = (ICubeLongReaderWrapper) discovery.getCubeReader(rowCountLocation);

    }

    private void initialField() {
        if (fieldInfoReader.canRead() && !isFieldInit()) {
            tableFields = new ArrayList<DBField>();
            try {
                int columnSize = Integer.parseInt(fieldInfoReader.getSpecificValue(0));
                for (int pos = 1; pos <= columnSize; pos++) {
                    JSONObject jo = new JSONObject(fieldInfoReader.getSpecificValue(pos));
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

    private boolean isFieldInit() {
        return tableFields != null;
    }

    @Override
    public void recordTableStructure(List<DBField> fields) {
        Iterator<DBField> fieldIterator = fields.iterator();
        int position = 0;
        fieldInfoWriter.recordSpecificValue(position, String.valueOf(fields.size()));//First position records size of columns.
        position++;
        while (fieldIterator.hasNext()) {
            DBField field = fieldIterator.next();
            try {
                fieldInfoWriter.recordSpecificValue(position, field.createJSON().toString());
                position++;
            } catch (Exception e) {
                BINonValueUtils.beyondControl("the field:" + field.toString() + " createJson method has problem.");
            }
        }
    }


    @Override
    public void recordRowCount(long rowCount) {
        rowCountWriter.recordSpecificValue(0, rowCount);

    }

    @Override
    public void recordLastTime() {
        timeStampWriter.recordSpecificValue(0, System.currentTimeMillis());
    }


    @Override
    public int getRowCount() {
        try {
            return Integer.parseInt(String.valueOf(rowCountReader.getSpecificValue(0)));
        } catch (BIResourceInvalidException e) {
            BILogger.getLogger().error(e.getMessage(), e);
            BINonValueUtils.beyondControl(e.getMessage(), e);
        }
        return -1;
    }

    @Override
    public Date getCubeLastTime() {
        try {
            return new Date(timeStampReader.getSpecificValue(0));
        } catch (BIResourceInvalidException e) {
            BILogger.getLogger().error(e.getMessage(), e);
            BINonValueUtils.beyondControl(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public void recordTableGenerateVersion(int version) {
        versionWriter.recordSpecificValue(0, version);
    }

    @Override
    public int getTableVersion() {
        try {
            return Integer.parseInt(String.valueOf(versionReader.getSpecificValue(0)));
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
        return fieldInfoReader.canRead();
    }

    @Override
    public void clear() {
        fieldInfoWriter.clear();
        fieldInfoReader.clear();

        versionWriter.clear();
        versionReader.clear();

        rowCountWriter.clear();
        rowCountReader.clear();

        timeStampWriter.clear();
        timeStampReader.clear();
    }
}

