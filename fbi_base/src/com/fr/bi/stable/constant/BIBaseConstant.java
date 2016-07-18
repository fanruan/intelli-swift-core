package com.fr.bi.stable.constant;

import com.fr.base.CoreDecimalFormat;
import com.fr.base.FRContext;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.bi.stable.operation.sort.comp.ChinesePinyinComparator;
import com.fr.cache.Status;
import com.fr.general.ComparatorUtils;
import com.fr.general.Inter;
import com.fr.stable.StableUtils;
import com.fr.stable.core.UUID;
import com.fr.stable.project.ProjectConstants;

import java.io.File;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.Comparator;

/**
 * BI静态值类
 *
 * @author Daniel
 */
public class BIBaseConstant {

    public final static String VERSION = "4.0";
    public final static String VERSIONTEXT = "version";
    public final static int SIZE500 = 500;
    public final static int SIZE1000 = 1000;
    public final static int BEST_COUNTS2_FETCH_TOGETHER = 1000;
    public final static int ROW_COUNT_BASE = 64;
    public final static int ROW_COUNT_OFFSET = 6;
    public final static int ROW_COUNT_BASE10000 = 10000;
    public final static int SIZE3 = 3;
    public final static int SIZE7 = 7;
    public final static int SIZE100 = 100;
    public final static int OFFSETOX1 = 0x1;
    public final static int OFFSETOX3F = 0x3F;
    public final static int OFFSET20 = 20;
    public final static int PREVIEW_COUNT = 50;
    public final static int MEMORY_PREVIEW_COUNT = 20;
    public static final int CLASSIC_BI = 0;
    public static final int HIHIDATA = 1;
    public static final int FINEX = 2;
    public static final DecimalFormat DEFAULTNUMBERFORMAT = new CoreDecimalFormat(new DecimalFormat("##.##"), "##.##");
    public static final String CONN_STRING = "__##__";
    public static final String GROUP_NO_GROUP = "__ungrouped__packages__";
    public static final String SERVERCONNECTNAME = Inter.getLocText("BI-Server_Data_Set");
    public static final String REPORT_ID = "reportId";

    //TODO 待确认 线程太多没用 改成cpu个数两倍
    public static final int X32_TOTAL_PAGESPLITROWSTEP = 24;
    public static final long X32_TOTAL_PAGESPLITROWCOUNT = 1l << X32_TOTAL_PAGESPLITROWSTEP;
    public static final int X64_PAGESPLITROWSTEP = 20;
    public static final int X64_TOPPAGESPLITROWSTEP = 5;
    public static final long X64_PAGESPLITROWCOUNT = 1l << X64_PAGESPLITROWSTEP;
    /**
     * 稀疏分组百分百线
     */
    public static final int RAREFACTIONGROUPLINE = 0xA;
    public static final int IDGROUPLINE = 0x3;
    public static final String NO_PRIVILEGE_NAME = "no_privilege";
    public static final String TOO_MANY_SUMARYS = "too_many_summarys";
    public static final String NO_REGISTERATION = "no_registeration";
    /**
     * 不显示全部时每个分组显示的数量
     */
    public static final int TEMPROWS = 8;
    public static final int LARGE_GROUP_LINE = 10000;
    public static final int SMALL_GROUP = 64;
    public static final int MIDDLE_GROUP = 256;
    //超过2kw 使用多线程
    public static final int MUTITHREADLESUMARYLINE = 20000000;
    public static final String TEMPMODELDESCRIPTION = "tempmodeldescription";
    public static final int GENERAL_QUERY_BUTTON = 9;
    public static final int PART_DATA_COUNT_LIMIT = 1000;
    public static final int PART_DATA_GROUP_LIMIT = 10;
    public static final int PART_DATA_GROUP_MAX_LIMIT = 100;
    public static String EMPTY_NODE_DATA = new String("");
    public static int BI_MODEL = CLASSIC_BI;
    public static boolean ISFINEEXCEL = false;
    public static int AVAILABLEPROCESSORS = Runtime.getRuntime().availableProcessors();
    //TODO 待确认 线程太多没用 改成cpu个数两倍
    public static int THREADPOOLSIZE = Math.max(1, AVAILABLEPROCESSORS) * 2;

    /**
     * 字符串相等判断
     *
     * @param s1 s字符串1
     * @param s2 字符串2
     * @return true或false
     */

    public static boolean stringEqualsIgnoreCase(String s1, String s2) {
        if (s1 == null || s2 == null) {
            return s1 == null && s2 == null;
        }
        return s1.equalsIgnoreCase(s2);
    }

    public static class MEMORY {

        public static final double RELEASE_PERCENT_LINE = (double) 2 / 3;

        public static final int RELEASE_LEFT_VALUE_LINE = 1 << 7;

