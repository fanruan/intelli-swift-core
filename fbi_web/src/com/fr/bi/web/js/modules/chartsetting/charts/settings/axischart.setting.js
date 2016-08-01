/**
 * @class BI.AxisChartsSetting
 * @extends BI.Widget
 * 柱状，堆积柱状，组合图样式
 */
BI.AxisChartsSetting = BI.inherit(BI.Widget, {
    constant: {
        SINGLE_LINE_HEIGHT: BICst.CHART.SINGLE_LINE_HIGHT,
        SIMPLE_H_GAP: BICst.CHART.SIMPLE_H_GAP,
        SIMPLE_L_GAP: BICst.CHART.SIMPLE_L_GAP,
        SIMPLE_H_LGAP: BICst.CHART.SIMPLE_H_LGAP,
        CHECKBOX_WIDTH: BICst.CHART.CHECKBOX_WIDTH,
        EDITOR_WIDTH: BICst.CHART.EDITOR_WIDTH,
        EDITOR_HEIGHT: BICst.CHART.EDITOR_HEIGHT,
        BUTTON_WIDTH: BICst.CHART.BUTTON_WIDTH,
        BUTTON_HEIGHT: BICst.CHART.BUTTON_HEIGHT,
        ICON_WIDTH: BICst.CHART.ICON_WIDTH,
        ICON_HEIGHT: BICst.CHART.ICON_HEIGHT,
        NUMBER_LEVEL_SEGMENT_WIDTH: BICst.CHART.NUMBER_LEVEL_SEGMENT_WIDTH,
        FORMAT_SEGMENT_WIDTH: BICst.CHART.FORMAT_SEGMENT_WIDTH,
        LEGEND_SEGMENT_WIDTH: BICst.CHART.LEGEND_SEGMENT_WIDTH
    },

    _defaultConfig: function(){
        return BI.extend(BI.AxisChartsSetting.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-charts-setting"
        })
    },

    _init: function(){
        BI.AxisChartsSetting.superclass._init.apply(this, arguments);
        var self = this, o = this.options;

        this.colorSelect = BI.createWidget({
            type: "bi.chart_setting_select_color_combo",
            width: 130
        });
        this.colorSelect.populate();

        this.colorSelect.on(BI.ChartSettingSelectColorCombo.EVENT_CHANGE, function(){
            self.fireEvent(BI.AxisChartsSetting.EVENT_CHANGE);
        });

        //风格——1、2
        this.chartStyleGroup = BI.createWidget({
            type: "bi.button_group",
            items: BI.createItems(BICst.AXIS_STYLE_GROUP, {
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
        this.chartStyleGroup.on(BI.ButtonGroup.EVENT_CHANGE, function () {
            self.fireEvent(BI.AxisChartsSetting.EVENT_CHANGE);
        });

        var tableStyle = BI.createWidget({
            type: "bi.horizontal_adapt",
            columnSize: [100],
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
                    text: BI.i18nText("BI-Chart_Color"),
                    cls: "attr-names"
                }, {
                    el: {
                        type: "bi.center_adapt",
                        items: [this.colorSelect]
                    },
                    lgap: this.constant.SIMPLE_H_GAP
                }, {
                    type: "bi.label",
                    text: BI.i18nText("BI-Table_Style"),
                    cls: "attr-names",
                    lgap: this.constant.SIMPLE_H_GAP
                }, {
                    el: {
                        type: "bi.center_adapt",
                        items: [this.chartStyleGroup]
                    },
                    lgap: this.constant.SIMPLE_H_GAP
                }], {
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
            self.fireEvent(BI.AxisChartsSetting.EVENT_CHANGE);
        });

        this.numberLevellY = BI.createWidget({
            type: "bi.segment",
            width: this.constant.NUMBER_LEVEL_SEGMENT_WIDTH,
            height: this.constant.BUTTON_HEIGHT,
            items: BICst.TARGET_STYLE_LEVEL
        });

        this.numberLevellY.on(BI.Segment.EVENT_CHANGE, function(){
            self.fireEvent(BI.AxisChartsSetting.EVENT_CHANGE);
        });

        this.rYAxisStyle = BI.createWidget({
            type: "bi.segment",
            width: this.constant.FORMAT_SEGMENT_WIDTH,
            height: this.constant.BUTTON_HEIGHT,
            items: BICst.TARGET_STYLE_FORMAT
        });

        this.rYAxisStyle.on(BI.Segment.EVENT_CHANGE, function(){
            self.fireEvent(BI.AxisChartsSetting.EVENT_CHANGE);
        });

        this.numberLevelrY = BI.createWidget({
            type: "bi.segment",
            width: this.constant.NUMBER_LEVEL_SEGMENT_WIDTH,
            height: this.constant.BUTTON_HEIGHT,
            items: BICst.TARGET_STYLE_LEVEL
        });

        this.numberLevelrY.on(BI.Segment.EVENT_CHANGE, function(){
            self.fireEvent(BI.AxisChartsSetting.EVENT_CHANGE);
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
            self.fireEvent(BI.AxisChartsSetting.EVENT_CHANGE);
        });

        this.RYUnit = BI.createWidget({
            type: "bi.sign_editor",
            width: this.constant.EDITOR_WIDTH,
            height: this.constant.EDITOR_HEIGHT,
            cls: "unit-input",
            watermark: BI.i18nText("BI-Custom_Input")
        });

        this.RYUnit.on(BI.SignEditor.EVENT_CONFIRM, function(){
            self.fireEvent(BI.AxisChartsSetting.EVENT_CHANGE);
        });

        //显示标题
        this.isShowTitleLY = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Show_Title"),
            width: 90
        });

        this.isShowTitleLY.on(BI.Controller.EVENT_CHANGE, function(){
            this.isSelected() ? self.editTitleLY.setVisible(true) : self.editTitleLY.setVisible(false);
            self.fireEvent(BI.AxisChartsSetting.EVENT_CHANGE);
        });

        this.editTitleLY = BI.createWidget({
            type: "bi.sign_editor",
            width: this.constant.EDITOR_WIDTH,
            height: this.constant.EDITOR_HEIGHT,
            cls: "unit-input"
        });
        this.editTitleLY.on(BI.SignEditor.EVENT_CONFIRM, function(){
            self.fireEvent(BI.AxisChartsSetting.EVENT_CHANGE);
        });

        this.isShowTitleRY = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Show_Title"),
            width: 90
        });

        this.isShowTitleRY.on(BI.Controller.EVENT_CHANGE, function(){
            this.isSelected() ? self.editTitleRY.setVisible(true) : self.editTitleRY.setVisible(false);
            self.fireEvent(BI.AxisChartsSetting.EVENT_CHANGE);
        });

        this.editTitleRY = BI.createWidget({
            type: "bi.sign_editor",
            width: this.constant.EDITOR_WIDTH,
            height: this.constant.EDITOR_HEIGHT,
            cls: "unit-input"
        });

        this.editTitleRY.on(BI.SignEditor.EVENT_CONFIRM, function(){
            self.fireEvent(BI.AxisChartsSetting.EVENT_CHANGE);
        });

        //轴逆序
        this.reversedLY = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Reversed_Axis"),
            width: 80
        });

        this.reversedLY.on(BI.Controller.EVENT_CHANGE, function(){
            self.fireEvent(BI.AxisChartsSetting.EVENT_CHANGE);
        });

        this.reversedRY = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Reversed_Axis"),
            width: 80
        });

        this.reversedRY.on(BI.Controller.EVENT_CHANGE, function(){
            self.fireEvent(BI.AxisChartsSetting.EVENT_CHANGE);
        });

        //横轴文本方向
        this.text_direction = BI.createWidget({
            type: "bi.sign_editor",
            width: this.constant.EDITOR_WIDTH,
            height: this.constant.EDITOR_HEIGHT,
            cls: "unit-input",
            allowBlank: false,
            value: "0",
            errorText: BI.i18nText("BI-Please_Enter_Number_From_To_To", -90, 90),
            validationChecker: function(v){
                return BI.isInteger(v) && v >= -90 && v <= 90;
            }
        });
        this.text_direction.on(BI.SignEditor.EVENT_CONFIRM, function(){
            self.fireEvent(BI.AxisChartsSetting.EVENT_CHANGE);
        });

        this.isShowTitleX = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Show_Title"),
            width: 90
        });

        this.isShowTitleX.on(BI.Controller.EVENT_CHANGE, function(){
            this.isSelected() ? self.editTitleX.setVisible(true) : self.editTitleX.setVisible(false);
            self.fireEvent(BI.AxisChartsSetting.EVENT_CHANGE);
        });

        this.editTitleX = BI.createWidget({
            type: "bi.sign_editor",
            width: this.constant.EDITOR_WIDTH,
            height: this.constant.EDITOR_HEIGHT,
            cls: "unit-input"
        });

        this.editTitleX.on(BI.SignEditor.EVENT_CONFIRM, function(){
            self.fireEvent(BI.AxisChartsSetting.EVENT_CHANGE);
        });

        //图例
        this.legend = BI.createWidget({
            type: "bi.segment",
            width: this.constant.LEGEND_SEGMENT_WIDTH,
            height: this.constant.BUTTON_HEIGHT,
            items: BICst.CHART_LEGEND
        });

        this.legend.on(BI.Segment.EVENT_CHANGE, function(){
            self.fireEvent(BI.AxisChartsSetting.EVENT_CHANGE);
        });

        //数据标签
        this.showDataLabel = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Show_Data_Label"),
            width: 115
        });

        this.showDataLabel.on(BI.Controller.EVENT_CHANGE, function(){
            self.fireEvent(BI.AxisChartsSetting.EVENT_CHANGE);
        });

        //数据表格
        this.showDataTable = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Show_Data_Table"),
            width: 115
        });

        this.showDataTable.on(BI.Controller.EVENT_CHANGE, function(){
            if(this.isSelected()){
                self.showZoom.setSelected(false);
            }
            self.fireEvent(BI.AxisChartsSetting.EVENT_CHANGE);
        });

        //网格线
        this.gridLine = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Show_Grid_Line"),
            width: 115
        });

        this.gridLine.on(BI.Controller.EVENT_CHANGE, function(){
            self.fireEvent(BI.AxisChartsSetting.EVENT_CHANGE);
        });

        //图表缩放滚轮
        this.showZoom = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Show_Zoom"),
            width: 140
        });

        this.showZoom.on(BI.Controller.EVENT_CHANGE, function(){
            if(this.isSelected()){
                self.showDataTable.setSelected(false);
            }
            self.fireEvent(BI.AxisChartsSetting.EVENT_CHANGE);
        });

        var showElement = BI.createWidget({
            type: "bi.horizontal_adapt",
            columnSize: [80],
            cls: "single-line-settings",
            items: [{
                type: "bi.label",
                text: BI.i18nText("BI-Element_Show"),
                lgap: this.constant.SIMPLE_H_LGAP,
                textAlign: "left",
                textHeight: this.constant.SINGLE_LINE_HEIGHT,
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
                    items: [this.showDataTable]
                }, {
                    type: "bi.center_adapt",
                    items: [this.gridLine]
                }, {
                    type: "bi.center_adapt",
                    items: [this.showZoom]
                }], {
                    height: this.constant.SINGLE_LINE_HEIGHT
                }),
                lgap: this.constant.SIMPLE_H_GAP
            }]
        });

        var xAxis = BI.createWidget({
            type: "bi.horizontal_adapt",
            columnSize: [80],
            cls: "single-line-settings",
            items: [{
                type: "bi.label",
                text: BI.i18nText("BI-Category_Axis"),
                lgap: this.constant.SIMPLE_H_LGAP,
                textAlign: "left",
                textHeight: this.constant.SINGLE_LINE_HEIGHT,
                cls: "line-title"
            }, {
                type: "bi.left",
                cls: "detail-style",
                items: BI.createItems([{
                    type: "bi.label",
                    text: BI.i18nText("BI-Text_Direction"),
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
            type: "bi.horizontal_adapt",
            columnSize: [80],
            cls: "single-line-settings",
            verticalAlign: "top",
            items: [{
                type: "bi.label",
                textHeight: this.constant.SINGLE_LINE_HEIGHT,
                text: BI.i18nText("BI-Left_Value_Axis"),
                textAlign: "left",
                lgap: this.constant.SIMPLE_H_LGAP,
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
                }, {
                    type: "bi.center_adapt",
                    items: [this.reversedLY]
                }], {
                    height: this.constant.SINGLE_LINE_HEIGHT
                }),
                lgap: this.constant.SIMPLE_H_GAP
            }]
        });

        var rYAxis = BI.createWidget({
            type: "bi.horizontal_adapt",
            columnSize: [80],
            cls: "single-line-settings",
            verticalAlign: "top",
            items: [{
                type: "bi.label",
                textHeight: this.constant.SINGLE_LINE_HEIGHT,
                lgap: this.constant.SIMPLE_H_LGAP,
                textAlign: "left",
                text: BI.i18nText("BI-Right_Value_Axis"),
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
                }, {
                    type: "bi.center_adapt",
                    items: [this.reversedRY]
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
            items: [tableStyle, lYAxis, rYAxis, xAxis, showElement, otherAttr],
            hgap: 10
        })
    },

    populate: function(){
        var wId = this.options.wId;
        var view = BI.Utils.getWidgetViewByID(wId);
        var titleLY = BI.Utils.getWSLeftYAxisTitleByID(wId);
        var titleX = BI.Utils.getWSXAxisTitleByID(wId);
        var titleRY = BI.Utils.getWSRightYAxisTitleByID(wId);
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
        if(titleRY === ""){
            BI.any(view[BICst.REGION.TARGET2], function(idx, dId){
                if(BI.Utils.isDimensionUsable(dId)){
                    titleRY = BI.Utils.getDimensionNameByID(dId);
                    return true;
                }
                return false;
            });
        }
        this.transferFilter.setSelected(BI.Utils.getWSTransferFilterByID(wId));
        this.colorSelect.setValue(BI.Utils.getWSChartColorByID(wId));
        this.chartStyleGroup.setValue(BI.Utils.getWSChartStyleByID(wId));
        this.lYAxisStyle.setValue(BI.Utils.getWSLeftYAxisStyleByID(wId));
        this.rYAxisStyle.setValue(BI.Utils.getWSRightYAxisStyleByID(wId));
        this.numberLevellY.setValue(BI.Utils.getWSLeftYAxisNumLevelByID(wId));
        this.numberLevelrY.setValue(BI.Utils.getWSRightYAxisNumLevelByID(wId));
        this.LYUnit.setValue(BI.Utils.getWSLeftYAxisUnitByID(wId));
        this.RYUnit.setValue(BI.Utils.getWSRightYAxisUnitByID(wId));
        this.isShowTitleLY.setSelected(BI.Utils.getWSShowLeftYAxisTitleByID(wId));
        this.isShowTitleRY.setSelected(BI.Utils.getWSShowRightYAxisTitleByID(wId));
        this.isShowTitleX.setSelected(BI.Utils.getWSShowXAxisTitleByID(wId));
        this.editTitleLY.setValue(titleLY);
        this.editTitleRY.setValue(titleRY);
        this.editTitleX.setValue(titleX);
        this.reversedLY.setSelected(BI.Utils.getWSLeftYAxisReversedByID(wId));
        this.reversedRY.setSelected(BI.Utils.getWSRightYAxisReversedByID(wId));
        this.text_direction.setValue(BI.Utils.getWSTextDirectionByID(wId));
        this.legend.setValue(BI.Utils.getWSChartLegendByID(wId));
        this.showDataLabel.setSelected(BI.Utils.getWSShowDataLabelByID(wId));
        this.showDataTable.setSelected(BI.Utils.getWSShowDataTableByID(wId));
        this.gridLine.setSelected(BI.Utils.getWSShowGridLineByID(wId));
        this.showZoom.setSelected(BI.Utils.getWSShowZoomByID(wId));

        this.isShowTitleLY.isSelected() ? this.editTitleLY.setVisible(true) : this.editTitleLY.setVisible(false);
        this.isShowTitleRY.isSelected() ? this.editTitleRY.setVisible(true) : this.editTitleRY.setVisible(false);
        this.isShowTitleX.isSelected() ? this.editTitleX.setVisible(true) : this.editTitleX.setVisible(false);
    },

    getValue: function(){
        return {
            transfer_filter: this.transferFilter.isSelected(),
            chart_color: this.colorSelect.getValue()[0],
            chart_style: this.chartStyleGroup.getValue()[0],
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
            left_y_axis_reversed: this.reversedLY.isSelected(),
            right_y_axis_reversed: this.reversedRY.isSelected(),
            text_direction: this.text_direction.getValue(),
            chart_legend: this.legend.getValue()[0],
            show_data_label: this.showDataLabel.isSelected(),
            show_data_table: this.showDataTable.isSelected(),
            show_grid_line: this.gridLine.isSelected(),
            show_zoom: this.showZoom.isSelected()
        }
    },

    setValue: function(v){
        this.transferFilter.setSelected(v.transfer_filter);
        this.colorSelect.setValue(v.chart_color);
        this.chartStyleGroup.setValue(v.chart_style);
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
        this.reversedLY.setSelected(v.left_y_axis_reversed);
        this.reversedRY.setSelected(v.right_y_axis_reversed);
        this.text_direction.setValue(v.text_direction);
        this.legend.setValue(v.chart_legend);
        this.showDataLabel.setSelected(v.show_data_label);
        this.showDataTable.setSelected(v.show_data_table);
        this.gridLine.setSelected(v.show_grid_line);
        this.showZoom.setSelected(v.show_zoom);
    }
});
BI.AxisChartsSetting.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.axis_charts_setting", BI.AxisChartsSetting);