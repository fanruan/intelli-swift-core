package com.fr.bi.stable.file;

import com.fr.bi.base.key.BIKey;
import com.fr.bi.stable.gvi.array.ICubeTableIndexReader;
import com.fr.bi.stable.io.newio.NIOReader;
import com.fr.bi.stable.io.newio.NIOWriter;
import com.fr.bi.stable.io.newio.SingleUserNIOReadManager;

public interface IndexFile {

    void writeVersion(int version);

    void releaseGroupValueIndexCreator();

    boolean checkVersion(int relation_version);

    NIOWriter<byte[]> createIndexWriter();

    NIOWriter<byte[]> createNullWriter();

    int getVersion();

    NIOReader<byte[]> createIndexReader(BIKey key,
                                        SingleUserNIOReadManager manager);

    NIOReader<byte[]> createNullIndexReader(BIKey key,
                                            SingleUserNIOReadManager manager);

    void writeGroupCount(long groupCount);
    
    long getGroupCount(BIKey key);

    ICubeTableIndexReader getGroupValueIndexArrayReader(SingleUserNIOReadManager manager);
}