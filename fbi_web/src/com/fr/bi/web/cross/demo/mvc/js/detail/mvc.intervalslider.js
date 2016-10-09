/**
 * Created by zcf on 2016/9/26.
 */
IntervalSliderView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(IntervalSliderView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: ""
        })
    },
    _init: function () {
        IntervalSliderView.superclass._init.apply(this, arguments);
    },
    _render: function (vessel) {
        var self = this;
        this.slider = BI.createWidget({
            type: "bi.interval_slider"
        });
        var min = BI.createWidget({
            type: "bi.sign_editor",
            cls: "slider-editor-button",
            watermark: "Enter Min",
            errorText: "",
            height: 28,
            width: 90
        });
        var max = BI.createWidget({
            type: "bi.sign_editor",
            cls: "slider-editor-button",
            watermark: "Enter Max",
            errorText: "",
            height: 28,
            width: 90
        });
        var button = BI.createWidget({
            type: "bi.button",
            text: "populate",
            height: 30,
            width: 30
        });
        button.on(BI.Button.EVENT_CHANGE, function () {
            self.slider.populate(min.getValue(), max.getValue());
        });
        var reset = BI.createWidget({
            type: "bi.button",
            text: "reset",
            height: 30,
            width: 30
        });
        reset.on(BI.Button.EVENT_CHANGE, function () {
             self.slider.reset();
            //self.slider._checkOverlap();
        });
        var editorOne = BI.createWidget({
            type: "bi.sign_editor",
            cls: "slider-editor-button",
            watermark: "Enter Value",
            errorText: "",
            height: 28,
            width: 90
        });
        var editorTwo = BI.createWidget({
            type: "bi.sign_editor",
            cls: "slider-editor-button",
            watermark: "Enter Value",
            errorText: "",
            height: 28,
            width: 90
        });
        var set = BI.createWidget({
            type: "bi.button",
            text: "setValue(Value)",
            height: 30,
            width: 30
        });
        set.on(BI.Button.EVENT_CHANGE, function () {
            self.slider.setValue([editorOne.getValue(),editorTwo.getValue()]);
        });
        var l = BI.createWidget({
            type: "bi.vertical",
            items: [min, max, button, reset, editorOne,editorTwo, set],
            hgap: 10,
            vgap: 10
        });
        BI.createWidget({
            type: "bi.vertical",
            element: vessel,
            items: [l, this.slider],
            hgap: 20,
            vgap: 20
        });
    }
});
IntervalSliderModel = BI.inherit(BI.Model, {});
