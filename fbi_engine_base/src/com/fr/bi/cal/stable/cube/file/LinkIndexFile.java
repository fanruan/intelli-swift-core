package com.fr.bi.cal.stable.cube.file;

import com.fr.bi.cal.stable.index.file.GroupLengthFile;
import com.fr.bi.cal.stable.index.file.VersionFile;
import com.fr.bi.cal.stable.index.file.field.index.group.gzip.GroupValueIndexFile;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.stable.constant.CubeConstant;
import com.fr.bi.stable.file.IndexFile;
import com.fr.bi.stable.gvi.array.GroupValueIndexArrayReader;
import com.fr.bi.stable.gvi.array.ICubeTableIndexReader;
import com.fr.bi.stable.utils.file.BIFileUtils;
import com.fr.bi.stable.utils.file.BIPathUtils;
import com.fr.bi.stable.io.newio.NIOReader;
import com.fr.bi.stable.io.newio.NIOWriter;
import com.fr.bi.stable.io.newio.SingleUserNIOReadManager;

public class LinkIndexFile implements IndexFile {

    protected String path;

    protected GroupValueIndexFile index;

    protected GroupValueIndexFile nullIndex;

    protected VersionFile currentVersion;

    protected VersionFile IndexVersion;
    
    protected GroupLengthFile lengthFile;

    private GroupValueIndexArrayReader gviArrayReader;

    private Object gviArrayReaderLock = new Object();
    
    public LinkIndexFile(String path) {
        this.path = path;
    }

    @Override
    public void writeVersion(int version) {
        getCurrentVersionFile().write(version);
        getIndexVersionFile().write(CubeConstant.LINKEDINDEXVERSION);
    }

    protected VersionFile getCurrentVersionFile() {
        return BIFileUtils.createFile(this, "currentVersion", VersionFile.class, BIPathUtils.createVersionColumnPath(path));
    }
    
    protected GroupLengthFile getCurrentLengthFile(){
    	 return BIFileUtils.createFile(this, "lengthFile", GroupLengthFile.class, BIPathUtils.createGroupLengthPath(path));
    }

    private VersionFile getIndexVersionFile() {
        return BIFileUtils.createFile(this, "IndexVersion", VersionFile.class, BIPathUtils.createVersionColumnCubePath(path));
    }
    
    @Override
	public void writeGroupCount(long rowCount) {
    	getCurrentLengthFile().write(rowCount);
    }
    
	public long getGroupCount() {
    	return getGroupCount(null);
    }
    
    @Override
	public long getGroupCount(BIKey key) {
    	return Long.parseLong(getCurrentLengthFile().read());
    }

    @Override
    public ICubeTableIndexReader getGroupValueIndexArrayReader(SingleUserNIOReadManager manager) {
        if (gviArrayReader == null){
            synchronized (gviArrayReaderLock){
                if (gviArrayReader != null){
                    return gviArrayReader;
                }
                gviArrayReader = new GroupValueIndexArrayReader(createIndexReader(BIKey.DEFAULT, manager));
            }
        }
        return gviArrayReader;
    }

    @Override
    public void releaseGroupValueIndexCreator() {
        if (index != null) {
            index.clearWriter();
        }
        if (nullIndex != null) {
            nullIndex.clearWriter();
        }
    }

    protected GroupValueIndexFile createIndexFile() {
        return BIFileUtils.createFile(this, "index", GroupValueIndexFile.class, BIPathUtils.createSingleFieldIndexPath(path));
    }

    protected GroupValueIndexFile createNullIndexFile() {
        return BIFileUtils.createFile(this, "nullIndex", GroupValueIndexFile.class, BIPathUtils.createSingleFieldNullIndexPath(path));
    }

    @Override
    public boolean checkVersion(int relation_version) {
        return Integer.parseInt(getIndexVersionFile().read()) == CubeConstant.LINKEDINDEXVERSION
                && getVersion() == relation_version;
    }

    @Override
    public int getVersion() {
        return Integer.parseInt(getCurrentVersionFile().read());
    }

    @Override
    public NIOWriter<byte[]> createIndexWriter() {
        return createIndexFile().createNIOWriter();
    }

    @Override
    public NIOWriter<byte[]> createNullWriter() {
        return createNullIndexFile().createNIOWriter();
    }

    @Override
    public NIOReader<byte[]> createIndexReader(BIKey key,
                                               SingleUserNIOReadManager manager) {
        return createIndexFile().createNIOReader(manager);
    }

    @Override
    public NIOReader<byte[]> createNullIndexReader(BIKey key,
                                                   SingleUserNIOReadManager manager) {
        return createNullIndexFile().createNIOReader(manager);
    }

}