/**
 * 指标过滤
 *
 * Created by GUY on 2015/11/20.
 * @class BI.ConfAndOrFilterExpander
 * @extend BI.Widget
 */
BI.ConfAndOrFilterExpander = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.ConfAndOrFilterExpander.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-target-filter",
            el: {},
            popup: {}
        })
    },

    _init: function () {
        BI.ConfAndOrFilterExpander.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.expander = BI.createWidget({
            type: "bi.filter_expander",
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
BI.ConfAndOrFilterExpander.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.conf_andor_filter_expander", BI.ConfAndOrFilterExpander);