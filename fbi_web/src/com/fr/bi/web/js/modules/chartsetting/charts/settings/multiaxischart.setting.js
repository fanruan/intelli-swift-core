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
            self.fireEvent(BI.MultiAxisChartSetting.EVENT_CHANGE);
        });

        //组件标题
        this.title = BI.createWidget({
            type: "bi.sign_editor",
            cls: "title-input",
            width: 120
        });

        this.title.on(BI.SignEditor.EVENT_CHANGE, function () {
            self.fireEvent(BI.MultiAxisChartSetting.EVENT_CHANGE)
        });

        //详细设置
        this.titleDetailSettting = BI.createWidget({
            type: "bi.show_title_detailed_setting_combo"
        });

        this.titleDetailSettting.on(BI.ShowTitleDetailedSettingCombo.EVENT_CHANGE, function () {
            self.fireEvent(BI.MultiAxisChartSetting.EVENT_CHANGE)
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
            self.fireEvent(BI.MultiAxisChartSetting.EVENT_CHANGE);
        });

        //风格——1、2、3
        this.chartSyleGroup = BI.createWidget({
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
        this.chartSyleGroup.on(BI.ButtonGroup.EVENT_CHANGE, function () {
            self.fireEvent(BI.MultiAxisChartSetting.EVENT_CHANGE);
        });

        //组件背景
        this.widgetBackground = BI.createWidget({
            type: "bi.global_style_index_background"
        });
        this.widgetBackground.on(BI.GlobalStyleIndexBackground.EVENT_CHANGE, function () {
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
                    items: [this.colorSelect]
                }, {
                    type: "bi.label",
                    text: BI.i18nText("BI-Table_Style"),
                    cls: "attr-names",
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.chartSyleGroup]
                }, {
                    type: "bi.label",
                    text: BI.i18nText("BI-Widget_Background_Colour"),
                    cls: "attr-names",
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
            self.fireEvent(BI.MultiAxisChartSetting.EVENT_CHANGE);
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
            self.fireEvent(BI.MultiAxisChartSetting.EVENT_CHANGE);
        });

        this.lYAxisStyle = BI.createWidget({
            type: "bi.segment",
            width: constant.FORMAT_SEGMENT_WIDTH,
            height: constant.BUTTON_HEIGHT,
            items: BICst.TARGET_STYLE_FORMAT
        });

        this.lYAxisStyle.on(BI.Segment.EVENT_CHANGE, function () {
            self.fireEvent(BI.MultiAxisChartSetting.EVENT_CHANGE);
        });

        //千分符
        this.separatorsLeft = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Separators"),
            width: 80
        });

        this.separatorsLeft.on(BI.Controller.EVENT_CHANGE, function () {
            self.fireEvent(BI.MultiAxisChartSetting.EVENT_CHANGE);
        });

        //显示标题
        this.isShowTitleLY = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Show_Title"),
            width: 90
        });

        this.isShowTitleLY.on(BI.Controller.EVENT_CHANGE, function () {
            this.isSelected() ? self.editTitleLY.setVisible(true) : self.editTitleLY.setVisible(false);
            self.fireEvent(BI.MultiAxisChartSetting.EVENT_CHANGE);
        });

        this.editTitleLY = BI.createWidget({
            type: "bi.sign_editor",
            width: constant.EDITOR_WIDTH,
            height: constant.EDITOR_HEIGHT,
            cls: "unit-input"
        });
        this.editTitleLY.on(BI.SignEditor.EVENT_CONFIRM, function () {
            self.fireEvent(BI.MultiAxisChartSetting.EVENT_CHANGE);
        });

        //左轴标签
        this.showLeftLabel = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Show_Label"),
            width: 100
        });

        this.showLeftLabel.on(BI.Controller.EVENT_CHANGE, function () {
            self.fireEvent(BI.MultiAxisChartSetting.EVENT_CHANGE)
        });

        this.leftLabelStyle = BI.createWidget({
            type: "bi.chart_label_detailed_setting_combo"
        });

        this.leftLabelStyle.on(BI.ChartLabelDetailedSettingCombo.EVENT_CHANGE, function () {
            self.fireEvent(BI.MultiAxisChartSetting.EVENT_CHANGE)
        });

        //左轴线颜色
        this.leftLineColor = BI.createWidget({
            type: "bi.color_chooser",
            width: 30,
            height: 30
        });

        this.leftLineColor.on(BI.ColorChooser.EVENT_CHANGE, function () {
            self.fireEvent(BI.MultiAxisChartSetting.EVENT_CHANGE)
        });

        //轴逆序
        this.reversedLY = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Reversed_Axis"),
            width: 80
        });

        this.reversedLY.on(BI.Controller.EVENT_CHANGE, function () {
            self.fireEvent(BI.MultiAxisChartSetting.EVENT_CHANGE);
        });

        //左轴刻度自定义
        this.showYCustomScale = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Scale_Customize"),
            width: 115
        });

        this.showYCustomScale.on(BI.Controller.EVENT_CHANGE, function () {
            self.customYScale.setVisible(this.isSelected());
            if (!this.isSelected()) {
                self.customYScale.setValue({})
            }
            self.fireEvent(BI.MultiAxisChartSetting.EVENT_CHANGE)
        });

        this.customYScale = BI.createWidget({
            type: "bi.custom_scale",
            wId: o.wId
        });

        this.customYScale.on(BI.CustomScale.EVENT_CHANGE, function () {
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
                    items: [this.separatorsLeft]
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.isShowTitleLY, this.editTitleLY]
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.showLeftLabel]
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.leftLabelStyle]
                }, {
                    type: "bi.label",
                    text: BI.i18nText("BI-Axis_Line_Color"),
                    cls: "attr-name"
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.leftLineColor]
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.reversedLY]
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.showYCustomScale]
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.customYScale]
                }], {
                    height: constant.SINGLE_LINE_HEIGHT
                }),
                lgap: constant.SIMPLE_H_GAP
            }]
        });

        this.numberLevelrY = BI.createWidget({
            type: "bi.segment",
            width: constant.NUMBER_LEVEL_SEGMENT_WIDTH,
            height: constant.BUTTON_HEIGHT,
            items: BICst.TARGET_STYLE_LEVEL
        });

        this.numberLevelrY.on(BI.Segment.EVENT_CHANGE, function () {
            self.fireEvent(BI.MultiAxisChartSetting.EVENT_CHANGE);
        });

        this.RYUnit = BI.createWidget({
            type: "bi.sign_editor",
            width: constant.EDITOR_WIDTH,
            height: constant.EDITOR_HEIGHT,
            cls: "unit-input",
            watermark: BI.i18nText("BI-Custom_Input")
        });

        this.RYUnit.on(BI.SignEditor.EVENT_CONFIRM, function () {
            self.fireEvent(BI.MultiAxisChartSetting.EVENT_CHANGE);
        });

        this.rYAxisStyle = BI.createWidget({
            type: "bi.segment",
            width: constant.FORMAT_SEGMENT_WIDTH,
            height: constant.BUTTON_HEIGHT,
            items: BICst.TARGET_STYLE_FORMAT
        });

        this.rYAxisStyle.on(BI.Segment.EVENT_CHANGE, function () {
            self.fireEvent(BI.MultiAxisChartSetting.EVENT_CHANGE);
        });

        this.separatorsRight = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Separators"),
            width: 80
        });

        this.separatorsRight.on(BI.Controller.EVENT_CHANGE, function () {
            self.fireEvent(BI.MultiAxisChartSetting.EVENT_CHANGE);
        });

        this.isShowTitleRY = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Show_Title"),
            width: 90
        });

        this.isShowTitleRY.on(BI.Controller.EVENT_CHANGE, function () {
            this.isSelected() ? self.editTitleRY.setVisible(true) : self.editTitleRY.setVisible(false);
            self.fireEvent(BI.MultiAxisChartSetting.EVENT_CHANGE);
        });

        this.editTitleRY = BI.createWidget({
            type: "bi.sign_editor",
            width: constant.EDITOR_WIDTH,
            height: constant.EDITOR_HEIGHT,
            cls: "unit-input"
        });

        this.editTitleRY.on(BI.SignEditor.EVENT_CONFIRM, function () {
            self.fireEvent(BI.MultiAxisChartSetting.EVENT_CHANGE);
        });

        //右轴标签
        this.showRightLabel = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Show_Label"),
            width: 100
        });

        this.showRightLabel.on(BI.Controller.EVENT_CHANGE, function () {
            self.fireEvent(BI.MultiAxisChartSetting.EVENT_CHANGE)
        });

        this.rightLabelStyle = BI.createWidget({
            type: "bi.chart_label_detailed_setting_combo"
        });

        this.rightLabelStyle.on(BI.ChartLabelDetailedSettingCombo.EVENT_CHANGE, function () {
            self.fireEvent(BI.MultiAxisChartSetting.EVENT_CHANGE)
        });

        //右轴线颜色
        this.rightLineColor = BI.createWidget({
            type: "bi.color_chooser",
            width: 30,
            height: 30
        });

        this.rightLineColor.on(BI.ColorChooser.EVENT_CHANGE, function () {
            self.fireEvent(BI.MultiAxisChartSetting.EVENT_CHANGE)
        });

        this.reversedRY = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Reversed_Axis"),
            width: 80
        });

        this.reversedRY.on(BI.Controller.EVENT_CHANGE, function () {
            self.fireEvent(BI.MultiAxisChartSetting.EVENT_CHANGE);
        });

        //右轴1刻度自定义
        this.showXCustomScale = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Scale_Customize"),
            width: 115
        });

        this.showXCustomScale.on(BI.Controller.EVENT_CHANGE, function () {
            self.customXScale.setVisible(this.isSelected());
            if (!this.isSelected()) {
                self.customXScale.setValue({})
            }
            self.fireEvent(BI.MultiAxisChartSetting.EVENT_CHANGE)
        });

        this.customXScale = BI.createWidget({
            type: "bi.custom_scale",
            wId: o.wId
        });

        this.customXScale.on(BI.CustomScale.EVENT_CHANGE, function () {
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
                    items: [this.numberLevelrY]
                }, {
                    type: "bi.label",
                    text: BI.i18nText("BI-Unit_Normal"),
                    cls: "attr-names"
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.RYUnit]
                }, {
                    type: "bi.label",
                    text: BI.i18nText("BI-Format"),
                    cls: "attr-names"
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.rYAxisStyle]
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.separatorsRight]
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.isShowTitleRY, this.editTitleRY]
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.showRightLabel]
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.rightLabelStyle]
                }, {
                    type: "bi.label",
                    text: BI.i18nText("BI-Axis_Line_Color"),
                    cls: "attr-name"
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.rightLineColor]
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.reversedRY]
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.showXCustomScale]
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.customXScale]
                }], {
                    height: constant.SINGLE_LINE_HEIGHT
                }),
                lgap: constant.SIMPLE_H_GAP
            }]
        });

        this.numberLevelrY2 = BI.createWidget({
            type: "bi.segment",
            width: constant.NUMBER_LEVEL_SEGMENT_WIDTH,
            height: constant.BUTTON_HEIGHT,
            items: BICst.TARGET_STYLE_LEVEL
        });

        this.numberLevelrY2.on(BI.Segment.EVENT_CHANGE, function () {
            self.fireEvent(BI.MultiAxisChartSetting.EVENT_CHANGE);
        });

        this.RY2Unit = BI.createWidget({
            type: "bi.sign_editor",
            width: constant.EDITOR_WIDTH,
            height: constant.EDITOR_HEIGHT,
            cls: "unit-input",
            watermark: BI.i18nText("BI-Custom_Input")
        });

        this.RY2Unit.on(BI.SignEditor.EVENT_CONFIRM, function () {
            self.fireEvent(BI.MultiAxisChartSetting.EVENT_CHANGE);
        });

        this.rYAxis2Style = BI.createWidget({
            type: "bi.segment",
            width: constant.FORMAT_SEGMENT_WIDTH,
            height: constant.BUTTON_HEIGHT,
            items: BICst.TARGET_STYLE_FORMAT
        });

        this.rYAxis2Style.on(BI.Segment.EVENT_CHANGE, function () {
            self.fireEvent(BI.MultiAxisChartSetting.EVENT_CHANGE);
        });

        this.separatorsRight2 = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Separators"),
            width: 80
        });

        this.separatorsRight2.on(BI.Controller.EVENT_CHANGE, function () {
            self.fireEvent(BI.MultiAxisChartSetting.EVENT_CHANGE);
        });

        this.isShowTitleRY2 = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Show_Title"),
            width: 90
        });

        this.isShowTitleRY2.on(BI.Controller.EVENT_CHANGE, function () {
            this.isSelected() ? self.editTitleRY2.setVisible(true) : self.editTitleRY2.setVisible(false);
            self.fireEvent(BI.MultiAxisChartSetting.EVENT_CHANGE);
        });

        this.editTitleRY2 = BI.createWidget({
            type: "bi.sign_editor",
            width: constant.EDITOR_WIDTH,
            height: constant.EDITOR_HEIGHT,
            cls: "unit-input"
        });

        this.editTitleRY2.on(BI.SignEditor.EVENT_CONFIRM, function () {
            self.fireEvent(BI.MultiAxisChartSetting.EVENT_CHANGE);
        });

        //右轴2标签
        this.showRight2Label = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Show_Label"),
            width: 100
        });

        this.showRight2Label.on(BI.Controller.EVENT_CHANGE, function () {
            self.fireEvent(BI.MultiAxisChartSetting.EVENT_CHANGE)
        });

        this.right2LabelStyle = BI.createWidget({
            type: "bi.chart_label_detailed_setting_combo"
        });

        this.right2LabelStyle.on(BI.ChartLabelDetailedSettingCombo.EVENT_CHANGE, function () {
            self.fireEvent(BI.MultiAxisChartSetting.EVENT_CHANGE)
        });

        //右轴线颜色
        this.right2LineColor = BI.createWidget({
            type: "bi.color_chooser",
            width: 30,
            height: 30
        });

        this.right2LineColor.on(BI.ColorChooser.EVENT_CHANGE, function () {
            self.fireEvent(BI.MultiAxisChartSetting.EVENT_CHANGE)
        });

        this.reversedRY2 = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Reversed_Axis"),
            width: 80
        });

        this.reversedRY2.on(BI.Controller.EVENT_CHANGE, function () {
            self.fireEvent(BI.MultiAxisChartSetting.EVENT_CHANGE);
        });

        //右轴2刻度自定义
        this.showZCustomScale = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Scale_Customize"),
            width: 115
        });

        this.showZCustomScale.on(BI.Controller.EVENT_CHANGE, function () {
            self.customZScale.setVisible(this.isSelected());
            if (!this.isSelected()) {
                self.customZScale.setValue({})
            }
            self.fireEvent(BI.MultiAxisChartSetting.EVENT_CHANGE)
        });

        this.customZScale = BI.createWidget({
            type: "bi.custom_scale",
            wId: o.wId
        });

        this.customZScale.on(BI.CustomScale.EVENT_CHANGE, function () {
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
                    items: [this.numberLevelrY2]
                }, {
                    type: "bi.label",
                    text: BI.i18nText("BI-Unit_Normal"),
                    lgap: constant.SIMPLE_H_GAP,
                    cls: "attr-names"
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.RY2Unit]
                }, {
                    type: "bi.label",
                    text: BI.i18nText("BI-Format"),
                    cls: "attr-names"
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.rYAxis2Style]
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.separatorsRight2]
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.isShowTitleRY2, this.editTitleRY2]
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.showRight2Label]
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.right2LabelStyle]
                }, {
                    type: "bi.label",
                    text: BI.i18nText("BI-Axis_Line_Color"),
                    cls: "attr-name"
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.right2LineColor]
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.reversedRY2]
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.showZCustomScale]
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.customZScale]
                }], {
                    height: constant.SINGLE_LINE_HEIGHT
                }),
                lgap: constant.SIMPLE_H_GAP
            }]
        });

        this.isShowTitleX = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Show_Title"),
            width: 90
        });

        this.isShowTitleX.on(BI.Controller.EVENT_CHANGE, function () {
            this.isSelected() ? self.editTitleX.setVisible(true) : self.editTitleX.setVisible(false);
            self.fireEvent(BI.MultiAxisChartSetting.EVENT_CHANGE);
        });

        this.editTitleX = BI.createWidget({
            type: "bi.sign_editor",
            width: constant.EDITOR_WIDTH,
            height: constant.EDITOR_HEIGHT,
            cls: "unit-input"
        });

        this.editTitleX.on(BI.SignEditor.EVENT_CONFIRM, function () {
            self.fireEvent(BI.MultiAxisChartSetting.EVENT_CHANGE);
        });

        //显示分类轴标签
        this.showCatLabel = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Show_Label"),
            width: 100
        });

        this.showCatLabel.on(BI.Controller.EVENT_CHANGE, function () {
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
                    items: [this.isShowTitleX, this.editTitleX]
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.showCatLabel]
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
        this.legendSetting = BI.createWidget({
            type: "bi.legend_detailed_setting_combo"
        });

        this.legendSetting.on(BI.LegendDetailedSettingCombo.EVENT_CHANGE, function () {
            self.fireEvent(BI.MultiAxisChartSetting.EVENT_CHANGE)
        });

        //网格线设置
        this.showHGridLine = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Horizontal"),
            width: 65
        });

        this.showHGridLine.on(BI.Controller.EVENT_CHANGE, function () {
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

        this.showVGridLine = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Vertical"),
            width: 65
        });

        this.showVGridLine.on(BI.Controller.EVENT_CHANGE, function () {
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
        this.tooltipSetting = BI.createWidget({
            type: "bi.tooltip_detailed_setting_combo"
        });

        this.tooltipSetting.on(BI.TooltipDetailedSettingCombo.EVENT_CHANGE, function () {
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
                    items: [this.legendSetting]
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
                }, {
                    type: "bi.label",
                    text: BI.i18nText("BI-Tooltip"),
                    cls: "attr-names"
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.tooltipSetting]
                }, {
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
        this.minimalistModel = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Minimalist_Model"),
            width: 170
        });

        this.minimalistModel.on(BI.Controller.EVENT_CHANGE, function () {
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
                }, this.minimalistModel]
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
        var titleLY = BI.Utils.getWSLeftYAxisTitleByID(wId);
        var titleX = BI.Utils.getWSXAxisTitleByID(wId);
        var titleRY = BI.Utils.getWSRightYAxisTitleByID(wId);
        var titleRY2 = BI.Utils.getWSRightYAxis2TitleByID(wId);
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
        this.showTitle.setSelected(BI.Utils.getWSShowNameByID(wId));
        this.widgetTitle.setVisible(BI.Utils.getWSShowNameByID(wId));
        this.title.setValue(BI.Utils.getWidgetNameByID(wId));
        this.titleDetailSettting.setValue(BI.Utils.getWSTitleDetailSettingByID(wId));
        this.widgetBackground.setValue(BI.Utils.getWSWidgetBGByID(wId));
        this.transferFilter.setSelected(BI.Utils.getWSTransferFilterByID(wId));
        this.colorSelect.setValue(BI.Utils.getWSChartColorByID(wId));
        this.chartSyleGroup.setValue(BI.Utils.getWSChartStyleByID(wId));
        this.lYAxisStyle.setValue(BI.Utils.getWSLeftYAxisStyleByID(wId));
        this.rYAxisStyle.setValue(BI.Utils.getWSRightYAxisStyleByID(wId));
        this.rYAxis2Style.setValue(BI.Utils.getWSRightYAxis2StyleByID(wId));
        this.numberLevellY.setValue(BI.Utils.getWSLeftYAxisNumLevelByID(wId));
        this.numberLevelrY.setValue(BI.Utils.getWSRightYAxisNumLevelByID(wId));
        this.numberLevelrY2.setValue(BI.Utils.getWSRightYAxis2NumLevelByID(wId));
        this.LYUnit.setValue(BI.Utils.getWSLeftYAxisUnitByID(wId));
        this.RYUnit.setValue(BI.Utils.getWSRightYAxisUnitByID(wId));
        this.RY2Unit.setValue(BI.Utils.getWSRightYAxis2UnitByID(wId));
        this.isShowTitleLY.setSelected(BI.Utils.getWSShowLeftYAxisTitleByID(wId));
        this.isShowTitleRY.setSelected(BI.Utils.getWSShowRightYAxisTitleByID(wId));
        this.isShowTitleRY2.setSelected(BI.Utils.getWSShowRightYAxis2TitleByID(wId));
        this.isShowTitleX.setSelected(BI.Utils.getWSShowXAxisTitleByID(wId));
        this.editTitleLY.setValue(titleLY);
        this.editTitleRY.setValue(titleRY);
        this.editTitleRY2.setValue(titleRY2);
        this.editTitleX.setValue(titleX);
        this.reversedLY.setSelected(BI.Utils.getWSLeftYAxisReversedByID(wId));
        this.reversedRY.setSelected(BI.Utils.getWSRightYAxisReversedByID(wId));
        this.reversedRY2.setSelected(BI.Utils.getWSRightYAxis2ReversedByID(wId));
        this.legend.setValue(BI.Utils.getWSChartLegendByID(wId));
        this.showDataLabel.setSelected(BI.Utils.getWSShowDataLabelByID(wId));
        this.showDataTable.setSelected(BI.Utils.getWSShowDataTableByID(wId));
        this.showZoom.setSelected(BI.Utils.getWSShowZoomByID(wId));
        this.minimalistModel.setSelected(BI.Utils.getWSMinimalistByID(wId));
        this._invisible(!BI.Utils.getWSMinimalistByID(wId));
        this.showYCustomScale.setSelected(BI.Utils.getWSShowYCustomScale(wId));
        this.customYScale.setValue(BI.Utils.getWSCustomYScale(wId));
        this.customYScale.setVisible(BI.Utils.getWSShowYCustomScale(wId));
        this.showXCustomScale.setSelected(BI.Utils.getWSShowXCustomScale(wId));
        this.customXScale.setValue(BI.Utils.getWSCustomXScale(wId));
        this.customXScale.setVisible(BI.Utils.getWSShowXCustomScale(wId));
        this.showZCustomScale.setSelected(BI.Utils.getWSShowZCustomScale(wId));
        this.customZScale.setValue(BI.Utils.getWSCustomZScale(wId));
        this.customZScale.setVisible(BI.Utils.getWSShowZCustomScale(wId));
        this.separatorsLeft.setSelected(BI.Utils.getWSNumberSeparatorsByID(wId));
        this.separatorsRight.setSelected(BI.Utils.getWSRightNumberSeparatorsByID(wId));
        this.separatorsRight2.setSelected(BI.Utils.getWSRight2NumberSeparatorsByID(wId));
        this.showLeftLabel.setSelected(BI.Utils.getWSShowLValueAxisLabelByID(wId));
        this.leftLabelStyle.setValue(BI.Utils.getWSLValueAxisLabelSettingByID(wId));
        this.leftLineColor.setValue(BI.Utils.getWSLValueAxisLineColorByID(wId));
        this.showRightLabel.setSelected(BI.Utils.getWSShowRValueAxisLabelByID(wId));
        this.rightLabelStyle.setValue(BI.Utils.getWSRValueAxisLabelSettingByID(wId));
        this.rightLineColor.setValue(BI.Utils.getWSRValueAxisLineColorByID(wId));
        this.showRight2Label.setSelected(BI.Utils.getWSShowR2ValueAxisLabelByID(wId));
        this.right2LabelStyle.setValue(BI.Utils.getWSR2ValueAxisLabelSettingByID(wId));
        this.right2LineColor.setValue(BI.Utils.getWSR2ValueAxisLineColorByID(wId));
        this.showCatLabel.setSelected(BI.Utils.getWSShowCatLabelByID(wId));
        this.catLabelStyle.setValue(BI.Utils.getWSCatLabelStyleByID(wId));
        this.catLineColor.setValue(BI.Utils.getWSCatLineColorByID(wId));
        this.legendSetting.setValue(BI.Utils.getWSLegendSettingByID(wId));
        this.showHGridLine.setSelected(BI.Utils.getWSShowHGridLineByID(wId));
        this.hGridLineColor.setValue(BI.Utils.getWSHGridLineColorByID(wId));
        this.showVGridLine.setSelected(BI.Utils.getWSShowVGridLineByID(wId));
        this.vGridLineColor.setValue(BI.Utils.getWSVGridLineColorByID(wId));
        this.nullContinuity.setSelected(BI.Utils.getWSNullContinueByID(wId));
        this.tooltipSetting.setValue(BI.Utils.getWSToolTipSettingByID(wId));

        this.isShowTitleLY.isSelected() ? this.editTitleLY.setVisible(true) : this.editTitleLY.setVisible(false);
        this.isShowTitleRY.isSelected() ? this.editTitleRY.setVisible(true) : this.editTitleRY.setVisible(false);
        this.isShowTitleRY2.isSelected() ? this.editTitleRY2.setVisible(true) : this.editTitleRY2.setVisible(false);
        this.isShowTitleX.isSelected() ? this.editTitleX.setVisible(true) : this.editTitleX.setVisible(false);
    },

    getValue: function () {
        return {
            show_name: this.showTitle.isSelected(),
            widget_title: this.title.getValue(),
            title_detail: this.titleDetailSettting.getValue(),
            widget_bg: this.widgetBackground.getValue(),
            transfer_filter: this.transferFilter.isSelected(),
            chart_color: this.colorSelect.getValue()[0],
            chart_style: this.chartSyleGroup.getValue()[0],
            left_y_axis_style: this.lYAxisStyle.getValue()[0],
            right_y_axis_style: this.rYAxisStyle.getValue()[0],
            right_y_axis_second_style: this.rYAxis2Style.getValue()[0],
            left_y_axis_number_level: this.numberLevellY.getValue()[0],
            right_y_axis_number_level: this.numberLevelrY.getValue()[0],
            right_y_axis_second_number_level: this.numberLevelrY2.getValue()[0],
            left_y_axis_unit: this.LYUnit.getValue(),
            right_y_axis_unit: this.RYUnit.getValue(),
            right_y_axis_second_unit: this.RY2Unit.getValue(),
            show_left_y_axis_title: this.isShowTitleLY.isSelected(),
            show_right_y_axis_title: this.isShowTitleRY.isSelected(),
            show_right_y_axis_second_title: this.isShowTitleRY2.isSelected(),
            show_x_axis_title: this.isShowTitleX.isSelected(),
            left_y_axis_title: this.editTitleLY.getValue(),
            right_y_axis_title: this.editTitleRY.getValue(),
            right_y_axis_second_title: this.editTitleRY2.getValue(),
            x_axis_title: this.editTitleX.getValue(),
            left_y_axis_reversed: this.reversedLY.isSelected(),
            right_y_axis_reversed: this.reversedRY.isSelected(),
            right_y_axis_second_reversed: this.reversedRY2.isSelected(),
            chart_legend: this.legend.getValue()[0],
            show_data_label: this.showDataLabel.isSelected(),
            show_data_table: this.showDataTable.isSelected(),
            show_zoom: this.showZoom.isSelected(),
            minimalist_model: this.minimalistModel.isSelected(),
            show_y_custom_scale: this.showYCustomScale.isSelected(),
            custom_y_scale: this.customYScale.getValue(),
            show_x_custom_scale: this.showXCustomScale.isSelected(),
            custom_x_scale: this.customXScale.getValue(),
            show_z_custom_scale: this.showZCustomScale.isSelected(),
            custom_z_scale: this.customZScale.getValue(),
            num_separators: this.separatorsLeft.isSelected(),
            right_num_separators: this.separatorsRight.isSelected(),
            right2_num_separators: this.separatorsRight2.isSelected(),
            show_left_label: this.showLeftLabel.isSelected(),
            left_label_style: this.leftLabelStyle.getValue(),
            left_line_color: this.leftLineColor.getValue(),
            show_right_label: this.showRightLabel.isSelected(),
            right_label_style: this.rightLabelStyle.getValue(),
            right_line_color: this.rightLineColor.getValue(),
            show_right2_label: this.showRight2Label.isSelected(),
            right2_label_style: this.right2LabelStyle.getValue(),
            right2_line_color: this.right2LineColor.getValue(),
            show_cat_label: this.showCatLabel.isSelected(),
            cat_label_style: this.catLabelStyle.getValue(),
            cat_line_color: this.catLineColor.getValue(),
            chart_legend_setting: this.legendSetting.getValue(),
            show_h_grid_line: this.showHGridLine.isSelected(),
            h_grid_line_color: this.hGridLineColor.getValue(),
            show_v_grid_line: this.showVGridLine.isSelected(),
            v_grid_line_color: this.vGridLineColor.getValue(),
            null_continue: this.nullContinuity.isSelected(),
            tooltip_setting: this.tooltipSetting.getValue(),
        }
    },

    setValue: function (v) {
        this.showTitle.setSelected(v.show_name);
        this.title.setValue(v.widget_title);
        this.titleDetailSettting.setValue(v.title_detail);
        this.widgetBackground.setValue(v.widget_bg);
        this.transferFilter.setSelected(v.transfer_filter);
        this.colorSelect.setValue(v.chart_color);
        this.chartSyleGroup.setValue(v.chart_style);
        this.lYAxisStyle.setValue(v.left_y_axis_style);
        this.rYAxisStyle.setValue(v.right_y_axis_style);
        this.rYAxis2Style.setValue(v.right_y_axis_second_style);
        this.numberLevellY.setValue(v.left_y_axis_number_level);
        this.numberLevelrY.setValue(v.right_y_axis_number_level);
        this.numberLevelrY2.setValue(v.right_y_axis_second_number_level);
        this.LYUnit.setValue(v.left_y_axis_unit);
        this.RYUnit.setValue(v.right_y_axis_unit);
        this.RY2Unit.setValue(v.right_y_axis_second_unit);
        this.isShowTitleLY.setSelected(v.show_left_y_axis_title);
        this.isShowTitleRY.setSelected(v.show_right_y_axis_title);
        this.isShowTitleRY2.setSelected(v.show_right_y_axis_second_title);
        this.isShowTitleX.setSelected(v.x_axis_title);
        this.editTitleLY.setValue(v.left_y_axis_title);
        this.editTitleRY.setValue(v.right_y_axis_title);
        this.editTitleRY2.setValue(v.right_y_axis_second_title);
        this.editTitleX.setValue(v.x_axis_title);
        this.reversedLY.setSelected(v.left_y_axis_reversed);
        this.reversedRY.setSelected(v.right_y_axis_reversed);
        this.reversedRY2.setSelected(v.right_y_axis_second_reversed);
        this.legend.setValue(v.chart_legend);
        this.showDataLabel.setSelected(v.show_data_label);
        this.showDataTable.setSelected(v.show_data_table);
        this.showZoom.setSelected(v.show_zoom);
        this.minimalistModel.setSelected(v.minimalist_model);
        this.showYCustomScale.setSelected(v.show_y_custom_scale);
        this.customYScale.setValue(v.custom_y_scale);
        this.showXCustomScale.setSelected(v.show_x_custom_scale);
        this.customXScale.setValue(v.custom_x_scale);
        this.showZCustomScale.setSelected(v.show_z_custom_scale);
        this.customZScale.setValue(v.custom_z_scale);
        this.separatorsLeft.setSelected(v.num_separators);
        this.separatorsRight.setSelected(v.right_num_separators);
        this.separatorsRight2.setSelected(v.right2_num_separators);
        this.showLeftLabel.setSelected(v.show_left_label);
        this.leftLabelStyle.setValue(v.left_label_style);
        this.leftLineColor.setValue(v.left_line_color);
        this.showRightLabel.setSelected(v.show_right_label);
        this.rightLabelStyle.setValue(v.right_label_style);
        this.rightLineColor.setValue(v.right_line_color);
        this.showRight2Label.setSelected(v.show_right2_label);
        this.right2LabelStyle.setValue(v.right2_label_style);
        this.right2LineColor.setValue(v.right2_line_color);
        this.showCatLabel.setSelected(v.show_cat_label);
        this.catLabelStyle.setValue(v.cat_label_style);
        this.catLineColor.setValue(v.cat_line_color);
        this.legendSetting.setValue(v.chart_legend_setting);
        this.showHGridLine.setSelected(v.show_h_grid_line);
        this.hGridLineColor.setValue(v.h_grid_line_color);
        this.showVGridLine.setSelected(v.show_v_grid_line);
        this.vGridLineColor.setValue(v.v_grid_line_color);
        this.nullContinuity.setSelected(v.null_continue);
        this.tooltipSetting.setValue(v.tooltip_setting);
    }
});
BI.MultiAxisChartSetting.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.multi_axis_chart_setting", BI.MultiAxisChartSetting);
