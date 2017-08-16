package com.fr.fs.mapeditor.vanchart.imgevent;

import com.fr.base.FRContext;
import com.fr.general.GeneralContext;
import com.fr.general.ComparatorUtils;
import com.fr.general.IOUtils;
import com.fr.stable.StringUtils;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.*;
import java.util.*;

public class ImageUtils {
    public static final String NO_SERVICE = "no service";
    public static final String SERVER_RENDER_FAULT = "server render fault";
    private static final int BUFFER_SIZE = 256;

    public static BufferedImage generateImage(String imageStr) {
        if (ComparatorUtils.equals(imageStr, null) || ComparatorUtils.equals(NO_SERVICE, imageStr)) { // 图像数据为空,说明服务器没起来
            //数据为空，说明服务器配置出现问题，显示提示信息
            String imgPath = "com/fr/fs/mapeditor/vanchart/imgevent/imageinfo/info.png";
            BufferedImage image = IOUtils.readImage(imgPath);
            return image;
        }

        if (ComparatorUtils.equals(imageStr, StringUtils.EMPTY) || ComparatorUtils.equals(SERVER_RENDER_FAULT, imageStr)){ //等待服务器发回图片来
            String imgPath;
            if (GeneralContext.isChineseEnv()){
                imgPath = "com/fr/fs/mapeditor/vanchart/imgevent/imageinfo/loading.png";
            }else{
                imgPath = "com/fr/fs/mapeditor/vanchart/imgevent/imageinfo/EN_loading.png";
            }
            BufferedImage image = IOUtils.readImage(imgPath);
            return image;
        }

        return base64Decoder(imageStr);
    }

    public static BufferedImage base64Decoder(String base64){
        BASE64Decoder decoder = new BASE64Decoder();
        BufferedImage img = null;
        try {
            // Base64解码
            byte[] bytes = decoder.decodeBuffer(base64);
            for (int i = 0; i < bytes.length; ++i) {
                if (bytes[i] < 0) {// 调整异常数据
                    bytes[i] += BUFFER_SIZE;
                }
            }
            InputStream buffin = new ByteArrayInputStream(bytes, 0, bytes.length);

            img = ImageIO.read(buffin);
        } catch (Exception e) {
            return null;
        }
        return img;
    }
}
