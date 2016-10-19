package com.finebi.cube.data.disk.reader.primitive;

import com.finebi.cube.data.disk.reader.BIBasicNIOReaderBuilder;
import com.finebi.cube.data.input.primitive.ICubePrimitiveReader;
import com.fr.bi.stable.io.newio.NIOConstant;

import java.io.File;

/**
 * This class created on 2016/3/16.
 *
 * @author Connery
 * @since 4.0
 */
public abstract class BIPrimitiveNIOReaderBuilder<T extends ICubePrimitiveReader> extends BIBasicNIOReaderBuilder<T> {
    boolean isSingleFile(File file){
        return file!= null && file.exists() && file.length() < NIOConstant.SINGLE_FILE_LENGTH && file.length() > 0;
    }
}
