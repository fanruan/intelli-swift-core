/**
 * Created by GUY on 2015/7/3.
 */
BIShow.TargetModel = BI.inherit(BI.Model, {
    _defaultConfig: function () {
        return BI.extend(BIShow.TargetModel.superclass._defaultConfig.apply(this, arguments), {
            _src: {},
            summary: 0,
            used: true
        });
    },

    _init: function () {
        BIShow.TargetModel.superclass._init.apply(this, arguments);
    },

    refresh: function () {

    },

    local: function () {
        return false;
    },

    change: function () {

    }
});
