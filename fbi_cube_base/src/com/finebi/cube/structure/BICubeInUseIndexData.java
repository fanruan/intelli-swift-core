package com.finebi.cube.structure;

import com.finebi.cube.data.input.ICubeReader;
import com.finebi.cube.data.output.ICubeGroupValueIndexWriter;
import com.finebi.cube.data.output.ICubeWriter;
import com.finebi.cube.exception.BIResourceInvalidException;
import com.finebi.cube.data.input.ICubeGroupValueIndexReader;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.utils.code.BILogger;

/**
 * Created by naleite on 16/3/17.
 */
public class BICubeInUseIndexData implements ICubeInUseIndexDataService {

    private ICubeGroupValueIndexReader indexReader;
    private ICubeGroupValueIndexWriter indexWriter;
    private ICubeWriter<String> versionWriter;
    private ICubeReader<String> versionReader;
    private int version = 0;

    @Override
    public void addIndex(int position, GroupValueIndex groupValueIndex) {
        indexWriter.recordSpecificValue(position,groupValueIndex);

    }

    @Override
    public void addNULLIndex(int position, GroupValueIndex groupValueIndex) {
        indexWriter.recordSpecificValue(position,groupValueIndex);
    }

    @Override
    public void addVersion(int version) {

        this.version = version;
        versionWriter.recordSpecificValue(0,String.valueOf(version));
    }

    @Override
    public GroupValueIndex getIndex(int position) {
        try {
            return indexReader.getSpecificValue(position);
        } catch (BIResourceInvalidException e) {
            BILogger.getLogger().error(e.getMessage(),e);
        }
        return null;
    }

    @Override
    public GroupValueIndex getNULLIndex(int position) {
        try {
            return indexReader.getSpecificValue(position);
        } catch (BIResourceInvalidException e) {
            BILogger.getLogger().error(e.getMessage(),e);
        }
        return null;
    }

    @Override
    public int getVersion() {
        try {
            this.version = Integer.parseInt(versionReader.getSpecificValue(0));
        } catch (BIResourceInvalidException e) {
            BILogger.getLogger().error(e.getMessage(),e);
        }
        return this.version;
    }
}