        public static final int RELEASE_SCHEDULE = 60 * 60 * 1000;

        public static final int NONE_USE_LINE = 0x3;

        public static final int RELEASE_TO_DISK_LINE = 0x3;

        public static final int RELEASE_TO_DISK_TIMECHECK = 60 * 60 * 1000;

    }

    /**
     * 排序控制的方向
     *
     * @author Daniel
     */
    public static final class SORTDIRECTION {

        public static final int X = 0x0;

        public static final int Y = 0x1;
    }

    /**
     * 数据处理方式
     *
     * @author frank
     */
    public static final class DATA {
        public static final int ABC = -1;

        public static final int SORT = 0;

        public static final int GROUP = 1;

        public static final int CALCULATOR = 2;

        public static final int OCUPIED = 3;
    }

    public static final class JOINTYPE {
        public static final int LEFT = 0x1;

        public static final int RIGHT = 0x2;

        public static final int INNER = 0x3;

        public static final int OUTER = 0x4;
    }

    /**
     * 单元格类型
     *
     * @author Daniel
     */
    public static final class DATAFIELD {
        public static final int ROW = 0;

        public static final int COLUMN = 1;

        public static final int SUMARY = 2;
    }

    public static final class TABLEEXCEL {
        public final static String EXCELPATH = File.separator + ProjectConstants.RESOURCES_NAME + File.separator + "bi_excel";
    }

    public static final class EXCELDATA {
        public final static String EXCEL_DATA_PATH = File.separator + ProjectConstants.RESOURCES_NAME + File.separator + "excel_data";
    }

    public static final class UPLOAD_IMAGE {
        public final static String IMAGE_PATH = File.separator + ProjectConstants.RESOURCES_NAME + File.separator + "images";
    }

    public static final class CUBEINDEX {


    }

    public static final class TABLETYPE {

        public static final int DB = 1;

        public static final int ETL = 2;

        public static final int SQL = 3;

        public static final int EXCEL = 4;

        public static final int SERVER = 5;

    }

    public static final class COMPARATOR {

        public final static class COMPARABLE {

            public final static Comparator ASC = new N();

            public final static Comparator DESC = new D();

            private static class N implements Comparator<Comparable>, Serializable {

                @Override
                public int compare(Comparable o1, Comparable o2) {
                    if (o1 == o2 || (o1 == null && o2 == null)) {
                        return 0;
                    }
                    if (o1 == null) {
                        return -1;
                    }
                    if (o2 == null) {
                        return 1;
                    }
                    return o1.compareTo(o2);
                }

            }

            private static class D implements Comparator<Comparable>, Serializable {

                @Override
                public int compare(Comparable o1, Comparable o2) {
                    if (o1 == o2 || (o1 == null && o2 == null)) {
                        return 0;
                    }
                    if (o1 == null) {
                        return 1;
                    }
                    if (o2 == null) {
                        return -1;
                    }
                    return o2.compareTo(o1);
                }

            }
        }

        public final static class STRING {

            //			private static final Comparator china = Collator.getInstance(java.util.Locale.CHINA);

            public static final Comparator<String> ASC_STRING_CC = new ChinesePinyinComparator();

            public static final Comparator<String> DESC_STRING_CC = new D_ChinesePinyinComparator();

            private static class D_ChinesePinyinComparator extends ChinesePinyinComparator {

                /**
                 *
                 */
                private static final long serialVersionUID = 550953059460281816L;

                @Override
                public int compare(String o1, String o2) {
                    String str1 = o2;
                    String str2 = o1;
                    return super.compare(str1, str2);
                }
            }

        }
    }

    /**
     * 时间分类
     *
     * @author frank
     */
    public static final class DATE {

        public static final String FIELDNAME = "__current_time_is_now__";

        public static final int YEAR = 0;

        public static final int SEASON = 1;

        public static final int MONTH = 2;

        public static final int WEEK = 3;

        public static final int DAY = 4;
    }

    public static class CACHE {
        private static File cache;
        private static Thread shutdownHook;
        private static Status status;
        private static File tcache;
        private static int checkCount = 0;
        private static Thread deleteTempCache;

        public final static File getCacheDirectory() {
            if (cache == null) {
                cache = new File(FRContext.getCurrentEnv().getPath()
                        + File.separator + ProjectConstants.RESOURCES_NAME + File.separator + "cache");
                try {
                    StableUtils.mkdirs(cache);
                } catch (Exception e) {
                    throw new RuntimeException("Can't create or access the directory: " + cache + ", please check your Permissions.", e);
                }

                status = Status.STATUS_UNINITIALISED;
                status = Status.STATUS_ALIVE;
                addShutdownHookIfRequired();
            }
            return cache;
        }

