/**
 * cube日志Expander
 *
 * Created by GUY on 2016/3/31.
 * @class BI.CubeLogExpander
 * @extends BI.Widget
 */
BI.CubeLogExpander = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.CubeLogExpander.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-cube-log-expander",
            el: {}
        });
    },

    _init: function () {
        BI.CubeLogExpander.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.trigger = BI.createWidget(BI.extend({
            type: "bi.cube_log_node"
        }, o.el));
        this.expander = BI.createWidget({
            type: "bi.expander",
            element: this.element,
            el: this.trigger,
            popup: o.popup
        });
        this.expander.on(BI.Controller.EVENT_CHANGE, function (type) {
            self.fireEvent(BI.Controller.EVENT_CHANGE, arguments);
        });
        this.expander.on(BI.Expander.EVENT_CHANGE, function () {
            self.trigger.setValue(this.getValue());
        });
    },

    populate: function (items, keyword, context, options) {
        this.expander.populate(items);
        this.trigger.populate(context.el);
    }
});
$.shortcut("bi.cube_log_expander", BI.CubeLogExpander);