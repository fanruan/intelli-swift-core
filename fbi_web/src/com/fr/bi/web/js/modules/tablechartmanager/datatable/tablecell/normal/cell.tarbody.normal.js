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
        var format = styleSettings.format, numLevel = styleSettings.num_level, num_separators = styleSettings.num_separators;
        text = BI.TargetBodyNormalCell.parseNumByLevel(text, numLevel);
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
        text = BI.TargetBodyNormalCell.parseFloatByDot(text, format, num_separators);
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
                columnSize: ["", 25]
            });
        } else {
            BI.createWidget({
                type: "bi.vertical",
                element: this.element,
                items: [textLabel]
            })
        }
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
        if (BI.isNotNull(dId)) {
            var widgetId = BI.Utils.getWidgetIDByDimensionID(dId);
            var linkage = BI.Utils.getWidgetLinkageByID(widgetId);
        }
        var linkedWidgets = [], linkedFrom = [];
        BI.each(linkage, function (i, link) {
            if (link.from === dId && BI.isEmpty(link.cids)) {
                linkedWidgets.push(link);
            } else if (link.cids && link.cids.contains(dId)) {
                linkedFrom.push(link);
            }
        });

        if (text === Infinity) {
            text = "N/0";
        } else if (BI.Utils.getDimensionSettingsByID(dId).num_level === BICst.TARGET_STYLE.NUM_LEVEL.PERCENT && BI.isNotNull(text)) {
            text += "%";
        }

        if (BI.isEmptyArray(linkedWidgets) && BI.isEmptyArray(linkedFrom)) {
            return BI.createWidget({
                type: "bi.label",
                text: text,
                title: text,
                height: 25,
                cls: "target-cell-text",
                textAlign: "right",
                rgap: 5
            });
        } else {
            var textButton = BI.createWidget({
                type: "bi.text_button",
                text: text,
                title: text,
                height: 25,
                textAlign: "right",
                cls: "target-linkage-label",
                rgap: 5
            });
            //一般指标或只有一个指标联动的计算指标
            if (linkedFrom.length <= 1) {
                textButton.on(BI.TextButton.EVENT_CHANGE, function () {
                    //这个clicked应该放到子widget中保存起来
                    BI.each(linkedWidgets.concat(linkedFrom), function (i, linkWid) {
                        BI.Broadcasts.send(BICst.BROADCAST.LINKAGE_PREFIX + linkWid.to, linkWid.from, clicked);
                        self._send2AllChildLinkWidget(linkWid.to);
                    });
                });
                return textButton;
            }
            //计算指标
            var linkages = [];
            BI.each(linkedFrom, function (idx, linkage) {
                var name = BI.i18nText("BI-An");
                BI.each(linkage.cids, function (i, cid) {
                    name += BI.Utils.getDimensionNameByID(cid) + "-";
                });
                name += BI.Utils.getDimensionNameByID(linkage.from);
                linkages.push({
                    text: name,
                    title: name,
                    from: linkage.from,
                    to: linkage.to
                })
            });

            var combo = BI.createWidget({
                type: "bi.combo",
                el: textButton,
                direction: "right",
                isNeedAdjustWidth: false,
                popup: {
                    el: BI.createWidget({
                        type: "bi.vertical",
                        cls: "bi-linkage-list",
                        items: BI.createItems(linkages, {
                            type: "bi.text_button",
                            cls: "bi-linkage-list-item",
                            height: 30,
                            textAlign: "left",
                            handler: function () {
                                var link = this.options;
                                BI.Broadcasts.send(BICst.BROADCAST.LINKAGE_PREFIX + link.to, link.from, clicked);
                                self._send2AllChildLinkWidget(link.to, link.from, clicked);
                                combo.hideView();
                            },
                            lgap: 10
                        }),
                        width: 164
                    })
                }
            });
            return combo;
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
BI.extend(BI.TargetBodyNormalCell, {
    parseFloatByDot: function (text, dot, separators) {
        if (text === Infinity || text !== text) {
            return text;
        }
        if (!BI.isNumeric(text)) {
            return text;
        }
        var num = BI.parseFloat(text);
        switch (dot) {
            case BICst.TARGET_STYLE.FORMAT.NORMAL:
                if (separators) {
                    num = BI.contentFormat(num, '#,###.##;-#,###.##')
                } else {
                    num = BI.contentFormat(num, '#.##;-#.##')
                }
                return num;
                break;
            case BICst.TARGET_STYLE.FORMAT.ZERO2POINT:
                if (separators) {
                    num = BI.contentFormat(num, '#,###;-#,###')
                } else {
                    num = BI.parseInt(num)
                }
                return num;
                break;
            case BICst.TARGET_STYLE.FORMAT.ONE2POINT:
                if (separators) {
                    num = BI.contentFormat(num, '#,###.0;-#,###.0')
                } else {
                    num = BI.contentFormat(num, '#.0;-#.0')
                }
                return num;
            case BICst.TARGET_STYLE.FORMAT.TWO2POINT:
                if (separators) {
                    num = BI.contentFormat(num, '#,###.00;-#,###.00')
                } else {
                    num = BI.contentFormat(num, '#.00;-#.00')
                }
                return num;
        }
        return BI.contentFormat(num, '#.##;-#.##');
    },
    parseNumByLevel: function (text, numLevel) {
        if (text === Infinity || text !== text || !BI.isNumber(text)) {
            return text;
        }
        switch (numLevel) {
            case BICst.TARGET_STYLE.NUM_LEVEL.TEN_THOUSAND:
                return text.div(10000);
            case BICst.TARGET_STYLE.NUM_LEVEL.MILLION:
                return text.div(1000000);
            case BICst.TARGET_STYLE.NUM_LEVEL.YI:
                return text.div(100000000);
            case BICst.TARGET_STYLE.NUM_LEVEL.PERCENT:
                return text * 100;
            default:
                return text;
        }
    }
});
