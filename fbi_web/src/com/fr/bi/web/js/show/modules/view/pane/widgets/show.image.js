/**
 * Created by GameJian on 2016/3/14.
 */
BIShow.ImageWidgetView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(BIShow.ImageWidgetView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-dashboard-widget"
        })
    },

    _init: function () {
        BIShow.ImageWidgetView.superclass._init.apply(this, arguments);
    },

    change: function (changed) {

    },

    _render: function (vessel) {
        var self = this;
        this.image = BI.createWidget({
            type: "bi.upload_image",
            element: vessel
        });

        this.image.on(BI.UploadImage.EVENT_CHANGE, function () {
            self.model.set(self.image.getValue());
        });

        this.image.on(BI.UploadImage.EVENT_DESTROY, function () {
            BI.Msg.confirm("", BI.i18nText("BI-Sure_Delete"), function (v) {
                if (v === true) {
                    self.model.destroy();
                }
            });
        });
    },

    local: function () {
        return false;
    },

    refresh: function () {
        this.image.setValue({
            href: this.model.get("href"),
            size: this.model.get("size"),
            src: this.model.get("src")
        });
    }
});