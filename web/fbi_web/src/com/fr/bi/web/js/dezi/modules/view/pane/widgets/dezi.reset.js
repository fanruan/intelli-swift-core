/**
 * Created by Young's on 2016/5/5.
 */
BIDezi.ResetView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(BIDezi.ResetView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-dashboard-widget"
        })
    },

    _init: function () {
        BIDezi.ResetView.superclass._init.apply(this, arguments);
    },

    _render: function (veseel) {
        var self = this;
        var resetButton = BI.createWidget({
            type: "bi.text_button",
            text: BI.i18nText("BI-Reset"),
            forceCenter: true,
            cls: "query-or-reset-button"
        });
        resetButton.on(BI.Button.EVENT_CHANGE, function () {
            self._resetAllControlValues();
        });
        var deleteCombo = BI.createWidget({
            type: "bi.bubble_combo",
            el: {
                type: "bi.icon_button",
                cls: "close-font remove-button",
                width: 20,
                height: 20
            },
            popup: {
                type: "bi.bubble_bar_popup_view",
                buttons: [{
                    value: BI.i18nText(BI.i18nText("BI-Sure")),
                    handler: function () {
                        self.model.destroy();
                    }
                }, {
                    value: BI.i18nText("BI-Cancel"),
                    level: "ignore",
                    handler: function () {
                        deleteCombo.hideView();
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

        deleteCombo.setVisible(false);
        BI.createWidget({
            type: "bi.absolute",
            element: veseel,
            items: [{
                el: resetButton,
                left: 0,
                right: 0,
                top: 0,
                bottom: 0
            }, {
                el: deleteCombo,
                right: 5,
                top: 10
            }]
        });
        veseel.hover(function () {
            deleteCombo.setVisible(true);
        }, function () {
            deleteCombo.setVisible(false);
        });
    },

    _resetAllControlValues: function () {
        Data.SharingPool.put("control_filters", []);
        BI.each(BI.Utils.getAllWidgetIDs(), function (i, wid) {
            BI.Broadcasts.send(BICst.BROADCAST.RESET_PREFIX + wid);
        });
        BI.Utils.broadcastAllWidgets2Refresh(true);
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
    }
});