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
        var self = this;
        this._broadcasts = [];
        this._broadcasts.push(BI.Broadcasts.on(BICst.BROADCAST.WIDGET_SELECTED_PREFIX, function () {
            if (!self.element.parent().parent().hasClass("selected")) {
                self.del.setVisible(false);
            }
        }));
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
            type: "bi.bubble_combo",
            el: {
                type: "bi.icon_button",
                width: 24,
                height: 24,
                cls: "img-shutdown-font delete-button",
                title: BI.i18nText("BI-Basic_Delete")
            },
            popup: {
                type: "bi.bubble_bar_popup_view",
                buttons: [{
                    value: BI.i18nText(BI.i18nText("BI-Basic_Sure")),
                    handler: function () {
                        self.model.destroy();
                    }
                }, {
                    value: BI.i18nText("BI-Basic_Cancel"),
                    level: "ignore",
                    handler: function () {
                        self.del.hideView();
                    }
                }],
                el: {
                    type: "bi.vertical_adapt",
                    items: [{
                        type: "bi.label",
                        text: BI.i18nText("BI-Sure_Delete_Current_Component"),
                        cls: "delete-label",
                        textAlign: "left",
                        width: 300
                    }],
                    width: 300,
                    height: 100,
                    hgap: 20
                },
                maxHeight: 140,
                minWidth: 340
            },
            invisible: true,
            stopPropagation: true
        });

        vessel.hover(function () {
            self.del.setVisible(true);
        }, function () {
            if (!vessel.parent().parent().parent().hasClass("selected")) {
                self.del.setVisible(false);
            }
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
                right: 4,
                top: 8
            }]
        });
    },

    local: function () {
        if (this.model.has("expand")) {
            this.model.get("expand");
            return true;
        }
        if (this.model.has("layout")) {
            this.model.get("layout");
            return true;
        }
        return false;
    },

    refresh: function () {
        this.textarea.setValue({
            style: this.model.get("style"),
            content: this.model.get("content")
        });
    },

    destroyed: function () {
        BI.each(this._broadcasts, function (I, removeBroadcast) {
            removeBroadcast();
        });
        this._broadcasts = [];
    }
});