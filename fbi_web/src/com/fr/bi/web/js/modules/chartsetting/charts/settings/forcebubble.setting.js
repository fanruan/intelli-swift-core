/**
 * @class BI.ForceBubbleSetting
 * @extends BI.Widget
 * 力学气泡样式
 */
BI.ForceBubbleSetting = BI.inherit(BI.AbstractChartSetting, {

    _defaultConfig: function () {
        return BI.extend(BI.ForceBubbleSetting.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-charts-setting bi-force-bubble-chart-setting"
        })
    },

    _init: function () {
        BI.ForceBubbleSetting.superclass._init.apply(this, arguments);
        var self = this, constant = BI.AbstractChartSetting;

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
            self.fireEvent(BI.ForceBubbleSetting.EVENT_CHANGE);
        });

        //组件标题
        this.widgetName = BI.createWidget({
            type: "bi.sign_editor",
            cls: "title-input",
            width: 120
        });

        this.widgetName.on(BI.SignEditor.EVENT_CHANGE, function () {
            self.fireEvent(BI.ForceBubbleSetting.EVENT_CHANGE)
        });

        //详细设置
        this.widgetNameStyle = BI.createWidget({
            type: "bi.show_title_detailed_setting_combo"
        });

        this.widgetNameStyle.on(BI.ShowTitleDetailedSettingCombo.EVENT_CHANGE, function () {
            self.fireEvent(BI.ForceBubbleSetting.EVENT_CHANGE)
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

        //显示规则
        this.displayRules = BI.createWidget({
            type: "bi.segment",
            whiteSpace: "normal",
            height: 40,
            width: constant.NUMBER_LEVEL_SEGMENT_WIDTH,
            items: BICst.BUBBLE_DISPLAY_RULES
        });

        this.displayRules.on(BI.Segment.EVENT_CHANGE, function (v) {
            self._colorSettingChange(v);
            self.fireEvent(BI.ForceBubbleSetting.EVENT_CHANGE)
        });

        this.addConditionButton = BI.createWidget({
            type: "bi.button",
            text: BI.i18nText("BI-Add_Condition"),
            height: constant.BUTTON_HEIGHT
        });

        this.addConditionButton.on(BI.Button.EVENT_CHANGE, function () {
            self.fixedStyle.addItem();
            self.fireEvent(BI.ForceBubbleSetting.EVENT_CHANGE)
        });

        this.centerConditionButton1 = BI.createWidget({
            type: "bi.vertical_adapt",
            items: [this.addConditionButton],
            height: constant.SINGLE_LINE_HEIGHT
        });

        this.fixedStyle = BI.createWidget({
            type: "bi.chart_add_condition_group"
        });

        this.fixedStyle.on(BI.ChartAddConditionGroup.EVENT_CHANGE, function () {
            self.fireEvent(BI.ForceBubbleSetting.EVENT_CHANGE)
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
            items: [this.colorSetting, this.fixedStyle],
            width: "100%"
        });

        this.gradientStyle = BI.createWidget({
            type: "bi.chart_add_gradient_condition_group"
        });

        this.gradientStyle.on(BI.ChartAddGradientConditionGroup.EVENT_CHANGE, function () {
            self.fireEvent(BI.ForceBubbleSetting.EVENT_CHANGE)
        });

        this.addGradientButton = BI.createWidget({
            type: "bi.button",
            text: BI.i18nText("BI-Add_Condition"),
            height: constant.BUTTON_HEIGHT
        });

        this.addGradientButton.on(BI.Button.EVENT_CHANGE, function () {
            self.gradientStyle.addItem();
            self.fireEvent(BI.ForceBubbleSetting.EVENT_CHANGE)
        });

        this.centerConditionButton2 = BI.createWidget({
            type: "bi.vertical_adapt",
            items: [this.addGradientButton],
            height: constant.SINGLE_LINE_HEIGHT
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
            items: [this.gradientSetting, this.gradientStyle],
            width: "100%"
        });

        this.chartColor = BI.createWidget({
            type: "bi.chart_setting_select_color_combo",
            width: 130
        });
        this.chartColor.populate();

        this.chartColor.on(BI.ChartSettingSelectColorCombo.EVENT_CHANGE, function () {
            self.fireEvent(BI.ForceBubbleSetting.EVENT_CHANGE);
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
            self.fireEvent(BI.ForceBubbleSetting.EVENT_CHANGE);
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
            self.fireEvent(BI.ForceBubbleSetting.EVENT_CHANGE)
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
            self.fireEvent(BI.ForceBubbleSetting.EVENT_CHANGE)
        });

        //组件背景
        this.widgetBG = BI.createWidget({
            type: "bi.global_style_index_background"
        });
        this.widgetBG.on(BI.GlobalStyleIndexBackground.EVENT_CHANGE, function () {
            self.fireEvent(BI.ForceBubbleSetting.EVENT_CHANGE);
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
                }, this.centerConditionButton1, this.centerConditionButton2, {
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
                    cls: "line-title",
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.widgetBG]
                },this.fixedColorSetting, this.gradientColorSetting,], {
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
            self.fireEvent(BI.ForceBubbleSetting.EVENT_CHANGE);
        });

        //图例详细设置
        this.legendStyle = BI.createWidget({
            type: "bi.legend_detailed_setting_combo"
        });

        this.legendStyle.on(BI.LegendDetailedSettingCombo.EVENT_CHANGE, function () {
            self.fireEvent(BI.ForceBubbleSetting.EVENT_CHANGE)
        });

        //数据标签
        this.showDataLabel = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Show_Data_Label"),
            width: 115
        });

        this.showDataLabel.on(BI.Controller.EVENT_CHANGE, function () {
            self.fireEvent(BI.ForceBubbleSetting.EVENT_CHANGE);
        });

        //数据点提示详细设置
        this.tooltipStyle = BI.createWidget({
            type: "bi.tooltip_detailed_setting_combo"
        });

        this.tooltipStyle.on(BI.TooltipDetailedSettingCombo.EVENT_CHANGE, function () {
            self.fireEvent(BI.ForceBubbleSetting.EVENT_CHANGE)
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
                    type: "bi.vertical_adapt",
                    items: [this.showDataLabel]
                }, /*, {
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
            self.fireEvent(BI.ForceBubbleSetting.EVENT_CHANGE);
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
            items: [widgetTitle, tableStyle, showElement, otherAttr],
            hgap: 10
        })
    },

    _colorSettingChange: function (v) {
        switch (v) {
            case BICst.DISPLAY_RULES.DIMENSION:
                this.dimensionColor.setVisible(true);
                this.centerConditionButton1.setVisible(false);
                this.fixedColorSetting.setVisible(false);
                this.centerConditionButton2.setVisible(false);
                this.gradientColorSetting.setVisible(false);
                break;
            case BICst.DISPLAY_RULES.FIXED:
                this.dimensionColor.setVisible(false);
                this.centerConditionButton1.setVisible(true);
                this.fixedColorSetting.setVisible(true);
                this.centerConditionButton2.setVisible(false);
                this.gradientColorSetting.setVisible(false);
                break;
            case BICst.DISPLAY_RULES.GRADIENT:
                this.dimensionColor.setVisible(false);
                this.centerConditionButton1.setVisible(false);
                this.fixedColorSetting.setVisible(false);
                this.centerConditionButton2.setVisible(true);
                this.gradientColorSetting.setVisible(true);
                break;
        }
    },

    populate: function () {
        var wId = this.options.wId;
        this.showName.setSelected(BI.Utils.getWSShowNameByID(wId));
        this.widgetTitle.setVisible(BI.Utils.getWSShowNameByID(wId));
        this.widgetName.setValue(BI.Utils.getWidgetNameByID(wId));
        this.widgetNameStyle.setValue(BI.Utils.getWSTitleDetailSettingByID(wId));

        this.widgetBG.setValue(BI.Utils.getWSWidgetBGByID(wId));
        this.displayRules.setValue(BI.Utils.getWSChartDisplayRulesByID(wId));
        this._colorSettingChange(BI.Utils.getWSChartDisplayRulesByID(wId));
        this.fixedStyle.setValue(BI.Utils.getWSChartBubbleFixedStyleByID(wId));
        this.gradientStyle.setValue(BI.Utils.getWSChartBubbleGradientStyleByID(wId));
        this.chartColor.setValue(BI.Utils.getWSChartColorByID(wId));
        this.legend.setValue(BI.Utils.getWSChartLegendByID(wId));
        this.showDataLabel.setSelected(BI.Utils.getWSChartShowDataLabelByID(wId));
        this.bubbleSizeFrom.setValue(BI.Utils.getWSChartBubbleSizeFromByID(wId));
        this.bubbleSizeTo.setValue(BI.Utils.getWSChartBubbleSizeToByID(wId));
        this.bubbleStyle.setValue(BI.Utils.getWSChartBubbleStyleByID(wId));

        this.transferFilter.setSelected(BI.Utils.getWSTransferFilterByID(wId));
    },

    getValue: function () {
        return {
            showName: this.showName.isSelected(),
            widgetName: this.widgetName.getValue(),
            widgetNameStyle: this.widgetNameStyle.getValue(),

            widgetBG: this.widgetBG.getValue(),
            displayRules: this.displayRules.getValue()[0],
            fixedStyle: this.fixedStyle.getValue(),
            gradientStyle: this.gradientStyle.getValue(),
            chartColor: this.chartColor.getValue()[0],
            legend: this.legend.getValue()[0],
            showDataLabel: this.showDataLabel.isSelected(),
            bubbleSizeFrom: this.bubbleSizeFrom.getValue(),
            bubbleSizeTo: this.bubbleSizeTo.getValue(),
            bubbleStyle: this.bubbleStyle.getValue()[0],

            transferFilter: this.transferFilter.isSelected(),
        }
    }
});
BI.ForceBubbleSetting.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.force_bubble_setting", BI.ForceBubbleSetting);
