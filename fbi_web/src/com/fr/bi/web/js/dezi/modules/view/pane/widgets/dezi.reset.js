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
            type: "bi.button",
            text: BI.i18nText("BI-Reset"),
            forceCenter: true
        });
        resetButton.on(BI.Button.EVENT_CHANGE, function () {
            self._resetAllControlValues();
        });
        var deleteButton = BI.createWidget({
            type: "bi.icon_button",
            cls: "close-font remove-button",
            width: 20,
            height: 20
        });
        deleteButton.on(BI.IconButton.EVENT_CHANGE, function () {
            BI.Msg.confirm(BI.i18nText("BI-Prompt"), BI.i18nText("BI-Sure_Delete") + self.model.get("name"), function (v) {
                if (v === true) {
                    self.model.destroy();
                }
            });
        });
        deleteButton.setVisible(false);
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
                el: deleteButton,
                right: 5,
                top: 10
            }]
        });
        veseel.hover(function () {
            deleteButton.setVisible(true);
        }, function () {
            deleteButton.setVisible(false);
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
        return false;
    }
});