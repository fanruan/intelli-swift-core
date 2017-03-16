/**
 * 汇总表格帮助类
 * Created by Young's on 2017/1/19.
 */
!(function () {
    BI.SummaryTableHelper = {};
    BI.extend(BI.SummaryTableHelper, {

        MIN_COLUMN_INIT_WIDTH: 80,
        MAX_COLUMN_INIT_WIDTH: 200,

        //获取字符宽度
        getGBWidth: function (str) {
            str = str + "";
            str = str.replace(/[^\x00-\xff]/g, 'xx');
            return Math.ceil(str.length / 2);
        },

        fit: function (widths) {
            function sumBy(array, it) {
                var res = 0;
                BI.each(array, function (i, width) {
                    res += it(i, width);
                });
                return res;
            }

            if (widths.length < 2) {
                return {a: widths[0], b: 0};
            }
            var w11 = widths.length;
            var w12 = (1 + widths.length) * widths.length / 2;
            var w21 = w12;
            var w22 = sumBy(widths, function (i, width) {
                return (i + 1) * (i + 1);
            });
            var f1 = BI.sum(widths);
            var f2 = sumBy(widths, function (i, width) {
                return (i + 1) * width;
            });
            return {
                a: (f2 * w12 - f1 * w22) / (w12 * w21 - w11 * w22),
                b: (f2 * w11 - f1 * w21) / (w11 * w22 - w21 * w12)
            }
        },

        getWidthsByItems: function (items) {
            var self = this;
            var widths = [];
            BI.each(items, function (i, item) {
                widths.push(self.getGBWidth(item.text) * 12 * 1.2 + (item.needExpand ? 25 : 0) + (item.iconClass ? 25 : 0) + (item.list ? 25 : 0));
            });
            return widths;
        },

        getColumnWidthByColumns: function (columns) {
            var self = this;
            var widths = [];
            BI.each(columns, function (i, items) {
                var fx = self.fit(self.getWidthsByItems(items));
                widths.push(BI.clamp(Math.ceil((fx.a + fx.b * Math.ceil((1 + items.length) / 2))) + 20, self.MIN_COLUMN_INIT_WIDTH, self.MAX_COLUMN_INIT_WIDTH));
            });
            return widths;
        },

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
                        color: "#ffffff",
                        fontWeight: "bold"
                    };
                    break;
                case BICst.TABLE_STYLE.STYLE2:
                    return {
                        background: themeColor,
                        color: "#ffffff",
                        fontWeight: "bold"
                    };
                    break;
                case BICst.TABLE_STYLE.STYLE3:
                    return {
                        background: "none",
                        color: "#808080",
                        fontWeight: "bold"
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