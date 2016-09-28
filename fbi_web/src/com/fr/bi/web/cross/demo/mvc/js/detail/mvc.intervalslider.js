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
        var button = BI.createWidget({
            type: "bi.button",
            text: "populate",
            height: 30,
            width: 30
        });
        button.on(BI.Button.EVENT_CHANGE, function () {
            self.slider.populate(0, 100)
        });
        var reset = BI.createWidget({
            type: "bi.button",
            text: "reset",
            height: 30,
            width: 30
        });
        reset.on(BI.Button.EVENT_CHANGE, function () {
            self.slider.reset()
        });
        var editor = BI.createWidget({
            type: "bi.sign_editor",
            cls: "slider-editor-button",
            errorText: "",
            height: 28,
            width: 90
        });
        var set = BI.createWidget({
            type: "bi.button",
            text: "setValue",
            height: 30,
            width: 30
        });
        set.on(BI.Button.EVENT_CHANGE, function () {
            // self.slider.setValue(editor.getValue());
        });
        var l = BI.createWidget({
            type: "bi.vertical",
            items: [button, reset, editor, set],
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
