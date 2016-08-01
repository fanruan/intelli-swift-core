/**
 * @class BI.BubbleChartSetting
 * @extends BI.Widget
 * 柱状，堆积柱状，组合图样式
 */
BI.BubbleChartSetting = BI.inherit(BI.Widget, {

    _defaultConfig: function(){
        return BI.extend(BI.BubbleChartSetting.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-charts-setting"
        })
    },

    _init: function(){
        BI.BubbleChartSetting.superclass._init.apply(this, arguments);
        var self = this;
        this.constant = BICst.CHART.CONSTANT;

        this.rulesDisplay = BI.createWidget({
            type: "bi.segment",
            whiteSpace: "normal",
            height: 40,
            width: this.constant.NUMBER_LEVEL_SEGMENT_WIDTH,
            items: BICst.BUBBLE_DISPLAY_RULES
        });

        this.rulesDisplay.on(BI.Segment.EVENT_CHANGE, function (v) {
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
            height: 20
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

        this.gradientConditions.on(BI.ChartAddGradientConditionGroup.EVENT_CHANGE, function() {
            self.fireEvent(BI.BubbleChartSetting.EVENT_CHANGE)
        });

        this.addGradientButton = BI.createWidget({
            type: "bi.button",
            text: BI.i18nText("BI-Add_Condition"),
            height: this.constant.BUTTON_HEIGHT
        });

        this.addGradientButton.on(BI.Button.EVENT_CHANGE, function() {
            self.gradientConditions.addItem();
            self.fireEvent(BI.BubbleChartSetting.EVENT_CHANGE)
        });

        this.gradientSetting = BI.createWidget({
            type: "bi.label",
            cls: "attr-names",
            textAlign: "left",
            text: BI.i18nText("BI-Color_Setting"),
            height: 20
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
            height: this.constant.BUTTON_HEIGHT
        });

        this.addConditionButton.on(BI.Button.EVENT_CHANGE, function() {
            self.fixedConditions.addItem();
            self.fireEvent(BI.BubbleChartSetting.EVENT_CHANGE)
        });

        this.conditions = BI.createWidget({
            type: "bi.chart_add_condition_group",
            width: "100%"
        });

        this.conditions.on(BI.ChartAddConditionGroup.EVENT_CHANGE, function() {
            self.fireEvent(BI.BubbleChartSetting.EVENT_CHANGE)
        });

        this.colorSelect = BI.createWidget({
            type: "bi.chart_setting_select_color_combo",
            width: 130
        });
        this.colorSelect.populate();

        this.colorSelect.on(BI.ChartSettingSelectColorCombo.EVENT_CHANGE, function(){
            self.fireEvent(BI.BubbleChartSetting.EVENT_CHANGE);
        });

        this.dimensionColor = BI.createWidget({
            type: "bi.left",
            items: BI.createItems([{
                type: "bi.label",
                text: BI.i18nText("BI-Color_Setting"),
                textHeight: this.constant.BUTTON_HEIGHT,
                cls: "attr-names"
            }, {
                type: "bi.center_adapt",
                items: [this.colorSelect],
                lgap: this.constant.SIMPLE_H_GAP
            }])
        });

        this.bubbleStyleGroup = BI.createWidget({
            type: "bi.button_group",
            items: BI.createItems(BICst.BUBBLE_CHART_STYLE_GROUP, {
                type: "bi.icon_button",
                width: 30,
                height: 30,
                iconWidth: 24,
                iconHeight: 24
            }),
            layouts: [{
                type: "bi.left",
                lgap: 2
            }]
        });

        this.bubbleStyleGroup.on(BI.ButtonGroup.EVENT_CHANGE, function(){
            self.fireEvent(BI.GroupTableSetting.EVENT_CHANGE);
        });

        var tableStyle = BI.createWidget({
            type: "bi.horizontal_adapt",
            columnSize: [100],
            verticalAlign: "top",
            cls: "single-line-settings",
            items: [{
                type: "bi.label",
                text: BI.i18nText("BI-Table_Sheet_Style"),
                textHeight: this.constant.SINGLE_LINE_HEIGHT,
                lgap: this.constant.SIMPLE_H_LGAP,
                textAlign: "left",
                cls: "line-title"
            }, {
                type: "bi.left",
                cls: "detail-style",
                items: BI.createItems([/*{
                    type: "bi.label",
                    text: BI.i18nText("BI-Display_Rules"),
                    cls: "attr-names"
                },  {
                    el: {
                        type: "bi.center_adapt",
                        items: [this.rulesDisplay]
                    },
                   lgap: this.constant.SIMPLE_H_GAP
                }, */{
                    type: "bi.center_adapt",
                    items: [this.dimensionColor]
                },/* {
                    type: "bi.center_adapt",
                    items: [this.addGradientButton],
                    lgap:15
                }, {
                    type: "bi.center_adapt",
                    items: [this.addConditionButton],
                    lgap: 15
                }, */{
                    type: "bi.label",
                    text: BI.i18nText("BI-Total_Style"),
                    cls: "attr-names",
                    lgap: 15
                }, {
                    el: {
                        type: "bi.center_adapt",
                        items: [this.bubbleStyleGroup]
                    },
                    lgap: this.constant.SIMPLE_H_GAP
                }/*, this.fixedColorSetting, this.gradientColorSetting*/], {
                    height: this.constant.SINGLE_LINE_HEIGHT
                })
            }]
        });

        //格式和数量级
        this.lYAxisStyle = BI.createWidget({
            type: "bi.segment",
            width: this.constant.FORMAT_SEGMENT_WIDTH,
            height: this.constant.BUTTON_HEIGHT,
            items: BICst.TARGET_STYLE_FORMAT
        });

        this.lYAxisStyle.on(BI.Segment.EVENT_CHANGE, function(){
            self.fireEvent(BI.BubbleChartSetting.EVENT_CHANGE);
        });

        this.numberLevellY = BI.createWidget({
            type: "bi.segment",
            width: this.constant.NUMBER_LEVEL_SEGMENT_WIDTH,
            height: this.constant.BUTTON_HEIGHT,
            items: BICst.TARGET_STYLE_LEVEL
        });

        this.numberLevellY.on(BI.Segment.EVENT_CHANGE, function(){
            self.fireEvent(BI.BubbleChartSetting.EVENT_CHANGE);
        });

        this.XAxisStyle = BI.createWidget({
            type: "bi.segment",
            width: this.constant.FORMAT_SEGMENT_WIDTH,
            height: this.constant.BUTTON_HEIGHT,
            items: BICst.TARGET_STYLE_FORMAT
        });

        this.XAxisStyle.on(BI.Segment.EVENT_CHANGE, function(){
            self.fireEvent(BI.BubbleChartSetting.EVENT_CHANGE);
        });

        this.numberLevelX = BI.createWidget({
            type: "bi.segment",
            width: this.constant.NUMBER_LEVEL_SEGMENT_WIDTH,
            height: this.constant.BUTTON_HEIGHT,
            items: BICst.TARGET_STYLE_LEVEL
        });

        this.numberLevelX.on(BI.Segment.EVENT_CHANGE, function(){
            self.fireEvent(BI.BubbleChartSetting.EVENT_CHANGE);
        });

        //单位
        this.LYUnit = BI.createWidget({
            type: "bi.sign_editor",
            width: this.constant.EDITOR_WIDTH,
            height: this.constant.EDITOR_HEIGHT,
            cls: "unit-input",
            watermark: BI.i18nText("BI-Custom_Input")
        });

        this.LYUnit.on(BI.SignEditor.EVENT_CONFIRM, function(){
            self.fireEvent(BI.BubbleChartSetting.EVENT_CHANGE);
        });

        this.XUnit = BI.createWidget({
            type: "bi.sign_editor",
            width: this.constant.EDITOR_WIDTH,
            height: this.constant.EDITOR_HEIGHT,
            cls: "unit-input",
            watermark: BI.i18nText("BI-Custom_Input")
        });

        this.XUnit.on(BI.SignEditor.EVENT_CONFIRM, function(){
            self.fireEvent(BI.BubbleChartSetting.EVENT_CHANGE);
        });

        //显示标题
        this.isShowTitleLY = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Show_Title"),
            width: 90
        });

        this.isShowTitleLY.on(BI.Controller.EVENT_CHANGE, function(){
            this.isSelected() ? self.editTitleLY.setVisible(true) : self.editTitleLY.setVisible(false);
            self.fireEvent(BI.BubbleChartSetting.EVENT_CHANGE);
        });

        this.editTitleLY = BI.createWidget({
            type: "bi.sign_editor",
            width: this.constant.EDITOR_WIDTH,
            height: this.constant.EDITOR_HEIGHT,
            cls: "unit-input"
        });
        this.editTitleLY.on(BI.SignEditor.EVENT_CONFIRM, function(){
            self.fireEvent(BI.BubbleChartSetting.EVENT_CHANGE);
        });

        this.isShowTitleX = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Show_Title"),
            width: 90
        });

        this.isShowTitleX.on(BI.Controller.EVENT_CHANGE, function(){
            this.isSelected() ? self.editTitleX.setVisible(true) : self.editTitleX.setVisible(false);
            self.fireEvent(BI.BubbleChartSetting.EVENT_CHANGE);
        });

        this.editTitleX = BI.createWidget({
            type: "bi.sign_editor",
            width: this.constant.EDITOR_WIDTH,
            height: this.constant.EDITOR_HEIGHT,
            cls: "unit-input"
        });

        this.editTitleX.on(BI.SignEditor.EVENT_CONFIRM, function(){
            self.fireEvent(BI.BubbleChartSetting.EVENT_CHANGE);
        });

        //轴刻度自定义
        this.YScale = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Scale_Customize"),
            width: 120
        });

        this.YScale.on(BI.Controller.EVENT_CHANGE, function() {

        });

        this.XScale = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Scale_Customize"),
            width: 120
        });

        this.XScale.on(BI.Controller.EVENT_CHANGE, function () {

        });

        var YAxis = BI.createWidget({
            type: "bi.horizontal_adapt",
            columnSize: [80],
            verticalAlign: "top",
            cls: "single-line-settings",
            items: [{
                type: "bi.label",
                height: "100%",
                textHeight: this.constant.SINGLE_LINE_HEIGHT,
                textAlign: "left",
                lgap: this.constant.SIMPLE_H_LGAP,
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
                    type: "bi.center_adapt",
                    items: [this.lYAxisStyle]
                }, {
                    type: "bi.label",
                    text: BI.i18nText("BI-Num_Level"),
                    lgap: this.constant.SIMPLE_H_GAP,
                    cls: "attr-names"
                }, {
                    type: "bi.center_adapt",
                    items: [this.numberLevellY]
                }, {
                    type: "bi.label",
                    text: BI.i18nText("BI-Unit_Normal"),
                    lgap: this.constant.SIMPLE_H_GAP,
                    cls: "attr-names"
                }, {
                    type: "bi.center_adapt",
                    items: [this.LYUnit]
                }, {
                    type: "bi.center_adapt",
                    items: [this.isShowTitleLY, this.editTitleLY]
                }/*, {
                    type: "bi.center_adapt",
                    items: [this.YScale]
                }*/], {
                    height: this.constant.SINGLE_LINE_HEIGHT
                }),
                lgap: this.constant.SIMPLE_H_GAP
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
                textHeight: this.constant.SINGLE_LINE_HEIGHT,
                lgap: this.constant.SIMPLE_H_LGAP
            }, {
                type: "bi.left",
                cls: "detail-style",
                items: BI.createItems([{
                    type: "bi.label",
                    text: BI.i18nText("BI-Format"),
                    cls: "attr-names"
                }, {
                    type: "bi.center_adapt",
                    items: [this.XAxisStyle]
                }, {
                    type: "bi.label",
                    text: BI.i18nText("BI-Num_Level"),
                    lgap: this.constant.SIMPLE_H_GAP,
                    cls: "attr-names"
                }, {
                    type: "bi.center_adapt",
                    items: [this.numberLevelX]
                }, {
                    type: "bi.label",
                    text: BI.i18nText("BI-Unit_Normal"),
                    lgap: this.constant.SIMPLE_H_GAP,
                    cls: "attr-names"
                }, {
                    type: "bi.center_adapt",
                    items: [this.XUnit]
                }, {
                    type: "bi.center_adapt",
                    items: [this.isShowTitleX, this.editTitleX]
                }/*, {
                    type: "bi.center_adapt",
                    items: [this.XScale]
                }*/], {
                    height: this.constant.SINGLE_LINE_HEIGHT
                }),
                lgap: this.constant.SIMPLE_H_GAP
            }]
        });

        //图例
        this.legend = BI.createWidget({
            type: "bi.segment",
            width: this.constant.LEGEND_SEGMENT_WIDTH,
            height: this.constant.BUTTON_HEIGHT,
            items: BICst.CHART_LEGEND
        });

        this.legend.on(BI.Segment.EVENT_CHANGE, function(){
            self.fireEvent(BI.BubbleChartSetting.EVENT_CHANGE);
        });

        //数据标签
        this.showDataLabel = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Show_Data_Label"),
            width: 115
        });

        this.showDataLabel.on(BI.Controller.EVENT_CHANGE, function(){
            self.fireEvent(BI.BubbleChartSetting.EVENT_CHANGE);
        });

        //网格线
        this.gridLine = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Show_Grid_Line"),
            width: 115
        });

        this.gridLine.on(BI.Controller.EVENT_CHANGE, function(){
            self.fireEvent(BI.BubbleChartSetting.EVENT_CHANGE);
        });

        //气泡大小
        this.bubbleSizeFrom = BI.createWidget({
            type: "bi.sign_editor",
            width: this.constant.EDITOR_WIDTH,
            height: this.constant.EDITOR_HEIGHT,
            cls: "unit-input"
        });

        this.bubbleSizeFrom.on(BI.SignEditor.EVENT_CONFIRM, function () {

        });

        this.bubbleSizeTo = BI.createWidget({
            type: "bi.sign_editor",
            width: this.constant.EDITOR_WIDTH,
            height: this.constant.EDITOR_HEIGHT,
            cls: "unit-input"
        });

        this.bubbleSizeTo.on(BI.SignEditor.EVENT_CONFIRM, function () {

        });

        var showElement = BI.createWidget({
            type: "bi.horizontal_adapt",
            columnSize: [80],
            verticalAlign: "top",
            cls: "single-line-settings",
            items: [{
                type: "bi.label",
                text: BI.i18nText("BI-Element_Show"),
                textHeight: this.constant.SINGLE_LINE_HEIGHT,
                lgap: this.constant.SIMPLE_H_LGAP,
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
                    type: "bi.center_adapt",
                    items: [this.legend]
                }, {
                    type: "bi.center_adapt",
                    items: [this.showDataLabel]
                }, {
                    type: "bi.center_adapt",
                    items: [this.gridLine]
                }/*, {
                    type: "bi.center_adapt",
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
                }*/], {
                    height: this.constant.SINGLE_LINE_HEIGHT
                }),
                lgap: this.constant.SIMPLE_H_GAP
            }]
        });

        //联动传递指标过滤条件
        this.transferFilter = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Bind_Target_Condition"),
            width: 170
        });
        this.transferFilter.on(BI.Controller.EVENT_CHANGE, function(){
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
            height: this.constant.SINGLE_LINE_HEIGHT,
            lhgap: this.constant.SIMPLE_H_GAP
        });

        //大数据模式
        this.bigDataMode = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Big_Data_Mode"),
            width: 120
        });

        this.bigDataMode.on(BI.Controller.EVENT_CHANGE, function() {

        });

        var modeChange = BI.createWidget({
            type:"bi.left_right_vertical_adapt",
            cls: "single-line-settings",
            items: {
                left: [{
                    type: "bi.label",
                    text: BI.i18nText("BI-Mode_Change"),
                    cls: "line-title"
                }, this.bigDataMode]
            },
            height: this.constant.SINGLE_LINE_HEIGHT,
            lhgap: this.constant.SIMPLE_H_GAP
        });

        BI.createWidget({
            type: "bi.vertical",
            element: this.element,
            items: [tableStyle, YAxis, XAxis, showElement, otherAttr/*, modeChange*/],
            hgap: 10
        })
    },

    _colorSettingChange: function (v) {
        switch(v) {
            case BICst.DISPLAY_RULES.DIMENSION:
                //this.dimensionColor.setVisible(true);
                this.addConditionButton.setVisible(false);
                this.fixedColorSetting.setVisible(false);
                this.addGradientButton.setVisible(false);
                this.gradientColorSetting.setVisible(false);
                break;
            case BICst.DISPLAY_RULES.FIXED:
                //this.dimensionColor.setVisible(false);
                this.addConditionButton.setVisible(true);
                this.fixedColorSetting.setVisible(true);
                this.addGradientButton.setVisible(false);
                this.gradientColorSetting.setVisible(false);
                break;
            case BICst.DISPLAY_RULES.GRADIENT:
                //this.dimensionColor.setVisible(false);
                this.addConditionButton.setVisible(false);
                this.fixedColorSetting.setVisible(false);
                this.addGradientButton.setVisible(true);
                this.gradientColorSetting.setVisible(true);
                break;
        }
    },

    populate: function(){
        var wId = this.options.wId;
        var view = BI.Utils.getWidgetViewByID(wId);
        var titleLY = BI.Utils.getWSLeftYAxisTitleByID(wId), titleX = BI.Utils.getWSXAxisTitleByID(wId);
        if(titleLY === ""){
            BI.any(view[BICst.REGION.TARGET1], function(idx, dId){
                if(BI.Utils.isDimensionUsable(dId)){
                    titleLY = BI.Utils.getDimensionNameByID(dId);
                    return true;
                }
                return false;
            });
        }
        if(titleX === ""){
            BI.any(view[BICst.REGION.DIMENSION1], function(idx, dId){
                if(BI.Utils.isDimensionUsable(dId)){
                    titleX = BI.Utils.getDimensionNameByID(dId);
                    return true;
                }
                return false;
            });
        }
        this.rulesDisplay.setValue(BI.Utils.getWSShowRulesByID(wId));
        this._colorSettingChange(BI.Utils.getWSShowRulesByID(wId));
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

        this.isShowTitleLY.isSelected() ? this.editTitleLY.setVisible(true) : this.editTitleLY.setVisible(false);
        this.isShowTitleX.isSelected() ? this.editTitleX.setVisible(true) : this.editTitleX.setVisible(false);
    },

    getValue: function(){
        return {
            rules_display: this.rulesDisplay.getValue()[0],
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
            show_grid_line: this.gridLine.isSelected()
        }
    },

    setValue: function(v){
        this.rulesDisplay.setValue(v.rules_display);
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
    }
});
BI.BubbleChartSetting.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.bubble_chart_setting", BI.BubbleChartSetting);