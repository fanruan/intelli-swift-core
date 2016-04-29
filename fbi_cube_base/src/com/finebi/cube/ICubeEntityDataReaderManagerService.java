package com.finebi.cube;

import com.finebi.cube.data.input.primitive.ICubePrimitiveReader;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.exception.BIKeyAbsentException;
import com.fr.bi.exception.BIKeyDuplicateException;

/**
 * This class created on 2016/3/3.
 *
 * @author Connery
 * @since 4.0
 */
public interface ICubeEntityDataReaderManagerService {

    ICubePrimitiveReader getCubeReader(BIKey readerKey) throws BIKeyAbsentException;

    void registerCubeReader(BIKey readerKey, ICubePrimitiveReader cubeReader) throws BIKeyDuplicateException;

    void removeCubeReader(BIKey readerKey)throws BIKeyAbsentException;
}
