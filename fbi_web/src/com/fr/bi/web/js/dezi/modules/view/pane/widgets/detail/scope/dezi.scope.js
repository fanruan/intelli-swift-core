/**
 * Created by GUY on 2015/7/3.
 */
BIDezi.ScopeView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(BIDezi.ScopeView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-scope"
        })
    },

    _init: function () {
        BIDezi.ScopeView.superclass._init.apply(this, arguments);
    },

    _render: function (vessel) {
        var self = this;
        return BI.createWidget({
            type: "bi.label",
            text: "ceshi"
        })
    },

    local: function () {
        return false;
    },

    change: function (changed) {
    },

    refresh: function () {

    }
});