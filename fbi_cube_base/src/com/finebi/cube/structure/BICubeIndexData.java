package com.finebi.cube.structure;

import com.finebi.cube.data.ICubeResourceDiscovery;
import com.finebi.cube.data.input.ICubeGroupValueIndexReader;
import com.finebi.cube.data.output.ICubeGroupValueIndexWriter;
import com.finebi.cube.exception.BICubeIndexException;
import com.finebi.cube.exception.BIResourceInvalidException;
import com.finebi.cube.location.ICubeResourceLocation;
import com.fr.bi.common.factory.BIFactoryHelper;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.utils.program.BINonValueUtils;

/**
 * This class created on 2016/3/3.
 *
 * @author Connery
 * @since 4.0
 */
public class BICubeIndexData implements ICubeIndexDataService {

    private ICubeGroupValueIndexReader indexReader;
    private ICubeGroupValueIndexWriter indexWriter;
    private ICubeGroupValueIndexReader nullReader;
    private ICubeGroupValueIndexWriter nullWriter;
    private ICubeResourceLocation currentLocation;

    public BICubeIndexData(ICubeResourceLocation currentLocation) {
        try {
            this.currentLocation = currentLocation;
            ICubeResourceLocation indexLocation = currentLocation.buildChildLocation("fbi_index");
            indexLocation.setGroupValueIndexType();
            indexLocation.setReaderSourceLocation();
            indexReader = (ICubeGroupValueIndexReader) BIFactoryHelper.getObject(ICubeResourceDiscovery.class).getCubeReader(indexLocation);
            indexLocation.setWriterSourceLocation();
            indexWriter = (ICubeGroupValueIndexWriter) BIFactoryHelper.getObject(ICubeResourceDiscovery.class).getCubeWriter(indexLocation);

            ICubeResourceLocation nullIndexLocation = currentLocation.buildChildLocation("fbi_null");
            nullIndexLocation.setGroupValueIndexType();
            nullIndexLocation.setReaderSourceLocation();
            nullReader = (ICubeGroupValueIndexReader) BIFactoryHelper.getObject(ICubeResourceDiscovery.class).getCubeReader(nullIndexLocation);
            nullIndexLocation.setWriterSourceLocation();
            nullWriter = (ICubeGroupValueIndexWriter) BIFactoryHelper.getObject(ICubeResourceDiscovery.class).getCubeWriter(nullIndexLocation);

        } catch (Exception e) {
            BINonValueUtils.beyondControl("", e);
        }
    }

    @Override
    public GroupValueIndex getBitmapIndex(int position) throws BICubeIndexException {
        try {
            return indexReader.getSpecificValue(position);
        } catch (BIResourceInvalidException e) {
            throw new BICubeIndexException(e.getMessage(), e);
        }

    }

    @Override
    public GroupValueIndex getNULLIndex(int position) throws BICubeIndexException {
        try {
            return nullReader.getSpecificValue(position);
        } catch (BIResourceInvalidException e) {
            throw new BICubeIndexException(e.getMessage(), e);
        }
    }


    @Override
    public void addIndex(int position, GroupValueIndex groupValueIndex) {
        indexWriter.recordSpecificValue(position, groupValueIndex);
    }

    @Override
    public void addNULLIndex(int position, GroupValueIndex groupValueIndex) {
        nullWriter.recordSpecificValue(position, groupValueIndex);
    }

    @Override
    public void clear() {
        indexReader.clear();
        indexWriter.clear();
        nullReader.clear();
        nullWriter.clear();
    }

    @Override
    public boolean isEmpty() {
        return !indexReader.canRead();
    }
}
