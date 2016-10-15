/**
 * @class BI.AccumulateRadarChartSetting
 * @extends BI.Widget
 * 堆积雷达样式
 */
BI.AccumulateRadarChartSetting = BI.inherit(BI.AbstractChartSetting, {

    _defaultConfig: function () {
        return BI.extend(BI.AccumulateRadarChartSetting.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-charts-setting bi-accumulate-radar-chart-setting"
        })
    },

    _init: function () {
        BI.AccumulateRadarChartSetting.superclass._init.apply(this, arguments);
        var self = this, o = this.options, constant = BI.AbstractChartSetting;

        //显示组件标题
        this.showTitle = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Show_Title"),
            cls: "attr-names",
            logic: {
                dynamic: true
            }
        });
        this.showTitle.on(BI.Controller.EVENT_CHANGE, function () {
            self.widgetTitle.setVisible(this.isSelected());
            self.fireEvent(BI.GroupTableSetting.EVENT_CHANGE);
        });

        //组件标题
        this.title = BI.createWidget({
            type: "bi.sign_editor",
            cls: "title-input",
            width: 120
        });

        this.title.on(BI.SignEditor.EVENT_CHANGE, function () {
            self.fireEvent(BI.GroupTableSetting.EVENT_CHANGE)
        });

        //详细设置
        this.titleDetailSettting = BI.createWidget({
            type: "bi.show_title_detailed_setting_combo"
        });

        this.titleDetailSettting.on(BI.ShowTitleDetailedSettingCombo.EVENT_CHANGE, function () {
            self.fireEvent(BI.GroupTableSetting.EVENT_CHANGE)
        });

        this.widgetTitle = BI.createWidget({
            type: "bi.left",
            items: [this.title, this.titleDetailSettting],
            hgap: constant.SIMPLE_H_GAP
        });

        var widgetTitle = BI.createWidget({
            type: "bi.left",
            cls: "single-line-settings",
            items: BI.createItems([{
                type: "bi.vertical_adapt",
                items: [this.showTitle]
            }, {
                type: "bi.vertical_adapt",
                items: [this.widgetTitle]
            }], {
                height: constant.SINGLE_LINE_HEIGHT
            }),
            hgap: constant.SIMPLE_H_GAP
        });

        this.colorSelect = BI.createWidget({
            type: "bi.chart_setting_select_color_combo",
            width: 130
        });
        this.colorSelect.populate();

        this.colorSelect.on(BI.ChartSettingSelectColorCombo.EVENT_CHANGE, function () {
            self.fireEvent(BI.AccumulateRadarChartSetting.EVENT_CHANGE);
        });

        this.chartStyleGroup = BI.createWidget({
            type: "bi.button_group",
            items: BI.createItems(BICst.AXIS_STYLE_GROUP, {
                type: "bi.icon_button",
                extraCls: "chart-style-font",
                width: constant.BUTTON_WIDTH,
                height: constant.BUTTON_HEIGHT,
                iconWidth: constant.ICON_WIDTH,
                iconHeight: constant.ICON_HEIGHT
            }),
            layouts: [{
                type: "bi.vertical_adapt",
                height: constant.SINGLE_LINE_HEIGHT
            }]
        });
        this.chartStyleGroup.on(BI.ButtonGroup.EVENT_CHANGE, function () {
            self.fireEvent(BI.AccumulateRadarChartSetting.EVENT_CHANGE);
        });

        this.chartTypeGroup = BI.createWidget({
            type: "bi.button_group",
            items: BI.createItems(BICst.ACC_RADAR_CHART_STYLE_GROUP, {
                type: "bi.icon_button",
                extraCls: "chart-style-font",
                width: constant.BUTTON_WIDTH,
                height: constant.BUTTON_HEIGHT,
                iconWidth: constant.ICON_WIDTH,
                iconHeight: constant.ICON_HEIGHT
            }),
            layouts: [{
                type: "bi.vertical_adapt",
                height: constant.SINGLE_LINE_HEIGHT
            }]
        });
        this.chartTypeGroup.on(BI.ButtonGroup.EVENT_CHANGE, function () {
            self.fireEvent(BI.AccumulateRadarChartSetting.EVENT_CHANGE);
        });

        //组件背景
        this.widgetBackground = BI.createWidget({
            type: "bi.global_style_index_background"
        });
        this.widgetBackground.on(BI.GlobalStyleIndexBackground.EVENT_CHANGE, function () {
            self.fireEvent(BI.GroupTableSetting.EVENT_CHANGE);
        });

        var tableChart = BI.createWidget({
            type: "bi.horizontal_adapt",
            columnSize: [80],
            cls: "single-line-settings",
            items: [{
                type: "bi.label",
                text: BI.i18nText("BI-Chart"),
                lgap: constant.SIMPLE_H_LGAP,
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
                    type: "bi.vertical_adapt",
                    items: [this.colorSelect]
                }, {
                    type: "bi.label",
                    text: BI.i18nText("BI-Table_Style"),
                    cls: "attr-names"
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.chartStyleGroup]
                }, {
                    type: "bi.label",
                    text: BI.i18nText("BI-Type"),
                    cls: "attr-names"
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.chartTypeGroup]
                }, {
                    type: "bi.label",
                    text: BI.i18nText("BI-Widget_Background_Colour"),
                    cls: "line-title"
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.widgetBackground]
                }], {
                    height: constant.SINGLE_LINE_HEIGHT
                }),
                lgap: constant.SIMPLE_H_GAP
            }]
        });

        //数量级
        this.numberLevellY = BI.createWidget({
            type: "bi.segment",
            width: constant.NUMBER_LEVEL_SEGMENT_WIDTH,
            height: constant.BUTTON_HEIGHT,
            items: BICst.TARGET_STYLE_LEVEL
        });

        this.numberLevellY.on(BI.Segment.EVENT_CHANGE, function () {
            self.fireEvent(BI.AccumulateRadarChartSetting.EVENT_CHANGE);
        });

        //单位
        this.LYUnit = BI.createWidget({
            type: "bi.sign_editor",
            width: constant.EDITOR_WIDTH,
            height: constant.EDITOR_HEIGHT,
            cls: "unit-input",
            watermark: BI.i18nText("BI-Custom_Input")
        });

        this.LYUnit.on(BI.SignEditor.EVENT_CONFIRM, function () {
            self.fireEvent(BI.AxisChartsSetting.EVENT_CHANGE);
        });

        //格式
        this.lYAxisStyle = BI.createWidget({
            type: "bi.segment",
            width: constant.FORMAT_SEGMENT_WIDTH,
            height: constant.BUTTON_HEIGHT,
            items: BICst.TARGET_STYLE_FORMAT
        });

        this.lYAxisStyle.on(BI.Segment.EVENT_CHANGE, function () {
            self.fireEvent(BI.AccumulateRadarChartSetting.EVENT_CHANGE);
        });

        //千分符
        this.separators = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Separators"),
            width: 80
        });

        this.separators.on(BI.Controller.EVENT_CHANGE, function () {
            self.fireEvent(BI.AccumulateRadarChartSetting.EVENT_CHANGE);
        });

        //lValueAxisLabel
        this.lValueAxisLabel = BI.createWidget({
            type: "bi.chart_label_detailed_setting_combo"
        });

        this.lValueAxisLabel.on(BI.ChartLabelDetailedSettingCombo.EVENT_CHANGE, function() {
            self.fireEvent(BI.AccumulateRadarChartSetting.EVENT_CHANGE)
        });

        //lValueAxisLineColor
        this.lValueAxisLineColor = BI.createWidget({
            type: "bi.color_chooser",
            width: 30,
            height: 30

        });

        this.lValueAxisLineColor.on(BI.ColorChooser.EVENT_CHANGE, function() {
            sel.fireEvent(BI.AccumulateRadarChartSetting.EVENT_CHANGE)
        });

        //轴刻度自定义
        this.showCustomScale = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Scale_Customize"),
            width: 115
        });

        this.showCustomScale.on(BI.Controller.EVENT_CHANGE, function (v) {
            self.customScale.setVisible(this.isSelected());
            if (!this.isSelected()) {
                self.customScale.setValue({})
            }
            self.fireEvent(BI.AccumulateRadarChartSetting.EVENT_CHANGE)
        });

        this.customScale = BI.createWidget({
            type: "bi.custom_scale",
            wId: o.wId
        });

        this.customScale.on(BI.CustomScale.EVENT_CHANGE, function () {
            self.fireEvent(BI.AccumulateRadarChartSetting.EVENT_CHANGE)
        });

        var lYAxis = BI.createWidget({
            type: "bi.horizontal_adapt",
            columnSize: [80],
            cls: "single-line-settings",
            items: [{
                type: "bi.label",
                textHeight: constant.SINGLE_LINE_HEIGHT,
                lgap: constant.SIMPLE_H_LGAP,
                textAlign: "left",
                text: BI.i18nText("BI-Value_Axis"),
                cls: "line-title"
            }, {
                type: "bi.left",
                cls: "detail-style",
                items: BI.createItems([{
                    type: "bi.label",
                    text: BI.i18nText("BI-Num_Level"),
                    cls: "attr-names"
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.numberLevellY]
                }, {
                    type: "bi.label",
                    text: BI.i18nText("BI-Unit_Normal"),
                    cls: "attr-names"
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.LYUnit]
                }, {
                    type: "bi.label",
                    text: BI.i18nText("BI-Format"),
                    cls: "attr-names"
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.lYAxisStyle]
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.separators]
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.lValueAxisLabel]
                }, {
                    type: "bi.label",
                    text: BI.i18nText("BI-Axis_Line_Color"),
                    cls: "attr-names"
                }, {
                    type: "bi.vertical_adapt",
                    items:[this.lValueAxisLabel]
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.showCustomScale]
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.showCustomScale]
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.customScale]
                }], {
                    height: constant.SINGLE_LINE_HEIGHT
                }),
                lgap: constant.SIMPLE_H_GAP
            }]
        });

        //图例
        this.legend = BI.createWidget({
            type: "bi.segment",
            width: constant.LEGEND_SEGMENT_WIDTH,
            height: constant.BUTTON_HEIGHT,
            items: BICst.CHART_LEGEND
        });

        this.legend.on(BI.Segment.EVENT_CHANGE, function () {
            self.fireEvent(BI.AccumulateRadarChartSetting.EVENT_CHANGE);
        });

        //数据标签
        this.showDataLabel = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Show_Data_Label"),
            width: 115
        });

        this.showDataLabel.on(BI.Controller.EVENT_CHANGE, function () {
            self.fireEvent(BI.AccumulateRadarChartSetting.EVENT_CHANGE);
        });

        //网格线
        this.gridLine = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Show_Grid_Line"),
            width: 115
        });

        this.gridLine.on(BI.Controller.EVENT_CHANGE, function () {
            self.fireEvent(BI.AccumulateRadarChartSetting.EVENT_CHANGE);
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
                textHeight: constant.SINGLE_LINE_HEIGHT,
                cls: "line-title"
            }, {
                type: "bi.left",
                cls: "detail-style",
                items: BI.createItems([{
                    type: "bi.label",
                    text: BI.i18nText("BI-Legend_Normal"),
                    cls: "attr-names"
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.legend]
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.showDataLabel]
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.gridLine]
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
            type: "bi.left_right_vertical_adapt",
            cls: "single-line-settings",
            items: {
                left: [{
                    type: "bi.label",
                    text: BI.i18nText("BI-Interactive_Attr"),
                    cls: "line-title"
                }, this.transferFilter]
            },
            height: constant.SINGLE_LINE_HEIGHT,
            lhgap: constant.SIMPLE_H_GAP
        });

        BI.createWidget({
            type: "bi.vertical",
            element: this.element,
            items: [widgetTitle, tableChart, lYAxis, showElement, otherAttr],
            hgap: 10
        })
    },

    populate: function () {
        var wId = this.options.wId;
        this.showTitle.setSelected(BI.Utils.getWSShowNameByID(wId));
        this.widgetTitle.setVisible(BI.Utils.getWSShowNameByID(wId));
        this.title.setValue(BI.Utils.getWidgetNameByID(wId));
        this.titleDetailSettting.setValue(BI.Utils.getWSTitleDetailSettingByID(wId));
        this.widgetBackground.setValue(BI.Utils.getWSWidgetBGByID(wId));
        this.transferFilter.setSelected(BI.Utils.getWSTransferFilterByID(wId));
        this.colorSelect.setValue(BI.Utils.getWSChartColorByID(wId));
        this.chartStyleGroup.setValue(BI.Utils.getWSChartStyleByID(wId));
        this.chartTypeGroup.setValue(BI.Utils.getWSChartRadarTypeByID(wId));
        this.lYAxisStyle.setValue(BI.Utils.getWSLeftYAxisStyleByID(wId));
        this.numberLevellY.setValue(BI.Utils.getWSLeftYAxisNumLevelByID(wId));
        this.legend.setValue(BI.Utils.getWSChartLegendByID(wId));
        this.showDataLabel.setSelected(BI.Utils.getWSShowDataLabelByID(wId));
        this.gridLine.setSelected(BI.Utils.getWSShowGridLineByID(wId));
        this.separators.setSelected(BI.Utils.getWSNumberSeparatorsByID(wId));
        this.showCustomScale.setSelected(BI.Utils.getWSShowYCustomScale(wId));
        this.customScale.setValue(BI.Utils.getWSCustomYScale(wId));
        this.customScale.setVisible(BI.Utils.getWSShowYCustomScale(wId));
    },

    getValue: function () {
        return {
            show_name: this.showTitle.isSelected(),
            widget_title: this.title.getValue(),
            title_detail: this.titleDetailSettting.getValue(),
            widget_bg: this.widgetBackground.getValue(),
            transfer_filter: this.transferFilter.isSelected(),
            chart_color: this.colorSelect.getValue()[0],
            chart_style: this.chartStyleGroup.getValue()[0],
            chart_radar_type: this.chartTypeGroup.getValue()[0],
            left_y_axis_style: this.lYAxisStyle.getValue()[0],
            left_y_axis_number_level: this.numberLevellY.getValue()[0],
            chart_legend: this.legend.getValue()[0],
            show_data_label: this.showDataLabel.isSelected(),
            show_grid_line: this.gridLine.isSelected(),
            show_y_custom_scale: this.showCustomScale.isSelected(),
            custom_y_scale: this.customScale.getValue(),
            num_separators: this.separators.isSelected()
        }
    },

    setValue: function (v) {
        this.showTitle.setSelected(v.show_name);
        this.title.setValue(v.widget_title);
        this.titleDetailSettting.setValue(v.title_detail);
        this.widgetBackground.setValue(v.widget_bg);
        this.transferFilter.setSelected(v.transfer_filter);
        this.colorSelect.setValue(v.chart_color);
        this.chartStyleGroup.setValue(v.chart_style);
        this.chartTypeGroup.setValue(v.chart_radar_type);
        this.lYAxisStyle.setValue(v.left_y_axis_style);
        this.numberLevellY.setValue(v.left_y_axis_number_level);
        this.legend.setValue(v.chart_legend);
        this.showDataLabel.setSelected(v.show_data_label);
        this.gridLine.setSelected(v.show_grid_line);
        this.showCustomScale.setSelected(v.show_y_custom_scale);
        this.customScale.setValue(v.custom_y_scale)
        this.separators.setSelected(v.num_separators)
    }
});
BI.AccumulateRadarChartSetting.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.accumulate_radar_chart_setting", BI.AccumulateRadarChartSetting);
