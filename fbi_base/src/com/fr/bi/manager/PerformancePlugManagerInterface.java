package com.fr.bi.manager;

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

    boolean isReturnEmptyIndex();

    boolean isSearchPinYin();

    boolean isGetTemplateScreenCapture();

    boolean controlMaxMemory();

    int getMaxNodeCount();


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

    void setBiTransportThreadPoolSize(int size);

    int getBiTransportThreadPoolSize();

    void printSystemParameters();

    void setPhantomServerIP(String ip);

    String getPhantomServerIP();

    void setPhantomServerPort(int port);

    int getPhantomServerPort();

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
     * @return
     */
    int getReIndexRowCount();

    void setMinCubeFreeHDSpaceRate(double rate);

    double getMinCubeFreeHDSpaceRate();

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
     * 生成cube的时候是否释放读的文件
     * @return
     */
    boolean isGeneratingReleaseReader();
}
