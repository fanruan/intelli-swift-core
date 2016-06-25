package com.fr.bi.cal.stable.cube.file;

import com.finebi.cube.api.ICubeColumnIndexReader;
import com.finebi.cube.api.ICubeColumnDetailGetter;
import com.finebi.cube.relation.BITableSourceRelation;
import com.fr.bi.base.ValueConverter;
import com.fr.bi.base.ValueConverterFactory;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.cal.stable.index.file.field.AbstractNIOCubeFile;
import com.fr.bi.cal.stable.tableindex.detailgetter.NormalDetailGetter;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.constant.DateConstant;
import com.fr.bi.stable.data.key.date.BIDayValue;
import com.fr.bi.stable.engine.index.key.IndexKey;
import com.fr.bi.stable.engine.index.key.IndexTypeKey;
import com.fr.bi.stable.file.ColumnFile;
import com.fr.bi.stable.file.IndexFile;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.gvi.array.GroupValueIndexArrayReader;
import com.fr.bi.stable.gvi.array.ICubeTableIndexReader;
import com.fr.bi.stable.index.CubeGenerator;
import com.fr.bi.stable.io.newio.NIOReader;
import com.fr.bi.stable.io.newio.NIOWriter;
import com.fr.bi.stable.io.newio.SingleUserNIOReadManager;
import com.fr.bi.stable.io.sortlist.ISortNIOReadList;
import com.fr.bi.stable.utils.file.BIFileUtils;
import com.fr.bi.stable.utils.file.BIPathUtils;

import java.util.ArrayList;
import java.util.List;

public class DateColumnFile implements ColumnFile<Long> {

    private String path;

    private LongColumnFile base;

    private IntegerColumnFile year;

    private IntegerColumnFile season;

    private IntegerColumnFile month;

    private IntegerColumnFile day;

    private IntegerColumnFile week;

    private LongColumnFile ymd;

    private GroupValueIndexArrayReader gviArrayReader;

    private Object gviArrayReaderLock = new Object();

