/**
 * Created by Young's on 2016/5/5.
 */
BIDezi.QueryModel = BI.inherit(BI.Model, {
    _defaultConfig: function () {
        return BI.extend(BIDezi.QueryModel.superclass._defaultConfig.apply(this, arguments), {})
    },

    _init: function () {
        BIDezi.QueryModel.superclass._init.apply(this, arguments);
    },

    local: function () {
        return false;
    }
});