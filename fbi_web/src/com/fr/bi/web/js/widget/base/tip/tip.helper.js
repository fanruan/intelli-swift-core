/**
 * 拖拽字段的helper
 * Created by roy on 15/10/13.
 */
BI.Helper = BI.inherit(BI.Tip, {
    _defaultConfig: function () {
        return BI.extend(BI.Helper.superclass._defaultConfig.apply(this, arguments), {
            extraCls: "bi-helper",
            text: "",
            value: ""
        })
    },

    _init: function () {
        var o = this.options;
        BI.Helper.superclass._init.apply(this, arguments);
        BI.createWidget({
            element: this.element,
            type: "bi.label",
            textAlign: "center",
            textHeight: 20,
            hgap: 5,
            text: o.text,
            value: o.value
        })
    }
})
$.shortcut("bi.helper", BI.Helper);