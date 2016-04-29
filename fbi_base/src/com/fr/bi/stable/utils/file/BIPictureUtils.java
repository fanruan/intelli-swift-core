package com.fr.bi.stable.utils.file;

import com.fr.base.FRContext;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.page.PagePainterProvider;
import com.fr.page.ReportPageProvider;
import com.fr.stable.Constants;
import com.fr.stable.CoreGraphHelper;
import com.fr.stable.bridge.StableFactory;
import com.fr.stable.project.ProjectConstants;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Connery on 2015/12/4.
 */
public class BIPictureUtils {


    private static Map<String, Object> imageLockMap = new ConcurrentHashMap<String, Object>();
    /**
     * 保存页面截屏
     *
     * @param bookPath   路径
     * @param pageNumber 页数
     * @param reportPage report页
     */
    public static void savePageScreenCapture(final String bookPath, final int pageNumber, final ReportPageProvider reportPage) {
        if (reportPage == null
                || pageNumber != 1
                || bookPath == null) {
            return;
        }
        new Thread(new Runnable() {

            @Override
            public void run() {
                int resolution = Constants.DEFAULT_WEBWRITE_AND_SCREEN_RESOLUTION;
                float imageWidth = reportPage.getPaperWidth().toPixI(resolution);
                float imageHeight = reportPage.getPaperHeight().toPixF(resolution);

                BufferedImage image = CoreGraphHelper.createBufferedImage((int) imageWidth, (int) imageHeight, BufferedImage.TYPE_INT_RGB);
                Graphics2D g = image.createGraphics();

                g.translate(0, 0);
                HashMap<String, Class> pagePainterClass = new HashMap<String, Class>();
                pagePainterClass.put(Constants.ARG_0, ReportPageProvider.class);
                pagePainterClass.put(Constants.ARG_1, Graphics2D.class);
                PagePainterProvider bp = StableFactory.getMarkedInstanceObjectFromClass(
                        PagePainterProvider.XML_TAG,
                        new Object[]{reportPage, g, Integer.valueOf(String.valueOf(resolution)), false},
                        pagePainterClass, PagePainterProvider.class);
                bp.convert();
                BufferedImage result = equimultipleConvert(400, 280, image);
                String screenCapturePath = FRContext.getCurrentEnv().getPath()
                        + File.separator + ProjectConstants.RESOURCES_NAME + File.separator + "screencapture";
                File dirFile = new File(screenCapturePath);
                if (!dirFile.exists()) {
                    dirFile.mkdir();
                }
                String filePath = screenCapturePath + File.separator + bookPath.hashCode() + ".jpg";
                Object lock = getImageLock(filePath);
                synchronized (lock) {
                    try {
                        ImageIO.write(result, "jpg", new File(filePath));
                    } catch (IOException e) {
                        BILogger.getLogger().error(e.getMessage(), e);
                    }
                }
            }
        }).start();
    }

    public static Object getImageLock(String filePath) {
        Object lock = null;
        synchronized (imageLockMap) {
            lock = imageLockMap.get(filePath);
            if (lock == null) {
                lock = new Object();
                imageLockMap.put(filePath, lock);
            }
        }
        return lock;
    }


    /**
     * 图片转换
     *
     * @param width  width宽
     * @param height height高
     * @param image  图片
     * @return 图片缓冲区
     */
    public static BufferedImage equimultipleConvert(float width, float height, BufferedImage image) {
        float time = width / height;
        float oldWidth = image.getWidth();
        float oldHeight = image.getHeight();
        float oldTime = oldWidth / oldHeight;

        float finalWidth;
        float finalHeight;

        if (oldTime > time) {//宽度太大，按高度截取
            finalWidth = oldHeight * time;
            finalHeight = oldHeight;
        } else {
            finalWidth = oldWidth;
            finalHeight = oldWidth / time;
        }
        BufferedImage cut = image.getSubimage(0, 0, (int) finalWidth, (int) finalHeight);
        BufferedImage result = new BufferedImage((int) width, (int) height, BufferedImage.TYPE_INT_RGB);
        Image scaledImage = cut.getScaledInstance((int) width, (int) height, BufferedImage.TYPE_INT_RGB);
        result.createGraphics().drawImage(scaledImage, null, null);
        return result;
    }
}