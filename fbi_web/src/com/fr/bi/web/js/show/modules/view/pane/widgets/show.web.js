/**
 * Created by GameJian on 2016/3/14.
 */
BIShow.WebWidgetView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(BIShow.WebWidgetView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-dashboard-widget"
        })
    },

    _init: function () {
        BIShow.WebWidgetView.superclass._init.apply(this, arguments);
    },

    change: function () {

    },

    _render: function (vessel) {
        var self = this;
        this.web = BI.createWidget({
            type: "bi.web_page",
            element: vessel
        });

        this.web.on(BI.WebPage.EVENT_DESTROY, function () {
            BI.Msg.confirm("", BI.i18nText("BI-Sure_Delete"), function (v) {
                if (v === true) {
                    self.model.destroy();
                }
            });
        });

        this.web.on(BI.WebPage.EVENT_VALUE_CHANGE, function () {
            self.model.set("url", self.web.getValue())
        })
    },

    refresh: function () {
        this.web.setValue(this.model.get("url"))
    }
});