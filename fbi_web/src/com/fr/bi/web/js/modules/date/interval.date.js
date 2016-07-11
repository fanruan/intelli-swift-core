/**
 * @class BI.CustomTimeInterval
 * @extend BI.Widget
 * 日期区间控件——适配后台数据
 */
BI.CustomTimeInterval = BI.inherit(BI.Widget, {
    _defaultConfig: function(){
        return BI.extend(BI.CustomTimeInterval.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-custom-time-interval"
        })
    },

    _init: function(){
        BI.CustomTimeInterval.superclass._init.apply(this, arguments);
        var self = this;
        this.timeInterval = BI.createWidget({
            type: "bi.time_interval",
            element: this.element,
            width: 230
        });
        this.timeInterval.on(BI.TimeInterval.EVENT_CHANGE, function(){
            self.fireEvent(BI.CustomTimeInterval.EVENT_CHANGE);
        });
    },

    getValue: function(){
        var timeRange = this.timeInterval.getValue();
        var start = timeRange.start, end = timeRange.end;
        return {
            startDetail: start,
            endDetail: end
        };
    },

    setValue: function(v){
        var sString = v.startDetail, eString = v.endDetail;
        var value = {};
        BI.isNull(sString) ? value["start"] = null : value["start"] = sString;
        BI.isNull(eString) ? value["end"] = null : value["end"] = eString;
        this.timeInterval.setValue(value);
    }
});
BI.CustomTimeInterval.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.custom_time_interval", BI.CustomTimeInterval);