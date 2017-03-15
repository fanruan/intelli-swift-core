/**
 * Created by Baron on 15/10/19.
 */
TimeIntervalView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(TimeIntervalView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-time-interval-view"
        })
    },
    _init: function () {
        TimeIntervalView.superclass._init.apply(this, arguments);
    },
    _render: function (vessel) {
        var self = this;
        this.interval = BI.createWidget({
            type: "bi.time_interval"

        });
        var items = [

            {el:self.interval},
            {
                type: "bi.button",
                text: "getValue",
                handler: function () {
                    BI.Msg.alert("", JSON.stringify(self.interval.getValue()));
                }
            }, {
                type: "bi.button",
                text: "setValue",
                handler: function () {
                    var date = new Date(),
                        dateObj = {
                            year: date.getFullYear(),
                            month: date.getMonth(),
                            day: date.getDate()
                        };
                    self.interval.setValue({start: dateObj, end: dateObj});
                }
            }];
        BI.createWidget({
            type: "bi.vertical",
            element: vessel,
            vgap: 20,
            hgap: 20,
            items: items
        })
    }
});

TimeIntervalModel = BI.inherit(BI.Model, {
    _defaultConfig: function () {
        return BI.extend(TimeIntervalModel.superclass._defaultConfig.apply(this, arguments), {})
    },
    _init: function () {
        TimeIntervalModel.superclass._init.apply(this, arguments);
    }
});