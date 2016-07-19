/**
 *
 * Created by AstronautOO7 on 2016/7/19.
 */
BI.SvgCustomScale = BI.inherit(BI.Widget , {
    _defaultConfig: function() {
        return BI.extend(BI.SvgCustomScale.superclass._defaultConfig.apply(this , arguments) , {
            baseCls: "bi-svg-custom-scale"
        })
    },

    _init: function() {
        BI.SvgCustomScale.superclass._init.apply(this , arguments);
        var self = this, o = this.options;

        var svg = BI.createWidget({
            type: "bi.svg",
            width: o.width,
            height: o.height
        });

        svg.path("M90,10 L100,0 L110,10 L600,10 L600,310 L0,310 L0,10 L90,10").attr({
            "stroke" : "#9e9e9e"
        });

        BI.createWidget({
            type: "bi.absolute",
            element: this.element,
            items: [{
                el: svg
            }]
        })
    }
});
$.shortcut("bi.svg-custom-scale" , BI.SvgCustomScale);