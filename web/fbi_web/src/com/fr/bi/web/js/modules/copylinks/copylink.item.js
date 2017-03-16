/**
 * Created by zcf on 2016/11/16.
 */
BI.CopyLinkItem = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.CopyLinkItem.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-copy-link-item"
        });
    },

    _init: function () {
        BI.CopyLinkItem.superclass._init.apply(this, arguments);

        var itemsButton = BI.createWidget({
            type: "bi.icon_text_item",
            text: BI.i18nText("BI-Copy") + BI.i18nText("BI-Links"),
            cls: 'toolbar-copy-link-font',
            stopPropagation: true,
            height: 30,
            width: 80
        });

        var zclip = BI.createWidget({
            type: 'bi.zero_clip',
            height: 30,
            width: 80,
            stopPropagation: true,
            copy: function () {
                return location.href;
            },

            afterCopy: function () {
                BI.Msg.toast(BI.i18nText("BI-Copy") + BI.i18nText("BI-Succeed"));
            }
        });

        BI.createWidget({
            type: "bi.absolute",
            element: this.element,
            items: [{
                el: itemsButton,
                top: 0,
                left: 0
            }, {
                el: zclip,
                top: 0,
                left: 0
            }]
        })
    }
});
$.shortcut("bi.copy_link_item", BI.CopyLinkItem);