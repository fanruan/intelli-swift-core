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
        var result = {};
        BI.isNull(start) ? result["start"] = null : result["start"] = Date.parseDateTime(start.year + "-" + (start.month + 1) + "-" + start.day, "%y-%x-%d").getTime();
        BI.isNull(end) ? result["end"] = null : result["end"] = Date.parseDateTime(end.year + "-" + (end.month + 1) + "-" + end.day, "%y-%x-%d").getTime();
        return result;
    },

    setValue: function(v){
        var sString = v.start, eString = v.end;
        var value = {};
        BI.isNull(sString) ? value["start"] = null : value["start"] = {
            year: new Date(sString).getFullYear(),
            month: new Date(sString).getMonth(),
            day: new Date(sString).getDate()
        };
        BI.isNull(eString) ? value["end"] = null : value["end"] = {
            year: new Date(eString).getFullYear(),
            month: new Date(eString).getMonth(),
            day: new Date(eString).getDate()
        };
        this.timeInterval.setValue(value);
    }
});
BI.CustomTimeInterval.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.custom_time_interval", BI.CustomTimeInterval);