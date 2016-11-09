/**
 * 由于x轴是值轴，跟坐标轴图的右1值轴共用属性
 * @class BI.BubbleChartSetting
 * @extends BI.Widget
 * 气泡图样式
 */
BI.BubbleChartSetting = BI.inherit(BI.AbstractChartSetting, {

    _defaultConfig: function () {
        return BI.extend(BI.BubbleChartSetting.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-charts-setting bi-bubble-chart-setting"
        })
    },

    _init: function () {
        BI.BubbleChartSetting.superclass._init.apply(this, arguments);
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
            self.fireEvent(BI.BubbleChartSetting.EVENT_CHANGE);
        });

        //组件标题
        this.widgetName = BI.createWidget({
            type: "bi.sign_editor",
            cls: "title-input",
            width: 120
        });

        this.widgetName.on(BI.SignEditor.EVENT_CHANGE, function () {
            self.fireEvent(BI.BubbleChartSetting.EVENT_CHANGE)
        });

        //详细设置
        this.widgetNameStyle = BI.createWidget({
            type: "bi.show_title_detailed_setting_combo"
        });

        this.widgetNameStyle.on(BI.ShowTitleDetailedSettingCombo.EVENT_CHANGE, function () {
            self.fireEvent(BI.BubbleChartSetting.EVENT_CHANGE)
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

        this.displayRules = BI.createWidget({
            type: "bi.segment",
            whiteSpace: "normal",
            height: 40,
            width: constant.NUMBER_LEVEL_SEGMENT_WIDTH,
            items: BICst.BUBBLE_DISPLAY_RULES
        });

        this.displayRules.on(BI.Segment.EVENT_CHANGE, function (v) {
            self._colorSettingChange(v);
            self.fireEvent(BI.BubbleChartSetting.EVENT_CHANGE)
        });

        this.fixedConditions = BI.createWidget({
            type: "bi.chart_add_condition_group"
        });

        this.fixedConditions.on(BI.ChartAddConditionGroup.EVENT_CHANGE, function () {
            self.fireEvent(BI.BubbleChartSetting.EVENT_CHANGE)
        });

        this.colorSetting = BI.createWidget({
            type: "bi.label",
            cls: "attr-names",
            textAlign: "left",
            text: BI.i18nText("BI-Color_Setting"),
            height: 30
        });

        this.fixedColorSetting = BI.createWidget({
            type: "bi.left",
            cls: "single-line-settings",
            tgap: 10,
            bgap: 10,
            hgap: 5,
            items: [this.colorSetting, this.fixedConditions],
            width: "100%"
        });

        this.gradientConditions = BI.createWidget({
            type: "bi.chart_add_gradient_condition_group"
        });

        this.gradientConditions.on(BI.ChartAddGradientConditionGroup.EVENT_CHANGE, function () {
            self.fireEvent(BI.BubbleChartSetting.EVENT_CHANGE)
        });

        this.addGradientButton = BI.createWidget({
            type: "bi.button",
            text: BI.i18nText("BI-Add_Condition"),
            height: constant.BUTTON_HEIGHT
        });

        this.addGradientButton.on(BI.Button.EVENT_CHANGE, function () {
            self.gradientConditions.addItem();
            self.fireEvent(BI.BubbleChartSetting.EVENT_CHANGE)
        });

        this.gradientSetting = BI.createWidget({
            type: "bi.label",
            cls: "attr-names",
            textAlign: "left",
            text: BI.i18nText("BI-Color_Setting"),
            height: 30
        });

        this.gradientColorSetting = BI.createWidget({
            type: "bi.left",
            cls: "single-line-settings",
            tgap: 10,
            bgap: 10,
            hgap: 5,
            items: [this.gradientSetting, this.gradientConditions],
            width: "100%"
        });

        this.addConditionButton = BI.createWidget({
            type: "bi.button",
            text: BI.i18nText("BI-Add_Condition"),
            height: constant.BUTTON_HEIGHT
        });

        this.addConditionButton.on(BI.Button.EVENT_CHANGE, function () {
            self.fixedConditions.addItem();
            self.fireEvent(BI.BubbleChartSetting.EVENT_CHANGE)
        });

        this.conditions = BI.createWidget({
            type: "bi.chart_add_condition_group",
            width: "100%"
        });

        this.conditions.on(BI.ChartAddConditionGroup.EVENT_CHANGE, function () {
            self.fireEvent(BI.BubbleChartSetting.EVENT_CHANGE)
        });

        this.chartColor = BI.createWidget({
            type: "bi.chart_setting_select_color_combo",
            width: 130
        });
        this.chartColor.populate();

        this.chartColor.on(BI.ChartSettingSelectColorCombo.EVENT_CHANGE, function () {
            self.fireEvent(BI.BubbleChartSetting.EVENT_CHANGE);
        });

        this.dimensionColor = BI.createWidget({
            type: "bi.left",
            items: BI.createItems([{
                type: "bi.label",
                text: BI.i18nText("BI-Color_Setting"),
                textHeight: constant.BUTTON_HEIGHT,
                cls: "attr-names"
            }, {
                type: "bi.vertical_adapt",
                items: [this.chartColor],
                lgap: constant.SIMPLE_H_GAP
            }])
        });

        this.bubbleStyle = BI.createWidget({
            type: "bi.button_group",
            items: BI.createItems(BICst.BUBBLE_CHART_STYLE_GROUP, {
                type: "bi.icon_button",
                extraCls: "chart-style-font",
                width: constant.BUTTON_WIDTH,
                height: constant.BUTTON_HEIGHT,
                iconWidth: constant.ICON_WIDTH,
                iconHeight: constant.ICON_HEIGHT
            }),
            layouts: [{
                type: "bi.left",
                lgap: 2
            }]
        });

        this.bubbleStyle.on(BI.ButtonGroup.EVENT_CHANGE, function () {
            self.fireEvent(BI.BubbleChartSetting.EVENT_CHANGE);
        });

        //组件背景
        this.widgetBG = BI.createWidget({
            type: "bi.global_style_index_background"
        });
        this.widgetBG.on(BI.GlobalStyleIndexBackground.EVENT_CHANGE, function () {
            self.fireEvent(BI.BubbleChartSetting.EVENT_CHANGE);
        });

        //气泡大小
        this.bubbleSizeFrom = BI.createWidget({
            type: "bi.sign_editor",
            width: constant.EDITOR_WIDTH,
            height: constant.EDITOR_HEIGHT,
            errorText: BI.i18nText("BI-Please_Input_Positive_Integer"),
            cls: "unit-input",
            validationChecker: function (v) {
                return BI.parseInt(v) > 0 && BI.parseInt(v) <= BI.parseInt(self.bubbleSizeTo.getValue())
            }
        });

        this.bubbleSizeFrom.on(BI.SignEditor.EVENT_CONFIRM, function () {
            self.fireEvent(BI.BubbleChartSetting.EVENT_CHANGE);
        });

        this.bubbleSizeTo = BI.createWidget({
            type: "bi.sign_editor",
            width: constant.EDITOR_WIDTH,
            height: constant.EDITOR_HEIGHT,
            errorText: BI.i18nText("BI-Please_Input_Integer_Greater_Than_Minimum"),
            cls: "unit-input",
            validationChecker: function (v) {
                return BI.parseFloat(v) >= BI.parseFloat(self.bubbleSizeFrom.getValue())
            }
        });

        this.bubbleSizeTo.on(BI.SignEditor.EVENT_CONFIRM, function () {
            self.fireEvent(BI.BubbleChartSetting.EVENT_CHANGE);
        });

        var tableStyle = BI.createWidget({
            type: "bi.horizontal_adapt",
            columnSize: [80],
            verticalAlign: "top",
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
                    text: BI.i18nText("BI-Display_Rules"),
                    cls: "attr-names"
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.displayRules]
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.dimensionColor]
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.addGradientButton]
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.addConditionButton]
                }, {
                    type: "bi.label",
                    text: BI.i18nText("BI-Total_Style"),
                    cls: "attr-names"
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.bubbleStyle]
                }, {
                    type: "bi.vertical_adapt",
                    items: [{
                        type: "bi.label",
                        text: BI.i18nText("BI-Bubble_Size")
                    }, this.bubbleSizeFrom, {
                        type: "bi.label",
                        text: "px <" + BI.i18nText("BI-Diameter") + "≤"
                    }, this.bubbleSizeTo, {
                        type: "bi.label",
                        text: "px"
                    }],
                    hgap: 3
                }, {
                    type: "bi.label",
                    text: BI.i18nText("BI-Widget_Background_Colour"),
                    cls: "attr-names",
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.widgetBG],
                    cls: "attr-names"
                }, this.fixedColorSetting, this.gradientColorSetting], {
                    height: constant.SINGLE_LINE_HEIGHT,
                    lgap: constant.SIMPLE_H_GAP
                })
            }]
        });

        this.rightYNumberLevel = BI.createWidget({
            type: "bi.segment",
            width: constant.NUMBER_LEVEL_SEGMENT_WIDTH,
            height: constant.BUTTON_HEIGHT,
            items: BICst.TARGET_STYLE_LEVEL
        });

        this.rightYNumberLevel.on(BI.Segment.EVENT_CHANGE, function () {
            self.fireEvent(BI.BubbleChartSetting.EVENT_CHANGE);
        });

        this.rightYUnit = BI.createWidget({
            type: "bi.sign_editor",
            width: constant.EDITOR_WIDTH,
            height: constant.EDITOR_HEIGHT,
            cls: "unit-input",
            watermark: BI.i18nText("BI-Custom_Input")
        });

        this.rightYUnit.on(BI.SignEditor.EVENT_CONFIRM, function () {
            self.fireEvent(BI.BubbleChartSetting.EVENT_CHANGE);
        });

        this.rightYNumberFormat = BI.createWidget({
            type: "bi.segment",
            width: constant.FORMAT_SEGMENT_WIDTH,
            height: constant.BUTTON_HEIGHT,
            items: BICst.TARGET_STYLE_FORMAT
        });

        this.rightYNumberFormat.on(BI.Segment.EVENT_CHANGE, function () {
            self.fireEvent(BI.BubbleChartSetting.EVENT_CHANGE);
        });

        this.rightYSeparator = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Separators"),
            width: 80
        });

        this.rightYSeparator.on(BI.Controller.EVENT_CHANGE, function () {
            self.fireEvent(BI.BubbleChartSetting.EVENT_CHANGE);
        });

        this.rightYShowTitle = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Show_Title"),
            width: 90
        });

        this.rightYShowTitle.on(BI.Controller.EVENT_CHANGE, function () {
            self.rightYTitle.setVisible(this.isSelected());
            self.rightYTitleStyle.setVisible(this.isSelected());
            self.fireEvent(BI.BubbleChartSetting.EVENT_CHANGE);
        });

        this.rightYTitle = BI.createWidget({
            type: "bi.sign_editor",
            width: constant.EDITOR_WIDTH,
            height: constant.EDITOR_HEIGHT,
            cls: "unit-input"
        });

        this.rightYTitle.on(BI.SignEditor.EVENT_CONFIRM, function () {
            self.fireEvent(BI.BubbleChartSetting.EVENT_CHANGE);
        });

        this.rightYTitleStyle = BI.createWidget({
            type: "bi.legend_detailed_setting_combo"
        });

        this.rightYTitleStyle.on(BI.LegendDetailedSettingCombo.EVENT_CHANGE, function () {
            self.fireEvent(BI.BubbleChartSetting.EVENT_CHANGE)
        });

        //右轴标签
        this.rightYShowLabel = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Show_Label"),
            width: 100
        });

        this.rightYShowLabel.on(BI.Controller.EVENT_CHANGE, function () {
            self.rightYLabelStyle.setVisible(this.isSelected());
            self.fireEvent(BI.BubbleChartSetting.EVENT_CHANGE)
        });

        this.rightYLabelStyle = BI.createWidget({
            type: "bi.chart_label_detailed_setting_combo"
        });

        this.rightYLabelStyle.on(BI.ChartLabelDetailedSettingCombo.EVENT_CHANGE, function () {
            self.fireEvent(BI.BubbleChartSetting.EVENT_CHANGE)
        });

        //右轴线颜色
        this.rightYLineColor = BI.createWidget({
            type: "bi.color_chooser",
            width: 30,
            height: 30
        });

        this.rightYLineColor.on(BI.ColorChooser.EVENT_CHANGE, function () {
            self.fireEvent(BI.BubbleChartSetting.EVENT_CHANGE)
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
            self.fireEvent(BI.BubbleChartSetting.EVENT_CHANGE)
        });

        this.rightYCustomScale = BI.createWidget({
            type: "bi.custom_scale",
            wId: o.wId
        });

        this.rightYCustomScale.on(BI.CustomScale.EVENT_CHANGE, function () {
            self.fireEvent(BI.BubbleChartSetting.EVENT_CHANGE)
        });

        var XAxis = BI.createWidget({
            type: "bi.horizontal_adapt",
            columnSize: [80],
            verticalAlign: "top",
            cls: "single-line-settings",
            items: [{
                type: "bi.label",
                cls: "line-title",
                text: BI.i18nText("BI-Uppercase_X_Axis"),
                height: "100%",
                textAlign: "left",
                textHeight: constant.SINGLE_LINE_HEIGHT,
                lgap: constant.SIMPLE_H_LGAP
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
                    items: [this.rightYNumberLevel]
                }, {
                    type: "bi.label",
                    text: BI.i18nText("BI-Unit_Normal"),
                    lgap: constant.SIMPLE_H_GAP,
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
                    height: constant.SINGLE_LINE_HEIGHT,
                    lgap: constant.SIMPLE_H_GAP
                })
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
            self.fireEvent(BI.BubbleChartSetting.EVENT_CHANGE);
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
            self.fireEvent(BI.BubbleChartSetting.EVENT_CHANGE);
        });

        this.leftYNumberFormat = BI.createWidget({
            type: "bi.segment",
            width: constant.FORMAT_SEGMENT_WIDTH,
            height: constant.BUTTON_HEIGHT,
            items: BICst.TARGET_STYLE_FORMAT
        });

        this.leftYNumberFormat.on(BI.Segment.EVENT_CHANGE, function () {
            self.fireEvent(BI.BubbleChartSetting.EVENT_CHANGE);
        });

        //千分符
        this.leftYSeparator = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Separators"),
            width: 80
        });

        this.leftYSeparator.on(BI.Controller.EVENT_CHANGE, function () {
            self.fireEvent(BI.BubbleChartSetting.EVENT_CHANGE);
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
            self.fireEvent(BI.BubbleChartSetting.EVENT_CHANGE);
        });

        this.leftYTitle = BI.createWidget({
            type: "bi.sign_editor",
            width: constant.EDITOR_WIDTH,
            height: constant.EDITOR_HEIGHT,
            cls: "unit-input"
        });
        this.leftYTitle.on(BI.SignEditor.EVENT_CONFIRM, function () {
            self.fireEvent(BI.BubbleChartSetting.EVENT_CHANGE);
        });

        this.leftYTitleStyle = BI.createWidget({
            type: "bi.legend_detailed_setting_combo"
        });

        this.leftYTitleStyle.on(BI.LegendDetailedSettingCombo.EVENT_CHANGE, function() {
            self.fireEvent(BI.BubbleChartSetting.EVENT_CHANGE)
        });

        //左轴标签
        this.leftYShowLabel = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Show_Label"),
            width: 100
        });

        this.leftYShowLabel.on(BI.Controller.EVENT_CHANGE, function () {
            self.leftYLabelStyle.setVisible(this.isSelected());
            self.fireEvent(BI.BubbleChartSetting.EVENT_CHANGE)
        });

        this.leftYLabelStyle = BI.createWidget({
            type: "bi.chart_label_detailed_setting_combo"
        });

        this.leftYLabelStyle.on(BI.ChartLabelDetailedSettingCombo.EVENT_CHANGE, function () {
            self.fireEvent(BI.BubbleChartSetting.EVENT_CHANGE)
        });

        //左轴线颜色
        this.leftYLineColor = BI.createWidget({
            type: "bi.color_chooser",
            width: 30,
            height: 30
        });

        this.leftYLineColor.on(BI.ColorChooser.EVENT_CHANGE, function () {
            self.fireEvent(BI.BubbleChartSetting.EVENT_CHANGE)
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
            self.fireEvent(BI.BubbleChartSetting.EVENT_CHANGE)
        });

        this.leftYCustomScale = BI.createWidget({
            type: "bi.custom_scale",
            wId: o.wId
        });

        this.leftYCustomScale.on(BI.CustomScale.EVENT_CHANGE, function () {
            self.fireEvent(BI.BubbleChartSetting.EVENT_CHANGE)
        });

        var YAxis = BI.createWidget({
            type: "bi.horizontal_adapt",
            columnSize: [80],
            verticalAlign: "top",
            cls: "single-line-settings",
            items: [{
                type: "bi.label",
                height: "100%",
                textHeight: constant.SINGLE_LINE_HEIGHT,
                textAlign: "left",
                lgap: constant.SIMPLE_H_LGAP,
                text: BI.i18nText("BI-Uppercase_Y_Axis"),
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
                    lgap: constant.SIMPLE_H_GAP,
                    height: constant.SINGLE_LINE_HEIGHT
                })
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
            self.fireEvent(BI.BubbleChartSetting.EVENT_CHANGE);
        });

        this.legendStyle = BI.createWidget({
            type: "bi.legend_detailed_setting_combo"
        });

        this.legendStyle.on(BI.LegendDetailedSettingCombo.EVENT_CHANGE, function () {
            self.fireEvent(BI.BubbleChartSetting.EVENT_CHANGE)
        });

        //网格线设置
        this.hShowGridLine = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Horizontal"),
            width: 65
        });

        this.hShowGridLine.on(BI.Controller.EVENT_CHANGE, function () {
            self.hGridLineColor.setVisible(this.isSelected());
            self.fireEvent(BI.BubbleChartSetting.EVENT_CHANGE)
        });

        this.hGridLineColor = BI.createWidget({
            type: "bi.color_chooser",
            width: 30,
            height: 30
        });

        this.hGridLineColor.on(BI.ColorChooser.EVENT_CHANGE, function () {
            self.fireEvent(BI.BubbleChartSetting.EVENT_CHANGE)
        });

        this.vShowGridLine = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Vertical"),
            width: 65
        });

        this.vShowGridLine.on(BI.Controller.EVENT_CHANGE, function () {
            self.vGridLineColor.setVisible(this.isSelected());
            self.fireEvent(BI.BubbleChartSetting.EVENT_CHANGE)
        });

        this.vGridLineColor = BI.createWidget({
            type: "bi.color_chooser",
            width: 30,
            height: 30
        });

        this.vGridLineColor.on(BI.ColorChooser.EVENT_CHANGE, function () {
            self.fireEvent(BI.BubbleChartSetting.EVENT_CHANGE)
        });

        //数据标签
        this.showDataLabel = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Show_Data_Label"),
            width: 115
        });

        this.showDataLabel.on(BI.Controller.EVENT_CHANGE, function () {
            self.fireEvent(BI.BubbleChartSetting.EVENT_CHANGE);
        });

        //数据点提示详细设置
        this.tooltipStyle = BI.createWidget({
            type: "bi.tooltip_detailed_setting_combo"
        });

        this.tooltipStyle.on(BI.TooltipDetailedSettingCombo.EVENT_CHANGE, function () {
            self.fireEvent(BI.BubbleChartSetting.EVENT_CHANGE)
        });

        var showElement = BI.createWidget({
            type: "bi.horizontal_adapt",
            columnSize: [80],
            verticalAlign: "top",
            cls: "single-line-settings",
            items: [{
                type: "bi.label",
                text: BI.i18nText("BI-Element_Show"),
                textHeight: constant.SINGLE_LINE_HEIGHT,
                lgap: constant.SIMPLE_H_LGAP,
                textAlign: "left",
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
            self.fireEvent(BI.BubbleChartSetting.EVENT_CHANGE);
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
                self.displayRules.setValue(BICst.DISPLAY_RULES.GRADIENT);
                self._colorSettingChange(BICst.DISPLAY_RULES.GRADIENT)
            }
            self.fireEvent(BI.BubbleChartSetting.EVENT_CHANGE)
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
            items: [widgetTitle, tableStyle, XAxis, YAxis, showElement, otherAttr, modeChange],
            hgap: 10
        })
    },

    _colorSettingChange: function (v) {
        switch (v) {
            case BICst.DISPLAY_RULES.DIMENSION:
                this.dimensionColor.setVisible(true);
                this.addConditionButton.setVisible(false);
                this.fixedColorSetting.setVisible(false);
                this.addGradientButton.setVisible(false);
                this.gradientColorSetting.setVisible(false);
                break;
            case BICst.DISPLAY_RULES.FIXED:
                this.dimensionColor.setVisible(false);
                this.addConditionButton.setVisible(true);
                this.fixedColorSetting.setVisible(true);
                this.addGradientButton.setVisible(false);
                this.gradientColorSetting.setVisible(false);
                break;
            case BICst.DISPLAY_RULES.GRADIENT:
                this.dimensionColor.setVisible(false);
                this.addConditionButton.setVisible(false);
                this.fixedColorSetting.setVisible(false);
                this.addGradientButton.setVisible(true);
                this.gradientColorSetting.setVisible(true);
                break;
        }
    },

    _bigDataMode: function (v) {
        this.showDataLabel.setEnable(v);
        this.transferFilter.setEnable(v)
    },

    populate: function () {
        var wId = this.options.wId;
        var view = BI.Utils.getWidgetViewByID(wId);
        var titleLY = BI.Utils.getWSLeftYAxisTitleByID(wId), titleX = BI.Utils.getWSRightYAxisTitleByID(wId);
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
        this.displayRules.setValue(BI.Utils.getWSShowRulesByID(wId));
        this._colorSettingChange(BI.Utils.getWSShowRulesByID(wId));
        this.fixedConditions.setValue(BI.Utils.getWSBubbleFixedColorsByID(wId));
        this.gradientConditions.setValue(BI.Utils.getWSBubbleGradientsByID(wId));
        this.bubbleStyle.setValue(BI.Utils.getWSBubbleStyleByID(wId));
        this.bubbleSizeFrom.setValue(BI.Utils.getWSMinBubbleSizeByID(wId));
        this.bubbleSizeTo.setValue(BI.Utils.getWSMaxBubbleSizeByID(wId));

        //x轴
        this.rightYNumberFormat.setValue(BI.Utils.getWSRightYAxisStyleByID(wId));
        this.rightYNumberLevel.setValue(BI.Utils.getWSRightYAxisNumLevelByID(wId));
        this.rightYUnit.setValue(BI.Utils.getWSRightYAxisUnitByID(wId));
        this.rightYShowTitle.setSelected(BI.Utils.getWSShowRightYAxisTitleByID(wId));
        this.rightYTitle.setValue(titleX);
        this.rightYShowCustomScale.setSelected(BI.Utils.getWSShowXCustomScale(wId));
        this.rightYCustomScale.setValue(BI.Utils.getWSCustomXScale(wId));
        this.rightYCustomScale.setVisible(BI.Utils.getWSShowXCustomScale(wId));
        this.rightYSeparator.setSelected(BI.Utils.getWSRightNumberSeparatorsByID(wId));
        this.rightYShowLabel.setSelected(BI.Utils.getWSShowRValueAxisLabelByID(wId));
        this.rightYLabelStyle.setValue(BI.Utils.getWSRValueAxisLabelSettingByID(wId));
        this.rightYLineColor.setValue(BI.Utils.getWSRValueAxisLineColorByID(wId));
        this.rightYTitleStyle.setValue(BI.Utils.getWSRightTitleStyleByID(wId));
        this.rightYTitle.setVisible(this.rightYShowTitle.isSelected());
        this.rightYTitleStyle.setVisible(this.rightYShowTitle.isSelected());

        //y轴
        this.leftYNumberFormat.setValue(BI.Utils.getWSLeftYAxisStyleByID(wId));
        this.leftYNumberLevel.setValue(BI.Utils.getWSLeftYAxisNumLevelByID(wId));
        this.leftYUnit.setValue(BI.Utils.getWSLeftYAxisUnitByID(wId));
        this.leftYShowTitle.setSelected(BI.Utils.getWSShowLeftYAxisTitleByID(wId));
        this.leftYTitle.setValue(titleLY);
        this.leftYShowCustomScale.setSelected(BI.Utils.getWSShowYCustomScale(wId));
        this.leftYCustomScale.setValue(BI.Utils.getWSCustomYScale(wId));
        this.leftYCustomScale.setVisible(BI.Utils.getWSShowYCustomScale(wId));
        this.leftYSeparator.setSelected(BI.Utils.getWSNumberSeparatorsByID(wId));
        this.leftYShowLabel.setSelected(BI.Utils.getWSShowLValueAxisLabelByID(wId));
        this.leftYLabelStyle.setValue(BI.Utils.getWSLValueAxisLabelSettingByID(wId));
        this.leftYLineColor.setValue(BI.Utils.getWSLValueAxisLineColorByID(wId));
        this.leftYTitleStyle.setValue(BI.Utils.getWSLeftTitleStyleByID(wId));
        this.leftYTitle.setVisible(this.leftYShowTitle.isSelected());
        this.leftYTitleStyle.setVisible(this.leftYShowTitle.isSelected());

        this.legend.setValue(BI.Utils.getWSChartLegendByID(wId));
        this.showDataLabel.setSelected(BI.Utils.getWSShowDataLabelByID(wId));
        this.legendStyle.setValue(BI.Utils.getWSLegendSettingByID(wId));
        this.hShowGridLine.setSelected(BI.Utils.getWSShowHGridLineByID(wId));
        this.hGridLineColor.setValue(BI.Utils.getWSHGridLineColorByID(wId));
        this.vShowGridLine.setSelected(BI.Utils.getWSShowVGridLineByID(wId));
        this.vGridLineColor.setValue(BI.Utils.getWSVGridLineColorByID(wId));
        this.tooltipStyle.setValue(BI.Utils.getWSToolTipSettingByID(wId));
        this.hGridLineColor.setVisible(this.hShowGridLine.isSelected());
        this.vGridLineColor.setVisible(this.vShowGridLine.isSelected());

        this.transferFilter.setSelected(BI.Utils.getWSTransferFilterByID(wId));

        this.bigDataMode.setSelected(BI.Utils.getWSBigDataModelByID(wId));
        this._bigDataMode(!BI.Utils.getWSBigDataModelByID(wId));
    },

    getValue: function () {
        return {
            showName: this.showName.isSelected(),
            widgetName: this.widgetName.getValue(),
            widgetNameStyle: this.widgetNameStyle.getValue(),

            chartColor: this.chartColor.getValue()[0],
            widgetBG: this.widgetBG.getValue(),
            displayRules: this.displayRules.getValue()[0],
            bubbleStyle: this.bubbleStyle.getValue()[0],
            fixedCondition: this.fixedConditions.getValue(),
            gradientConditions: this.gradientConditions.getValue(),
            bubbleSizeFrom: this.bubbleSizeFrom.getValue(),
            bubbleSizeTo: this.bubbleSizeTo.getValue(),

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
BI.BubbleChartSetting.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.bubble_chart_setting", BI.BubbleChartSetting);
