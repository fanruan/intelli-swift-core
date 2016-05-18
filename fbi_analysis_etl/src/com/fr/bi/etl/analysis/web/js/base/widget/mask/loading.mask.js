/**
 * @class BI.LoadingMask
 * @extend BI.Widget
 * 正在加载mask层
 */
BI.ETLLoadingMask = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.ETLLoadingMask.superclass._defaultConfig.apply(this, arguments), {})
    },

    _init: function () {
        BI.ETLLoadingMask.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        var mask = BI.Layers.make(this.getName(), o.masker);
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
                }, {
                    type: "bi.label",
                    cls: "loading-bar-label",
                    text: o.text,
                    height: 30
                }]
            }]
        });
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