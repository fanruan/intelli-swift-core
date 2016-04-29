/**
 * @class LoadingMaskView
 * @extend BI.View
 */
LoadingMaskView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(LoadingMaskView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-mvc-loading-mask bi-mvc-layout"
        })
    },

    _init: function () {
        LoadingMaskView.superclass._init.apply(this, arguments);
    },

    _render: function (vessel) {
        var self = this;
        var left = BI.createWidget({
            type: "bi.center_adapt",
            items: [{
                type: "bi.button",
                text: "LoadingMask",
                height: 30,
                handler: function () {
                    var mask = BI.createWidget({
                        type: "bi.loading_mask",
                        masker: vessel,
                        text: "正在加载数据"
                    });
                    setTimeout(function () {
                        mask.destroy();
                    }, 3000);
                }
            }]
        });
        var right = BI.createWidget({
            type: "bi.center_adapt",
            items: [{
                type: "bi.button",
                text: "CancelLoadingMask",
                height: 30,
                handler: function () {
                    var mask = BI.createWidget({
                        type: "bi.loading_cancel_mask",
                        masker: vessel,
                        text: "正在加载数据"
                    });
                    mask.on(BI.LoadingCancelMask.EVENT_VALUE_CANCEL, function () {
                        mask.destroy();
                        BI.Msg.toast("取消加载了...");
                    });
                }
            }]
        });
        BI.createWidget({
            type: "bi.center_adapt",
            element: vessel,
            items: [left, right],
            hgap: 20
        })
    }
});

LoadingMaskModel = BI.inherit(BI.Model, {});