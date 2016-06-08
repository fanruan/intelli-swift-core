/**
 * Created by GameJian on 2016/3/4.
 */
BIShow.ContentWidgetView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(BIShow.ContentWidgetView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-dashboard-widget"
        })
    },

    _init: function () {
        BIShow.ContentWidgetView.superclass._init.apply(this, arguments);
    },

    change: function (changed) {

    },

    _render: function (vessel) {
        var self = this;
        this.textarea = BI.createWidget({
            type: "bi.text_area",
            wId: this.model.get("id")
        });

        this.textarea.on(BI.TextArea.EVENT_VALUE_CHANGE, function () {
            self.model.set(self.textarea.getValue())
        });

        BI.createWidget({
            type: "bi.absolute",
            element: vessel,
            items: [{
                el: this.textarea,
                left: 0,
                right: 0,
                top: 0,
                bottom: 0
            }]
        });
    },

    refresh: function () {
        this.textarea.setValue({
            style: this.model.get("style"),
            content: this.model.get("content")
        });
    }
})
;