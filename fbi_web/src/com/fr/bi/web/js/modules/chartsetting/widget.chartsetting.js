/**
 * created by young
 * 图表属性
 */
BI.ChartSetting = BI.inherit(BI.Widget, {
    _defaultConfig: function(){
        return BI.extend(BI.ChartSetting.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-chart-setting"
        })
    },

    _init: function(){
        BI.ChartSetting.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.wrapper = BI.createWidget({
            type: "bi.absolute",
            element: this.element
        });
        var chartType = o.chartType, settings = o.settings;
        this.populate(chartType, settings);
    },

    populate: function(chartType, settings){
        var self = this;
        this.wrapper.empty();
        if(BI.isNotNull(this.chartSetting)){
            this.chartSetting.destroy();
        }
        switch (chartType){
            case BICst.WIDGET.TABLE:
                this.chartSetting = BI.createWidget({
                    type: "bi.group_table_setting",
                    settings: settings
                });
                this.chartSetting.on(BI.GroupTableSetting.EVENT_CHANGE, function(){
                    self.fireEvent(BI.ChartSetting.EVENT_CHANGE, this.getValue());
                });
                break;
            case BICst.WIDGET.CROSS_TABLE:
                this.chartSetting = BI.createWidget({
                    type: "bi.cross_table_setting",
                    settings: settings
                });
                this.chartSetting.on(BI.CrossTableSetting.EVENT_CHANGE, function(){
                    self.fireEvent(BI.ChartSetting.EVENT_CHANGE, this.getValue());
                });
                break;
            case BICst.WIDGET.COMPLEX_TABLE:
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