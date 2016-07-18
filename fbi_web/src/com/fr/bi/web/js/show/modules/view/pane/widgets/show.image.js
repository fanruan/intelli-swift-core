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
        this.img = BI.createWidget({
            type: "bi.image_button",
            invalid: true,
            width: "100%",
            height: "100%"
        });
        this.img.on(BI.ImageButton.EVENT_CHANGE, function () {
            window.open(self.model.get("href"));
        });

        BI.createWidget({
            type: "bi.absolute",
            element: vessel,
            items: [{
                el: {
                    type: "bi.absolute",
                    scrollable: false,
                    items: [{
                        el: this.img
                    }]
                },
                left: 0,
                right: 0,
                top: 0,
                bottom: 0
            }]
        })
    },

    _setSize: function (w, h) {
        this.img.setImageWidth(w);
        this.img.setImageHeight(h)
    },

    _sizeChange: function (size) {
        var self = this, o = this.options;
        switch (size) {
            case BI.ImageButtonSize.ORIGINAL:
                self._setSize("auto", "auto");
                break;
            case BI.ImageButtonSize.EQUAL:
                self._setSize("auto", "auto");
                var width = this.img.getImageWidth(), height = this.img.getImageHeight();
                var W = this.element.width(), H = this.element.height();
                if (W / H > width / height) {
                    self._setSize("auto", "100%");
                } else {
                    self._setSize("100%", "auto");
                }
                break;
            case BI.ImageButtonSize.WIDGET_SIZE:
                self._setSize("100%", "100%");
                break;
        }
    },

    local: function () {
        return false;
    },

    refresh: function () {
        this.img.setSrc(this.model.get("src"));
        this._sizeChange(this.model.get("size"));
        if (BI.isNotEmptyString(this.model.get("href"))) {
            this.img.setValid(true)
        } else {
            this.img.setValid(false)
        }
    }
});