package com.fr.fs.mapeditor.server;

import com.fr.base.BaseUtils;
import com.fr.base.FRContext;
import com.fr.general.http.HttpClient;
import com.fr.plugin.chart.vanchart.BackgroundImageParam;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Created by Mitisky on 16/6/24.
 */
public class MapGisLayerImageFactory {
    private static final String PATH2X = "com/fr/fs/plugin/mapeditor/images/marker-iconx2.png";
    private static Image image2x;
    private static final String PATH = "com/fr/fs/plugin/mapeditor/images/marker-icon.png";
    private static Image image;

    public static Image getPointMapImage() {
        if(image == null){
            image = BaseUtils.readImage(PATH);
        }
        return image;
    }

    public static Image getPointMap2XImage() {
        if(image2x == null){
            image2x = BaseUtils.readImage(PATH2X);
        }
        return image2x;
    }

    //todo:越存越多,什么时候给置空
    private static Map<String, BufferedImage> urlImageMap = new HashMap<String, BufferedImage>();//存每个url对应的image,省得每次发送请求去取了


    public static BufferedImage getImageFormURLDoInBackground(final BackgroundImageParam param, final String url){
        BufferedImage image = urlImageMap.get(url);

        //如果资源已经在请求，则不需要重复请求，请求结束后重绘就会拿到资源
        if(image == null){
            new SwingWorker<Void, Double>(){
                @Override
                protected Void doInBackground() throws Exception {
                    getImageFormURL(url);
                    return null;
                }

                public void done() {
                    if(urlImageMap.get(url) != null) {

                    }
                }
            }.execute();
        }

        return image;
    }

    public static BufferedImage getImageFormURL(final String url) {
        BufferedImage image = urlImageMap.get(url);

        if(image == null) {
            HttpClient hc = new HttpClient(url);
            hc.asGet();
            hc.setTimeout(60000);

            if(!hc.isServerAlive()){
                return null;
            }

            InputStream inputStream = hc.getResponseStream();
            if(inputStream != null){
                try {
                    urlImageMap.put(url, ImageIO.read(inputStream));
                } catch (IOException e){
                    //读取图片失败
                    FRContext.getLogger().error("gis image " + url + ": " + e.getMessage());
                }
            }
        }

        return urlImageMap.get(url);
    }
}
