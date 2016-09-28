/**
 * Created by zcf on 2016/9/22.
 */
BI.Slider = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.Slider.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-single-slider-slider"
        });
    },
    _init: function () {
        BI.extend(BI.Slider.superclass._init.apply(this, arguments));
        this.slider = BI.createWidget({
            type: "bi.icon_button",
            cls: "widget-slider-icon",
            element: this.element,
            iconWidth: 30,
            iconHeight: 30,
            height: 30,
            width: 30
        });
    }
});
$.shortcut("bi.single_slider_slider", BI.Slider);