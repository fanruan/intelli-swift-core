/**
 * @class BI.DashboardChartSetting
 * @extends BI.Widget
 * 仪表盘样式
 */
BI.DashboardChartSetting = BI.inherit(BI.Widget, {

    constant: {
        SINGLE_LINE_HEIGHT: 60,
        SIMPLE_H_GAP: 10,
        SIMPLE_L_GAP: 2,
        SIMPLE_H_LGAP: 5,
        CHECKBOX_WIDTH: 16,
        EDITOR_WIDTH: 80,
        EDITOR_HEIGHT: 26,
        BUTTON_WIDTH: 40,
        BUTTON_HEIGHT: 30,
        ICON_WIDTH: 24,
        ICON_HEIGHT: 24,
        NUMBER_LEVEL_SEGMENT_WIDTH: 300,
        FORMAT_SEGMENT_WIDTH: 240,
        RADIO_WIDTH: 100,
        DASHBOARD_HEIGHT: 60,
        POINTER_SEGMENT_WIDTH: 150
    },

    _defaultConfig: function(){
        return BI.extend(BI.DashboardChartSetting.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-charts-setting"
        })
    },

    _init: function(){
        BI.DashboardChartSetting.superclass._init.apply(this, arguments);
        var self = this, o = this.options;

        var items = o.conditions;


        //联动传递指标过滤条件
        this.transferFilter = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Bind_Target_Condition"),
            width: 170
        });
        this.transferFilter.on(BI.Controller.EVENT_CHANGE, function(){
            self.fireEvent(BI.DashboardChartSetting.EVENT_CHANGE);
        });

        this.chartTypeGroup = BI.createWidget({
            type: "bi.button_group",
            items: BI.createItems(BICst.DASHBOARD_CHART_STYLE_GROUP, {
                type: "bi.icon_button",
                extraCls: "chart-style-font",
                width: this.constant.BUTTON_WIDTH,
                height: this.constant.BUTTON_HEIGHT,
                iconWidth: this.constant.ICON_WIDTH,
                iconHeight: this.constant.ICON_HEIGHT
            }),
            layouts: [{
                type: "bi.vertical_adapt",
                height: this.constant.SINGLE_LINE_HEIGHT
            }]
        });
        this.chartTypeGroup.on(BI.ButtonGroup.EVENT_CHANGE, function(v){
            self._showPointer(v);
            self.fireEvent(BI.DashboardChartSetting.EVENT_CHANGE);
        });

        //单指针，多指针
        this.pointer = BI.createWidget({
            type: "bi.segment",
            width: this.constant.POINTER_SEGMENT_WIDTH,
            height: this.constant.BUTTON_HEIGHT,
            items: BICst.POINTERS
        });

        this.pointer.on(BI.Segment.EVENT_CHANGE, function () {
            self.fireEvent((BI.DashboardChartSetting.EVENT_CHANGE));
        });

        //数量级和单位
        this.numberLevellY = BI.createWidget({
            type: "bi.segment",
            width: this.constant.NUMBER_LEVEL_SEGMENT_WIDTH,
            height: this.constant.BUTTON_HEIGHT,
            items: BICst.TARGET_STYLE_LEVEL
        });

        this.numberLevellY.on(BI.Segment.EVENT_CHANGE, function(){
            self.fireEvent(BI.DashboardChartSetting.EVENT_CHANGE);
        });

        this.LYUnit = BI.createWidget({
            type: "bi.sign_editor",
            width: this.constant.EDITOR_WIDTH,
            height: this.constant.EDITOR_HEIGHT,
            cls: "unit-input",
            watermark: BI.i18nText("BI-Custom_Input")
        });

        this.LYUnit.on(BI.SignEditor.EVENT_CONFIRM, function(){
            self.fireEvent(BI.DashboardChartSetting.EVENT_CHANGE);
        });

        this.scale = BI.createWidget({
            type: "bi.button_group",
            items: BI.createItems(BICst.CHART_SCALE_SETTING, {
                type: "bi.single_select_radio_item",
                width: this.constant.RADIO_WIDTH ,
                height: this.constant.BUTTON_HEIGHT
            }),
            layouts: [{
                type: "bi.horizontal_adapt",
                height: this.constant.BUTTON_HEIGHT
            }]
        });

        this.scale.on(BI.ButtonGroup.EVENT_CHANGE , function (v) {
            self._doClickButton(v);
            self. fireEvent(BI.DashboardChartSetting.EVENT_CHANGE)
        });

        //添加条件button
        this.addConditionButton = BI.createWidget({
            type: "bi.button",
            text: "+" + BI.i18nText("BI-Add_Condition"),
            height: this.constant.BUTTON_HEIGHT
        });

        this.addConditionButton.on(BI.Button.EVENT_CHANGE, function () {
            self.conditions.addItem();
            self.fireEvent(BI.DashboardChartSetting.EVENT_CHANGE);
        });

        this.conditions = BI.createWidget({
            type: "bi.chart_add_condition_group"
        });

        this.conditions.on(BI.ChartAddConditionGroup.EVENT_CHANGE , function  () {
            self.fireEvent(BI.DashboardChartSetting.EVENT_CHANGE);
        });

        var tableStyle = BI.createWidget({
            type: "bi.horizontal_adapt",
            columnSize: [80],
            cls: "single-line-settings",
            items: [{
                type: "bi.label",
                text: BI.i18nText("BI-Table_Sheet_Style"),
                lgap: this.constant.SIMPLE_H_LGAP,
                textAlign: "left",
                cls: "line-title"
            }, {
                type: "bi.left",
                cls: "detail-style",
                items: BI.createItems([{
                    type: "bi.label",
                    text: BI.i18nText("BI-Type"),
                    cls: "attr-names",
                    lgap: this.constant.SIMPLE_H_GAP2
                }, {
                    el: {
                        type: "bi.center_adapt",
                        items: [this.chartTypeGroup]
                    },
                    lgap: this.constant.SIMPLE_H_GAP
                }], {
                    height: this.constant.SINGLE_LINE_HEIGHT
                })
            }]
        });

        var pointers = BI.createWidget({
            type: "bi.horizontal_adapt",
            columnSize: [78],
            cls:　"single-line-settings",
            items: [{
                    type: "bi.label",
                    text: BI.i18nText("BI-Number_of_pointers"),
                    textAlign: "left",
                    lgap: this.constant.SIMPLE_H_LGAP,
                    cls: "line-title"
            }, {
                type: "bi.left",
                items: BI.createItems([{
                    type: "bi.center_adapt",
                    items: [this.pointer]
                }], {
                    height: this.constant.SINGLE_LINE_HEIGHT
                }),
                lgap: this.constant.SIMPLE_H_GAP
            }]
        });

        var lYAxis = BI.createWidget({
            type: "bi.horizontal_adapt",
            columnSize: [80],
            cls: "single-line-settings",
            verticalAlign: "top",
            items: [{
                type: "bi.label",
                textHeight: this.constant.DASHBOARD_HEIGHT,
                text: BI.i18nText("BI-Dashboard"),
                textAlign: "left",
                lgap: this.constant.SIMPLE_H_LGAP,
                cls: "line-title"
            }, {
                type: "bi.left",
                cls: "detail-style",
                items: BI.createItems([{
                    type: "bi.label",
                    text: BI.i18nText("BI-Num_Level"),
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
                    items: [this.scale]
                }, {
                    type: "bi.center_adapt",
                    items: [this.addConditionButton]
                }, {
                    type: "bi.center_adapt",
                    items: [this.conditions],
                    width: 600,
                    height: ""
                }], {
                    height: this.constant.SINGLE_LINE_HEIGHT
                }),
                lgap: this.constant.SIMPLE_H_GAP
            }]
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

        BI.createWidget({
            type: "bi.vertical",
            element: this.element,
            items: [tableStyle, pointers, lYAxis, otherAttr],
            hgap: 10
        });

    },

    _doClickButton: function (v) {
        var self = this;
        switch(v) {
            case BICst.SCALE_SETTING.AUTO:
                self.addConditionButton.setVisible(false);
                self.conditions.setVisible(false);
                break;
            case BICst.SCALE_SETTING.CUSTOM:
                self.addConditionButton.setVisible(true);
                self.conditions.setVisible(true);
                break;
        }
    },

    _showPointer: function (pictureType) {
        self = this;
        switch (pictureType) {
            case BICst.CHART_SHAPE.NORMAL:
            case BICst.CHART_SHAPE.HALF_DASHBOARD:
                self.pointer.setEnable(true);
                break;
            case BICst.CHART_SHAPE.PERCENT_DASHBOARD:
            case BICst.CHART_SHAPE.PERCENT_SCALE_SLOT:
            case BICst.CHART_SHAPE.VERTICAL_TUBE:
            case BICst.CHART_SHAPE.HORIZONTAL_TUBE:
                self.pointer.setEnable(false);
                break;
        }
    },

    populate: function(){
        var wId = this.options.wId;
        this.transferFilter.setSelected(BI.Utils.getWSTransferFilterByID(wId));
        this.chartTypeGroup.setValue(BI.Utils.getWSChartDashboardTypeByID(wId));
        this.pointer.setValue(BI.Utils.getWSNumberOfPointerByID(wId));
        this._showPointer(BI.Utils.getWSNumberOfPointerByID(wId));
        this.scale.setValue(BI.Utils.getWSScaleByID(wId));
        this._doClickButton(BI.Utils.getWSScaleByID(wId));
        this.conditions.populate(BI.Utils.getWSConditionButtonsByID(wId));
        this.numberLevellY.setValue(BI.Utils.getWSDashboardNumLevelByID(wId));
        this.LYUnit.setValue(BI.Utils.getWSDashboardUnitByID(wId));
    },

    getValue: function(){
        return {
            transfer_filter: this.transferFilter.isSelected(),
            chart_dashboard_type: this.chartTypeGroup.getValue()[0],
            number_of_pointer: this.pointer.getValue()[0],
            dashboard_number_level: this.numberLevellY.getValue()[0],
            auto_custom: this.scale.getValue()[0],
            condition_buttons: this.conditions.getValue(),
            dashboard_unit: this.LYUnit.getValue()
        }
    },

    setValue: function(v){
        this.transferFilter.setSelected(v.transfer_filter);
        this.chartTypeGroup.setValue(v.chart_dashboard_type);
        this.pointer.setValue(v.number_of_pointer);
        this.numberLevellY.setValue(v.dashboard_number_level);
        this.scale.setValue(v.auto_custom);
        this.LYUnit.setValue(v.dashboard_unit);
    }
});
BI.DashboardChartSetting.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.dashboard_chart_setting", BI.DashboardChartSetting);