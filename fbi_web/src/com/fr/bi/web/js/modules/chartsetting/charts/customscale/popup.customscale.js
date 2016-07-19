/**
 * Created by GameJian on 2016/7/19.
 */
BI.PopupCustomScale = BI.inherit(BI.Widget , {
    _defaultConfig: function() {
        return BI.extend(BI.PopupCustomScale.superclass._defaultConfig.apply(this , arguments) , {
            baseCls: "bi-popup-custom-scale"
        })
    },

    _init: function() {
        BI.PopupCustomScale.superclass._init.apply(this , arguments);
        var self = this, o = this.options;

        var svg = BI.createWidget({
            type: "bi.svg",
            width: 20,
            height: 10
        });

        svg.path("M0,10 L10,0 L20,10").attr({
            "stroke" : "#b6b9ba"
        });

        this.pane = BI.createWidget({
            type: "bi.calculate_target_formula_pane"
        });

        BI.createWidget({
            type: "bi.absolute",
            cls: "",
            element: this.element,
            width: 600,
            height: 300,
            items: [{
                el: this.pane,
                left: 7,
                right: 7,
                top: 10,
                bottom: 10
            } , {
                el: svg,
                left: 100,
                top: -11
            }]
        })

    },

    getValue: function() {
        this.pane.getValue()
    },

    setValue: function(v) {
        this.pane.setValue(v)
    }
});
BI.PopupCustomScale.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.popup-custom-scale" , BI.PopupCustomScale);