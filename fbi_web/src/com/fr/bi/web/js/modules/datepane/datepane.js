/**
 * Created by zcf on 12/22/2016.
 */
BI.CustomMultiDatePane = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.CustomMultiDatePane.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-custom-multi-date-pane"
        })
    },

    _init: function () {
        BI.CustomMultiDatePane.superclass._init.apply(this, arguments);

        var self = this;
        this.datapane = BI.createWidget({
            type: "bi.date_pane_widget",
            cls: "date-calendar-popup"
        });
        this.datapane.on(BI.DateCalendarPopup.EVENT_CHANGE, function () {
            self.fireEvent(BI.CustomMultiDatePane.EVENT_CHANGE);
        });

        var wrapper = BI.createWidget({
            type: "bi.absolute",
            height: 200,
            items: [{
                el: this.datapane,
                left: 0,
                right: 0
            }]
        });
        BI.createWidget({
            type: "bi.adaptive",
            element: this.element,
            scrolly: true,
            items: [{
                type: "bi.adaptive",
                items: [wrapper]
            }]
        })
    },

    getValue: function () {
        return this.datapane.getValue();
    },

    setValue: function (v) {
        if (BI.isNotEmptyObject(v)) {
            this.datapane.setValue(v);
        }
    }
});
BI.CustomMultiDatePane.EVENT_CHANGE = "BI.CustomMultiDatePane.EVENT_CHANGE";
$.shortcut("bi.custom_multi_date_pane", BI.CustomMultiDatePane);