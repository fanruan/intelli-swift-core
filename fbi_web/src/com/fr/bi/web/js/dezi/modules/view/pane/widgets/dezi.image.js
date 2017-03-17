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
        var self = this;
        this._broadcasts = [];
        this._broadcasts.push(BI.Broadcasts.on(BICst.BROADCAST.WIDGET_SELECTED_PREFIX, function () {
            if (!self.image.element.parent().parent().parent().hasClass("selected")) {
                self.image.setToolbarVisible(false);
            }
        }));
    },

    change: function (changed) {
        if (BI.has(changed, "bounds")) {
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
            self.model.destroy();
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
        if (this.model.has("layout")) {
            this.model.get("layout");
            this.image.resize();
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
    },

    destroyed: function () {
        BI.each(this._broadcasts, function (I, removeBroadcast) {
            removeBroadcast();
        });
        this._broadcasts = [];
    }
});