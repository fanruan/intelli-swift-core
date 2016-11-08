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
        this.showName = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Show_Title"),
            cls: "attr-names",
            logic: {
                dynamic: true
            }
        });
        this.showName.on(BI.Controller.EVENT_CHANGE, function () {
            self.widgetTitle.setVisible(this.isSelected());
            self.fireEvent(BI.AccumulateRadarChartSetting.EVENT_CHANGE);
        });

        //组件标题
        this.widgetName = BI.createWidget({
            type: "bi.sign_editor",
            cls: "title-input",
            width: 120
        });
        this.widgetName.on(BI.SignEditor.EVENT_CHANGE, function () {
            self.fireEvent(BI.AccumulateRadarChartSetting.EVENT_CHANGE)
        });

        //详细设置
        this.widgetNameStyle = BI.createWidget({
            type: "bi.show_title_detailed_setting_combo"
        });
        this.widgetNameStyle.on(BI.ShowTitleDetailedSettingCombo.EVENT_CHANGE, function () {
            self.fireEvent(BI.AccumulateRadarChartSetting.EVENT_CHANGE)
        });

        this.widgetTitle = BI.createWidget({
            type: "bi.left",
            items: [this.widgetName, this.widgetNameStyle],
            hgap: constant.SIMPLE_H_GAP
        });

        var widgetTitle = BI.createWidget({
            type: "bi.left",
            cls: "single-line-settings",
            items: BI.createItems([{
                type: "bi.vertical_adapt",
                items: [this.showName]
            }, {
                type: "bi.vertical_adapt",
                items: [this.widgetTitle]
            }], {
                height: constant.SINGLE_LINE_HEIGHT
            }),
            hgap: constant.SIMPLE_H_GAP
        });

        this.chartColor = BI.createWidget({
            type: "bi.chart_setting_select_color_combo",
            width: 130
        });
        this.chartColor.populate();

        this.chartColor.on(BI.ChartSettingSelectColorCombo.EVENT_CHANGE, function () {
            self.fireEvent(BI.AccumulateRadarChartSetting.EVENT_CHANGE);
        });

        this.radarChartType = BI.createWidget({
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
        this.radarChartType.on(BI.ButtonGroup.EVENT_CHANGE, function () {
            self.fireEvent(BI.AccumulateRadarChartSetting.EVENT_CHANGE);
        });

        //组件背景
        this.widgetBG = BI.createWidget({
            type: "bi.global_style_index_background"
        });
        this.widgetBG.on(BI.GlobalStyleIndexBackground.EVENT_CHANGE, function () {
            self.fireEvent(BI.AccumulateRadarChartSetting.EVENT_CHANGE);
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
                    items: [this.chartColor]
                }, {
                    type: "bi.label",
                    text: BI.i18nText("BI-Type"),
                    cls: "attr-names"
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.radarChartType]
                }, {
                    type: "bi.label",
                    text: BI.i18nText("BI-Widget_Background_Colour"),
                    cls: "attr-names"
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.widgetBG]
                }], {
                    height: constant.SINGLE_LINE_HEIGHT
                }),
                lgap: constant.SIMPLE_H_GAP
            }]
        });

        //数量级
        this.leftYNumberLevel = BI.createWidget({
            type: "bi.segment",
            width: constant.NUMBER_LEVEL_SEGMENT_WIDTH,
            height: constant.BUTTON_HEIGHT,
            items: BICst.TARGET_STYLE_LEVEL
        });

        this.leftYNumberLevel.on(BI.Segment.EVENT_CHANGE, function () {
            self.fireEvent(BI.AccumulateRadarChartSetting.EVENT_CHANGE);
        });

        //单位
        this.leftYUnit = BI.createWidget({
            type: "bi.sign_editor",
            width: constant.EDITOR_WIDTH,
            height: constant.EDITOR_HEIGHT,
            cls: "unit-input",
            watermark: BI.i18nText("BI-Custom_Input")
        });

        this.leftYUnit.on(BI.SignEditor.EVENT_CONFIRM, function () {
            self.fireEvent(BI.AccumulateRadarChartSetting.EVENT_CHANGE);
        });

        //格式
        this.leftYNumberFormat = BI.createWidget({
            type: "bi.segment",
            width: constant.FORMAT_SEGMENT_WIDTH,
            height: constant.BUTTON_HEIGHT,
            items: BICst.TARGET_STYLE_FORMAT
        });

        this.leftYNumberFormat.on(BI.Segment.EVENT_CHANGE, function () {
            self.fireEvent(BI.AccumulateRadarChartSetting.EVENT_CHANGE);
        });

        //千分符
        this.leftYSeparator = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Separators"),
            width: 75
        });

        this.leftYSeparator.on(BI.Controller.EVENT_CHANGE, function () {
            self.fireEvent(BI.AccumulateRadarChartSetting.EVENT_CHANGE);
        });

        //显示标签
        this.leftYShowLabel = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Show_Label"),
            width: 90
        });

        this.leftYShowLabel.on(BI.Controller.EVENT_CHANGE, function () {
            self.leftYLabelStyle.setVisible(this.isSelected());
            self.fireEvent(BI.AccumulateRadarChartSetting.EVENT_CHANGE)
        });

        //左值轴标签设置
        this.leftYLabelStyle = BI.createWidget({
            type: "bi.chart_label_detailed_setting_combo"
        });

        this.leftYLabelStyle.on(BI.ChartLabelDetailedSettingCombo.EVENT_CHANGE, function () {
            self.fireEvent(BI.AccumulateRadarChartSetting.EVENT_CHANGE)
        });

        //坐直轴线颜色
        this.leftYLineColor = BI.createWidget({
            type: "bi.color_chooser",
            width: 30,
            height: 30
        });

        this.leftYLineColor.on(BI.ColorChooser.EVENT_CHANGE, function () {
            self.fireEvent(BI.AccumulateRadarChartSetting.EVENT_CHANGE)
        });

        //轴刻度自定义
        this.leftYShowCustomScale = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Scale_Customize"),
            width: 115
        });

        this.leftYShowCustomScale.on(BI.Controller.EVENT_CHANGE, function (v) {
            self.leftYCustomScale.setVisible(this.isSelected());
            if (!this.isSelected()) {
                self.leftYCustomScale.setValue({})
            }
            self.fireEvent(BI.AccumulateRadarChartSetting.EVENT_CHANGE)
        });

        this.leftYCustomScale = BI.createWidget({
            type: "bi.custom_scale",
            wId: o.wId
        });

        this.leftYCustomScale.on(BI.CustomScale.EVENT_CHANGE, function () {
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
                    items: [this.leftYNumberLevel]
                }, {
                    type: "bi.label",
                    text: BI.i18nText("BI-Unit_Normal"),
                    cls: "attr-names"
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.leftYUnit]
                }, {
                    type: "bi.label",
                    text: BI.i18nText("BI-Format"),
                    cls: "attr-names"
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.leftYNumberFormat]
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.leftYSeparator]
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.leftYShowLabel]
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.leftYLabelStyle]
                }, {
                    type: "bi.label",
                    text: BI.i18nText("BI-Axis_Line_Color"),
                    cls: "attr-names"
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.leftYLineColor]
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.leftYShowCustomScale]
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.leftYCustomScale]
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

        //图例详细设置
        this.legendStyle = BI.createWidget({
            type: "bi.legend_detailed_setting_combo"
        });

        this.legendStyle.on(BI.LegendDetailedSettingCombo.EVENT_CHANGE, function () {
            self.fireEvent(BI.AccumulateRadarChartSetting.EVENT_CHANGE)
        });

        //显示横向网格线
        this.showHGridLine = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Horizontal"),
            width: 60
        });

        this.showHGridLine.on(BI.Controller.EVENT_CHANGE, function () {
            self.hGridLineColor.setVisible(this.isSelected());
            self.fireEvent(BI.AccumulateRadarChartSetting.EVENT_CHANGE)
        });

        //横向网格线颜色
        this.hGridLineColor = BI.createWidget({
            type: "bi.color_chooser",
            width: 30,
            height: 30
        });

        this.hGridLineColor.on(BI.ColorChooser.EVENT_CHANGE, function () {
            self.fireEvent(BI.AccumulateRadarChartSetting.EVENT_CHANGE)
        });

        //显示纵向网格线
        this.showVGridLine = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Vertical"),
            width: 60
        });

        this.showVGridLine.on(BI.Controller.EVENT_CHANGE, function () {
            self.vGridLineColor.setVisible(this.isSelected());
            self.fireEvent(BI.AccumulateRadarChartSetting.EVENT_CHANGE)
        });

        //横向网格线颜色
        this.vGridLineColor = BI.createWidget({
            type: "bi.color_chooser",
            width: 30,
            height: 30
        });

        this.vGridLineColor.on(BI.ColorChooser.EVENT_CHANGE, function () {
            self.fireEvent(BI.AccumulateRadarChartSetting.EVENT_CHANGE)
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

        //数据点提示详细设置
        this.tooltipStyle = BI.createWidget({
            type: "bi.tooltip_detailed_setting_combo"
        });

        this.tooltipStyle.on(BI.TooltipDetailedSettingCombo.EVENT_CHANGE, function () {
            self.fireEvent(BI.AccumulateRadarChartSetting.EVENT_CHANGE)
        });

        //空值连续
        this.nullContinuity = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Null_Continue"),
            width: 90
        });

        this.nullContinuity.on(BI.Controller.EVENT_CHANGE, function () {
            self.fireEvent(BI.AccumulateRadarChartSetting.EVENT_CHANGE)
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
                    items: [this.legendStyle]
                }, {
                    type: "bi.label",
                    text: BI.i18nText("BI-Grid_Line"),
                    cls: "attr-names"
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.showHGridLine]
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.hGridLineColor]
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.showVGridLine]
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.vGridLineColor]
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.showDataLabel]
                }, /*{
                    type: "bi.label",
                    text: BI.i18nText("BI-Tooltip"),
                    cls: "attr-names"
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.tooltipStyle]
                },*/ {
                    type: "bi.vertical_adapt",
                    items: [this.nullContinuity]
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
            self.fireEvent(BI.AccumulateRadarChartSetting.EVENT_CHANGE);
        });

        //手动选择联动条件
        this.linkageSelection = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Select_Linkage_Manually"),
            width: 150
        });

        this.linkageSelection.on(BI.Controller.EVENT_CHANGE, function () {
            self.fireEvent(BI.AccumulateRadarChartSetting.EVENT_CHANGE)
        });

        var otherAttr = BI.createWidget({
            type: "bi.left_right_vertical_adapt",
            cls: "single-line-settings",
            items: {
                left: [{
                    type: "bi.label",
                    text: BI.i18nText("BI-Interactive_Attr"),
                    cls: "line-title"
                }, this.transferFilter/*, this.linkageSelection*/]
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

        this.showName.setSelected(BI.Utils.getWSShowNameByID(wId));
        this.widgetName.setValue(BI.Utils.getWidgetNameByID(wId));
        this.widgetNameStyle.setValue(BI.Utils.getWSTitleDetailSettingByID(wId));
        this.widgetTitle.setVisible(BI.Utils.getWSShowNameByID(wId));

        this.chartColor.setValue(BI.Utils.getWSChartColorByID(wId));
        this.radarChartType.setValue(BI.Utils.getWSChartRadarTypeByID(wId));
        this.widgetBG.setValue(BI.Utils.getWSWidgetBGByID(wId));

        this.leftYNumberLevel.setValue(BI.Utils.getWSLeftYAxisNumLevelByID(wId));
        this.leftYUnit.setValue(BI.Utils.getWSLeftYAxisUnitByID(wId));
        this.leftYNumberFormat.setValue(BI.Utils.getWSLeftYAxisStyleByID(wId));
        this.leftYSeparator.setSelected(BI.Utils.getWSNumberSeparatorsByID(wId));
        this.leftYShowLabel.setSelected(BI.Utils.getWSShowLValueAxisLabelByID(wId));
        this.leftYLabelStyle.setVisible(this.leftYShowLabel.isSelected());
        this.leftYLabelStyle.setValue(BI.Utils.getWSLValueAxisLabelSettingByID(wId));
        this.leftYLineColor.setValue(BI.Utils.getWSLValueAxisLineColorByID(wId));
        this.leftYShowCustomScale.setSelected(BI.Utils.getWSShowYCustomScale(wId));
        this.leftYCustomScale.setValue(BI.Utils.getWSCustomYScale(wId));
        this.leftYCustomScale.setVisible(BI.Utils.getWSShowYCustomScale(wId));

        this.legend.setValue(BI.Utils.getWSChartLegendByID(wId));
        this.legendStyle.setValue(BI.Utils.getWSLegendSettingByID(wId));
        this.showHGridLine.setSelected(BI.Utils.getWSShowHGridLineByID(wId));
        this.hGridLineColor.setVisible(this.showHGridLine.isSelected());
        this.hGridLineColor.setValue(BI.Utils.getWSHGridLineColorByID(wId));
        this.showVGridLine.setSelected(BI.Utils.getWSShowVGridLineByID(wId));
        this.vGridLineColor.setVisible(this.showVGridLine.isSelected());
        this.vGridLineColor.setValue(BI.Utils.getWSVGridLineColorByID(wId));
        this.showDataLabel.setSelected(BI.Utils.getWSShowDataLabelByID(wId));
        this.tooltipStyle.setValue(BI.Utils.getWSToolTipSettingByID(wId));
        this.nullContinuity.setSelected(BI.Utils.getWSNullContinueByID(wId));

        this.transferFilter.setSelected(BI.Utils.getWSTransferFilterByID(wId));
        this.linkageSelection.setSelected(BI.Utils.getWSLinkageSelectionByID(wId));
    },

    getValue: function () {
        return {
            showName: this.showName.isSelected(),
            widgetName: this.widgetName.getValue(),
            widgetNameStyle: this.widgetNameStyle.getValue(),

            chartColor: this.chartColor.getValue()[0],
            radarChartType: this.radarChartType.getValue()[0],
            widgetBG: this.widgetBG.getValue(),

            leftYNumberLevel: this.leftYNumberLevel.getValue()[0],
            leftYUnit: this.leftYUnit.getValue(),
            leftYNumberFormat: this.leftYNumberFormat.getValue()[0],
            leftYSeparator: this.leftYSeparator.isSelected(),
            leftYShowLabel: this.leftYShowLabel.isSelected(),
            leftYLabelStyle: this.leftYLabelStyle.getValue(),
            leftYLineColor: this.leftYLineColor.getValue(),
            leftYShowCustomScale: this.leftYShowCustomScale.isSelected(),
            leftYCustomScale: this.leftYCustomScale.getValue(),

            legend: this.legend.getValue()[0],
            legendStyle: this.legendStyle.getValue(),
            showHGridLine: this.showHGridLine.isSelected(),
            hGridLineColor: this.hGridLineColor.getValue(),
            showVGridLine: this.showVGridLine.isSelected(),
            vGridLineColor: this.vGridLineColor.getValue(),
            showDataLabel: this.showDataLabel.isSelected(),
            tooltipStyle: this.tooltipStyle.getValue(),
            nullContinuity: this.nullContinuity.isSelected(),

            transferFilter: this.transferFilter.isSelected(),
            linkageSelection: this.linkageSelection.isSelected()
        }
    }
});
BI.AccumulateRadarChartSetting.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.accumulate_radar_chart_setting", BI.AccumulateRadarChartSetting);
