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
        return BI.extend(BI.DashboardChartSetting.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-charts-setting"
        })
    },

    _init: function(){
        BI.DashboardChartSetting.superclass._init.apply(this, arguments);
        var self = this, o = this.options;

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
            items: [otherAttr],
            hgap: 10
        })
    },

    populate: function(){
        var wId = this.options.wId;
        this.transferFilter.setSelected(BI.Utils.getWSTransferFilterByID(wId));
    },

    getValue: function(){
        return {
            transfer_filter: this.transferFilter.isSelected()
        }
    },

    setValue: function(v){
        this.transferFilter.setSelected(v.transfer_filter);
    }
});
BI.DashboardChartSetting.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.dashboard_chart_setting", BI.DashboardChartSetting);