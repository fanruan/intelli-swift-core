package com.finebi.cube.structure.group;

import com.finebi.cube.data.ICubePrimitiveResourceDiscovery;
import com.finebi.cube.data.ICubeResourceDiscovery;
import com.finebi.cube.data.input.ICubeReader;
import com.finebi.cube.data.input.primitive.ICubeIntegerReader;
import com.finebi.cube.data.output.ICubeWriter;
import com.finebi.cube.data.output.primitive.ICubeIntegerWriter;
import com.finebi.cube.exception.BIBuildReaderException;
import com.finebi.cube.exception.BIBuildWriterException;
import com.finebi.cube.exception.IllegalCubeResourceLocationException;
import com.finebi.cube.exception.BIResourceInvalidException;
import com.finebi.cube.location.ICubeResourceLocation;
import com.fr.bi.common.factory.BIFactoryHelper;
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
    protected ICubeIntegerReader groupLengthReader;
    protected ICubeIntegerWriter groupLengthWriter;
    protected Comparator groupComparator;
    protected GroupValueSearchAssistance groupValueSearchAssistance;
    protected ICubeResourceLocation currentLocation;

    public BICubeGroupData(ICubeResourceLocation superLocation) {
        try {
            currentLocation = superLocation.buildChildLocation("group.fbi");
            ICubePrimitiveResourceDiscovery primitiveResourceDiscovery = BIFactoryHelper.getObject(ICubePrimitiveResourceDiscovery.class);
            groupValueSearchAssistance = new GroupValueSearchAssistance();
            ICubeResourceLocation sizeLocation = superLocation.buildChildLocation("size.fbi");
            sizeLocation.setIntegerType();
            sizeLocation.setReaderSourceLocation();
            groupLengthReader = (ICubeIntegerReader) primitiveResourceDiscovery.getCubeReader(sizeLocation);
            sizeLocation.setWriterSourceLocation();
            groupLengthWriter = (ICubeIntegerWriter) primitiveResourceDiscovery.getCubeWriter(sizeLocation);
            initial();
        } catch (Exception e) {
            BINonValueUtils.beyondControl(e.getMessage(), e);
        }
    }


    protected void initial() throws IllegalCubeResourceLocationException, BIBuildWriterException, BIBuildReaderException {
        currentLocation.setWriterSourceLocation();
        currentLocation = setGroupType();
        ICubeResourceDiscovery resourceDiscovery = BIFactoryHelper.getObject(ICubeResourceDiscovery.class);
        groupWriter = resourceDiscovery.getCubeWriter(currentLocation);
        currentLocation.setReaderSourceLocation();
        groupReader = resourceDiscovery.getCubeReader(currentLocation);
    }

    protected abstract ICubeResourceLocation setGroupType();

    @Override
    public void addGroupDataValue(int positionInGroup, T groupValue) {
        groupWriter.recordSpecificValue(positionInGroup, groupValue);
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
            return groupLengthReader.getSpecificValue(0);
        } catch (BIResourceInvalidException e) {
            BILogger.getLogger().error(e.getMessage(), e);
        }
        return -1;
    }

    @Override
    public T getGroupValueByPosition(int position) {
        try {
            return groupReader.getSpecificValue(position);

        } catch (BIResourceInvalidException e) {
            BILogger.getLogger().error(e.getMessage(), e);
        }

        return null;
    }

    @Override
    public void writeSizeOfGroup(int size) {
        groupLengthWriter.recordSpecificPositionValue(0, size);
    }

    @Override
    public void clear() {
        groupWriter.clear();
        groupReader.clear();
        groupLengthReader.clear();
        groupLengthWriter.clear();
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
