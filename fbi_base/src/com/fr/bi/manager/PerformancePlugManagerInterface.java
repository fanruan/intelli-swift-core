package com.fr.bi.manager;

import java.util.Map;

/**
 * Created by Hiram on 2015/3/18.
 */
public interface PerformancePlugManagerInterface {

    boolean controlTimeout();

    boolean isTimeout(long time);

    String getTimeoutMessage();

    void mark(String localAddr, long startTime);

    boolean controlUniqueThread();

    void checkExit();

    Map<String, String> getConfigByType(String paramType);

    boolean isReturnEmptyIndex();

    boolean isSearchPinYin();

    boolean isDiskSort();

    /**
     * dump的阀值，超过阀值后数据导出到硬盘
     *
     * @return
     */
    long getDiskSortDumpThreshold();

    boolean useStandardOutError();

    boolean verboseLog();

    boolean useLog4JPropertiesFile();


    String BIServerJarLocation();

    int getDeployModeSelectSize();

    void setDeployModeSelectSize(int size);

    void setThreadPoolSize(int size);

    int getThreadPoolSize();

    void setBackupWhenStart(boolean backupWhenStart);

    boolean isBackupWhenStart();

    void setBiTransportThreadPoolSize(int size);

    int getBiTransportThreadPoolSize();

    boolean updateParam(Map<String, String> resultMap);

    void saveDefaultConfig();

    Map<String, String> getExtraParam(String paramType);

    boolean isBelongTo(Map<String, String> resultMap);

    void printSystemParameters();

    /**
     * 是否高并发模式
     *
     * @return
     */
    boolean isExtremeConcurrency();

    /**
     * 内存索引的行数
     * 小鱼这个行数在算维度关系的时候会根据上层索引直接生成索引，而不是从磁盘再读一遍。
     * 如果是pcie的ssd，ddr4，加单文件，加i7-6700k单线程能力的这种cpu，这个参数影响不大
     * 磁盘慢的这个值就设大点，默认1<<12。
     * 星巴克哪种读硬盘很慢的情况4000000比较合适
     * 设的太大会比较占内存，最好不要超过一亿
     *
     * @return
     */
    int getReIndexRowCount();

    void setMinCubeFreeHDSpaceRate(double rate);

    double getMinCubeFreeHDSpaceRate();

    long getCubeReaderReleaseSleepTime();


    /**
     * 是否利用堆外内存来生成cube
     * 主要解决星巴克频繁gc的问题，这个参数为true的时候用完cube也要释放reader，并且写文件也要force
     * @return
     */
    boolean isDirectGenerating();

    /**
     * 写文件的时候是否强制同步
     * @return
     */
    boolean isForceWriter();

    /**
     * cube文件的大小
     * 返回4m左移的位置，最小4，最大8，4就是64M，8就是1G
     * @return
     */
    long getMaxCubeFileSize();

    /**
     * 控制单个分析中的node的size，超过这个size就不继续计算，并且会日志记录模板信息。
     * 默认0，表示不限制
     * @return
     */
    int getMaxStructureSize();

    /**
     * 控制螺旋分析中单个字段的的明细的行数，超过行数就不继续计算，并且会日志记录模板信息。
     * 默认0，表示不限制
     * @return
     */
    int getMaxSPADetailSize();
}
