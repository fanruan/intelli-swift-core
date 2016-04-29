package com.finebi.cube.structure;

import com.finebi.cube.data.input.ICubeReader;
import com.finebi.cube.exception.BIResourceInvalidException;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by naleite on 16/3/18.
 */
public class BICubeFieldsNameFromReader implements ICubeFieldsNameGetterService{

    ICubeReader<String> reader;

    public BICubeFieldsNameFromReader(ICubeReader<String> reader) {
        this.reader = reader;
    }

    @Override
    public List<String> getColumnsNames() throws BIResourceInvalidException {
        int columnSize = Integer.parseInt(reader.getSpecificValue(0));
        List<String> columnString = new LinkedList<String>();
        for(int pos = 1; pos<columnSize; pos++){
            columnString.add(reader.getSpecificValue(pos));
        }
        return columnString;
    }
}
