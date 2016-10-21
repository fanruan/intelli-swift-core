/**
 * @class BI.MapSetting
 * @extends BI.Widget
 * 地图样式
 */
BI.MapSetting = BI.inherit(BI.AbstractChartSetting, {

    _defaultConfig: function () {
        return BI.extend(BI.MapSetting.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-charts-setting bi-map-chart-setting"
        })
    },

    _init: function () {
        BI.MapSetting.superclass._init.apply(this, arguments);
        var self = this, constant = BI.AbstractChartSetting;

        //显示组件标题
        this.showTitle = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Show_Chart_Title"),
            cls: "attr-names",
            logic: {
                dynamic: true
            }
        });
        this.showTitle.on(BI.Controller.EVENT_CHANGE, function () {
            self.widgetTitle.setVisible(this.isSelected());
            self.fireEvent(BI.MapSetting.EVENT_CHANGE);
        });

        //组件标题
        this.title = BI.createWidget({
            type: "bi.sign_editor",
            cls: "title-input",
            width: 120
        });

        this.title.on(BI.SignEditor.EVENT_CHANGE, function () {
            self.fireEvent(BI.MapSetting.EVENT_CHANGE)
        });

        //详细设置
        this.titleDetailSettting = BI.createWidget({
            type: "bi.show_title_detailed_setting_combo"
        });

        this.titleDetailSettting.on(BI.ShowTitleDetailedSettingCombo.EVENT_CHANGE, function () {
            self.fireEvent(BI.MapSetting.EVENT_CHANGE)
        });

        this.widgetTitle = BI.createWidget({
            type: "bi.left",
            items: [this.title, this.titleDetailSettting],
            hgap: constant.SIMPLE_H_GAP
        });

        //组件背景
        this.widgetBackground = BI.createWidget({
            type: "bi.global_style_index_background"
        });
        this.widgetBackground.on(BI.GlobalStyleIndexBackground.EVENT_CHANGE, function () {
            self.fireEvent(BI.MapSetting.EVENT_CHANGE);
        });

        var widgetTitle = BI.createWidget({
            type: "bi.left",
            cls: "single-line-settings",
            items: BI.createItems([{
                type: "bi.label",
                text: BI.i18nText("BI-Component_Widget"),
                cls: "line-title",
            }, {
                type: "bi.label",
                text: BI.i18nText("BI-Title"),
                cls: "line-title",
                lgap: 38
            }, {
                type: "bi.vertical_adapt",
                items: [this.showTitle]
            }, {
                type: "bi.vertical_adapt",
                items: [this.widgetTitle]
            }, {
                type: "bi.label",
                text: BI.i18nText("BI-Background"),
                cls: "line-title",
            },{
                type: "bi.vertical_adapt",
                items: [this.widgetBackground]
            }], {
                height: constant.SINGLE_LINE_HEIGHT
            }),
            hgap: constant.SIMPLE_H_GAP
        });

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
                    type: "bi.vertical_adapt",
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
                type: "bi.vertical_adapt",
                items: [this.styleRadio]
            }, {
                type: "bi.vertical_adapt",
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

        //显示背景图层
        this.isShowBackgroundLayer = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-SHOW_BACKGROUND_LAYER"),
            width: 110
        });

        this.isShowBackgroundLayer.on(BI.Controller.EVENT_CHANGE, function () {
            this.isSelected() ? self.selectLayerCombo.setVisible(true) : self.selectLayerCombo.setVisible(false);
            self.fireEvent(BI.MapSetting.EVENT_CHANGE);
        });

        this.selectLayerCombo = BI.createWidget({
            type: "bi.text_value_combo",
            width: constant.COMBO_WIDTH,
            height: constant.EDITOR_HEIGHT
        });
        this.selectLayerCombo.on(BI.TextValueCombo.EVENT_CHANGE, function () {
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
                    type: "bi.vertical_adapt",
                    items: [this.legend]
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.showDataLabel]
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.isShowBackgroundLayer]
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.selectLayerCombo]
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
            self.fireEvent(BI.MapSetting.EVENT_CHANGE);
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
                type: "bi.vertical_adapt",
                items: [this.transferFilter],
                lgap: 30
            }], {
                height: constant.SINGLE_LINE_HEIGHT
            })
        });

        BI.createWidget({
            type: "bi.vertical",
            element: this.element,
            items: [widgetTitle, theme, intervalSetting, showElement, otherAttr],
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
        this.showTitle.setSelected(BI.Utils.getWSShowNameByID(wId));
        this.widgetTitle.setVisible(BI.Utils.getWSShowNameByID(wId));
        this.title.setValue(BI.Utils.getWidgetNameByID(wId));
        this.titleDetailSettting.setValue(BI.Utils.getWSTitleDetailSettingByID(wId));
        this.widgetBackground.setValue(BI.Utils.getWSWidgetBGByID(wId));
        this.colorChooser.setValue(BI.Utils.getWSThemeColorByID(wId));
        this.styleRadio.setValue(BI.Utils.getWSScaleByID(wId));
        this._doClickButton(BI.Utils.getWSScaleByID(wId));
        this.conditions.setValue(BI.Utils.getWSMapStylesByID(wId));
        this.transferFilter.setSelected(BI.Utils.getWSTransferFilterByID(wId));
        this.legend.setValue(BI.Utils.getWSChartLegendByID(wId));
        this.showDataLabel.setSelected(BI.Utils.getWSShowDataLabelByID(wId));
        this.isShowBackgroundLayer.setSelected(BI.Utils.getWSShowBackgroundByID(wId));
        this.isShowBackgroundLayer.isSelected() ? this.selectLayerCombo.setVisible(true) : this.selectLayerCombo.setVisible(false);
        this._setNumberLevel();
        var items = BI.map(MapConst.WMS_INFO, function (name, obj) {
            if (obj.type === BICst.TILELAYER_SERVER) {
                return {
                    text: name,
                    title: name,
                    value: name
                }
            }
            if (obj.type === BICst.WMS_SERVER) {
                return {
                    text: name,
                    title: name,
                    value: name
                }
            }
        });
        this.selectLayerCombo.populate(items);
        this.selectLayerCombo.setValue(BI.Utils.getWSBackgroundLayerInfoByID(wId));
    },

    getValue: function () {
        return {
            show_name: this.showTitle.isSelected(),
            widget_title: this.title.getValue(),
            title_detail: this.titleDetailSettting.getValue(),
            widget_bg: this.widgetBackground.getValue(),
            theme_color: this.colorChooser.getValue(),
            auto_custom: this.styleRadio.getValue()[0],
            map_styles: this.conditions.getValue(),
            transfer_filter: this.transferFilter.isSelected(),
            chart_legend: this.legend.getValue()[0],
            show_data_label: this.showDataLabel.isSelected(),
            show_background_layer: this.isShowBackgroundLayer.isSelected(),
            background_layer_info: this.selectLayerCombo.getValue()[0]
        }
    },

    setValue: function (v) {
        this.showTitle.setSelected(v.show_name);
        this.title.setValue(v.widget_title);
        this.titleDetailSettting.setValue(v.title_detail);
        this.widgetBackground.setValue(v.widget_bg);
        this.colorChooser.setValue(v.theme_color);
        this.styleRadio.setValue(v.auto_custom);
        this._doClickButton(v.auto_custom);
        this.conditions.setValue(v.map_styles);
        this.transferFilter.setSelected(v.transfer_filter);
        this.legend.setValue(v.chart_legend);
        this.showDataLabel.setSelected(v.show_data_label);
        this.isShowBackgroundLayer.setSelected(v.show_background_layer);
        this.selectLayerCombo.setValue(v.background_layer_info);
        this._setNumberLevel()
    }
});
BI.MapSetting.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.map_setting", BI.MapSetting);
