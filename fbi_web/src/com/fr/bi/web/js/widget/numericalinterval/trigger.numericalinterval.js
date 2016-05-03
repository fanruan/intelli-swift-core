/**
 * Created by roy on 15/9/17.
 */
BI.NumericalIntervalTrigger = BI.inherit(BI.Widget, {
    constants: {
        triggerHeight: 28,
        triggerWidth: 26
    },

    _defaultConfig: function () {
        var conf = BI.NumericalIntervalTrigger.superclass._defaultConfig.apply(this, arguments)
        return BI.extend(conf, {
            baseCls: "bi-numerical-interval-trigger",
            items: [],
            height: 30
        })
    },
    _init: function () {
        var o = this.options;
        BI.NumericalIntervalTrigger.superclass._init.apply(this, arguments);
        var triggerCls = "less-arrow-font";
        if (o.value === 1) {
            triggerCls = "less-equal-arrow-font"
        }
        this.trigger = BI.createWidget({
            element: this.element,
            type: "bi.icon_button",
            shadow: true,
            height: o.height,
            width: this.constants.triggerWidth,
            level: "warning",
            tipType: "warning",
            cls: triggerCls
        });
        if (BI.isNotNull(o.value)) {
            this.setValue(o.value);
        }
    },

    setEnable: function (b) {
        this.trigger.setEnable(b);
    },

    hover: function () {
        this.trigger.hover();
    },

    dishover: function () {
        this.trigger.dishover();
    },
    setTitle: function (v) {
        this.trigger.setTitle(v);
    },
    setValue: function (v) {
        var self = this, o = this.options;
        BI.each(o.items, function(){

        });
        if (v === c.less_arrow) {
            this.trigger.element.removeClass("less-equal-arrow-font");
            this.trigger.element.addClass("less-arrow-font");
        } else if (v === c.less_equal_arrow) {
            this.trigger.element.removeClass("less-arrow-font");
            this.trigger.element.addClass("less-equal-arrow-font")
        }

    }
});
$.shortcut("bi.numerical_interval_trigger", BI.NumericalIntervalTrigger);