/**
 * Created by GameJian on 2016/3/14.
 */
BIDezi.ImageWidgetView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(BIDezi.ImageWidgetView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-dashboard-widget"
        })
    },

    _init: function () {
        BIDezi.ImageWidgetView.superclass._init.apply(this, arguments);
    },

    change: function (changed) {
        if (BI.has(changed, "bounds")) {
            this.image.resize();
        }
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

        this.image.element.hover(function () {
            self.image.setToolbarVisible(true);
        }, function () {
            if (!self.image.element.parent().parent().parent().hasClass("selected")) {
                self.image.setToolbarVisible(false);
            }
        });
    },

    local: function () {
        if (this.model.has("expand")) {
            this.model.get("expand");
            return true;
        }
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