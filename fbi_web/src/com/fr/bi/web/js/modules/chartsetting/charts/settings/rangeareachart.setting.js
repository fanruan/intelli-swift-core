/**
 * @class BI.RangeAreaChartsSetting
 * @extends BI.Widget
 * 范围面积样式
 */
BI.RangeAreaChartsSetting = BI.inherit(BI.AbstractChartSetting, {

    _defaultConfig: function(){
        return BI.extend(BI.RangeAreaChartsSetting.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-charts-setting bi-range-area-chart-setting"
        })
    },

    _init: function(){
        BI.RangeAreaChartsSetting.superclass._init.apply(this, arguments);
        var self = this, o = this.options, constant = BI.AbstractChartSetting;

        //组件设置
        this.widgetSetting = BI.createWidget({
            type: "bi.widget_block_setting",
            wId: o.wId
        });

        this.widgetSetting.on(BI.WidgetBlockSetting.EVENT_CHANGE, function() {
            self.fireEvent(BI.RangeAreaChartsSetting.EVENT_CHANGE)
        });

        //图表样式设置
        this.chartStyleSetting = BI.createWidget({
            type: "bi.chart_style_block_setting"
        });

        this.chartStyleSetting.on(BI.ChartStyleBlockSetting.EVENT_CHANGE, function () {
            self.fireEvent(BI.AxisChartsSetting.EVENT_CHANGE)
        });

        //值轴设置
        this.lValueAxisSetting = BI.createWidget({
            type: "bi.value_axis_block_setting",
            headText: BI.i18nText("BI-Left_Value_Axis"),
            reversed: false,
            wId: o.wId
        });

        this.lValueAxisSetting.on(BI.ValueAxisBlockSetting.EVENT_CHANGE, function () {
            self.fireEvent(BI.AxisChartsSetting.EVENT_CHANGE)
        });

        //横轴文本方向
        this.text_direction = BI.createWidget({
            type: "bi.sign_editor",
            width: constant.EDITOR_WIDTH,
            height: constant.EDITOR_HEIGHT,
            cls: "unit-input",
            allowBlank: false,
            value: "0",
            errorText: BI.i18nText("BI-Please_Enter_Number_From_To_To", -90, 90),
            validationChecker: function(v){
                return BI.isInteger(v) && v >= -90 && v <= 90;
            }
        });
        this.text_direction.on(BI.SignEditor.EVENT_CONFIRM, function(){
            self.fireEvent(BI.RangeAreaChartsSetting.EVENT_CHANGE);
        });

        this.isShowTitleX = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Show_Title"),
            width: 90
        });

        this.isShowTitleX.on(BI.Controller.EVENT_CHANGE, function(){
            this.isSelected() ? self.editTitleX.setVisible(true) : self.editTitleX.setVisible(false);
            self.fireEvent(BI.RangeAreaChartsSetting.EVENT_CHANGE);
        });

        this.editTitleX = BI.createWidget({
            type: "bi.sign_editor",
            width: constant.EDITOR_WIDTH,
            height: constant.EDITOR_HEIGHT,
            cls: "unit-input"
        });

        this.editTitleX.on(BI.SignEditor.EVENT_CONFIRM, function(){
            self.fireEvent(BI.RangeAreaChartsSetting.EVENT_CHANGE);
        });

        //数据标签
        this.showDataLabel = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Show_Data_Label"),
            width: 115
        });

        this.showDataLabel.on(BI.Controller.EVENT_CHANGE, function(){
            self.fireEvent(BI.RangeAreaChartsSetting.EVENT_CHANGE);
        });

        //网格线
        this.gridLine = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Show_Grid_Line"),
            width: 115
        });

        this.gridLine.on(BI.Controller.EVENT_CHANGE, function(){
            self.fireEvent(BI.RangeAreaChartsSetting.EVENT_CHANGE);
        });

        this.showElement = BI.createWidget({
            type: "bi.horizontal_adapt",
            columnSize: [80],
            cls: "single-line-settings",
            items: [{
                type: "bi.label",
                text: BI.i18nText("BI-Element_Show"),
                textHeight: constant.SINGLE_LINE_HEIGHT,
                cls: "line-title"
            }, {
                type: "bi.left",
                cls: "detail-style",
                items: BI.createItems([{
                    type: "bi.vertical_adapt",
                    items: [this.gridLine]
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.showDataLabel]
                }], {
                    height: constant.SINGLE_LINE_HEIGHT
                }),
                lgap: constant.SIMPLE_H_LGAP
            }]
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
                    type: "bi.label",
                    text: BI.i18nText("BI-Text_Direction"),
                    cls: "attr-names"
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.text_direction]
                }, {
                    type: "bi.label",
                    text: "。",
                    textHeight: 30,
                    height: constant.SINGLE_LINE_HEIGHT
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.isShowTitleX]
                }, {
                    type: "bi.vertical_adapt",
                    items: [this.editTitleX]
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
        this.transferFilter.on(BI.Controller.EVENT_CHANGE, function(){
            self.fireEvent(BI.RangeAreaChartsSetting.EVENT_CHANGE);
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
            lhgap: 11
        });

        //极简模式
        this.minimalistModel = BI.createWidget({
            type: "bi.multi_select_item",
            value: BI.i18nText("BI-Minimalist_Model"),
            width: 170
        });

        this.minimalistModel.on(BI.Controller.EVENT_CHANGE, function () {
            self._invisible(!this.isSelected());
            self.fireEvent(BI.RangeAreaChartsSetting.EVENT_CHANGE)
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
            items: [this.widgetSetting, this.chartStyleSetting, this.lValueAxisSetting, this.xAxis, this.showElement, this.otherAttr, modelChange],
            hgap: 10
        })
    },

    _invisible: function (v) {
        this.chartStyleSetting.setVisible(v);
        this.lValueAxisSetting.setVisible(v);
        this.xAxis.setVisible(v);
        this.showElement.setVisible(v);
        this.otherAttr.setVisible(v);
    },

    populate: function(){
        var wId = this.options.wId;

        var view = BI.Utils.getWidgetViewByID(wId);
        var titleX = BI.Utils.getWSXAxisTitleByID(wId);
        if(titleX === ""){
            BI.any(view[BICst.REGION.DIMENSION1], function(idx, dId){
                if(BI.Utils.isDimensionUsable(dId)){
                    titleX = BI.Utils.getDimensionNameByID(dId);
                    return true;
                }
                return false;
            });
        }
        this.widgetSetting.populate();
        this.chartStyleSetting.setValue(BI.Utils.getWSChartStyleSettingByID(wId));
        this.lValueAxisSetting.setValue(BI.Utils.getWSLeftValueAxisSettingByID(wId));

        this.transferFilter.setSelected(BI.Utils.getWSTransferFilterByID(wId));
        this.isShowTitleX.setSelected(BI.Utils.getWSShowXAxisTitleByID(wId));
        this.editTitleX.setValue(titleX);
        this.text_direction.setValue(BI.Utils.getWSTextDirectionByID(wId));
        this.showDataLabel.setSelected(BI.Utils.getWSShowDataLabelByID(wId));
        this.gridLine.setSelected(BI.Utils.getWSShowGridLineByID(wId));
        this.minimalistModel.setSelected(BI.Utils.getWSMinimalistByID(wId));
        this._invisible(!BI.Utils.getWSMinimalistByID(wId));
        this.isShowTitleX.isSelected() ? this.editTitleX.setVisible(true) : this.editTitleX.setVisible(false);
    },

    getValue: function(){
        return {
            widget_setting: this.widgetSetting.getValue(),
            chart_style_setting: this.chartStyleSetting.getValue(),
            left_axis_setting: this.lValueAxisSetting.getValue(),

            show_x_axis_title: this.isShowTitleX.isSelected(),
            x_axis_title: this.editTitleX.getValue(),
            text_direction: this.text_direction.getValue(),
            show_data_label: this.showDataLabel.isSelected(),
            show_grid_line: this.gridLine.isSelected(),
            transfer_filter: this.transferFilter.isSelected(),
            minimalist_model: this.minimalistModel.isSelected(),
        }
    },

    setValue: function(v){
        this.widgetSetting.setValue(v.widget_setting);
        this.chartStyleSetting.setValue(v.chart_style_setting);
        this.lValueAxisSetting.setValue(v.left_axis_setting);

        this.isShowTitleX.setSelected(v.x_axis_title);
        this.editTitleX.setValue(v.x_axis_title);
        this.text_direction.setValue(v.text_direction);
        this.showDataLabel.setSelected(v.show_data_label);
        this.gridLine.setSelected(v.show_grid_line);
        this.transferFilter.setSelected(v.transfer_filter);
        this.minimalistModel.setSelected(v.minimalist_model);
    }
});
BI.RangeAreaChartsSetting.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.range_area_chart_setting", BI.RangeAreaChartsSetting);
