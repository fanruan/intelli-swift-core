package com.finebi.cube.structure.group;

import com.finebi.cube.data.ICubeResourceDiscovery;
import com.finebi.cube.data.input.ICubeIntegerReaderWrapper;
import com.finebi.cube.data.input.ICubeReader;
import com.finebi.cube.data.output.ICubeIntegerWriterWrapper;
import com.finebi.cube.data.output.ICubeWriter;
import com.finebi.cube.exception.BIResourceInvalidException;
import com.finebi.cube.location.ICubeResourceLocation;
import com.fr.bi.stable.io.sortlist.ArrayLookupHelper;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.bi.stable.utils.program.BINonValueUtils;

import java.util.Comparator;

/**
 * This class created on 2016/3/28.
 *
 * @author Connery
 * @since 4.0
 */
public abstract class BICubeGroupData<T> implements ICubeGroupDataService<T> {

    protected ICubeWriter<T> groupWriter;
    protected ICubeReader<T> groupReader;
    protected ICubeIntegerReaderWrapper groupLengthReader;
    protected ICubeIntegerWriterWrapper groupLengthWriter;
    protected Comparator groupComparator;
    protected GroupValueSearchAssistance groupValueSearchAssistance;
    protected ICubeResourceLocation superLocation;
    protected ICubeResourceLocation currentLocation;
    private ICubeResourceDiscovery resourceDiscovery;

    public BICubeGroupData(ICubeResourceDiscovery resourceDiscovery, ICubeResourceLocation superLocation) {
        try {
            this.superLocation = superLocation;
            currentLocation = superLocation.buildChildLocation("group.fbi");
            this.resourceDiscovery = resourceDiscovery;
            groupValueSearchAssistance = new GroupValueSearchAssistance();

        } catch (Exception e) {
            BINonValueUtils.beyondControl(e.getMessage(), e);
        }
    }

    private void initGroupWriter() {
        try {
            ICubeResourceLocation currentLocation = setGroupType();

            currentLocation.setWriterSourceLocation();
            groupWriter = resourceDiscovery.getCubeWriter(currentLocation);
        } catch (Exception e) {
            BINonValueUtils.beyondControl(e.getMessage(), e);
        }
    }

    private void initGroupReader() {
        try {
            ICubeResourceLocation currentLocation = setGroupType();

            currentLocation.setReaderSourceLocation();
            groupReader = resourceDiscovery.getCubeReader(currentLocation);
        } catch (Exception e) {
            BINonValueUtils.beyondControl(e.getMessage(), e);
        }
    }

    private void initGroupLengthReader() {
        try {
            ICubeResourceLocation sizeLocation = superLocation.buildChildLocation("size.fbi");
            sizeLocation.setIntegerType();
            sizeLocation.setReaderSourceLocation();
            groupLengthReader = (ICubeIntegerReaderWrapper) resourceDiscovery.getCubeReader(sizeLocation);
        } catch (Exception e) {
            BINonValueUtils.beyondControl(e.getMessage(), e);
        }
    }

    private void initGroupLengthWriter() {
        try {
            ICubeResourceLocation sizeLocation = superLocation.buildChildLocation("size.fbi");
            sizeLocation.setIntegerType();
            sizeLocation.setWriterSourceLocation();
            groupLengthWriter = (ICubeIntegerWriterWrapper) resourceDiscovery.getCubeWriter(sizeLocation);
        } catch (Exception e) {
            BINonValueUtils.beyondControl(e.getMessage(), e);
        }
    }

    public ICubeWriter<T> getGroupWriter() {
        if (!isGroupWriterAvailable()) {
            initGroupWriter();
        }
        return groupWriter;
    }

    public ICubeReader<T> getGroupReader() {
        if (!isGroupReaderAvailable()) {
            initGroupReader();
        }
        return groupReader;
    }

    public ICubeIntegerReaderWrapper getGroupLengthReader() {
        if (!isLengthReaderAvailable()) {
            initGroupLengthReader();
        }
        return groupLengthReader;
    }

    public ICubeIntegerWriterWrapper getGroupLengthWriter() {
        if (!isLengthWriterAvailable()) {
            initGroupLengthWriter();
        }
        return groupLengthWriter;
    }

    protected boolean isGroupReaderAvailable() {
        return groupReader != null;
    }

    protected boolean isGroupWriterAvailable() {
        return groupWriter != null;
    }

    protected boolean isLengthReaderAvailable() {
        return groupLengthReader != null;
    }

    protected boolean isLengthWriterAvailable() {
        return groupLengthWriter != null;
    }

    protected abstract ICubeResourceLocation setGroupType();

    @Override
    public void addGroupDataValue(int positionInGroup, T groupValue) {
        getGroupWriter().recordSpecificValue(positionInGroup, groupValue);
    }

    @Override
    public Comparator<T> getGroupComparator() {
        return groupComparator == null ? defaultComparator() : groupComparator;
    }


    @Override
    public void setGroupComparator(Comparator groupComparator) {
        this.groupComparator = groupComparator;
    }

    protected abstract Comparator<T> defaultComparator();

    @Override
    public int getPositionOfGroupValue(T groupValue) throws BIResourceInvalidException {
        return ArrayLookupHelper.lookup((T[]) new Object[]{groupValue}, groupValueSearchAssistance)[0];
    }

    @Override
    public int sizeOfGroup() {

        try {
            return getGroupLengthReader().getSpecificValue(0);
        } catch (BIResourceInvalidException e) {
            BILogger.getLogger().error(e.getMessage(), e);
        }
        return -1;
    }

    @Override
    public T getGroupValueByPosition(int position) {
        try {
            return getGroupReader().getSpecificValue(position);

        } catch (BIResourceInvalidException e) {
            BILogger.getLogger().error(e.getMessage(), e);
        }

        return null;
    }

    @Override
    public void writeSizeOfGroup(int size) {
        getGroupLengthWriter().recordSpecificValue(0, size);
    }


    protected void resetGroupWriter() {
        if (isGroupWriterAvailable()) {
            groupWriter.clear();
            groupWriter = null;
        }
    }

    protected void resetGroupReader() {
        if (isGroupReaderAvailable()) {
            groupReader.clear();
            groupReader = null;
        }
    }

    protected void resetLengthReader() {
        if (isLengthReaderAvailable()) {
            groupLengthReader.clear();
            groupLengthReader = null;
        }
    }

    protected void resetLengthWriter() {
        if (isLengthWriterAvailable()) {
            groupLengthWriter.clear();
            groupLengthWriter = null;
        }
    }

    @Override
    public void clear() {
        resetGroupWriter();
        resetGroupReader();
        resetLengthReader();
        resetLengthWriter();
    }

    public class GroupValueSearchAssistance implements ArrayLookupHelper.Lookup<T> {
        @Override
        public int minIndex() {
            return 0;
        }

        @Override
        public int maxIndex() {
            return sizeOfGroup();
        }

        @Override
        public T lookupByIndex(int index) {
            return getGroupValueByPosition(index);
        }

        @Override
        public int compare(T t1, T t2) {
            return getGroupComparator().compare(t1, t2);
        }
    }
}
