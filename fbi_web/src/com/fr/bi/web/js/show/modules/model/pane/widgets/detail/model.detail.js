/**
 * Created by GUY on 2015/6/24.
 */

//详细设置界面选择图表类型
BIShow.DetailModel = BI.inherit(BI.Model, {
    _defaultConfig: function () {
        return BI.extend(BIShow.DetailModel.superclass._defaultConfig.apply(this, arguments), {
            dimensions: {},
            view: {},
            name: "",
            type: BICst.WIDGET.TABLE,
            settings: {}
        });
    },

    _init: function () {
        BIShow.DetailModel.superclass._init.apply(this, arguments);
    },

    splice: function (old, key1, key2) {
    },

    duplicate: function (copy, key1, key2) {
    },

    change: function (changed) {

    },

    refresh: function () {

    },

    local: function () {
        var self = this;
        return false;
    }
});
