package com.finebi.cube.structure.table.property;

import com.finebi.cube.data.ICubeResourceDiscovery;
import com.finebi.cube.data.disk.reader.BIStringNIOReader;
import com.finebi.cube.data.disk.writer.BIStringNIOWriter;
import com.finebi.cube.exception.BIResourceInvalidException;
import com.finebi.cube.location.ICubeResourceLocation;
import com.finebi.cube.structure.property.BICubeProperty;
import com.fr.bi.stable.utils.program.BINonValueUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * This class created on 2016/5/18.
 *
 * @author Connery
 * @since 4.0
 */
public class ParentFieldProperty extends BICubeProperty<BIStringNIOReader, BIStringNIOWriter> {


    private Set<String> fieldNames;
    private static String PARENT_FIELD = "pf";

    public ParentFieldProperty(ICubeResourceLocation currentLocation, ICubeResourceDiscovery discovery) {
        super(currentLocation, discovery);
    }

    @Override
    protected String getPropertyName() {
        return PARENT_FIELD;
    }

    @Override
    protected void setReaderType(ICubeResourceLocation rowCountLocation) {
        rowCountLocation.setStringType();
    }

    @Override
    protected void setWriterType(ICubeResourceLocation rowCountLocation) {
        rowCountLocation.setStringType();
    }

    public void recordParentFields(Set<String> fieldNames) {
        if (fieldNames != null) {
            recordParentFieldSize(fieldNames);
            int count = 1;
            for (String name : fieldNames) {
                getWriter().recordSpecificValue(count++, name);
            }
            refreshField();
            this.fieldNames.addAll(fieldNames);
        }
    }

    private void refreshField() {
        if (isParentFieldInitial()) {
            fieldNames.clear();
        }
        fieldNames = new HashSet<String>();
    }

    private void recordParentFieldSize(Set<String> fieldNames) {
        if (fieldNames != null) {
            getWriter().recordSpecificValue(0, Integer.toString(fieldNames.size()));
        }
    }

    public Set<String> getParentFields() {
        if (!isParentFieldInitial()) {
            initialFieldNames();
        }
        return fieldNames;
    }

    private void initialFieldNames() {
        try {
            refreshField();
            if (getReader().canRead()) {
                int count = Integer.parseInt(getReader().getSpecificValue(0));
                for (int i = 1; i <= count; i++) {
                    fieldNames.add(getReader().getSpecificValue(i));
                }
            }
        } catch (BIResourceInvalidException e) {
            throw BINonValueUtils.beyondControl(e);
        }
    }

    private boolean isParentFieldInitial() {
        return fieldNames != null;
    }
}
