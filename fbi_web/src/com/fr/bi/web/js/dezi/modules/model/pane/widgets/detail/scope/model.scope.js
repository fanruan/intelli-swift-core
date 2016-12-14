/**
 * Created by GUY on 2015/7/3.
 */
BIDezi.ScopeModel = BI.inherit(BI.Model, {
    _defaultConfig: function () {
        return BI.extend(BIDezi.ScopeModel.superclass._defaultConfig.apply(this, arguments), {});
    },

    _init: function () {
        BIDezi.ScopeModel.superclass._init.apply(this, arguments);
    },

    change: function (change, prev) {
    },

    local: function () {
        return false;
    },

    refresh: function () {

    },
});