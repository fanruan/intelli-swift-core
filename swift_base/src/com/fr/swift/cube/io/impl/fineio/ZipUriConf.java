package com.fr.swift.cube.io.impl.fineio;

import com.fr.base.FRContext;
import com.fr.file.XMLFileManager;
import com.fr.general.ComparatorUtils;
import com.fr.general.GeneralContext;
import com.fr.stable.EnvChangedListener;
import com.fr.stable.StringUtils;
import com.fr.stable.xml.XMLPrintWriter;
import com.fr.stable.xml.XMLableReader;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.util.Util;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Zip4j URI配置文件
 *
 * @author yee
 * @date 2017/8/8
 */
public class ZipUriConf extends XMLFileManager {
    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(ZipUriConf.class);
    /**
     * 配置文件及标签名
     */
    public static final String TAG_NAME = "ZipURIConf";
    private static final String ARRAY_TAG = "URIArray";
    private static final String ITEM_TAG = "item";
    private static final String CACHE_TIMEOUT_TAG = "CacheTimeout";
    /**
     * 默认Cache超时为1天
     */
    private static final long DEFAULT_CACHE_TIMEOUT = 86400000;
    /**
     * 配置文件存储的内容，方便记录存储为字符串
     */
    private ConfList<URI> uris = new ConfList<URI>();
    /**
     * 存储超时
     */
    private long cacheTimer = DEFAULT_CACHE_TIMEOUT;


    private static ZipUriConf instance;

    public static ZipUriConf getInstance() {
        if (null != instance) {
            return instance;
        }
        synchronized (ZipUriConf.class) {
            if (null != instance) {
                return instance;
            }
            instance = new ZipUriConf();
            if (!instance.readXMLFile()) {
                try {
                    FRContext.getCurrentEnv().writeResource(instance);
                } catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
        }
        return instance;
    }

    private ZipUriConf() {
        // fixme
        String path = "/cubes/";
        uris.add(0, new File(path/*BIConfigurePathUtils.createBasePath()*/).toURI());
        GeneralContext.addEnvChangedListener(new EnvChangedListener() {
            @Override
            public void envChanged() {
                readXMLFile();
            }
        });
    }

    /**
     * 获取URI列表
     *
     * @return
     */
    public List<URI> getUris() {
        return uris;
    }

    /**
     * 获取默认的URI
     *
     * @return
     */
    public URI getDefaultURI() {
        if (getUris().size() > 0) {
            return getUris().get(0);
        }
        return null;
    }

    /**
     * 添加URI数组
     *
     * @param array
     * @return
     */
    public ZipUriConf addURIs(URI[] array) {
        Util.requireNonNull(array);
        for (URI uri : array) {
            uris.add(new File(uri.getPath()).toURI());
        }
        try {
            FRContext.getCurrentEnv().writeResource(instance);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return instance;
    }

    /**
     * 添加单个URI
     *
     * @param uri
     * @return
     */
    public ZipUriConf addURI(URI uri) {
        return addURIs(new URI[]{uri});
    }

    @Override
    public String fileName() {
        return TAG_NAME + ".xml";
    }

    @Override
    public void readXML(XMLableReader reader) {
        if (reader.isChildNode()) {

            if (ComparatorUtils.equals(reader.getTagName(), CACHE_TIMEOUT_TAG)) {
                this.cacheTimer = reader.getAttrAsLong("timer", DEFAULT_CACHE_TIMEOUT);
            }

            if (ComparatorUtils.equals(reader.getTagName(), ITEM_TAG)) {
                String path = reader.getAttrAsString("path", StringUtils.EMPTY);
                String rawPath = reader.getAttrAsString("rawPath", StringUtils.EMPTY);
                if (StringUtils.isNotEmpty(path)) {
                    uris.add(new File(path).toURI());
                } else if (StringUtils.isNotEmpty(rawPath)) {
                    uris.add(new File(rawPath).toURI());
                }
            }
        }
    }

    @Override
    public void writeXML(XMLPrintWriter writer) {
        writer.startTAG(TAG_NAME);
        writer.startTAG(CACHE_TIMEOUT_TAG);
        writer.attr("timer", cacheTimer);
        writer.end();
        writer.startTAG(ARRAY_TAG);
        for (URI uri : uris) {
            writer.startTAG(ITEM_TAG);
            if (null != uri.getPath()) {
                writer.attr("path", uri.getPath());
            }
            if (null != uri.getRawPath()) {
                writer.attr("rawPath", uri.getRawPath());
            }
            writer.end();
        }
        writer.end();
        writer.end();
    }

    /**
     * URI配置列表
     *
     * @param <T>
     */
    private class ConfList<T> extends ArrayList<T> {
        /**
         * 如果存在则不再添加
         *
         * @param t
         * @return
         */
        @Override
        public boolean add(T t) {
            return this.contains(t) ? true : super.add(t);
        }

        /**
         * 如果存在则不再添加，直接交换位置
         *
         * @param index
         * @param element
         */
        @Override
        public void add(int index, T element) {
            if (this.contains(element)) {
                int i = indexOf(element);
                if (i != index) {
                    T tmp = get(index);
                    super.add(index, element);
                    super.add(i, tmp);
                    super.remove(i + 1);
                    super.remove(index + 1);
                } else {
                    return;
                }
            } else {
                super.add(index, element);
            }
        }
    }

    public long getCacheTimer() {
        return cacheTimer;
    }

    public void setCacheTimer(long cacheTimer) {
        this.cacheTimer = cacheTimer;
    }
}
