/**
 * 汇总表格帮助类
 * Created by Young's on 2017/1/19.
 */
!(function () {
    BI.SummaryTableHelper = {};
    BI.extend(BI.SummaryTableHelper, {
        parseHEXAlpha2HEX: function (hex, alpha) {
            var rgb = BI.DOM.hex2rgb(hex);
            var rgbJSON = BI.DOM.rgb2json(rgb);
            rgbJSON.a = alpha;
            return BI.DOM.rgba2rgb(BI.DOM.json2rgba(rgbJSON));
        },

        getOddColorByThemeColor: function (color) {
            return this.parseHEXAlpha2HEX(color, 0.2);
        },

        getEvenColorByThemeColor: function (color) {
            return this.parseHEXAlpha2HEX(color, 0.05);
        },

        getSummaryColorByThemeColor: function (color) {
            return this.parseHEXAlpha2HEX(color, 0.4);
        },

        getRowColorByIndexAndThemeColor: function (index, color) {
            return index % 2 === 0 ? this.getOddColorByThemeColor(color) : this.getEvenColorByThemeColor(color);
        },

        getHeaderStyles: function (themeColor, styleType) {
            switch (styleType) {
                case BICst.TABLE_STYLE.STYLE1:
                    return {
                        background: themeColor,
                        color: "#ffffff"
                    };
                    break;
                case BICst.TABLE_STYLE.STYLE2:
                    return {
                        background: themeColor,
                        color: "#ffffff"
                    };
                    break;
                case BICst.TABLE_STYLE.STYLE3:
                    return {
                        color: "#808080"
                    };
                    break;
                default :
                    return {};
            }
        },

        getBodyStyles: function (themeColor, styleType, index) {
            switch (styleType) {
                case BICst.TABLE_STYLE.STYLE1:
                    return {
                        background: this.getRowColorByIndexAndThemeColor(index, themeColor)
                    };
                    break;
                case BICst.TABLE_STYLE.STYLE2:
                    return {};
                    break;
                case BICst.TABLE_STYLE.STYLE3:
                    return {};
                    break;
                default :
                    return {};
            }
        },

        getSummaryStyles: function (themeColor, styleType) {
            switch (styleType) {
                case BICst.TABLE_STYLE.STYLE1:
                    return {
                        background: this.getSummaryColorByThemeColor(themeColor),
                        color: "#1a1a1a",
                        fontWeight: "bold"
                    };
                    break;
                case BICst.TABLE_STYLE.STYLE2:
                    return {
                        color: "#1a1a1a",
                        fontWeight: "bold"
                    };
                    break;
                case BICst.TABLE_STYLE.STYLE3:
                    return {
                        color: "#1a1a1a",
                        fontWeight: "bold"
                    };
                    break;
                default :
                    return {};
            }
        },

        getLastSummaryStyles: function (themeColor, styleType) {
            switch (styleType) {
                case BICst.TABLE_STYLE.STYLE1:
                    return {
                        background: themeColor,
                        color: "#ffffff",
                        fontWeight: "bold"
                    };
                    break;
                case BICst.TABLE_STYLE.STYLE2:
                    return {
                        color: themeColor,
                        fontWeight: "bold"
                    };
                    break;
                case BICst.TABLE_STYLE.STYLE3:
                    return {
                        background: themeColor,
                        color: "#ffffff",
                        fontWeight: "bold"
                    };
                    break;
                default :
                    return {};
            }
        }

    });
})();