    public DateColumnFile(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public LongColumnFile createBaseFile() {
        return BIFileUtils.createFile(this, "base", LongColumnFile.class, BIPathUtils.createSingleFieldDetailPath(path));
    }

    public IntegerColumnFile createYearGroupFile() {
        return BIFileUtils.createFile(this, "year", IntegerColumnFile.class, BIPathUtils.createSingleFieldGroupPath(path + "_year"));
    }

    public IntegerColumnFile createSeasonGroupFile() {
        return BIFileUtils.createFile(this, "season", IntegerColumnFile.class, BIPathUtils.createSingleFieldDetailPath(path + "_season"));
    }

    public IntegerColumnFile createMonthGroupFile() {
        return BIFileUtils.createFile(this, "month", IntegerColumnFile.class, BIPathUtils.createSingleFieldDetailPath(path + "_month"));
    }

    public IntegerColumnFile createDayGroupFile() {
        return BIFileUtils.createFile(this, "day", IntegerColumnFile.class, BIPathUtils.createSingleFieldDetailPath(path + "_day"));
    }

    public IntegerColumnFile createWeekGroupFile() {
        return BIFileUtils.createFile(this, "week", IntegerColumnFile.class, BIPathUtils.createSingleFieldDetailPath(path + "_week"));
    }

    public LongColumnFile createYMDGroupFile() {
        return BIFileUtils.createFile(this, "ymd", LongColumnFile.class, BIPathUtils.createSingleFieldDetailPath(path + "_ymd"));
    }

    @Override
    public void createDetailDataWriter() {
        createBaseFile().createDetailDataWriter();
    }


    @Override
    public void releaseDetailDataWriter() {
        if (base != null) {
            base.releaseDetailDataWriter();
        }
    }

    @Override
    public void addDataValue(int row, Long value) {
        base.addDataValue(row, value);
    }

    @Override
    public void writeVersion(int version) {
        if (base != null) {
            base.writeVersion(version);
        }
        if (year != null) {
            year.writeVersion(version);
        }
        if (season != null) {
            season.writeVersion(version);
        }
        if (month != null) {
            month.writeVersion(version);
        }
        if (day != null) {
            day.writeVersion(version);
        }
        if (week != null) {
            week.writeVersion(version);
        }
        if (ymd != null){
            ymd.writeVersion(version);
        }
    }

    @Override
    public void releaseGroupValueIndexCreator() {
        if (base != null) {
            base.releaseGroupValueIndexCreator();
        }
        if (year != null) {
            year.releaseGroupValueIndexCreator();
        }
        if (season != null) {
            season.releaseGroupValueIndexCreator();
        }
        if (month != null) {
            month.releaseGroupValueIndexCreator();
        }
        if (day != null) {
            day.releaseGroupValueIndexCreator();
        }
        if (week != null) {
            week.releaseGroupValueIndexCreator();
        }
        if (ymd != null) {
            ymd.releaseGroupValueIndexCreator();
        }
    }

    @Override
    public CubeGenerator createGroupIndexCreator(BIKey key,
                                                       ValueConverter vc,
                                                       int version, long rowCount) {
        AbstractNIOCubeFile<Long> detail = createBaseFile().createDetailFile();
        SingleUserNIOReadManager manager = new SingleUserNIOReadManager(-1L);
        NIOReader sml = detail.createNIOReader(manager);
        return createGroupIndexCreator(manager, sml, key, vc, version, rowCount, true);
    }

    @Override
    public CubeGenerator createGroupIndexCreator(SingleUserNIOReadManager manager, NIOReader sml,BIKey key, ValueConverter vc, int version, long rowCount, boolean needRelease) {
        if (key instanceof IndexTypeKey) {
            switch (((IndexTypeKey) key).getType()) {
                case BIReportConstant.GROUP.Y: {
                    return createYearGroupFile().createGroupIndexCreator(manager, sml, new IndexKey(key.getKey() + "_year"),
                            ValueConverterFactory.createDateValueConverter(DateConstant.DATE.YEAR),
                            version, rowCount, needRelease);
                }
                case BIReportConstant.GROUP.M: {
                    return createMonthGroupFile().createGroupIndexCreator(manager, sml, new IndexKey(key.getKey() + "_month"),
                            ValueConverterFactory.createDateValueConverter(DateConstant.DATE.MONTH),
                            version, rowCount, needRelease);
                }
                case BIReportConstant.GROUP.S: {
                    return createSeasonGroupFile().createGroupIndexCreator(manager, sml, new IndexKey(key.getKey() + "_season"),
                            ValueConverterFactory.createDateValueConverter(DateConstant.DATE.SEASON),
                            version, rowCount, needRelease);
                }
                case BIReportConstant.GROUP.W: {
                    return createWeekGroupFile().createGroupIndexCreator(manager, sml, new IndexKey(key.getKey() + "_week"),
                            ValueConverterFactory.createDateValueConverter(DateConstant.DATE.WEEK),
                            version, rowCount, needRelease);
                }
                case BIReportConstant.GROUP.MD: {
                    return createDayGroupFile().createGroupIndexCreator(manager, sml, new IndexKey(key.getKey() + "_day"),
                            ValueConverterFactory.createDateValueConverter(DateConstant.DATE.DAY),
                            version, rowCount, needRelease);
                }
                case BIReportConstant.GROUP.YMD: {
                    return createYMDGroupFile().createGroupIndexCreator(manager, sml, new IndexKey(key.getKey() + "_ymd"),
                            ValueConverterFactory.createDateValueConverter(DateConstant.DATE.YMD),
                            version, rowCount, needRelease);
                }
                default: {
                    return createBaseFile().createGroupIndexCreator(manager, sml, key, vc, version, rowCount, needRelease);
                }
            }
        } else {
            return createBaseFile().createGroupIndexCreator(manager, sml, key, vc, version, rowCount, needRelease);
        }
    }

    @Override
    public boolean checkVersion(int version) {
        return createBaseFile().checkVersion(version)
                && createYearGroupFile().checkVersion(version)
                && createMonthGroupFile().checkVersion(version)
                && createSeasonGroupFile().checkVersion(version)
                && createDayGroupFile().checkVersion(version)
                && createWeekGroupFile().checkVersion(version)
                && createYMDGroupFile().checkVersion(version);
    }

    @Override
    public IndexFile getLinkIndexFile(BIKey key, List<BITableSourceRelation> relations) {
        ColumnFile cf = getColumnFile(key);
        return cf != null ? cf.getLinkIndexFile(key, relations) : null;
    }

    @Override
    public long getGroupCount(BIKey key) {
        ColumnFile cf = getColumnFile(key);
        return cf != null ? cf.getGroupCount(key) : 0;
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
    public int getVersion() {
        return createBaseFile().getVersion();
    }

    @Override
    public ISortNIOReadList createSortGroupReader(BIKey key, SingleUserNIOReadManager manager) {
        ColumnFile cf = getColumnFile(key);
        return cf != null ? cf.createSortGroupReader(key, manager) : null;
    }

    private NIOReader<Long> createDetailReader(SingleUserNIOReadManager manager) {
        return createBaseFile().createDetailReader(manager);
    }

    @Override
    public NIOReader<byte[]> createIndexReader(BIKey key, SingleUserNIOReadManager manager) {
        ColumnFile cf = getColumnFile(key);
        return cf != null ? cf.createIndexReader(key, manager) : null;
    }

    @Override
    public NIOReader<byte[]> createNullIndexReader(BIKey key, SingleUserNIOReadManager manager) {
        ColumnFile cf = getColumnFile(key);
        return cf != null ? cf.createNullIndexReader(key, manager) : null;
    }


    private ColumnFile getColumnFile(BIKey key) {
        ColumnFile cf = null;
        if (key instanceof IndexTypeKey) {
            switch (((IndexTypeKey) key).getType()) {
                case BIReportConstant.GROUP.Y:
                    cf = createYearGroupFile();
                    break;
                case BIReportConstant.GROUP.M:
                    cf = createMonthGroupFile();
                    break;
                case BIReportConstant.GROUP.S:
                    cf = createSeasonGroupFile();
                    break;
                case BIReportConstant.GROUP.W:
                    cf = createWeekGroupFile();
                    break;
                case BIReportConstant.GROUP.MD:
                    cf = createDayGroupFile();
                    break;
                case BIReportConstant.GROUP.YMD:
                    cf = createYMDGroupFile();
                    break;
                case BIReportConstant.GROUP.YMDHMS:
                    cf = createBaseFile();
                    break;
                default:
                    return null;
            }
        } else {
            cf = createBaseFile();
        }
        return cf;
    }

    @Override
    public ICubeColumnDetailGetter createDetailGetter(SingleUserNIOReadManager manager) {
        return new NormalDetailGetter(createDetailReader(manager));
    }

    /**
     * 下面五个用不上
     */
    @Override
    public void writeGroupCount(long groupCount) {
    }

    @Override
    public NIOWriter<byte[]> createIndexWriter() {
        return null;
    }

    @Override
    public NIOWriter<byte[]> createNullWriter() {
        return null;
    }

    @Override
    public NIOWriter<Long> createGroupWriter() {
        return null;
    }

    @Override
    public void deteleDetailFile() {
        createBaseFile().deteleDetailFile();
    }

    @Override
    public void copyDetailValue(String path, ColumnFile columnFile, SingleUserNIOReadManager manager, long rowCount) {
        createBaseFile().copyDetailValue(path, columnFile, manager, rowCount);
    }

    @Override
    public GroupValueIndex getIndexByRow(int row, SingleUserNIOReadManager manager) {
        long v = (Long)createDetailGetter(manager).getValue(row);
        ICubeColumnIndexReader map = createGroupByType(new IndexTypeKey(BIKey.DEFAULT.getKey(), BIReportConstant.GROUP.YMD), new ArrayList<BITableSourceRelation>(), manager);
        return map.getGroupIndex(new Long[]{new BIDayValue(v).getValue()})[0];
    }

    @Override
    public ICubeColumnIndexReader createGroupByType(BIKey key, List<BITableSourceRelation> relationList, SingleUserNIOReadManager manager) {
        return getColumnFile(key).createGroupByType(key, relationList, manager);
    }

    @Override
    public NIOReader createDetailNIOReader(SingleUserNIOReadManager manager) {
        return createBaseFile().createDetailNIOReader(manager);
    }
}