/**
 * Created by roy on 15/9/17.
 */
BI.NumericalIntervalPopup = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        var conf = BI.NumericalIntervalPopup.superclass._defaultConfig.apply(this, arguments);
        return BI.extend(conf, {
            baseCls: "bi-numerical-interval-popup",
            items: []
        })
    },
    _init: function () {
        var self = this, o = this.options
        BI.NumericalIntervalPopup.superclass._init.apply(this, arguments);
        this.popup = BI.createWidget({
                type: "bi.button_group",
                element: this.element,
                items: BI.createItems(o.items,
                    {
                        type: "bi.text_item",
                        cls: "numerical-interval-popup-item",
                        textHgap: 5,
                        height: 25
                    }
                ),
                layouts: [{
                    type: "bi.vertical"
                }]


        });
        this.popup.on(BI.Controller.EVENT_CHANGE, function (type) {
            if (type === BI.Events.CLICK) {
                self.fireEvent(BI.NumericalIntervalPopup.EVENT_CHANGE);
            }
        });

    },
    getValue: function () {
        return this.popup.getValue();
    },

    setValue: function (v) {
        this.popup.setValue(v);
    }

})
BI.NumericalIntervalPopup.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.numerical-interval-popup", BI.NumericalIntervalPopup);