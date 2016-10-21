if (window.BH == null) {
    window.BH = {};
}

/**
 * 这里添加一些必须要使用中文的键值对，然后通过FR.plainText(key)进行调用
 */
BH.chinese = {};

BH.i18n = ${i18n_props};
/**
 * 根据键获取国际化后的值
 * @param key 键
 * @returns {String} 国际化后的文本
 * @example
 *    FR.i18nText("Click");//输出结果为"点击"
 *    FR.i18nText("Sum({R1}, {R2}) = 3", 1,2);//输出结果为"Sum(1, 2) = 3"
 */
BH.i18nText = function (key) {
    var localeText = BH.i18n[key];
    if (!localeText) {
        localeText = key;
    }
    var len = arguments.length;
    if (len > 1) {
        for (var i = 1; i < len; i++) {
            var key = "{R" + i + "}";
            localeText = localeText.replaceAll(key, arguments[i] + "");
        }
    }
    return localeText;
};

/**
 * 获取不需要国际化的中文字符串
 * @param key 中文字符串对应的键
 * @returns {String} 中文字符串
 */
BH.plainText = function (key) {
    return BH.chinese[key] || key;
};