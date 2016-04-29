package com.fr.bi.stable.locale;

/**
 * Created by sheldon on 14-9-29.
 */

import com.fr.bi.stable.utils.code.BILogger;
import com.fr.general.*;
import com.fr.stable.EnvChangedListener;
import com.fr.stable.StringUtils;
import com.fr.stable.core.LocaleProvider;
import com.fr.stable.project.ProjectConstants;

import java.io.InputStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *  从finereport超过来的
 *  国际化，可以处理自定义的国际化文件。
 * 比如
 * 自定义的中文：WEB-INF/locale/fr_zh_CN.properties
 * 自定义的英文：WEB-INF/locale/fr_en_US.properties
 */
public class Interbi implements LocaleProvider {

    private static Map<Locale, ResourceBundle> predefinedMap = new HashMap<Locale, ResourceBundle>();
    private static Map<Locale, ResourceBundle> customMap = new HashMap<Locale, ResourceBundle>();
    private static Map<Locale, Set<String>> keysMap = new HashMap<Locale, Set<String>>();
    private static Locale defaultloc = Locale.CHINA;

    private static String paraPatStr = "\\{par\\}";
    private static String paraStr = new String("{par}");

    public static void setDefaultloc( Locale loc ) {
        if( loc != null ) {
            defaultloc = loc;
        }
    }

    static {
        loadLanguage();
        loadLanguageFromEnv();
        GeneralContext.addEnvChangedListener(new EnvChangedListener() {
            @Override
            public void envChanged() {
                loadLanguageFromEnv();
            }
        });
    }

    private static void loadLanguage() {
        loadLanguageInProject(GeneralContext.getLocale());
    }

    private static void loadLanguageInProject(Locale locale) {
        if (predefinedMap.containsKey(locale)) {
            return;
        }
        try {
            ResourceBundle rb = ResourceBundle.getBundle("com/fr/bi/stable/locale/fbi", locale, Inter.class.getClassLoader());
            predefinedMap.put(locale, rb);
        } catch (Throwable e) {
            BILogger.getLogger().error(e.getMessage(), e);
        }
    }

    private static void loadLanguageFromEnv() {
        loadLanguageFromEnv(GeneralContext.getLocale());
    }

    private static void loadLanguageFromEnv(Locale locale) {
        if (customMap.containsKey(locale)) {
            return;
        }
        EnvProvider env = GeneralContext.getEnvProvider();
        if (env == null) {
            return;
        }
        try {
            InputStream in = env.readBean("fbi_" + locale.toString() + ".properties",   "classes/com/fr/bi/stable/"+ ProjectConstants.LOCALE_NAME);
            ResourceBundle rb = new PropertyResourceBundle(in);
            customMap.put(locale, rb);

            Set<String> keys = new HashSet<String>();
            keysMap.put(locale, keys);
            Enumeration<String> em = rb.getKeys();
            while (em.hasMoreElements()) {
                keys.add(em.nextElement());
            }
        } catch (Exception e) {
            // richie:这里不需要抛错，因为本来这个文件就可能不存在的
            customMap.put(locale, null);
        }
    }


    /**
     * 获取国际化文本
     *
     * @param text 需要国际化的值对应的键
     * @return 国际化后的文本
     */
    public static String getLocText(String text) {
        return getLocText(text, defaultloc);
    }

    /**
     * 通过传入参数获取国际化文本
     * @param key      文本
     * @param param    获取到的参数
     * @return      国际化后的文本
     */
    public static String getLocText( String key, String[] param) {
        return getLocText(key, param, defaultloc);
    }

    /**
     * 通过传入参数获取国际化文本
     * @param key      文本
     * @param param    获取到的参数
     * @param locale   语言
     * @return      国际化后的文本
     */
    public static String getLocText( String key, String[] param, Locale locale) {
        String text = getLocText(key, locale);

        Pattern p = Pattern.compile( paraPatStr );
        Matcher matcher = p.matcher(text);

        int count = 0;
        while ( matcher.find() && count<param.length ) {
            text = matcher.replaceFirst( param[count++]);
            matcher = p.matcher(text);
        }

        return text;
    }

    /**
     * 获取国际化文本
     * @deprecated 不推荐再使用这个方法，所有的国际化都最好用单一的字符串而不是拼接
     * @param i18nTextArr 要分别国际化的字符串数组
     * @return 组合国际化后的文本
     */
    public static String getLocText(String[] i18nTextArr) {
        return getLocText(i18nTextArr, null);
    }

    /**
     * 获取国际化文本
     * @deprecated 不推荐再使用这个方法，所有的国际化都最好用单一的字符串而不是拼接
     * @param i18nTextArr 要分别国际化的字符串数组
     * @param delimiter   每个字符串数组在国际化后分别加的连接符
     * @return 组合国际化后的文本
     */
    public static String getLocText(String[] i18nTextArr, String[] delimiter) {
        String i18nText = StringUtils.EMPTY;
        // 英文需要在单词之间加一个空格
        String space = ComparatorUtils.equals(Locale.US, GeneralContext.getLocale()) ? " " : "";
        for (int i = 0, len = i18nTextArr.length; i < len; i++) {
            i18nText += (i == 0 ? "" : space) + getLocText(i18nTextArr[i]);
            if (delimiter != null && delimiter.length > i) {
                i18nText += space + delimiter[i];
            }
        }
        return i18nText;
    }

    /**
     * 根据不同的语言环境获取相应的国际化后的文本
     * @param text 国际化文本的键
     * @param locale 语言环境
     * @return 国际化后的文本
     */
    public static String getLocText(String text, Locale locale) {
        return getLocText(text, locale, Locale.SIMPLIFIED_CHINESE);
    }

    /**
     * 根据不同的语言环境获取相应的国际化后的文本
     * @param text 国际化文本的键
     * @param locale 当前的语言环境
     * @param defaultLocale 默认的语言环境，如果当前语言环境不在预定义的默认环境中时，将使用默认的语言环境
     * @return 国际化后的文本
     */
    public static String getLocText(String text, Locale locale, Locale defaultLocale) {
        if (locale == null) {
            locale = defaultLocale;
        }
        // 从浏览器端获取的locale可能没有国家信息，这时就需要指定一个国家信息才行
        if (StringUtils.isEmpty(locale.getCountry())) {
            String language = locale.getLanguage();
            if ("zh".equals(language)) {
                locale = Locale.SIMPLIFIED_CHINESE;
            } else if ("ja".equals(language)) {
                locale = Locale.JAPAN;
            } else {
                locale = Locale.US;
            }
        }
        if (!customMap.containsKey(locale)) {
            loadLanguageFromEnv(locale);
        }
        if (!predefinedMap.containsKey(locale)) {
            loadLanguageInProject(locale);
        }
        ResourceBundle custom = customMap.get(locale);
        Set keys = (Set)keysMap.get(locale);
        if (custom != null && keys.contains(text)) {
            return custom.getString(text);
        }
        ResourceBundle fr = predefinedMap.get(locale);
        if (fr == null) {
            fr = predefinedMap.get(defaultLocale);
        }
        return fr.getString(text);
    }
}