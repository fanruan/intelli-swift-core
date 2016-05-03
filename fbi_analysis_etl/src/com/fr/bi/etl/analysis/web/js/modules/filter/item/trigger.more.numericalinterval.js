/**
 * Created by roy on 15/9/17.
 */
BI.NumericalMoreIntervalTrigger = BI.inherit(BI.Widget, {
    constants: {
        triggerHeight: 28,
        triggerWidth: 26,
        more_arrow: 0,
        more_equal_arrow: 1
    },

    _defaultConfig: function () {
        var conf = BI.NumericalMoreIntervalTrigger.superclass._defaultConfig.apply(this, arguments)
        return BI.extend(conf, {
            baseCls: "bi-numerical-interval-trigger",
            height:30
        })
    },
    _init: function () {
        var o = this.options, triggerCls = "more-arrow-font";
        BI.NumericalMoreIntervalTrigger.superclass._init.apply(this, arguments)
        if (o.value === 1   ) {
            triggerCls = "more-equal-arrow-font"
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


    },

    setEnable:function(b){
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
        var c = this.constants;
        if (v === c.more_arrow) {
            this.trigger.element.removeClass("more-equal-arrow-font");
            this.trigger.element.addClass("more-arrow-font");
        } else if (v === c.more_equal_arrow) {
            this.trigger.element.removeClass("more-arrow-font");
            this.trigger.element.addClass("more-equal-arrow-font")
        }

    }


})
$.shortcut("bi.numerical_more_interval_trigger", BI.NumericalMoreIntervalTrigger)