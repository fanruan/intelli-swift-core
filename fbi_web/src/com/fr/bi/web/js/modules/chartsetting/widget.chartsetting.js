/**
 * created by young
 * 图表属性
 */
BI.ChartSetting = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.ChartSetting.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-chart-setting"
        })
    },

    _init: function () {
        BI.ChartSetting.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.wrapper = BI.createWidget({
            type: "bi.absolute",
            element: this.element
        });
        var chartType = o.chartType, settings = o.settings;
        this.populate(chartType, settings);
    },

    populate: function (chartType, settings) {
        var self = this;
        this.wrapper.empty();
        if (BI.isNotNull(this.chartSetting)) {
            this.chartSetting.destroy();
        }
        switch (chartType) {
            case BICst.WIDGET.TABLE:
                this.chartSetting = BI.createWidget({
                    type: "bi.group_table_setting",
                    settings: settings
                });
                this.chartSetting.on(BI.GroupTableSetting.EVENT_CHANGE, function () {
                    self.fireEvent(BI.ChartSetting.EVENT_CHANGE, this.getValue());
                });
                break;
            case BICst.WIDGET.CROSS_TABLE:
                this.chartSetting = BI.createWidget({
                    type: "bi.cross_table_setting",
                    settings: settings
                });
                this.chartSetting.on(BI.CrossTableSetting.EVENT_CHANGE, function () {
                    self.fireEvent(BI.ChartSetting.EVENT_CHANGE, this.getValue());
                });
                break;
            case BICst.WIDGET.COMPLEX_TABLE:
                break;
            case BICst.WIDGET.DETAIL:
                this.chartSetting = BI.createWidget({
                    type: "bi.detail_table_setting",
                    settings: settings
                });
                this.chartSetting.on(BI.DetailTable.EVENT_CHANGE, function () {
                    self.fireEvent(BI.ChartSetting.EVENT_CHANGE, this.getValue());
                });
                break;
            case BICst.WIDGET.BUBBLE:
            case BICst.WIDGET.SCATTER:
            case BICst.WIDGET.AXIS:
            case BICst.WIDGET.LINE:
            case BICst.WIDGET.AREA:
            case BICst.WIDGET.ACCUMULATE_AXIS:
            case BICst.WIDGET.ACCUMULATE_AREA:
            case BICst.WIDGET.ACCUMULATE_RADAR:
            case BICst.WIDGET.PERCENT_ACCUMULATE_AXIS:
            case BICst.WIDGET.PERCENT_ACCUMULATE_AREA:
            case BICst.WIDGET.COMPARE_AXIS:
            case BICst.WIDGET.COMPARE_AREA:
            case BICst.WIDGET.FALL_AXIS:
            case BICst.WIDGET.RANGE_AREA:
            case BICst.WIDGET.BAR:
            case BICst.WIDGET.ACCUMULATE_BAR:
            case BICst.WIDGET.COMPARE_BAR:
            case BICst.WIDGET.COMBINE_CHART:
            case BICst.WIDGET.DONUT:
            case BICst.WIDGET.RADAR:
            case BICst.WIDGET.PIE:
            case BICst.WIDGET.DASHBOARD:
            case BICst.WIDGET.MULTI_AXIS_COMBINE_CHART:
            case BICst.WIDGET.FORCE_BUBBLE:
            case BICst.WIDGET.FUNNEL:
            case BICst.WIDGET.MAP:
            case BICst.WIDGET.GIS_MAP:
                this.chartSetting = BI.createWidget({
                    type: "bi.charts_setting",
                    settings: settings
                });
                this.chartSetting.on(BI.ChartsSetting.EVENT_CHANGE, function () {
                    self.fireEvent(BI.ChartSetting.EVENT_CHANGE, this.getValue());
                });
                break;
        }
        this.chartSetting.setValue(settings);
        this.wrapper.addItem({
            el: this.chartSetting,
            top: 0,
            left: 0,
            bottom: 0,
            right: 0
        });
    }
});
BI.ChartSetting.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.chart_setting", BI.ChartSetting);