        public final static File getTempCacheDirectory() {
            if (tcache == null) {
                tcache = new File(FRContext.getCurrentEnv().getPath()
                        + File.separator + ProjectConstants.RESOURCES_NAME + File.separator + "tcache");
                try {
                    StableUtils.mkdirs(tcache);
                } catch (Exception e) {
                    throw new RuntimeException("Can't create or access the directory: " + cache + ", please check your Permissions.", e);
                }
            }
            return tcache;
        }

        public static void removeAndDeleteCache() {
            File f = getCacheDirectory();
            f.renameTo(new File(getTempCacheDirectory(), UUID.randomUUID().toString()));
            StableUtils.mkdirs(cache);
            checkCount++;
            deleteTempCache();
            synchronized (deleteTempCache) {
                deleteTempCache.notify();
            }
        }

        static void deleteTempCache() {
            if (deleteTempCache == null) {
                deleteTempCache = new Thread() {
                    @Override
                    public void run() {
                        while (true) {
                            synchronized (this) {
                                if (checkCount > 0) {
                                    checkCount--;
                                } else {
                                    try {
                                        this.wait();
                                    } catch (InterruptedException e) {
                                        BILogger.getLogger().error(e.getMessage(), e);
                                    }
                                }
                                if (getTempCacheDirectory().isDirectory()) {
                                    File[] files = getTempCacheDirectory().listFiles();
                                    for (int i = 0; i < files.length; i++) {
                                        StableUtils.deleteFile(files[i]);
                                    }
                                }
                            }
                        }
                    }

                };
                deleteTempCache.start();
            }
        }

        public final static void deleteCacheDirectory() {

            StableUtils.deleteFile(new File(FRContext.getCurrentEnv().getPath()
                    + File.separator + ProjectConstants.RESOURCES_NAME + File.separator + "cache"));

        }

        /**
         * Some caches might be xml, so we want to add a shutdown hook if
         * that is the case, so that the data and index can be written to disk.
         */
        private final static void addShutdownHookIfRequired() {
            boolean enabled = true;
            if (!enabled) {
                return;
            } else {
                // FRLogger.getLogger().log(Level.INFO,
                // "The CacheManager shutdown hook is enabled because " +
                // "com.fr.cache.enableShutdownHook" + " is set to true.");

                Thread localShutdownHook = new Thread() {
                    @Override
                    public void run() {
                        synchronized (this) {
                            if (ComparatorUtils.equals(status, Status.STATUS_ALIVE)) {
                                // clear shutdown hook reference to prevent
                                // removeShutdownHook to remove it during shutdown
                                shutdownHook = null;

                                shutdown();
                            }
                        }
                    }
                };

                Runtime.getRuntime().addShutdownHook(localShutdownHook);
                shutdownHook = localShutdownHook;
            }
        }

        /**
         * Remove the shutdown hook to prevent leaving orphaned CacheManagers
         * around. This is called by {@link #shutdown()} AFTER the status has been
         * set to shutdown.
         */
        private final static void removeShutdownHook() {
            if (shutdownHook != null) {
                // remove shutdown hook
                try {
                    Runtime.getRuntime().removeShutdownHook(shutdownHook);
                } catch (IllegalStateException e) {
                    // This will be thrown if the VM is shutting down. In this case
                    // we do not need to worry about leaving references to
                    // CacheManagers lying
                    // around and the call is ok to fail.
                    BILogger.getLogger().error(
                            "IllegalStateException due to attempt to remove a shutdown" + "hook while the VM is actually shutting down.", e);
                }
                shutdownHook = null;
            }
        }

        /**
         * 服务器退出时执行的一些方法。
         */
        public final static void shutdown() {
            synchronized (CACHE.class) {
                if (ComparatorUtils.equals(status, Status.STATUS_SHUTDOWN)) {
                    return;
                }

                StableUtils.deleteFile(new File(FRContext.getCurrentEnv().getPath()
                        + File.separator + ProjectConstants.RESOURCES_NAME + File.separator + "cache"));
                status = Status.STATUS_SHUTDOWN;
                removeShutdownHook();
            }
        }
    }

    public static final class STATUS {
        public static final byte UNLOAD = 0;
        public static final byte LOADING = 1;
        public static final byte LOADED = 2;
//		public static final byte UPDATING = 3;
    }

    public static final class DATEKEY {
        public static final int YEARINDEX = 0;
        public static final int SEASONINDEX = 1;
        public static final int MONTHINDEX = 2;
        public static final int WEEKINDEX = 3;
        public static final int DAYINDEX = 4;
        public static final int NULLINDEX = 0;
        public static final int SAMEINDEX = 1;
        public static final int DIFFERINDEX = 2;

    }

    public static final class ROLE_TYPE {
        public static final int COMPANY = 1;
        public static final int CUSTOM = 2;
    }
}