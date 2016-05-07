/**
 * Created by GameJian on 2016/3/4.
 */
BIDezi.ContentWidgetView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(BIDezi.ContentWidgetView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-dashboard-widget"
        })
    },

    _init: function () {
        BIDezi.ContentWidgetView.superclass._init.apply(this, arguments);
    },

    change: function (changed) {

    },

    _render: function (vessel) {
        var self = this;
        this.textarea = BI.createWidget({
            type: "bi.text_area_editor",
            wId: this.model.get("id"),
            element: vessel,
            height: '100%'
        });

        this.textarea.on(BI.TextAreaEditor.EVENT_DESTROY, function () {
            self.model.destroy()
        });

        this.textarea.on(BI.TextAreaEditor.EVENT_VALUE_CHANGE, function () {
            self.model.set(self.textarea.getValue())
        });
    },

    refresh: function () {
        this.textarea.setValue({
            style: this.model.get("style"),
            content: this.model.get("content")
        });
    }
});