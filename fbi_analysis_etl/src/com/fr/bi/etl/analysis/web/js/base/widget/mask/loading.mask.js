/**
 * @class BI.LoadingMask
 * @extend BI.Widget
 * 正在加载mask层
 */
BI.ETLLoadingMask = BI.inherit(BI.Widget, {

    _constants : {
        LOADING:"loading",
        CANCEL:"cancel",
        CANCELING:"canceling"
    },

    _defaultConfig: function () {
        return BI.extend(BI.ETLLoadingMask.superclass._defaultConfig.apply(this, arguments), {})
    },

    _init: function () {
        BI.ETLLoadingMask.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        var mask = BI.Layers.make(this.getName(), o.masker);
        var card = BI.createWidget({
            type:"bi.card",
            items:[{
                el:{
                    type: "bi.label",
                    cls: "loading-bar-label",
                    text:  BI.i18nText("BI-Loading"),
                    height: 30
                },
                cardName:this._constants.LOADING
            }, {
                el:{
                    type:"bi.center_adapt",
                    items:[{
                        type:"bi.button",
                        level:"ignore",
                        text: BI.i18nText("BI-Cancel"),
                        handler : function () {
                            card.showCardByName(self._constants.CANCELING)
                        }
                    }]
                },
                cardName:this._constants.CANCEL
            }, {
                el:{
                    type: "bi.label",
                    cls: "loading-bar-label",
                    text: BI.i18nText("BI-Canceling"),
                    height: 30
                },
                cardName:this._constants.CANCELING
            }],
            height:30
        })
        BI.createWidget({
            type: "bi.center_adapt",
            element: mask,
            cls: "bi-loading-mask",
            items: [{
                type: "bi.vertical",
                items: [{
                    type: "bi.center_adapt",
                    cls: "loading-bar-icon",
                    items: [{
                        type: "bi.icon",
                        width: 208,
                        height: 30
                    }]
                }, card]
            }]
        });
        card.showCardByName(this._constants.LOADING)
        BI.delay(function () {
            card.showCardByName(self._constants.CANCEL)
        }, 10000)
        BI.Layers.show(this.getName());
        BI.defer(function () {
            BI.Layers.show(self.getName());
        });
    },

    destroy: function () {
        BI.Layers.remove(this.getName());
    }
});
$.shortcut("bi.etl_loading_mask", BI.ETLLoadingMask);