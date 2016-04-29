package com.finebi.cube.data.disk.reader;

import com.finebi.cube.BICubeFileConstant;
import com.finebi.cube.data.ICubePrimitiveResourceDiscovery;
import com.finebi.cube.data.disk.BICubeDiskPrimitiveDiscovery;
import com.finebi.cube.data.input.ICubeByteArrayReader;
import com.finebi.cube.data.input.ICubeByteArrayReaderBuilder;
import com.finebi.cube.data.input.primitive.ICubeByteReader;
import com.finebi.cube.data.input.primitive.ICubeIntegerReader;
import com.finebi.cube.data.input.primitive.ICubeLongReader;
import com.finebi.cube.location.ICubeResourceLocation;

import java.io.File;

/**
 * This class created on 2016/3/11.
 *
 * @author Connery
 * @since 4.0
 */
public class BIByteArrayNIOReaderBuilder extends BINIOReaderBuilder<ICubeByteArrayReader> implements ICubeByteArrayReaderBuilder {


    @Override
    protected String getFragmentTag() {
        return this.FRAGMENT_TAG;
    }

    @Override
    protected ICubeByteArrayReader createNIOReader(File target, ICubeResourceLocation targetLocation) {
        try {
            ICubePrimitiveResourceDiscovery cubeDiscovery = BICubeDiskPrimitiveDiscovery.getInstance();
            /**
             * 获得内容部分的Byte类型Writer
             */
            ICubeResourceLocation contentLocation = targetLocation.copy();
            contentLocation.setReaderSourceLocation();
            contentLocation.setByteType();
            ICubeByteReader contentReader = (ICubeByteReader) cubeDiscovery.getCubeReader(contentLocation);
            /**
             * 获得位置部分的Long类型Writer
             */
            ICubeResourceLocation startPositionPath = targetLocation.generateWithSuffix(BICubeFileConstant.arrayPositionSuffix);
            startPositionPath.setReaderSourceLocation();
            startPositionPath.setLongType();
            ICubeLongReader positionReader = (ICubeLongReader) cubeDiscovery.getCubeReader(startPositionPath);
            /**
             * 获得长度部分的Integer类型Writer
             */
            ICubeResourceLocation lengthPath = targetLocation.generateWithSuffix(BICubeFileConstant.arrayLengthSuffix);
            lengthPath.setReaderSourceLocation();
            lengthPath.setIntegerType();
            ICubeIntegerReader lengthReader = (ICubeIntegerReader) cubeDiscovery.getCubeReader(lengthPath);

            return new BIByteArrayNIOReader(positionReader, lengthReader, contentReader);
        } catch (Exception ignore) {
            throw new RuntimeException(ignore.getMessage(), ignore);
        }
    }


}