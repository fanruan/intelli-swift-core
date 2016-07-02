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
            type: "bi.button",
            text: BI.i18nText("BI-Query"),
            height: 25,
            forceCenter: true
            //height: ""
        });
        queryButton.on(BI.Button.EVENT_CHANGE, function () {
            //需要缓存一份所有控件的过滤条件到SharingPool中
            Data.SharingPool.put("control_filters", BI.Utils.getControlCalculations());
            BI.Utils.broadcastAllWidgets2Refresh(true);
        });
        var deleteButton = BI.createWidget({
            type: "bi.icon_button",
            cls: "close-font remove-button",
            width: 20,
            height: 20,
            title: BI.i18nText("BI-Remove")
        });
        deleteButton.on(BI.IconButton.EVENT_CHANGE, function () {
            BI.Msg.confirm(BI.i18nText("BI-Prompt"), BI.i18nText("BI-Sure_Delete") + self.model.get("name") + "?", function (v) {
                if (v === true) {
                    self.model.destroy();
                    BI.Utils.broadcastAllWidgets2Refresh();
                }
            });
        });
        deleteButton.setVisible(false);
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

    local: function () {
        if (this.model.has("expand")) {
            this.model.get("expand");
            return true;
        }
        return false;
    }
});