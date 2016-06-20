package com.finebi.cube.structure;

import com.finebi.cube.data.ICubeResourceDiscovery;
import com.finebi.cube.data.input.ICubeGroupValueIndexReader;
import com.finebi.cube.data.output.ICubeGroupValueIndexWriter;
import com.finebi.cube.exception.BICubeIndexException;
import com.finebi.cube.exception.BIResourceInvalidException;
import com.finebi.cube.location.ICubeResourceLocation;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.utils.program.BINonValueUtils;

import java.net.URISyntaxException;

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
    private ICubeResourceLocation indexLocation;
    private ICubeResourceLocation nullIndexLocation;
    private ICubeResourceDiscovery discovery;

    public BICubeIndexData(ICubeResourceDiscovery discovery, ICubeResourceLocation currentLocation) {
        try {
            this.discovery = discovery;
            this.currentLocation = currentLocation;
            indexLocation = currentLocation.buildChildLocation("fbi_index");
            nullIndexLocation = currentLocation.buildChildLocation("fbi_null");
        } catch (URISyntaxException e) {
            BINonValueUtils.beyondControl(e);

        }


    }

    private void initIndexReader() {
        try {
            indexLocation.setGroupValueIndexType();
            indexLocation.setReaderSourceLocation();
            indexReader = (ICubeGroupValueIndexReader) discovery.getCubeReader(indexLocation);
        } catch (Exception e) {
            BINonValueUtils.beyondControl(e);
        }
    }

    private void initIndexWriter() {
        try {
            indexLocation.setGroupValueIndexType();
            indexLocation.setWriterSourceLocation();
            indexWriter = (ICubeGroupValueIndexWriter) discovery.getCubeWriter(indexLocation);
        } catch (Exception e) {
            BINonValueUtils.beyondControl(e);
        }
    }

    private void initNullReader() {
        try {
            nullIndexLocation.setReaderSourceLocation();
            nullIndexLocation.setGroupValueIndexType();
            nullReader = (ICubeGroupValueIndexReader) discovery.getCubeReader(nullIndexLocation);
        } catch (Exception e) {
            BINonValueUtils.beyondControl(e);
        }
    }

    private void initNullWriter() {
        try {
            nullIndexLocation.setWriterSourceLocation();
            nullIndexLocation.setGroupValueIndexType();
            nullWriter = (ICubeGroupValueIndexWriter) discovery.getCubeWriter(nullIndexLocation);
        } catch (Exception e) {
            BINonValueUtils.beyondControl(e);
        }
    }

    protected boolean isNullWriterAvailable() {
        return nullWriter != null;
    }

    protected boolean isNullReaderAvailable() {
        return nullReader != null;
    }

    protected boolean isIndexWriterAvailable() {
        return indexWriter != null;

    }

    protected boolean isIndexReaderAvailable() {
        return indexReader != null;
    }

    public ICubeGroupValueIndexReader getIndexReader() {
        if (!isIndexReaderAvailable()) {
            initIndexReader();
        }
        return indexReader;
    }

    public ICubeGroupValueIndexWriter getIndexWriter() {
        if (!isIndexWriterAvailable()) {
            initIndexWriter();
        }
        return indexWriter;
    }

    public ICubeGroupValueIndexReader getNullReader() {
        if (!isNullReaderAvailable()) {
            initNullReader();
        }
        return nullReader;
    }

    public ICubeGroupValueIndexWriter getNullWriter() {
        if (!isNullWriterAvailable()) {
            initNullWriter();
        }
        return nullWriter;
    }

    @Override
    public GroupValueIndex getBitmapIndex(int position) throws BICubeIndexException {
        try {
            return getIndexReader().getSpecificValue(position);
        } catch (BIResourceInvalidException e) {
            throw new BICubeIndexException(e.getMessage(), e);
        }

    }

    @Override
    public GroupValueIndex getNULLIndex(int position) throws BICubeIndexException {
        try {
            return getNullReader().getSpecificValue(position);
        } catch (BIResourceInvalidException e) {
            throw new BICubeIndexException(e.getMessage(), e);
        }
    }


    @Override
    public void addIndex(int position, GroupValueIndex groupValueIndex) {
        getIndexWriter().recordSpecificValue(position, groupValueIndex);
    }

    @Override
    public void addNULLIndex(int position, GroupValueIndex groupValueIndex) {
        getNullWriter().recordSpecificValue(position, groupValueIndex);
    }

    protected void resetIndexReader() {
        if (isIndexReaderAvailable()) {
            indexReader.clear();
            indexReader = null;
        }
    }

    protected void resetIndexWriter() {
        if (isIndexWriterAvailable()) {
            indexWriter.clear();
            indexWriter = null;
        }
    }

    protected void resetNullReader() {
        if (isNullReaderAvailable()) {
            nullReader.clear();
            nullReader = null;
        }
    }

    protected void resetNullWriter() {
        if (isNullWriterAvailable()) {
            nullWriter.clear();
            nullReader = null;
        }
    }

    @Override
    public void clear() {
        resetIndexReader();
        resetIndexWriter();
        resetNullReader();
        resetNullWriter();
    }

    @Override
    public boolean isEmpty() {
        return !getIndexReader().canRead();
    }

    @Override
    public ICubeResourceLocation getResourceLocation() {
        return currentLocation.copy();
    }
}
