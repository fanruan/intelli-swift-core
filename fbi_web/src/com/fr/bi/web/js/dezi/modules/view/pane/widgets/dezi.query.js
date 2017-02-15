/**
 * Created by Young's on 2016/5/5.
 */
BIDezi.QueryView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(BIDezi.QueryView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-dashboard-widget"
        })
    },

    _init: function () {
        BIDezi.QueryView.superclass._init.apply(this, arguments);
    },

    _render: function (veseel) {
        var self = this;
        var queryButton = BI.createWidget({
            type: "bi.text_button",
            text: BI.i18nText("BI-Query"),
            forceCenter: true,
            cls: "query-or-reset-button"
        });
        queryButton.on(BI.Button.EVENT_CHANGE, function () {
            //需要缓存一份所有控件的过滤条件到SharingPool中
            Data.SharingPool.put("control_filters", BI.Utils.getControlCalculations());
            BI.Utils.broadcastAllWidgets2Refresh(true);
        });
        var deleteCombo = BI.createWidget({
            type: "bi.bubble_combo",
            el: {
                type: "bi.icon_button",
                cls: "close-font remove-button",
                width: 20,
                height: 20,
                title: BI.i18nText("BI-Remove")
            },
            popup: {
                type: "bi.bubble_bar_popup_view",
                buttons: [{
                    value: BI.i18nText(BI.i18nText("BI-Sure")),
                    handler: function () {
                        self.model.destroy();
                        BI.Utils.broadcastAllWidgets2Refresh();
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
                el: queryButton,
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