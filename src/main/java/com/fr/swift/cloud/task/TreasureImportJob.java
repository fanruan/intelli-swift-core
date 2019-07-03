package com.fr.swift.cloud.task;

import com.fr.swift.cloud.SwiftCloudConstants;
import com.fr.swift.cloud.bean.TreasureBean;
import com.fr.swift.cloud.load.FileImportUtils;
import com.fr.swift.cloud.util.CloudLogUtils;
import com.fr.swift.cloud.util.ZipUtils;
import com.fr.swift.executor.task.job.BaseJob;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.util.FileUtil;
import com.fr.swift.util.Strings;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;

/**
 * This class created on 2019/5/9
 *
 * @author Lucifer
 * @description
 */
public class TreasureImportJob extends BaseJob<Boolean, TreasureBean> {

    private TreasureBean treasureBean;
    private final static int RETRY_TIMES = 10;
    private final static long RETRY_INTERVAL = 10 * 1000L;

    public TreasureImportJob(TreasureBean treasureBean) {
        this.treasureBean = treasureBean;
    }

    @Override
    public Boolean call() throws Exception {
        // 通过客户的用户ID、客户的应用ID和客户的数据包日期获取数据包的下载链接
        CloudLogUtils.logStartJob(treasureBean.getClientId(), treasureBean.getClientAppId(), treasureBean.getYearMonth(), treasureBean.getVersion(), "download");
        String downloadLink = treasureBean.getUrl();
        if (Strings.isNotEmpty(downloadLink)) {
            SwiftLoggers.getLogger().info("get download link success. link is {}", downloadLink);
            InputStream inputStream = null;
            for (int i = 0; i < RETRY_TIMES; i++) {
                try {
                    inputStream = new URL(downloadLink).openStream();
                    break;
                } catch (FileNotFoundException e) {
                    Thread.sleep(RETRY_INTERVAL);
                }
            }
            String downloadPath = SwiftCloudConstants.ZIP_FILE_PATH + File.separator + treasureBean.getClientId() + File.separator + treasureBean.getClientAppId() + File.separator + treasureBean.getYearMonth();
            try {
                ZipUtils.unzip(inputStream, new File(downloadPath));

                String treasurePath = downloadPath + File.separator + "treas" + treasureBean.getYearMonth();
                String gcLogsPath = treasurePath + File.separator + "logs" + File.separator + "gclogs";
                //把gclogs目录下的所有gc文件都移动到downloadPath下
                ZipUtils.moveGCFiles(gcLogsPath, downloadPath);

                // 先导入csv文件数据到cube，然后生成分析结果，并保存到数据库
                CloudLogUtils.logStartJob(treasureBean.getClientId(), treasureBean.getClientAppId(), treasureBean.getYearMonth(), treasureBean.getVersion(), "import");
                FileImportUtils.load(downloadPath, treasureBean.getClientAppId(), treasureBean.getYearMonth(), treasureBean.getVersion());
                new TreasureAnalysisTask(treasureBean).getJob().call();
//            TaskProducer.produceTask(new TreasureAnalysisTask(treasureBean));
            } finally {
                FileUtil.delete(downloadPath);
            }
        } else {
            throw new RuntimeException("Download link is empty");
        }
        return true;
    }

    @Override
    public TreasureBean serializedTag() {
        return treasureBean;
    }
}