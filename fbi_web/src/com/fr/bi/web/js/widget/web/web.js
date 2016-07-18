/**
 * web组件
 * Created by GameJian on 2016/3/1.
 */
BI.WebPage = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.WebPage.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-web-page"
        })
    },

    _init: function () {
        BI.WebPage.superclass._init.apply(this, arguments);
        var self = this, o = this.options;

        this.iframe = BI.createWidget({
            type: "bi.iframe"
        });

        this.label = BI.createWidget({
            type: "bi.label",
            cls: "web-page-text-button-label",
            whiteSpace: "normal",
            text: BI.i18nText("BI-Not_Add_Url")
        });

        this.del = BI.createWidget({
            type: "bi.icon_button",
            cls: "web-page-button img-shutdown-font",
            title: BI.i18nText("BI-Delete"),
            height: 24,
            width: 24
        });

        this.del.on(BI.IconButton.EVENT_CHANGE, function () {
            self.fireEvent(BI.WebPage.EVENT_DESTROY)
        });

        this.href = BI.createWidget({
            type: "bi.image_button_href",
            cls: "web-page-button"
        });

        this.href.on(BI.ImageButtonHref.EVENT_CHANGE, function () {
            self.setValue(this.getValue());
            self.fireEvent(BI.WebPage.EVENT_VALUE_CHANGE)
        });

        BI.createWidget({
            type: "bi.absolute",
            element: this.element,
            items: [{
                el: this.iframe
            }, {
                el: this.del,
                right: 4,
                top: 8
            }, {
                el: this.href,
                right: 36,
                top: 8
            }, {
                el: this.label,
                top: 32,
                left: 0,
                bottom: 0,
                right: 0
            }]
        });

        this.setToolbarVisible(false);
        this._showLabel();
    },

    _hideLabel: function () {
        this.label.invisible()
    },

    _showLabel: function () {
        this.label.visible()
    },

    setToolbarVisible: function (v) {
        this.href.setVisible(v);
        this.del.setVisible(v);
    },

    getValue: function () {
        return this.href.getValue()
    },

    setValue: function (url) {
        var self = this;
        if (BI.isNotEmptyString(url)) {
            self._hideLabel();
        } else {
            self._showLabel();
        }
        this.href.setValue(url);
        this.iframe.setSrc(url)
    }
});

BI.WebPage.EVENT_DESTROY = "EVENT_DESTROY";
BI.WebPage.EVENT_VALUE_CHANGE = "EVENT_VALUE_CHANGE";
$.shortcut("bi.web_page", BI.WebPage);