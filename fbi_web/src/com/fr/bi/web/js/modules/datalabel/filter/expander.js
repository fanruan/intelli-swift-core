/**
 * Created by fay on 2016/8/15.
 */

BI.AndOrFilterExpander = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.AndOrFilterExpander.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-target-filter",
            el: {},
            popup: {}
        })
    },

    _init: function () {
        BI.AndOrFilterExpander.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.expander = BI.createWidget({
            type: "bi.data_label_filter_expander",
            element: this.element,
            el: o.el,
            popup: o.popup,
            id: o.id,
            value: o.value
        });
        this.expander.on(BI.Controller.EVENT_CHANGE, function () {
            self.fireEvent(BI.Controller.EVENT_CHANGE, arguments);
        });
    },

    populate: function () {
        this.expander.populate.apply(this.expander, arguments);
    },

    getValue: function () {
        var val = this.expander.getValue();
        return {
            filter_type: val.type,
            filter_value: val.value,
            id: val.id
        };
    }
});
BI.AndOrFilterExpander.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.data_label_andor_filter_expander", BI.AndOrFilterExpander);