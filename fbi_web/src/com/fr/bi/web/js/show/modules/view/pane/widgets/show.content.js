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

        this.del = BI.createWidget({
            type: "bi.icon_button",
            width: 32,
            height: 32,
            invisible: true,
            cls: "img-shutdown-font",
            title: BI.i18nText("BI-Delete")
        });

        this.del.on(BI.IconButton.EVENT_CHANGE, function () {
            BI.Msg.confirm("", BI.i18nText("BI-Sure_Delete"), function (v) {
                if (v === true) {
                    self.model.destroy();
                }
            });
        });

        vessel.hover(function () {
            self.del.setVisible(true);
        }, function () {
            self.del.setVisible(false);
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
            }, {
                el: this.del,
                right: 0,
                top: 0
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