/**
 * @class BI.CompareChartsSetting
 * @extends BI.Widget
 * 对比柱状样式
 */
BI.CompareChartsSetting = BI.inherit(BI.Widget, {

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
        FORMAT_SEGMENT_WIDTH: 240,
        LEGEND_SEGMENT_WIDTH: 180
    },

    _defaultConfig: function(){
        return BI.extend(BI.CompareChartsSetting.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-charts-setting"
        })
    },

    _init: function(){
        BI.CompareChartsSetting.superclass._init.apply(this, arguments);
        var self = this, o = this.options;

        this.colorSelect = BI.createWidget({
            type: "bi.chart_setting_select_color_combo",
            width: 130
        });
        this.colorSelect.populate(BICst.CHART_COLORS);

        this.colorSelect.on(BI.ChartSettingSelectColorCombo.EVENT_CHANGE, function(){
            self.fireEvent(BI.CompareChartsSetting.EVENT_CHANGE);
        });

        var tableStyle = BI.createWidget({
            type: "bi.left_right_vertical_adapt",
            cls: "single-line-settings",
            items: {
                left: [{
                    type: "bi.label",
                    text: BI.i18nText("BI-Chart_Color"),
                    cls: "line-title"
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
            self.fireEvent(BI.CompareChartsSetting.EVENT_CHANGE);
        });

        this.numberLevellY = BI.createWidget({
            type: "bi.segment",
            width: this.constant.NUMBER_LEVEL_SEGMENT_WIDTH,
            height: this.constant.BUTTON_HEIGHT,
            items: BICst.TARGET_STYLE_LEVEL
        });

        this.numberLevellY.on(BI.Segment.EVENT_CHANGE, function(){
            self.fireEvent(BI.CompareChartsSetting.EVENT_CHANGE);
        });

        this.rYAxisStyle = BI.createWidget({
            type: "bi.segment",
            width: this.constant.FORMAT_SEGMENT_WIDTH,
            height: this.constant.BUTTON_HEIGHT,
            items: BICst.TARGET_STYLE_FORMAT
        });

        this.rYAxisStyle.on(BI.Segment.EVENT_CHANGE, function(){
            self.fireEvent(BI.CompareChartsSetting.EVENT_CHANGE);
        });

        this.numberLevelrY = BI.createWidget({
            type: "bi.segment",
            width: this.constant.NUMBER_LEVEL_SEGMENT_WIDTH,
            height: this.constant.BUTTON_HEIGHT,
            items: BICst.TARGET_STYLE_LEVEL
        });

        this.numberLevelrY.on(BI.Segment.EVENT_CHANGE, function(){
            self.fireEvent(BI.CompareChartsSetting.EVENT_CHANGE);
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
            self.fireEvent(BI.CompareChartsSetting.EVENT_CHANGE);
        });

        this.RYUnit = BI.createWidget({
            type: "bi.sign_editor",
            width: this.constant.EDITOR_WIDTH,
            height: this.constant.EDITOR_HEIGHT,
            cls: "unit-input",
            watermark: BI.i18nText("BI-Custom_Input")
        });

        this.RYUnit.on(BI.SignEditor.EVENT_CONFIRM, function(){
            self.fireEvent(BI.CompareChartsSetting.EVENT_CHANGE);
        });

        //显示标题
        this.isShowTitleLY = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Show_Title"),
            width: 90
        });

        this.isShowTitleLY.on(BI.Controller.EVENT_CHANGE, function(){
            this.isSelected() ? self.editTitleLY.setVisible(true) : self.editTitleLY.setVisible(false);
            self.fireEvent(BI.CompareChartsSetting.EVENT_CHANGE);
        });

        this.editTitleLY = BI.createWidget({
            type: "bi.sign_editor",
            width: this.constant.EDITOR_WIDTH,
            height: this.constant.EDITOR_HEIGHT,
            cls: "unit-input"
        });
        this.editTitleLY.on(BI.SignEditor.EVENT_CONFIRM, function(){
            self.fireEvent(BI.CompareChartsSetting.EVENT_CHANGE);
        });

        this.isShowTitleRY = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Show_Title"),
            width: 90
        });

        this.isShowTitleRY.on(BI.Controller.EVENT_CHANGE, function(){
            this.isSelected() ? self.editTitleRY.setVisible(true) : self.editTitleRY.setVisible(false);
            self.fireEvent(BI.CompareChartsSetting.EVENT_CHANGE);
        });

        this.editTitleRY = BI.createWidget({
            type: "bi.sign_editor",
            width: this.constant.EDITOR_WIDTH,
            height: this.constant.EDITOR_HEIGHT,
            cls: "unit-input"
        });

        this.editTitleRY.on(BI.SignEditor.EVENT_CONFIRM, function(){
            self.fireEvent(BI.CompareChartsSetting.EVENT_CHANGE);
        });

        //轴逆序
        this.reversedLY = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Reversed_Axis"),
            width: 80
        });

        this.reversedLY.on(BI.Controller.EVENT_CHANGE, function(){
            self.fireEvent(BI.CompareChartsSetting.EVENT_CHANGE);
        });

        this.reversedRY = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Reversed_Axis"),
            width: 80
        });

        this.reversedRY.on(BI.Controller.EVENT_CHANGE, function(){
            self.fireEvent(BI.CompareChartsSetting.EVENT_CHANGE);
        });

        //横轴文本方向
        this.text_direction = BI.createWidget({
            type: "bi.sign_editor",
            width: this.constant.EDITOR_WIDTH,
            height: this.constant.EDITOR_HEIGHT,
            cls: "unit-input",
            value: "0",
            validationChecker: function(v){
                return BI.isInteger(v) && v >= -90 && v <= 90;
            }
        });
        this.text_direction.on(BI.SignEditor.EVENT_CONFIRM, function(){
            self.fireEvent(BI.CompareChartsSetting.EVENT_CHANGE);
        });

        this.isShowTitleX = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Show_Title"),
            width: 90
        });

        this.isShowTitleX.on(BI.Controller.EVENT_CHANGE, function(){
            this.isSelected() ? self.editTitleX.setVisible(true) : self.editTitleX.setVisible(false);
            self.fireEvent(BI.CompareChartsSetting.EVENT_CHANGE);
        });

        this.editTitleX = BI.createWidget({
            type: "bi.sign_editor",
            width: this.constant.EDITOR_WIDTH,
            height: this.constant.EDITOR_HEIGHT,
            cls: "unit-input"
        });

        this.editTitleX.on(BI.SignEditor.EVENT_CONFIRM, function(){
            self.fireEvent(BI.CompareChartsSetting.EVENT_CHANGE);
        });

        //图例
        this.legend = BI.createWidget({
            type: "bi.segment",
            width: this.constant.LEGEND_SEGMENT_WIDTH,
            height: this.constant.BUTTON_HEIGHT,
            items: BICst.CHART_LEGEND
        });

        this.legend.on(BI.Segment.EVENT_CHANGE, function(){
            self.fireEvent(BI.ChartsSetting.EVENT_CHANGE);
        });

        //数据标签
        this.showDataLabel = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Show_Data_Label"),
            width: 115
        });

        this.showDataLabel.on(BI.Controller.EVENT_CHANGE, function(){
            self.fireEvent(BI.ChartsSetting.EVENT_CHANGE);
        });

        //数据表格
        this.showDataTable = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Show_Data_Table"),
            width: 115
        });

        this.showDataTable.on(BI.Controller.EVENT_CHANGE, function(){
            self.fireEvent(BI.ChartsSetting.EVENT_CHANGE);
        });

        //网格线
        this.gridLine = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Show_Grid_Line"),
            width: 115
        });

        this.gridLine.on(BI.Controller.EVENT_CHANGE, function(){
            self.fireEvent(BI.ChartsSetting.EVENT_CHANGE);
        });

        var showElement = BI.createWidget({
            type: "bi.horizontal",
            cls: "single-line-settings",
            lgap: this.constant.SIMPLE_H_GAP,
            items: [{
                type: "bi.label",
                text: BI.i18nText("BI-Element_Show"),
                textHeight: 60,
                cls: "line-title"
            }, {
                type: "bi.left",
                cls: "detail-style",
                items: BI.createItems([{
                    type: "bi.label",
                    text: BI.i18nText("BI-Legend_Normal"),
                    lgap: this.constant.SIMPLE_H_GAP,
                    cls: "attr-names"
                }, {
                    type: "bi.center_adapt",
                    items: [this.legend]
                }, {
                    type: "bi.center_adapt",
                    items: [this.showDataLabel]
                }, {
                    type: "bi.center_adapt",
                    items: [this.showDataTable]
                }, {
                    type: "bi.center_adapt",
                    items: [this.gridLine]
                }], {
                    height: this.constant.SINGLE_LINE_HEIGHT
                }),
                lgap: this.constant.SIMPLE_H_GAP
            }]
        });

        var xAxis = BI.createWidget({
            type: "bi.horizontal",
            cls: "single-line-settings",
            lgap: this.constant.SIMPLE_H_GAP,
            items: [{
                type: "bi.label",
                text: BI.i18nText("BI-Category_Axis"),
                cls: "line-title"
            }, {
                type: "bi.left",
                cls: "detail-style",
                items: BI.createItems([{
                    type: "bi.label",
                    text: BI.i18nText("BI-Text_Direction"),
                    lgap: this.constant.SIMPLE_H_GAP,
                    cls: "attr-names"
                }, {
                    type: "bi.center_adapt",
                    items: [this.text_direction]
                }, {
                    type: "bi.label",
                    text: "。",
                    textHeight: 30,
                    height: this.constant.SINGLE_LINE_HEIGHT
                }, {
                    type: "bi.center_adapt",
                    items: [this.isShowTitleX]
                }, {
                    type: "bi.center_adapt",
                    items: [this.editTitleX]
                }], {
                    height: this.constant.SINGLE_LINE_HEIGHT
                }),
                lgap: this.constant.SIMPLE_H_GAP
            }]
        });

        var lYAxis = BI.createWidget({
            type: "bi.horizontal",
            cls: "single-line-settings",
            lgap: this.constant.SIMPLE_H_GAP,
            items: [{
                type: "bi.label",
                height: "100%",
                textHeight: 60,
                text: BI.i18nText("BI-Positive_Value_Axis"),
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
                }], {
                    height: this.constant.SINGLE_LINE_HEIGHT
                }),
                lgap: this.constant.SIMPLE_H_GAP
            }]
        });

        var rYAxis = BI.createWidget({
            type: "bi.horizontal",
            cls: "single-line-settings",
            lgap: this.constant.SIMPLE_H_GAP,
            items: [{
                type: "bi.label",
                height: "100%",
                textHeight: 60,
                text: BI.i18nText("BI-Negative_Value_Axis"),
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
                    items: [this.rYAxisStyle]
                }, {
                    type: "bi.label",
                    text: BI.i18nText("BI-Num_Level"),
                    lgap: this.constant.SIMPLE_H_GAP,
                    cls: "attr-names"
                }, {
                    type: "bi.center_adapt",
                    items: [this.numberLevelrY]
                }, {
                    type: "bi.label",
                    text: BI.i18nText("BI-Unit_Normal"),
                    lgap: this.constant.SIMPLE_H_GAP,
                    cls: "attr-names"
                }, {
                    type: "bi.center_adapt",
                    items: [this.RYUnit]
                }, {
                    type: "bi.center_adapt",
                    items: [this.isShowTitleRY, this.editTitleRY]
                }], {
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

        BI.createWidget({
            type: "bi.vertical",
            element: this.element,
            items: [tableStyle, lYAxis, rYAxis, xAxis, otherAttr],
            hgap: 10
        })
    },

    populate: function(){
        var wId = this.options.wId;
        this.transferFilter.setSelected(BI.Utils.getWSTransferFilterByID(wId));
        this.colorSelect.setValue(BI.Utils.getWSChartColorByID(wId));
        this.lYAxisStyle.setValue(BI.Utils.getWSLeftYAxisStyleByID(wId));
        this.rYAxisStyle.setValue(BI.Utils.getWSRightYAxisStyleByID(wId));
        this.numberLevellY.setValue(BI.Utils.getWSLeftYAxisNumLevelByID(wId));
        this.numberLevelrY.setValue(BI.Utils.getWSRightYAxisNumLevelByID(wId));
        this.LYUnit.setValue(BI.Utils.getWSLeftYAxisUnitByID(wId));
        this.RYUnit.setValue(BI.Utils.getWSRightYAxisUnitByID(wId));
        this.isShowTitleLY.setSelected(BI.Utils.getWSShowLeftYAxisTitleByID(wId));
        this.isShowTitleRY.setSelected(BI.Utils.getWSShowRightYAxisTitleByID(wId));
        this.isShowTitleX.setSelected(BI.Utils.getWSShowXAxisTitleByID(wId));
        this.editTitleLY.setValue(BI.Utils.getWSLeftYAxisTitleByID(wId));
        this.editTitleRY.setValue(BI.Utils.getWSRightYAxisTitleByID(wId));
        this.editTitleX.setValue(BI.Utils.getWSXAxisTitleByID(wId));
        this.text_direction.setValue(BI.Utils.getWSTextDirectionByID(wId));
        this.legend.setValue(BI.Utils.getWSChartLegendByID(wId));
        this.showDataLabel.setSelected(BI.Utils.getWSShowDataLabelByID(wId));
        this.showDataTable.setSelected(BI.Utils.getWSShowDataTableByID(wId));
        this.gridLine.setSelected(BI.Utils.getWSShowGridLineByID(wId));

        this.isShowTitleLY.isSelected() ? this.editTitleLY.setVisible(true) : this.editTitleLY.setVisible(false);
        this.isShowTitleRY.isSelected() ? this.editTitleRY.setVisible(true) : this.editTitleRY.setVisible(false);
        this.isShowTitleX.isSelected() ? this.editTitleX.setVisible(true) : this.editTitleX.setVisible(false);
    },

    getValue: function(){
        return {
            transfer_filter: this.transferFilter.isSelected(),
            chart_color: this.colorSelect.getValue()[0],
            left_y_axis_style: this.lYAxisStyle.getValue()[0],
            right_y_axis_style: this.rYAxisStyle.getValue()[0],
            left_y_axis_number_level: this.numberLevellY.getValue()[0],
            right_y_axis_number_level: this.numberLevelrY.getValue()[0],
            left_y_axis_unit: this.LYUnit.getValue(),
            right_y_axis_unit: this.RYUnit.getValue(),
            show_left_y_axis_title: this.isShowTitleLY.isSelected(),
            show_right_y_axis_title: this.isShowTitleRY.isSelected(),
            show_x_axis_title: this.isShowTitleX.isSelected(),
            left_y_axis_title: this.editTitleLY.getValue(),
            right_y_axis_title: this.editTitleRY.getValue(),
            x_axis_title: this.editTitleX.getValue(),
            text_direction: this.text_direction.getValue(),
            chart_legend: this.legend.getValue()[0],
            show_data_label: this.showDataLabel.isSelected(),
            show_data_table: this.showDataTable.isSelected(),
            show_grid_line: this.gridLine.isSelected()
        }
    },

    setValue: function(v){
        this.transferFilter.setSelected(v.transfer_filter);
        this.colorSelect.setValue(v.chart_color);
        this.lYAxisStyle.setValue(v.left_y_axis_style);
        this.rYAxisStyle.setValue(v.right_y_axis_style);
        this.numberLevellY.setValue(v.left_y_axis_number_level);
        this.numberLevelrY.setValue(v.right_y_axis_number_level);
        this.LYUnit.setValue(v.left_y_axis_unit);
        this.RYUnit.setValue(v.right_y_axis_unit);
        this.isShowTitleLY.setSelected(v.show_left_y_axis_title);
        this.isShowTitleRY.setSelected(v.show_right_y_axis_title);
        this.isShowTitleX.setSelected(v.x_axis_title);
        this.editTitleLY.setValue(v.left_y_axis_title);
        this.editTitleRY.setValue(v.right_y_axis_title);
        this.editTitleX.setValue(v.x_axis_title);
        this.text_direction.setValue(v.text_direction);
        this.legend.setValue(v.chart_legend);
        this.showDataLabel.setSelected(v.show_data_label);
        this.showDataTable.setSelected(v.show_data_table);
        this.gridLine.setSelected(v.show_grid_line);
    }
});
BI.CompareChartsSetting.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.compare_chart_setting", BI.CompareChartsSetting);