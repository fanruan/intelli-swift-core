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
            type: "bi.textarea_editor",
            invalid: true
        });

        BI.createWidget({
            type: "bi.absolute",
            element: vessel,
            items: [{
                el: this.textarea,
                left: 10,
                right: 10,
                top: 10,
                bottom: 10
            }]
        });
    },

    refresh: function () {
        this.textarea.setValue(this.model.get("content"));
        this.textarea.setStyle(this.model.get("style"));
    }
})
;