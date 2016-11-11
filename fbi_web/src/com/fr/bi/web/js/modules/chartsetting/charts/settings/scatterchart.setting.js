/**
 * @class BI.ScatterChartSetting
 * @extends BI.Widget
 * 散点样式
 */
BI.ScatterChartSetting = BI.inherit(BI.AbstractChartSetting, {

    _defaultConfig: function () {
        return BI.extend(BI.ScatterChartSetting.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-charts-setting bi-scatter-chart-setting"
        })
    },

    _init: function () {
        BI.ScatterChartSetting.superclass._init.apply(this, arguments);
        var self = this, o = this.options, constant = BI.AbstractChartSetting;

        //显示组件标题
        this.showName = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Show_Chart_Title"),
            cls: "attr-names",
            logic: {
                dynamic: true
            }
        });
        this.showName.on(BI.Controller.EVENT_CHANGE, function () {
            self.widgetTitle.setVisible(this.isSelected());
            self.fireEvent(BI.ScatterChartSetting.EVENT_CHANGE);
        });

        //组件标题
        this.widgetName = BI.createWidget({
            type: "bi.sign_editor",
            cls: "title-input",
            width: 120
        });

        this.widgetName.on(BI.SignEditor.EVENT_CHANGE, function () {
            self.fireEvent(BI.ScatterChartSetting.EVENT_CHANGE)
        });

        //详细设置
        this.widgetNameStyle = BI.createWidget({
            type: "bi.show_title_detailed_setting_combo"
        });

        this.widgetNameStyle.on(BI.ShowTitleDetailedSettingCombo.EVENT_CHANGE, function () {
            self.fireEvent(BI.ScatterChartSetting.EVENT_CHANGE)
        });

        this.widgetTitle = BI.createWidget({
            type: "bi.left",
            items: [this.widgetName, this.widgetName],
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
            self.fireEvent(BI.ScatterChartSetting.EVENT_CHANGE);
        });

        //组件背景
        this.widgetBG = BI.createWidget({
            type: "bi.global_style_index_background"
        });
        this.widgetBG.on(BI.GlobalStyleIndexBackground.EVENT_CHANGE, function () {
            self.fireEvent(BI.ScatterChartSetting.EVENT_CHANGE);
        });

        var tableStyle = BI.createWidget({
            type: "bi.horizontal_adapt",
            columnSize: [80],
            cls: "single-line-settings",
            items: [{
                type: "bi.label",
                text: BI.i18nText("BI-Chart"),
                textAlign: "left",
                lgap: constant.SIMPLE_H_LGAP,
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
                    text: BI.i18nText("BI-Widget_Background_Colour"),
                    cls: "attr-names",
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.widgetBG]
                }], {
                    height: constant.SINGLE_LINE_HEIGHT
                }),
                lgap: constant.SIMPLE_H_GAP
            }]
        });

        this.rightYNumberLevel = BI.createWidget({
            type: "bi.segment",
            width: constant.NUMBER_LEVEL_SEGMENT_WIDTH,
            height: constant.BUTTON_HEIGHT,
            items: BICst.TARGET_STYLE_LEVEL
        });

        this.rightYNumberLevel.on(BI.Segment.EVENT_CHANGE, function () {
            self.fireEvent(BI.ScatterChartSetting.EVENT_CHANGE);
        });

        this.rightYUnit = BI.createWidget({
            type: "bi.sign_editor",
            width: constant.EDITOR_WIDTH,
            height: constant.EDITOR_HEIGHT,
            cls: "unit-input",
            watermark: BI.i18nText("BI-Custom_Input")
        });

        this.rightYUnit.on(BI.SignEditor.EVENT_CONFIRM, function () {
            self.fireEvent(BI.ScatterChartSetting.EVENT_CHANGE);
        });

        this.rightYNumberFormat = BI.createWidget({
            type: "bi.segment",
            width: constant.FORMAT_SEGMENT_WIDTH,
            height: constant.BUTTON_HEIGHT,
            items: BICst.TARGET_STYLE_FORMAT
        });

        this.rightYNumberFormat.on(BI.Segment.EVENT_CHANGE, function () {
            self.fireEvent(BI.ScatterChartSetting.EVENT_CHANGE);
        });

        this.rightYSeparator = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Separators"),
            width: 80
        });

        this.rightYSeparator.on(BI.Controller.EVENT_CHANGE, function () {
            self.fireEvent(BI.ScatterChartSetting.EVENT_CHANGE);
        });

        this.rightYShowTitle = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Show_Title"),
            width: 90
        });

        this.rightYShowTitle.on(BI.Controller.EVENT_CHANGE, function () {
            self.rightYTitle.setVisible(this.isSelected());
            self.rightYTitleStyle.setVisible(this.isSelected());
            self.fireEvent(BI.ScatterChartSetting.EVENT_CHANGE);
        });

        this.rightYTitle = BI.createWidget({
            type: "bi.sign_editor",
            width: constant.EDITOR_WIDTH,
            height: constant.EDITOR_HEIGHT,
            cls: "unit-input"
        });

        this.rightYTitle.on(BI.SignEditor.EVENT_CONFIRM, function () {
            self.fireEvent(BI.ScatterChartSetting.EVENT_CHANGE);
        });

        this.rightYTitleStyle = BI.createWidget({
            type: "bi.legend_detailed_setting_combo"
        });

        this.rightYTitleStyle.on(BI.LegendDetailedSettingCombo.EVENT_CHANGE, function () {
            self.fireEvent(BI.ScatterChartSetting.EVENT_CHANGE)
        });

        //右轴标签
        this.rightYShowLabel = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Show_Label"),
            width: 100
        });

        this.rightYShowLabel.on(BI.Controller.EVENT_CHANGE, function () {
            self.rightYLabelStyle.setVisible(this.isSelected());
            self.fireEvent(BI.ScatterChartSetting.EVENT_CHANGE)
        });

        this.rightYLabelStyle = BI.createWidget({
            type: "bi.chart_label_detailed_setting_combo"
        });

        this.rightYLabelStyle.on(BI.ChartLabelDetailedSettingCombo.EVENT_CHANGE, function () {
            self.fireEvent(BI.ScatterChartSetting.EVENT_CHANGE)
        });

        //右轴线颜色
        this.rightYLineColor = BI.createWidget({
            type: "bi.color_chooser",
            width: 30,
            height: 30
        });

        this.rightYLineColor.on(BI.ColorChooser.EVENT_CHANGE, function () {
            self.fireEvent(BI.ScatterChartSetting.EVENT_CHANGE)
        });

        //x轴刻度自定义
        this.rightYShowCustomScale = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Scale_Customize"),
            width: 115
        });

        this.rightYShowCustomScale.on(BI.Controller.EVENT_CHANGE, function () {
            self.rightYCustomScale.setVisible(this.isSelected());
            if (!this.isSelected()) {
                self.rightYCustomScale.setValue({})
            }
            self.fireEvent(BI.ScatterChartSetting.EVENT_CHANGE)
        });

        this.rightYCustomScale = BI.createWidget({
            type: "bi.custom_scale",
            wId: o.wId
        });

        this.rightYCustomScale.on(BI.CustomScale.EVENT_CHANGE, function () {
            self.fireEvent(BI.ScatterChartSetting.EVENT_CHANGE)
        });

        var xAxis = BI.createWidget({
            type: "bi.horizontal_adapt",
            columnSize: [80],
            cls: "single-line-settings",
            items: [{
                type: "bi.label",
                textHeight: constant.SINGLE_LINE_HEIGHT,
                textAlign: "left",
                lgap: constant.SIMPLE_H_LGAP,
                text: BI.i18nText("BI-X_Axis"),
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
                    items: [this.rightYNumberLevel]
                }, {
                    type: "bi.label",
                    text: BI.i18nText("BI-Unit_Normal"),
                    cls: "attr-names"
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.rightYUnit]
                }, {
                    type: "bi.label",
                    text: BI.i18nText("BI-Format"),
                    cls: "attr-names"
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.rightYNumberFormat]
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.rightYSeparator]
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.rightYShowTitle, this.rightYTitle]
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.rightYTitleStyle]
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.rightYShowLabel]
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.rightYLabelStyle]
                }, {
                    type: "bi.label",
                    text: BI.i18nText("BI-Axis_Line_Color"),
                    cls: "attr-name"
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.rightYLineColor]
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.rightYShowCustomScale]
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.rightYCustomScale]
                }], {
                    height: constant.SINGLE_LINE_HEIGHT
                }),
                lgap: constant.SIMPLE_H_GAP
            }]
        });

        this.leftYNumberLevel = BI.createWidget({
            type: "bi.segment",
            width: constant.NUMBER_LEVEL_SEGMENT_WIDTH,
            height: constant.BUTTON_HEIGHT,
            items: BICst.TARGET_STYLE_LEVEL
        });

        this.leftYNumberLevel.on(BI.Segment.EVENT_CHANGE, function () {
            self.fireEvent(BI.ScatterChartSetting.EVENT_CHANGE);
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
            self.fireEvent(BI.ScatterChartSetting.EVENT_CHANGE);
        });

        this.leftYNumberFormat = BI.createWidget({
            type: "bi.segment",
            width: constant.FORMAT_SEGMENT_WIDTH,
            height: constant.BUTTON_HEIGHT,
            items: BICst.TARGET_STYLE_FORMAT
        });

        this.leftYNumberFormat.on(BI.Segment.EVENT_CHANGE, function () {
            self.fireEvent(BI.ScatterChartSetting.EVENT_CHANGE);
        });

        //千分符
        this.leftYSeparator = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Separators"),
            width: 80
        });

        this.leftYSeparator.on(BI.Controller.EVENT_CHANGE, function () {
            self.fireEvent(BI.ScatterChartSetting.EVENT_CHANGE);
        });

        //显示标题
        this.leftYShowTitle = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Show_Title"),
            width: 90
        });

        this.leftYShowTitle.on(BI.Controller.EVENT_CHANGE, function () {
            self.leftYTitle.setVisible(this.isSelected());
            self.leftYTitleStyle.setVisible(this.isSelected());
            self.fireEvent(BI.ScatterChartSetting.EVENT_CHANGE);
        });

        this.leftYTitle = BI.createWidget({
            type: "bi.sign_editor",
            width: constant.EDITOR_WIDTH,
            height: constant.EDITOR_HEIGHT,
            cls: "unit-input"
        });
        this.leftYTitle.on(BI.SignEditor.EVENT_CONFIRM, function () {
            self.fireEvent(BI.ScatterChartSetting.EVENT_CHANGE);
        });

        this.leftYTitleStyle = BI.createWidget({
            type: "bi.legend_detailed_setting_combo"
        });

        this.leftYTitleStyle.on(BI.LegendDetailedSettingCombo.EVENT_CHANGE, function () {
            self.fireEvent(BI.ScatterChartSetting.EVENT_CHANGE)
        });

        //左轴标签
        this.leftYShowLabel = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Show_Label"),
            width: 100
        });

        this.leftYShowLabel.on(BI.Controller.EVENT_CHANGE, function () {
            self.leftYLabelStyle.setVisible(this.isSelected());
            self.fireEvent(BI.ScatterChartSetting.EVENT_CHANGE)
        });

        this.leftYLabelStyle = BI.createWidget({
            type: "bi.chart_label_detailed_setting_combo"
        });

        this.leftYLabelStyle.on(BI.ChartLabelDetailedSettingCombo.EVENT_CHANGE, function () {
            self.fireEvent(BI.ScatterChartSetting.EVENT_CHANGE)
        });

        //左轴线颜色
        this.leftYLineColor = BI.createWidget({
            type: "bi.color_chooser",
            width: 30,
            height: 30
        });

        this.leftYLineColor.on(BI.ColorChooser.EVENT_CHANGE, function () {
            self.fireEvent(BI.ScatterChartSetting.EVENT_CHANGE)
        });

        //y轴刻度自定义
        this.leftYShowCustomScale = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Scale_Customize"),
            width: 115
        });

        this.leftYShowCustomScale.on(BI.Controller.EVENT_CHANGE, function () {
            self.leftYCustomScale.setVisible(this.isSelected());
            if (!this.isSelected()) {
                self.leftYCustomScale.setValue({})
            }
            self.fireEvent(BI.ScatterChartSetting.EVENT_CHANGE)
        });

        this.leftYCustomScale = BI.createWidget({
            type: "bi.custom_scale",
            wId: o.wId
        });

        this.leftYCustomScale.on(BI.CustomScale.EVENT_CHANGE, function () {
            self.fireEvent(BI.ScatterChartSetting.EVENT_CHANGE)
        });

        var lYAxis = BI.createWidget({
            type: "bi.horizontal_adapt",
            columnSize: [80],
            cls: "single-line-settings",
            items: [{
                type: "bi.label",
                textHeight: constant.SINGLE_LINE_HEIGHT,
                textAlign: "left",
                lgap: constant.SIMPLE_H_LGAP,
                text: BI.i18nText("BI-Y_Axis"),
                cls: "line-title"
            }, {
                type: "bi.left",
                cls: "detail-style",
                items: BI.createItems([{
                    type: "bi.label",
                    text: BI.i18nText("BI-Num_Level"),
                    lgap: constant.SIMPLE_H_GAP,
                    cls: "attr-names"
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.leftYNumberLevel]
                }, {
                    type: "bi.label",
                    text: BI.i18nText("BI-Unit_Normal"),
                    lgap: constant.SIMPLE_H_GAP,
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
                    items: [this.leftYShowTitle, this.leftYTitle]
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.leftYTitleStyle]
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.leftYShowLabel]
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.leftYLabelStyle]
                }, {
                    type: "bi.label",
                    text: BI.i18nText("BI-Axis_Line_Color"),
                    cls: "attr-name"
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
            self.fireEvent(BI.ScatterChartSetting.EVENT_CHANGE);
        });

        this.legendStyle = BI.createWidget({
            type: "bi.legend_detailed_setting_combo"
        });

        this.legendStyle.on(BI.LegendDetailedSettingCombo.EVENT_CHANGE, function () {
            self.fireEvent(BI.ScatterChartSetting.EVENT_CHANGE)
        });

        //网格线设置
        this.hShowGridLine = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Horizontal"),
            width: 65
        });

        this.hShowGridLine.on(BI.Controller.EVENT_CHANGE, function () {
            self.hGridLineColor.setVisible(this.isSelected());
            self.fireEvent(BI.ScatterChartSetting.EVENT_CHANGE)
        });

        this.hGridLineColor = BI.createWidget({
            type: "bi.color_chooser",
            width: 30,
            height: 30
        });

        this.hGridLineColor.on(BI.ColorChooser.EVENT_CHANGE, function () {
            self.fireEvent(BI.ScatterChartSetting.EVENT_CHANGE)
        });

        this.vShowGridLine = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Vertical"),
            width: 65
        });

        this.vShowGridLine.on(BI.Controller.EVENT_CHANGE, function () {
            self.vGridLineColor.setVisible(this.isSelected());
            self.fireEvent(BI.ScatterChartSetting.EVENT_CHANGE)
        });

        this.vGridLineColor = BI.createWidget({
            type: "bi.color_chooser",
            width: 30,
            height: 30
        });

        this.vGridLineColor.on(BI.ColorChooser.EVENT_CHANGE, function () {
            self.fireEvent(BI.ScatterChartSetting.EVENT_CHANGE)
        });

        //数据标签
        this.showDataLabel = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Show_Data_Label"),
            width: 115
        });

        this.showDataLabel.on(BI.Controller.EVENT_CHANGE, function () {
            self.fireEvent(BI.ScatterChartSetting.EVENT_CHANGE);
        });

        //数据点提示详细设置
        this.tooltipStyle = BI.createWidget({
            type: "bi.tooltip_detailed_setting_combo"
        });

        this.tooltipStyle.on(BI.TooltipDetailedSettingCombo.EVENT_CHANGE, function () {
            self.fireEvent(BI.ScatterChartSetting.EVENT_CHANGE)
        });

        var showElement = BI.createWidget({
            type: "bi.horizontal_adapt",
            columnSize: [80],
            cls: "single-line-settings",
            items: [{
                type: "bi.label",
                textAlign: "left",
                lgap: constant.SIMPLE_H_LGAP,
                text: BI.i18nText("BI-Element_Show"),
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
                    items: [this.hShowGridLine]
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.hGridLineColor]
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.vShowGridLine]
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.vGridLineColor]
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.showDataLabel]
                }/*, {
                    type: "bi.label",
                    text: BI.i18nText("BI-Tooltip"),
                    cls: "attr-names"
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.tooltipStyle]
                }*/], {
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
            self.fireEvent(BI.ScatterChartSetting.EVENT_CHANGE);
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

        //大数据模式
        this.bigDataMode = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Big_Data_Mode"),
            width: 120
        });

        this.bigDataMode.on(BI.Controller.EVENT_CHANGE, function () {
            self._bigDataMode(!this.isSelected());
            if (this.isSelected()) {
                self._colorSettingChange(BICst.DISPLAY_RULES.GRADIENT)
            }
            self.fireEvent(BI.ScatterChartSetting.EVENT_CHANGE)
        });

        var modeChange = BI.createWidget({
            type: "bi.left_right_vertical_adapt",
            cls: "single-line-settings",
            items: {
                left: [{
                    type: "bi.label",
                    text: BI.i18nText("BI-Mode_Change"),
                    cls: "line-title"
                }, this.bigDataMode]
            },
            height: constant.SINGLE_LINE_HEIGHT,
            lhgap: constant.SIMPLE_H_GAP
        });

        BI.createWidget({
            type: "bi.vertical",
            element: this.element,
            items: [widgetTitle, tableStyle, xAxis, lYAxis, showElement, otherAttr, modeChange],
            hgap: 10
        })
    },

    _bigDataMode: function (v) {
        this.showDataLabel.setEnable(v);
        this.transferFilter.setEnable(v)
    },

    populate: function () {
        var wId = this.options.wId;

        var view = BI.Utils.getWidgetViewByID(wId);
        var titleLY = BI.Utils.getWSLeftYAxisTitleByID(wId);
        var titleX = BI.Utils.getWSRightYAxisTitleByID(wId);
        if (titleLY === "") {
            BI.any(view[BICst.REGION.TARGET1], function (idx, dId) {
                if (BI.Utils.isDimensionUsable(dId)) {
                    titleLY = BI.Utils.getDimensionNameByID(dId);
                    return true;
                }
                return false;
            });
        }
        if (titleX === "") {
            BI.any(view[BICst.REGION.DIMENSION1], function (idx, dId) {
                if (BI.Utils.isDimensionUsable(dId)) {
                    titleX = BI.Utils.getDimensionNameByID(dId);
                    return true;
                }
                return false;
            });
        }
        this.showName.setSelected(BI.Utils.getWSShowNameByID(wId));
        this.widgetTitle.setVisible(BI.Utils.getWSShowNameByID(wId));
        this.widgetName.setValue(BI.Utils.getWidgetNameByID(wId));
        this.widgetNameStyle.setValue(BI.Utils.getWSTitleDetailSettingByID(wId));

        this.chartColor.setValue(BI.Utils.getWSChartColorByID(wId));
        this.widgetBG.setValue(BI.Utils.getWSWidgetBGByID(wId));

        //x轴
        this.rightYNumberFormat.setValue(BI.Utils.getWSRightYAxisStyleByID(wId));
        this.rightYNumberLevel.setValue(BI.Utils.getWSRightYAxisNumLevelByID(wId));
        this.rightYUnit.setValue(BI.Utils.getWSRightYAxisUnitByID(wId));
        this.rightYShowTitle.setSelected(BI.Utils.getWSShowRightYAxisTitleByID(wId));
        this.rightYTitle.setValue(titleX);
        this.rightYShowCustomScale.setSelected(BI.Utils.getWSShowXCustomScale(wId));
        this.rightYCustomScale.setValue(BI.Utils.getWSChartLeftYCustomScale(wId));
        this.rightYCustomScale.setVisible(BI.Utils.getWSShowXCustomScale(wId));
        this.rightYSeparator.setSelected(BI.Utils.getWSRightYNumberSeparatorByID(wId));
        this.rightYShowLabel.setSelected(BI.Utils.getWSRightYShowLabelByID(wId));
        this.rightYLabelStyle.setValue(BI.Utils.getWSRightYLabelStyleByID(wId));
        this.rightYLineColor.setValue(BI.Utils.getWSRightYLineColorByID(wId));
        this.rightYTitleStyle.setValue(BI.Utils.getWSChartRightYTitleStyleByID(wId));
        this.rightYTitle.setVisible(this.rightYShowTitle.isSelected());
        this.rightYTitleStyle.setVisible(this.rightYShowTitle.isSelected());

        //y轴
        this.leftYNumberFormat.setValue(BI.Utils.getWSLeftYAxisStyleByID(wId));
        this.leftYNumberLevel.setValue(BI.Utils.getWSLeftYAxisNumLevelByID(wId));
        this.leftYUnit.setValue(BI.Utils.getWSLeftYAxisUnitByID(wId));
        this.leftYShowTitle.setSelected(BI.Utils.getWSShowLeftYAxisTitleByID(wId));
        this.leftYTitle.setValue(titleLY);
        this.leftYShowCustomScale.setSelected(BI.Utils.getWSShowYCustomScale(wId));
        this.leftYCustomScale.setValue(BI.Utils.getWSChartLeftYCustomScale(wId));
        this.leftYCustomScale.setVisible(BI.Utils.getWSShowYCustomScale(wId));
        this.leftYSeparator.setSelected(BI.Utils.getWSLeftYNumberSeparatorByID(wId));
        this.leftYShowLabel.setSelected(BI.Utils.getWSChartLeftYShowLabelByID(wId));
        this.leftYLabelStyle.setValue(BI.Utils.getWSChartLeftYLabelStyleByID(wId));
        this.leftYLineColor.setValue(BI.Utils.getWSChartLeftYLineColorByID(wId));
        this.leftYTitleStyle.setValue(BI.Utils.getWSChartLeftYTitleStyleByID(wId));
        this.leftYTitle.setVisible(this.leftYShowTitle.isSelected());
        this.leftYTitleStyle.setVisible(this.leftYShowTitle.isSelected());

        this.legend.setValue(BI.Utils.getWSChartLegendByID(wId));
        this.showDataLabel.setSelected(BI.Utils.getWSChartShowDataLabelByID(wId));
        this.legendStyle.setValue(BI.Utils.getWSChartLegendStyleByID(wId));
        this.hShowGridLine.setSelected(BI.Utils.getWSChartHShowGridLineByID(wId));
        this.hGridLineColor.setValue(BI.Utils.getWSChartHGridLineColorByID(wId));
        this.vShowGridLine.setSelected(BI.Utils.getWSChartVShowGridLineByID(wId));
        this.vGridLineColor.setValue(BI.Utils.getWSChartVGridLineColorByID(wId));
        this.tooltipStyle.setValue(BI.Utils.getWSChartToolTipStyleByID(wId));
        this.hGridLineColor.setVisible(this.hShowGridLine.isSelected());
        this.vGridLineColor.setVisible(this.vShowGridLine.isSelected());

        this.transferFilter.setSelected(BI.Utils.getWSTransferFilterByID(wId));

        this.bigDataMode.setSelected(BI.Utils.getWSChartBigDataModeByID(wId));
        this._bigDataMode(!BI.Utils.getWSChartBigDataModeByID(wId));
    },

    getValue: function () {
        return {
            showName: this.showName.isSelected(),
            widgetName: this.widgetName.getValue(),
            widgetNameStyle: this.widgetNameStyle.getValue(),

            chartColor: this.chartColor.getValue()[0],
            widgetBG: this.widgetBG.getValue(),

            rightYNumberFormat: this.rightYNumberFormat.getValue()[0],
            rightYNumberLevel: this.rightYNumberLevel.getValue()[0],
            rightYUnit: this.rightYUnit.getValue(),
            rightYShowTitle: this.rightYShowTitle.isSelected(),
            rightYTitle: this.rightYTitle.getValue(),
            rightYShowCustomScale: this.rightYShowCustomScale.isSelected(),
            rightYCustomScale: this.rightYCustomScale.getValue(),
            rightYSeparator: this.rightYSeparator.isSelected(),
            rightYShowLabel: this.rightYShowLabel.isSelected(),
            rightYLabelStyle: this.rightYLabelStyle.getValue(),
            rightYLineColor: this.rightYLineColor.getValue(),
            rightYTitleStyle: this.rightYTitleStyle.getValue(),

            leftYNumberFormat: this.leftYNumberFormat.getValue()[0],
            leftYNumberLevel: this.leftYNumberLevel.getValue()[0],
            leftYUnit: this.leftYUnit.getValue(),
            leftYShowTitle: this.leftYShowTitle.isSelected(),
            leftYTitle: this.leftYTitle.getValue(),
            leftYShowCustomScale: this.leftYShowCustomScale.isSelected(),
            leftYSeparator: this.leftYSeparator.isSelected(),
            leftYCustomScale: this.leftYCustomScale.getValue(),
            leftYShowLabel: this.leftYShowLabel.isSelected(),
            leftYLabelStyle: this.leftYLabelStyle.getValue(),
            leftYLineColor: this.leftYLineColor.getValue(),
            leftYTitleStyle: this.leftYTitleStyle.getValue(),

            legend: this.legend.getValue()[0],
            showDataLabel: this.showDataLabel.isSelected(),
            legendStyle: this.legendStyle.getValue(),
            hShowGridLine: this.hShowGridLine.isSelected(),
            hGridLineColor: this.hGridLineColor.getValue(),
            vShowGridLine: this.vShowGridLine.isSelected(),
            vGridLineColor: this.vGridLineColor.getValue(),
            tooltipStyle: this.tooltipStyle.getValue(),

            transferFilter: this.transferFilter.isSelected(),

            bigDataMode: this.bigDataMode.isSelected(),
        }
    }
});
BI.ScatterChartSetting.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.scatter_chart_setting", BI.ScatterChartSetting);
