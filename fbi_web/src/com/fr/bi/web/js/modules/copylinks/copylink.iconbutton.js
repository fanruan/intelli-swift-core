/**
 * Created by zcf on 2016/11/17.
 */
BI.CopyLingIconButton = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.CopyLingIconButton.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-copy-link-icon-button",
            width: 16,
            height: 16,
            iconWidth: 16,
            iconHeight: 16,
            buildUrl: ""
        })
    },

    _init: function () {
        BI.CopyLingIconButton.superclass._init.apply(this, arguments);

        var o = this.options;

        this.copyButton = BI.createWidget({
            type: "bi.icon_button",
            cls: 'copy-link-report-font ' + o.cls,
            invisible: true,
            stopPropagation: true,
            width: o.width,
            height: o.height,
            iconWidth: o.iconWidth,
            iconHeight: o.iconHeight
        });

        var zclip = BI.createWidget({
            type: 'bi.zero_clip',
            width: o.width,
            height: o.height,
            stopPropagation: true,
            copy: function () {
                return location.origin + FR.servletURL + o.buildUrl;
            },
            afterCopy: function () {
                BI.Msg.toast(BI.i18nText("BI-Copy") + BI.i18nText("BI-Succeed"));
            }
        });
        this._setStopPropagation();

        BI.createWidget({
            type: "bi.absolute",
            element: this.element,
            items: [{
                el: this.copyButton,
                top: 0,
                left: 0
            }, {
                el: zclip,
                top: 0,
                left: 0
            }]
        })
    },

    _setStopPropagation: function () {
        this.element.mouseleave(function (e) {
            e.stopPropagation();
        });
    },

    setVisible: function (enable) {
        this.copyButton.setVisible(enable);
    }
});
$.shortcut("bi.copy_link_icon_button", BI.CopyLingIconButton);