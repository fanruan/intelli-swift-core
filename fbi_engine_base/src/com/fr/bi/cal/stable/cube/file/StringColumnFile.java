package com.fr.bi.cal.stable.cube.file;

import com.finebi.cube.api.ICubeColumnDetailGetter;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.cal.stable.index.GroupIndexCreator;
import com.fr.bi.cal.stable.index.file.VersionFile;
import com.fr.bi.cal.stable.index.file.field.CubeIntegerFile;
import com.fr.bi.cal.stable.index.file.field.CubeStringFile;
import com.fr.bi.cal.stable.tableindex.detailgetter.NormalDetailGetter;
import com.fr.bi.stable.file.ColumnFile;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.io.io.read.StringIndexReadMappedList;
import com.fr.bi.stable.io.newio.NIOReader;
import com.fr.bi.stable.io.newio.SingleUserNIOReadManager;
import com.fr.bi.stable.io.sortlist.ISortNIOReadList;
import com.fr.bi.stable.io.sortlist.SortNIOReadStringList;
import com.fr.bi.stable.operation.sort.comp.ComparatorFacotry;
import com.fr.bi.stable.utils.file.BIFileUtils;
import com.fr.bi.stable.utils.file.BIPathUtils;

import java.io.File;
import java.util.Comparator;

public class StringColumnFile extends AbstractSingleColumnFile<String> {
	
	private CubeIntegerFile stringIndexGroup;
	
	@SuppressWarnings("unused")
	private VersionFile stringIndexGroupVersion;
	
	private Boolean hasDetailFile;
	
    public StringColumnFile(String path) {
        super(path);
    }

    @Override
    protected CubeStringFile createDetailFile() {
        return BIFileUtils.createFile(this, getDetailFieldName(), CubeStringFile.class, BIPathUtils.createSingleFieldDetailPath(path));
    }
    
    private VersionFile getStringIndexVersionFile() {
        return BIFileUtils.createFile(this, "stringIndexGroupVersion", VersionFile.class, BIPathUtils.createVersionColumnCubePath(BIPathUtils.createSingleFieldDetailPath(path)));
    }

    @Override
    protected CubeStringFile createGroupFile() {
        return BIFileUtils.createFile(this, getGroupFieldName(), CubeStringFile.class, BIPathUtils.createSingleFieldGroupPath(path));
    }

    private CubeIntegerFile createIndexGroupFile() {
    	return BIFileUtils.createFile(this, "stringIndexGroup", CubeIntegerFile.class, BIPathUtils.createCorrespondPath(path));
    }
    
	@Override
	protected void dealWithCreator(GroupIndexCreator creator){
		creator.setStringGroupWriter(createIndexGroupFile().createNIOWriter());
		creator.setStringGroupVersion(getStringIndexVersionFile());
	}

	public NIOReader<Integer> createStringGroupIndexReader(SingleUserNIOReadManager manager) {
		return createIndexGroupFile().createNIOReader(manager);
	}
	
	
	@Override
	public void releaseGroupValueIndexCreator() {
		super.releaseGroupValueIndexCreator();
		if(stringIndexGroup != null){
			stringIndexGroup.clearWriter();
		}
		
	}
	
	private boolean hasDetailFile() {
		if(hasDetailFile == null){
			hasDetailFile = createDetailFile().exists();
		}
		return hasDetailFile;
    }

	
	@Override
	public NIOReader<String> createDetailReader(SingleUserNIOReadManager manager){
		if(this.hasDetailFile()){
			return super.createDetailReader(manager);
		} else {
		return new StringIndexReadMappedList(createStringGroupIndexReader(manager), createGroupReader(manager));
		}
	}
    
    @Override
    protected Comparator<String> getComparator() {
        return ComparatorFacotry.CHINESE_ASC;
    }

    
	

    @Override
    public ISortNIOReadList<String> createSortGroupReader(BIKey key,
                                                          SingleUserNIOReadManager manager) {
        return new SortNIOReadStringList(createGroupReader(manager), getGroupCount(key));
    }

    @Override
    public ICubeColumnDetailGetter createDetailGetter(SingleUserNIOReadManager manager) {

        return new NormalDetailGetter(createDetailReader(manager));
    }

    @Override
    public void copyDetailValue(String path,  ColumnFile columnFile, SingleUserNIOReadManager manager, long rowCount) {
        File newFile = new File(path);
        File oldFile = new File(this.path);
        String field = BIPathUtils.createSingleFieldDetailPath(this.path);
        //pony 先复制string的三个文件
        BIFileUtils.copyFile(field, oldFile, newFile);
        BIFileUtils.copyFile(BIPathUtils.createIndexPath(field), oldFile, newFile);
        BIFileUtils.copyFile(BIPathUtils.createSizePath(field), oldFile, newFile);
        //pony 还要移动下writer的位置
        createDetailFile().createNIOWriter().setPos(((StringColumnFile) columnFile).createDetailReader(manager).getLastPos(rowCount));
    }

    @Override
    public GroupValueIndex getIndexByRow(int row, SingleUserNIOReadManager manager) {
        int groupIndex = createStringGroupIndexReader(manager).get(row);
        return getGroupValueIndexArrayReader(manager).get(groupIndex);
    }

}