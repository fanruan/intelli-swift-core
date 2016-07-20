/**
 * Created by GameJian on 2016/7/19.
 */
BI.PopupCustomScale = BI.inherit(BI.Widget , {
    _defaultConfig: function() {
        return BI.extend(BI.PopupCustomScale.superclass._defaultConfig.apply(this , arguments) , {
            baseCls: "bi-popup-custom-scale",
            height: 300,
            width: 600
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

        this.pane.on(BI.CalculateTargetFormulaPane.EVENT_CHANGE , function() {
            self.fireEvent(BI.PopupCustomScale.EVENT_CHANGE)
        });

        BI.createWidget({
            type: "bi.absolute",
            cls: "",
            element: this.element,
            height: o.height,
            width: o.width,
            items: [{
                el: this.pane,
                left: 7,
                right: 7,
                top: 10,
                bottom: 10
            } , {
                el: svg,
                left: 85,
                top: -11
            }]
        })

    },

    getValue: function() {
        return this.pane.getValue()
    },

    setValue: function(v) {
        this.pane.setValue(v)
    },

    populate: function(mode) {
        this.pane.populate(mode)
    }
});
BI.PopupCustomScale.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.popup_custom_scale" , BI.PopupCustomScale);