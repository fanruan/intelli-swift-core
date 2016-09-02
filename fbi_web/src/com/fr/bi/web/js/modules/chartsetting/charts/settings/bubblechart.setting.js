/**
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

        this.rulesDisplay = BI.createWidget({
            type: "bi.segment",
            whiteSpace: "normal",
            height: 40,
            width: constant.NUMBER_LEVEL_SEGMENT_WIDTH,
            items: BICst.BUBBLE_DISPLAY_RULES
        });

        this.rulesDisplay.on(BI.Segment.EVENT_CHANGE, function (v) {
            self._colorSettingChange(v);
            self.fireEvent(BI.BubbleChartSetting.EVENT_CHANGE)
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

        this.centerConditionButton1 = BI.createWidget({
            type: "bi.vertical_adapt",
            items: [this.addConditionButton],
            height: constant.SINGLE_LINE_HEIGHT
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
            items: [this.gradientSetting, this.gradientConditions],
            width: "100%"
        });

        this.colorSelect = BI.createWidget({
            type: "bi.chart_setting_select_color_combo",
            width: 130
        });
        this.colorSelect.populate();

        this.colorSelect.on(BI.ChartSettingSelectColorCombo.EVENT_CHANGE, function () {
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
                items: [this.colorSelect],
                lgap: constant.SIMPLE_H_GAP
            }])
        });

        this.bubbleStyleGroup = BI.createWidget({
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

        this.bubbleStyleGroup.on(BI.ButtonGroup.EVENT_CHANGE, function () {
            self.fireEvent(BI.BubbleChartSetting.EVENT_CHANGE);
        });

        var tableStyle = BI.createWidget({
            type: "bi.horizontal_adapt",
            columnSize: [80],
            verticalAlign: "top",
            cls: "single-line-settings",
            items: [{
                type: "bi.label",
                text: BI.i18nText("BI-Table_Sheet_Style"),
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
                    el: {
                        type: "bi.vertical_adapt",
                        items: [this.rulesDisplay]
                    }
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.dimensionColor]
                }, this.centerConditionButton1, this.centerConditionButton2, {
                    type: "bi.label",
                    text: BI.i18nText("BI-Total_Style"),
                    cls: "attr-names"
                }, {
                    el: {
                        type: "bi.vertical_adapt",
                        items: [this.bubbleStyleGroup]
                    }
                }, this.fixedColorSetting, this.gradientColorSetting], {
                    height: constant.SINGLE_LINE_HEIGHT
                }),
                lgap: constant.SIMPLE_H_GAP
            }]
        });

        //格式和数量级
        this.lYAxisStyle = BI.createWidget({
            type: "bi.segment",
            width: constant.FORMAT_SEGMENT_WIDTH,
            height: constant.BUTTON_HEIGHT,
            items: BICst.TARGET_STYLE_FORMAT
        });

        this.lYAxisStyle.on(BI.Segment.EVENT_CHANGE, function () {
            self.fireEvent(BI.BubbleChartSetting.EVENT_CHANGE);
        });

        this.numberLevellY = BI.createWidget({
            type: "bi.segment",
            width: constant.NUMBER_LEVEL_SEGMENT_WIDTH,
            height: constant.BUTTON_HEIGHT,
            items: BICst.TARGET_STYLE_LEVEL
        });

        this.numberLevellY.on(BI.Segment.EVENT_CHANGE, function () {
            self.fireEvent(BI.BubbleChartSetting.EVENT_CHANGE);
        });

        this.XAxisStyle = BI.createWidget({
            type: "bi.segment",
            width: constant.FORMAT_SEGMENT_WIDTH,
            height: constant.BUTTON_HEIGHT,
            items: BICst.TARGET_STYLE_FORMAT
        });

        this.XAxisStyle.on(BI.Segment.EVENT_CHANGE, function () {
            self.fireEvent(BI.BubbleChartSetting.EVENT_CHANGE);
        });

        this.numberLevelX = BI.createWidget({
            type: "bi.segment",
            width: constant.NUMBER_LEVEL_SEGMENT_WIDTH,
            height: constant.BUTTON_HEIGHT,
            items: BICst.TARGET_STYLE_LEVEL
        });

        this.numberLevelX.on(BI.Segment.EVENT_CHANGE, function () {
            self.fireEvent(BI.BubbleChartSetting.EVENT_CHANGE);
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
            self.fireEvent(BI.BubbleChartSetting.EVENT_CHANGE);
        });

        this.XUnit = BI.createWidget({
            type: "bi.sign_editor",
            width: constant.EDITOR_WIDTH,
            height: constant.EDITOR_HEIGHT,
            cls: "unit-input",
            watermark: BI.i18nText("BI-Custom_Input")
        });

        this.XUnit.on(BI.SignEditor.EVENT_CONFIRM, function () {
            self.fireEvent(BI.BubbleChartSetting.EVENT_CHANGE);
        });

        //显示标题
        this.isShowTitleLY = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Show_Title"),
            width: 90
        });

        this.isShowTitleLY.on(BI.Controller.EVENT_CHANGE, function () {
            this.isSelected() ? self.editTitleLY.setVisible(true) : self.editTitleLY.setVisible(false);
            self.fireEvent(BI.BubbleChartSetting.EVENT_CHANGE);
        });

        this.editTitleLY = BI.createWidget({
            type: "bi.sign_editor",
            width: constant.EDITOR_WIDTH,
            height: constant.EDITOR_HEIGHT,
            cls: "unit-input"
        });
        this.editTitleLY.on(BI.SignEditor.EVENT_CONFIRM, function () {
            self.fireEvent(BI.BubbleChartSetting.EVENT_CHANGE);
        });

        this.isShowTitleX = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Show_Title"),
            width: 90
        });

        this.isShowTitleX.on(BI.Controller.EVENT_CHANGE, function () {
            this.isSelected() ? self.editTitleX.setVisible(true) : self.editTitleX.setVisible(false);
            self.fireEvent(BI.BubbleChartSetting.EVENT_CHANGE);
        });

        this.editTitleX = BI.createWidget({
            type: "bi.sign_editor",
            width: constant.EDITOR_WIDTH,
            height: constant.EDITOR_HEIGHT,
            cls: "unit-input"
        });

        this.editTitleX.on(BI.SignEditor.EVENT_CONFIRM, function () {
            self.fireEvent(BI.BubbleChartSetting.EVENT_CHANGE);
        });

        //y轴刻度自定义
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
            self.fireEvent(BI.BubbleChartSetting.EVENT_CHANGE)
        });

        this.customYScale = BI.createWidget({
            type: "bi.custom_scale",
            wId: o.wId
        });

        this.customYScale.on(BI.CustomScale.EVENT_CHANGE, function () {
            self.fireEvent(BI.BubbleChartSetting.EVENT_CHANGE)
        });

        //x轴刻度自定义
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
            self.fireEvent(BI.BubbleChartSetting.EVENT_CHANGE)
        });

        this.customXScale = BI.createWidget({
            type: "bi.custom_scale",
            wId: o.wId
        });

        this.customXScale.on(BI.CustomScale.EVENT_CHANGE, function () {
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
                    text: BI.i18nText("BI-Format"),
                    cls: "attr-names"
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.lYAxisStyle]
                }, {
                    type: "bi.label",
                    text: BI.i18nText("BI-Num_Level"),
                    lgap: constant.SIMPLE_H_GAP,
                    cls: "attr-names"
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.numberLevellY]
                }, {
                    type: "bi.label",
                    text: BI.i18nText("BI-Unit_Normal"),
                    lgap: constant.SIMPLE_H_GAP,
                    cls: "attr-names"
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.LYUnit]
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.isShowTitleLY, this.editTitleLY]
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
                    text: BI.i18nText("BI-Format"),
                    cls: "attr-names"
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.XAxisStyle]
                }, {
                    type: "bi.label",
                    text: BI.i18nText("BI-Num_Level"),
                    lgap: constant.SIMPLE_H_GAP,
                    cls: "attr-names"
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.numberLevelX]
                }, {
                    type: "bi.label",
                    text: BI.i18nText("BI-Unit_Normal"),
                    lgap: constant.SIMPLE_H_GAP,
                    cls: "attr-names"
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.XUnit]
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.isShowTitleX, this.editTitleX]
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

        //数据标签
        this.showDataLabel = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Show_Data_Label"),
            width: 115
        });

        this.showDataLabel.on(BI.Controller.EVENT_CHANGE, function () {
            self.fireEvent(BI.BubbleChartSetting.EVENT_CHANGE);
        });

        //网格线
        this.gridLine = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Show_Grid_Line"),
            width: 115
        });

        this.gridLine.on(BI.Controller.EVENT_CHANGE, function () {
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
            self.fireEvent(BI.BubbleChartSetting.EVENT_CHANGE)
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
                    items: [this.showDataLabel]
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.gridLine]
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
                self.rulesDisplay.setValue(BICst.DISPLAY_RULES.GRADIENT);
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
            items: [tableStyle, YAxis, XAxis, showElement, otherAttr, modeChange],
            hgap: 10
        })
    },

    _bigDataMode: function (v) {
        this.showDataLabel.setEnable(v);
        this.transferFilter.setEnable(v)
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
        var view = BI.Utils.getWidgetViewByID(wId);
        var titleLY = BI.Utils.getWSLeftYAxisTitleByID(wId), titleX = BI.Utils.getWSXAxisTitleByID(wId);
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
        this.rulesDisplay.setValue(BI.Utils.getWSShowRulesByID(wId));
        this._colorSettingChange(BI.Utils.getWSShowRulesByID(wId));
        this.fixedConditions.setValue(BI.Utils.getWSBubbleFixedColorsByID(wId));
        this.gradientConditions.setValue(BI.Utils.getWSBubbleGradientsByID(wId));
        this.bubbleStyleGroup.setValue(BI.Utils.getWSBubbleStyleByID(wId));
        this.transferFilter.setSelected(BI.Utils.getWSTransferFilterByID(wId));
        this.colorSelect.setValue(BI.Utils.getWSChartColorByID(wId));
        this.lYAxisStyle.setValue(BI.Utils.getWSLeftYAxisStyleByID(wId));
        this.XAxisStyle.setValue(BI.Utils.getWSXAxisStyleByID(wId));
        this.numberLevellY.setValue(BI.Utils.getWSLeftYAxisNumLevelByID(wId));
        this.numberLevelX.setValue(BI.Utils.getWSXAxisNumLevelByID(wId));
        this.LYUnit.setValue(BI.Utils.getWSLeftYAxisUnitByID(wId));
        this.XUnit.setValue(BI.Utils.getWSXAxisUnitByID(wId));
        this.isShowTitleLY.setSelected(BI.Utils.getWSShowLeftYAxisTitleByID(wId));
        this.isShowTitleX.setSelected(BI.Utils.getWSShowXAxisTitleByID(wId));
        this.editTitleLY.setValue(titleLY);
        this.editTitleX.setValue(titleX);
        this.legend.setValue(BI.Utils.getWSChartLegendByID(wId));
        this.showDataLabel.setSelected(BI.Utils.getWSShowDataLabelByID(wId));
        this.gridLine.setSelected(BI.Utils.getWSShowGridLineByID(wId));
        this.bubbleSizeFrom.setValue(BI.Utils.getWSMinBubbleSizeByID(wId));
        this.bubbleSizeTo.setValue(BI.Utils.getWSMaxBubbleSizeByID(wId));
        this.bigDataMode.setSelected(BI.Utils.getWSBigDataModelByID(wId));
        this._bigDataMode(!BI.Utils.getWSBigDataModelByID(wId));
        this.showYCustomScale.setSelected(BI.Utils.getWSShowYCustomScale(wId));
        this.customYScale.setValue(BI.Utils.getWSCustomYScale(wId));
        this.customYScale.setVisible(BI.Utils.getWSShowYCustomScale(wId));
        this.showXCustomScale.setSelected(BI.Utils.getWSShowXCustomScale(wId));
        this.customXScale.setValue(BI.Utils.getWSCustomXScale(wId));
        this.customXScale.setVisible(BI.Utils.getWSShowXCustomScale(wId));

        this.isShowTitleLY.isSelected() ? this.editTitleLY.setVisible(true) : this.editTitleLY.setVisible(false);
        this.isShowTitleX.isSelected() ? this.editTitleX.setVisible(true) : this.editTitleX.setVisible(false);
    },

    getValue: function () {
        return {
            rules_display: this.rulesDisplay.getValue()[0],
            fixed_colors: this.fixedConditions.getValue(),
            gradient_colors: this.gradientConditions.getValue(),
            bubble_style: this.bubbleStyleGroup.getValue()[0],
            transfer_filter: this.transferFilter.isSelected(),
            chart_color: this.colorSelect.getValue()[0],
            left_y_axis_style: this.lYAxisStyle.getValue()[0],
            x_axis_style: this.XAxisStyle.getValue()[0],
            left_y_axis_number_level: this.numberLevellY.getValue()[0],
            x_axis_number_level: this.numberLevelX.getValue()[0],
            left_y_axis_unit: this.LYUnit.getValue(),
            x_axis_unit: this.XUnit.getValue(),
            show_left_y_axis_title: this.isShowTitleLY.isSelected(),
            show_x_axis_title: this.isShowTitleX.isSelected(),
            left_y_axis_title: this.editTitleLY.getValue(),
            x_axis_title: this.editTitleX.getValue(),
            chart_legend: this.legend.getValue()[0],
            show_data_label: this.showDataLabel.isSelected(),
            show_grid_line: this.gridLine.isSelected(),
            bubble_min_size: this.bubbleSizeFrom.getValue(),
            bubble_max_size: this.bubbleSizeTo.getValue(),
            big_data_mode: this.bigDataMode.isSelected(),
            show_y_custom_scale: this.showYCustomScale.isSelected(),
            custom_y_scale: this.customYScale.getValue(),
            show_x_custom_scale: this.showXCustomScale.isSelected(),
            custom_x_scale: this.customXScale.getValue()
        }
    },

    setValue: function (v) {
        this.rulesDisplay.setValue(v.rules_display);
        this.fixedConditions.setValue(v.fixed_colors);
        this.gradientConditions.setValue(v.gradient_colors);
        this.bubbleStyleGroup.setValue(v.bubble_style);
        this.transferFilter.setSelected(v.transfer_filter);
        this.colorSelect.setValue(v.chart_color);
        this.lYAxisStyle.setValue(v.left_y_axis_style);
        this.XAxisStyle.setValue(v.x_axis_style);
        this.numberLevellY.setValue(v.left_y_axis_number_level);
        this.numberLevelX.setValue(v.x_axis_number_level);
        this.LYUnit.setValue(v.left_y_axis_unit);
        this.XUnit.setValue(v.x_axis_unit);
        this.isShowTitleLY.setSelected(v.show_left_y_axis_title);
        this.isShowTitleX.setSelected(v.x_axis_title);
        this.editTitleLY.setValue(v.left_y_axis_title);
        this.editTitleX.setValue(v.x_axis_title);
        this.legend.setValue(v.chart_legend);
        this.showDataLabel.setSelected(v.show_data_label);
        this.gridLine.setSelected(v.show_grid_line);
        this.bubbleSizeFrom.setValue(v.bubble_min_size);
        this.bubbleSizeTo.setValue(v.bubble_max_size);
        this.bigDataMode.setSelected(v.big_data_mode);
        this.showYCustomScale.setSelected(v.show_y_custom_scale);
        this.customYScale.setValue(v.custom_y_scale);
        this.showXCustomScale.setSelected(v.show_x_custom_scale);
        this.customXScale.setValue(v.custom_x_scale)
    }
});
BI.BubbleChartSetting.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.bubble_chart_setting", BI.BubbleChartSetting);