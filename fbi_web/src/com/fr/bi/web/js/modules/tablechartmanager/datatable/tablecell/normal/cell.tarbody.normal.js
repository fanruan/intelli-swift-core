/**
 * Created by Young's on 2016/3/24.
 */
BI.TargetBodyNormalCell = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.TargetBodyNormalCell.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-target-body-normal-cell"
        })
    },

    _init: function () {
        BI.TargetBodyNormalCell.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        var dId = o.dId;
        var styleSettings = BI.Utils.getDimensionSettingsByID(dId);
        var text = o.text;
        var iconCls = "", color = "";
        var format = styleSettings.format, numLevel = styleSettings.num_level;
        text = this._parseNumLevel(text, numLevel);
        text = this._parseFloatByDot(text, format);
        var iconStyle = styleSettings.icon_style, mark = styleSettings.mark;
        iconCls = this._getIconByStyleAndMark(text, iconStyle, mark);
        var conditions = styleSettings.conditions;
        BI.some(conditions, function (i, co) {
            var range = co.range;
            var min = BI.parseFloat(range.min), max = BI.parseFloat(range.max);
            var minBoolean = true;
            var maxBoolean = true;
            if (BI.isNumeric(min)) {
                minBoolean = (range.closemin === true ? text >= min : text > min);
            }
            if (BI.isNumeric(max)) {
                maxBoolean = (range.closemax === true ? text <= max : text < max);
            }
            if (minBoolean && maxBoolean) {
                color = co.color;
            }
        });
        var textLabel = this._createTargetText(text);
        if (BI.isNotEmptyString(color)) {
            textLabel.element.css("color", color);
        }
        if (BI.isNotEmptyString(iconCls)) {

            BI.createWidget({
                type: "bi.horizontal_adapt",
                element: this.element,
                items: [textLabel, {
                    type: "bi.default",
                    cls: iconCls,
                    items: [{
                        type: "bi.icon",
                        width: 16,
                        height: 16
                    }],
                    width: 16,
                    height: 16
                }],
                columnSize: ["", 30]
            });
        } else {
            BI.createWidget({
                type: "bi.vertical",
                element: this.element,
                items: [textLabel]
            })
        }
    },

    _parseNumLevel: function (text, numLevel) {
        if (text === Infinity || text !== text) {
            return text;
        }
        switch (numLevel) {
            case BICst.TARGET_STYLE.NUM_LEVEL.NORMAL:
                return text;
            case BICst.TARGET_STYLE.NUM_LEVEL.TEN_THOUSAND:
                return text / 10000;
            case BICst.TARGET_STYLE.NUM_LEVEL.MILLION:
                return text / 1000000;
            case BICst.TARGET_STYLE.NUM_LEVEL.YI:
                return text / 100000000;
            case BICst.TARGET_STYLE.NUM_LEVEL.PERCENT:
                return text * 100;
        }
        return text;
    },

    _parseFloatByDot: function (text, dot) {
        if (text === Infinity || text !== text) {
            return text;
        }
        var num = BI.parseFloat(text);
        switch (dot) {
            case BICst.TARGET_STYLE.FORMAT.NORMAL:
                return num;
                break;
            case BICst.TARGET_STYLE.FORMAT.ZERO2POINT:
                return BI.parseInt(num);
                break;
            case BICst.TARGET_STYLE.FORMAT.ONE2POINT:
                var mnum = Math.round(num * 10) / 10;
                var snum = mnum.toString();
                if (snum.indexOf(".") < 0) {
                    snum = snum + ".0"
                }
                return snum;
            case BICst.TARGET_STYLE.FORMAT.TWO2POINT:
                var mnum = Math.round(num * 100) / 100;
                var snum = mnum.toString();
                if (snum.indexOf(".") < 0) {
                    snum = snum + ".00"
                }
                return snum;
        }
        return text;
    },

    _getIconByStyleAndMark: function (text, style, mark) {
        var num = BI.parseFloat(text), nMark = BI.parseFloat(mark);
        switch (style) {
            case BICst.TARGET_STYLE.ICON_STYLE.NONE:
                return "";
            case BICst.TARGET_STYLE.ICON_STYLE.POINT:
                if (num > nMark) {
                    return "target-style-more-dot-font";
                } else if (num === nMark) {
                    return "target-style-equal-dot-font"
                } else {
                    return "target-style-less-dot-font";
                }
            case BICst.TARGET_STYLE.ICON_STYLE.ARROW:
                if (num > nMark) {
                    return "target-style-more-arrow-font";
                } else if (num === nMark) {
                    return "target-style-equal-arrow-font";
                } else {
                    return "target-style-less-arrow-font";
                }
        }
        return "";
    },

    _createTargetText: function (text) {
        //联动
        var self = this;
        var o = this.options;
        var dId = o.dId, clicked = o.clicked;
        var widgetId = BI.Utils.getWidgetIDByDimensionID(dId);
        var linkage = BI.Utils.getWidgetLinkageByID(widgetId);
        var linkedWidgets = [];
        BI.each(linkage, function (i, link) {
            if (link.from === dId) {
                linkedWidgets.push(link.to);
            }
        });
        if (text === Infinity) {
            text = "N/0";
        }
        if (text !== text) {
            text = "0/0";
        }
        if (BI.isEmptyArray(linkedWidgets)) {
            return BI.createWidget({
                type: "bi.label",
                text: text,
                title: text,
                height: 30,
                textAlign: "left",
                lgap: 5
            });
        } else {
            var textButton = BI.createWidget({
                type: "bi.text_button",
                text: text,
                title: text,
                height: 30,
                textAlign: "left",
                cls: "target-linkage-label",
                lgap: 5
            });
            textButton.on(BI.TextButton.EVENT_CHANGE, function () {
                //这个clicked应该放到子widget中保存起来
                BI.each(linkedWidgets, function (i, linkWid) {
                    BI.Broadcasts.send(BICst.BROADCAST.LINKAGE_PREFIX + linkWid, dId, clicked);
                    self._send2AllChildLinkWidget(linkWid);
                });
            });
            return textButton;
        }
    },

    _send2AllChildLinkWidget: function (wid) {
        var self = this, dId = this.options.dId, clicked = this.options.clicked;
        var linkage = BI.Utils.getWidgetLinkageByID(wid);
        BI.each(linkage, function (i, link) {
            BI.Broadcasts.send(BICst.BROADCAST.LINKAGE_PREFIX + link.to, dId, clicked);
            self._send2AllChildLinkWidget(link.to);
        });
    }
});
$.shortcut("bi.target_body_normal_cell", BI.TargetBodyNormalCell);