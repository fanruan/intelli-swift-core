/**
 * Created by roy on 15/9/18.
 */
NumericalIntervalView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(NumericalIntervalView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-numerical-interval-view"
        })
    },
    _init: function () {
        NumericalIntervalView.superclass._init.apply(this, arguments);
    },
    _render: function (vessel) {
        var self = this
        this.interval = BI.createWidget({
            type: "bi.numerical_interval",
            width: 260,
            min: 2,
            max: 11,
            closemin: true,
            closemax: false

        })
        var items = [
            {
                el: self.interval
            }, {
                type: "bi.button",
                text: "getValue",
                handler: function () {
                    BI.Msg.alert("", JSON.stringify(self.interval.getValue()));
                }
            },
            {
                type: "bi.button",
                text: "setValue",
                handler: function () {
                    self.interval.setValue({min: 1, max: 10, closemin: false, closemax: true});
                }
            },
            {
                type: "bi.button",
                text: "setEnableFalse",
                handler: function () {
                    //self.interval.setMinEnable(false);
                    //self.interval.setCloseMinEnable(false);
                    self.interval.setEnable(false);
                }
            },
            {
                type: "bi.button",
                text: "setEnableTrue",
                handler: function () {
                    //self.interval.setMinEnable(false);
                    //self.interval.setCloseMinEnable(false);
                    self.interval.setEnable(true);
                }
            }
        ]


        BI.createWidget({
            type: "bi.left",
            element: vessel,
            vgap: 20,
            hgap: 20,
            items: items
        })


    }

})

NumericalIntervalModel = BI.inherit(BI.Model, {
    _defaultConfig: function () {
        return BI.extend(NumericalIntervalModel.superclass._defaultConfig.apply(this, arguments), {})
    },
    _init: function () {
        NumericalIntervalModel.superclass._init.apply(this, arguments);
    }
})