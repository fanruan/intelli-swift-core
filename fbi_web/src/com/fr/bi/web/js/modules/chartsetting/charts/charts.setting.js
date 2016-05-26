/**
 * created by young
 * 分组表的样式设置
 */
BI.ChartsSetting = BI.inherit(BI.Widget, {

    constant: {
        SINGLE_LINE_HEIGHT: 60,
        SIMPLE_H_GAP: 10,
        SIMPLE_L_GAP: 2,
        CHECKBOX_WIDTH: 16,
        EDITOR_WIDTH: 80,
        EDITOR_HEIGHT: 26,
        BUTTON_WIDTH: 40,
        BUTTON_HEIGHT: 30,
        ICON_WIDTH: 24,
        ICON_HEIGHT: 24,
        NUMBER_LEVEL_SEGMENT_WIDTH: 300,
        FORMAT_SEGMENT_WIDTH: 240
    },

    _defaultConfig: function(){
        return BI.extend(BI.ChartsSetting.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-charts-setting"
        })
    },

    _init: function(){
        BI.ChartsSetting.superclass._init.apply(this, arguments);
        var self = this, o = this.options;

        this.colorSelect = BI.createWidget({
            type: "bi.chart_setting_select_color_combo",
            width: 130
        });
        this.colorSelect.populate(BICst.CHART_COLORS);

        this.colorSelect.on(BI.ChartSettingSelectColorCombo.EVENT_CHANGE, function(){
            self.fireEvent(BI.ChartsSetting.EVENT_CHANGE);
        });

        var tableStyle = BI.createWidget({
            type: "bi.left_right_vertical_adapt",
            cls: "single-line-settings",
            items: {
                left: [{
                    type: "bi.label",
                    text: BI.i18nText("BI-Chart_Color"),
                    cls: "attr-names"
                }, this.colorSelect]
            },
            height: this.constant.SINGLE_LINE_HEIGHT,
            lhgap: this.constant.SIMPLE_H_GAP
        });

        //格式和数量级
        this.lYAxisStyle = BI.createWidget({
            type: "bi.segment",
            width: this.constant.FORMAT_SEGMENT_WIDTH,
            height: this.constant.BUTTON_HEIGHT,
            items: BICst.TARGET_STYLE_FORMAT
        });

        this.lYAxisStyle.on(BI.Segment.EVENT_CHANGE, function(){
            self.fireEvent(BI.ChartsSetting.EVENT_CHANGE);
        });

        //this.numberLevellY = BI.createWidget({
        //    type: "bi.segment",
        //    width: this.constant.NUMBER_LEVEL_SEGMENT_WIDTH,
        //    height: this.constant.BUTTON_HEIGHT,
        //    items: BICst.TARGET_STYLE_LEVEL
        //});

        this.rYAxisStyle = BI.createWidget({
            type: "bi.segment",
            width: this.constant.FORMAT_SEGMENT_WIDTH,
            height: this.constant.BUTTON_HEIGHT,
            items: BICst.TARGET_STYLE_FORMAT
        });

        this.rYAxisStyle.on(BI.Segment.EVENT_CHANGE, function(){
            self.fireEvent(BI.ChartsSetting.EVENT_CHANGE);
        });

        //this.numberLevelrY = BI.createWidget({
        //    type: "bi.segment",
        //    width: this.constant.NUMBER_LEVEL_SEGMENT_WIDTH,
        //    height: this.constant.BUTTON_HEIGHT,
        //    items: BICst.TARGET_STYLE_LEVEL
        //});

        //单位
        //this.LYUnit = BI.createWidget({
        //    type: "bi.sign_editor",
        //    width: this.constant.EDITOR_WIDTH,
        //    height: this.constant.EDITOR_HEIGHT,
        //    cls: "unit-input",
        //    watermark: BI.i18nText("BI-Custom_Input")
        //});
        //
        //this.RYUnit = BI.createWidget({
        //    type: "bi.sign_editor",
        //    width: this.constant.EDITOR_WIDTH,
        //    height: this.constant.EDITOR_HEIGHT,
        //    cls: "unit-input",
        //    watermark: BI.i18nText("BI-Custom_Input")
        //});

        //显示标题
        this.isShowTitleLY = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Show_Title"),
            width: 90
        });

        this.isShowTitleLY.on(BI.Controller.EVENT_CHANGE, function(){
            self.fireEvent(BI.ChartsSetting.EVENT_CHANGE);
        });

        this.isShowTitleRY = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Show_Title"),
            width: 90
        });

        this.isShowTitleRY.on(BI.Controller.EVENT_CHANGE, function(){
            self.fireEvent(BI.ChartsSetting.EVENT_CHANGE);
        });

        //轴逆序
        this.reversedLY = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Reversed_Axis"),
            width: 80
        });

        this.reversedLY.on(BI.Controller.EVENT_CHANGE, function(){
            self.fireEvent(BI.ChartsSetting.EVENT_CHANGE);
        });

        this.reversedRY = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Reversed_Axis"),
            width: 80
        });

        this.reversedRY.on(BI.Controller.EVENT_CHANGE, function(){
            self.fireEvent(BI.ChartsSetting.EVENT_CHANGE);
        });

        var lYAxis = BI.createWidget({
            type: "bi.left_right_vertical_adapt",
            cls: "single-line-settings",
            items: {
                left: [{
                    type: "bi.label",
                    text: BI.i18nText("BI-Left_Value_Axis"),
                    cls: "line-title"
                }, {
                    type: "bi.left",
                    items: [{
                        type: "bi.label",
                        text: BI.i18nText("BI-Format"),
                        cls: "attr-names",
                        height: this.constant.SINGLE_LINE_HEIGHT
                    }, {
                        type: "bi.center_adapt",
                        height: this.constant.SINGLE_LINE_HEIGHT,
                        items: [this.lYAxisStyle]
                    }],
                    lgap: this.constant.SIMPLE_L_GAP
                }, this.isShowTitleLY, this.reversedLY]
            },
            height: this.constant.SINGLE_LINE_HEIGHT,
            lhgap: this.constant.SIMPLE_H_GAP
        });

        var rYAxis = BI.createWidget({
            type: "bi.left_right_vertical_adapt",
            cls: "single-line-settings",
            items: {
                left: [{
                    type: "bi.label",
                    text: BI.i18nText("BI-Left_Value_Axis"),
                    cls: "line-title"
                }, {
                    type: "bi.left",
                    items: [{
                        type: "bi.label",
                        text: BI.i18nText("BI-Format"),
                        cls: "attr-names",
                        height: this.constant.SINGLE_LINE_HEIGHT
                    }, {
                        type: "bi.center_adapt",
                        height: this.constant.SINGLE_LINE_HEIGHT,
                        items: [this.rYAxisStyle]
                    }],
                    lgap: this.constant.SIMPLE_L_GAP
                }, this.isShowTitleRY, this.reversedRY]
            },
            height: this.constant.SINGLE_LINE_HEIGHT,
            lhgap: this.constant.SIMPLE_H_GAP
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

        BI.createWidget({
            type: "bi.vertical",
            element: this.element,
            items: [tableStyle, lYAxis, rYAxis, otherAttr],
            hgap: 10
        })
    },

    getValue: function(){
        return {
            transfer_filter: this.transferFilter.isSelected(),
            chart_color: this.colorSelect.getValue()[0],
            left_y_axis_style: this.lYAxisStyle.getValue()[0],
            right_y_axis_style: this.rYAxisStyle.getValue()[0],
            //left_y_axis_number_level: this.numberLevellY.getValue(),
            //right_y_axis_number_level: this.numberLevelrY.getValue(),
            //left_y_axis_unit: this.LYUnit.getValue(),
            //right_y_axis_unit: this.RYUnit.getValue(),
            show_left_y_axis_title: this.isShowTitleLY.isSelected(),
            show_right_y_axis_title: this.isShowTitleRY.isSelected(),
            left_y_axis_reversed: this.reversedLY.isSelected(),
            right_y_axis_reversed: this.reversedRY.isSelected()
        }
    },

    setValue: function(v){
        this.transferFilter.setSelected(v.transfer_filter);
        this.colorSelect.setValue(v.chart_color);
        this.lYAxisStyle.setValue(v.left_y_axis_style);
        this.rYAxisStyle.setValue(v.right_y_axis_style);
        //this.numberLevellY.setValue(v.left_y_axis_number_level);
        //this.numberLevelrY.setValue(v.right_y_axis_number_level);
        //this.LYUnit.setValue(v.left_y_axis_unit);
        //this.RYUnit.setValue(v.right_y_axis_unit);
        this.isShowTitleLY.setSelected(v.show_left_y_axis_title);
        this.isShowTitleRY.setSelected(v.show_right_y_axis_title);
        this.reversedLY.setSelected(v.left_y_axis_reversed);
        this.reversedRY.setSelected(v.right_y_axis_reversed);
    }
});
BI.ChartsSetting.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.charts_setting", BI.ChartsSetting);