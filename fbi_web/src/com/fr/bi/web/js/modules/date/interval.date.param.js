/**
 * @class BI.CustomParamTimeInterval
 * @extend BI.Widget
 * 带参数的日期区间——适配后台数据
 */
BI.CustomParamTimeInterval = BI.inherit(BI.Widget, {
    _defaultConfig: function(){
        return BI.extend(BI.CustomParamTimeInterval.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-custom-param-time-interval"
        })
    },

    _init: function(){
        BI.CustomParamTimeInterval.superclass._init.apply(this, arguments);
        var self = this;
        this.timeInterval = BI.createWidget({
            type: "bi.param_time_interval",
            element: this.element,
            width: 230
        });
        this.timeInterval.on(BI.TimeInterval.EVENT_CHANGE, function(){
            self.fireEvent(BI.CustomParamTimeInterval.EVENT_CHANGE);
        });
    },

    getValue: function(){
        return this.timeInterval.getValue();
    },

    setValue: function(v){
        this.timeInterval.setValue(v);
    }
});
BI.CustomParamTimeInterval.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.custom_param_time_interval", BI.CustomParamTimeInterval);