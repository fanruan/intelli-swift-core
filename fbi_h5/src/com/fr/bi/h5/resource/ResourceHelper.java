package com.fr.bi.h5.resource;

import com.fr.base.ConfigManager;
import com.fr.base.TemplateUtils;
import com.fr.bi.h5.locale.InterH5;
import com.fr.general.ComparatorUtils;
import com.fr.general.FRLogger;
import com.fr.general.IOUtils;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;
import com.fr.script.Calculator;
import com.fr.stable.bridge.StableFactory;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Root on 2016/10/21.
 */
public class ResourceHelper {

    //i18n.js需要在浏览器语言切换时每次加载，而i18n.js必须在中间加载....
    private static String UN_NECESSARY_JS = null;
    private static String NO_REPLACE_JS = null;

    //用于判断是否需要重新加载i18n.js若两次访问的local不同则加载之, 也可以作为当前浏览器语言使用

    private static final Map<FileCacheKey, String> DEFAULT_CACHE_MAP = new ConcurrentHashMap<FileCacheKey, String>();

    public static String[] getMobileJs() {
        return new String[]{
                "com/fr/bi/h5/alias/mobile.jQuery.js",
                "com/fr/bi/h5/alias/alias.js",
                "com/fr/bi/web/js/third/d3.js",
                "com/fr/bi/web/js/third/es5-sham.js",
                "com/fr/bi/web/js/third/raphael.js",
                "com/fr/bi/web/js/third/leaflet.js",
                "com/fr/bi/web/js/third/vancharts-all.js",

                "com/fr/bi/web/js/core/underscore.js",
                "com/fr/bi/web/js/base/base.js",
                "com/fr/bi/web/js/data/data.js",
                "com/fr/bi/web/js/data/constant/biconst.js",
                "com/fr/bi/web/js/data/constant/enums.js",
                "com/fr/bi/web/js/base/proto/number.js",
                "com/fr/bi/web/js/base/proto/array.js",
                "com/fr/bi/web/js/base/proto/date.js",
                "com/fr/bi/web/js/data/utils.js"
        };
    }

    private static String[] getLocaleFiles() {
        return new String[]{"com/fr/bi/h5/locale/h5"};
    }

    private static StringBuffer ensureI18n(Locale locale) {
        StringBuffer i18nJS = new StringBuffer();
        JSONObject jo = new JSONObject();
        String[] localeDirs = getLocaleFiles();
        for (String dir : localeDirs) {
            ResourceBundle r = null;
            try {
                r = ResourceBundle.getBundle(dir, locale, InterH5.class.getClassLoader());
            } catch (Exception e) {
                try {
                    r = ResourceBundle.getBundle(dir, Locale.US, InterH5.class.getClassLoader());
                    FRLogger.getLogger().error("No suitable international properties file, use EN_US to replace!");
                } catch (Exception e1) {
                    FRLogger.getLogger().error("No suitable international properties file");
                }

            }
            if (r != null) {
                Enumeration<String> enumeration = r.getKeys();
                while (enumeration.hasMoreElements()) {
                    String key = enumeration.nextElement();
                    try {
                        jo.put(key, r.getString(key));
                    } catch (JSONException e) {
                        FRLogger.getLogger().error(e.getMessage(), e);
                    }
                }
            }
        }
        Map<String, JSONObject> map = new HashMap<String, JSONObject>();
        map.put("i18n_props", jo);
        try {
            i18nJS = new StringBuffer()
                    .append(TemplateUtils.renderTemplate("/com/fr/bi/h5/templates/i18n4properties.js", map));
        } catch (IOException e) {
            FRLogger.getLogger().error(e.getMessage(), e);
        }
        return i18nJS;
    }

    private static String createI18NJs(Locale locale) {
        return ensureI18n(locale).toString();
    }

    public static String createDefaultJs(Locale locale) throws Exception {
        FileCacheKey jsCacheKey = new FileCacheKey(ResourceConstants.DEFAULT_H5_JS, locale);
        String js = DEFAULT_CACHE_MAP.get(jsCacheKey);
        if (js == null) {
            synchronized (ResourceHelper.class) {
                js = DEFAULT_CACHE_MAP.get(jsCacheKey);
                if(js != null) {
                    return js;
                }
                //daniel 稍微修改下i18n.js需要每次加载 否则会导致浏览器国际化不正确
                String un_ne_js = UN_NECESSARY_JS;
                if (un_ne_js == null) {
                    String[] unnecessaryReplaceJS = new String[]{
                            "com/fr/bi/web/js/third/zepto.js",
                            "com/fr/bi/web/js/third/d3.js",
                            "com/fr/bi/web/js/third/leaflet.js",
                            "com/fr/bi/web/js/third/vancharts-all.js",
                            "com/fr/bi/h5/alias/alias.js",

                            "com/fr/bi/web/js/core/foundation.js",
                            "com/fr/bi/web/js/data/data.js",
                            "com/fr/bi/web/js/data/constant/biconst.js",
                            "com/fr/bi/web/js/data/constant/constant.js",
                            "com/fr/bi/web/js/data/constant/strings.js",
                            "com/fr/bi/web/js/data/constant/enums.js",
                            "com/fr/bi/web/js/data/constant/colors.js",
                            "com/fr/bi/web/js/data/constant/attrs.js"
                    };
                    //b:finereport.js中只有servletURL & i18n 的替换都是相对固定的，所以就直接这里替换了
                    un_ne_js = new StringBuffer().append(contactArrayString(unnecessaryReplaceJS)).append("\n").toString();
                    UN_NECESSARY_JS = un_ne_js;
                }
                js = new StringBuffer().append(createI18NJs(locale)).append(un_ne_js).toString();
                DEFAULT_CACHE_MAP.put(jsCacheKey, js);
            }
        }
        return js;
    }

    public static void writeJS(HttpServletResponse res, String js) throws IOException {
        String charset = ConfigManager.getProviderInstance().getServerCharset();
        res.setContentType("text/javascript;charset=" + charset);
        PrintWriter writer = WebUtils.createPrintWriter(res);
        writer.write(js);
        writer.flush();
        writer.close();
    }

    private static String contactArrayString(String[] files) {
        return IOUtils.concatFiles(files, '\n');
    }

    public static void getH5Js(HttpServletRequest req, HttpServletResponse res) throws Exception {
        Locale locale = WebUtils.getLocale(req);
        writeJS(res, createDefaultJs(locale));
    }

    static class FileCacheKey {
        private String resources;
        private Locale locale;

        public FileCacheKey(String servletPath, Locale locale) {
            this.resources = servletPath;
            this.locale = locale;
        }

        FileCacheKey(String resources) {
            this.resources = resources;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            FileCacheKey that = (FileCacheKey) o;

            if (locale != null ? !ComparatorUtils.equals(locale, that.locale) : that.locale != null) {
                return false;
            }
            if (resources != null ? !ComparatorUtils.equals(resources, that.resources) : that.resources != null) {
                return false;
            }

            return true;
        }

        public int hashCode() {
            int result = resources != null ? resources.hashCode() : 0;
            result = 31 * result + (locale != null ? locale.hashCode() : 0);
            return result;
        }
    }
}
