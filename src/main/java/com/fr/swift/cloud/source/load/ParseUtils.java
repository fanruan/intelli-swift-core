package com.fr.swift.cloud.source.load;

import com.fr.swift.source.ColumnTypeConstants;
import com.fr.swift.source.ColumnTypeUtils;
import com.fr.swift.source.SwiftMetaDataColumn;
import info.monitorenter.cpdetector.io.ASCIIDetector;
import info.monitorenter.cpdetector.io.CodepageDetectorProxy;
import info.monitorenter.cpdetector.io.JChardetFacade;
import info.monitorenter.cpdetector.io.UnicodeDetector;
import org.apache.commons.lang3.math.NumberUtils;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lyon on 2019/3/7.
 */
public class ParseUtils {

    public static Map<String, Object> parseFields(String[] row, List<SwiftMetaDataColumn> columns) {
        Map<String, Object> map = new LinkedHashMap<String, Object>();
        for (int i = 0; i < columns.size(); i++) {
            map.put(columns.get(i).getName(), convert(row[i], columns.get(i)));
        }
        return map;
    }

    public static Object convert(String value, SwiftMetaDataColumn column) {
        if (value == null) {
            return null;
        }
        ColumnTypeConstants.ClassType type = ColumnTypeUtils.getClassType(column);
        switch (type) {
            case DOUBLE: {
                if (NumberUtils.isCreatable(value)) {
                    return NumberUtils.toDouble(value);
                }
                break;
            }
            case INTEGER: {
                if (NumberUtils.isCreatable(value)) {
                    return NumberUtils.toInt(value);
                }
                break;
            }
            case LONG:
            case DATE: {
                if (NumberUtils.isCreatable(value)) {
                    Number n = NumberUtils.createNumber(value);
                    return n == null ? null : n.longValue();
                }
                break;
            }
            default:
                return value;
        }
        return null;
    }

    /**
     * 来自：https://github.com/YFSAO/CPDetector
     * <p>
     * 利用第三方开源包cpdetector获取文件编码格式
     *
     * @param path 要判断文件编码格式的源文件的路径
     * @author WangFengwei
     */
    public static String getFileEncode(String path) {

        try {
            return getFileEncode(new File(path).toURI().toURL());
        } catch (MalformedURLException e) {
        }
        return null;
    }

    /**
     * 利用第三方开源包cpdetector获取URL对应的文件编码
     *
     * @param url 要判断文件编码格式的源文件的URL
     * @author huanglei
     * @version 2012-7-12 14:05
     */
    private static String getFileEncode(URL url) {
        /*
         * detector是探测器，它把探测任务交给具体的探测实现类的实例完成。
         * cpDetector内置了一些常用的探测实现类，这些探测实现类的实例可以通过add方法 加进来，如ParsingDetector、
         * JChardetFacade、ASCIIDetector、UnicodeDetector。
         * detector按照“谁最先返回非空的探测结果，就以该结果为准”的原则返回探测到的
         * 字符集编码。使用需要用到三个第三方JAR包：antlr.jar、chardet.jar和cpdetector.jar
         * cpDetector是基于统计学原理的，不保证完全正确。
         */
        CodepageDetectorProxy detector = CodepageDetectorProxy.getInstance();
        /*
         * JChardetFacade封装了由Mozilla组织提供的JChardet，它可以完成大多数文件的编码
         * 测定。所以，一般有了这个探测器就可满足大多数项目的要求，如果你还不放心，可以
         * 再多加几个探测器，比如下面的ASCIIDetector、UnicodeDetector等。
         */
        detector.add(JChardetFacade.getInstance());// 用到antlr.jar、chardet.jar
        // ASCIIDetector用于ASCII编码测定
        detector.add(ASCIIDetector.getInstance());
        // UnicodeDetector用于Unicode家族编码的测定
        detector.add(UnicodeDetector.getInstance());
        java.nio.charset.Charset charset = null;
        try {
            charset = detector.detectCodepage(url);
        } catch (Exception ex) {
        }
        if (charset != null)
            return charset.name();
        else
            return null;
    }
}
