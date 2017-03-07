package com.finebi.cube.provider;

import com.finebi.cube.data.disk.reader.BIByteArrayNIOReader;
import com.finebi.cube.data.disk.reader.BIGroupValueIndexNIOReader;
import com.finebi.cube.data.disk.reader.ReaderHandlerManager;
import com.finebi.cube.data.disk.reader.primitive.BIByteNIOReader;
import com.finebi.cube.data.disk.reader.primitive.BIIntegerNIOReader;
import com.finebi.cube.data.disk.reader.primitive.BILongNIOReader;
import com.finebi.cube.data.disk.writer.BIByteArrayNIOWriter;
import com.finebi.cube.data.disk.writer.BIGroupValueIndexNIOWriter;
import com.finebi.cube.data.disk.writer.WriterHandlerManager;
import com.finebi.cube.data.disk.writer.primitive.BIByteNIOWriter;
import com.finebi.cube.data.disk.writer.primitive.BIIntegerNIOWriter;
import com.finebi.cube.data.disk.writer.primitive.BILongNIOWriter;
import com.finebi.cube.data.input.primitive.ICubeByteReader;
import com.finebi.cube.data.input.primitive.ICubeIntegerReader;
import com.finebi.cube.data.input.primitive.ICubeLongReader;
import com.finebi.cube.data.output.primitive.ICubeByteWriter;
import com.finebi.cube.data.output.primitive.ICubeIntegerWriter;
import com.finebi.cube.data.output.primitive.ICubeLongWriter;

/**
 * Created by Lucifer on 2017-2-22.
 *
 * @author Lucifer
 * @since 4.0
 */
public class BIGroupValueIndexNIOProvider {

    public static BIGroupValueIndexNIOWriter BIGroupValueIndexNIOWriterProvider(String start, String length, String content) {
        ICubeLongWriter startPositionRecorder = new BILongNIOWriter(start);
        ICubeIntegerWriter lengthRecorder = new BIIntegerNIOWriter(length);
        ICubeByteWriter contentRecorder = new BIByteNIOWriter(content);
        startPositionRecorder.setHandlerReleaseHelper(new WriterHandlerManager(startPositionRecorder));
        lengthRecorder.setHandlerReleaseHelper(new WriterHandlerManager(lengthRecorder));
        contentRecorder.setHandlerReleaseHelper(new WriterHandlerManager(contentRecorder));
        return new BIGroupValueIndexNIOWriter(new BIByteArrayNIOWriter(startPositionRecorder, lengthRecorder, contentRecorder));
    }

    public static BIGroupValueIndexNIOReader BIGroupValueIndexNIOReaderProvider(String start, String length, String content) {
        ICubeLongReader startPositionRecorderReader = new BILongNIOReader(start);
        ICubeIntegerReader lengthRecorderReader = new BIIntegerNIOReader(length);
        ICubeByteReader contentRecorderReader = new BIByteNIOReader(content);
        startPositionRecorderReader.setHandlerReleaseHelper(new ReaderHandlerManager(startPositionRecorderReader));
        lengthRecorderReader.setHandlerReleaseHelper(new ReaderHandlerManager(lengthRecorderReader));
        contentRecorderReader.setHandlerReleaseHelper(new ReaderHandlerManager(contentRecorderReader));
        return new BIGroupValueIndexNIOReader(new BIByteArrayNIOReader(startPositionRecorderReader, lengthRecorderReader, contentRecorderReader));
    }
}
