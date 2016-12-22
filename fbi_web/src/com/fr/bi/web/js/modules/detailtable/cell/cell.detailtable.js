/**
 * Created by roy on 16/5/23.
 */
BI.DetailTableCell = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.DetailTableCell.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-detail-table-cell",
            dId: "",
            text: ""
        })
    },

    _init: function () {
        BI.DetailTableCell.superclass._init.apply(this, arguments);
        this._createItem();
    },

    _createItem: function () {
        var o = this.options;
        var dId = this.options.dId;
        var type = BI.Utils.getDimensionTypeByID(dId);
        if (this._checkHyperLinkDimension()) {
            var hyperlink = BI.Utils.getDimensionHyperLinkByID(dId);
            var expression = BI.Func.formatAddress(hyperlink.expression);
            var item = BI.createWidget({
                type: "bi.text_button",
                cls: "hyper-link-item",
                textAlign: (type === BICst.TARGET_TYPE.NUMBER || type === BICst.TARGET_TYPE.FORMULA) ? "right" : "left",
                height: o.height,
                text: o.text,
                title: o.text,
                lgap: 5,
                rgap: 5
            });
            item.on(BI.TextButton.EVENT_CHANGE, function () {
                window.open(expression.replaceAll("\\$\\{.*\\}", o.text));
            });
        } else {
            var item = BI.createWidget({
                type: "bi.label",
                cls: "detail-table-cell-text",
                textAlign: (type === BICst.TARGET_TYPE.NUMBER || type === BICst.TARGET_TYPE.FORMULA) ? "right" : "left",
                whiteSpace: "nowrap",
                height: this.options.height,
                text: this.options.text,
                title: this.options.text,
                value: this.options.value,
                lgap: 5,
                rgap: 5
            });
        }
        this._createItemWithStyle(item);
    },

    _checkHyperLinkDimension: function () {
        var hyperlink = BI.Utils.getDimensionHyperLinkByID(this.options.dId);
        return hyperlink.used || false
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

    _createItemWithStyle: function (item) {
        var o = this.options;
        var iconCls = "", color = "";
        var text = o.text;
        var dId = this.options.dId;
        var styleSettings = BI.Utils.getDimensionSettingsByID(dId);
        var type = BI.Utils.getDimensionTypeByID(dId);

        var format = styleSettings.format, numLevel = styleSettings.numLevel,
            iconStyle = styleSettings.iconStyle, mark = styleSettings.mark,
            numSeparators = styleSettings.numSeparators;
        text = BI.TargetBodyNormalCell.parseNumByLevel(text, numLevel);

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

        if (type === BICst.TARGET_TYPE.NUMBER || type === BICst.TARGET_TYPE.FORMULA) {
            text = BI.TargetBodyNormalCell.parseFloatByDot(text, format, numSeparators);
        }

        if (text === Infinity) {
            text = "N/0";
        } else if (BI.Utils.getDimensionSettingsByID(dId).num_level === BICst.TARGET_STYLE.NUM_LEVEL.PERCENT && BI.isNumeric(text)) {
            text += "%";
        }

        //日期的需要format
        var dGroup = BI.Utils.getDimensionGroupByID(o.dId);
        if (BI.isNotNull(dGroup) && BI.isNumeric(text)) {
            if (dGroup.type === BICst.GROUP.YMD) {
                var date = new Date(BI.parseInt(text));
                text = date.print("%Y-%X-%d");
            }
            if (dGroup.type === BICst.GROUP.YMDHMS) {
                var date = new Date(BI.parseInt(text));
                text = date.print("%Y-%X-%d  %H:%M:%S");
            }
            if (dGroup.type === BICst.GROUP.S) {
                text = BICst.FULL_QUARTER_NAMES[text - 1];
            }
            if (dGroup.type === BICst.GROUP.M) {
                text = BICst.FULL_MONTH_NAMES[text];
            }
            if (dGroup.type === BICst.GROUP.W) {
                text = BICst.FULL_WEEK_NAMES[text - 1];
            }
        }

        item.setText(text);
        item.setTitle(text);

        if (BI.isNotEmptyString(color)) {
            item.element.css("color", color);
        }
        if (BI.isNotEmptyString(iconCls)) {
            BI.createWidget({
                type: "bi.horizontal_adapt",
                element: this.element,
                items: [item, {
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
                items: [item]
            })
        }

    }

});
$.shortcut("bi.detail_table_cell", BI.DetailTableCell);