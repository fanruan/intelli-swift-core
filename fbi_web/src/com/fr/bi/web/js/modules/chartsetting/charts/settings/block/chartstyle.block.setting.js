/**
 * 图的配色和样式区
 * Created by AstronautOO7 on 2016/10/13.
 */
BI.ChartStyleBlockSetting = BI.inherit(BI.Widget, {
    _defaultConfig: function() {
        return BI.extend(BI.ChartStyleBlockSetting.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-chart-style-block-setting bi-charts-setting"
        })
    },

    _init: function() {
        BI.ChartStyleBlockSetting.superclass._init.apply(this, arguments);
        var self = this;

        this.colorSelect = BI.createWidget({
            type: "bi.chart_setting_select_color_combo",
            width: 130
        });
        this.colorSelect.populate();

        this.colorSelect.on(BI.ChartSettingSelectColorCombo.EVENT_CHANGE, function () {
            self.fireEvent(BI.ChartStyleBlockSetting.EVENT_CHANGE);
        });

        //风格——1、2
        this.chartStyleGroup = BI.createWidget({
            type: "bi.button_group",
            items: BI.createItems(BICst.AXIS_STYLE_GROUP, {
                type: "bi.icon_button",
                extraCls: "chart-style-font",
                width: 40,
                height: 30,
                iconWidth: 24,
                iconHeight: 24
            }),
            layouts: [{
                type: "bi.vertical_adapt",
                height: 58
            }]
        });
        this.chartStyleGroup.on(BI.ButtonGroup.EVENT_CHANGE, function () {
            self.fireEvent(BI.ChartStyleBlockSetting.EVENT_CHANGE);
        });

        BI.createWidget({
            type: "bi.horizontal_adapt",
            columnSize: [100],
            cls: "single-line-settings",
            element: this.element,
            items: [{
                type: "bi.label",
                text: BI.i18nText("BI-Chart"),
                lgap: 5,
                textAlign: "left",
                cls: "line-title"
            }, {
                type: "bi.left",
                cls: "detail-style",
                items: BI.createItems([{
                    type: "bi.label",
                    text: BI.i18nText("BI-Color_Setting"),
                    cls: "attr-names"
                }, {
                    el: {
                        type: "bi.vertical_adapt",
                        items: [this.colorSelect]
                    },
                    lgap: 10
                }, {
                    type: "bi.label",
                    text: BI.i18nText("BI-Table_Style"),
                    cls: "attr-names",
                    lgap: 10
                }, {
                    el: {
                        type: "bi.vertical_adapt",
                        items: [this.chartStyleGroup]
                    },
                    lgap: 10
                }], {
                    height: 58
                })
            }]
        });
    },

    populate: function() {
        var wId = this.options.wId;

    },

    getValue: function() {
        return {

        }
    },

    setValue: function(v) {

    }

});
BI.ChartStyleBlockSetting.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.chart_style_block_setting", BI.ChartStyleBlockSetting);