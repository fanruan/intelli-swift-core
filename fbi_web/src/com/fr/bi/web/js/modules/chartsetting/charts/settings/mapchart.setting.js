/**
 * @class BI.MapSetting
 * @extends BI.Widget
 * 柱状，堆积柱状，组合图样式
 */
BI.MapSetting = BI.inherit(BI.AbstractChartSetting, {

    _defaultConfig: function () {
        return BI.extend(BI.MapSetting.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-charts-setting"
        })
    },

    _init: function () {
        BI.MapSetting.superclass._init.apply(this, arguments);
        var self = this, constant = BI.AbstractChartSetting;

        //主题颜色
        this.colorChooser = BI.createWidget({
            type: "bi.color_chooser",
            width: constant.BUTTON_HEIGHT,
            height: constant.BUTTON_HEIGHT
        });

        this.colorChooser.on(BI.ColorChooser.EVENT_CHANGE, function () {
            self.fireEvent(BI.MapSetting.EVENT_CHANGE);
        });

        var theme = BI.createWidget({
            type: "bi.horizontal_adapt",
            columnSize: [80],
            cls: "single-line-settings",
            items: [{
                type: "bi.label",
                text: BI.i18nText("BI-Chart_Table_Style"),
                lgap: 5,
                textAlign: "left",
                textHeight: 60,
                cls: "line-title"
            }, {
                type: "bi.left",
                cls: "detail-style",
                items: BI.createItems([{
                    type: "bi.label",
                    text: BI.i18nText("BI-Theme_Color"),
                    lgap: 0,
                    cls: "attr-names"
                }, {
                    type: "bi.center_adapt",
                    items: [this.colorChooser]
                }], {
                    height: constant.SINGLE_LINE_HEIGHT
                }),
                lgap: constant.SIMPLE_H_GAP
            }]
        });

        this.styleRadio = BI.createWidget({
            type: "bi.button_group",
            items: BI.createItems(BICst.CHART_SCALE_SETTING, {
                type: "bi.single_select_radio_item",
                width: 100,
                height: constant.BUTTON_HEIGHT
            }),
            layouts: [{
                type: "bi.horizontal_adapt",
                height: constant.BUTTON_HEIGHT
            }]
        });

        this.styleRadio.on(BI.ButtonGroup.EVENT_CHANGE, function (v) {
            self._doClickButton(v);
            self.fireEvent(BI.MapSetting.EVENT_CHANGE)
        });

        this.addConditionButton = BI.createWidget({
            type: "bi.button",
            text: BI.i18nText("BI-Add_Condition"),
            height: constant.BUTTON_HEIGHT
        });

        this.addConditionButton.on(BI.Button.EVENT_CHANGE, function () {
            self.conditions.addItem();
            self.fireEvent(BI.MapSetting.EVENT_CHANGE)
        });

        this.conditions = BI.createWidget({
            type: "bi.chart_add_condition_group",
            width: "100%"
        });

        this.conditions.on(BI.ChartAddConditionGroup.EVENT_CHANGE, function () {
            self.fireEvent(BI.MapSetting.EVENT_CHANGE)
        });

        var interval = BI.createWidget({
            type: "bi.left",
            cls: "detail-style",
            items: BI.createItems([{
                type: "bi.center_adapt",
                items: [this.styleRadio]
            }, {
                type: "bi.center_adapt",
                items: [this.addConditionButton]
            }, this.conditions], {
                height: constant.SINGLE_LINE_HEIGHT
            }),
            lgap: constant.SIMPLE_H_GAP
        });

        var intervalSetting = BI.createWidget({
            type: "bi.horizontal_adapt",
            cls: "single-line-settings",
            columnSize: [80],
            verticalAlign: "top",
            items: [{
                type: "bi.label",
                text: BI.i18nText("BI-Interval_Setting"),
                textHeight: constant.SINGLE_LINE_HEIGHT,
                textAlign: "left",
                lgap: 5,
                cls: "line-title"
            }, interval]
        });

        //图例
        this.legend = BI.createWidget({
            type: "bi.segment",
            width: constant.LEGEND_SEGMENT_WIDTH,
            height: constant.BUTTON_HEIGHT,
            items: BICst.CHART_LEGEND
        });

        this.legend.on(BI.Segment.EVENT_CHANGE, function () {
            self.fireEvent(BI.MapSetting.EVENT_CHANGE);
        });

        //数据标签
        this.showDataLabel = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Show_Data_Label"),
            width: 115
        });

        this.showDataLabel.on(BI.Controller.EVENT_CHANGE, function () {
            self.fireEvent(BI.MapSetting.EVENT_CHANGE);
        });

        var showElement = BI.createWidget({
            type: "bi.horizontal_adapt",
            columnSize: [80],
            cls: "single-line-settings",
            items: [{
                type: "bi.label",
                text: BI.i18nText("BI-Element_Show"),
                lgap: constant.SIMPLE_H_LGAP,
                textAlign: "left",
                textHeight: 60,
                cls: "line-title"
            }, {
                type: "bi.left",
                cls: "detail-style",
                items: BI.createItems([{
                    type: "bi.label",
                    text: BI.i18nText("BI-Legend_Normal"),
                    lgap: constant.SIMPLE_H_GAP,
                    cls: "attr-names"
                }, {
                    type: "bi.center_adapt",
                    items: [this.legend]
                }, {
                    type: "bi.center_adapt",
                    items: [this.showDataLabel]
                }], {
                    height: constant.SINGLE_LINE_HEIGHT
                }),
                lgap: constant.SIMPLE_H_GAP
            }]
        });

        //联动传递指标过滤条件
        this.transferFilter = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Bind_Target_Condition"),
            width: 170
        });
        this.transferFilter.on(BI.Controller.EVENT_CHANGE, function () {
            self.fireEvent(BI.GroupTableSetting.EVENT_CHANGE);
        });

        var otherAttr = BI.createWidget({
            type: "bi.left",
            cls: "single-line-settings",
            items: BI.createItems([{
                type: "bi.label",
                text: BI.i18nText("BI-Interactive_Attr"),
                cls: "line-title",
                lgap: constant.SIMPLE_H_GAP
            }, {
                type: "bi.center_adapt",
                items: [this.transferFilter],
                lgap: 30
            }], {
                height: constant.SINGLE_LINE_HEIGHT
            })
        });

        BI.createWidget({
            type: "bi.vertical",
            element: this.element,
            items: [theme, intervalSetting, showElement, otherAttr],
            hgap: constant.SIMPLE_H_GAP
        })
    },

    _doClickButton: function (v) {
        switch (v) {
            case BICst.SCALE_SETTING.AUTO:
                this.addConditionButton.setVisible(false);
                this.conditions.setVisible(false);
                break;
            case BICst.SCALE_SETTING.CUSTOM:
                this.addConditionButton.setVisible(true);
                this.conditions.setVisible(true);
                break;
        }
    },

    _setNumberLevel: function () {
        var wId = this.options.wId;
        var targetIDs = BI.Utils.getAllUsableTargetDimensionIDs(wId);
        var styleSettings = BI.Utils.getDimensionSettingsByID(targetIDs[0]);
        switch (styleSettings.num_level) {
            case BICst.TARGET_STYLE.NUM_LEVEL.NORMAL:
                this.conditions.setNumTip("");
                break;
            case BICst.TARGET_STYLE.NUM_LEVEL.TEN_THOUSAND:
                this.conditions.setNumTip(BI.i18nText("BI-Wan"));
                break;
            case BICst.TARGET_STYLE.NUM_LEVEL.MILLION:
                this.conditions.setNumTip(BI.i18nText("BI-Million"));
                break;
            case BICst.TARGET_STYLE.NUM_LEVEL.YI:
                this.conditions.setNumTip(BI.i18nText("BI-Yi"));
                break;
            case BICst.TARGET_STYLE.NUM_LEVEL.PERCENT:
                this.conditions.setNumTip(BI.i18nText("%"));
                break;
        }
    },

    populate: function () {
        var wId = this.options.wId;
        this.colorChooser.setValue(BI.Utils.getWSThemeColorByID(wId));
        this.styleRadio.setValue(BI.Utils.getWSScaleByID(wId));
        this._doClickButton(BI.Utils.getWSScaleByID(wId));
        this.conditions.setValue(BI.Utils.getWSMapStylesByID(wId));
        this.transferFilter.setSelected(BI.Utils.getWSTransferFilterByID(wId));
        this.legend.setValue(BI.Utils.getWSChartLegendByID(wId));
        this.showDataLabel.setSelected(BI.Utils.getWSShowDataLabelByID(wId));
        this._setNumberLevel()
    },

    getValue: function () {
        return {
            theme_color: this.colorChooser.getValue(),
            auto_custom: this.styleRadio.getValue()[0],
            map_styles: this.conditions.getValue(),
            transfer_filter: this.transferFilter.isSelected(),
            chart_legend: this.legend.getValue()[0],
            show_data_label: this.showDataLabel.isSelected()
        }
    },

    setValue: function (v) {
        this.colorChooser.setValue(v.theme_color);
        this.styleRadio.setValue(v.auto_custom);
        this._doClickButton(v.auto_custom);
        this.conditions.setValue(v.map_styles);
        this.transferFilter.setSelected(v.transfer_filter);
        this.legend.setValue(v.chart_legend);
        this.showDataLabel.setSelected(v.show_data_label);
        this._setNumberLevel()
    }
});
BI.MapSetting.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.map_setting", BI.MapSetting);