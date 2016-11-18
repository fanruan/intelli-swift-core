/**
 * @class BI.MultiAxisChartSetting
 * @extends BI.Widget
 * 多值轴组合图样式
 */
BI.MultiAxisChartSetting = BI.inherit(BI.AbstractChartSetting, {

    _defaultConfig: function () {
        return BI.extend(BI.MultiAxisChartSetting.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-charts-setting bi-multi-axis-chart-setting"
        })
    },

    _init: function () {
        BI.MultiAxisChartSetting.superclass._init.apply(this, arguments);
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
            self.fireEvent(BI.MultiAxisChartSetting.EVENT_CHANGE);
        });

        //组件标题
        this.widgetName = BI.createWidget({
            type: "bi.sign_editor",
            cls: "title-input",
            width: 120
        });

        this.widgetName.on(BI.SignEditor.EVENT_CHANGE, function () {
            self.fireEvent(BI.MultiAxisChartSetting.EVENT_CHANGE)
        });

        //详细设置
        this.widgetNameStyle = BI.createWidget({
            type: "bi.show_title_detailed_setting_combo"
        });

        this.widgetNameStyle.on(BI.ShowTitleDetailedSettingCombo.EVENT_CHANGE, function () {
            self.fireEvent(BI.MultiAxisChartSetting.EVENT_CHANGE)
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
            self.fireEvent(BI.MultiAxisChartSetting.EVENT_CHANGE);
        });

        //风格——1、2、3
        this.chartSyle = BI.createWidget({
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
        this.chartSyle.on(BI.ButtonGroup.EVENT_CHANGE, function () {
            self.fireEvent(BI.MultiAxisChartSetting.EVENT_CHANGE);
        });

        //组件背景
        this.widgetBG = BI.createWidget({
            type: "bi.global_style_index_background"
        });
        this.widgetBG.on(BI.GlobalStyleIndexBackground.EVENT_CHANGE, function () {
            self.fireEvent(BI.MultiAxisChartSetting.EVENT_CHANGE);
        });

        this.tableStyle = BI.createWidget({
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
                    text: BI.i18nText("BI-Table_Style"),
                    cls: "attr-names",
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.chartSyle]
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

        //数量级
        this.leftYNumberLevel = BI.createWidget({
            type: "bi.segment",
            width: constant.NUMBER_LEVEL_SEGMENT_WIDTH,
            height: constant.BUTTON_HEIGHT,
            items: BICst.TARGET_STYLE_LEVEL
        });

        this.leftYNumberLevel.on(BI.Segment.EVENT_CHANGE, function () {
            self.fireEvent(BI.MultiAxisChartSetting.EVENT_CHANGE);
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
            self.fireEvent(BI.MultiAxisChartSetting.EVENT_CHANGE);
        });

        this.leftYNumberFormat = BI.createWidget({
            type: "bi.segment",
            width: constant.FORMAT_SEGMENT_WIDTH,
            height: constant.BUTTON_HEIGHT,
            items: BICst.TARGET_STYLE_FORMAT
        });

        this.leftYNumberFormat.on(BI.Segment.EVENT_CHANGE, function () {
            self.fireEvent(BI.MultiAxisChartSetting.EVENT_CHANGE);
        });

        //千分符
        this.leftYSeparator = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Separators"),
            width: 80
        });

        this.leftYSeparator.on(BI.Controller.EVENT_CHANGE, function () {
            self.fireEvent(BI.MultiAxisChartSetting.EVENT_CHANGE);
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
            self.fireEvent(BI.MultiAxisChartSetting.EVENT_CHANGE);
        });

        this.leftYTitle = BI.createWidget({
            type: "bi.sign_editor",
            width: constant.EDITOR_WIDTH,
            height: constant.EDITOR_HEIGHT,
            cls: "unit-input"
        });
        this.leftYTitle.on(BI.SignEditor.EVENT_CONFIRM, function () {
            self.fireEvent(BI.MultiAxisChartSetting.EVENT_CHANGE);
        });

        this.leftYTitleStyle = BI.createWidget({
            type: "bi.legend_detailed_setting_combo"
        });

        this.leftYTitleStyle.on(BI.LegendDetailedSettingCombo.EVENT_CHANGE, function () {
            self.fireEvent(BI.MultiAxisChartSetting.EVENT_CHANGE)
        });

        //左轴标签
        this.leftYShowLabel = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Show_Label"),
            width: 100
        });

        this.leftYShowLabel.on(BI.Controller.EVENT_CHANGE, function () {
            self.leftYLabelStyle.setVisible(this.isSelected());
            self.fireEvent(BI.MultiAxisChartSetting.EVENT_CHANGE)
        });

        this.leftYLabelStyle = BI.createWidget({
            type: "bi.chart_label_detailed_setting_combo"
        });

        this.leftYLabelStyle.on(BI.ChartLabelDetailedSettingCombo.EVENT_CHANGE, function () {
            self.fireEvent(BI.MultiAxisChartSetting.EVENT_CHANGE)
        });

        //左轴线颜色
        this.leftYLineColor = BI.createWidget({
            type: "bi.color_chooser",
            width: 30,
            height: 30
        });

        this.leftYLineColor.on(BI.ColorChooser.EVENT_CHANGE, function () {
            self.fireEvent(BI.MultiAxisChartSetting.EVENT_CHANGE)
        });

        //轴逆序
        this.leftYReverse = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Reversed_Axis"),
            width: 80
        });

        this.leftYReverse.on(BI.Controller.EVENT_CHANGE, function () {
            self.fireEvent(BI.MultiAxisChartSetting.EVENT_CHANGE);
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
            self.fireEvent(BI.MultiAxisChartSetting.EVENT_CHANGE)
        });

        this.leftYCustomScale = BI.createWidget({
            type: "bi.custom_scale",
            wId: o.wId
        });

        this.leftYCustomScale.on(BI.CustomScale.EVENT_CHANGE, function () {
            self.fireEvent(BI.MultiAxisChartSetting.EVENT_CHANGE)
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
                }, /*{
                    type: "bi.label",
                    text: BI.i18nText("BI-Axis_Line_Color"),
                    cls: "attr-name"
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.leftYLineColor]
                }, */{
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

        this.rightYNumberLevel = BI.createWidget({
            type: "bi.segment",
            width: constant.NUMBER_LEVEL_SEGMENT_WIDTH,
            height: constant.BUTTON_HEIGHT,
            items: BICst.TARGET_STYLE_LEVEL
        });

        this.rightYNumberLevel.on(BI.Segment.EVENT_CHANGE, function () {
            self.fireEvent(BI.MultiAxisChartSetting.EVENT_CHANGE);
        });

        this.rightYUnit = BI.createWidget({
            type: "bi.sign_editor",
            width: constant.EDITOR_WIDTH,
            height: constant.EDITOR_HEIGHT,
            cls: "unit-input",
            watermark: BI.i18nText("BI-Custom_Input")
        });

        this.rightYUnit.on(BI.SignEditor.EVENT_CONFIRM, function () {
            self.fireEvent(BI.MultiAxisChartSetting.EVENT_CHANGE);
        });

        this.rightYNumberFormat = BI.createWidget({
            type: "bi.segment",
            width: constant.FORMAT_SEGMENT_WIDTH,
            height: constant.BUTTON_HEIGHT,
            items: BICst.TARGET_STYLE_FORMAT
        });

        this.rightYNumberFormat.on(BI.Segment.EVENT_CHANGE, function () {
            self.fireEvent(BI.MultiAxisChartSetting.EVENT_CHANGE);
        });

        this.rightYSeparator = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Separators"),
            width: 80
        });

        this.rightYSeparator.on(BI.Controller.EVENT_CHANGE, function () {
            self.fireEvent(BI.MultiAxisChartSetting.EVENT_CHANGE);
        });

        this.rightYShowTitle = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Show_Title"),
            width: 90
        });

        this.rightYShowTitle.on(BI.Controller.EVENT_CHANGE, function () {
            self.rightYTitle.setVisible(this.isSelected());
            self.rightYTitleStyle.setVisible(this.isSelected());
            self.fireEvent(BI.MultiAxisChartSetting.EVENT_CHANGE);
        });

        this.rightYTitle = BI.createWidget({
            type: "bi.sign_editor",
            width: constant.EDITOR_WIDTH,
            height: constant.EDITOR_HEIGHT,
            cls: "unit-input"
        });

        this.rightYTitle.on(BI.SignEditor.EVENT_CONFIRM, function () {
            self.fireEvent(BI.MultiAxisChartSetting.EVENT_CHANGE);
        });

        this.rightYTitleStyle = BI.createWidget({
            type: "bi.legend_detailed_setting_combo"
        });

        this.rightYTitleStyle.on(BI.LegendDetailedSettingCombo.EVENT_CHANGE, function() {
            self.fireEvent(BI.MultiAxisChartSetting.EVENT_CHANGE)
        });

        //右轴标签
        this.rightYShowLabel = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Show_Label"),
            width: 100
        });

        this.rightYShowLabel.on(BI.Controller.EVENT_CHANGE, function () {
            self.rightYLabelStyle.setVisible(this.isSelected());
            self.fireEvent(BI.MultiAxisChartSetting.EVENT_CHANGE)
        });

        this.rightYLabelStyle = BI.createWidget({
            type: "bi.chart_label_detailed_setting_combo"
        });

        this.rightYLabelStyle.on(BI.ChartLabelDetailedSettingCombo.EVENT_CHANGE, function () {
            self.fireEvent(BI.MultiAxisChartSetting.EVENT_CHANGE)
        });

        //右轴线颜色
        this.rightYLineColor = BI.createWidget({
            type: "bi.color_chooser",
            width: 30,
            height: 30
        });

        this.rightYLineColor.on(BI.ColorChooser.EVENT_CHANGE, function () {
            self.fireEvent(BI.MultiAxisChartSetting.EVENT_CHANGE)
        });

        this.rightYReverse = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Reversed_Axis"),
            width: 80
        });

        this.rightYReverse.on(BI.Controller.EVENT_CHANGE, function () {
            self.fireEvent(BI.MultiAxisChartSetting.EVENT_CHANGE);
        });

        //右轴1刻度自定义
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
            self.fireEvent(BI.MultiAxisChartSetting.EVENT_CHANGE)
        });

        this.rightYCustomScale = BI.createWidget({
            type: "bi.custom_scale",
            wId: o.wId
        });

        this.rightYCustomScale.on(BI.CustomScale.EVENT_CHANGE, function () {
            self.fireEvent(BI.MultiAxisChartSetting.EVENT_CHANGE)
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
                text: BI.i18nText("BI-Right_Value_Axis_One"),
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
                }, /*{
                    type: "bi.label",
                    text: BI.i18nText("BI-Axis_Line_Color"),
                    cls: "attr-name"
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.rightYLineColor]
                }, */{
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

        this.rightY2NumberLevel = BI.createWidget({
            type: "bi.segment",
            width: constant.NUMBER_LEVEL_SEGMENT_WIDTH,
            height: constant.BUTTON_HEIGHT,
            items: BICst.TARGET_STYLE_LEVEL
        });

        this.rightY2NumberLevel.on(BI.Segment.EVENT_CHANGE, function () {
            self.fireEvent(BI.MultiAxisChartSetting.EVENT_CHANGE);
        });

        this.rightY2Unit = BI.createWidget({
            type: "bi.sign_editor",
            width: constant.EDITOR_WIDTH,
            height: constant.EDITOR_HEIGHT,
            cls: "unit-input",
            watermark: BI.i18nText("BI-Custom_Input")
        });

        this.rightY2Unit.on(BI.SignEditor.EVENT_CONFIRM, function () {
            self.fireEvent(BI.MultiAxisChartSetting.EVENT_CHANGE);
        });

        this.rightY2NumberFormat = BI.createWidget({
            type: "bi.segment",
            width: constant.FORMAT_SEGMENT_WIDTH,
            height: constant.BUTTON_HEIGHT,
            items: BICst.TARGET_STYLE_FORMAT
        });

        this.rightY2NumberFormat.on(BI.Segment.EVENT_CHANGE, function () {
            self.fireEvent(BI.MultiAxisChartSetting.EVENT_CHANGE);
        });

        this.rightY2Separator = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Separators"),
            width: 80
        });

        this.rightY2Separator.on(BI.Controller.EVENT_CHANGE, function () {
            self.fireEvent(BI.MultiAxisChartSetting.EVENT_CHANGE);
        });

        this.rightY2ShowTitle = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Show_Title"),
            width: 90
        });

        this.rightY2ShowTitle.on(BI.Controller.EVENT_CHANGE, function () {
            self.rightY2Title.setVisible(this.isSelected());
            self.rightY2TitleStyle.setVisible(this.isSelected());
            self.fireEvent(BI.MultiAxisChartSetting.EVENT_CHANGE);
        });

        this.rightY2Title = BI.createWidget({
            type: "bi.sign_editor",
            width: constant.EDITOR_WIDTH,
            height: constant.EDITOR_HEIGHT,
            cls: "unit-input"
        });

        this.rightY2Title.on(BI.SignEditor.EVENT_CONFIRM, function () {
            self.fireEvent(BI.MultiAxisChartSetting.EVENT_CHANGE);
        });

        this.rightY2TitleStyle = BI.createWidget({
            type: "bi.legend_detailed_setting_combo"
        });

        this.rightY2TitleStyle.on(BI.LegendDetailedSettingCombo.EVENT_CHANGE, function () {
            self.fireEvent(BI.MultiAxisChartSetting.EVENT_CHANGE)
        });

        //右轴2标签
        this.rightY2ShowLabel = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Show_Label"),
            width: 100
        });

        this.rightY2ShowLabel.on(BI.Controller.EVENT_CHANGE, function () {
            self.rightY2LabelStyle.setVisible(this.isSelected());
            self.fireEvent(BI.MultiAxisChartSetting.EVENT_CHANGE)
        });

        this.rightY2LabelStyle = BI.createWidget({
            type: "bi.chart_label_detailed_setting_combo"
        });

        this.rightY2LabelStyle.on(BI.ChartLabelDetailedSettingCombo.EVENT_CHANGE, function () {
            self.fireEvent(BI.MultiAxisChartSetting.EVENT_CHANGE)
        });

        //右轴线颜色
        this.rightY2LineColor = BI.createWidget({
            type: "bi.color_chooser",
            width: 30,
            height: 30
        });

        this.rightY2LineColor.on(BI.ColorChooser.EVENT_CHANGE, function () {
            self.fireEvent(BI.MultiAxisChartSetting.EVENT_CHANGE)
        });

        this.rightY2Reverse = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Reversed_Axis"),
            width: 80
        });

        this.rightY2Reverse.on(BI.Controller.EVENT_CHANGE, function () {
            self.fireEvent(BI.MultiAxisChartSetting.EVENT_CHANGE);
        });

        //右轴2刻度自定义
        this.rightY2ShowCustomScale = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Scale_Customize"),
            width: 115
        });

        this.rightY2ShowCustomScale.on(BI.Controller.EVENT_CHANGE, function () {
            self.rightY2CustomScale.setVisible(this.isSelected());
            if (!this.isSelected()) {
                self.rightY2CustomScale.setValue({})
            }
            self.fireEvent(BI.MultiAxisChartSetting.EVENT_CHANGE)
        });

        this.rightY2CustomScale = BI.createWidget({
            type: "bi.custom_scale",
            wId: o.wId
        });

        this.rightY2CustomScale.on(BI.CustomScale.EVENT_CHANGE, function () {
            self.fireEvent(BI.MultiAxisChartSetting.EVENT_CHANGE)
        });

        this.rYAxis2 = BI.createWidget({
            type: "bi.horizontal_adapt",
            columnSize: [80],
            verticalAlign: "top",
            cls: "single-line-settings",
            items: [{
                type: "bi.label",
                textHeight: constant.SINGLE_LINE_HEIGHT,
                text: BI.i18nText("BI-Right_Value_Axis_Two"),
                lgap: constant.SIMPLE_H_LGAP,
                cls: "line-title",
                textAlign: "left"
            }, {
                type: "bi.left",
                cls: "detail-style",
                items: BI.createItems([{
                    type: "bi.label",
                    text: BI.i18nText("BI-Num_Level"),
                    cls: "attr-names"
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.rightY2NumberLevel]
                }, {
                    type: "bi.label",
                    text: BI.i18nText("BI-Unit_Normal"),
                    lgap: constant.SIMPLE_H_GAP,
                    cls: "attr-names"
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.rightY2Unit]
                }, {
                    type: "bi.label",
                    text: BI.i18nText("BI-Format"),
                    cls: "attr-names"
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.rightY2NumberFormat]
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.rightY2Separator]
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.rightY2ShowTitle, this.rightY2Title]
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.rightY2TitleStyle]
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.rightY2ShowLabel]
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.rightY2LabelStyle]
                }, /*{
                    type: "bi.label",
                    text: BI.i18nText("BI-Axis_Line_Color"),
                    cls: "attr-name"
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.rightY2LineColor]
                }, */{
                    type: "bi.vertical_adapt",
                    items: [this.rightY2Reverse]
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.rightY2ShowCustomScale]
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.rightY2CustomScale]
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
            self.fireEvent(BI.MultiAxisChartSetting.EVENT_CHANGE);
        });

        this.catTitle = BI.createWidget({
            type: "bi.sign_editor",
            width: constant.EDITOR_WIDTH,
            height: constant.EDITOR_HEIGHT,
            cls: "unit-input"
        });

        this.catTitle.on(BI.SignEditor.EVENT_CONFIRM, function () {
            self.fireEvent(BI.MultiAxisChartSetting.EVENT_CHANGE);
        });

        this.catTitleStyle = BI.createWidget({
            type: "bi.legend_detailed_setting_combo"
        });

        this.catTitleStyle.on(BI.LegendDetailedSettingCombo.EVENT_CHANGE, function () {
            self.fireEvent(BI.MultiAxisChartSetting.EVENT_CHANGE)
        });

        //显示分类轴标签
        this.catShowLabel = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Show_Label"),
            width: 100
        });

        this.catShowLabel.on(BI.Controller.EVENT_CHANGE, function () {
            self.catLabelStyle.setVisible(this.isSelected());
            self.fireEvent(BI.MultiAxisChartSetting.EVENT_CHANGE)
        });

        //分类轴标签
        this.catLabelStyle = BI.createWidget({
            type: "bi.chart_label_detailed_setting_combo"
        });

        this.catLabelStyle.on(BI.ChartLabelDetailedSettingCombo.EVENT_CHANGE, function () {
            self.fireEvent(BI.MultiAxisChartSetting.EVENT_CHANGE)
        });

        //分类轴线颜色
        this.catLineColor = BI.createWidget({
            type: "bi.color_chooser",
            width: 30,
            height: 30
        });

        this.catLineColor.on(BI.ColorChooser.EVENT_CHANGE, function () {
            self.fireEvent(BI.MultiAxisChartSetting.EVENT_CHANGE)
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
                cls: "line-title"
            }, {
                type: "bi.left",
                cls: "detail-style",
                items: BI.createItems([{
                    type: "bi.vertical_adapt",
                    items: [this.catShowTitle, this.catTitle]
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
            self.fireEvent(BI.MultiAxisChartSetting.EVENT_CHANGE);
        });

        //图例详细设置
        this.legendStyle = BI.createWidget({
            type: "bi.legend_detailed_setting_combo"
        });

        this.legendStyle.on(BI.LegendDetailedSettingCombo.EVENT_CHANGE, function () {
            self.fireEvent(BI.MultiAxisChartSetting.EVENT_CHANGE)
        });

        //网格线设置
        this.hShowGridLine = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Horizontal"),
            width: 65
        });

        this.hShowGridLine.on(BI.Controller.EVENT_CHANGE, function () {
            self.hGridLineColor.setVisible(this.isSelected());
            self.fireEvent(BI.MultiAxisChartSetting.EVENT_CHANGE)
        });

        this.hGridLineColor = BI.createWidget({
            type: "bi.color_chooser",
            width: 30,
            height: 30
        });

        this.hGridLineColor.on(BI.ColorChooser.EVENT_CHANGE, function () {
            self.fireEvent(BI.MultiAxisChartSetting.EVENT_CHANGE)
        });

        this.vShowGridLine = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Vertical"),
            width: 65
        });

        this.vShowGridLine.on(BI.Controller.EVENT_CHANGE, function () {
            self.vGridLineColor.setVisible(this.isSelected());
            self.fireEvent(BI.MultiAxisChartSetting.EVENT_CHANGE)
        });

        this.vGridLineColor = BI.createWidget({
            type: "bi.color_chooser",
            width: 30,
            height: 30
        });

        this.vGridLineColor.on(BI.ColorChooser.EVENT_CHANGE, function () {
            self.fireEvent(BI.MultiAxisChartSetting.EVENT_CHANGE)
        });

        //数据标签
        this.showDataLabel = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Show_Data_Label"),
            width: 115
        });

        this.showDataLabel.on(BI.Controller.EVENT_CHANGE, function () {
            self.fireEvent(BI.MultiAxisChartSetting.EVENT_CHANGE);
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
            self.fireEvent(BI.MultiAxisChartSetting.EVENT_CHANGE);
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
            self.fireEvent(BI.MultiAxisChartSetting.EVENT_CHANGE);
        });

        //数据点提示详细设置
        this.tooltipStyle = BI.createWidget({
            type: "bi.tooltip_detailed_setting_combo"
        });

        this.tooltipStyle.on(BI.TooltipDetailedSettingCombo.EVENT_CHANGE, function () {
            self.fireEvent(BI.MultiAxisChartSetting.EVENT_CHANGE)
        });

        //空值连续
        this.nullContinuity = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Null_Continue"),
            width: 90
        });

        this.nullContinuity.on(BI.Controller.EVENT_CHANGE, function() {
            self.fireEvent(BI.MultiAxisChartSetting.EVENT_CHANGE)
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
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.showDataTable]
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.showZoom]
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.nullContinuity]
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
            self.fireEvent(BI.MultiAxisChartSetting.EVENT_CHANGE);
        });

        this.otherAttr = BI.createWidget({
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

        //极简模式
        this.miniModel = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Minimalist_Model"),
            width: 170
        });

        this.miniModel.on(BI.Controller.EVENT_CHANGE, function () {
            self._invisible(!this.isSelected());
            self.fireEvent(BI.MultiAxisChartSetting.EVENT_CHANGE)
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
            items: [widgetTitle, this.tableStyle, this.lYAxis, this.rYAxis, this.rYAxis2, this.xAxis, this.showElement, this.otherAttr, modelChange],
            hgap: 10
        })
    },

    _invisible: function (v) {
        this.tableStyle.setVisible(v);
        this.lYAxis.setVisible(v);
        this.xAxis.setVisible(v);
        this.rYAxis.setVisible(v);
        this.rYAxis2.setVisible(v);
        this.showElement.setVisible(v);
        this.otherAttr.setVisible(v);
    },

    populate: function () {
        var wId = this.options.wId;

        var view = BI.Utils.getWidgetViewByID(wId);
        var titleLY = BI.Utils.getWSChartLeftYTitleByID(wId);
        var titleX = BI.Utils.getWSChartCatTitleByID(wId);
        var titleRY = BI.Utils.getWSChartRightYTitleByID(wId);
        var titleRY2 = BI.Utils.getWSChartRightY2TitleByID(wId);
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
        if (titleRY2 === "") {
            BI.any(view[BICst.REGION.TARGET3], function (idx, dId) {
                if (BI.Utils.isDimensionUsable(dId)) {
                    titleRY2 = BI.Utils.getDimensionNameByID(dId);
                    return true;
                }
                return false;
            });
        }
        this.showName.setSelected(BI.Utils.getWSShowNameByID(wId));
        this.widgetTitle.setVisible(BI.Utils.getWSShowNameByID(wId));
        this.widgetName.setValue(BI.Utils.getWidgetNameByID(wId));
        this.widgetNameStyle.setValue(BI.Utils.getWSTitleDetailSettingByID(wId));

        this.widgetBG.setValue(BI.Utils.getWSWidgetBGByID(wId));
        this.chartColor.setValue(BI.Utils.getWSChartColorByID(wId));
        this.chartSyle.setValue(BI.Utils.getWSChartStyleByID(wId));

        this.leftYNumberFormat.setValue(BI.Utils.getWSChartLeftYNumberFormatByID(wId));
        this.leftYNumberLevel.setValue(BI.Utils.getWSChartLeftYNumberLevelByID(wId));
        this.leftYUnit.setValue(BI.Utils.getWSLeftYAxisUnitByID(wId));
        this.leftYShowTitle.setSelected(BI.Utils.getWSChartLeftYShowTitleByID(wId));
        this.leftYTitle.setValue(titleLY);
        this.leftYReverse.setSelected(BI.Utils.getWSChartLeftYReverseByID(wId));
        this.leftYShowCustomScale.setSelected(BI.Utils.getWSChartLeftYShowCustomScaleByID(wId));
        this.leftYCustomScale.setValue(BI.Utils.getWSChartLeftYCustomScaleByID(wId));
        this.leftYCustomScale.setVisible(BI.Utils.getWSChartLeftYShowCustomScaleByID(wId));
        this.leftYSeparator.setSelected(BI.Utils.getWSLeftYNumberSeparatorByID(wId));
        this.leftYShowLabel.setSelected(BI.Utils.getWSChartLeftYShowLabelByID(wId));
        this.leftYLabelStyle.setValue(BI.Utils.getWSChartLeftYLabelStyleByID(wId));
        this.leftYLineColor.setValue(BI.Utils.getWSChartLeftYLineColorByID(wId));
        this.leftYTitleStyle.setValue(BI.Utils.getWSChartLeftYTitleStyleByID(wId));
        this.leftYTitleStyle.setVisible(this.leftYShowTitle.isSelected());
        this.leftYLabelStyle.setVisible(this.leftYShowLabel.isSelected());
        this.leftYTitle.setVisible(this.leftYShowTitle.isSelected());

        this.rightYNumberFormat.setValue(BI.Utils.getWSChartRightYNumberFormatByID(wId));
        this.rightYNumberLevel.setValue(BI.Utils.getWSChartRightYNumberLevelByID(wId));
        this.rightYUnit.setValue(BI.Utils.getWSChartRightYUnitByID(wId));
        this.rightYShowTitle.setSelected(BI.Utils.getWSChartRightYShowTitleByID(wId));
        this.rightYTitle.setValue(titleRY);
        this.rightYReverse.setSelected(BI.Utils.getWSChartRightYReverseByID(wId));
        this.rightYShowCustomScale.setSelected(BI.Utils.getWSChartRightYShowCustomScaleByID(wId));
        this.rightYCustomScale.setValue(BI.Utils.getWSChartRightYCustomScaleByID(wId));
        this.rightYCustomScale.setVisible(BI.Utils.getWSChartRightYShowCustomScaleByID(wId));
        this.rightYSeparator.setSelected(BI.Utils.getWSRightYNumberSeparatorByID(wId));
        this.rightYShowLabel.setSelected(BI.Utils.getWSRightYShowLabelByID(wId));
        this.rightYLabelStyle.setValue(BI.Utils.getWSRightYLabelStyleByID(wId));
        this.rightYLineColor.setValue(BI.Utils.getWSRightYLineColorByID(wId));
        this.rightYTitleStyle.setValue(BI.Utils.getWSChartRightYTitleStyleByID(wId));
        this.rightYTitle.setVisible(this.rightYShowTitle.isSelected());
        this.rightYTitleStyle.setVisible(this.rightYShowTitle.isSelected());
        this.rightYLabelStyle.setVisible(this.rightYShowLabel.isSelected());

        this.rightY2NumberFormat.setValue(BI.Utils.getWSChartRightY2NumberFormatByID(wId));
        this.rightY2NumberLevel.setValue(BI.Utils.getWSChartRightY2NumberLevelByID(wId));
        this.rightY2Unit.setValue(BI.Utils.getWSChartRightYAxis2UnitByID(wId));
        this.rightY2ShowTitle.setSelected(BI.Utils.getWSChartRightY2ShowTitleByID(wId));
        this.rightY2Title.setValue(titleRY2);
        this.rightY2Reverse.setSelected(BI.Utils.getWSChartRightY2ReverseByID(wId));
        this.rightY2ShowCustomScale.setSelected(BI.Utils.getWSChartRightY2ShowCustomScaleByID(wId));
        this.rightY2CustomScale.setValue(BI.Utils.getWSChartRightY2CustomScaleByID(wId));
        this.rightY2CustomScale.setVisible(BI.Utils.getWSChartRightY2ShowCustomScaleByID(wId));
        this.rightY2Separator.setSelected(BI.Utils.getWSRightY2NumberSeparatorByID(wId));
        this.rightY2ShowLabel.setSelected(BI.Utils.getWSRightY2ShowLabelByID(wId));
        this.rightY2LabelStyle.setValue(BI.Utils.getWSRightY2LabelStyleByID(wId));
        this.rightY2LineColor.setValue(BI.Utils.getWSRightY2LineColorByID(wId));
        this.rightY2TitleStyle.setValue(BI.Utils.getWSChartRightY2TitleStyleByID(wId));
        this.rightY2Title.setVisible(this.rightY2ShowTitle.isSelected());
        this.rightY2TitleStyle.setVisible(this.rightY2ShowTitle.isSelected());
        this.rightY2LabelStyle.setVisible(this.rightY2ShowLabel.isSelected());

        this.catShowTitle.setSelected(BI.Utils.getWSChartCatShowTitleByID(wId));
        this.catTitle.setValue(titleX);
        this.catShowLabel.setSelected(BI.Utils.getWSChartCatShowLabelByID(wId));
        this.catLabelStyle.setValue(BI.Utils.getWSChartCatLabelStyleByID(wId));
        this.catLineColor.setValue(BI.Utils.getWSChartCatLineColorByID(wId));
        this.catTitleStyle.setValue(BI.Utils.getWSChartCatTitleStyleByID(wId));
        this.catTitle.setVisible(this.catShowTitle.isSelected());
        this.catTitleStyle.setVisible(this.catShowTitle.isSelected());
        this.catLabelStyle.setVisible(this.catShowLabel.isSelected());

        this.legend.setValue(BI.Utils.getWSChartLegendByID(wId));
        this.legendStyle.setValue(BI.Utils.getWSChartLegendStyleByID(wId));
        this.showDataLabel.setSelected(BI.Utils.getWSChartShowDataLabelByID(wId));
        this.showDataTable.setSelected(BI.Utils.getWSChartShowDataTableByID(wId));
        this.showZoom.setSelected(BI.Utils.getWSChartShowZoomByID(wId));
        this.hShowGridLine.setSelected(BI.Utils.getWSChartHShowGridLineByID(wId));
        this.hGridLineColor.setValue(BI.Utils.getWSChartHGridLineColorByID(wId));
        this.hGridLineColor.setVisible(this.hShowGridLine.isSelected());
        this.vShowGridLine.setSelected(BI.Utils.getWSChartVShowGridLineByID(wId));
        this.vGridLineColor.setVisible(this.vShowGridLine.isSelected());
        this.vGridLineColor.setValue(BI.Utils.getWSChartVGridLineColorByID(wId));
        this.nullContinuity.setSelected(BI.Utils.getWSNullContinuityByID(wId));
        this.tooltipStyle.setValue(BI.Utils.getWSChartToolTipStyleByID(wId));

        this.transferFilter.setSelected(BI.Utils.getWSTransferFilterByID(wId));

        this.miniModel.setSelected(BI.Utils.getWSMinimalistByID(wId));
        this._invisible(!BI.Utils.getWSMinimalistByID(wId));
    },

    getValue: function () {
        return {
            showName: this.showName.isSelected(),
            widgetName: this.widgetName.getValue(),
            widgetNameStyle: this.widgetNameStyle.getValue(),

            widgetBG: this.widgetBG.getValue(),
            chartColor: this.chartColor.getValue()[0],
            chartStyle: this.chartSyle.getValue()[0],

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
            rightYShowCustomScale: this.rightYShowCustomScale.isSelected(),
            rightYCustomScale: this.rightYCustomScale.getValue(),
            rightYSeparator: this.rightYSeparator.isSelected(),
            rightYShowLabel: this.rightYShowLabel.isSelected(),
            rightYLabelStyle: this.rightYLabelStyle.getValue(),
            rightYLineColor: this.rightYLineColor.getValue(),
            rightYTitleStyle: this.rightYTitleStyle.getValue(),

            rightY2NumberFormat: this.rightY2NumberFormat.getValue()[0],
            rightY2NumberLevel: this.rightY2NumberLevel.getValue()[0],
            rightY2Unit: this.rightY2Unit.getValue(),
            rightY2ShowTitle: this.rightY2ShowTitle.isSelected(),
            rightY2Title: this.rightY2Title.getValue(),
            rightY2Reverse: this.rightY2Reverse.isSelected(),
            rightY2ShowCustomScale: this.rightY2ShowCustomScale.isSelected(),
            rightY2CustomScale: this.rightY2CustomScale.getValue(),
            rightY2Separator: this.rightYSeparator.isSelected(),
            rightY2ShowLabel: this.rightY2ShowLabel.isSelected(),
            rightY2LabelStyle: this.rightY2LabelStyle.getValue(),
            rightY2LineColor: this.rightY2LineColor.getValue(),
            rightY2TitleStyle: this.rightY2TitleStyle.getValue(),

            catShowTitle: this.catShowTitle.isSelected(),
            catTitle: this.catTitle.getValue(),
            catShowLabel: this.catShowLabel.isSelected(),
            catLabelStyle: this.catLabelStyle.getValue(),
            catLineColor: this.catLineColor.getValue(),
            catTitleStyle: this.catTitleStyle.getValue(),

            legend: this.legend.getValue()[0],
            showDataLabel: this.showDataLabel.isSelected(),
            showDataTable: this.showDataTable.isSelected(),
            showZoom: this.showZoom.isSelected(),
            legendStyle: this.legendStyle.getValue(),
            hShowGridLine: this.hShowGridLine.isSelected(),
            hGridLineColor: this.hGridLineColor.getValue(),
            vShowGridLine: this.vShowGridLine.isSelected(),
            vGridLineColor: this.vGridLineColor.getValue(),
            nullContinuity: this.nullContinuity.isSelected(),
            tooltipStyle: this.tooltipStyle.getValue(),

            transferFilter: this.transferFilter.isSelected(),

            miniModel: this.miniModel.isSelected(),

        }
    }
});
BI.MultiAxisChartSetting.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.multi_axis_chart_setting", BI.MultiAxisChartSetting);
