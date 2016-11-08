/**
 * @class BI.AxisChartsSetting
 * @extends BI.Widget
 * 柱状，堆积柱状，组合图样式
 */
BI.AxisChartsSetting = BI.inherit(BI.AbstractChartSetting, {

    _defaultConfig: function () {
        return BI.extend(BI.AxisChartsSetting.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-charts-setting bi-axis-chart-setting"
        })
    },

    _init: function () {
        BI.AxisChartsSetting.superclass._init.apply(this, arguments);
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
            self.fireEvent(BI.AxisChartsSetting.EVENT_CHANGE);
        });

        //组件标题
        this.widgetName = BI.createWidget({
            type: "bi.sign_editor",
            cls: "title-input",
            width: 120
        });

        this.widgetName.on(BI.SignEditor.EVENT_CHANGE, function () {
            self.fireEvent(BI.AxisChartsSetting.EVENT_CHANGE)
        });

        //详细设置
        this.widgetNameStyle = BI.createWidget({
            type: "bi.show_title_detailed_setting_combo"
        });

        this.widgetNameStyle.on(BI.ShowTitleDetailedSettingCombo.EVENT_CHANGE, function () {
            self.fireEvent(BI.AxisChartsSetting.EVENT_CHANGE)
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
            self.fireEvent(BI.AxisChartsSetting.EVENT_CHANGE);
        });

        //风格——1、2
        this.chartStyle = BI.createWidget({
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
        this.chartStyle.on(BI.ButtonGroup.EVENT_CHANGE, function () {
            self.fireEvent(BI.AxisChartsSetting.EVENT_CHANGE);
        });

        //组件背景
        this.widgetBG = BI.createWidget({
            type: "bi.global_style_index_background"
        });
        this.widgetBG.on(BI.GlobalStyleIndexBackground.EVENT_CHANGE, function () {
            self.fireEvent(BI.AxisChartsSetting.EVENT_CHANGE);
        });

        this.tableStyle = BI.createWidget({
            type: "bi.horizontal_adapt",
            columnSize: [80],
            cls: "single-line-settings",
            items: [{
                type: "bi.label",
                text: BI.i18nText("BI-Chart"),
                textHeight: constant.SINGLE_LINE_HEIGHT,
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
                    text: BI.i18nText("BI-Table_Style"),
                    cls: "attr-names"
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.chartStyle]
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

        //左值轴数量级
        this.leftYNumberLevel = BI.createWidget({
            type: "bi.segment",
            width: constant.NUMBER_LEVEL_SEGMENT_WIDTH,
            height: constant.BUTTON_HEIGHT,
            items: BICst.TARGET_STYLE_LEVEL
        });
        this.leftYNumberLevel.on(BI.Segment.EVENT_CHANGE, function () {
            self.fireEvent(BI.AxisChartsSetting.EVENT_CHANGE);
        });

        //左值轴单位
        this.leftYUnit = BI.createWidget({
            type: "bi.sign_editor",
            width: constant.EDITOR_WIDTH,
            height: constant.EDITOR_HEIGHT,
            cls: "unit-input",
            watermark: BI.i18nText("BI-Custom_Input")
        });
        this.leftYUnit.on(BI.SignEditor.EVENT_CONFIRM, function () {
            self.fireEvent(BI.AxisChartsSetting.EVENT_CHANGE);
        });

        //左值轴格式
        this.leftYNumberFormat = BI.createWidget({
            type: "bi.segment",
            width: constant.FORMAT_SEGMENT_WIDTH,
            height: constant.BUTTON_HEIGHT,
            items: BICst.TARGET_STYLE_FORMAT
        });

        this.leftYNumberFormat.on(BI.Segment.EVENT_CHANGE, function () {
            self.fireEvent(BI.AxisChartsSetting.EVENT_CHANGE);
        });

        //左值轴千分符
        this.leftYSeparator = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Separators"),
            width: 80
        });

        this.leftYSeparator.on(BI.Controller.EVENT_CHANGE, function () {
            self.fireEvent(BI.AxisChartsSetting.EVENT_CHANGE);
        });

        //左值轴显示标题
        this.leftYShowTitle = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Show_Title"),
            width: 90
        });

        this.leftYShowTitle.on(BI.Controller.EVENT_CHANGE, function () {
            self.leftYTitle.setVisible(this.isSelected());
            self.leftYTitleStyle.setVisible(this.isSelected());
            self.fireEvent(BI.AxisChartsSetting.EVENT_CHANGE);
        });

        //左值轴标题
        this.leftYTitle = BI.createWidget({
            type: "bi.sign_editor",
            width: constant.EDITOR_WIDTH,
            height: constant.EDITOR_HEIGHT,
            cls: "unit-input"
        });
        this.leftYTitle.on(BI.SignEditor.EVENT_CONFIRM, function () {
            self.fireEvent(BI.AxisChartsSetting.EVENT_CHANGE);
        });

        this.leftYTitleStyle = BI.createWidget({
            type: "bi.legend_detailed_setting_combo"
        });

        this.leftYTitleStyle.on(BI.LegendDetailedSettingCombo.EVENT_CHANGE, function () {
            self.fireEvent(BI.AxisChartsSetting.EVENT_CHANGE)
        });

        //左轴标签
        this.leftYShowLabel = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Show_Label"),
            width: 100
        });

        this.leftYShowLabel.on(BI.Controller.EVENT_CHANGE, function () {
            self.leftYLabelStyle.setVisible(this.isSelected());
            self.fireEvent(BI.AxisChartsSetting.EVENT_CHANGE)
        });

        this.leftYLabelStyle = BI.createWidget({
            type: "bi.chart_label_detailed_setting_combo"
        });

        this.leftYLabelStyle.on(BI.ChartLabelDetailedSettingCombo.EVENT_CHANGE, function () {
            self.fireEvent(BI.AxisChartsSetting.EVENT_CHANGE)
        });

        //左轴线颜色
        this.leftYLineColor = BI.createWidget({
            type: "bi.color_chooser",
            width: 30,
            height: 30
        });

        this.leftYLineColor.on(BI.ColorChooser.EVENT_CHANGE, function () {
            self.fireEvent(BI.AxisChartsSetting.EVENT_CHANGE)
        });

        //左值轴轴逆序
        this.leftYReverse = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Reversed_Axis"),
            width: 80
        });

        this.leftYReverse.on(BI.Controller.EVENT_CHANGE, function () {
            self.fireEvent(BI.AxisChartsSetting.EVENT_CHANGE);
        });

        //左轴刻度自定义
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
            self.fireEvent(BI.AxisChartsSetting.EVENT_CHANGE)
        });

        this.leftYCustomScale = BI.createWidget({
            type: "bi.custom_scale",
            wId: o.wId
        });

        this.leftYCustomScale.on(BI.CustomScale.EVENT_CHANGE, function () {
            self.fireEvent(BI.AxisChartsSetting.EVENT_CHANGE)
        });

        this.lYAxis = BI.createWidget({
            type: "bi.horizontal_adapt",
            columnSize: [80],
            cls: "single-line-settings",
            verticalAlign: "top",
            items: [{
                type: "bi.label",
                textHeight: constant.SINGLE_LINE_HEIGHT,
                text: BI.i18nText("BI-Left_Value_Axis"),
                textAlign: "left",
                lgap: constant.SIMPLE_H_LGAP,
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
                    items: [this.leftYReverse]
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

        //右值轴数量级
        this.rightYNumberLevel = BI.createWidget({
            type: "bi.segment",
            width: constant.NUMBER_LEVEL_SEGMENT_WIDTH,
            height: constant.BUTTON_HEIGHT,
            items: BICst.TARGET_STYLE_LEVEL
        });

        this.rightYNumberLevel.on(BI.Segment.EVENT_CHANGE, function () {
            self.fireEvent(BI.AxisChartsSetting.EVENT_CHANGE);
        });

        //右值轴单位
        this.rightYUnit = BI.createWidget({
            type: "bi.sign_editor",
            width: constant.EDITOR_WIDTH,
            height: constant.EDITOR_HEIGHT,
            cls: "unit-input",
            watermark: BI.i18nText("BI-Custom_Input")
        });

        this.rightYUnit.on(BI.SignEditor.EVENT_CONFIRM, function () {
            self.fireEvent(BI.AxisChartsSetting.EVENT_CHANGE);
        });

        //右值轴格式
        this.rightYNumberFormat = BI.createWidget({
            type: "bi.segment",
            width: constant.FORMAT_SEGMENT_WIDTH,
            height: constant.BUTTON_HEIGHT,
            items: BICst.TARGET_STYLE_FORMAT
        });

        this.rightYNumberFormat.on(BI.Segment.EVENT_CHANGE, function () {
            self.fireEvent(BI.AxisChartsSetting.EVENT_CHANGE);
        });

        //右值轴千分符
        this.rightYSeparator = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Separators"),
            width: 80
        });

        this.rightYSeparator.on(BI.Controller.EVENT_CHANGE, function () {
            self.fireEvent(BI.AxisChartsSetting.EVENT_CHANGE);
        });

        //右值轴显示标题
        this.rightYShowTitle = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Show_Title"),
            width: 90
        });

        this.rightYShowTitle.on(BI.Controller.EVENT_CHANGE, function () {
            self.rightYTitle.setVisible(this.isSelected());
            self.rightYTitleStyle.setVisible(this.isSelected());
            self.fireEvent(BI.AxisChartsSetting.EVENT_CHANGE);
        });

        //右值轴标题
        this.rightYTitle = BI.createWidget({
            type: "bi.sign_editor",
            width: constant.EDITOR_WIDTH,
            height: constant.EDITOR_HEIGHT,
            cls: "unit-input"
        });

        this.rightYTitle.on(BI.SignEditor.EVENT_CONFIRM, function () {
            self.fireEvent(BI.AxisChartsSetting.EVENT_CHANGE);
        });

        this.rightYTitleStyle = BI.createWidget({
            type: "bi.legend_detailed_setting_combo"
        });

        this.rightYTitleStyle.on(BI.LegendDetailedSettingCombo.EVENT_CHANGE, function () {
            self.fireEvent(BI.AxisChartsSetting.EVENT_CHANGE)
        });

        //右轴标签
        this.rightYShowLabel = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Show_Label"),
            width: 100
        });

        this.rightYShowLabel.on(BI.Controller.EVENT_CHANGE, function () {
            self.rightYLabelStyle.setVisible(this.isSelected());
            self.fireEvent(BI.AxisChartsSetting.EVENT_CHANGE)
        });

        this.rightYLabelStyle = BI.createWidget({
            type: "bi.chart_label_detailed_setting_combo"
        });

        this.rightYLabelStyle.on(BI.ChartLabelDetailedSettingCombo.EVENT_CHANGE, function () {
            self.fireEvent(BI.AxisChartsSetting.EVENT_CHANGE)
        });

        //右轴线颜色
        this.rightYLineColor = BI.createWidget({
            type: "bi.color_chooser",
            width: 30,
            height: 30
        });

        this.rightYLineColor.on(BI.ColorChooser.EVENT_CHANGE, function () {
            self.fireEvent(BI.AxisChartsSetting.EVENT_CHANGE)
        });

        //右值轴逆轴序
        this.rightYReverse = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Reversed_Axis"),
            width: 80
        });

        this.rightYReverse.on(BI.Controller.EVENT_CHANGE, function () {
            self.fireEvent(BI.AxisChartsSetting.EVENT_CHANGE);
        });

        //右轴刻度自定义
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
            self.fireEvent(BI.AxisChartsSetting.EVENT_CHANGE)
        });

        this.rightYCustomScale = BI.createWidget({
            type: "bi.custom_scale",
            wId: o.wId
        });

        this.rightYCustomScale.on(BI.CustomScale.EVENT_CHANGE, function () {
            self.fireEvent(BI.AxisChartsSetting.EVENT_CHANGE)
        });

        this.rYAxis = BI.createWidget({
            type: "bi.horizontal_adapt",
            columnSize: [80],
            cls: "single-line-settings",
            verticalAlign: "top",
            items: [{
                type: "bi.label",
                textHeight: constant.SINGLE_LINE_HEIGHT,
                lgap: constant.SIMPLE_H_LGAP,
                textAlign: "left",
                text: BI.i18nText("BI-Right_Value_Axis"),
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
                    items: [this.leftYShowLabel]
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
                    items: [this.rightYReverse]
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

        this.catShowTitle = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Show_Title"),
            width: 90
        });

        this.catShowTitle.on(BI.Controller.EVENT_CHANGE, function () {
            self.catTitle.setVisible(this.isSelected());
            self.catTitleStyle.setVisible(this.isSelected());
            self.fireEvent(BI.AxisChartsSetting.EVENT_CHANGE);
        });

        this.catTitle = BI.createWidget({
            type: "bi.sign_editor",
            width: constant.EDITOR_WIDTH,
            height: constant.EDITOR_HEIGHT,
            cls: "unit-input"
        });

        this.catTitle.on(BI.SignEditor.EVENT_CONFIRM, function () {
            self.fireEvent(BI.AxisChartsSetting.EVENT_CHANGE);
        });

        this.catTitleStyle = BI.createWidget({
            type: "bi.legend_detailed_setting_combo"
        });

        this.catTitleStyle.on(BI.LegendDetailedSettingCombo.EVENT_CHANGE, function () {
            self.fireEvent(BI.AxisChartsSetting.EVENT_CHANGE)
        });

        //显示分类轴标签
        this.catShowLabel = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Show_Label"),
            width: 100
        });

        this.catShowLabel.on(BI.Controller.EVENT_CHANGE, function () {
            self.catLabelStyle.setVisible(this.isSelected());
            self.fireEvent(BI.AxisChartsSetting.EVENT_CHANGE)
        });

        //分类轴标签
        this.catLabelStyle = BI.createWidget({
            type: "bi.chart_label_detailed_setting_combo"
        });

        this.catLabelStyle.on(BI.ChartLabelDetailedSettingCombo.EVENT_CHANGE, function () {
            self.fireEvent(BI.AxisChartsSetting.EVENT_CHANGE)
        });

        //分类轴线颜色
        this.catLineColor = BI.createWidget({
            type: "bi.color_chooser",
            width: 30,
            height: 30
        });

        this.catLineColor.on(BI.ColorChooser.EVENT_CHANGE, function () {
            self.fireEvent(BI.AxisChartsSetting.EVENT_CHANGE)
        });

        this.xAxis = BI.createWidget({
            type: "bi.horizontal_adapt",
            columnSize: [80],
            cls: "single-line-settings",
            items: [{
                type: "bi.label",
                text: BI.i18nText("BI-Category_Axis"),
                lgap: constant.SIMPLE_H_LGAP,
                textAlign: "left",
                textHeight: constant.SINGLE_LINE_HEIGHT,
                cls: "line-title"
            }, {
                type: "bi.left",
                cls: "detail-style",
                items: BI.createItems([{
                    type: "bi.vertical_adapt",
                    items: [this.catShowTitle]
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.catTitle]
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.catTitleStyle]
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.catShowLabel]
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.catLabelStyle]
                }, {
                    type: "bi.label",
                    text: BI.i18nText("BI-Axis_Line_Color"),
                    cls: "attr-name"
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.catLineColor]
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
            self.fireEvent(BI.AxisChartsSetting.EVENT_CHANGE);
        });

        //图例详细设置
        this.legendStyle = BI.createWidget({
            type: "bi.legend_detailed_setting_combo"
        });

        this.legendStyle.on(BI.LegendDetailedSettingCombo.EVENT_CHANGE, function () {
            self.fireEvent(BI.AxisChartsSetting.EVENT_CHANGE)
        });

        //网格线设置
        this.showHGridLine = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Horizontal"),
            width: 65
        });

        this.showHGridLine.on(BI.Controller.EVENT_CHANGE, function () {
            self.hGridLineColor.setVisible(this.isSelected());
            self.fireEvent(BI.AxisChartsSetting.EVENT_CHANGE)
        });

        this.hGridLineColor = BI.createWidget({
            type: "bi.color_chooser",
            width: 30,
            height: 30
        });

        this.hGridLineColor.on(BI.ColorChooser.EVENT_CHANGE, function () {
            self.fireEvent(BI.AxisChartsSetting.EVENT_CHANGE)
        });

        this.showVGridLine = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Vertical"),
            width: 65
        });

        this.showVGridLine.on(BI.Controller.EVENT_CHANGE, function () {
            self.vGridLineColor.setVisible(this.isSelected());
            self.fireEvent(BI.AxisChartsSetting.EVENT_CHANGE)
        });

        this.vGridLineColor = BI.createWidget({
            type: "bi.color_chooser",
            width: 30,
            height: 30
        });

        this.vGridLineColor.on(BI.ColorChooser.EVENT_CHANGE, function () {
            self.fireEvent(BI.AxisChartsSetting.EVENT_CHANGE)
        });

        //数据标签
        this.showDataLabel = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Show_Data_Label"),
            width: 115
        });

        this.showDataLabel.on(BI.Controller.EVENT_CHANGE, function () {
            self.fireEvent(BI.AxisChartsSetting.EVENT_CHANGE);
        });

        //数据表格
        this.showDataTable = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Show_Data_Table"),
            width: 115
        });

        this.showDataTable.on(BI.Controller.EVENT_CHANGE, function () {
            if (this.isSelected()) {
                self.showZoom.setSelected(false);
            }
            self.fireEvent(BI.AxisChartsSetting.EVENT_CHANGE);
        });

        //图表缩放滚轮
        this.showZoom = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Show_Zoom"),
            width: 140
        });

        this.showZoom.on(BI.Controller.EVENT_CHANGE, function () {
            if (this.isSelected()) {
                self.showDataTable.setSelected(false);
            }
            self.fireEvent(BI.AxisChartsSetting.EVENT_CHANGE);
        });

        //数据点提示详细设置
        this.tooltipStyle = BI.createWidget({
            type: "bi.tooltip_detailed_setting_combo"
        });

        this.tooltipStyle.on(BI.TooltipDetailedSettingCombo.EVENT_CHANGE, function () {
            self.fireEvent(BI.AxisChartsSetting.EVENT_CHANGE)
        });

        this.showElement = BI.createWidget({
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
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.showDataTable]
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.showZoom]
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
            self.fireEvent(BI.AxisChartsSetting.EVENT_CHANGE);
        });

        //手动选择联动条件
        this.linkageSelection = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Select_Linkage_Manually"),
            width: 150
        });

        this.linkageSelection.on(BI.Controller.EVENT_CHANGE, function () {
            self.fireEvent(BI.AxisChartsSetting.EVENT_CHANGE)
        });

        this.otherAttr = BI.createWidget({
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

        //极简模式
        this.miniModel = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Minimalist_Model"),
            width: 170
        });

        this.miniModel.on(BI.Controller.EVENT_CHANGE, function () {
            self._invisible(!this.isSelected());
            self.fireEvent(BI.AxisChartsSetting.EVENT_CHANGE)
        });

        var modelChange = BI.createWidget({
            type: "bi.left_right_vertical_adapt",
            cls: "single-line-settings",
            items: {
                left: [{
                    type: "bi.label",
                    text: BI.i18nText("BI-Mode_Change"),
                    cls: "line-title"
                }, this.miniModel]
            },
            height: constant.SINGLE_LINE_HEIGHT,
            lhgap: constant.SIMPLE_H_GAP
        });

        BI.createWidget({
            type: "bi.vertical",
            element: this.element,
            items: [widgetTitle, this.tableStyle, this.lYAxis, this.rYAxis, this.xAxis, this.showElement, this.otherAttr, modelChange],
            hgap: 10
        })
    },

    _invisible: function (v) {
        this.tableStyle.setVisible(v);
        this.lYAxis.setVisible(v);
        this.xAxis.setVisible(v);
        this.rYAxis.setVisible(v);
        this.showElement.setVisible(v);
        this.otherAttr.setVisible(v);
    },

    populate: function () {
        var wId = this.options.wId;
        var view = BI.Utils.getWidgetViewByID(wId);
        var titleLY = BI.Utils.getWSLeftYAxisTitleByID(wId);
        var titleX = BI.Utils.getWSXAxisTitleByID(wId);
        var titleRY = BI.Utils.getWSRightYAxisTitleByID(wId);
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
        if (titleRY === "") {
            BI.any(view[BICst.REGION.TARGET2], function (idx, dId) {
                if (BI.Utils.isDimensionUsable(dId)) {
                    titleRY = BI.Utils.getDimensionNameByID(dId);
                    return true;
                }
                return false;
            });
        }
        this.showName.setSelected(BI.Utils.getWSShowNameByID(wId));
        this.widgetName.setVisible(BI.Utils.getWSShowNameByID(wId));
        this.widgetName.setValue(BI.Utils.getWidgetNameByID(wId));
        this.widgetNameStyle.setValue(BI.Utils.getWSTitleDetailSettingByID(wId));

        this.chartColor.setValue(BI.Utils.getWSChartColorByID(wId));
        this.chartStyle.setValue(BI.Utils.getWSChartStyleByID(wId));
        this.widgetBG.setValue(BI.Utils.getWSWidgetBGByID(wId));

        this.leftYNumberLevel.setValue(BI.Utils.getWSLeftYAxisNumLevelByID(wId));
        this.leftYUnit.setValue(BI.Utils.getWSLeftYAxisUnitByID(wId));
        this.leftYNumberFormat.setValue(BI.Utils.getWSLeftYAxisStyleByID(wId));
        this.leftYShowTitle.setSelected(BI.Utils.getWSShowLeftYAxisTitleByID(wId));
        this.leftYTitle.setValue(titleLY);
        this.leftYReverse.setSelected(BI.Utils.getWSLeftYAxisReversedByID(wId));
        this.leftYTitle.setVisible(this.leftYShowTitle.isSelected());
        this.leftYTitleStyle.setVisible(this.leftYShowTitle.isSelected());
        this.leftYTitleStyle.setValue(BI.Utils.getWSLeftTitleStyleByID(wId));
        this.leftYShowCustomScale.setSelected(BI.Utils.getWSShowYCustomScale(wId));
        this.leftYCustomScale.setValue(BI.Utils.getWSCustomYScale(wId));
        this.leftYCustomScale.setVisible(BI.Utils.getWSShowYCustomScale(wId));
        this.leftYSeparator.setSelected(BI.Utils.getWSNumberSeparatorsByID(wId));
        this.leftYShowLabel.setSelected(BI.Utils.getWSShowLValueAxisLabelByID(wId));
        this.leftYLabelStyle.setVisible(this.leftYShowLabel.isSelected());
        this.leftYLabelStyle.setValue(BI.Utils.getWSLValueAxisLabelSettingByID(wId));
        this.leftYLineColor.setValue(BI.Utils.getWSLValueAxisLineColorByID(wId));

        this.rightYNumberFormat.setValue(BI.Utils.getWSRightYAxisStyleByID(wId));
        this.rightYNumberLevel.setValue(BI.Utils.getWSRightYAxisNumLevelByID(wId));
        this.rightYUnit.setValue(BI.Utils.getWSRightYAxisUnitByID(wId));
        this.rightYShowTitle.setSelected(BI.Utils.getWSShowRightYAxisTitleByID(wId));
        this.rightYTitle.setValue(titleRY);
        this.rightYReverse.setSelected(BI.Utils.getWSRightYAxisReversedByID(wId));
        this.rightYTitle.setVisible(this.rightYShowTitle.isSelected());
        this.rightYTitleStyle.setVisible(this.rightYShowTitle.isSelected());
        this.rightYTitleStyle.setValue(BI.Utils.getWSRightTitleStyleByID(wId));
        this.rightYShowCustomScale.setSelected(BI.Utils.getWSShowXCustomScale(wId));
        this.rightYCustomScale.setValue(BI.Utils.getWSCustomXScale(wId));
        this.rightYCustomScale.setVisible(BI.Utils.getWSShowXCustomScale(wId));
        this.rightYSeparator.setSelected(BI.Utils.getWSRightNumberSeparatorsByID(wId));
        this.rightYShowLabel.setSelected(BI.Utils.getWSShowRValueAxisLabelByID(wId));
        this.rightYLabelStyle.setVisible(this.rightYShowLabel.isSelected());
        this.rightYLabelStyle.setValue(BI.Utils.getWSRValueAxisLabelSettingByID(wId));
        this.rightYLineColor.setValue(BI.Utils.getWSRValueAxisLineColorByID(wId));

        this.catShowTitle.setSelected(BI.Utils.getWSShowXAxisTitleByID(wId));
        this.catTitle.setValue(titleX);
        this.catTitle.setVisible(this.catShowTitle.isSelected());
        this.catTitleStyle.setVisible(this.catShowTitle.isSelected());
        this.catShowLabel.setSelected(BI.Utils.getWSShowCatLabelByID(wId));
        this.catLabelStyle.setVisible(this.catShowLabel.isSelected());
        this.catLabelStyle.setValue(BI.Utils.getWSCatLabelStyleByID(wId));
        this.catLineColor.setValue(BI.Utils.getWSCatLineColorByID(wId));
        this.catTitleStyle.setValue(BI.Utils.getWSCatTitleStyleByID(wId));

        this.legend.setValue(BI.Utils.getWSChartLegendByID(wId));
        this.legendStyle.setValue(BI.Utils.getWSLegendSettingByID(wId));
        this.showDataLabel.setSelected(BI.Utils.getWSShowDataLabelByID(wId));
        this.showDataTable.setSelected(BI.Utils.getWSShowDataTableByID(wId));
        this.showZoom.setSelected(BI.Utils.getWSShowZoomByID(wId));
        this.showHGridLine.setSelected(BI.Utils.getWSShowHGridLineByID(wId));
        this.hGridLineColor.setVisible(this.showHGridLine.isSelected());
        this.hGridLineColor.setValue(BI.Utils.getWSHGridLineColorByID(wId));
        this.showVGridLine.setSelected(BI.Utils.getWSShowVGridLineByID(wId));
        this.vGridLineColor.setVisible(this.showVGridLine.isSelected());
        this.vGridLineColor.setValue(BI.Utils.getWSVGridLineColorByID(wId));
        this.tooltipStyle.setValue(BI.Utils.getWSToolTipSettingByID(wId));

        this.transferFilter.setSelected(BI.Utils.getWSTransferFilterByID(wId));

        this.miniModel.setSelected(BI.Utils.getWSMinimalistByID(wId));
        this._invisible(!BI.Utils.getWSMinimalistByID(wId));
    },

    getValue: function () {
        return {
            showName: this.showName.isSelected(),
            widgetName: this.widgetName.getValue(),
            widgetNameStyle: this.widgetNameStyle.getValue(),

            chartColor: this.chartColor.getValue()[0],
            chartStyle: this.chartStyle.getValue()[0],
            widgetBG: this.widgetBG.getValue(),

            leftYNumberFormat: this.leftYNumberFormat.getValue()[0],
            leftYNumberLevel: this.leftYNumberLevel.getValue()[0],
            leftYUnit: this.leftYUnit.getValue(),
            leftYShowTitle: this.leftYShowTitle.isSelected(),
            leftYTitle: this.leftYTitle.getValue(),
            leftYReverse: this.leftYReverse.isSelected(),
            leftYShowCustomScale: this.leftYShowCustomScale.isSelected(),
            leftYCustomScale: this.leftYCustomScale.getValue(),
            leftYSeparator: this.leftYSeparator.isSelected(),
            leftYShowLabel: this.leftYShowLabel.isSelected(),
            leftYLabelStyle: this.leftYLabelStyle.getValue(),
            leftYLineColor: this.leftYLineColor.getValue(),
            leftYTitleStyle: this.leftYTitleStyle.getValue(),

            rightYNumberFormat: this.rightYNumberFormat.getValue()[0],
            rightYNumberLevel: this.rightYNumberLevel.getValue()[0],
            rightYUnit: this.rightYUnit.getValue(),
            rightYShowTitle: this.rightYShowTitle.isSelected(),
            rightYTitle: this.rightYTitle.getValue(),
            rightYReverse: this.rightYReverse.isSelected(),
            rightYTitleStyle: this.rightYTitleStyle.getValue(),
            rightYShowCustomScale: this.rightYShowCustomScale.isSelected(),
            rightYCustomScale: this.rightYCustomScale.getValue(),
            rightYSeparator: this.rightYSeparator.isSelected(),
            rightYShowLabel: this.rightYShowLabel.isSelected(),
            rightYLabelStyle: this.rightYLabelStyle.getValue(),
            rightYLineColor: this.rightYLineColor.getValue(),

            catShowTitle: this.catShowTitle.isSelected(),
            catTitle: this.catTitle.getValue(),
            catTitleStyle: this.catTitleStyle.getValue(),
            catShowLabel: this.catShowLabel.isSelected(),
            catLabelStyle: this.catLabelStyle.getValue(),
            catLineColor: this.catLineColor.getValue(),

            legend: this.legend.getValue()[0],
            legendStyle: this.legendStyle.getValue(),
            showDataLabel: this.showDataLabel.isSelected(),
            showDataTable: this.showDataTable.isSelected(),
            showZoom: this.showZoom.isSelected(),
            showHGridLine: this.showHGridLine.isSelected(),
            hGridLineColor: this.hGridLineColor.getValue(),
            showVGridLine: this.showVGridLine.isSelected(),
            vGridLineColor: this.vGridLineColor.getValue(),
            tooltipStyle: this.tooltipStyle.getValue(),

            miniModel: this.miniModel.isSelected(),

            transferFilter: this.transferFilter.isSelected(),
        }
    }
});
BI.AxisChartsSetting.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.axis_charts_setting", BI.AxisChartsSetting);